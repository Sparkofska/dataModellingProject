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

	@SuppressWarnings("unused")
	private Logger log = Logger.getInstance();

	public DatabaseMetadataReader(String dbUrl, String user, String password) {
		super(dbUrl, user, password);
	}

	public List<Table> getMetadata(final String dbName) throws SQLException {
		connect();

		List<Table> metadata = getTables(dbName);

		for (Table table : metadata) {

			List<Column> cols = getColumns(dbName, table);
			List<String> primKeys = getPrimaryKeys(dbName, table);
			List<ForeignKey> imKeys = getImportedKeys(dbName, table);
			List<String> exKeys = getExportedKeys(dbName, table);

			for (Column col : cols) {
				// handle primary keys
				for (String pk : primKeys) {
					if (col.getName().equals(pk)) {
						col.setPrimaryKey(true);
						break;
					}
				}

				// handle foreign keys
				for (ForeignKey fk : imKeys) {
					if (col.getName().equals(fk.getColname())) {
						col.setForeignKey(true);
						col.setForeignKeyTable(fk.getReferencingTable());
						col.setForeignKeyColumn(fk.getReferencingColumn());
					}
				}

			}
			table.setCols(cols);
			table.setnExportedKeys(exKeys.size());
			table.setnImportedKeys(imKeys.size());
		}
		close();
		return metadata;
	}

	private List<Table> getTables(final String dbName) throws SQLException {
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
		return tables;
	}

	private List<Column> getColumns(final String dbName, final Table table) throws SQLException {
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
				} else if (label != null && label.equals("TYPE_NAME")) {
					valid++;
					if (value == null){
						col.setType("isNull");
					}
					else{
						if (value.equals("VARCHAR")){
							col.setType(value+"(255)");
						}
						else{
							col.setType(value.toString());
						}
					}
				}
			}

			if (valid == 2)
				cols.add(col);
		}

		return cols;
	}

	private List<String> getPrimaryKeys(final String dbName, final Table table) throws SQLException {
		DatabaseMetaData dbmd = getConnection().getMetaData();

		ResultSet rs = dbmd.getPrimaryKeys(dbName, null, table.getName());
		ResultSetMetaData rsmd = rs.getMetaData();

		int columnCount = rsmd.getColumnCount();

		List<String> keys = new ArrayList<String>();
		while (rs.next()) {

			for (int i = 1; i <= columnCount; i++) {
				String label = rsmd.getColumnLabel(i);
				Object value = rs.getObject(i);

				// log.debug("label=" + label + " : object=" + (value != null ?
				// value.toString() : "isNULL"));
				if (label != null && label.equals("COLUMN_NAME")) {
					keys.add(value != null ? value.toString() : "isNull");
				} else if (label != null && label.equals("KEY_SEQ")) {
					// may be important some day
				} else if (label != null && label.equals("PK_NAME")) {
					// may be important some day
				}
			}
		}

		return keys;
	}

	private List<ForeignKey> getImportedKeys(final String dbName, final Table table) throws SQLException {
		DatabaseMetaData dbmd = getConnection().getMetaData();

		ResultSet rs = dbmd.getImportedKeys(dbName, null, table.getName());
		ResultSetMetaData rsmd = rs.getMetaData();

		int columnCount = rsmd.getColumnCount();

		List<ForeignKey> keys = new ArrayList<ForeignKey>();
		while (rs.next()) {

			ForeignKey fk = new ForeignKey();
			int valid = 0;

			for (int i = 1; i <= columnCount; i++) {
				String label = rsmd.getColumnLabel(i);
				Object value = rs.getObject(i);

				// log.debug("label=" + label + " : object=" + (value != null ?
				// value.toString() : "isNULL"));
				if (label != null && label.equals("PKTABLE_NAME")) {
					valid++;
					fk.setReferencingTable(value != null ? value.toString() : "isNull");
				} else if (label != null && label.equals("PKCOLUMN_NAME")) {
					valid++;
					fk.setReferencingColumn(value != null ? value.toString() : "isNull");
				} else if (label != null && label.equals("FKCOLUMN_NAME")) {
					valid++;
					fk.setColname(value != null ? value.toString() : "isNull");
				}
			}
			if (valid == 3)
				keys.add(fk);
		}

		return keys;
	}

	private List<String> getExportedKeys(final String dbName, final Table table) throws SQLException {
		DatabaseMetaData dbmd = getConnection().getMetaData();

		ResultSet rs = dbmd.getExportedKeys(dbName, null, table.getName());
		ResultSetMetaData rsmd = rs.getMetaData();

		int columnCount = rsmd.getColumnCount();

		List<String> keys = new ArrayList<>();
		while (rs.next()) {

			for (int i = 1; i <= columnCount; i++) {
				String label = rsmd.getColumnLabel(i);
				Object value = rs.getObject(i);

				// log.debug("label=" + label + " : object=" + (value != null ?
				// value.toString() : "isNULL"));
				if (label != null && label.equals("PKTABLE_NAME")) {
					keys.add(value != null ? value.toString() : "isNull");
					break;
				}
			}
		}

		return keys;
	}

	private class ForeignKey {
		private String colname;
		private String referencingTable;
		private String referencingColumn;

		public String getColname() {
			return colname;
		}

		public void setColname(String colname) {
			this.colname = colname;
		}

		public String getReferencingTable() {
			return referencingTable;
		}

		public void setReferencingTable(String referencingTable) {
			this.referencingTable = referencingTable;
		}

		public String getReferencingColumn() {
			return referencingColumn;
		}

		public void setReferencingColumn(String referencingColumn) {
			this.referencingColumn = referencingColumn;
		}
	}

	// ---------------------------------------------------------
	static private void dumpResultSet(ResultSet rs) throws SQLException {
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
