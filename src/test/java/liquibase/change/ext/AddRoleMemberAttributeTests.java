package liquibase.change.ext;

public class AddRoleMemberAttributeTests extends KimChangeBaseTest {


	@Override
	protected String entityName() {
		return "krim_role_mbr_attr_data_t";
	}

	@Override
	protected String whereClause() {
		return "attr_val = 'Test Role Mbr Attr'";
	}
}
