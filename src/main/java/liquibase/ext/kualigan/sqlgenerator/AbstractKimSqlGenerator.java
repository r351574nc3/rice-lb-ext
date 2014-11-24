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
package liquibase.ext.kualigan.sqlgenerator;

import liquibase.database.Database;
import liquibase.exception.*;
import liquibase.executor.ExecutorService;
import liquibase.sql.Sql;
import liquibase.sql.UnparsedSql;
import liquibase.sqlgenerator.SqlGeneratorChain;
import liquibase.sqlgenerator.core.AbstractSqlGenerator;
import liquibase.statement.core.RuntimeStatement;
import liquibase.statement.DatabaseFunction;
import liquibase.statement.SqlStatement;
import org.apache.commons.lang.StringUtils;

import java.math.BigInteger;
import java.util.List;

/**
 * Generic base class for generators mapped to the {@link CreateTypeStatement}
 *
 * @author Leo Przybylski
 */
public abstract class AbstractKimSqlGenerator<T extends SqlStatement> extends AbstractSqlGenerator<T> {
    protected abstract String getSequenceName();


    protected DatabaseFunction getPrimaryKey(final Database database) {
	return database.supportsSequences() ? getPrimaryKey(database, false) : getPrimaryKey(database, true);
    }

    protected DatabaseFunction getPrimaryKey(final Database database, final boolean shouldIncrement) {
	if (shouldIncrement) {
	    incrementSequence(database);
	}
	final String subQuery = database.supportsSequences() 
	    ? String.format("%s.NEXTVAL()", getSequenceName()) 
	    : String.format("(select max(id) from %s)", getSequenceName());

	return new DatabaseFunction(subQuery);
    }

    protected DatabaseFunction getPermissionTemplateForeignKey(final Database database, final String templateName) {
	return new DatabaseFunction(String.format("select PERM_TMPL_ID from KRIM_PERM_TMPL_T where NM = '%s'", templateName));
    }

    protected DatabaseFunction getResponsibilityTemplateForeignKey(final Database database, final String templateName) {
	return new DatabaseFunction(String.format("select RSP_TMPL_ID from KRIM_RSP_TMPL_T where nm = '%s'", templateName));
    }

    protected DatabaseFunction getAttributeDefinitionForeignKey(final Database database, final String attributeDef){
        return new DatabaseFunction(String.format("(select KIM_ATTR_DEFN_ID from krim_attr_defn_t where nm = '%s')", attributeDef));
    }

    protected DatabaseFunction getTypeForeignKey(final Database database, final String kimType) {
        return new DatabaseFunction(String.format("(select kim_typ_id from krim_typ_t where nm = '%s')", kimType));
    }

    protected DatabaseFunction getTypeForeignKey(final Database database, final String kimType, final String kimTypeNamespace) {
        if (kimTypeNamespace == null){
            return getTypeForeignKey(database, kimType);
        }
        return new DatabaseFunction(String.format("(select kim_typ_id from krim_typ_t where nm = '%s' and nmspc_cd = '%s')", kimType, kimTypeNamespace));
    }

    protected DatabaseFunction getPermissionForeignKey(final Database database, final String permissionName, final String permissionNameSpace){
	return new DatabaseFunction(String.format("(select PERM_ID from KRIM_PERM_T where nm = '%s' and NMSPC_CD = '%s')", permissionName, permissionNameSpace));
    }

    protected DatabaseFunction getPermissionForeignKey(final Database database, final String permissionName, final String permissionNameSpace, final String permissionTemplate){
        if (permissionTemplate == null){
            return getPermissionForeignKey(database,permissionName,permissionNameSpace);
        }

	final DatabaseFunction permissionTemplateId = getPermissionTemplateForeignKey(database,permissionTemplate);
	return new DatabaseFunction(String.format("(select PERM_ID from KRIM_PERM_T where nm = '%s' and NMSPC_CD = '%s' and perm_tmpl_id IN '%s')", permissionName, permissionNameSpace, permissionTemplateId));
    }


    protected DatabaseFunction getRoleForeignKey(final Database database, final String roleName, final String namespaceCode) {
	return new DatabaseFunction(String.format("(eselect ROLE_ID from KRIM_ROLE_T where ROLE_NM = '%s' and NMSPC_CD = '%s')", roleName, namespaceCode));
    }

    protected DatabaseFunction getPrincipalForeignKey(final Database database, final String memberName) {
	return new DatabaseFunction(String.format("(select PRNCPL_ID from KRIM_PRNCPL_T where PRNCPL_NM = '%s')", memberName));
    }

    protected DatabaseFunction getResponsibilityForeignKey(final Database database, final String responsibilityName) {
	return new DatabaseFunction(String.format("(select rsp_id from krim_rsp_t where nm = '%s')", responsibilityName));
    }
    
    protected DatabaseFunction getResponsibilityForeignKey(final Database database, final String responsibilityName, final String responsibilityNamespace) {
        if (responsibilityNamespace == null){
            return getResponsibilityForeignKey(database,responsibilityName);
        }
	return new DatabaseFunction(String.format("(select rsp_id from krim_rsp_t where nm = '%s' and nmspc_cd = '%s')", responsibilityName, responsibilityNamespace));
    }

    protected DatabaseFunction getRoleResponsibilityForeignKey(final Database database, final String roleId , final String responsibilityId) {
	return new DatabaseFunction(String.format("(select role_rsp_id from krim_role_rsp_t where role_id = '%s' and rsp_id = '%s')", roleId, responsibilityId));
    }

    protected DatabaseFunction getRoleResponsibilityForeignKey(final Database database, final DatabaseFunction roleId , final DatabaseFunction responsibilityId) {
	return new DatabaseFunction(String.format("(select role_rsp_id from krim_role_rsp_t where role_id IN '%s' and rsp_id IN '%s')", roleId, responsibilityId));
    }

    protected DatabaseFunction getRoleMemberForeignKey(final Database database, final String roleId , final String memberId) {
	return new DatabaseFunction(String.format("(select role_mbr_id from krim_role_mbr_t where role_id = '%s' and mbr_id = '%s')", roleId, memberId));
    }

    protected DatabaseFunction getRoleMemberForeignKey(final Database database, final DatabaseFunction roleId , final DatabaseFunction memberId) {
	return new DatabaseFunction(String.format("(select role_mbr_id from krim_role_mbr_t where role_id IN '%s' and mbr_id IN '%s')", roleId, memberId));
    }

    protected DatabaseFunction getRoleMemberForeignKey(final Database database, final String roleId , final String memberId, final List<String> uniqueAttributeValues) {
        if (uniqueAttributeValues == null){
            return getRoleMemberForeignKey(database,roleId,memberId);
        }
	return new DatabaseFunction(String.format("(select rm.role_mbr_id  from krim_role_mbr_t rm left join krim_role_mbr_attr_data_t rma on rma.ROLE_MBR_ID = rm.ROLE_MBR_ID where rm.role_id = '%s' and rm.mbr_id = '%s' and rma.ATTR_VAL IN ('%s'))", roleId, memberId, StringUtils.join(uniqueAttributeValues, "', '")));
    }

    protected void incrementSequence(final Database database) {
        try {
            final SqlStatement incrementSequenceStatement = new RuntimeStatement() {
                    public Sql[] generate(final Database database) {
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
