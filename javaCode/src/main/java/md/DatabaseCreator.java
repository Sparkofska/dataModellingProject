package md;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseCreator {

	static final String DB_URL = "jdbc:mysql://localhost/";

	private static final String DB_NAME = "testdb";

	private static final String USER = "root";
	private static final String PASS = ".noihkm.";

	private Logger log = Logger.getInstance();
	private Connection connection = null;

	public DatabaseCreator() {

	}

	private void connect(String dbUrl, String user, String password) throws SQLException {
		if (this.connection != null)
			return;
		log.info("Connecting to database...");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		this.connection = DriverManager.getConnection(dbUrl, user, password);
		if (this.connection != null)
			log.info("connected successfully.");
	}

	private void close() throws SQLException {
		if (this.connection != null)
			this.connection.close();
		this.connection = null;
	}

	private void createOrReplaceDatabase(final String dbName) throws SQLException {
		checkConnected();

		// check if database exists
		boolean dbExistsAlready = false;
		ResultSet resultSet = connection.getMetaData().getCatalogs();
		log.debug("\nfound databases:");
		while (resultSet.next()) {
			// Get the database name, which is at position 1
			String databaseName = resultSet.getString(1);
			log.debug(databaseName);
			if (databaseName != null && databaseName.equals(dbName)) {
				dbExistsAlready = true;
				break;
			}
		}
		log.debug("\n");
		resultSet.close();

		// delete database if exists
		Statement stmt = this.connection.createStatement();
		if (dbExistsAlready) {
			log.info("deleting existing database " + dbName + " ...");
			String drop = "DROP DATABASE " + dbName;
			stmt.execute(drop);
			log.info("deleted database successfully.");
		}

		// create new database
		log.info("creating new database " + dbName + " ...");
		String createdb = "CREATE DATABASE " + dbName;
		stmt.execute(createdb);
		log.info("created database successfully.");
	}

	private void createExampleData(final String dbName) throws SQLException {

		checkConnected();
		Statement stmt = this.connection.createStatement();
		String use = "USE " + dbName;
		stmt.execute(use);

		// create tables in database
		log.info("creating tables and inserting values...");
		String create1 = "CREATE TABLE IF NOT EXISTS uni (id INTEGER not NULL, name VARCHAR(255), city VARCHAR(255), PRIMARY KEY ( id ))";
		stmt.execute(create1);
		String create2 = "CREATE TABLE IF NOT EXISTS student (id INTEGER not NULL, name VARCHAR(255), age INTEGER, studiesAt INTEGER, PRIMARY KEY ( id ), FOREIGN KEY (studiesAt) REFERENCES uni(id))";
		stmt.execute(create2);

		// insert values to tables
		List<String> inserts = new ArrayList<String>(5);
		inserts.add("INSERT INTO uni (id, name, city) VALUES (1, \"Universität\", \"Koblenz\");");
		inserts.add("INSERT INTO uni (id, name, city) VALUES (2, \"Universidade Nova\", \"Lisboa\");");
		inserts.add("INSERT INTO uni (id, name, city) VALUES (3, \"Univerzitnú\", \"Brno\");");
		inserts.add("INSERT INTO student (id, name, age, studiesAt) VALUES (1, \"Jonas\", 25, 1);");
		inserts.add("INSERT INTO student (id, name, age, studiesAt) VALUES (2, \"Dusan\", 23, 3);");

		for (String insert : inserts)
			stmt.execute(insert);
		log.info("inserted successfully.");
	}

	private void checkConnected() {
		if (this.connection == null)
			throw new RuntimeException("Database not connected. Call connect() before!");
	}

	public void createTestDatabase() throws SQLException {
		connect(DB_URL, USER, PASS);
		createOrReplaceDatabase(DB_NAME);
		createExampleData(DB_NAME);
		close();
	}
}
