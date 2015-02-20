package liquibase.ext.kualigan.change;

public class CreateResponsibilityTests extends KimChangeBaseTest{

	protected String entityName(){
		return "krim_rsp_t";
	}

	protected String whereClause() {
		return "NM = 'Tst Responsibility'";
	}

}
