package md;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import md.beans.Column;
import md.beans.DimensionalModel;
import md.beans.Table;

// @formatter:on
/*
 * our rules for classifying the tables: 1. transaction: roots of
 * table-graph 2. components: 1st order neighbours of transactions 3.
 * classification: all resuming tables will be collapsed as a hierarchy
 */
// @formatter:off

// - find opportunities for grouping attributes and aggregateable
// grouping attributes:
// aggregateable: numeric values
// Aggregation function: SUM, MEAN
public class Suggestor {

	public static class TransactionSuggestion {
		private List<Table> transactions;
		private List<Table> unclassified;

		public TransactionSuggestion(List<Table> transactions, List<Table> unclassified) {
			this.transactions = transactions;
			this.unclassified = unclassified;
		}

		public List<Table> getTransactions() {
			return transactions;
		}

		public void setTransactions(List<Table> transactions) {
			this.transactions = transactions;
		}

		public List<Table> getUnclassified() {
			return unclassified;
		}

		public void setUnclassified(List<Table> unclassified) {
			this.unclassified = unclassified;
		}

	}

	public static TransactionSuggestion makeTransactionSuggestion(final List<Table> oltp) {

		List<Table> roots = new ArrayList<Table>();
		for (Table t : oltp) {
			if (t.getnExportedKeys() == 0)
				roots.add(t);
		}
		List<Table> remains = new ArrayList<Table>(oltp);
		for (Table t : roots) {
			remains.remove(t);
		}
		Collections.sort(remains, new Comparator<Table>() {

			@Override
			public int compare(Table t1, Table t2) {
				// sort the table in a way that the most likely transaction
				// tables appear in the front
				return getForeignKeyRatio(t2) - getForeignKeyRatio(t1);
			}

			private int getForeignKeyRatio(Table table) {
				return table.getnImportedKeys() - table.getnExportedKeys();
			}

		});

		// TODO add also graph meeting points between roots to transactions

		return new TransactionSuggestion(roots, remains);
	}

	public static List<DimensionalModel> makeStarPeakSuggestion(final List<Table> oltp,
			final TransactionSuggestion transactionsFixed) {
		// TODO
		List<DimensionalModel> ret = new ArrayList<>();

		for (Table trans : transactionsFixed.getTransactions()) {
			DimensionalModel model = new DimensionalModel();
			model.addTransactionTable(trans);
			
			//TODO remove those who are not reachable
			model.setUnclassifiedTables(new ArrayList<>(transactionsFixed.getUnclassified()));
			
			ret.add(model);
		}

		return ret;
	}

	private Collection<Table> getReferencedTables(Collection<Table> oltp, Table root) {
		List<Column> cols = root.getCols();
		if (cols == null)
			return new ArrayList<Table>(0);

		ArrayList<Table> ret = new ArrayList<Table>();
		for (Column col : cols) {
			if (col.isForeignKey()) {
				String referencedTableName = col.getForeignKeyTable();
				for (Table t : oltp) {
					if (t.getName().equals(referencedTableName)) {
						ret.add(t);
						break;
					}
				}
			}
		}
		return ret;
	}
}
