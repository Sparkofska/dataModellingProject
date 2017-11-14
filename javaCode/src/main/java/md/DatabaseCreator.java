package md;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseCreator extends DatabaseAccessor {

	private Logger log = Logger.getInstance();

	public DatabaseCreator(String dbUrl, String user, String password) {
		super(dbUrl, user, password);
	}

	public void createTestDatabase(final String dbName) throws SQLException {
		connect();
		createOrReplaceDatabase(dbName);
		createExampleData(dbName);
		close();
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
}
