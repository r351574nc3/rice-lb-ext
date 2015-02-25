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
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.*;

@DatabaseChange(name = "importWorkflow", description = "Import Workflow XML", priority = ChangeMetaData.PRIORITY_DEFAULT)
public class ImportWorkflowChange extends AbstractChange {

	private String file;
	private String directory;

	private final String KFS_HOME_DIR = "target/kuali/kfs";
	private final String KFS_ZIP_DIR = "target/workflow";
	private final String KFS_RESOURCES_DIR = "src/main/resources";

	@Override
	public boolean supports(Database database) {
		return database instanceof OracleDatabase;
	}

	@Override
	public ValidationErrors validate(Database database) {
		ValidationErrors validationErrors = super.validate(database);
		if (file == null && directory == null) {
			validationErrors.addError("You must specify a file or a directory for the importWorkflow task.");
		} else if (file != null && directory != null) {
			validationErrors.addError("You may only specify one of file and directory on the importWorkflow task.");
		} else if (file != null && !fileExists()) {
			validationErrors.addError(String.format("File '%s' does not exist", file));
		} else if (directory != null) {
			try {
				if (listFiles(directory).isEmpty()) {
					validationErrors.addError("Directory " + directory + " contained no files.");
				}
			} catch (IOException e) {
				validationErrors.addError("Unable to read directory: " + directory);
				e.printStackTrace();
			}
		}
		return validationErrors;
	}


	@Override
	public CheckSum generateCheckSum() {
		if (file != null) {
			return generateCheckSum(file);
		} else {
			try {
				StringBuilder checkSumString = new StringBuilder();
				for (String fileName : listFiles(directory)) {
					checkSumString.append(generateCheckSum(fileName).toString());
				}
				// now, checksum the filename/checksum combos
				return CheckSum.compute(checkSumString.toString());
			} catch (IOException ex) {
				throw new UnexpectedLiquibaseException("Error obtaining workflow XML files from " + directory, ex);
			}
		}
	}


	@Override
	public SqlStatement[] generateStatements(Database database) {
		if (ExecutorService.getInstance().getExecutor(database).updatesDatabase()) {
			return generateStatementsUsingLocalWorkflowEngine(database);
		} else {
			return generateStatementsUsingSqlZip(database);
		}
	}

	private SqlStatement[] generateStatementsUsingSqlZip(Database database) {
		File tempDirectory = prepareTempWorkingDir();
		StringBuilder sb = new StringBuilder();
		Collection<File> files = FileUtils.listFiles(tempDirectory, new String[]{"xml", "zip"}, true);
		Iterator<File> filesIter = files.iterator();
		while (filesIter.hasNext()) {
			try {
				File orgFile = filesIter.next();
				FileUtils.copyFile(orgFile, new File(getBaseDir(), KFS_ZIP_DIR + "/" + orgFile.getName()));
			} catch (IOException e) {
				throw new UnexpectedLiquibaseException(e);
			}
			sb.append("-- Automated workflow ingestion on server will import workflow XML file: ").append(file).append("\n");
		}
		return new SqlStatement[]{new RawSqlStatement(sb.toString())};
	}

	private SqlStatement[] generateStatementsUsingLocalWorkflowEngine(Database database) {
		try {
			File tempDirectory = prepareTempWorkingDir();

			List<String> args = new ArrayList<String>();
			args.add("-Dworkflow.dir=" + tempDirectory.toString().replace('\\', '/'));
			args.add("-Ddatasource.url=" + database.getConnection().getURL());
			args.add("-Ddatasource.username=" + database.getConnection().getConnectionUserName());
			args.add("-Ddatasource.password=" + getChangeSet().getChangeLog().getChangeLogParameters().getValue("import.workflow.database.password"));
			args.add("-Drice.server.datasource.url=" + database.getConnection().getURL());
			args.add("-Drice.server.datasource.username=" + database.getConnection().getConnectionUserName());
			args.add("-Drice.server.datasource.password=" + getChangeSet().getChangeLog().getChangeLogParameters().getValue("import.workflow.database.password"));
			args.add("-Djava.awt.headless=true");
			args.add("-Dbuild.environment=wfimport");
			args.add("-Dkfs.home=" + new File(getBaseDir(),KFS_HOME_DIR).getAbsolutePath());

			execJavaProcess("za.org.kuali.kfs.sys.util.WorkflowImporter", tempDirectory.toString().replace('\\', '/'), args, Arrays.asList(new String[]{tempDirectory.toString().replace('\\', '/')}));

			// just to speed things up if we have multiple workflow to run
			Object value = getChangeSet().getChangeLog().getChangeLogParameters().getValue("import.workflow.clean-already-run");
			if (value == null) {
				args.add("clean-all");
				getChangeSet().getChangeLog().getChangeLogParameters().set("import.workflow.clean-already-run", Boolean.TRUE);
			}

			return new SqlStatement[]{new RawSqlStatement("select 1 from dual")};
		} catch (Exception e) {
			throw new UnexpectedLiquibaseException("Unable to generate statements using inline workflow engine", e);
		}
	}

	private File prepareTempWorkingDir() {
		try {
			File workFlowDir = new File(getBaseDir(),KFS_HOME_DIR + "/staging");
			List<String> files = directory != null ? listFiles(directory) : Arrays.asList(file);
			for (String fileName : files) {
				copyFileToTempDir(workFlowDir, fileName);
			}
			return workFlowDir;
		} catch (Exception e) {
			throw new UnexpectedLiquibaseException(e);
		}
	}

	private void copyFileToTempDir(File tempDirectory, String theFile) throws IOException {
		InputStream fileInputStream = null;
		try {
			fileInputStream = getFileInputStream(theFile);
			FileUtils.copyInputStreamToFile(fileInputStream, new File(tempDirectory, theFile));
		} finally {
			IOUtils.closeQuietly(fileInputStream);
		}
	}


	@Override
	public String getConfirmationMessage() {
		if (file != null) {
			return "Imported Workflow from File: " + file;
		} else {
			return "Imported Workflow from Directory: " + directory;
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
			stream = getFileInputStream(file);
		} catch (Exception ex) {
			return false;
		} finally {
			IOUtils.closeQuietly(stream);
		}
		return true;
	}

	private void execJavaProcess(String className, String dir, List<String> jvmArgs, List<String> args) throws IOException,
					InterruptedException {
		List<String> builderArgs = new ArrayList<String>();
		builderArgs.add(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java");
		builderArgs.add("-cp");
		builderArgs.add(configureClasspath());
		if (jvmArgs != null) {
			builderArgs.addAll(jvmArgs);
		}
		builderArgs.add(className);
		if (args != null) {
			builderArgs.addAll(args);
		}

		ProcessBuilder builder = new ProcessBuilder(builderArgs);
		System.out.println("running: " + builder.command());
		builder.directory(new File(dir));
		builder.redirectErrorStream(true);
		Process process = builder.start();

		InputStream stdout = process.getInputStream();
		InputStream stderr = process.getErrorStream();
		Thread threadOut = new Thread(new MyInputStreamSink(stdout, "out"));
		Thread threadErr = new Thread(new MyInputStreamSink(stderr, "err"));

		threadOut.setDaemon(true);
		threadErr.setDaemon(true);
		threadOut.setName(String.format("stdout reader"));
		threadErr.setName(String.format("stderr reader"));

		threadOut.start();
		threadErr.start();

		process.waitFor();

		if (process.exitValue() != 0) {
			throw new UnexpectedLiquibaseException(IOUtils.toString(process.getInputStream(), "UTF-8"));
		}
	}

	private String configureClasspath() {
		String classpath = new File(getBaseDir(),"target/classes;").getAbsolutePath() + (String) getChangeSet().getChangeLog().getChangeLogParameters().getValue("mavenClasspath");
		return StringUtils.replace(classpath, ";", System.getProperty("path.separator"));
	}


	@DatabaseChangeProperty(description = "Workflow XML/ZIP File To Load", exampleValue = "workflow/KFS-18130/PA_PurchaseAgreement.xml")
	public String getFile() {
		return file;
	}


	public void setFile(String file) {
		this.file = file;
	}


	@DatabaseChangeProperty(description = "Workflow XML Files To Load", exampleValue = "workflow/KFS-18130")
	public String getDirectory() {
		return directory;
	}


	public void setDirectory(String directory) {
		this.directory = directory;
	}

	private List<String> listFiles(String directory) throws IOException {
		if (directory == null) {
			return Collections.emptyList();
		}
		directory = directory.replace('\\', '/');
		if (!(directory.endsWith("/"))) {
			directory = directory + '/';
		}

		File baseDir = new File(getBaseDir(), KFS_RESOURCES_DIR);
		File changeLogDir = new File(baseDir, getChangeSet().getChangeLog().getPhysicalFilePath()).getParentFile();
		File workflowDir = new File(changeLogDir, directory);
		String baseDirUnix = changeLogDir.getAbsolutePath().replace('\\', '/');
		if (!workflowDir.exists()) {
			throw new IOException(String.format("Directory '%s' does not exist! ", workflowDir));
		}

		List<File> unsortedResources = new ArrayList(FileUtils.listFiles(workflowDir, new String[]{"xml", "zip"}, true));
		SortedSet<String> resources = new TreeSet<String>();
		if (unsortedResources != null) {
			for (File resourcePath : unsortedResources) {
				String unixFilePath = resourcePath.getAbsolutePath().replace('\\', '/');
				resources.add(unixFilePath.replaceFirst(baseDirUnix + "/", ""));
			}
		}
		//System.out.println( "Returning: " + resources );
		return new ArrayList(resources);
	}

	private File getBaseDir(){
		String mavenBaseDir = (String) getChangeSet().getChangeLog().getChangeLogParameters().getValue("mavenBaseDir");
		if (mavenBaseDir == null){
			throw new IllegalArgumentException("ExpressionVars 'mavenBaseDir' not set!");
		}
		return new File(mavenBaseDir);
	}

	private static class MyInputStreamSink implements Runnable {
		private InputStream m_in;
		private String m_streamName;

		MyInputStreamSink(InputStream in, String streamName) {
			m_in = in;
			m_streamName = streamName;
		}

		@Override
		public void run() {
			BufferedReader reader = null;
			Writer writer = null;

			try {
				reader = new BufferedReader(new InputStreamReader(m_in));
				for (String line = null; ((line = reader.readLine()) != null); ) {
					System.out.println(line);
				}
			} catch (IOException e) {
				System.err.println("Unexpected I/O exception reading from process. " + e.getMessage());
			} finally {
				try {
					if (null != reader) reader.close();
				} catch (java.io.IOException e) {
					System.err.println("Unexpected I/O exception closing a stream. " + e.getMessage());
				}
			}
		}
	}

}




