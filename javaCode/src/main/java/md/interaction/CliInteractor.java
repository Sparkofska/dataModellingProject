package md.interaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import md.Presenter;
import md.beans.DimensionalModel;
import md.beans.TransactionSuggestion;

public class CliInteractor {

	private Presenter presenter;
	private BufferedReader input;

	public CliInteractor(Presenter presenter, InputStream input) {
		this.presenter = presenter;
		this.input = new BufferedReader(new InputStreamReader(input));
	}

	public TransactionSuggestion letUserConfirm(TransactionSuggestion suggestion) throws IOException {

		TransactionSuggestion edit = new TransactionSuggestion(suggestion);
		History history = new History();
		presenter.present("");
		presenter.present("The transaction tables must be selected!");

		boolean confirmed = false;
		while (!confirmed) {
			try {

				presenter.present("");
				presenter.present(edit);

				promptHelp(false);

				String line = input.readLine();
				String[] lineParts = line.split(" ");
				Command command = null;

				switch (lineParts[0]) {
				case "set":
					if(lineParts.length != 3)
						throw new WrongUserInputException("Wrong input for set. use it this way: set <TableName> <transaction, unclassified>");
					switch (lineParts[2]) {
					case "transaction":
						command = new AddTransactionTable(edit, lineParts[1]);
						break;
					case "unclassified":
						command = new RemoveTransactionTable(edit, lineParts[1]);
						break;

					default:
						throw new WrongUserInputException(
								"Wrong input for set: can only be 'transaction' or 'unclassified' but was "
										+ lineParts[2]);
					}
					try {
						history.addStep(command);
					} catch (IllegalArgumentException e) {
						throw new WrongUserInputException("Wrong input: the table " + lineParts[1] + " does not exist.",
								e);
					}
					break;
				case "fix":
					confirmed = true;
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
			} catch (WrongUserInputException e) {
				presenter.present(e.getMessage());
			}
		}
		return edit;
	}

	public List<DimensionalModel> letUserConfirm(List<DimensionalModel> suggestion) throws IOException {

		List<DimensionalModel> edits = new ArrayList<>(suggestion.size());
		int i = 0;
		for (DimensionalModel rename : suggestion) {

			DimensionalModel edit = new DimensionalModel(rename);
			edits.add(edit);

			History history = new History();
			presenter.present("");
			presenter.present(
					"For every selected transaction table the components and classifications must be specified!");
			presenter.present(suggestion, i++);

			boolean confirmed = false;
			while (!confirmed) {
				try {
					presenter.present("");
					presenter.present(edit);

					promptHelp(false);

					String line = input.readLine();
					String[] lineParts = line.split(" ");
					Command command = null;

					switch (lineParts[0]) {
					case "set":
						if(lineParts.length != 3)
							throw new WrongUserInputException("Wrong input for set. use it this way: set <TableName> <component, classification, unclassified>");
						switch (lineParts[2]) {
						case "component":
							command = new SetComponent(edit, lineParts[1]);
							break;

						case "classification":
							command = new SetClassification(edit, lineParts[1]);
							break;

						case "unclassified":
							command = new SetUnclassified(edit, lineParts[1]);
							break;

						default:
							throw new WrongUserInputException(
									"Wrong input for set: can only be 'component', 'classification' or 'unclassified' but was "
											+ lineParts[2]);
						}
						try {
							history.addStep(command);
						} catch (IllegalArgumentException e) {
							throw new WrongUserInputException(
									"Wrong input: the table " + lineParts[1] + " does not exist.", e);
						}
						break;

					case "fix":
						confirmed = true;
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
				} catch (WrongUserInputException e) {
					presenter.present(e.getMessage());
				}
			}
		}

		return edits;
	}

	private void promptHelp(boolean extendedVersion) {
		if (extendedVersion) {
			// TODO write help text
			presenter.present("No extended Version of help available yet.");
		} else {
			presenter.present("Possible actions: set, save, fix, undo, help");
		}
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
