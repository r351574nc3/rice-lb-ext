package org.kuali.rice.liquibase.change.ext;

public class AddPermissionAttributeTests extends KimChangeBaseTest{

	@Override
	protected String entityName() {
		return "krim_perm_attr_data_t";
	}

	@Override
	protected String whereClause() {

//		final String atrDefSql = String.format("(select KIM_ATTR_DEFN_ID from krim_attr_defn_t where nm = '%s')", "beanName");
//		final String typeSql = String.format("(select kim_typ_id from krim_typ_t where nm = '%s')", "Default");
//		final String permSql = String.format("(select PERM_ID from KRIM_PERM_T where nm = '%s' and NMSPC_CD = '%s')", "Use Document Operation Screen", "KR-WKFLW");
//
//		String.format("NM = ''perm_id in %s AND kim_typ_id in %s AND kim_attr_defn_id in %s", atrDefSql, typeSql, permSql);
		return "attr_val = 'Test Perm Attr'";

	}
}
