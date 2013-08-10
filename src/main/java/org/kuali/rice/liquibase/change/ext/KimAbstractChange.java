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

public abstract class KimAbstractChange extends AbstractChange implements CustomSqlChange, CustomSqlRollback {

	public KimAbstractChange(String changeName, String changeDescription, int priority) {
		super(changeName, changeDescription, priority);
	}

	@Override
	public final ValidationErrors validate(Database database) {
		//todo: Default validation calls generateStatements which in turn tries to retrieve foreign key references for parameters not yet initialized
		return new ValidationErrors();
	}

	@Override
	public final String getConfirmationMessage() {
		return "";
	}

	public final void setFileOpener(final ResourceAccessor resourceAccessor) {
		setResourceAccessor(resourceAccessor);
	}

	public final void setUp() throws SetupException {
	}

	@Override
	public final boolean supports(Database database) {
		return true;
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

	protected BigInteger getResponsibilityTemplateForeignKey(Database database, final String templateName, final String nameSpaceCode) {
		try {
			final RuntimeStatement templateIdStatement = new RuntimeStatement() {
				public Sql[] generate(Database database1) {
					return new Sql[]{
						new UnparsedSql(String.format("select RSP_TMPL_ID from KRIM_RSP_TMPL_T where nm = '%s'", templateName))
					};
				}
			};
			return (BigInteger) ExecutorService.getInstance().getExecutor(database).queryForObject(templateIdStatement, BigInteger.class);
		} catch (DatabaseException e) {
			throw new UnexpectedLiquibaseException(String.format("Unable to retrieve foreign key for 'Responsibility Template' (nm: %s, nmSpace: %s)", templateName, nameSpaceCode), e);
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

	protected BigInteger getTypeForeignKey(Database database, final String kimType) {
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

	protected BigInteger getPermissionForeignKey(Database database, final String permissionName, final String permissionNameSpace){
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


	protected BigInteger getRoleForeignKey(Database database, final String roleName, final String namespaceCode) {
		try {
			final SqlStatement getRoleId = new RuntimeStatement() {
				public Sql[] generate(Database database) {
					return new Sql[] {
						new UnparsedSql(String.format("select ROLE_ID from KRIM_ROLE_T where ROLE_NM = '%s' and NMSPC_CD = '%s'", roleName, namespaceCode))
					};
				}
			};
			return (BigInteger) ExecutorService.getInstance().getExecutor(database).queryForObject(getRoleId, BigInteger.class);
		} catch (DatabaseException e) {
			throw new UnexpectedLiquibaseException(String.format("Unable to retrieve foreign key for 'Role' (role: %s, namespace: %s)", roleName, namespaceCode), e);
		}
	}

	protected BigInteger getPrincipalForeignKey(Database database, final String memberName) {
		try {
			final SqlStatement getMemberId = new RuntimeStatement() {
				public Sql[] generate(Database database) {
					return new Sql[] {
						new UnparsedSql(String.format("select PRNCPL_ID from KRIM_PRNCPL_T where PRNCPL_NM = '%s'", memberName))
					};
				}
			};
			return (BigInteger) ExecutorService.getInstance().getExecutor(database).queryForObject(getMemberId, BigInteger.class);
		} catch (DatabaseException e) {
			throw new UnexpectedLiquibaseException(String.format("Unable to retrieve foreign key for 'Principal' (%s)", memberName), e);
		}
	}

	protected BigInteger getResponsibilityForeignKey(Database database, final String responsibilityName) {
		try {
			final SqlStatement getResponsibilityId = new RuntimeStatement() {
				public Sql[] generate(Database database) {
					return new Sql[]{
						new UnparsedSql(String.format("select rsp_id from krim_rsp_t where nm = '%s'", responsibilityName))
					};
				}
			};
			return (BigInteger) ExecutorService.getInstance().getExecutor(database).queryForObject(getResponsibilityId, BigInteger.class);
		} catch (DatabaseException e) {
			throw new UnexpectedLiquibaseException(String.format("Unable to retrieve foreign key for 'Responsibility' (%s)", responsibilityName), e);
		}
	}

	protected BigInteger getRoleResponsibilityForeignKey(Database database, final BigInteger roleId , final BigInteger responsibilityId) {
		try {
			final SqlStatement getRoleRespId = new RuntimeStatement() {
				public Sql[] generate(Database database) {
					return new Sql[]{
						new UnparsedSql(String.format("select role_rsp_id from krim_role_rsp_t where role_id = '%s' and rsp_id = '%s'", roleId, responsibilityId))
					};
				}
			};
			return (BigInteger) ExecutorService.getInstance().getExecutor(database).queryForObject(getRoleRespId, BigInteger.class);
		} catch (DatabaseException e) {
			throw new UnexpectedLiquibaseException(String.format("Unable to retrieve foreign key for 'Role Responsibility' (role_id: %s, resp_id: %s)", roleId, responsibilityId), e);
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

}
