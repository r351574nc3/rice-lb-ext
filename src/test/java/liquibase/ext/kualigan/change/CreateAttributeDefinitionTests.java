package liquibase.ext.kualigan.change;

public class CreateAttributeDefinitionTests extends KimChangeBaseTest {

	@Override
	protected String entityName() {
		return "krim_attr_defn_t";
	}

	@Override
	protected String whereClause() {
		return "NM = 'Test Attr Def'";
	}
}
