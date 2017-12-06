package md.interaction;

import md.beans.DimensionalModel;
import md.beans.Table;

public class SetComponent extends DimensionalModelCommand {
	public SetComponent(DimensionalModel model, String tableName) {
		super(model, tableName);
	}

	@Override
	public void execute() {
		Table t = pollTableAndMemorizeOrigin();
		model.addComponentTable(t);
	}
}
