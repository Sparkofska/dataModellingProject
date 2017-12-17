package md.interaction;

import md.AggTableEdit;

public abstract class AggregationCommand extends Command {

	private AggTableEdit edit;
	private String columnName;
	private String formula;

	public AggregationCommand(AggTableEdit edit, String columnName) {
		this.edit = edit;
		this.columnName = columnName;
	}

	public AggregationCommand(AggTableEdit edit, String columnName, String formula) {
		super();
		this.edit = edit;
		this.columnName = columnName;
		this.formula = formula;
	}

	public AggTableEdit getEdit() {
		return edit;
	}

	public void setEdit(AggTableEdit edit) {
		this.edit = edit;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}
}
