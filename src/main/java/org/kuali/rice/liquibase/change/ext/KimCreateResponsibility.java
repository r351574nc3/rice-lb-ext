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
 * Custom Liquibase Refactoring for creating a KIM Responsibility.
 *
 * @author Leo Przybylski
 */
public class KimCreateResponsibility extends AbstractChange implements CustomSqlChange {
    private String template;
    private String namespace;
    private String name;
    private String active;
    
    
    public KimCreateResponsibility() {
        super("KimCreateResponsiblity", "Adding a Responsibility to KIM", EXTENSION_PRIORITY);
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
        final InsertStatement insertResponsibility = new InsertStatement(database.getDefaultSchemaName(),
                                                                         "krim_rsp_t");
        final SqlStatement getResponsibilityId = new RuntimeStatement() {
                public Sql[] generate(Database database) {
                    return new Sql[] {
                        new UnparsedSql("insert into krim_rsp_id_s values(null);"),
                        new UnparsedSql("select max(id) from krim_rsp_id_s;")
                    };
                }
            };

       final SqlStatement getTemplateId = new RuntimeStatement() {
                public Sql[] generate(Database database) {
                    return new Sql[] {
                        new UnparsedSql(String.format("select RSP_TMPL_ID from KRIM_RSP_TMPL_T where nm = '%s' and NMSPC_CD = '%s'", getTemplate(), getNamespace()))
                    };
                }
            };

        try {
            final BigInteger responsibilityId = (BigInteger) ExecutorService.getInstance().getExecutor(database).queryForObject(getResponsibilityId, BigInteger.class);
            final BigInteger templateId       = (BigInteger) ExecutorService.getInstance().getExecutor(database).queryForObject(getTemplateId, BigInteger.class);
            
            insertResponsibility.addColumnValue("rsp_id", responsibilityId);
            insertResponsibility.addColumnValue("nmspc_cd", getNamespace());
            insertResponsibility.addColumnValue("nm", getName());
            insertResponsibility.addColumnValue("actv_ind", getActive());
            insertResponsibility.addColumnValue("rsp_tmpl_id", 1);
            insertResponsibility.addColumnValue("ver_nbr", 1);
            insertResponsibility.addColumnValue("obj_id", "sys_guid()");
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new SqlStatement[] {
            insertResponsibility
        };
    }


    /**
     * Used for rollbacks. Defines the steps/{@link Change}s necessary to rollback.
     * 
     * @return {@link Array} of {@link Change} instances
     */
    protected Change[] createInverses() {
        final DeleteDataChange removeResponsibility = new DeleteDataChange();
        final String templateId = String.format("(select RSP_TMPL_ID from KRIM_RSP_TMPL_T where nm = '%s' and NMSPC_CD = '%s')", getTemplate(), getNamespace());
        removeResponsibility.setTableName("krim_rsp_t");
        removeResponsibility.setWhereClause(String.format("nm = '%s' and NMSPC_CD = '%s' and RSP_TMPL_ID in %s", getName(), getNamespace(), templateId));

        return new Change[] {
            removeResponsibility
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

    public void setUp() throws SetupException {
    }

    public void setFileOpener(final ResourceAccessor resourceAccessor) {    
        setResourceAccessor(resourceAccessor);
    }    
}