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

/**
 * Statement basically exists solely to map and kick-off the sql generator chain
 * 
 * @author Leo Przybylski
 */
public class AddRoleResponsibilityActionStatement extends AbstractSqlStatement {

    protected String roleNamespace;
    protected String roleName;
    protected String responsibility;
    protected String member;
    protected String priority;
    protected String force;
    protected String actionTypeCode;
    protected String actionPolicyCode;
    protected String roleMemberId;

    public AddRoleResponsibilityActionStatement() {
    }

    public AddRoleResponsibilityActionStatement(final String roleNamespace,
						final String roleName,
						final String responsibility,
						final String member,
						final String priority,
						final String force,
						final String actionTypeCode,
						final String actionPolicyCode) {
	setRoleNamespace(roleNamespace);
	setRoleName(roleName);
	setResponsibility(responsibility);
	setMember(member);
	setPriority(priority);
	setForce(force);
	setActionTypeCode(actionTypeCode);
	setActionPolicyCode(actionPolicyCode);
    }

    /**
     * Get the roleNamespace attribute on this object
     *
     * @return roleNamespace value
     */
    public String getRoleNamespace() {
        return this.roleNamespace;
    }

    /**
     * Set the roleNamespace attribute on this object
     *
     * @param roleNamespace value to set
     */
    public void setRoleNamespace(final String roleNamespace) {
        this.roleNamespace = roleNamespace;
    }

    /**
     * Get the responsibility attribute on this object
     *
     * @return responsibility value
     */
    public String getResponsibility() {
        return this.responsibility;
    }

    /**
     * Set the responsibility attribute on this object
     *
     * @param responsibility value to set
     */
    public void setResponsibility(final String responsibility) {
        this.responsibility = responsibility;
    }

    /**
     * Get the priority attribute on this object
     *
     * @return priority value
     */
    public String getPriority() {
        return this.priority;
    }

    /**
     * Set the priority attribute on this object
     *
     * @param priority value to set
     */
    public void setPriority(final String priority) {
        this.priority = priority;
    }

    /**
     * Get the actionPolicyCode attribute on this object
     *
     * @return actionPolicyCode value
     */
    public String getActionPolicyCode() {
        return this.actionPolicyCode;
    }

    /**
     * Set the actionPolicyCode attribute on this object
     *
     * @param actionPolicyCode value to set
     */
    public void setActionPolicyCode(final String actionPolicyCode) {
        this.actionPolicyCode = actionPolicyCode;
    }

    /**
     * Get the force attribute on this object
     *
     * @return force value
     */
    public String getForce() {
        return this.force;
    }

    /**
     * Set the force attribute on this object
     *
     * @param force value to set
     */
    public void setForce(final String force) {
        this.force = force;
    }

    /**
     * Get the role attribute on this object
     *
     * @return role value
     */
    public String getRoleName() {
        return this.roleName;
    }

    /**
     * Set the role attribute on this object
     *
     * @param roleName value to set
     */
    public void setRoleName(final String roleName) {
        this.roleName = roleName;
    }

    /**
     * Get the actionTypeCode attribute on this object
     *
     * @return actionTypeCode value
     */
    public String getActionTypeCode() {
        return this.actionTypeCode;
    }

    /**
     * Set the actionTypeCode attribute on this object
     *
     * @param actionTypeCode value to set
     */
    public void setActionTypeCode(final String actionTypeCode) {
        this.actionTypeCode = actionTypeCode;
    }

    public String getMember() {
	return member;
    }

    public void setMember(String member) {
	this.member = member;
    }

    public void setRoleMemberId(String roleMemberId) {
	this.roleMemberId = roleMemberId;
    }
}
