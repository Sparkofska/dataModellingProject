package md.interaction;

import md.beans.DimensionalModel;
import md.beans.Table;

public class SetUnclassified extends Command {
	private enum Origin {
		TRANSACTION, COMPONENT, CLASSIFICATION, UNCLASSIFIED;
	}

	private DimensionalModel model;
	private String tableName;

	private Origin originOfTable;

	public SetUnclassified(DimensionalModel model, String tableName) {
		this.model = model;
		this.tableName = tableName;
	}

	@Override
	public void execute() {
		Table t = null;// = model.pollTableByName(tableName);

		for (Table candidate : model.getTransactionTables()) {
			if (candidate.getName().equals(tableName)) {
				t = candidate;
				originOfTable = Origin.TRANSACTION;
				break;
			}
		}
		if (t == null)
			for (Table candidate : model.getComponentTables()) {
				if (candidate.getName().equals(tableName)) {
					t = candidate;
					originOfTable = Origin.COMPONENT;
					break;
				}
			}
		if (t == null)
			for (Table candidate : model.getClassificationTables()) {
				if (candidate.getName().equals(tableName)) {
					t = candidate;
					originOfTable = Origin.CLASSIFICATION;
					break;
				}
			}
		if (t == null)
			for (Table candidate : model.getUnclassifiedTables()) {
				if (candidate.getName().equals(tableName)) {
					t = candidate;
					originOfTable = Origin.UNCLASSIFIED;
					break;
				}
			}

		if (t == null)
			throw new IllegalArgumentException(
					"A table with the specified name '" + tableName + "' does not exist in the model.");
		
		switch (originOfTable) {
		case TRANSACTION:
			model.getTransactionTables().remove(t);
			break;
		case COMPONENT:
			model.getComponentTables().remove(t);
			break;
		case CLASSIFICATION:
			model.getClassificationTables().remove(t);
			break;
		case UNCLASSIFIED:
			model.getUnclassifiedTables().remove(t);
			break;
		}
		model.addUnclassifiedTable(t);
	}

	@Override
	public void undo() {
		if (originOfTable == null)
			return;

		Table t = model.pollTableByName(tableName);
		if (t == null)
			throw new IllegalArgumentException(
					"A table with the specified name '" + tableName + "' does not exist in the model.");
		
		switch (originOfTable) {
		case TRANSACTION:
			model.addTransactionTable(t);
			break;
		case COMPONENT:
			model.addComponentTable(t);
			break;
		case CLASSIFICATION:
			model.addClassificationTable(t);
			break;
		case UNCLASSIFIED:
			model.addUnclassifiedTable(t);
			break;
		}
	}
}
