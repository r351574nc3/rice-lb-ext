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
package org.kuali.rice.liquibase.change.ext;

import java.math.BigInteger;

import liquibase.change.AbstractChange;
import liquibase.change.Change;
import liquibase.database.Database;
import liquibase.exception.ValidationErrors;
import liquibase.executor.ExecutorService;
import liquibase.sql.Sql;
import liquibase.sql.UnparsedSql;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.InsertStatement;
import liquibase.statement.core.RuntimeStatement;

import liquibase.change.core.DeleteDataChange;

import static liquibase.ext.Constants.EXTENSION_PRIORITY;

/**
 * Custom Liquibase refactoring for adding a permission to a KIM role.
 *
 * @author Leo Przybylski
 */
public class AssignRolePermission extends AbstractChange {
    private String permission;
    private String namespace;
    private String role;
    private String active;
    
    
    public AssignRolePermission() {
        super("AssignRolePermission", "Assigning a KIM permission to a role", EXTENSION_PRIORITY);
    }
    
    /**
     * Supports all databases 
     */
    @Override
    public boolean supports(Database database) {
        return true;
    }

    /**
     *
     */
    @Override
    public ValidationErrors validate(Database database) {
        return super.validate(database);
    }

    /**
     * Generates the SQL statements required to run the change.
     *
     * @param database databasethe target {@link liquibase.database.Database} associated to this change's statements
     * @return an array of {@link String}s with the statements
     */
    public SqlStatement[] generateStatements(Database database) {
        final InsertStatement assignPermission = new InsertStatement(database.getDefaultSchemaName(),
                                                                     "krim_role_perm_t");
        final SqlStatement getId = new RuntimeStatement() {
                public Sql[] generate(Database database) {
                    return new Sql[] {
                        new UnparsedSql("insert into krim_role_perm_id_s values(null)"),
                        new UnparsedSql("select max(id) from krim_role_perm_id_s")
                    };
                }
            };


        final SqlStatement getRoleId = new RuntimeStatement() {
                public Sql[] generate(Database database) {
                    return new Sql[] {
                        new UnparsedSql(String.format("select ROLE_ID from KRIM_ROLE_T where ROLE_NM = '%s' and NMSPC_CD = '%s'", getRole(), getNamespace()))
                    };
                }
            };

        final SqlStatement getPermId = new RuntimeStatement() {
                public Sql[] generate(Database database) {
                    return new Sql[] {
                        new UnparsedSql(String.format("select PERM_ID from KRIM_PERM_T where nm = '%s' and NMSPC_CD = '%s'", getPermission(), getNamespace()))
                    };
                }
            };

        try {
            final BigInteger id     = (BigInteger) ExecutorService.getInstance().getExecutor(database).queryForObject(getId, BigInteger.class);
            final BigInteger roleId = (BigInteger) ExecutorService.getInstance().getExecutor(database).queryForObject(getRoleId, BigInteger.class);
            final BigInteger permId = (BigInteger) ExecutorService.getInstance().getExecutor(database).queryForObject(getPermId, BigInteger.class);
            
            assignPermission.addColumnValue("role_perm_id", id);
            assignPermission.addColumnValue("role_id", roleId);
            assignPermission.addColumnValue("perm_id", permId);
            assignPermission.addColumnValue("actv_ind", getActive());
            assignPermission.addColumnValue("ver_nbr", 1);
            assignPermission.addColumnValue("obj_id", "sys_guid()");
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new SqlStatement[] {
            assignPermission
        };
    }


    /**
     * Used for rollbacks. Defines the steps/{@link Change}s necessary to rollback.
     * 
     * @return {@link Array} of {@link Change} instances
     */
    protected Change[] createInverses() {
        final DeleteDataChange undoAssign = new DeleteDataChange();
        final String roleId = String.format("(select ROLE_ID from KRIM_ROLE_T where ROLE_NM = '%s' and NMSPC_CD = '%s')", getRole(), getNamespace());
        final String permId = String.format("(select PERM_ID from KRIM_PERM_T where nm = '%s' and NMSPC_CD = '%s')", getPermission(), getNamespace());
        undoAssign.setTableName("krim_role_perm_t");
        undoAssign.setWhereClause(String.format("role_id in %s and perm_id in %s", roleId, permId));

        return new Change[] {
            undoAssign
        };
    }
    
    /**
     * @return Confirmation message to be displayed after the change is executed
     */
    public String getConfirmationMessage() {
        return "";
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