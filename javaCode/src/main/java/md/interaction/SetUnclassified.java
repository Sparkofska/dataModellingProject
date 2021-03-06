package md.interaction;

import md.beans.DimensionalModel;
import md.beans.Table;

public class SetUnclassified extends DimensionalModelCommand {
	public SetUnclassified(DimensionalModel model, String tableName) {
		super(model, tableName);
	}

	@Override
	public void execute() {
		Table t = pollTableAndMemorizeOrigin();
		model.addUnclassifiedTable(t);
	}
}
