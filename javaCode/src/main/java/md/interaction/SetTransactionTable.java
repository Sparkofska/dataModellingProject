package md.interaction;

import md.beans.DimensionalModel;
import md.beans.Table;

public class SetTransactionTable extends Command {

	private DimensionalModel model;
	private String tableName;

	public SetTransactionTable(DimensionalModel model, String tableName) {
		this.model = model;
		this.tableName = tableName;
	}

	@Override
	public void execute() {
		Table t = model.pollTableByName(tableName);
		if (t == null)
			throw new IllegalArgumentException(
					"A table with the specified name '" + tableName + "' does not exist in the model.");
		model.addTransactionTable(t);
	}

	@Override
	public void undo() {
		// TODO This is not really an undo. An real undo should memorize the
		// origin of the table.
		Table t = model.pollTableByName(tableName);
		if (t == null)
			throw new IllegalArgumentException(
					"A table with the specified name '" + tableName + "' does not exist in the model.");
		model.addUnclassifiedTable(t);
	}
}
