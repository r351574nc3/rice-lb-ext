package liquibase.ext.kualigan.change;

import org.junit.Ignore;
import org.junit.Test;

public class AssignRoleMemberTests extends KimChangeBaseTest{

	@Test
	@Ignore
	public void testUpdateAndRollback() throws Exception {
		//todo: implement once rollback is in place - causing duplicate key contraints
	}


	@Override
	protected String entityName() {
		return "krim_role_mbr_t";
	}

	@Override
	protected String whereClause() {
		final String roleId = "(select ROLE_ID from KRIM_ROLE_T where ROLE_NM = 'User2' and NMSPC_CD = 'KUALI')";
		final String memberId = "(select PRNCPL_ID from KRIM_PRNCPL_T where PRNCPL_NM = 'kr')";
		return String.format("role_id in %s and MBR_ID in %s", roleId, memberId);
	}
}
