package md;

import java.util.ArrayList;
import java.util.List;

import md.beans.Column;
import md.beans.Table;

public class AggTableEdit {

	private List<Column> unclassified;
	private List<Column> aggKeys;
	private List<String> aggFormulas;

	public AggTableEdit(Table table) {
		this.setUnclassified(table.getCols());
	}

	public void addAggregation(String keyColName, String fomula) {
		for (Column col : getUnclassified()) {
			if (col.getName().equals(keyColName)) {
				getUnclassified().remove(col);
				getAggKeys().add(col);
				getAggFormulas().add(fomula);
				return;
			}
		}
		throw new IllegalArgumentException("A column with the name '" + keyColName + "' does not exist in the table");
	}

	public String removeAggregation(String keyColName) {
		int i = 0;
		for (Column col : getAggKeys()) {
			if (col.getName().equals(keyColName)) {
				getAggKeys().remove(i);
				String formula = getAggFormulas().remove(i);
				getUnclassified().add(col);
				return formula;
			}
			i++;
		}
		throw new IllegalArgumentException("A column with the name '" + keyColName + "' does not exist in the table");
	}

	public List<Column> getUnclassified() {
		if (this.unclassified == null)
			this.unclassified = new ArrayList<>();
		return unclassified;
	}

	public void setUnclassified(List<Column> unclassfied) {
		this.unclassified = unclassfied;
	}

	public List<Column> getAggKeys() {
		if (this.aggKeys == null)
			this.aggKeys = new ArrayList<>();
		return aggKeys;
	}

	public void setAggKeys(List<Column> aggKeys) {
		this.aggKeys = aggKeys;
	}

	public List<String> getAggFormulas() {
		if (this.aggFormulas == null)
			this.aggFormulas = new ArrayList<>();
		return aggFormulas;
	}

	public void setAggFormulas(List<String> addFormulas) {
		this.aggFormulas = addFormulas;
	}

}
