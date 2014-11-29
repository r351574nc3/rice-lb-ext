package liquibase.ext.kualigan.change;

public class AssignKimTypeAttributeTests extends KimChangeBaseTest {

	@Override
	protected String entityName() {
		return "krim_typ_attr_t";
	}

	@Override
	protected String whereClause() {
		return "KIM_TYP_ID = '1' AND KIM_ATTR_DEFN_ID = '1'";
	}
}
