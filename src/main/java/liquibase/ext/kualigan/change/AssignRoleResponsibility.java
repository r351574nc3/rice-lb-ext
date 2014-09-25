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
package liquibase.ext.kualigan.change;

import liquibase.change.DatabaseChange;
import liquibase.change.DatabaseChangeProperty;
import liquibase.change.core.DeleteDataChange;
import liquibase.change.custom.CustomSqlChange;
import liquibase.database.Database;
import liquibase.exception.RollbackImpossibleException;
import liquibase.exception.CustomChangeException;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.InsertStatement;

import java.math.BigInteger;
import java.util.UUID;

import static liquibase.ext.Constants.EXTENSION_PRIORITY;

/**
 * Custom Liquibase refactoring for adding a role responsibility to a KIM role.
 *
 * @author Leo Przybylski
 */
@DatabaseChange(name="assignRoleResponsibility", description = "Assigns a KIM Responsibility to a given KIM Role.", priority = EXTENSION_PRIORITY)
public class AssignRoleResponsibility extends KimAbstractChange implements CustomSqlChange {
    protected String responsibility;
    protected String namespace;
    protected String role;
    protected String active = "Y";


    public AssignRoleResponsibility() {
        super("roleResponsibility", "Assigning a KIM responsibility to a role", EXTENSION_PRIORITY);
    }

    @Override
    protected String getSequenceName() {
        return "krim_role_rsp_id_s";
    }

    /**
     * Generates the SQL statements required to run the change.
     *
     * @param database databasethe target {@link liquibase.database.Database} associated to this change's statements
     * @return an array of {@link String}s with the statements
     */
    public SqlStatement[] generateStatements(final Database database) {
	final InsertStatement statement = new InsertStatement(null, database.getDefaultSchemaName(), "krim_role_rsp_t");
	final BigInteger id = getPrimaryKey(database);
	final String roleId = getRoleForeignKey(database, getRole(), getNamespace());
	final String rspId = getResponsibilityForeignKey(database, getResponsibility());

	statement.addColumnValue("role_rsp_id", id);
	statement.addColumnValue("role_id", roleId);
	statement.addColumnValue("rsp_id", rspId);
	statement.addColumnValue("actv_ind", getActive());
	statement.addColumnValue("ver_nbr", 1);
	statement.addColumnValue("obj_id", UUID.randomUUID().toString());

	return new SqlStatement[]{
	    statement
	};
    }


    @Override
    public SqlStatement[] generateRollbackStatements(final Database database) throws RollbackImpossibleException {
	final DeleteDataChange undoAssign = new DeleteDataChange();
	final String roleId = getRoleForeignKey(database, getRole(), getNamespace());
	final String responsibilityId = getResponsibilityForeignKey(database, getResponsibility());
	undoAssign.setTableName("krim_role_rsp_t");
	undoAssign.setWhereClause(String.format("role_id = '%s' and rsp_id = '%s'", roleId, responsibilityId));
	return undoAssign.generateStatements(database);
    }

    /**
     * Get the responsibility attribute on this object
     *
     * @return responsibility value
     */
    public String getResponsibility() {
        return this.responsibility;
    }

    /**
     * Set the responsibility attribute on this object
     *
     * @param responsibility value to set
     */
    public void setResponsibility(final String responsibility) {
        this.responsibility = responsibility;
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
     * Get the active attribute on this object
     *
     * @return active value
     */
    public String getActive() {
        return this.active;
    }

    /**
     * Set the active attribute on this object
     *
     * @param active value to set
     */
    public void setActive(final String active) {
        this.active = active;
    }

}
