package md;

import java.sql.SQLException;

public class Hello {

	public static void main(String[] args) {
		System.out.println("Hello World!");

		DatabaseCreator dbc;
		try {
			dbc = new DatabaseCreator();
			dbc.createTestDatabase();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

}
