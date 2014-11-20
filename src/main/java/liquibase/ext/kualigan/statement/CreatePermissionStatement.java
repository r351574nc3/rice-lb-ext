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

import java.util.List;

/**
 * Statement basically exists solely to map and kick-off the sql generator chain
 * 
 * @author Leo Przybylski
 */
public class CreatePermissionStatement extends AbstractSqlStatement {
    protected String template;
    protected String namespace;
    protected String name;
    protected String description;
    protected String active = "Y";
    protected String uniqueAttributeDefinitions;

    public CreatePermissionStatement(final String template,
				     final String namespace,
				     final String name, 
				     final String description,
				     final String active) {
	setTemplate(template);
	setNamespace(namespace);
	setName(name);
	setDescription(description);
	setActive(active);
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

    public String getUniqueAttributeDefinitions() {
	return uniqueAttributeDefinitions;
    }

    public void setUniqueAttributeDefinitions(final String uniqueAttributeDefinitions) {
	this.uniqueAttributeDefinitions = uniqueAttributeDefinitions;
    }

}
