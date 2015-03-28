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

import java.util.List;

/**
 * Statement basically exists solely to map and kick-off the sql generator chain
 *
 * @author Leo Przybylski
 */
public class AssignRolePermissionStatement extends AbstractSqlStatement {

	private String permissionName;
	private String permissionNamespace;
	private String roleName;
	private String roleNamespace;
	private String active;

	public AssignRolePermissionStatement(String permissionName, String permissionNamespace, String roleName, String roleNamespace, String active) {
		this.permissionName = permissionName;
		this.permissionNamespace = permissionNamespace;
		this.roleName = roleName;
		this.roleNamespace = roleNamespace;
		this.active = active;
	}

	public String getPermissionName() {
		return permissionName;
	}

	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}

	public String getPermissionNamespace() {
		return permissionNamespace;
	}

	public void setPermissionNamespace(String permissionNamespace) {
		this.permissionNamespace = permissionNamespace;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleNamespace() {
		return roleNamespace;
	}

	public void setRoleNamespace(String roleNamespace) {
		this.roleNamespace = roleNamespace;
	}

	public String getActive() {
        return this.active == null ? "Y" : this.active;
	}

	public void setActive(String active) {
		this.active = active;
	}
}
