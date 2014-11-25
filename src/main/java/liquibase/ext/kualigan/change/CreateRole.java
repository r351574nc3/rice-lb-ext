// Copyright 2014 Leo Przybylski. All rights reserved.
//
// Redistribution and use in source and binary forms, with or without modification, are
// permitted provided that the following conditions are met:
//
//    1. Redistributions of source code must retain the above copyright notice, this list of
//       conditions and the following disclaimer.
//
//    2. Redistributions in binary form must reproduce the above copyright notice, this list
//       of conditions and the following disclaimer in the documentation and/or other materials
//       provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY Leo Przybylski ''AS IS'' AND ANY EXPRESS OR IMPLIED
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
// FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> OR
// CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
// ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
// NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
// ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
// The views and conclusions contained in the software and documentation are those of the
// authors and should not be interpreted as representing official policies, either expressed
// or implied, of Leo Przybylski.
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
