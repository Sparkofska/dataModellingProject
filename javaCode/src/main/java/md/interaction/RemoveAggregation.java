package md.interaction;

import md.AggTableEdit;

public class RemoveAggregation extends AggregationCommand {

	public RemoveAggregation(AggTableEdit edit, String columnName) {
		super(edit, columnName);
	}

	@Override
	public void execute() {
		String formula = getEdit().removeAggregation(getColumnName());
		setFormula(formula); // backup for undo
	}

	@Override
	public void undo() {
		if(getFormula() == null)
			throw new RuntimeException("Command cannot be undone before execute() was called.");
		
		getEdit().addAggregation(getColumnName(), getFormula());
	}
}
