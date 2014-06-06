package liquibase.change.ext;

public class AddPermissionAttributeTests extends KimChangeBaseTest{

	@Override
	protected String entityName() {
		return "krim_perm_attr_data_t";
	}

	@Override
	protected String whereClause() {
		return "attr_val = 'Test Perm Attr'";
	}
}
