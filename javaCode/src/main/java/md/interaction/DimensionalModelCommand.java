package md.interaction;

import md.beans.DimensionalModel;
import md.beans.Table;

public abstract class DimensionalModelCommand extends Command {

	protected enum Origin {
		TRANSACTION, COMPONENT, CLASSIFICATION, UNCLASSIFIED;
	}

	protected DimensionalModel model;
	protected String tableName;
	protected Origin originOfTable;

	public DimensionalModelCommand() {
		super();
	}

	public DimensionalModelCommand(DimensionalModel model, String tableName) {
		this.model = model;
		this.tableName = tableName;
	}

	@Override
	public void undo() {
		if (originOfTable == null)
			throw new RuntimeException("Command cannot be undone before execute() was called.");

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

	protected Table pollTableAndMemorizeOrigin() {
		Table t = null;

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
		return t;
	}
}