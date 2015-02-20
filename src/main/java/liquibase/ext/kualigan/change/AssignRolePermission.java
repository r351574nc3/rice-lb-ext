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
package liquibase.ext.kualigan.change;

import java.math.BigInteger;
import java.util.UUID;

import liquibase.change.Change;
import liquibase.change.DatabaseChange;
import liquibase.change.DatabaseChangeProperty;
import liquibase.change.custom.CustomSqlChange;
import liquibase.database.Database;
import liquibase.exception.RollbackImpossibleException;
import liquibase.exception.CustomChangeException;
import liquibase.ext.kualigan.statement.AssignRolePermissionStatement;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.InsertStatement;

import liquibase.change.core.DeleteDataChange;

import static liquibase.ext.Constants.EXTENSION_PRIORITY;

/**
 * Custom Liquibase refactoring for adding a permission to a KIM role.
 *
 * @author Leo Przybylski
 */
//todo: implement sql generator
@DatabaseChange(name="rolePermission", description = "Assign a KIM Permission to a KIM Role.", priority = EXTENSION_PRIORITY)
public class AssignRolePermission extends KimAbstractChange implements CustomSqlChange {

    protected String permission;
    protected String permissionNamespace;
    protected String role;
    protected String roleNamespace;
    protected String active;
    
    
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
    public SqlStatement[] generateStatements(final Database database) {
	    return new SqlStatement[] {
			    new AssignRolePermissionStatement(permission,permissionNamespace,role,roleNamespace,active)
	    };
    }

    @Override
    public SqlStatement[] generateRollbackStatements(final Database database) throws RollbackImpossibleException {
	final DeleteDataChange undoAssign = new DeleteDataChange();
	final String roleId = getRoleForeignKey(database, getRole(), getRoleNamespace());
	final String permId = getPermissionForeignKey(database, getPermission(), getPermissionNamespace());
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
     * Get the permissionNamespace attribute on this object
     *
     * @return permissionNamespace value
     */
    public String getPermissionNamespace() {
        return this.permissionNamespace;
    }

    /**
     * Set the permissionNamespace attribute on this object
     *
     * @param permissionNamespace value to set
     */
    public void setPermissionNamespace(final String permissionNamespace) {
        this.permissionNamespace = permissionNamespace;
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
        return this.active == null ? "Y" : this.active;
    }

    /**
     * Set the active attribute on this object
     *
     * @param active value to set
     */
    public void setActive(final String active) {
        this.active = active;
    }

    public String getRoleNamespace() {
	return roleNamespace;
    }

    public void setRoleNamespace(String roleNamespace) {
	this.roleNamespace = roleNamespace;
    }
}
