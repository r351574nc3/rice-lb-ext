package liquibase.change.ext;

public class AddResponsibilityAttributeTests extends KimChangeBaseTest{

	@Override
	protected String entityName() {
		return "krim_rsp_attr_data_t";
	}

	@Override
	protected String whereClause() {
		return "attr_val = 'Test Responsibility Attr'";
	}
}
