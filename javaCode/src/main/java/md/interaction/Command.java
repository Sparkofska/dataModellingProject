package md.interaction;

public abstract class Command {

	public abstract void execute();

	public void undo() {
		throw new UnsupportedOperationException(
				"undo() not implemented for " + getClass() + ". Make sure to not call super.undo() on Command.");
	}
}
