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
public class AssignRoleMemberStatement extends AbstractSqlStatement {

    protected String namespace;
    protected String type;
    protected String member;
    protected String memberNamespace;
    protected String role;
    protected String active = "Y";
//    protected List<SqlStatement> attributes;
//    protected List<SqlStatement> actions;
    
    
    public AssignRoleMemberStatement() {
    }

    public AssignRoleMemberStatement(final String namespace,
				    final String type,
				    final String member,
				    final String memberNamespace,
				    final String role,
				    final String active/*,
				    final List<SqlStatement> attributes,
				    final List<SqlStatement> actions*/) {
	setNamespace(namespace);
	setType(type);
	setMember(member);
	setMemberNamespace(memberNamespace);
	setRole(role);
	setActive(active);
//	setAttributes(attributes);
//	setActions(actions);
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
     * Set the active attribute on this object
     *
     * @param active value to set
     */
    public void setActive(final String active) {
        this.active = active;
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

    public String getMemberNamespace() {
	return memberNamespace;
    }

    public void setMemberNamespace(String memberNamespace) {
	this.memberNamespace = memberNamespace;
    }

//    public List<SqlStatement> getAttributes() {
//	return attributes;
//    }
//
//    public void setAttributes(final List<SqlStatement> attributes) {
//	this.attributes = attributes;
//    }
//    public List<SqlStatement> getActions() {
//	return actions;
//    }
//
//    public void setActions(final List<SqlStatement> actions) {
//	this.actions = actions;
//    }
}
