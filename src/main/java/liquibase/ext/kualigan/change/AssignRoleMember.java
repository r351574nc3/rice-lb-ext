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
import org.apache.commons.lang.StringUtils;

import liquibase.ext.kualigan.statement.AssignMemberStatement;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static liquibase.ext.Constants.EXTENSION_PRIORITY;

/**
 * Custom refactoring for adding a Role to KIM.
 *
 * @author Leo Przybylski
 */
@DatabaseChange(name="assignRoleMember", description = "Assigns a member to a role", priority = EXTENSION_PRIORITY)
public class AssignRoleMember extends KimAbstractChange implements CustomSqlChange {

    protected String namespace;
    protected String type;
    protected String member;
    protected String memberNamespace;
    protected String role;
    protected String active;
    protected List<AddRoleMemberAttribute> attributes = new ArrayList<AddRoleMemberAttribute>();
    protected String uniqueAttributeDefinitions;
    protected List<AddRoleResponsibilityAction> actions = new ArrayList<AddRoleResponsibilityAction>();
                        
    
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
    public SqlStatement[] generateStatements(final Database database) {
        final List<SqlStatement> attributeStatements = new ArrayList<SqlStatement>();
        
        for (final AddRoleMemberAttribute member : getAttributes()) {
            for (final SqlStatement statement : member.generateStatements(database)) {
                attributeStatements.add(statement);
            }
        }

        final List<SqlStatement> actionStatements = new ArrayList<SqlStatement>();
        for (final AddRoleResponsibilityAction member : getActions()) {
            for (final SqlStatement statement : member.generateStatements(database)) {
                actionStatements.add(statement);
            }
        }

        return new SqlStatement[] { new AssignMemberStatement(getNamespace(),
                                                              getType(),
                                                              getMember(),
                                                              getMemberNamespace(),
                                                              getRole(),
                                                              getActive(),
                                                              attributeStatements,
                                                              actionStatements) };
    }

    private String getMemberId(Database database) {
        String memberId;
        if ("P".equals(getType())){
            memberId = getPrincipalForeignKey(database, getMember());
        }else if ("R".equals(getType())){
            memberId = getRoleForeignKey(database, getMember(), getMemberNamespace()!=null?getMemberNamespace():getNamespace());
        }
        else{
            throw new RuntimeException(String.format("Role type '%s' not supported!", getType()));
        }
        return memberId;
    }


    @Override
    public SqlStatement[] generateRollbackStatements(Database database) throws RollbackImpossibleException {
        final DeleteDataChange undoAssign = new DeleteDataChange();
        final String roleId = getRoleForeignKey(database, getRole(),getNamespace());
        String memberId = getMemberId(database);
        List<String> uniqueAttributeValues = null;
        if (StringUtils.isNotBlank(uniqueAttributeDefinitions)){
            uniqueAttributeValues = getUniqueAttributeValues(attributes, uniqueAttributeDefinitions);
        }
        final String rolMemberId = getRoleMemberForeignKey(database,roleId,memberId,uniqueAttributeValues);
        undoAssign.setTableName("KRIM_ROLE_MBR_T");
        undoAssign.setWhereClause(String.format("role_id = '%s' and mbr_id = '%s'", roleId, memberId));

        List<SqlStatement> results = new ArrayList<SqlStatement>();
        for (AddRoleMemberAttribute addRoleMemberAttribute : attributes){
            addRoleMemberAttribute.setRoleMemberId(rolMemberId);
            results.addAll(Arrays.asList(addRoleMemberAttribute.generateRollbackStatements(database)));
        }
        for (AddRoleResponsibilityAction action : actions){
            action.setRoleMemberId(rolMemberId);
            results.addAll(Arrays.asList(action.generateRollbackStatements(database)));
        }
        results.addAll(Arrays.asList(undoAssign.generateStatements(database)));
        return results.toArray(new SqlStatement[results.size()]);
    }

    private List<String> getUniqueAttributeValues(List<AddRoleMemberAttribute> attributes, String uniqueAttributeDefinitions) {
        List<String> uniqueTokens = Arrays.asList(uniqueAttributeDefinitions.split(","));
        List<String> result = new ArrayList<String>();
        for (AddRoleMemberAttribute addRoleMemberAttribute : attributes){
            if (uniqueTokens.contains(addRoleMemberAttribute.getAttributeDef())){
                result.add(addRoleMemberAttribute.getValue());
            }
            else{
                throw new IllegalArgumentException(String.format("Attribute definition '%s' defined as unique but not part or attributes list", addRoleMemberAttribute.getAttributeDef()));
            }
        }
        if (uniqueTokens.size() > result.size()){
            throw new IllegalArgumentException(String.format("Unique Attribute definitions do not match attributes defined '%s<%s'",result.size(),uniqueTokens.size()));
        }
        return result;
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

    /**
     * Get the attributes attribute on this object
     *
     * @return attributes value
     */
    public List<AddRoleMemberAttribute> getAttributes() {
        return this.attributes;
    }

    /**
     * Set the attributes attribute on this object
     *
     * @param attributes value to set
     */
    public void setAttributes(final List<AddRoleMemberAttribute> attributes) {
        this.attributes = attributes;
    }

    /**
     * Get the actions attribute on this object
     *
     * @return actions value
     */
    public List<AddRoleResponsibilityAction> getActions() {
        return this.actions;
    }

    /**
     * Set the actions attribute on this object
     *
     * @param actions value to set
     */
    public void setActions(final List<AddRoleResponsibilityAction> actions) {
        this.actions = actions;
    }


    public String getMemberNamespace() {
        return memberNamespace;
    }

    public void setMemberNamespace(String memberNamespace) {
        this.memberNamespace = memberNamespace;
    }

    public String getUniqueAttributeDefinitions() {
	return uniqueAttributeDefinitions;
    }

    public void setUniqueAttributeDefinitions(String uniqueAttributeDefinitions) {
	this.uniqueAttributeDefinitions = uniqueAttributeDefinitions;
    }

    public AddRoleMemberAttribute createAttribute(){
	AddRoleMemberAttribute addRoleMemberAttribute = new AddRoleMemberAttribute();
	this.attributes.add(addRoleMemberAttribute);
	return addRoleMemberAttribute;
    }

    public AddRoleResponsibilityAction createAction(){
	AddRoleResponsibilityAction addRoleResponsibilityAction = new AddRoleResponsibilityAction();
	this.actions.add(addRoleResponsibilityAction);
	return addRoleResponsibilityAction;
    }
}
