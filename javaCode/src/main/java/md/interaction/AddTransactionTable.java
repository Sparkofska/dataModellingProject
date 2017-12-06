package md.interaction;

import md.beans.TransactionSuggestion;

public class AddTransactionTable extends TransactionTableCommand {

	public AddTransactionTable(TransactionSuggestion model, String tableName) {
		super(model, tableName);
	}

	@Override
	public void execute() {
		addTransaction();
	}

	@Override
	public void undo() {
		removeTransaction();
	}
}
