package md;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseCreator {

	static final String DB_URL = "jdbc:mysql://localhost/";

	private static final String TABLE1_NAME = "testtable1";

	private static final String USER = "root";
	private static final String PASS = ".noihkm.";

	private Logger log = Logger.getInstance();
	private Connection connection = null;

	public DatabaseCreator() throws SQLException {
		this.connection = connect(DB_URL, USER, PASS);
	}

	private Connection connect(String dbUrl, String user, String password) throws SQLException {
		if (this.connection != null)
			return this.connection;
		log.info("Connecting to database...");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		Connection con = DriverManager.getConnection(dbUrl, user, password);
		if (con != null)
			log.info("connected successfully.");
		return con;
	}

	private void createOrReplaceDatabase(final String dbName) throws SQLException {
		checkConnected();

		// check if database exists
		boolean dbExistsAlready = false;
		ResultSet resultSet = connection.getMetaData().getCatalogs();
		while (resultSet.next()) {
			// Get the database name, which is at position 1
			String databaseName = resultSet.getString(1);
			log.debug(databaseName);
			if (databaseName != null && databaseName.equals(dbName)) {
				dbExistsAlready = true;
				break;
			}
		}
		resultSet.close();

		// delete database if exists
		Statement stmt = this.connection.createStatement();
		if (dbExistsAlready) {
			log.info("deleting existing database " + dbName+" ...");
			String drop = "DROP DATABASE " + dbName;
			stmt.execute(drop);
			log.info("deleted database successfully.");
		}
		

		// create new database
		log.info("creating new database "+ dbName+" ...");
		String createdb = "CREATE DATABASE " + dbName;
		stmt.execute(createdb);
		log.info("created database successfully.");

		// create tables in database

		// insert values to tables

	}

	private void checkConnected() {
		if (this.connection == null)
			throw new RuntimeException("Database not connected. Call connect() before!");
	}

	public void createTestDatabase() throws SQLException {

		createOrReplaceDatabase(TABLE1_NAME);
	}
}
