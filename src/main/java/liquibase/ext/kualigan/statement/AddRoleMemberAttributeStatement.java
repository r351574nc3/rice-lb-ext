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
public class AddRoleMemberAttributeStatement extends AbstractSqlStatement {

	protected String type;
	protected String attributeDef;
	protected String role;
	protected String roleNamespace;
	protected String member;
	protected String value;
	protected String active = "Y";
	private String memberFkSeq;

	public AddRoleMemberAttributeStatement(final String type,
					final String attributeDef,
					final String value,
					final String role,
					final String roleNamespace,
					final String member) {
		setType(type);
		setAttributeDef(attributeDef);
		setRoleNamespace(roleNamespace);
		setRole(role);
		setMember(member);
		setValue(value);
		setActive(active);
	}

	public AddRoleMemberAttributeStatement(final String type,
					final String attributeDef,
					final String value,
					final String memberFkSeq) {
		setType(type);
		setAttributeDef(attributeDef);
		setValue(value);
		setActive(active);
		setMemberFkSeq(memberFkSeq);
	}


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAttributeDef() {
		return attributeDef;
	}

	public void setAttributeDef(String attributeDef) {
		this.attributeDef = attributeDef;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getRoleNamespace() {
		return roleNamespace;
	}

	public void setRoleNamespace(String roleNamespace) {
		this.roleNamespace = roleNamespace;
	}

	public String getMember() {
		return member;
	}

	public void setMember(String member) {
		this.member = member;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getMemberFkSeq() {
		return memberFkSeq;
	}

	public void setMemberFkSeq(String memberFkSeq) {
		this.memberFkSeq = memberFkSeq;
	}
}
