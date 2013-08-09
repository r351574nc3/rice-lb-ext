package org.kuali.rice.liquibase.change.ext;

public class CreateTypeTests extends KimChangeBaseTest {

	protected String entityName(){
		return "krim_typ_t";
	}

	protected String whereClause() {
		return "NM = 'lb=ext Type'";
	}

}
