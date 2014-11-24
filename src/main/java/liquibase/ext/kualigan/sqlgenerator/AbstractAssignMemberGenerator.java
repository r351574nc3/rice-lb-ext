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
import liquibase.statement.DatabaseFunction;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.InsertStatement;

import liquibase.sql.Sql;

import liquibase.ext.kualigan.statement.AssignMemberStatement;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static liquibase.ext.Constants.EXTENSION_PRIORITY;

/**
 * Generic base class for generators mapped to the {@link AssignMemberStatement}
 *
 * @author Leo Przybylski
 */
public abstract class AbstractAssignMemberGenerator extends AbstractKimSqlGenerator<AssignMemberStatement> {

    @Override
    protected String getSequenceName() {
	return "KRIM_ROLE_MBR_ID_S";
    }

    @Override
    public ValidationErrors validate(final AssignMemberStatement statement,
                                     final Database database, 
				     final SqlGeneratorChain generators) {
        final ValidationErrors retval = new ValidationErrors();
        retval.checkRequiredField("namespace", statement.getNamespace());
        retval.checkRequiredField("member", statement.getMember());
        retval.checkRequiredField("role", statement.getRole());
        return retval;
    }

    /**
     * Generate the actual Sql for the given statement and database.
     *
     * @see liquibase.sqlgenerator#generateSql(StatementType, Database, SqlGeneratorChain)
     */
    public Sql[] generateSql(final AssignMemberStatement statement, 
			     final Database database, 
			     final SqlGeneratorChain chain) {
	final InsertStatement assignRole = new InsertStatement(null, database.getDefaultSchemaName(), "krim_role_mbr_t");

	assignRole.addColumnValue("role_mbr_id", getPrimaryKey(database));
	assignRole.addColumnValue("role_id", getRoleForeignKey(database, statement.getRole(), statement.getNamespace()));
	assignRole.addColumnValue("mbr_id", getMemberId(database, statement));
	assignRole.addColumnValue("mbr_typ_cd", statement.getType());
	assignRole.addColumnValue("ver_nbr", 1);
	assignRole.addColumnValue("obj_id", UUID.randomUUID().toString());

	final List<SqlStatement> retval = new ArrayList<SqlStatement>();
	retval.add(assignRole);
	retval.addAll(statement.getAttributes());
	retval.addAll(statement.getActions());
	
	return SqlGeneratorFactory.getInstance().generateSql(retval.toArray(new SqlStatement[retval.size()]), database);
    }

    protected DatabaseFunction getMemberId(final Database database, final AssignMemberStatement statement) {
	DatabaseFunction memberId;
	if ("P".equals(statement.getType())){
	    memberId = getPrincipalForeignKey(database, statement.getMember());
	}
	else if ("R".equals(statement.getType())){
	    memberId = getRoleForeignKey(database, statement.getMember(), statement.getMemberNamespace() != null 
					 ? statement.getMemberNamespace() 
					 : statement.getNamespace());
	}
	else {
	    throw new RuntimeException(String.format("Role type '%s' not supported!", statement.getType()));
	}
	return memberId;
    }
}
