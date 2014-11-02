package liquibase.change.ext;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.*;

import static junit.framework.Assert.fail;

public abstract class KimChangeBaseTest {

	protected static Connection connection;
	protected static Database database;

	@BeforeClass
	public static void bootstrapTestDB() throws Exception {
		connection = DriverManager.getConnection("jdbc:h2:mem:TEST;MODE=Oracle");
		database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
		Liquibase liquibase = new Liquibase("org/kuali/rice/liquibase/change/ext/bootstrap.xml", new ClassLoaderResourceAccessor(), database);
		liquibase.update("");
	}

	@Test
	public void testUpdateAndRollback() throws Exception {
		performUpdateAndRollback("default");
	}

	@Test
	public void testUpdateAndRollback_customNamespace() throws Exception {
		performUpdateAndRollback("custom-namespace");
	}

	protected abstract String entityName();

	protected abstract String whereClause();

	protected void assertInsert(String whereClause) throws SQLException {
		ResultSet r = getEntityResultSet(whereClause);
		if (!r.next()) {
			fail("Insert failed!");
		}
	}

	protected void assertRollback(String whereClause) throws SQLException {
		ResultSet r = getEntityResultSet(whereClause);
		if (r.next()) {
			fail("Rollback failed!");
		}
	}

	private void performUpdateAndRollback(String context) throws Exception {
		//given
		Liquibase liquibase = new Liquibase("org/kuali/rice/liquibase/change/ext/" + entityName() + ".xml", new ClassLoaderResourceAccessor(), database);
		//when
		liquibase.update(context);
		//then
		assertInsert(whereClause());

		//when
		liquibase.rollback(1, context);
		//then
		assertRollback(whereClause());
	}

	private ResultSet getEntityResultSet(String whereClause) throws SQLException {
		PreparedStatement ps = connection.prepareStatement(String.format("select * from %s where %s", entityName(), whereClause));
		return ps.executeQuery();
	}
}
