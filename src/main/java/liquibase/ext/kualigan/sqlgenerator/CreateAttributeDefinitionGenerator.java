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
import liquibase.database.core.OracleDatabase;
import liquibase.exception.ValidationErrors;
import liquibase.ext.kualigan.statement.AddRoleMemberAttributeStatement;
import liquibase.ext.kualigan.statement.CreateAttributeDefinitionStatement;
import liquibase.sql.Sql;
import liquibase.sqlgenerator.SqlGeneratorChain;
import liquibase.sqlgenerator.SqlGeneratorFactory;
import liquibase.statement.core.InsertStatement;

import java.util.UUID;

/**
 * Generic base class for generators mapped to the {@link CreateAttributeDefinitionStatement}
 *
 * @author Leo Przybylski
 */
public class CreateAttributeDefinitionGenerator extends AbstractKimSqlGenerator<CreateAttributeDefinitionStatement> {

	@Override
	public boolean supports(final CreateAttributeDefinitionStatement statement, final Database database) {
		return true;
	}

	@Override
	protected String getSequenceName() {
		return "krim_attr_defn_id_s";
	}

	@Override
	public ValidationErrors validate(CreateAttributeDefinitionStatement statement, Database database, SqlGeneratorChain sqlGeneratorChain) {
		final ValidationErrors retval = new ValidationErrors();
		retval.checkRequiredField("label", statement.getLabel());
		retval.checkRequiredField("namespace", statement.getNamespace());
		retval.checkRequiredField("name", statement.getName());
		retval.checkRequiredField("component", statement.getComponent());
		return retval;
	}

	@Override
	public Sql[] generateSql(CreateAttributeDefinitionStatement statement, Database database, SqlGeneratorChain sqlGeneratorChain) {
		final InsertStatement insertStatement = new InsertStatement("", database.getDefaultSchemaName(), "krim_attr_defn_t");

		insertStatement.addColumnValue("kim_attr_defn_id", getPrimaryKey(database));
		insertStatement.addColumnValue("nmspc_cd", statement.getNamespace());
		insertStatement.addColumnValue("nm", statement.getName());
		insertStatement.addColumnValue("lbl", statement.getLabel());
		insertStatement.addColumnValue("actv_ind", statement.getActive());
		insertStatement.addColumnValue("cmpnt_nm", statement.getComponent());
		insertStatement.addColumnValue("ver_nbr", 1);
		insertStatement.addColumnValue("obj_id", UUID.randomUUID().toString());

		return SqlGeneratorFactory.getInstance().generateSql(insertStatement, database);
	}
}
