package md;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import md.CredentialParser.Credentials;
import md.beans.DimensionalModel;
import md.beans.Table;
import md.beans.TransactionSuggestion;
import md.interaction.CliInteractor;

public class Hello {

	protected static final String DB_URL = "jdbc:mysql://localhost/";
	protected static final String DB_NAME = "testdb";
	private static final String DB_NAME_MOODY = "moody";
	private static final String CREDENTIAL_FILE_PATH = "src/main/java/md/credentials.txt";

	public static void main(String[] args) throws SQLException {
		System.out.println("Hello World!");
		Credentials credentials = new CredentialParser(new File(CREDENTIAL_FILE_PATH)).doMagic();
		// createExampleDatabase(credentials);
		pipeline(credentials);
		System.out.println("terminated.");
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
	private static void pipeline(Credentials credentials) {
		Presenter presenter = new Presenter(System.out);

		try {
			// new DatabaseMetadataReader(DB_URL, credentials.username,
			// credentials.password).doit();

			// 1. read meta data of given database
			List<Table> tables = new DatabaseMetadataReader(DB_URL, credentials.username, credentials.password)
					.getMetadata(DB_NAME_MOODY);

			// 2. present meta data to user
//			presenter.presentMetadata(tables);

			// 3. think of suggestions for conversion
			 TransactionSuggestion transactionSuggestion = Suggestor.makeTransactionSuggestion(tables);

			// 4. present suggestions to user
			// 5. Let user edit suggestions
			// 6. [Breakpoint at each step of editing]
			 // 7. Let user confirm
			CliInteractor cli = new CliInteractor(presenter, System.in);
			TransactionSuggestion transactionsFixed = cli.letUserConfirm(transactionSuggestion);
			
			List<DimensionalModel> modelSuggestion = Suggestor.makeStarPeakSuggestion(tables, transactionsFixed);
			List<DimensionalModel> modelFixed = cli.letUserConfirm(modelSuggestion);

			// 8. convert database
			// TODO Dusan's part

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		} catch (IOException e) {
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
}
