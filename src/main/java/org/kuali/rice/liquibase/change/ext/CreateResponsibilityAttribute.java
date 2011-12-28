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
public class CreateResponsibilityAttribute extends AbstractChange {
    private String value;
    private String attributeDef;
    private String responsibility;
    private String type;
    private String active;
    
    
    public CreateResponsibilityAttribute() {
        super("CreateResponsibilityAttribute", "Adding an attribute to a responsibility to KIM", EXTENSION_PRIORITY);
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
        final InsertStatement insertAttribute = new InsertStatement(database.getDefaultSchemaName(),
                                                                         "krim_rsp_attr_data_t");
        final SqlStatement getAttributeId = new RuntimeStatement() {
                public Sql[] generate(Database database) {
                    return new Sql[] {
                        new UnparsedSql("insert into krim_rsp_rqrd_attr_id_s values(null);"),
                        new UnparsedSql("select max(id) from krim_rsp_rqrd_attr_id_s;")
                    };
                }
            };

         final SqlStatement getResponsibilityId = new RuntimeStatement() {
                public Sql[] generate(Database database) {
                    return new Sql[] {
                        new UnparsedSql(String.format("select rsp_id from krim_rsp_t where nm = '%s'", getResponsibility()))
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

        final SqlStatement getDefinitionId = new RuntimeStatement() {
                public Sql[] generate(Database database) {
                    return new Sql[] {
                        new UnparsedSql(String.format("select KIM_ATTR_DEFN_ID from krim_attr_defn_t where nm = '%s'", getAttributeDef()))
                    };
                }
            };

        try {
            final BigInteger attributeId      = (BigInteger) ExecutorService.getInstance().getExecutor(database).queryForObject(getAttributeId, BigInteger.class);
            final BigInteger responsibilityId = (BigInteger) ExecutorService.getInstance().getExecutor(database).queryForObject(getResponsibilityId, BigInteger.class);
            final BigInteger typeId           = (BigInteger) ExecutorService.getInstance().getExecutor(database).queryForObject(getTypeId, BigInteger.class);
            final BigInteger definitionId     = (BigInteger) ExecutorService.getInstance().getExecutor(database).queryForObject(getDefinitionId, BigInteger.class);
            
            insertAttribute.addColumnValue("attr_data_id", attributeId);
            insertAttribute.addColumnValue("kim_attr_defn_id", definitionId);
            insertAttribute.addColumnValue("rsp_id", responsibilityId);
            insertAttribute.addColumnValue("actv_ind", getActive());
            insertAttribute.addColumnValue("kim_typ_id", typeId);
            insertAttribute.addColumnValue("attr_val", getValue());
            insertAttribute.addColumnValue("ver_nbr", 1);
            insertAttribute.addColumnValue("obj_id", "sys_guid()");
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new SqlStatement[] {
            insertAttribute
        };
    }


    /**
     * Used for rollbacks. Defines the steps/{@link Change}s necessary to rollback.
     * 
     * @return {@link Array} of {@link Change} instances
     */
    protected Change[] createInverses() {
        final DeleteDataChange undoAssign = new DeleteDataChange();
        final String respId = String.format("(select rsp_id from krim_rsp_t where nm = '%s')", getResponsibility());
        final String typeId = String.format("(select kim_typ_id from krim_typ_t where nm = '%s')", getType());
        final String defnId = String.format("(select KIM_ATTR_DEFN_ID from krim_attr_defn_t where nm = '%s')", getAttributeDef());
        undoAssign.setTableName("krim_rsp_attr_data_t");
        undoAssign.setWhereClause(String.format("rsp_id in %s and kim_typ_id in %s and kim_attr_defn_id in %s", respId, typeId, defnId));

        return new Change[] {
            undoAssign
        };
    }
    
    /**
     * @return Confirmation message to be displayed after the change is executed
     */
    public String getConfirmationMessage() {
        return "";
    }

    /**
     * Get the responsibility attribute on this object
     *
     * @return responsibility value
     */
    public String getResponsibility() {
        return this.responsibility;
    }

    /**
     * Set the responsibility attribute on this object
     *
     * @param responsibility value to set
     */
    public void setResponsibility(final String responsibility) {
        this.responsibility = responsibility;
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
    public void setActive(final String active) {
        this.active = active;
    }

}