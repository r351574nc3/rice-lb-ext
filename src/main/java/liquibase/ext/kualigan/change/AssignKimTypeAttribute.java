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
import liquibase.database.Database;
import liquibase.exception.RollbackImpossibleException;
import liquibase.exception.CustomChangeException;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.InsertStatement;
import org.apache.commons.lang.StringUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static liquibase.ext.Constants.EXTENSION_PRIORITY;

/**
 * Custom refactoring for adding a Role to KIM.
 *
 * @author Leo Przybylski
 */
@DatabaseChange(name="assignKimTypeAttribute", description = "Assigns an attribute to a KIM Type", priority = EXTENSION_PRIORITY)
public class AssignKimTypeAttribute extends KimAbstractChange implements CustomSqlChange {

    protected String namespace;
    protected String type;
    protected String attribute;
    protected String active;
	   	        
    
    public AssignKimTypeAttribute() {
        super("assignKimTypAttribute", "Assigning a KIM Type Attribute", EXTENSION_PRIORITY);
    }

    @Override
    protected String getSequenceName() {
        return "KRIM_TYP_ATTR_ID_S";
    }

    /**
     * Generates the SQL statements required to run the change.
     *
     * @param database databasethe target {@link liquibase.database.Database} associated to this change's statements
     * @return an array of {@link String}s with the statements
     */
    public SqlStatement[] generateStatements(final Database database) {
        final InsertStatement assignAttribute = new InsertStatement(null, database.getDefaultSchemaName(), "KRIM_TYP_ATTR_T");
        final BigInteger id      = getPrimaryKey(database);
        final String attributeId = getAttributeDefinitionForeignKey(database, getAttribute());
        final String typeId      = getTypeForeignKey(database, getType(), getNamespace());

        assignAttribute.addColumnValue("KIM_TYP_ATTR_ID", id);
        assignAttribute.addColumnValue("KIM_TYP_ID", typeId);
        assignAttribute.addColumnValue("KIM_ATTR_DEFN_ID", attributeId);
        assignAttribute.addColumnValue("ACTV_IND", getActive());
        assignAttribute.addColumnValue("ver_nbr", 1);
        assignAttribute.addColumnValue("obj_id", UUID.randomUUID().toString());

        final List<SqlStatement> results = new ArrayList<SqlStatement>();
        results.add(assignAttribute);
        return results.toArray(new SqlStatement[results.size()]);
    }

    @Override
    public SqlStatement[] generateRollbackStatements(final Database database) throws RollbackImpossibleException {
        final DropKimTypeAttribute undoAssign = new DropKimTypeAttribute(getNamespace(), getType(), getAttribute());
        final List<SqlStatement> results = new ArrayList<SqlStatement>();
        results.addAll(Arrays.asList(undoAssign.generateStatements(database)));
        return results.toArray(new SqlStatement[results.size()]);
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
     * Get the attribute attribute on this object
     *
     * @return attribute value
     */
    public String getAttribute() {
        return this.attribute;
    }

    /**
     * Set the attribute attribute on this object
     *
     * @param attribute value to set
     */
    public void setAttribute(final String attribute) {
        this.attribute = attribute;
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
