package md.interaction;

import java.util.LinkedList;

public class History {

	LinkedList<Command> history;

	public History() {
		this.history = new LinkedList<>();
	}

	public void addStep(Command cmd) {
		cmd.execute();
		history.push(cmd);
	}

	/**
	 * 
	 * @return The Command that was undone. NULL if no steps for undo available.
	 */
	public Command undo() {
		if (history.isEmpty())
			return null;

		Command cmd = history.pop();
		cmd.undo();
		return cmd;
	}
}
