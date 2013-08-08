package org.kuali.rice.liquibase.change.ext;

import liquibase.change.AbstractChange;
import liquibase.change.custom.CustomSqlChange;
import liquibase.change.custom.CustomSqlRollback;
import liquibase.database.Database;
import liquibase.exception.DatabaseException;
import liquibase.exception.SetupException;
import liquibase.exception.UnexpectedLiquibaseException;
import liquibase.exception.ValidationErrors;
import liquibase.executor.ExecutorService;
import liquibase.resource.ResourceAccessor;
import liquibase.sql.Sql;
import liquibase.sql.UnparsedSql;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.RuntimeStatement;
import org.apache.commons.lang.StringUtils;

import java.math.BigInteger;

public abstract class RiceAbstractChange extends AbstractChange implements CustomSqlChange, CustomSqlRollback {

	public RiceAbstractChange(String changeName, String changeDescription, int priority) {
		super(changeName, changeDescription, priority);
	}

	@Override
	public ValidationErrors validate(Database database) {
		//todo: Default validation calls generateStatements which in turn tries to retrieve foreign key references for parameters not yet initialized
		return new ValidationErrors();
	}


	@Override
	public String getConfirmationMessage() {
		return "";
	}

	public void setFileOpener(final ResourceAccessor resourceAccessor) {
		setResourceAccessor(resourceAccessor);
	}

	public void setUp() throws SetupException {
	}

	protected abstract String getSequenceName();


	protected BigInteger getPrimaryKey(Database database) {
		try {
			final SqlStatement getPermissionId = new RuntimeStatement() {
				public Sql[] generate(Database database) {
					if (database.supportsSequences()) {
						return new Sql[]{
							new UnparsedSql(String.format("SELECT %s.NEXTVAL FROM dual", getSequenceName()))
						};
					}
					incrementSequence(database);
					return new Sql[]{
						new UnparsedSql(String.format("select max(id) from %s;", getSequenceName()))
					};
				}
			};
			return (BigInteger) ExecutorService.getInstance().getExecutor(database).queryForObject(getPermissionId, BigInteger.class);
		} catch (DatabaseException e) {
			throw new UnexpectedLiquibaseException(String.format("Unable to generate primary key using sequence (%s)", getSequenceName()), e);
		}
	}

	protected BigInteger getPermissionTemplateForeignKey(Database database, final String templateName) {
		try {
			if (StringUtils.isBlank(templateName)) {
			/* Template not required - Default to 1 */
				return BigInteger.ONE;
			}
			final RuntimeStatement templateIdStatement = new RuntimeStatement() {
				public Sql[] generate(Database database1) {
					return new Sql[]{
						new UnparsedSql(String.format("select PERM_TMPL_ID from KRIM_PERM_TMPL_T where NM = '%s'", templateName))
					};
				}
			};
			return (BigInteger) ExecutorService.getInstance().getExecutor(database).queryForObject(templateIdStatement, BigInteger.class);
		} catch (DatabaseException e) {
			throw new UnexpectedLiquibaseException(String.format("Unable to retrieve foreign key for 'Permission Template' (%s)", templateName), e);
		}
	}

	protected BigInteger getAttributeDefinitionForeignKey(Database database, final String attributeDef){
		try {
			final SqlStatement getDefinitionId = new RuntimeStatement() {
				public Sql[] generate(Database database) {
					return new Sql[] {
						new UnparsedSql(String.format("select KIM_ATTR_DEFN_ID from krim_attr_defn_t where nm = '%s'", attributeDef))
					};
				}
			};
			return (BigInteger) ExecutorService.getInstance().getExecutor(database).queryForObject(getDefinitionId, BigInteger.class);
		} catch (DatabaseException e) {
			throw new UnexpectedLiquibaseException(String.format("Unable to retrieve foreign key 'Attribute Definition' (%s)",attributeDef),e);
		}
	}

	private void incrementSequence(Database database) {
		try {
			final SqlStatement incrementSequenceStatement = new RuntimeStatement() {
				public Sql[] generate(Database database) {
					return new Sql[]{
						new UnparsedSql(String.format("insert into %s values(null);",getSequenceName()))
					};
				}
			};
			ExecutorService.getInstance().getExecutor(database).execute(incrementSequenceStatement);
		} catch (DatabaseException e) {
			throw new UnexpectedLiquibaseException(String.format("Unable to increment sequence (%s)",getSequenceName()),e);
		}
	}

	protected BigInteger getTypeReference(Database database, final String kimType) {
		try {
			final SqlStatement getTypeId = new RuntimeStatement() {
				public Sql[] generate(Database database) {
					return new Sql[] {
						new UnparsedSql(String.format("select kim_typ_id from krim_typ_t where nm = '%s'", kimType))
					};
				}
			};
			return (BigInteger) ExecutorService.getInstance().getExecutor(database).queryForObject(getTypeId, BigInteger.class);
		} catch (DatabaseException e) {
			throw new UnexpectedLiquibaseException(String.format("Unable to retrieve foreign key 'Type Reference' (%s)",kimType),e);
		}
	}

	protected BigInteger getPermissionReference(Database database, final String permissionName, final String permissionNameSpace){
		try {
			final SqlStatement getPermissionId = new RuntimeStatement() {
				public Sql[] generate(Database database) {
					return new Sql[]{
						new UnparsedSql(String.format("select PERM_ID from KRIM_PERM_T where nm = '%s' and NMSPC_CD = '%s'", permissionName, permissionNameSpace))
					};
				}
			};
			return (BigInteger) ExecutorService.getInstance().getExecutor(database).queryForObject(getPermissionId, BigInteger.class);
		} catch (DatabaseException e) {
			throw new UnexpectedLiquibaseException(String.format("Unable to retreive foreign key reference for 'Permission' (name: %s, namespace: %s)", permissionName, permissionNameSpace));
		}
	}
}
