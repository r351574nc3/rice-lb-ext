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
 * Custom Liquibase Refactorign for adding a Role to KIM
 *
 * @author Leo Przybylski
 */
public class CreateRole extends AbstractChange implements CustomSqlChange {
    private String template;
    private String namespace;
    private String name;
    private String description;
    private String type;
    private String lastUpdated;
    private String active;
    
    
    public CreateRole() {
        super("CreateRole", "Adding a Role to KIM", EXTENSION_PRIORITY);
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
        final InsertStatement insertRole = new InsertStatement(database.getDefaultSchemaName(),
                                                                         "KRIM_ROLE_T");
        final SqlStatement getRoleId = new RuntimeStatement() {
                public Sql[] generate(Database database) {
                    return new Sql[] {
                        new UnparsedSql("insert into krim_role_id_s values(null);"),
                        new UnparsedSql("select max(id) from krim_role_id_s;")
                    };
                }
            };

       final SqlStatement getTypeId = new RuntimeStatement() {
                public Sql[] generate(Database database) {
                    return new Sql[] {
                        new UnparsedSql(String.format("select kim_typ_id from krim_typ_t where nm = '%s'", getType()))
                    };
                }
            };

        try {
            final BigInteger roleId = (BigInteger) ExecutorService.getInstance().getExecutor(database).queryForObject(getRoleId, BigInteger.class);
            final BigInteger typeId = (BigInteger) ExecutorService.getInstance().getExecutor(database).queryForObject(getTypeId, BigInteger.class);
            
            insertRole.addColumnValue("role_id", roleId);
            insertRole.addColumnValue("nmspc_cd", getNamespace());
            insertRole.addColumnValue("role_nm", getName());
            insertRole.addColumnValue("actv_ind", getActive());
            insertRole.addColumnValue("kim_typ_id", typeId);
            insertRole.addColumnValue("ver_nbr", 1);
            insertRole.addColumnValue("desc_text", getDescription());
            if (getLastUpdated() != null) {
                insertRole.addColumnValue("LAST_UPDT_DT", getLastUpdated());
            }
            insertRole.addColumnValue("obj_id", "sys_guid()");
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new SqlStatement[] {
            insertRole
        };
    }


    /**
     * Used for rollbacks. Defines the steps/{@link Change}s necessary to rollback.
     * 
     * @return {@link Array} of {@link Change} instances
     */
    protected Change[] createInverses() {
        final DeleteDataChange removeRole = new DeleteDataChange();
        final String typeId = String.format("(select kim_typ_id from krim_typ_t where nm = '%s')", getType());
        removeRole.setTableName("KRIM_ROLE_T");
        removeRole.setWhereClause(String.format("role_nm = '%s' and kim_typ_id in %s", getName(), typeId));

        return new Change[] {
            removeRole
        };
    }
    
    /**
     * @return Confirmation message to be displayed after the change is executed
     */
    public String getConfirmationMessage() {
        return "";
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

    public void setFileOpener(final ResourceAccessor resourceAccessor) {    
        setResourceAccessor(resourceAccessor);
    }

    public void setUp() throws SetupException {
    }
}