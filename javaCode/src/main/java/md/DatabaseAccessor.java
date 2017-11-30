package md;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseAccessor {

	private String dbUrl;
	private String user;
	private String password;
	protected Connection connection = null;

	private Logger log = Logger.getInstance();

	public DatabaseAccessor(final String dbUrl, final String user, final String password) {
		this.dbUrl = dbUrl;
		this.user = user;
		this.password = password;
	}

	protected void connect() throws SQLException {
		if (this.connection != null)
			return;
//		log.info("Connecting to database...");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		this.connection = DriverManager.getConnection(this.dbUrl, this.user, this.password);
//		if (this.connection != null)
//			log.info("connected successfully.");
	}

	protected void close() throws SQLException {
		if (this.connection != null)
			this.connection.close();
		this.connection = null;
	}

	protected void checkConnected() {
		if (this.connection == null)
			throw new RuntimeException("Database not connected. Call connect() before!");
	}

	protected Connection getConnection() {
		return this.connection;
	}
}