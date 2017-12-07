package md.interaction;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import md.beans.Column;
import md.beans.DimensionalModel;
import md.beans.Table;
import md.beans.TransactionSuggestion;

public class SaveAndLoad {

	public static void save(String filename, TransactionSuggestion model) throws IOException {

		JSONObject json = new JSONObject();
		json.put("status", "TRANSACTION_SELECTION");
		json.put("model", toJSON(model));

		writeToFile(filename, json.toJSONString());
	}

	public static void save(String filename, Collection<DimensionalModel> model) throws IOException {
		JSONObject json = new JSONObject();
		json.put("status", "COMPONENT_CLASSIFICATION");

		JSONArray list = new JSONArray();

		for (DimensionalModel m : model)
			list.add(toJSON(m));

		json.put("model", list);

		writeToFile(filename, json.toJSONString());
	}

	private static JSONObject toJSON(DimensionalModel model) {
		JSONObject json = new JSONObject();

		json.put("transactions", toJSON(model.getTransactionTables()));
		json.put("components", toJSON(model.getComponentTables()));
		json.put("classifications", toJSON(model.getClassificationTables()));
		json.put("unclassified", toJSON(model.getUnclassifiedTables()));

		return json;
	}

	private static JSONArray toJSON(Collection<Table> tables) {
		JSONArray json = new JSONArray();

		for (Table t : tables)
			json.add(toJSON(t));

		return json;
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

	public static Object load(String filename) {
		// TODO
		return null;
	}

}
