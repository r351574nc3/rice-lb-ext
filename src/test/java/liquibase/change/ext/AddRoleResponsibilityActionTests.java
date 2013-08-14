package liquibase.change.ext;

public class AddRoleResponsibilityActionTests extends KimChangeBaseTest {

	@Override
	protected String entityName() {
		return "krim_role_rsp_actn_t";
	}

	@Override
	protected String whereClause() {
		return "PRIORITY_NBR = '999'";
	}
}
