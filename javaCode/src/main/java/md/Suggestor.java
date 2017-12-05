package md;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import md.beans.DimensionalModel;
import md.beans.Table;

public class Suggestor {

	private static final int N_SUGGEST_RESULTS = 4;

	public DimensionalModel makeSuggestion(List<Table> oltp) {
		// TODO Auto-generated method stub

		// @formatter:on
		// - classify tables in Transaction, Component and Classification
		// Transactions:
		// - - particular events at point of time (in interest of analysts
		// - - contain measurements or quantities (that can be summarized)
		// - -
		// Component:
		// Classification:
		// @formatter:off

		// - find opportunities for grouping attributes and aggregateable
		// grouping attributes:
		// aggregateable: numeric values
		// Aggregation function: SUM, MEAN

		List<Table> transactionSuggestion = new ArrayList<Table>(oltp);
		Collections.sort(transactionSuggestion, new Comparator<Table>() {

			@Override
			public int compare(Table t1, Table t2) {
				// TODO sort the table in a way that the most likely transaction
				// tables appear in the front
				return getForeignKeyRatio(t2) - getForeignKeyRatio(t1);
			}

			private int getForeignKeyRatio(Table table) {
				return table.getnImportedKeys() - table.getnExportedKeys();
			}

		});

		DimensionalModel suggestion = new DimensionalModel();
		int i = 0;
		for (Table t : transactionSuggestion) {
			if (i < N_SUGGEST_RESULTS) {
				suggestion.addTransactionTable(t);
			} else {
				suggestion.addUnclassifiedTable(t);
			}
			i++;
		}

		return suggestion;
	}
}
