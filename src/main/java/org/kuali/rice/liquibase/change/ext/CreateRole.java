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

import java.math.BigInteger;
import java.util.UUID;

import liquibase.change.custom.CustomSqlChange;
import liquibase.database.Database;
import liquibase.exception.RollbackImpossibleException;
import liquibase.exception.UnsupportedChangeException;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.InsertStatement;

import liquibase.change.core.DeleteDataChange;

import static liquibase.ext.Constants.EXTENSION_PRIORITY;

/**
 * Custom Liquibase Refactorign for adding a Role to KIM
 *
 * @author Leo Przybylski
 */
public class CreateRole extends KimAbstractChange implements CustomSqlChange {

	private String name;
	private String namespace;
	private String description;
	private String type;
    private String lastUpdated;
    private String active;
    
    
    public CreateRole() {
        super("CreateRole", "Adding a Role to KIM", EXTENSION_PRIORITY);
    }

	@Override
	protected String getSequenceName() {
		return "krim_role_id_s";
	}

	/**
     * Generates the SQL statements required to run the change.
     *
     * @param database databasethe target {@link liquibase.database.Database} associated to this change's statements
     * @return an array of {@link String}s with the statements
     */
	public SqlStatement[] generateStatements(Database database) {
		final InsertStatement insertRole = new InsertStatement(database.getDefaultSchemaName(), "KRIM_ROLE_T");
		final BigInteger roleId = getPrimaryKey(database);
		final BigInteger typeId = getTypeForeignKey(database, getType());

		insertRole.addColumnValue("role_id", roleId);
		insertRole.addColumnValue("nmspc_cd", getNamespace());
		insertRole.addColumnValue("role_nm", getName());
		insertRole.addColumnValue("actv_ind", getActive());
		insertRole.addColumnValue("kim_typ_id", typeId);
		insertRole.addColumnValue("ver_nbr", 1);
		insertRole.addColumnValue("desc_txt", getDescription());
		if (getLastUpdated() != null) {
			insertRole.addColumnValue("LAST_UPDT_DT", getLastUpdated());
		}
		insertRole.addColumnValue("obj_id", UUID.randomUUID().toString());

		return new SqlStatement[]{
			insertRole
		};
	}

	@Override
	public SqlStatement[] generateRollbackStatements(Database database) throws UnsupportedChangeException, RollbackImpossibleException {
		final BigInteger typeReference = getTypeForeignKey(database, getType());
		final DeleteDataChange removeRole = new DeleteDataChange();
		removeRole.setTableName("KRIM_ROLE_T");
		removeRole.setWhereClause(String.format("role_nm = '%s' and kim_typ_id = '%s'", getName(), typeReference));
		return removeRole.generateStatements(database);
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
     * Get the lastUpdated attribute on this object
     *
     * @return lastUpdated value
     */
    public String getLastUpdated() {
        return this.lastUpdated;
    }

    /**
     * Set the lastUpdated attribute on this object
     *
     * @param lastUpdated value to set
     */
    public void setLastUpdated(final String lastUpdated) {
        this.lastUpdated = lastUpdated;
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