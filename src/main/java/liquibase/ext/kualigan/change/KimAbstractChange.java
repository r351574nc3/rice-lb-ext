// Copyright 2014 Leo Przybylski. All rights reserved.
//
// Redistribution and use in source and binary forms, with or without modification, are
// permitted provided that the following conditions are met:
//
//    1. Redistributions of source code must retain the above copyright notice, this list of
//       conditions and the following disclaimer.
//
//    2. Redistributions in binary form must reproduce the above copyright notice, this list
//       of conditions and the following disclaimer in the documentation and/or other materials
//       provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY Leo Przybylski ''AS IS'' AND ANY EXPRESS OR IMPLIED
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
// FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> OR
// CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
// ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
// NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
// ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
// The views and conclusions contained in the software and documentation are those of the
// authors and should not be interpreted as representing official policies, either expressed
// or implied, of Leo Przybylski.
package liquibase.ext.kualigan.change;

import liquibase.change.AbstractChange;
import liquibase.change.custom.CustomSqlChange;
import liquibase.change.custom.CustomSqlRollback;
import liquibase.database.Database;
import liquibase.exception.*;
import liquibase.executor.ExecutorService;
import liquibase.resource.ResourceAccessor;
import liquibase.sql.Sql;
import liquibase.sql.UnparsedSql;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.RuntimeStatement;
import org.apache.commons.lang.StringUtils;

import java.math.BigInteger;
import java.util.List;

public abstract class KimAbstractChange extends AbstractChange implements CustomSqlChange, CustomSqlRollback {

    public KimAbstractChange(String changeName, String changeDescription, int priority) {
    }

    @Override
    public final ValidationErrors validate(Database database) {
	//todo: Default validation calls generateStatements which in turn tries to retrieve foreign key references for parameters not yet initialized
	return new ValidationErrors();
    }

    @Override
    public Warnings warn(Database database) {
	return new Warnings();
    }

    @Override
    public String getConfirmationMessage() {
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


		//todo: remove.... all primary key retrievals should happen in sql generators
    protected BigInteger getPrimaryKey(Database database) {
	try {
	    final SqlStatement getPermissionId = new RuntimeStatement() {
		    public Sql[] generate(Database database) {
			if (database.supportsSequences()) {
			    return new Sql[]{
						new UnparsedSql(String.format("%s.NEXTVAL", getSequenceName()))
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

    protected String getPermissionTemplateForeignKey(Database database, final String templateName) {
	try {
	    final RuntimeStatement templateIdStatement = new RuntimeStatement() {
		    public Sql[] generate(Database database1) {
			return new Sql[]{
			    new UnparsedSql(String.format("select PERM_TMPL_ID from KRIM_PERM_TMPL_T where NM = '%s'", templateName))
			};
		    }
		};
	    return (String) ExecutorService.getInstance().getExecutor(database).queryForObject(templateIdStatement, String.class);
	} catch (DatabaseException e) {
	    throw new UnexpectedLiquibaseException(String.format("Unable to retrieve foreign key for 'Permission Template' (%s)", templateName), e);
	}
    }

    protected String getResponsibilityTemplateForeignKey(Database database, final String templateName) {
	try {
	    final RuntimeStatement templateIdStatement = new RuntimeStatement() {
		    public Sql[] generate(Database database1) {
			return new Sql[]{
			    new UnparsedSql(String.format("select RSP_TMPL_ID from KRIM_RSP_TMPL_T where nm = '%s'", templateName))
			};
		    }
		};
	    return (String) ExecutorService.getInstance().getExecutor(database).queryForObject(templateIdStatement, String.class);
	} catch (DatabaseException e) {
	    throw new UnexpectedLiquibaseException(String.format("Unable to retrieve foreign key for 'Responsibility Template' (nm: %s)", templateName), e);
	}
    }

    protected String getAttributeDefinitionForeignKey(Database database, final String attributeDef){
	try {
	    final SqlStatement getDefinitionId = new RuntimeStatement() {
		    public Sql[] generate(Database database) {
			return new Sql[] {
			    new UnparsedSql(String.format("select KIM_ATTR_DEFN_ID from krim_attr_defn_t where nm = '%s'", attributeDef))
			};
		    }
		};
	    return (String) ExecutorService.getInstance().getExecutor(database).queryForObject(getDefinitionId, String.class);
	} catch (DatabaseException e) {
	    throw new UnexpectedLiquibaseException(String.format("Unable to retrieve foreign key 'Attribute Definition' (%s)",attributeDef),e);
	}
    }

    protected String getTypeForeignKey(Database database, final String kimType) {
	try {
	    final SqlStatement getTypeId = new RuntimeStatement() {
		    public Sql[] generate(Database database) {
			return new Sql[] {
			    new UnparsedSql(String.format("select kim_typ_id from krim_typ_t where nm = '%s'", kimType))
			};
		    }
		};
	    return (String) ExecutorService.getInstance().getExecutor(database).queryForObject(getTypeId, String.class);
	} catch (DatabaseException e) {
	    throw new UnexpectedLiquibaseException(String.format("Unable to retrieve foreign key 'Type Reference' (%s)",kimType),e);
	}
    }

    protected String getTypeForeignKey(Database database, final String kimType, final String kimTypeNamespace) {
	if (kimTypeNamespace == null){
	    return getTypeForeignKey(database,kimType);
	}
	try {
	    final SqlStatement getTypeId = new RuntimeStatement() {
		    public Sql[] generate(Database database) {
			return new Sql[] {
			    new UnparsedSql(String.format("select kim_typ_id from krim_typ_t where nm = '%s' and nmspc_cd = '%s'", kimType, kimTypeNamespace))
			};
		    }
		};
	    return (String) ExecutorService.getInstance().getExecutor(database).queryForObject(getTypeId, String.class);
	} catch (DatabaseException e) {
	    throw new UnexpectedLiquibaseException(String.format("Unable to retrieve foreign key 'Type Reference' (%s, %s)",kimType, kimTypeNamespace),e);
	}
    }

    protected String getPermissionForeignKey(Database database, final String permissionName, final String permissionNameSpace){
	try {
	    final SqlStatement getPermissionId = new RuntimeStatement() {
		    public Sql[] generate(Database database) {
			return new Sql[]{
			    new UnparsedSql(String.format("select PERM_ID from KRIM_PERM_T where nm = '%s' and NMSPC_CD = '%s'", permissionName, permissionNameSpace))
			};
		    }
		};
	    return (String) ExecutorService.getInstance().getExecutor(database).queryForObject(getPermissionId, String.class);
	} catch (DatabaseException e) {
	    throw new UnexpectedLiquibaseException(String.format("Unable to retreive foreign key reference for 'Permission' (name: %s, namespace: %s)", permissionName, permissionNameSpace));
	}
    }

    protected String getPermissionForeignKey(Database database, final String permissionName, final String permissionNameSpace, final String permissionTemplate){
	if (permissionTemplate == null){
	    return getPermissionForeignKey(database,permissionName,permissionNameSpace);
	}
	try {
	    final String permissionTemplateId = getPermissionTemplateForeignKey(database,permissionTemplate);
	    final SqlStatement getPermissionId = new RuntimeStatement() {
		    public Sql[] generate(Database database) {
			return new Sql[]{
			    new UnparsedSql(String.format("select PERM_ID from KRIM_PERM_T where nm = '%s' and NMSPC_CD = '%s' and perm_tmpl_id = '%s'", permissionName, permissionNameSpace, permissionTemplateId))
			};
		    }
		};
	    return (String) ExecutorService.getInstance().getExecutor(database).queryForObject(getPermissionId, String.class);
	} catch (DatabaseException e) {
	    throw new UnexpectedLiquibaseException(String.format("Unable to retreive foreign key reference for 'Permission' (name: %s, namespace: %s)", permissionName, permissionNameSpace));
	}
    }


    protected String getRoleForeignKey(Database database, final String roleName, final String namespaceCode) {
	try {
	    final SqlStatement getRoleId = new RuntimeStatement() {
		    public Sql[] generate(Database database) {
			return new Sql[] {
			    new UnparsedSql(String.format("select ROLE_ID from KRIM_ROLE_T where ROLE_NM = '%s' and NMSPC_CD = '%s'", roleName, namespaceCode))
			};
		    }
		};
	    return (String) ExecutorService.getInstance().getExecutor(database).queryForObject(getRoleId, String.class);
	} catch (DatabaseException e) {
	    throw new UnexpectedLiquibaseException(String.format("Unable to retrieve foreign key for 'Role' (role: %s, namespace: %s)", roleName, namespaceCode), e);
	}
    }

    protected String getPrincipalForeignKey(Database database, final String memberName) {
	try {
	    final SqlStatement getMemberId = new RuntimeStatement() {
		    public Sql[] generate(Database database) {
			return new Sql[] {
			    new UnparsedSql(String.format("select PRNCPL_ID from KRIM_PRNCPL_T where PRNCPL_NM = '%s'", memberName))
			};
		    }
		};
	    return (String) ExecutorService.getInstance().getExecutor(database).queryForObject(getMemberId, String.class);
	} catch (DatabaseException e) {
	    throw new UnexpectedLiquibaseException(String.format("Unable to retrieve foreign key for 'Principal' (%s)", memberName), e);
	}
    }

    protected String getResponsibilityForeignKey(Database database, final String responsibilityName) {
	try {
	    final SqlStatement getResponsibilityId = new RuntimeStatement() {
		    public Sql[] generate(Database database) {
			return new Sql[]{
			    new UnparsedSql(String.format("select rsp_id from krim_rsp_t where nm = '%s'", responsibilityName))
			};
		    }
		};
	    return (String) ExecutorService.getInstance().getExecutor(database).queryForObject(getResponsibilityId, String.class);
	} catch (DatabaseException e) {
	    throw new UnexpectedLiquibaseException(String.format("Unable to retrieve foreign key for 'Responsibility' (%s)", responsibilityName), e);
	}
    }

    protected String getResponsibilityForeignKey(Database database, final String responsibilityName, final String responsibilityNamespace) {
	if (responsibilityNamespace == null){
	    return getResponsibilityForeignKey(database,responsibilityName);
	}
	try {
	    final SqlStatement getResponsibilityId = new RuntimeStatement() {
		    public Sql[] generate(Database database) {
			return new Sql[]{
			    new UnparsedSql(String.format("select rsp_id from krim_rsp_t where nm = '%s' and nmspc_cd = '%s'", responsibilityName, responsibilityNamespace))
			};
		    }
		};
	    return (String) ExecutorService.getInstance().getExecutor(database).queryForObject(getResponsibilityId, String.class);
	} catch (DatabaseException e) {
	    throw new UnexpectedLiquibaseException(String.format("Unable to retrieve foreign key for 'Responsibility' (nm: %s, space: %s)", responsibilityName, responsibilityNamespace), e);
	}
    }

    protected String getRoleResponsibilityForeignKey(Database database, final String roleId , final String responsibilityId) {
	try {
	    final SqlStatement getRoleRespId = new RuntimeStatement() {
		    public Sql[] generate(Database database) {
			return new Sql[]{
			    new UnparsedSql(String.format("select role_rsp_id from krim_role_rsp_t where role_id = '%s' and rsp_id = '%s'", roleId, responsibilityId))
			};
		    }
		};
	    return (String) ExecutorService.getInstance().getExecutor(database).queryForObject(getRoleRespId, String.class);
	} catch (DatabaseException e) {
	    throw new UnexpectedLiquibaseException(String.format("Unable to retrieve foreign key for 'Role Responsibility' (role_id: %s, resp_id: %s)", roleId, responsibilityId), e);
	}
    }

    protected String getRoleMemberForeignKey(Database database, final String roleId , final String memberId) {
	try {
	    final SqlStatement getRoleRespId = new RuntimeStatement() {
		    public Sql[] generate(Database database) {
			return new Sql[]{
			    new UnparsedSql(String.format("select role_mbr_id from krim_role_mbr_t where role_id = '%s' and mbr_id = '%s'", roleId, memberId))
			};
		    }
		};
	    return (String) ExecutorService.getInstance().getExecutor(database).queryForObject(getRoleRespId, String.class);
	} catch (DatabaseException e) {
	    throw new UnexpectedLiquibaseException(String.format("Unable to retrieve foreign key for 'Role Member' (role_id: %s, member_id: %s)", roleId, memberId), e);
	}
    }

    protected String getRoleMemberForeignKey(Database database, final String roleId , final String memberId, final List<String> uniqueAttributeValues) {
	if (uniqueAttributeValues == null){
	    return getRoleMemberForeignKey(database,roleId,memberId);
	}
	try {
	    final StringBuilder sql = new StringBuilder();
	    final String sqlTemplate = "select rm.role_mbr_id  from krim_role_mbr_t rm left join krim_role_mbr_attr_data_t rma on rma.ROLE_MBR_ID = rm.ROLE_MBR_ID  " +
		"where rm.role_id = '%s' and rm.mbr_id = '%s' and rma.ATTR_VAL = '%s'";
	    for (String uniqueAttributeValue : uniqueAttributeValues){
		sql.append(String.format(sqlTemplate, roleId, memberId, uniqueAttributeValue));
		sql.append(" intersect ");
	    }
	    final SqlStatement getRoleRespId = new RuntimeStatement() {
		    public Sql[] generate(Database database) {
			return new Sql[]{
			    new UnparsedSql(StringUtils.removeEnd(sql.toString(),"intersect "))
			};
		    }
		};
	    return (String) ExecutorService.getInstance().getExecutor(database).queryForObject(getRoleRespId, String.class);
	} catch (DatabaseException e) {
	    throw new UnexpectedLiquibaseException(String.format("Unable to retrieve foreign key for 'Role Member' (role_id: %s, member_id: %s, attr. val: %s)", roleId, memberId, uniqueAttributeValues), e);
	}
    }

    protected void incrementSequence(Database database) {
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
