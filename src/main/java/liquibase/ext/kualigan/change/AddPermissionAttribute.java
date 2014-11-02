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
//todo: change bind name to addPermissionAtrribute
@DatabaseChange(name="permissionAttribute", description = "Adds an attribute to a KIM permission", priority = EXTENSION_PRIORITY)
public class AddPermissionAttribute extends KimAbstractChange {

    protected String name;
    protected String value;
    protected String namespace;
    protected String attributeDef;
    protected String permission;
    protected String type;
    protected String active = "Y";
    protected String permissionId;

    public AddPermissionAttribute() {
        super("permissionAttribute", "Adding an attribute to a permission to KIM", EXTENSION_PRIORITY);
    }

    /**
     * Generates the SQL statements required to run the change.
     *
     * @param database databasethe target {@link liquibase.database.Database} associated to this change's statements
     * @return an array of {@link String}s with the statements
     */
    public SqlStatement[] generateStatements(final Database database) {
        final InsertStatement insertAttribute = new InsertStatement("", database.getDefaultSchemaName(), "krim_perm_attr_data_t");
	final String attrName = getAttributeDef() != null ? getAttributeDef() : getName();
        try {
            final BigInteger attributeId = getPrimaryKey(database);
	    if (permissionId == null){
		permissionId = getPermissionForeignKey(database, getPermission(), getNamespace());
	    }

	    final String typeId = getTypeForeignKey(database, getType());
            final String definitionId = getAttributeDefinitionForeignKey(database, attrName);

            insertAttribute.addColumnValue("attr_data_id", attributeId);
            insertAttribute.addColumnValue("perm_id", permissionId);
            insertAttribute.addColumnValue("kim_typ_id", typeId);
            insertAttribute.addColumnValue("kim_attr_defn_id", definitionId);
            insertAttribute.addColumnValue("attr_val", getValue());
            insertAttribute.addColumnValue("ver_nbr", 1);
            insertAttribute.addColumnValue("obj_id", UUID.randomUUID().toString());

	    return new SqlStatement[] {
		insertAttribute
	    };
        }
        catch (Exception e) {
            throw new UnexpectedLiquibaseException(String.format("Unable to generate sql statements for 'Permission Attribute' (perm: %s, name: %s, attr_def: %s)'", getPermission(), getValue(), attrName), e);
        }
    }

    @Override
    protected String getSequenceName() {
	return "KRIM_ATTR_DATA_ID_S";
    }


    @Override
    public SqlStatement[] generateRollbackStatements(final Database database) throws RollbackImpossibleException {
	final DeleteDataChange removeAttribute = new DeleteDataChange();
	removeAttribute.setTableName("krim_perm_attr_data_t");
	if (permissionId == null){
	    permissionId = getPermissionForeignKey(database, getPermission(), getNamespace());
	}
	final String typeId = getTypeForeignKey(database, getType());
	final String attrName = getAttributeDef() != null ? getAttributeDef() : getName();
	final String definitionId = getAttributeDefinitionForeignKey(database, attrName);

	removeAttribute.setWhereClause(String.format("perm_id = '%s' AND kim_typ_id = '%s' AND kim_attr_defn_id = '%s'",
						     permissionId, typeId, definitionId));
	return removeAttribute.generateStatements(database);
    }


    /**
     * Get the attributeDef attribute on this object
     *
     * @return attributeDef value
     */
    public String getAttributeDef() {
        return this.attributeDef;
    }

    /**
     * Set the attributeDef attribute on this object
     *
     * @param attributeDef value to set
     */
    public void setAttributeDef(final String attributeDef) {
        this.attributeDef = attributeDef;
    }


    /**
     * Get the name attribute on this object
     *
     * @return name value
     */
    public String getName() {
	return this.name;
    }

    /**
     * Set the name attribute on this object
     *
     * @param name value to set
     */
    @DatabaseChangeProperty
    public void setName(final String name) {
        this.name = name;
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
    @DatabaseChangeProperty
    public void setType(final String type) {
        this.type = type;
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
    @DatabaseChangeProperty
    public void setNamespace(final String namespace) {
        this.namespace = namespace;
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
    @DatabaseChangeProperty
    public void setPermission(final String permission) {
        this.permission = permission;
    }

    /**
     * Get the value attribute on this object
     *
     * @return value value
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Set the value attribute on this object
     *
     * @param value value to set
     */
    @DatabaseChangeProperty
    public void setValue(final String value) {
        this.value = value;
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
    @DatabaseChangeProperty
    public void setActive(final String active) {
        this.active = active;
    }

    public void setPermissionId(String permissionId) {
	this.permissionId = permissionId;
    }

}
