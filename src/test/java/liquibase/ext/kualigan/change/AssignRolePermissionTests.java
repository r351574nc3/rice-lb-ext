package liquibase.ext.kualigan.change;

public class AssignRolePermissionTests extends KimChangeBaseTest {

	@Override
	protected String entityName() {
		return "krim_role_perm_t";
	}

	@Override
	protected String whereClause() {
		return "perm_id = '140' and role_id = '1'";
	}
}
