package md;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import md.beans.Column;
import md.beans.DimensionalModel;
import md.beans.Table;
import md.beans.TransactionSuggestion;

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

		List<DimensionalModel> ret = new ArrayList<>();

		for (Table trans : transactionsFixed.getTransactions()) {
			DimensionalModel model = new DimensionalModel();
			model.addTransactionTable(trans);

			// remove those who are not reachable
			Collection<Table> reachable = getReachableTables(oltp, trans);

			// find direct neighbours of transactions
			Collection<Table> componentCandidates = getReferencedTables(reachable, trans);
			for (Table cand : componentCandidates) {
				model.addComponentTable(cand);
			}

			// rest is Classification
			Set<Table> rest = makeRelativeComplement(reachable, componentCandidates);
			for (Table classific : rest) {
				model.addClassificationTable(classific);
			}

			ret.add(model);
		}
		return ret;
	}

	/**
	 * Creates a copy of set which misses the elements of subSet. In other
	 * words: Removes all the elements of subSet from set leaving the original
	 * set untouched.
	 */
	private static Set<Table> makeRelativeComplement(final Collection<Table> set, final Collection<Table> subSet) {
		Set<Table> ret = new HashSet<>(set);
		for (Table sub : subSet)
			ret.remove(sub);
		return ret;
	}

	/**
	 * Retrieves all Tables from oltp (without altering oltp itself) that are
	 * reachable from root by a chain of foreign keys. Excluding root itself.
	 */
	private static Set<Table> getReachableTables(final Collection<Table> oltp, final Table root) {
		Set<Table> ret = new HashSet<>();
		for (Table child : getReferencedTables(oltp, root)) {
			if (!ret.contains(child)) {
				ret.add(child);
				ret.addAll(getReachableTables(oltp, child));
			}
		}
		return ret;
	}

	/**
	 * Retrieves all Tables from oltp (without altering oltp itself) that are
	 * directly referenced by foreign keys of root. Excluding root itself.
	 */
	private static Collection<Table> getReferencedTables(final Collection<Table> oltp, final Table root) {
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
