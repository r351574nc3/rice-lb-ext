package liquibase.ext.kualigan.change;

import liquibase.change.*;
import liquibase.database.Database;
import liquibase.database.core.OracleDatabase;
import liquibase.exception.UnexpectedLiquibaseException;
import liquibase.exception.ValidationErrors;
import liquibase.executor.ExecutorService;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.RawSqlStatement;
import liquibase.util.StreamUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@DatabaseChange(name = "importWorkflow", description = "Import Workflow XML", priority = ChangeMetaData.PRIORITY_DEFAULT)
public class ImportWorkflowChange extends AbstractChange {

	private String fileName;
	private String directoryName;

	@Override
	public boolean supports(Database database) {
		return database instanceof OracleDatabase;
	}

	@Override
	public ValidationErrors validate(Database database) {
		ValidationErrors validationErrors = super.validate(database);
		if (fileName == null && directoryName == null) {
			validationErrors.addError("You must specify a fileName or a directoryName for the importWorkflow task.");
		} else if (fileName != null && directoryName != null) {
			validationErrors.addError("You may only specify one of fileName and directoryName on the importWorkflow task.");
		} else if (fileName != null && fileExists()) {
			validationErrors.addError(String.format("File '%s' does not exist", fileName));
		}
		//todo: implement dir support
//		else if (directoryName != null) {
//			try {
//				if (getDirectoryFileNames().isEmpty()) {
//					validationErrors.addError("Directory " + directoryName + " contained no files.");
//				}
//			} catch (IOException e) {
//				validationErrors.addError("Unable to read directory: " + directoryName);
//				e.printStackTrace();
//			}
//		}
		return validationErrors;
	}


	@Override
	public CheckSum generateCheckSum() {
		if (fileName != null) {
			return generateCheckSum(fileName);
		} else {
			//todo: implement directory support
			throw new UnsupportedOperationException("Directories not yet supported");
//			try {
//				StringBuilder checkSumString = new StringBuilder();
//				for (String fileName : getDirectoryFileNames()) {
//					checkSumString.append(generateCheckSum(fileName).toString());
//				}
//				// now, checksum the filename/checksum combos
//				return CheckSum.compute(checkSumString.toString());
//			} catch (IOException ex) {
//				throw new UnexpectedLiquibaseException("Error obtaining workflow XML files from " + directoryName, ex);
//			}
		}
	}


	@Override
	public SqlStatement[] generateStatements(Database database) {
		if (ExecutorService.getInstance().getExecutor(database).updatesDatabase()) {
			return generateStatementsUsingLocalWorkflowEngine(database);
		} else {
			return generateStatementsUsingZip(database);
		}
	}

	private SqlStatement[] generateStatementsUsingZip(Database database) {
		Path tempDirectory = prepareTempWorkingDir();
		StringBuilder sb = new StringBuilder();
		Collection<File> files = FileUtils.listFiles(tempDirectory.toFile(), new String[]{"xml","zip"}, false);
		Iterator<File> filesIter = files.iterator();
		while (filesIter.hasNext()) {
			try {
				File orgFile = filesIter.next();
				FileUtils.copyFile(orgFile, new File(System.getProperty("user.dir") + "/target/liquibase/workflow/", orgFile.getName()));
			} catch (IOException e) {
				throw new UnexpectedLiquibaseException(e);
			}
			sb.append("-- Automated workflow ingestion on server will import workflow XML file: ").append(fileName).append("\n");
		}
		return new SqlStatement[]{new RawSqlStatement(sb.toString())};
	}

	private SqlStatement[] generateStatementsUsingLocalWorkflowEngine(Database database) {
		throw new UnsupportedOperationException("Running updates not yet supported");
//				// execute here - pull the connection information from the database object
//				List<String> args = new ArrayList<String>();
//				args.add("-Dworkflow.dir=" + tempDirectory.toString().replace('\\', '/'));
//				args.add("-Ddatasource.url=" + database.getConnection().getURL());
//				args.add("-Ddatasource.username=" + database.getConnection().getConnectionUserName());
//				args.add("-Ddatasource.password=" + getChangeSet().getChangeLog().getChangeLogParameters().getValue("import.workflow.database.password"));
//				args.add("-Drice.server.datasource.url=" + database.getConnection().getURL());
//				args.add("-Drice.server.datasource.username=" + database.getConnection().getConnectionUserName());
//				args.add("-Drice.server.datasource.password=" + getChangeSet().getChangeLog().getChangeLogParameters().getValue("import.workflow.database.password"));
//				args.add("-Djava.awt.headless=true");


//				args.add("-Dbuild.environment=wfimport");
//				// just to speed things up if we have multiple workflow to run
//				Object value = getChangeSet().getChangeLog().getChangeLogParameters().getValue("import.workflow.clean-already-run");
//				if (value == null) {
//					args.add("clean-all");
//					getChangeSet().getChangeLog().getChangeLogParameters().set("import.workflow.clean-already-run", Boolean.TRUE);
//				}
//				args.add("import-workflow-xml");

		//todo: implement delegation to java process for inline processing
//				JavaProcess.exec("za.org.kuali.kfs.sys.util.WorkflowImporter",
//								tempDirectory.toString().replace('\\', '/'),
//								getChangeSet().getChangeLog().getChangeLogParameters().getValue("import.workflow.classpath").toString(),
//			return new SqlStatement[0];

	}

	private Path prepareTempWorkingDir() {
		Path tempDirectory = null;
		try {
			tempDirectory = Files.createTempDirectory("liquibase-workflow");
			if (directoryName != null) {
				FileUtils.copyDirectory(new File(directoryName), tempDirectory.toFile().getAbsoluteFile());
			} else {
				InputStream fileInputStream = null;
				try {
					fileInputStream = getFileInputStream(fileName);
					FileUtils.copyInputStreamToFile(fileInputStream, new File(tempDirectory.toFile(), fileName));
				} finally {
					IOUtils.closeQuietly(fileInputStream);
				}
			}
		} catch (Exception e) {
			throw new UnexpectedLiquibaseException(e);
		}
		return tempDirectory;
	}


	@Override
	public String getConfirmationMessage() {
		if (fileName != null) {
			return "Imported Workflow from File: " + fileName;
		} else {
			return "Imported Workflow from Directory: " + directoryName;
		}
	}

	/**
	 * Stopping certain validation which was running the workflow at the wrong time
	 */
	@Override
	public boolean generateStatementsVolatile(Database database) {
		return true;
	}

	private CheckSum generateCheckSum(String fileName) {
		InputStream stream = null;
		try {
			stream = getFileInputStream(fileName);
			return CheckSum.compute(fileName + ":" + CheckSum.compute(stream, true).toString());
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException ignore) {
				}
			}
		}
	}

	private InputStream getFileInputStream(String fileName) {
		InputStream stream = null;
		try {
			stream = StreamUtil.openStream(fileName, true, getChangeSet(), getResourceAccessor());
			if (stream == null) {
				throw new UnexpectedLiquibaseException(fileName + " could not be found");
			}
			stream = new BufferedInputStream(stream);
			return stream;
		} catch (IOException e) {
			throw new UnexpectedLiquibaseException(e);
		}
	}


	private boolean fileExists() {
		InputStream stream = null;
		try {
			stream = getFileInputStream(fileName);
		} catch (Exception ex) {
			return false;
		} finally {
			IOUtils.closeQuietly(stream);
		}
		return true;
	}

	private static int execJavaProcess( String className, String dir, String classpath, List<String> jvmArgs,  List<String> args ) throws IOException,
					InterruptedException {
		String javaHome = System.getProperty("java.home");
		String javaBin = javaHome +
						File.separator + "bin" +
						File.separator + "java";
		if ( classpath == null ) {
			classpath = System.getProperty("java.class.path");
		}

		List<String> builderArgs = new ArrayList<String>();
		builderArgs.add(javaBin);
		builderArgs.add("-cp");
		builderArgs.add(classpath);
		if ( jvmArgs != null ) {
			builderArgs.addAll(jvmArgs);
		}
		builderArgs.add(className);
		if ( args != null ) {
			builderArgs.addAll(args);
		}

		ProcessBuilder builder = new ProcessBuilder( builderArgs );
		builder.directory( new File( dir ) );
		builder.inheritIO();
		System.out.println( "running: " + builder.command() );
		Process process = builder.start();
		process.waitFor();
		return process.exitValue();
	}


	@DatabaseChangeProperty(description = "Workflow XML/ZIP File To Load", exampleValue = "workflow/KFS-18130/PA_PurchaseAgreement.xml")
	public String getFileName() {
		return fileName;
	}


	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	@DatabaseChangeProperty(description = "Workflow XML Files To Load", exampleValue = "workflow/KFS-18130")
	public String getDirectoryName() {
		return directoryName;
	}


	public void setDirectoryName(String directoryName) {
		this.directoryName = directoryName;
	}
}

//	protected String getRelativeFilePath(String fileName) {
//		File baseDir = new File(System.getProperty("user.dir"));
//		File changeLogDir = new File(baseDir, getChangeSet().getChangeLog().getPhysicalFilePath()).getParentFile();
//		File workflowFile = new File(changeLogDir, fileName);
//
//		String rootPath = baseDir.getAbsolutePath().replace('\\','/') + "/";
//
//        return workflowFile.getAbsolutePath().replace('\\','/').replaceFirst(baseDir.getAbsolutePath()+"/", "");
//	}


//	private List<String> getDirectoryFileNames() throws IOException {
//		if (directoryName == null) {
//			return Collections.emptyList();
//		}
//
//		directoryName = directoryName.replace('\\', '/');
//
//		if (!(directoryName.endsWith("/"))) {
//			directoryName = directoryName + '/';
//		}
//
//		File baseDir = new File(System.getProperty("user.dir"));
//		File changeLogDir = new File(baseDir, getChangeSet().getChangeLog().getPhysicalFilePath()).getParentFile();
//		File workflowDir = new File(changeLogDir, directoryName);
//		String baseDirUnix = baseDir.getAbsolutePath().replace('\\', '/');
//
//		File[] unsortedResources = workflowDir.listFiles(new FileFilter() {
//			@Override
//			public boolean accept(File pathname) {
//				return pathname.isFile() && pathname.getName().endsWith(".xml");
//			}
//		});
//		SortedSet<String> resources = new TreeSet<String>();
//		if (unsortedResources != null) {
//			for (File resourcePath : unsortedResources) {
//				String unixFilePath = resourcePath.getAbsolutePath().replace('\\', '/');
//				resources.add(unixFilePath.replaceFirst(baseDirUnix + "/", ""));
//			}
//		}
//		//System.out.println( "Returning: " + resources );
//		return new ArrayList(resources);
//	}
