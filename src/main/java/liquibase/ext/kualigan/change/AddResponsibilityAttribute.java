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

import java.math.BigInteger;
import java.util.UUID;

import liquibase.change.Change;
import liquibase.change.DatabaseChange;
import liquibase.change.DatabaseChangeProperty;
import liquibase.change.custom.CustomSqlChange;
import liquibase.database.Database;
import liquibase.exception.RollbackImpossibleException;
import liquibase.exception.CustomChangeException;
import liquibase.ext.kualigan.statement.AddResponsibilityAttributeStatement;
import liquibase.ext.kualigan.statement.CreatePermissionStatement;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.InsertStatement;

import liquibase.change.core.DeleteDataChange;

import static liquibase.ext.Constants.EXTENSION_PRIORITY;

/**
 * Custom Liquibase Refactoring for adding an attribute to a responsibility in KIM.
 *
 * @author Leo Przybylski
 */
//todo: change bind name
@DatabaseChange(name="responsibilityAttribute", description = "Adds an Attribute to a Responsibility", priority = EXTENSION_PRIORITY)
public class AddResponsibilityAttribute extends KimAbstractChange implements CustomSqlChange {

	private String type;
	private String attributeDef;
	private String value;
	private String responsibility;
	//todo: add responsibility namespace
	private String responsibilityFkSeq;

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
    public SqlStatement[] generateStatements(final Database database) {
	    AddResponsibilityAttributeStatement addResponsibilityAttributeStatement = new AddResponsibilityAttributeStatement(getValue(),
					    getAttributeDef(),
					    getType(),
					    getResponsibility());
	    addResponsibilityAttributeStatement.setResponsibilityFkSeq(responsibilityFkSeq);
	    return new SqlStatement[]{addResponsibilityAttributeStatement};
    }


    @Override
    public SqlStatement[] generateRollbackStatements(final Database database) throws RollbackImpossibleException {
	    final DeleteDataChange undoAssign = new DeleteDataChange();
        final String responsibilityId = getResponsibilityFkSeq();
	    final String typeId = getTypeForeignKey(database, getType());
	    final String defnId = getAttributeDefinitionForeignKey(database, getAttributeDef());
	    undoAssign.setTableName("krim_rsp_attr_data_t");
	    undoAssign.setWhereClause(String.format("rsp_id = '%s' and kim_typ_id = '%s' and kim_attr_defn_id = '%s'", responsibilityId, typeId, defnId));
	    return undoAssign.generateStatements(database);
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

	public String getResponsibility() {
		return responsibility;
	}

	public void setResponsibility(String responsibility) {
		this.responsibility = responsibility;
	}

	public String getResponsibilityFkSeq() {
		return responsibilityFkSeq;
	}

	public void setResponsibilityFkSeq(String responsibilityFkSeq) {
		this.responsibilityFkSeq = responsibilityFkSeq;
	}
}
