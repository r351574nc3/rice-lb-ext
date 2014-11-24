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
 * Custom refactoring for dropping the assignment of an attribute to a KIM Type.
 *
 * @author Leo Przybylski
 */
@DatabaseChange(name="dropKimTypeAttribute", description = "Drops the assignment of an attribute ot a KIM Type", priority = EXTENSION_PRIORITY)
public class DropKimTypeAttribute extends KimAbstractChange implements CustomSqlChange {

    protected String type;
    protected String attribute;
    protected String namespace;
    
    public DropKimTypeAttribute() {
        super("dropKimTypAttribute", "Assigning a KIM Type Attribute", EXTENSION_PRIORITY);
    }

    public DropKimTypeAttribute(final String namespace, final String type, final String attribute) {
        super("dropKimTypAttribute", "Assigning a KIM Type Attribute", EXTENSION_PRIORITY);
	this.type = type;
	this.namespace = namespace;
	this.attribute = attribute;
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
	final DeleteDataChange undoAssign = new DeleteDataChange();
	final String attributeId = getAttributeDefinitionForeignKey(database, getAttribute());
	final String typeId      = getTypeForeignKey(database, getType(), getNamespace());

	undoAssign.setTableName("KRIM_TYP_ATTR_T");
	undoAssign.setWhereClause(String.format("KIM_TYP_ID = '%s' and KIM_ATTR_DEFN_ID = '%s'"));

	final List<SqlStatement> results = new ArrayList<SqlStatement>();
	results.addAll(Arrays.asList(undoAssign.generateStatements(database)));
	return results.toArray(new SqlStatement[results.size()]);

    }

    /**
     * This action cannot be undone
     */
    @Override
    public SqlStatement[] generateRollbackStatements(Database database) throws RollbackImpossibleException {
	return new SqlStatement[] {};
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
}
