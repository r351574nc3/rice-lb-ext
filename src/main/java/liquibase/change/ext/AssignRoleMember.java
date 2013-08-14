/*
 * Copyright 2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package liquibase.change.ext;

import java.math.BigInteger;
import java.util.UUID;

import liquibase.change.custom.CustomSqlChange;
import liquibase.database.Database;
import liquibase.exception.*;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.InsertStatement;

import liquibase.change.core.DeleteDataChange;

import static liquibase.ext.Constants.EXTENSION_PRIORITY;

/**
 * Custom refactoring for adding a Role to KIM.
 *
 * @author Leo Przybylski
 */
public class AssignRoleMember extends KimAbstractChange implements CustomSqlChange {

    private String namespace;
    private String type;
    private String member;
    private String role;
	   	        
    
    public AssignRoleMember() {
        super("roleMember", "Assigning a KIM role", EXTENSION_PRIORITY);
    }

	@Override
	protected String getSequenceName() {
		return "KRIM_ROLE_MBR_ID_S";
	}

	/**
     * Generates the SQL statements required to run the change.
     *
     * @param database databasethe target {@link liquibase.database.Database} associated to this change's statements
     * @return an array of {@link String}s with the statements
     */
	public SqlStatement[] generateStatements(Database database) {
		final InsertStatement assignRole = new InsertStatement(database.getDefaultSchemaName(), "krim_role_mbr_t");
		final BigInteger id = getPrimaryKey(database);
		final BigInteger roleId = getRoleForeignKey(database, getRole(), getNamespace());
		final BigInteger memberId = getPrincipalForeignKey(database, getMember());

		assignRole.addColumnValue("role_mbr_id", id);
		assignRole.addColumnValue("role_id", roleId);
		assignRole.addColumnValue("mbr_id", memberId);
		assignRole.addColumnValue("mbr_typ_cd", getType());
		assignRole.addColumnValue("ver_nbr", 1);
		assignRole.addColumnValue("obj_id", UUID.randomUUID().toString());

		return new SqlStatement[]{
			assignRole
		};
	}


	@Override
	public SqlStatement[] generateRollbackStatements(Database database) throws UnsupportedChangeException, RollbackImpossibleException {
		final DeleteDataChange undoAssign = new DeleteDataChange();
		final BigInteger roleId = getRoleForeignKey(database, getRole(),getNamespace());
		final BigInteger mbrId  = getPrincipalForeignKey(database, getMember());
		undoAssign.setTableName("KRIM_ROLE_MBR_T");
		undoAssign.setWhereClause(String.format("role_id = '%s' and mbr_id = '%s'", roleId, mbrId));
		return undoAssign.generateStatements(database);
	}

    /**
     * Get the member attribute on this object
     *
     * @return member value
     */
    public String getMember() {
        return this.member;
    }

    /**
     * Set the member attribute on this object
     *
     * @param member value to set
     */
    public void setMember(final String member) {
        this.member = member;
    }

    /**
     * Get the namespace attribute on this object
     *
     * @return namespace value
     */
    public String getNamespace() {
        return this.namespace;
    }

    /**
     * Set the namespace attribute on this object
     *
     * @param namespace value to set
     */
    public void setNamespace(final String namespace) {
        this.namespace = namespace;
    }

    /**
     * Get the role attribute on this object
     *
     * @return role value
     */
    public String getRole() {
        return this.role;
    }

    /**
     * Set the role attribute on this object
     *
     * @param role value to set
     */
    public void setRole(final String role) {
        this.role = role;
    }

    /**
     * Get the type attribute on this object
     *
     * @return type value
     */
    public String getType() {
        return this.type;
    }

    /**
     * Set the type attribute on this object
     *
     * @param type value to set
     */
    public void setType(final String type) {
        this.type = type;
    }
}