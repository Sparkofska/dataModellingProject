package md;

import java.io.PrintStream;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import md.beans.Table;

public class DatabaseMetadataReader extends DatabaseAccessor {

	private Logger log = Logger.getInstance();

	public DatabaseMetadataReader(String dbUrl, String user, String password) {
		super(dbUrl, user, password);
	}

	public List<Table> getTables(final String dbName) throws SQLException {
		connect();
		DatabaseMetaData dbmd = getConnection().getMetaData();

		ResultSet rs = dbmd.getTables(dbName, null, "", new String[] { "TABLE" }); // "VIEWS
		ResultSetMetaData rsmd = rs.getMetaData();

		int columnCount = rsmd.getColumnCount();
//		log.debug("fetchSize: " + rs.getFetchSize());
//		log.debug("columnCount: " + columnCount);

		List<Table> tables = new ArrayList<Table>();
		while (rs.next()) {
			for (int i = 1; i <= columnCount; i++) {
				String label = rsmd.getColumnLabel(i);
				Object value = rs.getObject(i);

				Table table = new Table();
				if (label != null && label.equals("TABLE_NAME")) {
					table.setName(value != null ? value.toString() : "isNull");
				}
/*
				log.debug("\n" + label);
				log.debug(value != null ? value.toString() : "null");
*/
				tables.add(table);
			}
		}

		return tables;
	}

	// ---------------------------------------------------------
	static void dumpResultSet(ResultSet rs) throws SQLException {
		ResultSetMetaData md = rs.getMetaData();
		int columnCount = md.getColumnCount();
		PrintStream out = System.out;
		while (rs.next()) {
			out.print("{\n");
			for (int i = 1; i <= columnCount; i++) {
				out.print("    ");
				out.print(md.getColumnLabel(i));
				out.print(": ");
				out.print(rs.getObject(i));

				if (i < columnCount - 1) {
					out.print(", ");
				}
				out.print("\n");
			}
			out.print("}\n");
		}
	}

	public void doit() throws SQLException {
		connect();
		DatabaseMetaData md = getConnection().getMetaData();

		dumpResultSet(md.getTables("testdb", null, "", new String[] { "TABLE", "VIEW" }));
		dumpResultSet(md.getColumns(null, null, "uni", ""));
		dumpResultSet(md.getExportedKeys(null, null, "TABLE_NAME"));
		dumpResultSet(md.getImportedKeys(null, null, "TABLE_NAME"));
		dumpResultSet(md.getPrimaryKeys(null, null, "TABLE_NAME"));
		dumpResultSet(md.getIndexInfo(null, null, "TABLE_NAME", false, true));
	}

}
