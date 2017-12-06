package md.interaction;

import md.beans.TransactionSuggestion;

public class RemoveTransactionTable extends AddTransactionTable {

	public RemoveTransactionTable(TransactionSuggestion edit, String tableName) {
		super(edit, tableName);
	}

	@Override
	public void execute() {
		removeTransaction();
	}

	@Override
	public void undo() {
		addTransaction();
	}
}
