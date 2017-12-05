package md;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import md.beans.Column;
import md.beans.DimensionalModel;
import md.beans.Table;

public class Suggestor {

	private static final int N_SUGGEST_RESULTS = 4;

	
	public List<DimensionalModel> makeSuggestion(List<Table> oltp) {
		// TODO Auto-generated method stub

		// @formatter:on
		/*
		 * our rules for classifying the tables:
		 *  1. transaction: roots of table-graph
		 *  2. components: 1st order neighbours of transactions
		 *  3. classification: all resuming tables will be collapsed as a hierarchy
		 */
		// @formatter:off

		// - find opportunities for grouping attributes and aggregateable
		// grouping attributes:
		// aggregateable: numeric values
		// Aggregation function: SUM, MEAN

		/*
		 *This is the old approach 
		 */
//		List<Table> transactionSuggestion = new ArrayList<Table>(oltp);
//		Collections.sort(transactionSuggestion, new Comparator<Table>() {
//
//			@Override
//			public int compare(Table t1, Table t2) {
//				// TODO sort the table in a way that the most likely transaction
//				// tables appear in the front
//				return getForeignKeyRatio(t2) - getForeignKeyRatio(t1);
//			}
//
//			private int getForeignKeyRatio(Table table) {
//				return table.getnImportedKeys() - table.getnExportedKeys();
//			}
//
//		});
//
//		DimensionalModel suggestion = new DimensionalModel();
//		int i = 0;
//		for (Table t : transactionSuggestion) {
//			if (i < N_SUGGEST_RESULTS) {
//				suggestion.addTransactionTable(t);
//			} else {
//				suggestion.addUnclassifiedTable(t);
//			}
//			i++;
//		}
		List<Table> roots = new ArrayList<Table>();
		for(Table t:oltp){
			if(t.getnExportedKeys() == 0)
				roots.add(t);
		}
		List<Table> remains = new ArrayList<Table>(oltp);
		for(Table t:roots){
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
		
		DimensionalModel suggestion = new DimensionalModel();
		suggestion.setTransactionTables(roots);
		suggestion.setUnclassifiedTables(remains);

		return suggestion;
	}
	
	private Collection<Table> getReferencedTables(Collection<Table> oltp, Table root)
	{
		List<Column> cols = root.getCols();
		if(cols == null)
			return new ArrayList<Table>(0);
		
		for(Column col:cols) {
			if(col.isForeignKey())
			{
				String referencedTableName = col.getForeignKeyTable();
				for(Table t:oltp)
				{
					if(t.getName().equals(referencedTableName))
						
				}
			}
		}
		
		ArrayList<Table> ret = new ArrayList<Table>();
		return ret;
	}
}
