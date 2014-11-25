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
package liquibase.ext.kualigan.statement;


import liquibase.statement.AbstractSqlStatement;
import liquibase.statement.SqlStatement;
import liquibase.sql.Sql;
import liquibase.sql.UnparsedSql;

import java.util.ArrayList;
import java.util.List;

/**
 * Statement basically exists solely to map and kick-off the sql generator chain
 * 
 * @author Leo Przybylski
 */
public class CreateRoleStatement extends AbstractSqlStatement {
    protected String name;
    protected String namespace;
    protected String description;
    protected String type;
    protected String typeNamespace;
    protected String lastUpdated;
    protected String active = "Y";

    protected List<SqlStatement> members;
    protected List<SqlStatement> types; // There's only one really
    
    public CreateRoleStatement() {
        types = new ArrayList<SqlStatement>();
        members = new ArrayList<SqlStatement>();
    }

    public CreateRoleStatement(final String namespace,
                               final String name, 
                               final String description,
                               final String type,
                               final String typeNamespace,
                               final String lastUpdated,
                               final String active,
                               final List<SqlStatement> members,
                               final List<SqlStatement> types) {
        setNamespace(namespace);
        setName(name);
        setDescription(description);
        setActive(active);
        setType(type);
        setTypeNamespace(typeNamespace);
        setLastUpdated(lastUpdated);
        setMembers(members);
        setTypes(types);
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

    public List<SqlStatement> getTypes() {
        return types;
    }

    public void setTypes(final List<SqlStatement> types) {
        this.types = types;
    }   

    public List<SqlStatement> getMembers() {
        return members;
    }

    public void setMembers(final List<SqlStatement> members) {
        this.members = members;
    }   
}
