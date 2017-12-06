package md.beans;

import java.util.ArrayList;
import java.util.List;

public class TransactionSuggestion {
	private List<Table> transactions;
	private List<Table> unclassified;

	public TransactionSuggestion(List<Table> transactions, List<Table> unclassified) {
		this.transactions = transactions;
		this.unclassified = unclassified;
	}

	/**
	 * copy constructor
	 */
	public TransactionSuggestion(TransactionSuggestion orig) {
		this(new ArrayList<Table>(orig.getTransactions()), new ArrayList<Table>(orig.getUnclassified()));
	}

	public List<Table> getTransactions() {
		if (this.transactions == null)
			this.transactions = new ArrayList<>();
		return transactions;
	}

	public void setTransactions(List<Table> transactions) {
		this.transactions = transactions;
	}

	public List<Table> getUnclassified() {
		if (this.unclassified == null)
			this.unclassified = new ArrayList<>();
		return unclassified;
	}

	public void setUnclassified(List<Table> unclassified) {
		this.unclassified = unclassified;
	}

	public void moveToTransaction(String tableName) {
		for (Table t : getUnclassified()) {
			if (t.getName().equals(tableName)) {
				this.unclassified.remove(t);
				this.transactions.add(t);
				return;
			}
		}
		throw new IllegalArgumentException(
				"A table with the specified name '" + tableName + "' does not exist in the unclassified tables.");
	}

	public void moveToUnclassified(String tableName) {
		for (Table t : getTransactions()) {
			if (t.getName().equals(tableName)) {
				this.transactions.remove(t);
				this.unclassified.add(t);
				return;
			}
		}
		throw new IllegalArgumentException(
				"A table with the specified name '" + tableName + "' does not exist in the transaction tables.");
	}
}
