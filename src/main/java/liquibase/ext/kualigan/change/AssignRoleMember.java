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

import liquibase.change.ChangeParameterMetaData;
import liquibase.change.DatabaseChange;
import liquibase.change.custom.CustomSqlChange;
import liquibase.database.Database;
import liquibase.ext.kualigan.statement.AssignRoleMemberStatement;
import liquibase.statement.SqlStatement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static liquibase.ext.Constants.EXTENSION_PRIORITY;

/**
 * Custom refactoring for adding a Role to KIM.
 *
 * @author Leo Przybylski
 */
@DatabaseChange(name = "roleMember", description = "Assigns a member to a role", priority = EXTENSION_PRIORITY)
public class AssignRoleMember extends KimAbstractChange implements CustomSqlChange {

	private String namespace;
	private String type;
	private String member;
	private String memberNamespace;
	private String role;
	private String active;
	private String uniqueAttributeDefinitions;
	private List<AddRoleMemberAttribute> attribute = new ArrayList<AddRoleMemberAttribute>();
	private List<AddRoleResponsibilityAction> action = new ArrayList<AddRoleResponsibilityAction>();


	@Override
	protected ChangeParameterMetaData createChangeParameterMetadata(String parameterName) {
		return super.createChangeParameterMetadata(parameterName);    //To change body of overridden methods use File | Settings | File Templates.
	}

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
		List<SqlStatement> statements = new ArrayList<SqlStatement>();
		statements.add(new AssignRoleMemberStatement(namespace,type, member,	memberNamespace, role,active));
		statements.addAll(generateRoleReponsibilityActionStatements(database));
		statements.addAll(generateAddRoleMemberAttributeStatements(database));
		return statements.toArray(new SqlStatement[statements.size()]);
	}

	private List<SqlStatement> generateAddRoleMemberAttributeStatements(Database database) {
		final List<SqlStatement> statements = new ArrayList<SqlStatement>();
		for (final AddRoleMemberAttribute addAttribute : getAttribute()) {
			addAttribute.setMemberFkSeq(getSequenceName());
			statements.addAll(Arrays.asList(addAttribute.generateStatements(database)));
		}
		return statements;
	}

	private List<SqlStatement> generateRoleReponsibilityActionStatements(Database database) {
		final List<SqlStatement> statements = new ArrayList<SqlStatement>();
		for (final AddRoleResponsibilityAction addRoleResponsibilityAction : getAction()) {
			addRoleResponsibilityAction.setMemberFkSeq(getSequenceName());
			statements.addAll(Arrays.asList(addRoleResponsibilityAction.generateStatements(database)));
		}
		return statements;
	}


	@Override
	public boolean supportsRollback(Database database) {
		return false;
	}
//
//	@Override
//    public SqlStatement[] generateRollbackStatements(Database database) throws RollbackImpossibleException {
//        final DeleteDataChange undoAssign = new DeleteDataChange();
//        final String roleId = getRoleForeignKey(database, getRole(),getNamespace());
//        String memberId = getMemberId(database);
//        List<String> uniqueAttributeValues = null;
//        if (StringUtils.isNotBlank(uniqueAttributeDefinitions)){
//            uniqueAttributeValues = getUniqueAttributeValues(attributes, uniqueAttributeDefinitions);
//        }
//        final String rolMemberId = getRoleMemberForeignKey(database,roleId,memberId,uniqueAttributeValues);
//        undoAssign.setTableName("KRIM_ROLE_MBR_T");
//        undoAssign.setWhereClause(String.format("role_id = '%s' and mbr_id = '%s'", roleId, memberId));
//
//        List<SqlStatement> results = new ArrayList<SqlStatement>();
//        for (AddRoleMemberAttribute addRoleMemberAttribute : attributes){
//            addRoleMemberAttribute.setMemberFkSeq(rolMemberId);
//            results.addAll(Arrays.asList(addRoleMemberAttribute.generateRollbackStatements(database)));
//        }
//        for (AddRoleResponsibilityAction action1 : action){
//            action1.setMemberFkSeq(rolMemberId);
//            results.addAll(Arrays.asList(action1.generateRollbackStatements(database)));
//        }
//        results.addAll(Arrays.asList(undoAssign.generateStatements(database)));
//        return results.toArray(new SqlStatement[results.size()]);
//    }

//    private List<String> getUniqueAttributeValues(List<AddRoleMemberAttribute> attributes, String uniqueAttributeDefinitions) {
//        List<String> uniqueTokens = Arrays.asList(uniqueAttributeDefinitions.split(","));
//        List<String> result = new ArrayList<String>();
//        for (AddRoleMemberAttribute addRoleMemberAttribute : attributes){
//            if (uniqueTokens.contains(addRoleMemberAttribute.getAttributeDef())){
//                result.add(addRoleMemberAttribute.getValue());
//            }
//            else{
//                throw new IllegalArgumentException(String.format("Attribute definition '%s' defined as unique but not part or attributes list", addRoleMemberAttribute.getAttributeDef()));
//            }
//        }
//        if (uniqueTokens.size() > result.size()){
//            throw new IllegalArgumentException(String.format("Unique Attribute definitions do not match attributes defined '%s<%s'",result.size(),uniqueTokens.size()));
//        }
//        return result;
//    }

//	private String getMemberId(Database database) {
//		String memberId;
//		if ("P".equals(type)) {
//			memberId = getPrincipalForeignKey(database, member);
//		} else if ("R".equals(type)) {
//			memberId = getRoleForeignKey(database, member, memberNamespace != null ? memberNamespace : namespace);
//		} else {
//			throw new RuntimeException(String.format("Role type '%s' not supported!", type));
//		}
//		return memberId;
//	}


//	@DatabaseChangeProperty(serializationType = SerializationType.NESTED_OBJECT, isChangeProperty = true)
	public List<AddRoleMemberAttribute> getAttribute() {
		return this.attribute;
	}

	public void setAttribute(final List<AddRoleMemberAttribute> attributes) {
		this.attribute = attributes;
	}

	public void setAction(final List<AddRoleResponsibilityAction> actions) {
		this.action = actions;
	}

//	@DatabaseChangeProperty(serializationType = SerializationType.NESTED_OBJECT, isChangeProperty = true)
	public List<AddRoleResponsibilityAction> getAction() {
		return this.action;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMember() {
		return member;
	}

	public void setMember(String member) {
		this.member = member;
	}

	public String getMemberNamespace() {
		return memberNamespace;
	}

	public void setMemberNamespace(String memberNamespace) {
		this.memberNamespace = memberNamespace;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}


}
