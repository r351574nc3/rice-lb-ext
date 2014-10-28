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
public class CreateSystemParameterStatement extends AbstractSqlStatement {

    protected String application;
    protected String component;
    protected String namespace;
    protected String name;
    protected String type;
    protected String value;
    protected String description;
    protected String operator;
    protected String active = "Y";
    
    public CreateSystemParameterStatement() {
    }

    public CreateSystemParameterStatement(final String application,
					  final String namespace,
					  final String component, 
					  final String name,
					  final String type,
					  final String value,
					  final String description,
					  final String operator,
					  final String active) {
	setApplication(application);
	setComponent(component);
	setNamespace(namespace);
	setName(name);
	setType(type);
	setValue(value);
	setOperator(operator);
	setDescription(description);
	setActive(active);
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
     * Get the component attribute on this object
     *
     * @return component value
     */
    public String getComponent() {
        return this.component;
    }

    /**
     * Set the component attribute on this object
     *
     * @param component value to set
     */
    public void setComponent(final String component) {
        this.component = component;
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

    /**
     * Get the operator attribute on this object
     *
     * @return operator value
     */
    public String getOperator() {
        return this.operator;
    }

    /**
     * Set the operator attribute on this object
     *
     * @param operator value to set
     */
    public void setOperator(final String operator) {
        this.operator = operator;
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
