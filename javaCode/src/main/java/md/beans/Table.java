package md.beans;

import java.util.ArrayList;
import java.util.List;

public class Table {

	private String name;
	private List<Column> cols;
	/**
	 * number of columns that reference another table
	 */
	private int nImportedKeys;

	/**
	 * number of tables that are referencing this table
	 */
	private int nExportedKeys;

	public String getName() {
		return name;
	}

	public String getColsNames() {
	    String listOfCols = "";
		for (Column col: this.cols){
			listOfCols += col.getName() + "\n";
		}
		return listOfCols;
	}

	public Table duplicateTable(Table tableToDuplicate){
		Table duplicate= new Table();
		duplicate.setName(tableToDuplicate.getName());

		List<Column> clonedColumns = new ArrayList<>();
		for (Column col : tableToDuplicate.getCols()) {
			try{
				clonedColumns.add(Column.class.cast(col.clone()));
			}
			catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}
		duplicate.setCols(clonedColumns);
		return duplicate;
	}

	public List<Column> getFKColumns(){
	    List<Column> fkCols= new ArrayList<>();
		for (Column col: this.cols){
			if (col.isForeignKey()) {
				try {
					fkCols.add(Column.class.cast(col.clone()));
				} catch (Exception e) {
				}
			}
		}
		return fkCols;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Column> getCols() {
		return cols;
	}

	public Column getColumnByName(String colName) throws Exception {
	    if (colName == null) return null;
		for (Column col: this.getCols()){
			if (col.getName().equals(colName)){
				return col;
			}
		}
		throw new Exception("Column " + colName + " is not present in table " + this.getName());
	}

	public void setCols(List<Column> cols) {
		this.cols = cols;
	}

	public int getnImportedKeys() {
		return nImportedKeys;
	}

	public void setnImportedKeys(int nImportedKeys) {
		this.nImportedKeys = nImportedKeys;
	}

	public int getnExportedKeys() {
		return nExportedKeys;
	}

	public void setnExportedKeys(int nExportedKeys) {
		this.nExportedKeys = nExportedKeys;
	}
	
	public void addnExportedKey()
	{
		this.nExportedKeys++;
	}
}
