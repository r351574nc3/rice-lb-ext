package liquibase.ext.kualigan.change;

public class CreatePermissionTests extends KimChangeBaseTest {

	protected String entityName(){
		return "krim_perm_t";
	}

	protected String whereClause() {
		return "NM = 'lb-ext permission'";
	}

}
