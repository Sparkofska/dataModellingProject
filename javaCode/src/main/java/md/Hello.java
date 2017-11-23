package md;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import md.CredentialParser.Credentials;
import md.beans.Column;
import md.beans.Table;

public class Hello {

	protected static final String DB_URL = "jdbc:mysql://localhost/";
	protected static final String DB_NAME = "testdb";
	private static final String CREDENTIAL_FILE_PATH = "/home/jonas/M/Uni/10Semester/dataModelling/project/javaCode/src/main/java/md/credentials.txt";

	public static void main(String[] args) {
		System.out.println("Hello World!");
		Credentials credentials = new CredentialParser(new File(CREDENTIAL_FILE_PATH)).doMagic();

		/*
		 * DatabaseCreator dbc; try { dbc = new DatabaseCreator(DB_URL, USER,
		 * PASS); dbc.createTestDatabase(DB_NAME); } catch (SQLException e) {
		 * e.printStackTrace(); } catch (RuntimeException e) {
		 * e.printStackTrace(); }
		 */

		try {
			// new DatabaseMetadataReader(DB_URL, credentials.username,
			// credentials.password).doit();
			
			StringBuilder sb = new StringBuilder();

			DatabaseMetadataReader databaseMetadataReader = new DatabaseMetadataReader(DB_URL, credentials.username,
					credentials.password);
			List<Table> tables = databaseMetadataReader.getTables(DB_NAME);
			for (Table t : tables) {

				sb.append(t.getName()+"\n");
				List<Column> cols = databaseMetadataReader.getColumns(t);
				for (Column col : cols) {
					sb.append("\t" + col.getName() + " [" + col.getType() + "]\n");
				}
				
				//TODO pks not working yet
//				System.out.println("PRIMARY KEYS " + t.getName());
//				databaseMetadataReader.getPrimaryKeys(t);
			}
			
			System.out.println(sb);

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}
}
