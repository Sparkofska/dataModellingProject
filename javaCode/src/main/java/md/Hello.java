package md;

import java.sql.SQLException;
import java.util.List;

import md.beans.Table;

public class Hello {
	
	protected static final String DB_URL = "jdbc:mysql://localhost/";
	protected static final String DB_NAME = "testdb";
	protected static final String USER = "root";
	protected static final String PASS = ".noihkm.";

	public static void main(String[] args) {
		System.out.println("Hello World!");
		
		/*
		DatabaseCreator dbc;
		try {
			dbc = new DatabaseCreator(DB_URL, USER, PASS);
			dbc.createTestDatabase(DB_NAME);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		catch (RuntimeException e) {
			e.printStackTrace();
		}
		*/
 
		try {
			//new DatabaseMetadataReader(DB_URL, USER, PASS).doit();
			
			List<Table> tables = new DatabaseMetadataReader(DB_URL, USER, PASS).getTables(DB_NAME);
			for(Table t:tables)
			{
				System.out.println(t.getName());
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}
}
