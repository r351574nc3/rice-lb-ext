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
 * Custom Liquibase Refactoring for adding actions to a role/responsibility assignment in KIM.
 *
 * @author Leo Przybylski
 */
public class AddRoleResponsibilityAction extends KimAbstractChange implements CustomSqlChange {
    private String role;
    private String responsibility;
    private String namespace;
    private String priority;
    private String force;
    private String actionTypeCode;
    private String actionPolicyCode;

    public AddRoleResponsibilityAction() {
        super("roleResponsibilityAction", "Adding an action to a role with a responsibility to KIM", EXTENSION_PRIORITY);
    }

	@Override
	protected String getSequenceName() {
		return "krim_role_rsp_actn_id_s";
	}

	/**
     * Generates the SQL statements required to run the change.
     *
     * @param database databasethe target {@link liquibase.database.Database} associated to this change's statements
     * @return an array of {@link String}s with the statements
     */
	public SqlStatement[] generateStatements(Database database) {
		final InsertStatement insertAction = new InsertStatement(database.getDefaultSchemaName(), "krim_role_rsp_actn_t");
		final BigInteger id = getPrimaryKey(database);
		final BigInteger roleId = getRoleForeignKey(database, getRole(), getNamespace());
		final BigInteger responsibilityId = getResponsibilityForeignKey(database, getResponsibility());
		final BigInteger roleRespId = getRoleResponsibilityForeignKey(database, roleId, responsibilityId);

		insertAction.addColumnValue("role_rsp_actn_id", id);
		insertAction.addColumnValue("actn_typ_cd", getActionTypeCode());
		insertAction.addColumnValue("actn_plcy_cd", getActionPolicyCode());
		insertAction.addColumnValue("frc_actn", getForce());
		insertAction.addColumnValue("role_rsp_id", roleRespId);
		insertAction.addColumnValue("priority_nbr", getPriority());
		insertAction.addColumnValue("role_mbr_id", "*");
		insertAction.addColumnValue("ver_nbr", 1);
		insertAction.addColumnValue("obj_id", UUID.randomUUID().toString());

		return new SqlStatement[]{
			insertAction
		};
	}


	@Override
	public SqlStatement[] generateRollbackStatements(Database database) throws UnsupportedChangeException, RollbackImpossibleException {
		final BigInteger roleId = getRoleForeignKey(database, getRole(), getNamespace());
		final BigInteger responsibilityId = getResponsibilityForeignKey(database, getResponsibility());
		final BigInteger assignId = getRoleResponsibilityForeignKey(database, roleId, responsibilityId);

		final DeleteDataChange undoAssign = new DeleteDataChange();
		undoAssign.setTableName("krim_role_rsp_actn_t");
		undoAssign.setWhereClause(String.format("role_rsp_id = '%s' and role_mbr_id = '*'", assignId));

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

}
