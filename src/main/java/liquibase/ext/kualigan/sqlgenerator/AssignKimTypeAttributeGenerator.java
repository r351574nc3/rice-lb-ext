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
import liquibase.ext.kualigan.statement.AssignKimTypeAttributeStatement;
import liquibase.sql.Sql;
import liquibase.sqlgenerator.SqlGeneratorChain;
import liquibase.sqlgenerator.SqlGeneratorFactory;
import liquibase.statement.core.InsertStatement;

import java.util.UUID;

/**
 * Generic base class for generators mapped to the {@link liquibase.ext.kualigan.statement.AssignRoleMemberStatement}
 *
 * @author Leo Przybylski
 */
public class AssignKimTypeAttributeGenerator extends AbstractKimSqlGenerator<AssignKimTypeAttributeStatement> {

	@Override
	public boolean supports(final AssignKimTypeAttributeStatement statement, final Database database) {
		return true;
	}

	@Override
	protected String getSequenceName() {
		return "krim_typ_attr_id_s";
	}

	@Override
	public ValidationErrors validate(AssignKimTypeAttributeStatement statement, Database database, SqlGeneratorChain sqlGeneratorChain) {
		final ValidationErrors retval = new ValidationErrors();
		retval.checkRequiredField("kimTypeName", statement.getTypeName());
		retval.checkRequiredField("kimTypeNamespace", statement.getAttributeDefinition());
		return retval;
	}

	@Override
	public Sql[] generateSql(AssignKimTypeAttributeStatement statement, Database database, SqlGeneratorChain sqlGeneratorChain) {
		final InsertStatement insertStatement = new InsertStatement("", database.getDefaultSchemaName(), "KRIM_TYP_ATTR_T");

		insertStatement.addColumnValue("KIM_TYP_ATTR_ID", getPrimaryKey(database));
		insertStatement.addColumnValue("KIM_TYP_ID", getTypeForeignKey(database, statement.getTypeName(), statement.getTypeNamespace()));
		insertStatement.addColumnValue("KIM_ATTR_DEFN_ID", getAttributeDefinitionForeignKey(database, statement.getAttributeDefinition()));
		insertStatement.addColumnValue("VER_NBR", 1);
		insertStatement.addColumnValue("OBJ_ID", UUID.randomUUID().toString());
		insertStatement.addColumnValue("ACTV_IND", statement.getActive());

		return SqlGeneratorFactory.getInstance().generateSql(insertStatement, database);
	}

}
