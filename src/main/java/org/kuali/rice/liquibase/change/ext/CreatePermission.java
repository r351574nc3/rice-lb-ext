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

import liquibase.change.core.DeleteDataChange;
import liquibase.change.custom.CustomSqlChange;
import liquibase.change.custom.CustomSqlRollback;
import liquibase.database.Database;
import liquibase.exception.RollbackImpossibleException;
import liquibase.exception.UnsupportedChangeException;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.InsertStatement;

import java.math.BigInteger;
import java.util.UUID;

import static liquibase.ext.Constants.EXTENSION_PRIORITY;

/**
 * Custom Liquibase Refactoring for creating a KIM permission.
 *
 * @author Leo Przybylski
 */
public class CreatePermission extends RiceAbstractChange implements CustomSqlChange, CustomSqlRollback {

	private static final String SEQUENCE_NAME = "KRIM_PERM_ID_S";

	private String template;
	private String namespace;
	private String name;
	private String description;
	private String active;


	public CreatePermission() {
        super("CreatePermission", "Adding a Permission to KIM", EXTENSION_PRIORITY);
    }

    /**
     * Supports all databases
     */
    @Override
    public boolean supports(Database database) {
        return true;
    }

    /**
     * Generates the SQL statements required to run the change.
     *
     * @param database databasethe target {@link liquibase.database.Database} associated to this change's statements
     * @return an array of {@link String}s with the statements
     */
    public SqlStatement[] generateStatements(Database database) {
        final InsertStatement insertPermission = new InsertStatement(database.getDefaultSchemaName(), "krim_perm_t");
		final BigInteger permissionId = getPrimaryKey(database);
		final BigInteger templateId = getPermissionTemplateForeignKey(database,getTemplate());

		insertPermission.addColumnValue("perm_id", permissionId);
		insertPermission.addColumnValue("nmspc_cd", getNamespace());
		insertPermission.addColumnValue("nm", getName());
		insertPermission.addColumnValue("desc_txt", getDescription());
		insertPermission.addColumnValue("actv_ind", getActive());
		insertPermission.addColumnValue("perm_tmpl_id", templateId);
		insertPermission.addColumnValue("ver_nbr", 1);
		insertPermission.addColumnValue("obj_id", UUID.randomUUID().toString());

        return new SqlStatement[] {
            insertPermission
        };
    }

	@Override
	public SqlStatement[] generateRollbackStatements(Database database) throws UnsupportedChangeException, RollbackImpossibleException {
		BigInteger templateId = getPermissionTemplateForeignKey(database,getTemplate());

		final DeleteDataChange removePerm = new DeleteDataChange();
		removePerm.setTableName("krim_perm_t");
		removePerm.setWhereClause(String.format("nmspc_cd = '%s' AND nm = '%s' AND perm_tmpl_id = '%s'", getNamespace(), getName(), templateId));

		return removePerm.generateStatements(database);
	}

	@Override
	protected String getSequenceName() {
		return SEQUENCE_NAME;
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


}