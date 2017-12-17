package md.interaction;

import md.AggTableEdit;

public class AddAggregation extends AggregationCommand {

	public AddAggregation(AggTableEdit edit, String columnName, String formula) {
		super(edit, columnName, formula);
	}

	@Override
	public void execute() {
		getEdit().addAggregation(getColumnName(), getFormula());
	}

	@Override
	public void undo() {
		getEdit().removeAggregation(getColumnName());
	}
}
