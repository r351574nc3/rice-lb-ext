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

import liquibase.change.ChangeProperty;
import liquibase.change.core.DeleteDataChange;
import liquibase.change.custom.CustomSqlChange;
import liquibase.database.Database;
import liquibase.exception.RollbackImpossibleException;
import liquibase.exception.UnsupportedChangeException;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.InsertStatement;
import org.apache.commons.lang.StringUtils;

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
public class AssignRoleMember extends KimAbstractChange implements CustomSqlChange {

    private String namespace;
    private String type;
    private String member;
    private String memberNamespace;
    private String role;
	@ChangeProperty(includeInSerialization = false)
	private List<AddRoleMemberAttribute> attributes = new ArrayList<AddRoleMemberAttribute>();
	private String uniqueAttributeDefinitions;
	@ChangeProperty(includeInSerialization = false)
	private List<AddRoleResponsibilityAction> actions = new ArrayList<AddRoleResponsibilityAction>();
	   	        
    
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
		final String roleId = getRoleForeignKey(database, getRole(), getNamespace());
		String memberId = getMemberId(database);

		assignRole.addColumnValue("role_mbr_id", id);
		assignRole.addColumnValue("role_id", roleId);
		assignRole.addColumnValue("mbr_id", memberId);
		assignRole.addColumnValue("mbr_typ_cd", getType());
		assignRole.addColumnValue("ver_nbr", 1);
		assignRole.addColumnValue("obj_id", UUID.randomUUID().toString());

		List<SqlStatement> results = new ArrayList<SqlStatement>();
		results.add(assignRole);
		for (AddRoleMemberAttribute addRoleMemberAttribute : attributes){
			addRoleMemberAttribute.setRoleMemberId(id.toString());
			results.addAll(Arrays.asList(addRoleMemberAttribute.generateStatements(database)));
		}
		for (AddRoleResponsibilityAction action : actions){
			action.setRoleMemberId(id.toString());
			results.addAll(Arrays.asList(action.generateStatements(database)));
		}
       return results.toArray(new SqlStatement[results.size()]);
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
	public SqlStatement[] generateRollbackStatements(Database database) throws UnsupportedChangeException, RollbackImpossibleException {
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