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


import liquibase.change.Change;
import liquibase.change.DatabaseChange;
import liquibase.change.DatabaseChangeProperty;
import liquibase.change.core.DeleteDataChange;
import liquibase.database.Database;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.InsertStatement;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static liquibase.ext.Constants.EXTENSION_PRIORITY;

/**
 * Custom Liquibase Refactoring for creating a KIM Type.
 *
 * @author Leo Przybylski
 */
@DatabaseChange(name="createKimType", description = "Creates a Rice Kim Type Record.", priority = EXTENSION_PRIORITY)
public class CreateType extends KimAbstractChange {

    protected String application;
    protected String namespace;
    protected String name;
    protected String service;
    protected String active = "Y";
    protected List<AssignKimTypeAttribute> attributes;
    protected String uniqueAttributeDefinitions;
    
    public CreateType() {
        super("type", "Adding a new KIM Type to KIM", EXTENSION_PRIORITY);
	setAttributes(new ArrayList<AssignKimTypeAttribute>());
    }

    @Override
    public final String getConfirmationMessage() {
	return String.format("Inserted KIM Type '%s' into namespace '%s' successfully.", getName(), getNamespace());
    }

    @Override
    protected String getSequenceName() {
	return "krim_typ_id_s";
    }

    /**
     * Generates the SQL statements required to run the change.
     *
     * @param database databasethe target {@link liquibase.database.Database} associated to this change's statements
     * @return an array of {@link String}s with the statements
     */
    public SqlStatement[] generateStatements(final Database database) {
	final InsertStatement insertType = new InsertStatement(null, database.getDefaultSchemaName(), "krim_typ_t");
	insertType.addColumnValue("kim_typ_id", getPrimaryKey(database));
	insertType.addColumnValue("nmspc_cd", getNamespace());
	insertType.addColumnValue("nm", getName());
	insertType.addColumnValue("srvc_nm", getService());
	insertType.addColumnValue("actv_ind", getActive());
	insertType.addColumnValue("ver_nbr", 1);
	insertType.addColumnValue("obj_id", UUID.randomUUID().toString());
		
	return new SqlStatement[]{
	    insertType
	};
    }

    /**
     * Used for rollbacks. Defines the steps/{@link liquibase.change.Change}s necessary to rollback.
     *
     * @return {@link Array} of {@link liquibase.change.Change} instances
     */
    protected Change[] createInverses() {
        final DeleteDataChange removeType = new DeleteDataChange();
        removeType.setTableName("krim_typ_t");
        removeType.setWhereClause(String.format("nmspc_cd = '%s' AND nm = '%s' AND srvc_nm = '%s'", getNamespace(), getName(), getService()));

        return new Change[] {
            removeType
        };
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
     * Get the service attribute on this object
     *
     * @return service value
     */
    public String getService() {
        return this.service;
    }

    /**
     * Set the service attribute on this object
     *
     * @param service value to set
     */
    public void setService(final String service) {
        this.service = service;
    }

    /**
     * Get the application attribute on this object
     *
     * @return application value
     */
    public String getApplication() {
        return this.application;
    }

    /**
     * Set the application attribute on this object
     *
     * @param application value to set
     */
    public void setApplication(final String application) {
        this.application = application;
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


    public String getUniqueAttributeDefinitions() {
	return uniqueAttributeDefinitions;
    }

    public void setUniqueAttributeDefinitions(final String uniqueAttributeDefinitions) {
	this.uniqueAttributeDefinitions = uniqueAttributeDefinitions;
    }

    public void setAttributes(final List<AssignKimTypeAttribute> attributes) {
	this.attributes = attributes;
    }

    public List<AssignKimTypeAttribute> getAttributes() {
	return this.attributes;
    }

    public AssignKimTypeAttribute createAttribute() {
	final AssignKimTypeAttribute assignKimTypeAttribute = new AssignKimTypeAttribute();
	this.getAttributes().add(assignKimTypeAttribute);
	return assignKimTypeAttribute;
    }
}
