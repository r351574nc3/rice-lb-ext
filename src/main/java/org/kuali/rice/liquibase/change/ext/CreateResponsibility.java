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
import liquibase.database.Database;
import liquibase.exception.RollbackImpossibleException;
import liquibase.exception.UnsupportedChangeException;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.InsertStatement;

import java.math.BigInteger;
import java.util.UUID;

import static liquibase.ext.Constants.EXTENSION_PRIORITY;

/**
 * Custom Liquibase Refactoring for creating a KIM Responsibility.
 *
 * @author Leo Przybylski
 */
public class CreateResponsibility extends KimAbstractChange implements CustomSqlChange {
    private String template;
    private String namespace;
    private String name;
    private String active;
	private String description;
    
    
    public CreateResponsibility() {
        super("KimCreateResponsiblity", "Adding a Responsibility to KIM", EXTENSION_PRIORITY);
    }

	@Override
	protected String getSequenceName() {
		return "krim_rsp_id_s";
	}

	/**
     * Generates the SQL statements required to run the change.
     *
     * @param database databasethe target {@link liquibase.database.Database} associated to this change's statements
     * @return an array of {@link String}s with the statements
     */
    public SqlStatement[] generateStatements(Database database) {
		final InsertStatement insertResponsibility = new InsertStatement(database.getDefaultSchemaName(), "krim_rsp_t");
		final BigInteger responsibilityId = getPrimaryKey(database);
		BigInteger responsibilityTemplateId = null;
		if (getTemplate() != null){
			responsibilityTemplateId = getResponsibilityTemplateForeignKey(database, getTemplate(), getNamespace());
		}
		insertResponsibility.addColumnValue("rsp_id", responsibilityId);
		insertResponsibility.addColumnValue("rsp_tmpl_id", responsibilityTemplateId);
		insertResponsibility.addColumnValue("nm", getName());
		insertResponsibility.addColumnValue("nmspc_cd", getNamespace());
		insertResponsibility.addColumnValue("desc_txt", getDescription());
		insertResponsibility.addColumnValue("actv_ind", getActive());
		insertResponsibility.addColumnValue("ver_nbr", 1);
		insertResponsibility.addColumnValue("obj_id", UUID.randomUUID().toString());

        return new SqlStatement[] {
            insertResponsibility
        };
    }


	@Override
	public SqlStatement[] generateRollbackStatements(Database database) throws UnsupportedChangeException, RollbackImpossibleException {
		String responsibilityTemplateId = "null";
		if (getTemplate() != null){
			responsibilityTemplateId = new String("'" + getResponsibilityTemplateForeignKey(database, getTemplate(), getNamespace()) + "'");
		}
		final DeleteDataChange removeResponsibility = new DeleteDataChange();
		removeResponsibility.setTableName("krim_rsp_t");
		removeResponsibility.setWhereClause(String.format("nm = '%s' and NMSPC_CD = '%s' and RSP_TMPL_ID = %s", getName(), getNamespace(), responsibilityTemplateId));

		return removeResponsibility.generateStatements(database);
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}