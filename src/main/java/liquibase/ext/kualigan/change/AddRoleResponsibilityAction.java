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
import liquibase.statement.SqlStatement;
import liquibase.statement.core.InsertStatement;
import org.apache.commons.lang.StringUtils;

import java.math.BigInteger;
import java.util.UUID;

import static liquibase.ext.Constants.EXTENSION_PRIORITY;

/**
 * Custom Liquibase Refactoring for adding actions to a role/responsibility assignment in KIM.
 *
 * @author Leo Przybylski
 */
@DatabaseChange(name="roleResponsibilityAction", description = "Adds an Action to a Role/Responsibility assignment", priority = EXTENSION_PRIORITY)
public class AddRoleResponsibilityAction extends KimAbstractChange implements CustomSqlChange {

    private String roleName;
    private String responsibilityName;
    private String roleNamespace;
    private String member;
    private String priority;
    private String force;
    private String actionTypeCode;
    private String actionPolicyCode;
    private String roleMemberId;

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
    public SqlStatement[] generateStatements(final Database database) {
	final InsertStatement insertAction = new InsertStatement(null, database.getDefaultSchemaName(), "krim_role_rsp_actn_t");
	final BigInteger id = getPrimaryKey(database);
	final String roleRespId = resolveRoleResponsibility(database);
	if (roleMemberId == null){
	    roleMemberId = resolveRoleMember(database);
	}

	insertAction.addColumnValue("role_rsp_actn_id", id);
	insertAction.addColumnValue("actn_typ_cd", getActionTypeCode());
	insertAction.addColumnValue("actn_plcy_cd", getActionPolicyCode());
	insertAction.addColumnValue("frc_actn", getForce());
	insertAction.addColumnValue("role_rsp_id", roleRespId);
	insertAction.addColumnValue("priority_nbr", getPriority());
	insertAction.addColumnValue("role_mbr_id", roleMemberId);
	insertAction.addColumnValue("ver_nbr", 1);
	insertAction.addColumnValue("obj_id", UUID.randomUUID().toString());

	return new SqlStatement[]{
	    insertAction
	};
    }

    @Override
    public SqlStatement[] generateRollbackStatements(final Database database) throws RollbackImpossibleException {
	final String roleRespId = resolveRoleResponsibility(database);
	if (roleMemberId == null){
	    roleMemberId = resolveRoleMember(database);
	}

	final DeleteDataChange undoAssign = new DeleteDataChange();
	undoAssign.setTableName("krim_role_rsp_actn_t");
	undoAssign.setWhereClause(String.format("role_rsp_id = '%s' and role_mbr_id = '%s'", roleRespId, roleMemberId));
	return undoAssign.generateStatements(database);
    }

    /**
     * Get the responsibility attribute on this object
     *
     * @return responsibility value
     */
    public String getResponsibilityName() {
        return this.responsibilityName;
    }

    /**
     * Set the responsibility attribute on this object
     *
     * @param responsibilityName value to set
     */
    public void setResponsibilityName(final String responsibilityName) {
        this.responsibilityName = responsibilityName;
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
    public String getRoleNamespace() {
        return this.roleNamespace;
    }

    /**
     * Set the namespace attribute on this object
     *
     * @param roleNamespace value to set
     */
    public void setRoleNamespace(final String roleNamespace) {
        this.roleNamespace = roleNamespace;
    }

    /**
     * Get the role attribute on this object
     *
     * @return role value
     */
    public String getRoleName() {
        return this.roleName;
    }

    /**
     * Set the role attribute on this object
     *
     * @param roleName value to set
     */
    public void setRoleName(final String roleName) {
        this.roleName = roleName;
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

    public String getMember() {
	return member;
    }

    public void setMember(String member) {
	this.member = member;
    }

    public void setRoleMemberId(String roleMemberId) {
	this.roleMemberId = roleMemberId;
    }


    private String resolveRoleMember(Database database) {
	if (StringUtils.isBlank(member)){
	    return "*";
	}
	final String roleId = getRoleForeignKey(database, roleName, roleNamespace);
	final String memberId = getPrincipalForeignKey(database, member);
	return getRoleMemberForeignKey(database, roleId, memberId);
    }

    private String resolveRoleResponsibility(Database database) {
	if (StringUtils.isBlank(responsibilityName) || "*".equals(responsibilityName)){
	    return "*";
	}
	String responsibilityId = getResponsibilityForeignKey(database, getResponsibilityName());
	final String roleId = getRoleForeignKey(database, roleName, roleNamespace);
	return getRoleResponsibilityForeignKey(database, roleId, responsibilityId);
    }
}
