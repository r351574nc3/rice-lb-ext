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
import liquibase.exception.RollbackImpossibleException;
import liquibase.exception.UnsupportedChangeException;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.InsertStatement;

import liquibase.change.core.DeleteDataChange;

import static liquibase.ext.Constants.EXTENSION_PRIORITY;

/**
 * Custom Liquibase refactoring for adding a permission to a KIM role.
 *
 * @author Leo Przybylski
 */
public class AssignRolePermission extends KimAbstractChange implements CustomSqlChange {
    private String permission;
    private String namespace;
    private String role;
    private String active = "Y";
    
    
    public AssignRolePermission() {
        super("rolePermission", "Assigning a KIM permission to a role", EXTENSION_PRIORITY);
    }

	@Override
	protected String getSequenceName() {
		return "krim_role_perm_id_s";
	}

	/**
     * Generates the SQL statements required to run the change.
     *
     * @param database databasethe target {@link liquibase.database.Database} associated to this change's statements
     * @return an array of {@link String}s with the statements
     */
    public SqlStatement[] generateStatements(Database database) {
		final InsertStatement assignPermission = new InsertStatement(database.getDefaultSchemaName(), "krim_role_perm_t");
		final BigInteger id = getPrimaryKey(database);
		final BigInteger roleId = getRoleForeignKey(database, getRole(), getNamespace());
		final BigInteger permId = getPermissionForeignKey(database, getPermission(), getNamespace());

		assignPermission.addColumnValue("role_perm_id", id);
		assignPermission.addColumnValue("role_id", roleId);
		assignPermission.addColumnValue("perm_id", permId);
		assignPermission.addColumnValue("actv_ind", getActive());
		assignPermission.addColumnValue("ver_nbr", 1);
		assignPermission.addColumnValue("obj_id", UUID.randomUUID().toString());

		return new SqlStatement[]{
			assignPermission
		};
	}


	@Override
	public SqlStatement[] generateRollbackStatements(Database database) throws UnsupportedChangeException, RollbackImpossibleException {
		final DeleteDataChange undoAssign = new DeleteDataChange();
		final BigInteger roleId = getRoleForeignKey(database, getRole(), getNamespace());
		final BigInteger permId = getPermissionForeignKey(database, getPermission(), getNamespace());
		undoAssign.setTableName("krim_role_perm_t");
		undoAssign.setWhereClause(String.format("role_id = '%s' and perm_id = '%s'", roleId, permId));
		return undoAssign.generateStatements(database);
	}

    /**
     * Get the permission attribute on this object
     *
     * @return permission value
     */
    public String getPermission() {
        return this.permission;
    }

    /**
     * Set the permission attribute on this object
     *
     * @param permission value to set
     */
    public void setPermission(final String permission) {
        this.permission = permission;
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