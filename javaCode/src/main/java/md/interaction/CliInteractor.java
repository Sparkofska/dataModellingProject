package md.interaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import md.AggTableEdit;
import md.Presenter;
import md.beans.AggTable;
import md.beans.DimensionalModel;
import md.beans.Table;
import md.beans.TableSQLCreator;
import md.beans.TransTable;
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
					if (lineParts.length != 3)
						throw new WrongUserInputException(
								"Wrong input for set. use it this way: set <TableName> <transaction, unclassified>");
					switch (lineParts[2]) {
					case "transaction":
					case "trans":
						command = new AddTransactionTable(edit, lineParts[1]);
						break;
					case "unclassified":
					case "unclass":
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
					if (lineParts.length != 2)
						throw new WrongUserInputException("Wrong input for save. Use it this way: save <filename>");
					try {
						SaveAndLoad.save(lineParts[1], edit);
					} catch (IOException ioe) {
						presenter.present(
								"SAVE not possible due to " + ioe.getClass().getName() + " (" + ioe.getMessage() + ")");
					}
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
						if (lineParts.length != 3)
							throw new WrongUserInputException(
									"Wrong input for set. use it this way: set <TableName> <component, classification, unclassified>");
						switch (lineParts[2]) {
						case "component":
						case "comp":
							command = new SetComponent(edit, lineParts[1]);
							break;

						case "classification":
						case "class":
							command = new SetClassification(edit, lineParts[1]);
							break;

						case "unclassified":
						case "unclass":
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
						if (lineParts.length != 2)
							throw new WrongUserInputException("Wrong input for save. Use it this way: save <filename>");
						try {
							SaveAndLoad.save(lineParts[1], edits);
						} catch (IOException ioe) {
							presenter.present("SAVE not possible due to " + ioe.getClass().getName() + " ("
									+ ioe.getMessage() + ")");
						}
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

	private AggTableEdit letUserChooseAggregation(DimensionalModel model) throws IOException {

		AggTableEdit edit = new AggTableEdit(model.getTransactionTables().get(0));
		History history = new History();
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
					if (lineParts.length < 3 || lineParts.length > 4)
						throw new WrongUserInputException(
								"Wrong input for set. use it this way: set <columnName> <aggregation, unclassified> <aggregationFormula>");
					switch (lineParts[2]) {
					case "aggregation":
					case "agg":
					case "aggregate":
						if (lineParts.length != 4)
							throw new WrongUserInputException(
									"Wrong input for aggregation. use it this way: set <columnName> aggregation <aggregationFormula>. aggregationFormula must not contain whitespace.");
						command = new AddAggregation(edit, lineParts[1], lineParts[3]);
						break;

					case "unclassified":
					case "unclass":
						if (lineParts.length != 3)
							throw new WrongUserInputException(
									"Wrong input for unclassified. use it this way: set <columnName> unclassified");
						command = new RemoveAggregation(edit, lineParts[1]);
						break;

					default:
						throw new WrongUserInputException(
								"Wrong input for set: can only be 'aggregation' or 'unclassified' but was "
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
					if (lineParts.length != 2)
						throw new WrongUserInputException("Wrong input for save. Use it this way: save <filename>");
					// try {
					// TODO save
					throw new UnsupportedOperationException("TODO");
					// SaveAndLoad.save(lineParts[1], edits);
					// } catch (IOException ioe) {
					// presenter.present("SAVE not possible due to " +
					// ioe.getClass().getName() + " ("
					// + ioe.getMessage() + ")");
					// }
					// break;

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

	public List<TableSQLCreator> letUserChooseAggregation(List<DimensionalModel> models) throws IOException {
		List<TableSQLCreator> ret = new ArrayList<>();

		int i = 0;
		for (DimensionalModel model : models) {
			presenter.present("");
			presenter.present("For every selected transaction table an aggregation can be specified!");
			presenter.present(models, i++);
			presenter.present(
					"Do you want to aggregate this table '" + model.getTransactionTables().get(0).getName() + "'? Possible actions: yes, no");
			try {
				Table transTable = model.getTransactionTables().get(0); 
				String line = input.readLine();
				switch (line.toLowerCase()) {
				case "yes":
				case "y":
					AggTableEdit edit = letUserChooseAggregation(model);
					ret.add(new AggTable(transTable, edit.getAggFormulas(), edit.getAggKeys()));
					break;
				case "no":
				case "n":
					ret.add(new TransTable(transTable));
					break;
				default:
					throw new WrongUserInputException("Wrong input: can only be 'yes' or 'no' but was " + line);
				}
			} catch (WrongUserInputException e) {
				presenter.present(e.getMessage());
			}
		}

		return ret;
	}

	public class AggregationDecision {
		public List<AggTableEdit> aggregations;
		public List<DimensionalModel> keep;

		public AggregationDecision() {
			this.aggregations = new ArrayList<>();
			this.keep = new ArrayList<>();
		}
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
