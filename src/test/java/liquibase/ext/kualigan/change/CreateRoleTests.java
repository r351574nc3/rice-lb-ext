package liquibase.ext.kualigan.change;

public class CreateRoleTests extends KimChangeBaseTest{

	@Override
	protected String entityName() {
		return "krim_role_t";
	}

	@Override
	protected String whereClause() {
		return "role_nm = 'Tst Role'";
	}
}
