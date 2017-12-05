package md.interaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import md.Presenter;
import md.beans.DimensionalModel;

public class CliInteractor {

	private Presenter presenter;
	private BufferedReader input;
	private boolean TRANSACTION_TABLES_FIXED = false;

	public CliInteractor(Presenter presenter, InputStream input) {
		this.presenter = presenter;
		this.input = new BufferedReader(new InputStreamReader(input));
	}

	public DimensionalModel doMagic(List<DimensionalModel> suggestion) throws IOException {

		DimensionalModel original = (DimensionalModel) suggestion.clone();

		// first of all: transaction tables must be set
		History history = letUserFreeEdit(suggestion);
		this.TRANSACTION_TABLES_FIXED = true;

		// suggest other tables to classify accordingly
		alignTablesAutomatically(suggestion);

		// user can make modifications as he likes
		letUserFreeEdit(suggestion);

		return suggestion;
	}

	private History letUserFreeEdit(DimensionalModel model) throws IOException {
		History history = new History();
		letUserFreeEdit(model, history);
		return history;
	}

	private void letUserFreeEdit(DimensionalModel model, History history) throws IOException {

		boolean confirmed = false;
		while (!confirmed) {

			presenter.present(model);

			promptHelp(false);

			String line = input.readLine();
			String[] lineParts = line.split(" ");
			Command command = null;

			switch (lineParts[0]) {
			case "set":
				switch (lineParts[2]) {
				case "transaction":
					if (!TRANSACTION_TABLES_FIXED) {
						command = new SetTransactionTable(model, lineParts[1]);
					} else {
						throw new WrongUserInputException("TransactionTables are already fixed.");
					}
					break;
				case "component":
					if (TRANSACTION_TABLES_FIXED) {
						// TODO build Component Command
					} else {
						throw new WrongUserInputException("TransactionTables must be fixed before.");
					}
					break;
				case "classification":
					if (TRANSACTION_TABLES_FIXED) {
						// TODO build classification command
					} else {
						throw new WrongUserInputException("TransactionTables must be fixed before.");
					}
					break;
				case "unclassified":
					command = new SetUnclassified(model, lineParts[1]);
					break;

				default:
					throw new WrongUserInputException(
							"Wrong input for set: can only be 'transaction', 'component' or 'classification' but was "
									+ lineParts[2]);
				}
				history.addStep(command);
				break;
			case "fix":
				// TODO implement fixing depending on TRANSACTION_TABLES_FIXED
				break;
			case "save":
				// TODO implement save action
				break;

			case "undo":
				command = history.undo();
				break;

			case "help":
				promptHelp(true);
				break;

			default:
				throw new WrongUserInputException("Wrong input: " + line);
			}
		}
	}

	private void alignTablesAutomatically(DimensionalModel model) {
		presenter.present(model);
		presenter.present("Possible actions: yes, no");
		// TODO read from cli
		String action = "action";
	}

	private void promptHelp(boolean extendedVersion) {
		if (extendedVersion) {
			// TODO write help text
			presenter.present("No extended Version of help available yet.");
		} else {
			presenter.present("Possible actions: set, save, fix, undo, help");
		}
		if (!TRANSACTION_TABLES_FIXED)
			presenter.present("In Transaction-Table-Mode. Leave it by action: fix");
	}

	public class WrongUserInputException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public WrongUserInputException(String message, Throwable cause) {
			super(message, cause);
		}

		public WrongUserInputException(String message) {
			super(message);
		}
	}
}
