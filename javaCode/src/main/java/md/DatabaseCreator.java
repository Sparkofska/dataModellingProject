package md;

import md.beans.CollapsedTable;
import md.beans.HierarchyTable;
import md.beans.Table;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
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

	public void collapseAndMigrate(HierarchyTable table, final String dbName)throws SQLException{
		connect();
		checkConnected();

		Statement stmt = this.connection.createStatement();
		Statement insertStmt = this.connection.createStatement();

		String use = "USE " + dbName;
		stmt.execute(use);
		insertStmt.execute(use);
		stmt.execute(table.getSqlCreate());
		ResultSet rs = stmt.executeQuery(table.getSqlSelect());

		ResultSetMetaData rsmd = rs.getMetaData();
		int colNumber = rsmd.getColumnCount();
		while (rs.next()){
			String insertValues="";
			for (int i = 1; i<=colNumber; i++){
				if (!insertValues.equals("")) insertValues+=", ";
				insertValues+="\"" + rs.getString(i) + "\"";
			}
			String insertQuery = table.getSqlInsert() + " VALUES(" + insertValues +");";
			try {
				insertStmt.execute(insertQuery);
			}
			catch (SQLIntegrityConstraintViolationException e){
				continue;
			}
		}

		close();
	}

	public void executeQuery(final String query, final String dbName) throws SQLException{
		connect();
		checkConnected();
		Statement stmt = this.connection.createStatement();
		String use = "USE " + dbName;
		stmt.execute(use);
		stmt.execute(query);
		close();

	}

	/*public void executeSelectQuery(final String query, HierarchyTable targetTable, final String dbName) throws SQLException{
		connect();
		checkConnected();
		Statement stmt = this.connection.createStatement();
		Statement insertStmt = this.connection.createStatement();
		String use = "USE " + dbName;
		System.out.print("Executing\n");
		stmt.execute(use);
		insertStmt.execute(use);
		ResultSet rs = stmt.executeQuery(query);
		ResultSetMetaData rsmd = rs.getMetaData();
		System.out.print("*********DONE*****\n");
		int colNumber = rsmd.getColumnCount();
		System.out.print(colNumber+"\n");
		while (rs.next()){
			String insertValues="";
			System.out.print("*********iiii*****\n");
		    for (int i = 1; i<=colNumber; i++){
		    	if ( i>1) System.out.print(", ");
		    	System.out.print(rs.getString(i) + " " + rsmd.getColumnName(i));
		    	if (!insertValues.equals("")) insertValues+=", ";
		    	insertValues+="\"" + rs.getString(i) + "\"";
			}
			System.out.print("\n");
			String insertQuery = "INSERT INTO " + targetTable.getCollapsedTable().getName() + " VALUES(" + insertValues +");";
			System.out.print("Executing insert \n");
			System.out.print(insertQuery + "\n");
			insertStmt.execute(insertQuery);
		}

		close();

	}*/

	public void createTestDatabaseMoody(final String dbName) throws SQLException, FileNotFoundException, IOException {
		connect();
		createOrReplaceDatabase(dbName);
		createExampleDataMoody(dbName);
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

	private void createExampleDataMoody(final String dbName) throws SQLException, FileNotFoundException, IOException {

		// parse sql file
		File sqlFile = new File("../sqlScripts/createMoodyDb.sql");
		StringBuilder sqlStatements = new StringBuilder();
		BufferedReader fileReader = new BufferedReader(new FileReader(sqlFile));
		String line;
		while ((line = fileReader.readLine()) != null) {
			sqlStatements.append(line);
		}
		fileReader.close();
		
		//execute commands from file
		checkConnected();
		Statement stmt = this.connection.createStatement();
		String use = "USE " + dbName;
		stmt.execute(use);

		// create tables in database
		log.info("creating tables and inserting values...");
		for (String command : sqlStatements.toString().split(";")) {
			command += ";";
			stmt.execute(command);
		}

		// insert values to tables
		List<String> inserts = new ArrayList<String>(5);
//		inserts.add("INSERT INTO uni (id, name, city) VALUES (1, \"Universität\", \"Koblenz\");");

		for (String insert : inserts)
			stmt.execute(insert);
		log.info("inserted successfully.");

	}
}
