package md;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import md.Suggestor.TransactionSuggestion;
import md.beans.Column;
import md.beans.DimensionalModel;
import md.beans.Table;

public class Presenter {

	private OutputStream out;

	public Presenter(OutputStream out) {
		this.out = out;
	}

	public void present(String msg) {
		try {
			out.write((msg + "\n").getBytes());
		} catch (IOException e) {
			handleException(e);
		}
	}

	public void presentMetadata(List<Table> metadata) {
		try {
			for (Table t : metadata) {

				out.write((t.getName() + " (importedKeys: " + t.getnImportedKeys() + ", exportedKeys: "
						+ t.getnExportedKeys() + ")\n").getBytes());
				List<Column> cols = t.getCols();
				if (cols != null) {
					for (Column col : cols) {
						String string = "  " + col.getName();
						string += " [" + col.getType() + "]";

						if (col.isPrimaryKey())
							string += " (PK)";

						if (col.isForeignKey())
							string += " (FK->" + col.getForeignKeyTable() + ":" + col.getForeignKeyColumn() + ")";

						string += "\n";
						out.write(string.getBytes());
					}
					out.write("\n".getBytes());
				}
			}

			out.flush();
		} catch (IOException e) {
			handleException(e);
		}
	}

	private void handleException(IOException e) {
		e.printStackTrace();
	}

	public void present(DimensionalModel suggestion) {
		try {
			out.write("TRANSACTION TABLES:\n".getBytes());
			int i = 1;
			for (Table t : suggestion.getTransactionTables()) {
				out.write((i + ". " + t.getName() + "\n").getBytes());
				i++;
			}

			out.write("COMPONENT TABLES:\n".getBytes());
			i = 1;
			for (Table t : suggestion.getComponentTables()) {
				out.write((i + ". " + t.getName() + "\n").getBytes());
				i++;
			}

			out.write("CLASSIFICATION TABLES:\n".getBytes());
			i = 1;
			for (Table t : suggestion.getClassificationTables()) {
				out.write((i + ". " + t.getName() + "\n").getBytes());
				i++;
			}

			out.write("UNCLASSIFIED TABLES:\n".getBytes());
			i = 1;
			for (Table t : suggestion.getUnclassifiedTables()) {
				out.write((i + ". " + t.getName() + "\n").getBytes());
				i++;
			}

			out.flush();
		} catch (IOException e) {
			handleException(e);
		}

	}

	public void present(TransactionSuggestion suggestion) {
		try {
			out.write("TRANSACTION TABLES:\n".getBytes());
			int i = 1;
			for (Table t : suggestion.getTransactions()) {
				out.write((i + ". " + t.getName() + "\n").getBytes());
				i++;
			}
			
			out.write("UNCLASSIFIED TABLES:\n".getBytes());
			i = 1;
			for (Table t : suggestion.getUnclassified()) {
				out.write((i + ". " + t.getName() + "\n").getBytes());
				i++;
			}
			out.flush();
		} catch (IOException e) {
			handleException(e);
		}
	}
}
