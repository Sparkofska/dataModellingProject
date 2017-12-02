package md.beans;

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

	public void setName(String name) {
		this.name = name;
	}

	public List<Column> getCols() {
		return cols;
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
