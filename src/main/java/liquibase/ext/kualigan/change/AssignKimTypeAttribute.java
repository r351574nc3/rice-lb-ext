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
import liquibase.change.custom.CustomSqlChange;
import liquibase.database.Database;
import liquibase.exception.RollbackImpossibleException;
import liquibase.ext.kualigan.statement.AssignKimTypeAttributeStatement;
import liquibase.statement.SqlStatement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static liquibase.ext.Constants.EXTENSION_PRIORITY;

/**
 * Custom refactoring for adding a Role to KIM.
 *
 * @author Leo Przybylski
 */
//todo: change bind name
@DatabaseChange(name = "assignKimTypeAttribute", description = "Assigns an attribute to a KIM Type", priority = EXTENSION_PRIORITY)
public class AssignKimTypeAttribute  extends KimAbstractChange implements CustomSqlChange {

	protected String kimTypeNamespace;
	protected String kimTypeName;
	protected String attributeDefinition;
	protected String active = "Y";


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
		return new SqlStatement[]{new AssignKimTypeAttributeStatement(
						kimTypeNamespace,
						kimTypeName,
						attributeDefinition,
						active)
		};
	}

	@Override
	public SqlStatement[] generateRollbackStatements(final Database database) throws RollbackImpossibleException {
		final DropKimTypeAttribute undoAssign = new DropKimTypeAttribute(getKimTypeNamespace(), getKimTypeName(), getAttributeDefinition());
		final List<SqlStatement> results = new ArrayList<SqlStatement>();
		results.addAll(Arrays.asList(undoAssign.generateStatements(database)));
		return results.toArray(new SqlStatement[results.size()]);
	}

	/**
	 * Get the namespace attribute on this object
	 *
	 * @return namespace value
	 */
	public String getKimTypeNamespace() {
		return this.kimTypeNamespace;
	}

	/**
	 * Set the namespace attribute on this object
	 *
	 * @param kimTypeNamespace value to set
	 */
	public void setKimTypeNamespace(final String kimTypeNamespace) {
		this.kimTypeNamespace = kimTypeNamespace;
	}

	/**
	 * Get the attribute attribute on this object
	 *
	 * @return attribute value
	 */
	public String getAttributeDefinition() {
		return this.attributeDefinition;
	}

	/**
	 * Set the attribute attribute on this object
	 *
	 * @param attributeDefinition value to set
	 */
	public void setAttributeDefinition(final String attributeDefinition) {
		this.attributeDefinition = attributeDefinition;
	}

	/**
	 * Get the type attribute on this object
	 *
	 * @return type value
	 */
	public String getKimTypeName() {
		return this.kimTypeName;
	}

	/**
	 * Set the type attribute on this object
	 *
	 * @param kimTypeName value to set
	 */
	public void setKimTypeName(final String kimTypeName) {
		this.kimTypeName = kimTypeName;
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
