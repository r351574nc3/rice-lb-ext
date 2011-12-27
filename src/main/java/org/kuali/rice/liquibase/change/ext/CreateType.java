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

import liquibase.change.AbstractChange;
import liquibase.change.Change;
import liquibase.database.Database;
import liquibase.exception.ValidationErrors;
import liquibase.executor.ExecutorService;
import liquibase.sql.Sql;
import liquibase.sql.UnparsedSql;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.InsertStatement;
import liquibase.statement.core.RuntimeStatement;

import liquibase.change.core.DeleteDataChange;

import static liquibase.ext.Constants.EXTENSION_PRIORITY;

/**
 *
 * @author Leo Przybylski
 */
public class CreateType extends AbstractChange {
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

    /**
     *
     */
    @Override
    public ValidationErrors validate(Database database) {
        return super.validate(database);
    }

    /**
     * Generates the SQL statements required to run the change.
     *
     * @param database databasethe target {@link liquibase.database.Database} associated to this change's statements
     * @return an array of {@link String}s with the statements
     */
    public SqlStatement[] generateStatements(Database database) {
        final InsertStatement insertType = new InsertStatement(database.getDefaultSchemaName(), "krim_typ_t");
        final SqlStatement getId = new RuntimeStatement() {
                public Sql[] generate(Database database) {
                    return new Sql[] {
                        new UnparsedSql("insert into krim_typ_id_s values(null);"),
                        new UnparsedSql("select max(id) from krim_typ_id_s;")
                    };
                }
            };

        try {
            final BigInteger id = (BigInteger) ExecutorService.getInstance().getExecutor(database).queryForObject(getId, BigInteger.class);
            
            insertType.addColumnValue("kim_typ_id", id);
            insertType.addColumnValue("nmspc_cd", getNamespace());
            insertType.addColumnValue("nm", getName());
            insertType.addColumnValue("svc_nm", getServiceName());
            insertType.addColumnValue("actv_ind", getActive());
            insertType.addColumnValue("ver_nbr", 1);
            insertType.addColumnValue("obj_id", "sys_guid()");
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

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
        removeType.setWhereClause(String.format("nmspc_cd = '%s' AND nm = '%s' AND svc_nm = '%s'", getNamespace(), getName(), getServiceName()));

        return new Change[] {
            removeType
        };
    }
    
    /**
     * @return Confirmation message to be displayed after the change is executed
     */
    public String getConfirmationMessage() {
        return "";
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