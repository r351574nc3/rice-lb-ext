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
import liquibase.database.Database;
import liquibase.exception.RollbackImpossibleException;
import liquibase.exception.UnexpectedLiquibaseException;
import liquibase.exception.CustomChangeException;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.InsertStatement;

import java.math.BigInteger;
import java.util.UUID;

import static liquibase.ext.Constants.EXTENSION_PRIORITY;

/**
 * Custom Liquibase refactoring for adding an attribute to a permission. Here's an example of the XML used to do this:
 * <code>
 * &lt;customChange class="org.liquibase.change.ext.AddPermissionAttribute"&gt;
 *   &lt;param name="permission"   value="Amend TA" /&gt;
 *   &lt;param name="namespace"    value="KFS-TEM" /&gt;
 *   &lt;param name="name"         value="Amend TA" /&gt;
 *   &lt;param name="type"         value="Document Type, Routing Node &amp; Field(s)" /&gt;
 *   &lt;param name="attributeDef" value="Button" /&gt;
 *   &lt;param name="value"        value="TA" /&gt;
 * &lt;/customChange&gt;
 * </code>
 *
 * @author Leo Przybylski
 */
@DatabaseChange(name="addRoleMemberAttribute", description = "Adds an attribute to a Role member.", priority = EXTENSION_PRIORITY)
public class AddRoleMemberAttribute extends KimAbstractChange {

    protected String type;
    protected String attributeDef;
    protected String roleName;
    protected String roleNamespace;
    protected String member;
    protected String value;
    protected String unique;
    protected String roleMemberId;

    public AddRoleMemberAttribute() {
        super("roleMemberAttribute", "Adding an attribute to a Role Member", EXTENSION_PRIORITY);
    }

    /**
     * Generates the SQL statements required to run the change.
     *
     * @param database databasethe target {@link liquibase.database.Database} associated to this change's statements
     * @return an array of {@link String}s with the statements
     */
    public SqlStatement[] generateStatements(final Database database) {
        final InsertStatement insertAttribute = new InsertStatement("", database.getDefaultSchemaName(), "krim_role_mbr_attr_data_t");
        try {
            final BigInteger attributeId = getPrimaryKey(database);
	    final String typeId = getTypeForeignKey(database, getType());
            final String definitionId = getAttributeDefinitionForeignKey(database, getAttributeDef());
	    if (roleMemberId == null){
		String roleId = getRoleForeignKey(database, getRoleName(), getRoleNamespace());
		final String memberId = getPrincipalForeignKey(database, getMember());
		roleMemberId = getRoleMemberForeignKey(database, roleId, memberId);
	    }

            insertAttribute.addColumnValue("attr_data_id", attributeId);
            insertAttribute.addColumnValue("role_mbr_id", roleMemberId);
            insertAttribute.addColumnValue("kim_typ_id", typeId);
            insertAttribute.addColumnValue("kim_attr_defn_id", definitionId);
            insertAttribute.addColumnValue("attr_val", getValue());
            insertAttribute.addColumnValue("ver_nbr", 1);
            insertAttribute.addColumnValue("obj_id", UUID.randomUUID().toString());
        }
        catch (Exception e) {
            throw new UnexpectedLiquibaseException(String.format("Unable to generate sql statements for 'Role Member Attribute' (role: %s, mbr: %s, val: %s)'",getRoleName(),
								 getMember(),getValue()), e);
        }

        return new SqlStatement[] {
            insertAttribute
        };
    }

    @Override
    protected String getSequenceName() {
	return "krim_attr_data_id_s";
    }


    @Override
    public SqlStatement[] generateRollbackStatements(Database database) throws RollbackImpossibleException {
	final DeleteDataChange removeAttribute = new DeleteDataChange();
	removeAttribute.setTableName("krim_role_mbr_attr_data_t");

	final String definitionId = getAttributeDefinitionForeignKey(database, getAttributeDef());
	if (roleMemberId == null){
	    String roleId = getRoleForeignKey(database, getRoleName(), getRoleNamespace());
	    String memberId = getPrincipalForeignKey(database, getMember());
	    roleMemberId = getRoleMemberForeignKey(database, roleId, memberId);
	}

	removeAttribute.setWhereClause(String.format("role_mbr_id = '%s' AND kim_attr_defn_id = '%s'", roleMemberId, definitionId));
	return removeAttribute.generateStatements(database);
    }

    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    public String getAttributeDef() {
	return attributeDef;
    }

    public void setAttributeDef(String attributeDef) {
	this.attributeDef = attributeDef;
    }

    public String getRoleName() {
	return roleName;
    }

    public void setRoleName(String roleName) {
	this.roleName = roleName;
    }

    public String getRoleNamespace() {
	return roleNamespace;
    }

    public void setRoleNamespace(String roleNamespace) {
	this.roleNamespace = roleNamespace;
    }

    public String getMember() {
	return member;
    }

    public void setMember(String member) {
	this.member = member;
    }

    public String getValue() {
	return value;
    }

    public void setValue(String value) {
	this.value = value;
    }

    public boolean isUnique() {
	return unique!=null?Boolean.valueOf(unique):false;
    }

    public void setUnique(String unique) {
	this.unique = unique;
    }

    public void setRoleMemberId(String roleMemberId) {
	this.roleMemberId = roleMemberId;
    }
}
