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
import liquibase.change.custom.CustomSqlChange;
import liquibase.database.Database;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.executor.ExecutorService;
import liquibase.resource.ResourceAccessor;
import liquibase.sql.Sql;
import liquibase.sql.UnparsedSql;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.InsertStatement;
import liquibase.statement.core.RuntimeStatement;

import liquibase.change.core.DeleteDataChange;

import static liquibase.ext.Constants.EXTENSION_PRIORITY;

/**
 * Custom Liquibase Refactoring for adding actions to a role/responsibility assignment in KIM.
 *
 * @author Leo Przybylski
 */
public class CreateRoleResponsibilityAction extends AbstractChange implements CustomSqlChange {
    private String role;
    private String responsibility;
    private String namespace;
    private String priority;
    private String force;
    private String actionTypeCode;
    private String actionPolicyCode;
    
    
    public CreateRoleResponsibilityAction() {
        super("CreateRoleResponsibilityAction", "Adding an action to a role with a responsibility to KIM", EXTENSION_PRIORITY);
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
        final InsertStatement insertAction = new InsertStatement(database.getDefaultSchemaName(),
                                                                         "krim_role_rsp_actn_t");
        final SqlStatement getActionId = new RuntimeStatement() {
                public Sql[] generate(Database database) {
                    return new Sql[] {
                        new UnparsedSql("insert into krim_role_rsp_actn_id_s values(null);"),
                        new UnparsedSql("select max(id) from krim_role_rsp_actn_id_s;")
                    };
                }
            };

        final SqlStatement getRoleId = new RuntimeStatement() {
                public Sql[] generate(Database database) {
                    return new Sql[] {
                        new UnparsedSql(String.format("select role_id from krim_role_t where role_nm = '%s' and nmspc_cd = '%s'", getRole(), getNamespace()))
                    };
                }
            };

        try {
            final BigInteger roleId = (BigInteger) ExecutorService.getInstance().getExecutor(database).queryForObject(getRoleId, BigInteger.class);
            
            final SqlStatement getResponsibilityId = new RuntimeStatement() {
                    public Sql[] generate(Database database) {
                        return new Sql[] {
                            new UnparsedSql(String.format("select rsp_id from krim_rsp_t where nm = '%s' and nmspc_cd = '%s'", getResponsibility(), getNamespace()))
                        };
                    }
                };
            final BigInteger responsibilityId = (BigInteger) ExecutorService.getInstance().getExecutor(database).queryForObject(getResponsibilityId, BigInteger.class);
            
            final SqlStatement getRoleRespId = new RuntimeStatement() {
                    public Sql[] generate(Database database) {
                        return new Sql[] {
                            new UnparsedSql(String.format("select role_rsp_id from krim_role_rsp_t where role_id = '%s' and rsp_id = '%s'", roleId, responsibilityId))
                        };
                    }
                };

            final BigInteger actionId   = (BigInteger) ExecutorService.getInstance().getExecutor(database).queryForObject(getActionId, BigInteger.class);
            final BigInteger roleRespId = (BigInteger) ExecutorService.getInstance().getExecutor(database).queryForObject(getRoleRespId, BigInteger.class);
            
            insertAction.addColumnValue("ROLE_RSP_ACTN_ID", actionId);
            insertAction.addColumnValue("actn_typ_cd", getActionTypeCode());
            insertAction.addColumnValue("actn_plcy_cd", getActionPolicyCode());
            insertAction.addColumnValue("force", getForce());
            insertAction.addColumnValue("role_rsp_id", roleRespId);
            insertAction.addColumnValue("ver_nbr", 1);
            insertAction.addColumnValue("role_mbr_id", "*");
            insertAction.addColumnValue("obj_id", "sys_guid()");
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new SqlStatement[] {
            insertAction
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
        final String respId = String.format("(select rsp_id from krim_rsp_t where nm = '%s' and nmspc_cd = '%s')", getResponsibility(), getNamespace());
        final String assignId = String.format("(select role_rsp_id from krim_role_rsp_t where role_id in '%s' and rsp_id in '%s')", roleId, respId);
        undoAssign.setTableName("krim_role_rsp_actn_t");
        undoAssign.setWhereClause(String.format("role_rsp_id in %s and mbr_id = '*'", assignId));

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
     * Get the priority attribute on this object
     *
     * @return priority value
     */
    public String getPriority() {
        return this.priority;
    }

    /**
     * Set the priority attribute on this object
     *
     * @param priority value to set
     */
    public void setPriority(final String priority) {
        this.priority = priority;
    }

    /**
     * Get the actionPolicyCode attribute on this object
     *
     * @return actionPolicyCode value
     */
    public String getActionPolicyCode() {
        return this.actionPolicyCode;
    }

    /**
     * Set the actionPolicyCode attribute on this object
     *
     * @param actionPolicyCode value to set
     */
    public void setActionPolicyCode(final String actionPolicyCode) {
        this.actionPolicyCode = actionPolicyCode;
    }

    /**
     * Get the force attribute on this object
     *
     * @return force value
     */
    public String getForce() {
        return this.force;
    }

    /**
     * Set the force attribute on this object
     *
     * @param force value to set
     */
    public void setForce(final String force) {
        this.force = force;
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
     * Get the actionTypeCode attribute on this object
     *
     * @return actionTypeCode value
     */
    public String getActionTypeCode() {
        return this.actionTypeCode;
    }

    /**
     * Set the actionTypeCode attribute on this object
     *
     * @param actionTypeCode value to set
     */
    public void setActionTypeCode(final String actionTypeCode) {
        this.actionTypeCode = actionTypeCode;
    }

    public void setFileOpener(final ResourceAccessor resourceAccessor) {    
        setResourceAccessor(resourceAccessor);
    }

    public void setUp() throws SetupException {
    }
}
