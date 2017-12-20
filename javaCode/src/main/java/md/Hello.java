package md;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import md.CredentialParser.Credentials;
import md.beans.DimensionalModel;
import md.beans.ExpandTransTable;
import md.beans.HierarchyTable;
import md.beans.Table;
import md.beans.TableSQLCreator;
import md.beans.TransactionSuggestion;
import md.interaction.CliInteractor;
import md.interaction.SaveAndLoad;
import md.interaction.SaveAndLoad.LoadReturnValue;

public class Hello {

	protected static final String DB_URL = "jdbc:mysql://localhost/";
	protected static final String DB_NAME = "testdb";
	private static final String DB_NAME_MOODY = "moody";
	private static final String DB_NAME_TARGET = "target";
	private static final String CREDENTIAL_FILE_PATH = "src/main/java/md/credentials.txt";

	public static void main(String[] args) {
		Credentials credentials = new CredentialParser(new File(CREDENTIAL_FILE_PATH)).doMagic();
		// createExampleDatabase(credentials);

		File loadFile = parseCliArguments(args);

		pipeline(credentials, loadFile);
	}

	// @formatter:off
	// 1. read meta data of given database
	// 2. present meta data to user
	// 3. think of suggestions for conversion
	// - classify tables in Transaction, Component and Classification
	// - find opportunities for grouping attributes and aggregateable
	// attributes
	// 4. present suggestions to user
	// 5. Let user edit suggestions
	// - choose transaction tables
	// - select tables to collapse
	// - select grouping attributes
	// - specify aggregation function
	// 6. [Breakpoint at each step of editing]
	// 7. Let user confirm
	// 8. convert database
	// - collapse hierarchy (Classification tables)
	// - aggregate
	// @formatter:on
	private static void pipeline(Credentials credentials, File loadFile) {
		Presenter presenter = new Presenter(System.out);

		try {
			// new DatabaseMetadataReader(DB_URL, credentials.username,
			// credentials.password).doit();

			CliInteractor cli = new CliInteractor(presenter, System.in);
			List<DimensionalModel> modelFixed;
			if (loadFile != null) {
				LoadReturnValue load = SaveAndLoad.load(loadFile);
				if (load.getStatus().equals(SaveAndLoad.TRANSACTION_SELECTION)) {
					List<Table> tables = new DatabaseMetadataReader(DB_URL, credentials.username, credentials.password)
							.getMetadata(DB_NAME_MOODY);

					TransactionSuggestion transactionSuggestion = load.getTransactionSuggestion();

					TransactionSuggestion transactionsFixed = cli.letUserConfirm(transactionSuggestion);

					List<DimensionalModel> modelSuggestion = Suggestor.makeDimensionalModelSuggestion(tables,
							transactionsFixed);
					modelFixed = cli.letUserConfirm(modelSuggestion);
				} else if (load.getStatus().equals(SaveAndLoad.COMPONENT_CLASSIFICATION)) {
					List<DimensionalModel> modelSuggestion = load.getModel();
					modelFixed = cli.letUserConfirm(modelSuggestion);
				} else {
					throw new UnsupportedOperationException(
							"Hey programmer, you forgot to catch a case: " + load.getStatus());
				}
			} else {
				List<Table> tables = new DatabaseMetadataReader(DB_URL, credentials.username, credentials.password)
						.getMetadata(DB_NAME_MOODY);

				presenter.presentMetadata(tables);

				TransactionSuggestion transactionSuggestion = Suggestor.makeTransactionSuggestion(tables);

				TransactionSuggestion transactionsFixed = cli.letUserConfirm(transactionSuggestion);

				List<DimensionalModel> modelSuggestion = Suggestor.makeDimensionalModelSuggestion(tables,
						transactionsFixed);
				modelFixed = cli.letUserConfirm(modelSuggestion);
			}

			String designOption = cli.letUserChooseFinalModel();

			List<TableSQLCreator> createTables = cli.letUserChooseAggregation(modelFixed, designOption);

			presenter.present("Generating dimensional model database...");
			for (TableSQLCreator transTable : createTables) {
				createAndMigrateTable(credentials, transTable.getSelectQuery(), transTable.getInsertQuery(),
						transTable.getCreateQuery());
			}

			List<Table> transTabs = new ArrayList<>();
			if (!designOption.equals("2")) {
				for (DimensionalModel dimModel : modelFixed) {
					transTabs.addAll(dimModel.getTransactionTables());
				}
			}

			for (DimensionalModel dimModel : modelFixed) {
				for (Table compTable : dimModel.getComponentTables()) {
					Boolean skipTransactionTable = false;
					for (Table tt : transTabs) {
						if (tt.getName().equals(compTable.getName()))
							skipTransactionTable = true;
					}
					if (skipTransactionTable && !designOption.equals("2"))
						continue;
					HierarchyTable hirTable = new HierarchyTable(compTable, transTabs);
					hirTable.createReferencedTableList(dimModel.getClassificationTables());
					createAndMigrateTable(credentials, hirTable.getSelectQuery(), hirTable.getInsertQuery(),
							hirTable.getCreateQuery());
				}
			}

			for (Table factTable : transTabs) {
				ExpandTransTable hirTable = new ExpandTransTable(factTable, transTabs);
				hirTable.createReferencedTableList(transTabs);
				createAndMigrateTable(credentials, hirTable.getSelectQuery(), hirTable.getInsertQuery(),
						hirTable.getCreateQuery());
			}

			presenter.present("Dimensional model successfully generated.");

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void createAndMigrateTable(Credentials credentials, String select, String insert, String create) {
		DatabaseCreator dbc;
		try {
			dbc = new DatabaseCreator(DB_URL, credentials.username, credentials.password);
			dbc.migrate(select, insert, create, DB_NAME_MOODY, DB_NAME_TARGET);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	private static void createExampleDatabase(Credentials credentials) {
		DatabaseCreator dbc;
		try {
			dbc = new DatabaseCreator(DB_URL, credentials.username, credentials.password);
			// dbc.createTestDatabase(DB_NAME);
			dbc.createTestDatabaseMoody(DB_NAME_MOODY);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static File parseCliArguments(String[] args) {
		int i = 0;
		for (String arg : args) {
			i++;
			switch (arg) {
			case "-load":
				if (i >= args.length)
					throw new RuntimeException("Wrong call. Use it like: project.jar -load <filename>");
				File file = new File(args[i]);
				if (!file.exists())
					throw new RuntimeException("Wrong call. File " + args[i] + " does not exist.");
				return file;

			default:
				System.out.println("No option specified for " + arg);
			}
		}
		return null;
	}
}
