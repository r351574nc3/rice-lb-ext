package liquibase.change.ext;

import org.junit.Test;

public class CreateTypeTests extends KimChangeBaseTest {

	protected String entityName(){
		return "krim_typ_t";
	}

	protected String whereClause() {
		return "NM = 'lb=ext Type'";
	}

	@Test
	public void testUpdateAndRollback_CustomNamespace() throws Exception{
		performUpdateAndRollback("custom-namespace");
	}

}
