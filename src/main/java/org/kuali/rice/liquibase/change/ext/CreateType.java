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

import liquibase.change.AbstractChange;
import liquibase.change.Change;
import liquibase.change.custom.CustomSqlChange;
import liquibase.database.Database;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.executor.ExecutorService;
import liquibase.resource.ResourceAccessor;
import liquibase.sql.Sql;
import liquibase.sql.UnparsedSql;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.InsertStatement;
import liquibase.statement.core.RuntimeStatement;

import liquibase.change.core.DeleteDataChange;

import static liquibase.ext.Constants.EXTENSION_PRIORITY;

/**
 * Custom Liquibase Refactoring for creating a KIM Type.
 *
 * @author Leo Przybylski
 */
public class CreateType extends RiceAbstractChange{
    private String namespace;
    private String name;
    private String serviceName;
    private String active;
    
    
    public CreateType() {
        super("CreateType", "Adding a new KIM Type to KIM", EXTENSION_PRIORITY);
    }
    
    /**
     * Supports all databases 
     */
    @Override
    public boolean supports(Database database) {
        return true;
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
    public SqlStatement[] generateStatements(Database database) {
        final InsertStatement insertType = new InsertStatement(database.getDefaultSchemaName(), "krim_typ_t");
            insertType.addColumnValue("kim_typ_id", getPrimaryKey(database));
            insertType.addColumnValue("nmspc_cd", getNamespace());
            insertType.addColumnValue("nm", getName());
            insertType.addColumnValue("srvc_nm", getServiceName());
            insertType.addColumnValue("actv_ind", getActive());
            insertType.addColumnValue("ver_nbr", 1);
            insertType.addColumnValue("obj_id", UUID.randomUUID().toString());
        return new SqlStatement[] {
            insertType
        };
    }


    /**
     * Used for rollbacks. Defines the steps/{@link Change}s necessary to rollback.
     * 
     * @return {@link Array} of {@link Change} instances
     */
    protected Change[] createInverses() {
        final DeleteDataChange removeType = new DeleteDataChange();
        removeType.setTableName("krim_typ_t");
        removeType.setWhereClause(String.format("nmspc_cd = '%s' AND nm = '%s' AND srvc_nm = '%s'", getNamespace(), getName(), getServiceName()));

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
     * Get the serviceName attribute on this object
     *
     * @return serviceName value
     */
    public String getServiceName() {
        return this.serviceName;
    }

    /**
     * Set the serviceName attribute on this object
     *
     * @param serviceName value to set
     */
    public void setServiceName(final String serviceName) {
        this.serviceName = serviceName;
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