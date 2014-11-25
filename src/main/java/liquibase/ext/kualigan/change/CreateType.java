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


import liquibase.change.Change;
import liquibase.change.DatabaseChange;
import liquibase.change.DatabaseChangeProperty;
import liquibase.change.core.DeleteDataChange;
import liquibase.database.Database;
import liquibase.statement.SqlStatement;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import liquibase.ext.kualigan.statement.CreateTypeStatement;

import static liquibase.ext.Constants.EXTENSION_PRIORITY;

/**
 * Custom Liquibase Refactoring for creating a KIM Type.
 *
 * @author Leo Przybylski
 */
@DatabaseChange(name="createKimType", description = "Creates a Rice Kim Type Record.", priority = EXTENSION_PRIORITY)
public class CreateType extends KimAbstractChange {

    protected String application;
    protected String namespace;
    protected String name;
    protected String service;
    protected String active = "Y";
    protected List<AssignKimTypeAttribute> attributes;
    protected String uniqueAttributeDefinitions;
    
    public CreateType() {
        super("type", "Adding a new KIM Type to KIM", EXTENSION_PRIORITY);
        setAttributes(new ArrayList<AssignKimTypeAttribute>());
    }

    @Override
    public final String getConfirmationMessage() {
        return String.format("Inserted KIM Type '%s' into namespace '%s' successfully.", getName(), getNamespace());
    }

    @Override
    protected String getSequenceName() {
        return "krim_typ_id_s";
    }

    /**
     * Generates the SQL statements required to run the change.
     *
     * @param database databasethe target {@link liquibase.database.Database} associated to this change's statements
     * @return an array of {@link String}s with the statements
     */
    public SqlStatement[] generateStatements(final Database database) {
        
        final List<SqlStatement> attributeStatements = new ArrayList<SqlStatement>();

        for (final AssignKimTypeAttribute attribute : getAttributes()) {
            for (final SqlStatement statement : attribute.generateStatements(database)) {
                attributeStatements.add(statement);
            }
        }

        return new SqlStatement[] { new CreateTypeStatement(getNamespace(),
                                                            getName(),
                                                            getService(),
                                                            getActive(), attributeStatements) };
    }

    /**
     * Used for rollbacks. Defines the steps/{@link liquibase.change.Change}s necessary to rollback.
     *
     * @return {@link Array} of {@link liquibase.change.Change} instances
     */
    protected Change[] createInverses() {
        final DeleteDataChange removeType = new DeleteDataChange();
        removeType.setTableName("krim_typ_t");
        removeType.setWhereClause(String.format("nmspc_cd = '%s' AND nm = '%s' AND srvc_nm = '%s'", getNamespace(), getName(), getService()));

        return new Change[] {
            removeType
        };
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
     * Get the service attribute on this object
     *
     * @return service value
     */
    public String getService() {
        return this.service;
    }

    /**
     * Set the service attribute on this object
     *
     * @param service value to set
     */
    public void setService(final String service) {
        this.service = service;
    }

    /**
     * Get the application attribute on this object
     *
     * @return application value
     */
    public String getApplication() {
        return this.application;
    }

    /**
     * Set the application attribute on this object
     *
     * @param application value to set
     */
    public void setApplication(final String application) {
        this.application = application;
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


    public String getUniqueAttributeDefinitions() {
        return uniqueAttributeDefinitions;
    }

    public void setUniqueAttributeDefinitions(final String uniqueAttributeDefinitions) {
        this.uniqueAttributeDefinitions = uniqueAttributeDefinitions;
    }

    public void setAttributes(final List<AssignKimTypeAttribute> attributes) {
        this.attributes = attributes;
    }

    public List<AssignKimTypeAttribute> getAttributes() {
        return this.attributes;
    }

    public AssignKimTypeAttribute createAttribute() {
        final AssignKimTypeAttribute assignKimTypeAttribute = new AssignKimTypeAttribute();
        this.getAttributes().add(assignKimTypeAttribute);
        return assignKimTypeAttribute;
    }
}
