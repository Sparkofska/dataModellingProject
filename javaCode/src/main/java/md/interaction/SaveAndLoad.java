package md.interaction;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import md.beans.Column;
import md.beans.DimensionalModel;
import md.beans.Table;
import md.beans.TransactionSuggestion;

public class SaveAndLoad {

	public static final String TRANSACTION_SELECTION = "TRANSACTION_SELECTION";
	public static final String COMPONENT_CLASSIFICATION = "COMPONENT_CLASSIFICATION";

	public static void save(String filename, TransactionSuggestion model) throws IOException {

		JSONObject json = new JSONObject();
		json.put("status", TRANSACTION_SELECTION);
		json.put("model", toJSON(model));

		writeToFile(filename, json.toJSONString());
	}

	public static void save(String filename, Collection<DimensionalModel> model) throws IOException {
		JSONObject json = new JSONObject();
		json.put("status", COMPONENT_CLASSIFICATION);

		JSONArray list = new JSONArray();

		for (DimensionalModel m : model)
			list.add(toJSON(m));

		json.put("model", list);

		writeToFile(filename, json.toJSONString());
	}

	private static List<DimensionalModel> fromJSONDimensionalModels(JSONArray jsonModels) {
		List<DimensionalModel> models = new ArrayList<>();
		for (Object obj : jsonModels) {
			JSONObject jsonModel = (JSONObject) obj;
			DimensionalModel model = new DimensionalModel();
			model.setTransactionTables(fromJSONTables((JSONArray) jsonModel.get("transactions")));
			model.setComponentTables(fromJSONTables((JSONArray) jsonModel.get("components")));
			model.setClassificationTables(fromJSONTables((JSONArray) jsonModel.get("classifications")));
			model.setUnclassifiedTables(fromJSONTables((JSONArray) jsonModel.get("unclassified")));
		}
		return models;
	}

	private static JSONObject toJSON(DimensionalModel model) {
		JSONObject json = new JSONObject();

		json.put("transactions", toJSON(model.getTransactionTables()));
		json.put("components", toJSON(model.getComponentTables()));
		json.put("classifications", toJSON(model.getClassificationTables()));
		json.put("unclassified", toJSON(model.getUnclassifiedTables()));

		return json;
	}

	private static List<Table> fromJSONTables(JSONArray jsonTables) {
		List<Table> tables = new ArrayList<>();

		for (Object obj : jsonTables) {
			JSONObject jsonTable = (JSONObject) obj;
			tables.add(fromJSONTable(jsonTable));
		}

		return tables;
	}

	private static JSONArray toJSON(Collection<Table> tables) {
		JSONArray json = new JSONArray();

		for (Table t : tables)
			json.add(toJSON(t));

		return json;
	}

	private static TransactionSuggestion fromJSONTransactionSuggestion(JSONObject jsonTS) {
		List<Table> transactions = fromJSONTables((JSONArray) jsonTS.get("transactions"));
		List<Table> unclassified = fromJSONTables((JSONArray) jsonTS.get("unclassified"));
		return new TransactionSuggestion(transactions, unclassified);
	}

	private static JSONObject toJSON(TransactionSuggestion model) {
		JSONObject json = new JSONObject();

		JSONArray transactions = new JSONArray();
		for (Table t : model.getTransactions())
			transactions.add(toJSON(t));
		json.put("transactions", transactions);

		JSONArray unclassified = new JSONArray();
		for (Table t : model.getUnclassified())
			unclassified.add(toJSON(t));
		json.put("unclassified", unclassified);

		return json;
	}

	private static Table fromJSONTable(JSONObject jsonTable) {
		Table table = new Table();

		table.setName((String) jsonTable.get("name"));
		table.setnExportedKeys((int) (long) jsonTable.get("nExportedKeys"));
		table.setnImportedKeys((int) (long) jsonTable.get("nImportedKeys"));

		List<Column> cols = new ArrayList<>();
		for (Object obj : (JSONArray) jsonTable.get("cols")) {
			JSONObject jsonCol = (JSONObject) obj;
			cols.add(fromJSONCol(jsonCol));
		}

		table.setCols(cols);

		return table;
	}

	private static JSONObject toJSON(Table t) {
		JSONObject json = new JSONObject();

		json.put("name", t.getName());
		json.put("nExportedKeys", t.getnExportedKeys());
		json.put("nImportedKeys", t.getnImportedKeys());

		JSONArray cols = new JSONArray();
		for (Column c : t.getCols())
			cols.add(toJSON(c));
		json.put("cols", cols);

		return json;
	}

	private static Column fromJSONCol(JSONObject jsonCol) {
		Column col = new Column();

		col.setName((String) jsonCol.get("name"));
		col.setType((String) jsonCol.get("type"));
		col.setPrimaryKey((boolean) jsonCol.get("primaryKey"));
		col.setForeignKey((boolean) jsonCol.get("foreignKey"));
		col.setForeignKeyTable((String) jsonCol.get("foreignKeyTable"));
		col.setForeignKeyColumn((String) jsonCol.get("foreignKeyColumn"));

		return col;
	}

	private static JSONObject toJSON(Column c) {
		JSONObject json = new JSONObject();

		json.put("name", c.getName());
		json.put("type", c.getType());
		json.put("primaryKey", c.isPrimaryKey());
		json.put("foreignKey", c.isForeignKey());
		json.put("foreignKeyTable", c.getForeignKeyTable());
		json.put("foreignKeyColumn", c.getForeignKeyColumn());

		return json;
	}

	private static void writeToFile(String filename, String content) throws IOException {
		FileWriter writer = new FileWriter(filename);
		writer.write(content);
		writer.flush();
		writer.close();
	}

	public static LoadReturnValue load(String filename) throws IOException {
		return load(new File(filename));
	}

	public static LoadReturnValue load(File loadFile) throws IOException {

		JSONParser parser = new JSONParser();
		try {
			JSONObject json = (JSONObject) parser.parse(new FileReader(loadFile));

			String status = (String) json.get("status");
			switch (status) {
			case TRANSACTION_SELECTION:
				return new LoadReturnValue(fromJSONTransactionSuggestion((JSONObject) json.get("model")));
			case COMPONENT_CLASSIFICATION:
				return new LoadReturnValue(fromJSONDimensionalModels((JSONArray) json.get("model")));

			default:
				throw new IllegalStateException("Given file " + loadFile.getAbsolutePath() + " has unknown status: " + status);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static class LoadReturnValue {
		private String status;
		private TransactionSuggestion transactionSuggestion;
		private List<DimensionalModel> model;

		public LoadReturnValue(TransactionSuggestion transactionSuggestion) {
			this.status = TRANSACTION_SELECTION;
			this.transactionSuggestion = transactionSuggestion;
		}

		public LoadReturnValue(List<DimensionalModel> model) {
			this.status = COMPONENT_CLASSIFICATION;
			this.model = model;
		}

		public String getStatus() {
			return status;
		}

		public TransactionSuggestion getTransactionSuggestion() {
			if (this.status != TRANSACTION_SELECTION)
				throw new IllegalStateException(
						"Can retrieve model only in status TRANSACTION_SELECTION. Check getStatus() before calling this!");
			return transactionSuggestion;
		}

		public List<DimensionalModel> getModel() {
			if (this.status != COMPONENT_CLASSIFICATION)
				throw new IllegalStateException(
						"Can retrieve model only in status COMPONENT_CLASSIFICATION. Check getStatus() before calling this!");
			return model;
		}
	}
}
