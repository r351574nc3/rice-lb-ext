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
package liquibase.change.ext;

import java.math.BigInteger;
import java.util.UUID;

import liquibase.change.ChangeProperty;
import liquibase.change.custom.CustomSqlChange;
import liquibase.database.Database;
import liquibase.exception.RollbackImpossibleException;
import liquibase.exception.UnsupportedChangeException;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.InsertStatement;

import liquibase.change.core.DeleteDataChange;

import static liquibase.ext.Constants.EXTENSION_PRIORITY;

/**
 * Custom Liquibase Refactoring for adding an attribute to a responsibility in KIM.
 *
 * @author Leo Przybylski
 */
public class AddResponsibilityAttribute extends KimAbstractChange implements CustomSqlChange {
    private String value;
    private String attributeDef;
    private String responsibility;
    private String type;
	@ChangeProperty(includeInSerialization = false)
	private String responsibilityId;


	public AddResponsibilityAttribute() {
        super("responsibilityAttribute", "Adding an attribute to a responsibility to KIM", EXTENSION_PRIORITY);
    }

	@Override
	protected String getSequenceName() {
		return "krim_rsp_rqrd_attr_id_s";
	}

	/**
     * Generates the SQL statements required to run the change.
     *
     * @param database databasethe target {@link liquibase.database.Database} associated to this change's statements
     * @return an array of {@link String}s with the statements
     */
	public SqlStatement[] generateStatements(Database database) {
		final InsertStatement insertAttribute = new InsertStatement(database.getDefaultSchemaName(), "krim_rsp_attr_data_t");

		final BigInteger attributeId = getPrimaryKey(database);
		if (responsibilityId == null){
			responsibilityId = getResponsibilityForeignKey(database, getResponsibility());
		}
		final String typeId = getTypeForeignKey(database, getType());
		final String attributeDefintionId = getAttributeDefinitionForeignKey(database, getAttributeDef());

		insertAttribute.addColumnValue("attr_data_id", attributeId);
		insertAttribute.addColumnValue("rsp_id", responsibilityId);
		insertAttribute.addColumnValue("kim_typ_id", typeId);
		insertAttribute.addColumnValue("kim_attr_defn_id", attributeDefintionId);
		insertAttribute.addColumnValue("attr_val", getValue());
		insertAttribute.addColumnValue("ver_nbr", 1);
		insertAttribute.addColumnValue("obj_id", UUID.randomUUID().toString());

		return new SqlStatement[]{
			insertAttribute
		};
	}


	@Override
	public SqlStatement[] generateRollbackStatements(Database database) throws UnsupportedChangeException, RollbackImpossibleException {
		final DeleteDataChange undoAssign = new DeleteDataChange();
		if (responsibilityId == null){
			responsibilityId = getResponsibilityForeignKey(database, getResponsibility());
		}
		String typeId = getTypeForeignKey(database, getType());
		String defnId = getAttributeDefinitionForeignKey(database, getAttributeDef());
		undoAssign.setTableName("krim_rsp_attr_data_t");
		undoAssign.setWhereClause(String.format("rsp_id = '%s' and kim_typ_id = '%s' and kim_attr_defn_id = '%s'", responsibilityId, typeId, defnId));

		return undoAssign.generateStatements(database);
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

	public void setResponsibilityId(String responsibilityId) {
		this.responsibilityId = responsibilityId;
	}
}