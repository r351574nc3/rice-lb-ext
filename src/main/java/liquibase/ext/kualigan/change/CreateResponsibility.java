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

import liquibase.change.Change;
import liquibase.change.DatabaseChange;
import liquibase.change.DatabaseChangeProperty;
import liquibase.change.core.DeleteDataChange;
import liquibase.change.custom.CustomSqlChange;
import liquibase.database.Database;
import liquibase.exception.RollbackImpossibleException;
import liquibase.ext.kualigan.statement.AssignRoleMemberStatement;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.InsertStatement;

import liquibase.ext.kualigan.statement.CreateResponsibilityStatement;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static liquibase.ext.Constants.EXTENSION_PRIORITY;

/**
 * Custom Liquibase Refactoring for creating a KIM Responsibility.
 *
 * @author Leo Przybylski
 */
@DatabaseChange(name = "responsibility", description = "Creates a KIM Responsibility.", priority = EXTENSION_PRIORITY)
public class CreateResponsibility extends KimAbstractChange implements CustomSqlChange {

	private String template;
	private String namespace;
	private String name;
	private String description;
	private String active = "Y";
	private List<AddResponsibilityAttribute> attribute = new ArrayList<AddResponsibilityAttribute>();

	public CreateResponsibility() {
		super("responsibility", "Adding a Responsibility to KIM", EXTENSION_PRIORITY);
	}

	@Override
	protected String getSequenceName() {
		return "krim_rsp_id_s";
	}

	/**
	 * Generates the SQL statements required to run the change.
	 *
	 * @param database databasethe target {@link liquibase.database.Database} associated to this change's statements
	 * @return an array of {@link String}s with the statements
	 */
	public SqlStatement[] generateStatements(Database database) {
		List<SqlStatement> statements = new ArrayList<SqlStatement>();
		statements.add(new CreateResponsibilityStatement(getTemplate(),
						getNamespace(),
						getName(),
						getDescription(),
						getActive()));
		statements.addAll(generateReponsibilityAttributeStatements(database));
		return statements.toArray(new SqlStatement[statements.size()]);

	}

	private List<SqlStatement> generateReponsibilityAttributeStatements(Database database) {
		final List<SqlStatement> attributeStatements = new ArrayList<SqlStatement>();
		for (final AddResponsibilityAttribute attribute : getAttribute()) {
			attribute.setResponsibilityFkSeq(getSequenceName());
			attributeStatements.addAll(Arrays.asList(attribute.generateStatements(database)));
		}
		return attributeStatements;
	}


	@Override
	public SqlStatement[] generateRollbackStatements(final Database database) throws RollbackImpossibleException {
		List<SqlStatement> result = new ArrayList<SqlStatement>();
		String responsibilityId = getResponsibilityForeignKey(database, getName(), getNamespace());

		final DeleteDataChange removeResponsibility = new DeleteDataChange();
		removeResponsibility.setTableName("krim_rsp_t");
		removeResponsibility.setWhereClause(String.format("rsp_id = '%s'", responsibilityId));

		for (AddResponsibilityAttribute attribute : getAttribute()) {
			attribute.setResponsibilityFkSeq(responsibilityId);
			result.addAll(Arrays.asList(attribute.generateRollbackStatements(database)));
		}

		result.addAll(Arrays.asList(removeResponsibility.generateStatements(database)));
		return result.toArray(new SqlStatement[result.size()]);

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<AddResponsibilityAttribute> getAttribute() {
		return attribute;
	}

	public void setAttribute(List<AddResponsibilityAttribute> attribute) {
		this.attribute = attribute;
	}
}
