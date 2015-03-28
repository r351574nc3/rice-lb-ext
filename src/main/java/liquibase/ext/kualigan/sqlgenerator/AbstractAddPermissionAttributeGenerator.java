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
import liquibase.sqlgenerator.SqlGeneratorChain;
import liquibase.sqlgenerator.SqlGeneratorFactory;
import liquibase.statement.DatabaseFunction;
import liquibase.statement.core.InsertStatement;

import liquibase.sql.Sql;

import liquibase.ext.kualigan.statement.AddPermissionAttributeStatement;

import java.util.UUID;

/**
 * Generic base class for generators mapped to the {@link AddPermissionAttributeStatement}
 *
 * @author Leo Przybylski
 */
public abstract class AbstractAddPermissionAttributeGenerator extends AbstractKimSqlGenerator<AddPermissionAttributeStatement> {
    
    @Override
    protected String getSequenceName() {
	return "KRIM_ATTR_DATA_ID_S";
    }

    @Override
    public ValidationErrors validate(final AddPermissionAttributeStatement statement,
                                     final Database database, 
				     final SqlGeneratorChain generators) {
        final ValidationErrors retval = new ValidationErrors();
        retval.checkRequiredField("namespace", statement.getNamespace());
        retval.checkRequiredField("permission", statement.getPermission());
        retval.checkRequiredField("value", statement.getValue());
        return retval;
    }

    /**
     * Generate the actual Sql for the given statement and database.
     *
     * @see liquibase.sqlgenerator#generateSql(StatementType, Database, SqlGeneratorChain)
     */
    public Sql[] generateSql(final AddPermissionAttributeStatement statement, 
			     final Database database, 
			     final SqlGeneratorChain chain) {
        final InsertStatement insertAttribute = new InsertStatement("", database.getDefaultSchemaName(), "krim_perm_attr_data_t");

	insertAttribute.addColumnValue("attr_data_id", getPrimaryKey(database));
	insertAttribute.addColumnValue("perm_id", getPermissionForeignKey(statement, database));
	insertAttribute.addColumnValue("kim_typ_id", getTypeForeignKey(database, statement.getType()));
	insertAttribute.addColumnValue("kim_attr_defn_id", getAttributeDefinitionForeignKey(database, statement.getAttributeDef()));
	insertAttribute.addColumnValue("attr_val", statement.getValue());
	insertAttribute.addColumnValue("ver_nbr", 1);
	insertAttribute.addColumnValue("obj_id", UUID.randomUUID().toString());
	
	return SqlGeneratorFactory.getInstance().generateSql(insertAttribute, database);
    }

	private DatabaseFunction getPermissionForeignKey(AddPermissionAttributeStatement statement, Database database) {
		if (statement.getPermissionFkSeq() != null){
			return getForeignKeySequenceCurrentValue(database,statement.getPermissionFkSeq());
		}
		return getPermissionForeignKey(database, statement.getPermission(), statement.getNamespace());
	}
}
