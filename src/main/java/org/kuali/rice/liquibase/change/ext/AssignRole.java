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
package org.kuali.rice.liquibase.change.ext;

import java.math.BigInteger;

import liquibase.change.AbstractChange;
import liquibase.change.Change;
import liquibase.change.custom.CustomSqlChange;
import liquibase.database.Database;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.executor.ExecutorService;
import liquibase.resource.ResourceAccessor;
import liquibase.sql.Sql;
import liquibase.sql.UnparsedSql;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.InsertStatement;
import liquibase.statement.core.RuntimeStatement;

import liquibase.change.core.DeleteDataChange;

import static liquibase.ext.Constants.EXTENSION_PRIORITY;

/**
 * Custom refactoring for adding a Role to KIM.
 *
 * @author Leo Przybylski
 */
public class AssignRole extends AbstractChange implements CustomSqlChange {
    private String namespace;
    private String type;
    private String member;
    private String role;
	   	        
    
    public AssignRole() {
        super("AssignRole", "Assigning a KIM role", EXTENSION_PRIORITY);
    }
    
    /**
     * Supports all databases 
     */
    @Override
    public boolean supports(Database database) {
        return true;
    }

    /**
     *
     */
    @Override
    public ValidationErrors validate(Database database) {
        return super.validate(database);
    }

    /**
     * Generates the SQL statements required to run the change.
     *
     * @param database databasethe target {@link liquibase.database.Database} associated to this change's statements
     * @return an array of {@link String}s with the statements
     */
    public SqlStatement[] generateStatements(Database database) {
        final InsertStatement assignPermission = new InsertStatement(database.getDefaultSchemaName(),
                                                                     "krim_role_mbr_t");
        final SqlStatement getId = new RuntimeStatement() {
                public Sql[] generate(Database database) {
                    return new Sql[] {
                        new UnparsedSql("insert into KRIM_ROLE_MBR_ID_S values(null)"),
                        new UnparsedSql("select max(id) from KRIM_ROLE_MBR_ID_S")
                    };
                }
            };


        final SqlStatement getRoleId = new RuntimeStatement() {
                public Sql[] generate(Database database) {
                    return new Sql[] {
                        new UnparsedSql(String.format("select ROLE_ID from KRIM_ROLE_T where ROLE_NM = '%s' and NMSPC_CD = '%s'", getRole(), getNamespace()))
                    };
                }
            };

        final SqlStatement getMemberId = new RuntimeStatement() {
                public Sql[] generate(Database database) {
                    return new Sql[] {
                        new UnparsedSql(String.format("select PRNCPL_ID from KRIM_PRNCPL_T where PRNCPL_NM = '%s'", getMember()))
                    };
                }
            };

        try {
            final BigInteger id     = (BigInteger) ExecutorService.getInstance().getExecutor(database).queryForObject(getId, BigInteger.class);
            final BigInteger roleId = (BigInteger) ExecutorService.getInstance().getExecutor(database).queryForObject(getRoleId, BigInteger.class);
            final BigInteger memberId = (BigInteger) ExecutorService.getInstance().getExecutor(database).queryForObject(getMemberId, BigInteger.class);
            
            assignPermission.addColumnValue("ROLE_MBR_ID", id);
            assignPermission.addColumnValue("role_id", roleId);
            assignPermission.addColumnValue("mbr_id", memberId);
            assignPermission.addColumnValue("MBR_TYP_CD", getType());
            assignPermission.addColumnValue("ver_nbr", 1);
            assignPermission.addColumnValue("obj_id", "sys_guid()");
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new SqlStatement[] {
            assignPermission
        };
    }


    /**
     * Used for rollbacks. Defines the steps/{@link Change}s necessary to rollback.
     * 
     * @return {@link Array} of {@link Change} instances
     */
    protected Change[] createInverses() {
        final DeleteDataChange undoAssign = new DeleteDataChange();
        final String roleId = String.format("(select ROLE_ID from KRIM_ROLE_T where ROLE_NM = '%s' and NMSPC_CD = '%s')", getRole(), getNamespace());
        final String mbrId  = String.format("(select PRNCPL_ID from KRIM_PRNCPL_T where nm = '%s')", getMember());
        undoAssign.setTableName("KRIM_ROLE_MBR_T");
        undoAssign.setWhereClause(String.format("role_id in %s and mbr_id in %s", roleId, mbrId));

        return new Change[] {
            undoAssign
        };
    }
    
    /**
     * @return Confirmation message to be displayed after the change is executed
     */
    public String getConfirmationMessage() {
        return "";
    }

    /**
     * Get the member attribute on this object
     *
     * @return member value
     */
    public String getMember() {
        return this.member;
    }

    /**
     * Set the member attribute on this object
     *
     * @param member value to set
     */
    public void setMember(final String member) {
        this.member = member;
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
     * Get the role attribute on this object
     *
     * @return role value
     */
    public String getRole() {
        return this.role;
    }

    /**
     * Set the role attribute on this object
     *
     * @param role value to set
     */
    public void setRole(final String role) {
        this.role = role;
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

    public void setFileOpener(final ResourceAccessor resourceAccessor) {    
        setResourceAccessor(resourceAccessor);
    }

    public void setUp() throws SetupException {
    }
}