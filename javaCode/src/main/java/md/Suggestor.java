package md;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import md.beans.DimensionalModel;
import md.beans.Table;

public class Suggestor {

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
				// TODO sort the table in a way that the most likely transaction tables appear in the front
				return getForeignKeyRatio(t2) - getForeignKeyRatio(t1);
			}
			
			private int getForeignKeyRatio(Table table)
			{
				return table.getnImportedKeys() - table.getnExportedKeys();
			}

		});
		
		System.out.println("\n\nTHIS IS THE SUGGESTION FOR TRANSACTION TABLES:");
		for(Table t : transactionSuggestion)
		{
			System.out.println(t.getName() + " ("+ (t.getnImportedKeys() - t.getnExportedKeys()) + ")");
		}

		return null;
	}
}
