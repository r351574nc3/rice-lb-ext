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
public class AddPermissionAttributeStatement extends AbstractSqlStatement {

	protected String value;
	protected String namespace;
	protected String attributeDef;
	protected String permission;
	protected String type;
	protected String active = "Y";
	protected String permissionFkSeq;

	public AddPermissionAttributeStatement(final String value, final String attributeDef, final String type, final String active, final String permission, final String namespace) {
		setNamespace(namespace);
		setValue(value);
		setAttributeDef(attributeDef);
		setPermission(permission);
		setType(type);
		setActive(active);
	}

	public AddPermissionAttributeStatement(final String value, final String attributeDef, final String type, final String active, final String permissionFkSeq) {
		setValue(value);
		setAttributeDef(attributeDef);
		setType(type);
		setActive(active);
		setPermissionFkSeq(permissionFkSeq);
	}


	/**
	 * Get the attribute attribute on this object
	 *
	 * @return attribute value
	 */
	public String getAttributeDef() {
		return this.attributeDef;
	}

	/**
	 * Set the attribute attribute on this object
	 *
	 * @param attributeDef value to set
	 */
	public void setAttributeDef(final String attributeDef) {
		this.attributeDef = attributeDef;
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
	 * Get the permission attribute on this object
	 *
	 * @return permission value
	 */
	public String getPermission() {
		return this.permission;
	}

	/**
	 * Set the permission attribute on this object
	 *
	 * @param permission value to set
	 */
	public void setPermission(final String permission) {
		this.permission = permission;
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

	public void setPermissionFkSeq(String permissionFkSeq) {
		this.permissionFkSeq = permissionFkSeq;
	}

	public String getPermissionFkSeq() {
		return permissionFkSeq;
	}
}
