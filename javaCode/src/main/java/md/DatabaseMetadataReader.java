package md;

import java.io.PrintStream;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import md.beans.Column;
import md.beans.Table;

public class DatabaseMetadataReader extends DatabaseAccessor {

	private Logger log = Logger.getInstance();

	public DatabaseMetadataReader(String dbUrl, String user, String password) {
		super(dbUrl, user, password);
	}

	public List<Table> doit(final String dbName) throws SQLException {
		List<Table> metadata = getTables(dbName);

		for (Table table : metadata) {
			List<Column> cols = getColumns(dbName, table);
			List<String> keys = getPrimaryKeys(dbName, table);
			handlePks(cols, keys);
			
			// TODO foreign keys
			
			table.setCols(cols);
		}

		return metadata;
	}

	private void handlePks(List<Column> cols, List<String> keys) {
		for(Column col:cols)
		{
			for(String pk:keys)
			{
				if(col.getName().equals(pk)){
					col.setPrimaryKey(true);
					break;
				}
			}
		}
	}

	public List<Table> getTables(final String dbName) throws SQLException {
		connect();
		DatabaseMetaData dbmd = getConnection().getMetaData();

		ResultSet rs = dbmd.getTables(dbName, null, "", new String[] { "TABLE" });// VIEWS
		ResultSetMetaData rsmd = rs.getMetaData();

		int columnCount = rsmd.getColumnCount();
		// log.debug("fetchSize: " + rs.getFetchSize());
		// log.debug("columnCount: " + columnCount);

		List<Table> tables = new ArrayList<Table>();
		while (rs.next()) {
			Table table = new Table();
			boolean valid = false;
			for (int i = 1; i <= columnCount; i++) {
				String label = rsmd.getColumnLabel(i);
				Object value = rs.getObject(i);

				if (label != null && label.equals("TABLE_NAME")) {
					table.setName(value != null ? value.toString() : "isNull");
					valid = true;
				}
			}
			if (valid)
				tables.add(table);
		}
		close();
		return tables;
	}

	public List<Column> getColumns(final String dbName, final Table table) throws SQLException {
		connect();
		DatabaseMetaData dbmd = getConnection().getMetaData();

		ResultSet rs = dbmd.getColumns(dbName, null, table.getName(), "");
		ResultSetMetaData rsmd = rs.getMetaData();

		int columnCount = rsmd.getColumnCount();

		List<Column> cols = new ArrayList<Column>();
		while (rs.next()) {

			Column col = new Column();
			int valid = 0;

			for (int i = 1; i <= columnCount; i++) {
				String label = rsmd.getColumnLabel(i);
				Object value = rs.getObject(i);

				if (label != null && label.equals("COLUMN_NAME")) {
					valid++;
					col.setName(value != null ? value.toString() : "isNull");
				}
				if (label != null && label.equals("TYPE_NAME")) {
					valid++;
					col.setType(value != null ? value.toString() : "isNull");
				}
			}

			if (valid == 2)
				cols.add(col);
		}

		close();
		return cols;
	}

	public List<String> getPrimaryKeys(final String dbName, final Table table) throws SQLException {
		connect();
		DatabaseMetaData dbmd = getConnection().getMetaData();

		ResultSet rs = dbmd.getPrimaryKeys(dbName, null, table.getName());
		ResultSetMetaData rsmd = rs.getMetaData();

		int columnCount = rsmd.getColumnCount();

		List<String> keys = new ArrayList<String>();
		while (rs.next()) {

			int valid = 0;

			for (int i = 1; i <= columnCount; i++) {
				String label = rsmd.getColumnLabel(i);
				Object value = rs.getObject(i);

//				log.debug("label=" + label + " : object=" + (value != null ? value.toString() : "isNULL"));
				if (label != null && label.equals("COLUMN_NAME")) {
					valid++;
					keys.add(value != null ? value.toString() : "isNull");
				}
				if (label != null && label.equals("KEY_SEQ")) {
					// may be important some day
				}
				if (label != null && label.equals("PK_NAME")) {
					// may be important some day
				}
			}
		}

		close();
		return keys;
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

	public void example() throws SQLException {
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
