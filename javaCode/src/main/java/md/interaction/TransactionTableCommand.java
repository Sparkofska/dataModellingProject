package md.interaction;

import md.beans.TransactionSuggestion;

public abstract class TransactionTableCommand extends Command {

	protected TransactionSuggestion model;
	protected String tableName;

	public TransactionTableCommand(TransactionSuggestion model, String tableName) {
		this.model = model;
		this.tableName = tableName;
	}

	protected void addTransaction() {
		model.moveToTransaction(this.tableName);
	}

	protected void removeTransaction() {
		model.moveToUnclassified(this.tableName);
	}

}