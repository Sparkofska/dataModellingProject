package md.beans;

import java.util.ArrayList;
import java.util.List;

public class DimensionalModel implements Cloneable {

	private List<Table> transactionTables;
	private List<Table> componentTables;
	private List<Table> classificationTables;
	private List<Table> unclassifiedTables;

	public DimensionalModel() {
	}

	/**
	 * copy constructor. Be careful: Lists are cloned but not their elements.
	 */
	public DimensionalModel(DimensionalModel orig) {
		this.transactionTables = new ArrayList<>(orig.getTransactionTables());
		this.componentTables = new ArrayList<>(orig.getComponentTables());
		this.classificationTables = new ArrayList<>(orig.getClassificationTables());
		this.unclassifiedTables = new ArrayList<>(orig.getUnclassifiedTables());
	}

	/**
	 * Retrieves and removes the specified table from this model. Assuming all
	 * tables in the model have distinct names; Otherwise the first occurrence
	 * of the specified name will be retrieved.
	 * 
	 * @param tableName
	 * @return The removed table. NULL if no table having the given name is
	 *         present in this model.
	 */
	public Table pollTableByName(String tableName) {
		List<List<Table>> allTables = new ArrayList<>(4);
		allTables.add(getTransactionTables());
		allTables.add(getComponentTables());
		allTables.add(getClassificationTables());
		allTables.add(getUnclassifiedTables());

		for (List<Table> list : allTables) {
			Table ret = null;
			for (Table t : list)
				if (t.getName().equals(tableName)) {
					ret = t;
					break;
				}
			if (ret != null) {
				list.remove(ret);
				return ret;
			}
		}
		return null;
	}

	public void setTransactionTables(List<Table> transactionTables) {
		this.transactionTables = transactionTables;
	}

	public void addTransactionTable(Table transactionTable) {
		getTransactionTables().add(transactionTable);
	}

	public List<Table> getTransactionTables() {
		if (this.transactionTables == null)
			this.transactionTables = new ArrayList<>();
		return transactionTables;
	}

	public List<Table> getComponentTables() {
		if (this.componentTables == null)
			this.componentTables = new ArrayList<>();
		return componentTables;
	}

	public void addComponentTable(Table table) {
		getComponentTables().add(table);
	}

	public void setComponentTables(List<Table> componentTables) {
		this.componentTables = componentTables;
	}

	public List<Table> getClassificationTables() {
		if (this.classificationTables == null)
			this.classificationTables = new ArrayList<>();
		return classificationTables;
	}

	public void addClassificationTable(Table table) {
		getClassificationTables().add(table);
	}

	public void setClassificationTables(List<Table> classificationTables) {
		this.classificationTables = classificationTables;
	}

	public List<Table> getUnclassifiedTables() {
		if (this.unclassifiedTables == null)
			this.unclassifiedTables = new ArrayList<>();
		return unclassifiedTables;
	}

	public void addUnclassifiedTable(Table table) {
		getUnclassifiedTables().add(table);
	}

	public void setUnclassifiedTables(List<Table> unclassifiedTables) {
		this.unclassifiedTables = unclassifiedTables;
	}

}
