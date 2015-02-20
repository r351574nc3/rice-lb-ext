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
import liquibase.exception.ValidationErrors;
import liquibase.ext.kualigan.statement.AssignRolePermissionStatement;
import liquibase.ext.kualigan.statement.CreateAttributeDefinitionStatement;
import liquibase.sql.Sql;
import liquibase.sqlgenerator.SqlGeneratorChain;
import liquibase.sqlgenerator.SqlGeneratorFactory;
import liquibase.statement.core.InsertStatement;

import java.util.UUID;

/**
 * Generic base class for generators mapped to the {@link liquibase.ext.kualigan.statement.AssignRolePermissionStatement}
 *
 * @author Leo Przybylski
 */
public class AssignRolePermissionGenerator extends AbstractKimSqlGenerator<AssignRolePermissionStatement> {

	@Override
	public boolean supports(final AssignRolePermissionStatement statement, final Database database) {
		return true;
	}

	@Override
	protected String getSequenceName() {
		return "krim_role_perm_id_s";
	}

	@Override
	public ValidationErrors validate(AssignRolePermissionStatement statement, Database database, SqlGeneratorChain sqlGeneratorChain) {
		final ValidationErrors retval = new ValidationErrors();
		retval.checkRequiredField("permissionName", statement.getPermissionName());
		retval.checkRequiredField("permissionNamespace", statement.getPermissionNamespace());
		retval.checkRequiredField("role", statement.getRoleName());
		retval.checkRequiredField("roleNamespace", statement.getRoleNamespace());
		return retval;
	}

	@Override
	public Sql[] generateSql(AssignRolePermissionStatement statement, Database database, SqlGeneratorChain sqlGeneratorChain) {
		final InsertStatement insertStatement = new InsertStatement("", database.getDefaultSchemaName(), "krim_role_perm_t");

		insertStatement.addColumnValue("role_perm_id", getPrimaryKey(database));
		insertStatement.addColumnValue("role_id", getRoleForeignKey(database, statement.getRoleName(), statement.getRoleNamespace()));
		insertStatement.addColumnValue("perm_id", getPermissionForeignKey(database, statement.getPermissionName(), statement.getPermissionNamespace()));
		insertStatement.addColumnValue("actv_ind", statement.getActive());
		insertStatement.addColumnValue("ver_nbr", 1);
		insertStatement.addColumnValue("obj_id", UUID.randomUUID().toString());

		return SqlGeneratorFactory.getInstance().generateSql(insertStatement, database);

	}
}
