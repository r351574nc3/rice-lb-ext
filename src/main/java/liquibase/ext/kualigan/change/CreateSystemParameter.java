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

import liquibase.change.AbstractChange;
import liquibase.change.Change;
import liquibase.change.DatabaseChange;
import liquibase.change.DatabaseChangeProperty;
import liquibase.change.core.DeleteDataChange;
import liquibase.change.custom.CustomSqlChange;
import liquibase.change.custom.CustomSqlRollback;
import liquibase.database.Database;
import liquibase.exception.RollbackImpossibleException;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.InsertStatement;
import org.apache.commons.lang.StringUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import liquibase.ext.kualigan.statement.CreateSystemParameterStatement;

import static liquibase.ext.Constants.EXTENSION_PRIORITY;

/**
 * Custom Liquibase Refactoring for creating a Rice System Parameter.
 *
 * @author Leo Przybylski
 */
@DatabaseChange(name="createParameter", description = "Creates a Rice System parameter.", priority = EXTENSION_PRIORITY)
public class CreateSystemParameter extends AbstractChange implements CustomSqlChange, CustomSqlRollback {
    public static final String WHERE_CLAUSE = "APPL_ID = '%s' AND NMSPC_CD = '%s' AND CMPNT_CD = '%s' AND PARM_NM = '%s'";

    protected String application;
    protected String component;
    protected String namespace;
    protected String name;
    protected String type;
    protected String value;
    protected String description;
    protected String operator;
    protected String active = "Y";

    public CreateSystemParameter() {
    }

    @Override
    public final String getConfirmationMessage() {
	return "";
    }

    @Override
    public void setUp() throws SetupException {
    }

    @Override 
    public ValidationErrors validate(final Database database) {
	return new ValidationErrors();
    }

    @Override
    public void setFileOpener(final ResourceAccessor resourceAccessor) {
	// setResourceAccessor(resourceAccessor);
    }

    /**
     * Generates the SQL statements required to run the change.
     *
     * @param database databasethe target {@link liquibase.database.Database} associated to this change's statements
     * @return an array of {@link String}s with the statements
     */
    public SqlStatement[] generateStatements(final Database database) {
	
        return new SqlStatement[] { new CreateSystemParameterStatement(getApplication(),
								       getNamespace(),
								       getComponent(),
								       getName(),
								       getType(),
								       getValue(),
								       getDescription(),
								       getOperator(),
								       getActive()) };
    }

    @Override
    public SqlStatement[] generateRollbackStatements(final Database database) throws RollbackImpossibleException {
	final DeleteDataChange change = new DeleteDataChange();
	change.setTableName("KRCR_PARM_T");
	change.setWhere(String.format(WHERE_CLAUSE, getApplication(), getNamespace(), getComponent(), getName()));

	return change.generateStatements(database);
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
