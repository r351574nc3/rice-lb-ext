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
import liquibase.change.custom.CustomSqlRollback;
import liquibase.database.Database;
import liquibase.exception.RollbackImpossibleException;
import liquibase.exception.CustomChangeException;
import liquibase.exception.ValidationErrors;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.InsertStatement;
import org.apache.commons.lang.StringUtils;

import liquibase.ext.kualigan.statement.CreatePermissionStatement;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static liquibase.ext.Constants.EXTENSION_PRIORITY;

/**
 * Custom Liquibase Refactoring for creating a KIM permission.
 *
 * @author Leo Przybylski
 */
@DatabaseChange(name="CreatePermission", description = "Creates a KIM Permission record.", priority = EXTENSION_PRIORITY)
public class CreatePermission extends KimAbstractChange implements CustomSqlChange, CustomSqlRollback {

    protected String template;
    protected String namespace;
    protected String name;
    protected String description;
    protected String active = "Y";
    protected List<AddPermissionAttribute> attributes = new ArrayList<AddPermissionAttribute>();


    public CreatePermission() {
        super("permission", "Adding a Permission to KIM", EXTENSION_PRIORITY);
    }

    /**
     * Generates the SQL statements required to run the change.
     *
     * @param database databasethe target {@link liquibase.database.Database} associated to this change's statements
     * @return an array of {@link String}s with the statements
     */
    public SqlStatement[] generateStatements(final Database database) {

        final List<SqlStatement> attributeStatements = new ArrayList<SqlStatement>();
        
        for (final AddPermissionAttribute attribute : getAttributes()) {
            for (final SqlStatement statement : attribute.generateStatements(database)) {
                attributeStatements.add(statement);
            }
        }

        return new SqlStatement[] { new CreatePermissionStatement(getTemplate(),
								  getNamespace(),
								  getName(),
								  getDescription(),
								  getActive(), 
								  attributeStatements) };
    }

    @Override
    public SqlStatement[] generateRollbackStatements(final Database database) throws RollbackImpossibleException {
	List<SqlStatement> result = new ArrayList<SqlStatement>();
	String permissionId = getPermissionForeignKey(database, getName(), getNamespace(), getTemplate());

	final DeleteDataChange removePerm = new DeleteDataChange();
	removePerm.setTableName("krim_perm_t");
	removePerm.setWhereClause(String.format("perm_id = '%s'",permissionId));

	for (AddPermissionAttribute attribute : attributes){
	    attribute.setPermissionId(permissionId);
	    result.addAll(Arrays.asList(attribute.generateRollbackStatements(database)));
	}

	result.addAll(Arrays.asList(removePerm.generateStatements(database)));
	return result.toArray(new SqlStatement[result.size()]);
    }

    @Override
    protected String getSequenceName() {
	return "KRIM_PERM_ID_S";
    }

    /**
     * Get the template attribute on this object
     *
     * @return template value
     */
    public String getTemplate() {
        return this.template;
    }

    /**
     * Set the template attribute on this object
     *
     * @param template value to set
     */
    public void setTemplate(final String template) {
        this.template = template;
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
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Get the description attribute on this object
     *
     * @return description value
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Set the description attribute on this object
     *
     * @param description value to set
     */
    public void setDescription(final String description) {
        this.description = description;
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
    public List<AddPermissionAttribute> getAttributes() {
        return this.attributes;
    }

    /**
     * Set the attributes attribute on this object
     *
     * @param attributes value to set
     */
    public void setAttributes(final List<AddPermissionAttribute> attributes) {
        this.attributes = attributes;
    }


    public AddPermissionAttribute createAttribute(){
	AddPermissionAttribute permissionAttribute = new AddPermissionAttribute();
	this.attributes.add(permissionAttribute);
	return permissionAttribute;
    }

}
