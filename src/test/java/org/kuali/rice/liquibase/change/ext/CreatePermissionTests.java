package org.kuali.rice.liquibase.change.ext;

public class CreatePermissionTests extends KimChangeBaseTest {

	protected String entityName(){
		return "krim_perm_t";
	}

	protected String whereClause() {
		return "NM = 'lb-ext permission'";
	}

}
