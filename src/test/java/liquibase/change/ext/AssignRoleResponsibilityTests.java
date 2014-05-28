package liquibase.change.ext;

public class AssignRoleResponsibilityTests extends KimChangeBaseTest {

	@Override
	protected String entityName() {
		return "krim_role_rsp_t";
	}

	@Override
	protected String whereClause() {
		return "rsp_id = '10000' and role_id = '1'";
	}
}
