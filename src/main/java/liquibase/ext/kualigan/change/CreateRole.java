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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import liquibase.change.DatabaseChange;
import liquibase.change.DatabaseChangeProperty;
import liquibase.change.custom.CustomSqlChange;
import liquibase.database.Database;
import liquibase.exception.RollbackImpossibleException;
import liquibase.exception.CustomChangeException;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.InsertStatement;

import liquibase.ext.kualigan.statement.CreateRoleStatement;

import liquibase.change.core.DeleteDataChange;

import static liquibase.ext.Constants.EXTENSION_PRIORITY;

/**
 * Custom Liquibase Refactorign for adding a Role to KIM
 *
 * @author Leo Przybylski
 */
@DatabaseChange(name="createRole", description = "Creates a KIM Role.", priority = EXTENSION_PRIORITY)
public class CreateRole extends KimAbstractChange implements CustomSqlChange {

    private String name;
    private String namespace;
    private String description;
    private String type;
    private String typeNamespace;
    private String lastUpdated;
    private String active = "Y";

    protected List<AssignRoleMember> members;
    protected List<CreateType> types; // There's only one really
    

    public CreateRole() {
        super("role", "Adding a Role to KIM", EXTENSION_PRIORITY);
	members = new ArrayList<AssignRoleMember>();
	types = new ArrayList<CreateType>();
    }

    @Override
    protected String getSequenceName() {
	return "krim_role_id_s";
    }

    /**
     * Generates the SQL statements required to run the change.
     *
     * @param database databasethe target {@link liquibase.database.Database} associated to this change's statements
     * @return an array of {@link String}s with the statements
     */
    public SqlStatement[] generateStatements(final Database database) {
        final List<SqlStatement> memberStatements = new ArrayList<SqlStatement>();
        
        for (final AssignRoleMember member : getMembers()) {
            for (final SqlStatement statement : member.generateStatements(database)) {
                memberStatements.add(statement);
            }
        }

        final List<SqlStatement> typeStatements = new ArrayList<SqlStatement>();
        for (final CreateType type : getTypes()) {
            for (final SqlStatement statement : type.generateStatements(database)) {
                typeStatements.add(statement);
            }
        }

        return new SqlStatement[] { new CreateRoleStatement(getNamespace(),
							    getName(),
							    getDescription(),
							    "",
							    "",
							    getLastUpdated(),
							    getActive(),
							    memberStatements,
							    typeStatements) };

    }

    @Override
    public SqlStatement[] generateRollbackStatements(final Database database) throws RollbackImpossibleException {
	final String typeReference = getTypeForeignKey(database, getType(), getTypeNamespace());
	final DeleteDataChange removeRole = new DeleteDataChange();
	removeRole.setTableName("KRIM_ROLE_T");
	removeRole.setWhereClause(String.format("role_nm = '%s' and kim_typ_id = '%s'", getName(), typeReference));
	return removeRole.generateStatements(database);
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

    public String getTypeNamespace() {
	return typeNamespace;
    }

    public void setTypeNamespace(String typeNamespace) {
	this.typeNamespace = typeNamespace;
    }

    public List<CreateType> getTypes() {
	return types;
    }

    public void setTypes(final List<CreateType> types) {
	this.types = types;
    }   

    public List<AssignRoleMember> getMembers() {
	return members;
    }

    public void setMembers(final List<AssignRoleMember> members) {
	this.members = members;
    }   

    public CreateType createType(){
	final CreateType createType = new CreateType();
	getTypes().add(createType);
	return createType;
    }

    public AssignRoleMember createMember(){
	final AssignRoleMember assignRoleMember = new AssignRoleMember();
	getMembers().add(assignRoleMember);
	return assignRoleMember;
    }
}
