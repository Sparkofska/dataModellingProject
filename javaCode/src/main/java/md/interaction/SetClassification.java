package md.interaction;

import md.beans.DimensionalModel;
import md.beans.Table;

public class SetClassification extends DimensionalModelCommand {
	public SetClassification(DimensionalModel model, String tableName) {
		super(model, tableName);
	}

	@Override
	public void execute() {
		Table t = pollTableAndMemorizeOrigin();
		model.addClassificationTable(t);
	}
}
