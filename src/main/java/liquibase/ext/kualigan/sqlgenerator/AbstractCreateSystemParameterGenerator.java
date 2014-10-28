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
import liquibase.sqlgenerator.core.AbstractSqlGenerator;
import liquibase.statement.core.InsertStatement;

import liquibase.sql.Sql;

import liquibase.ext.kualigan.statement.CreateSystemParameterStatement;

import java.util.UUID;

import static liquibase.ext.Constants.EXTENSION_PRIORITY;

/**
 * Generic base class for generators mapped to the {@link CreateSystemParameterStatement}
 *
 * @author Leo Przybylski
 */
public abstract class AbstractCreateSystemParameterGenerator extends AbstractKimSqlGenerator<CreateSystemParameterStatement> {


    @Override
    protected String getSequenceName() {
	return "";
    }

    @Override
    public ValidationErrors validate(final CreateSystemParameterStatement statement,
                                     final Database database, 
				     final SqlGeneratorChain generators) {
        final ValidationErrors retval = new ValidationErrors();
        retval.checkRequiredField("namespace", statement.getNamespace());
        retval.checkRequiredField("name", statement.getName());
        retval.checkRequiredField("component", statement.getComponent());
        retval.checkRequiredField("type", statement.getType());
        retval.checkRequiredField("value", statement.getValue());
        retval.checkRequiredField("application", statement.getApplication());
        return retval;
    }

    /**
     * Generate the actual Sql for the given statement and database.
     *
     * @see liquibase.sqlgenerator#generateSql(StatementType, Database, SqlGeneratorChain)
     */
    public Sql[] generateSql(final CreateSystemParameterStatement statement, 
			     final Database database, 
			     final SqlGeneratorChain chain) {
	final InsertStatement insertParameter = new InsertStatement("", database.getDefaultSchemaName(),"KRCR_PARM_T");
	insertParameter.addColumnValue("APPL_ID", statement.getApplication());
	insertParameter.addColumnValue("NMSPC_CD", statement.getNamespace());
	insertParameter.addColumnValue("CMPNT_CD", statement.getComponent());
	insertParameter.addColumnValue("PARM_NM", statement.getName());
	insertParameter.addColumnValue("VAL", statement.getValue());
	insertParameter.addColumnValue("PARM_TYP_CD", statement.getType());
	if (statement.getOperator() != null) {
	    insertParameter.addColumnValue("EVAL_OPRTR_CD", statement.getOperator().substring(0, 1));
	}
	if (statement.getDescription() != null) {
	    insertParameter.addColumnValue("PARM_DESC_TXT", statement.getDescription());
	}

	return SqlGeneratorFactory.getInstance().generateSql(insertParameter, database);
    }
}
