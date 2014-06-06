package liquibase.change.ext;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.fail;

public class CreatePermissionAndAddPermissionAttributeTests extends KimChangeBaseTest {

	@Override
	public void testUpdateAndRollback() throws Exception {}

	protected String entityName(){
		return "krim_perm_and_perm_attr_t";
	}

	protected String whereClause() {
		return "";
	}

	@Override
	protected void assertInsert(String whereClause) throws SQLException {
		ResultSet rs = getPermissionResultSet();
		if (!rs.next()){
			fail("Permission insert");
		}

		ResultSet rs2 = getPermissionAttributeResultSet();
		if (!rs2.next()){
			fail("Permission Attribute insert");
		}
	}

	@Override
	protected void assertRollback(String whereClause) throws SQLException {
		ResultSet rs = getPermissionResultSet();
		if (rs.next()){
			fail("Permission rollback");
		}

		ResultSet rs2 = getPermissionAttributeResultSet();
		if (rs2.next()){
			fail("Permission Attribute rollback");
		}	}

	private ResultSet getPermissionResultSet() throws SQLException {
		PreparedStatement ps = connection.prepareStatement(String.format("select * from krim_perm_t where NM = '%s'", "lb-ext permission"));
		return ps.executeQuery();
	}

	private ResultSet getPermissionAttributeResultSet() throws SQLException {
		PreparedStatement ps = connection.prepareStatement("select * from krim_perm_attr_data_t where attr_val = 'Test Perm Attr'");
		return ps.executeQuery();
	}
}
