package md;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import md.beans.Column;
import md.beans.Table;

public class Presenter {

	private OutputStream out;

	public Presenter(OutputStream out) {
		this.out = out;
	}

	public void presentMetadata(List<Table> metadata) {
		try {
			for (Table t : metadata) {

				out.write((t.getName() + "\n").getBytes());
				List<Column> cols = t.getCols();
				if (cols != null) {
					for (Column col : cols) {
						String string = "\t" + col.getName();
						if (col.isPrimaryKey())
							string += " (PK)";

						if (col.isForeignKey())
							string += " (FK->" + col.getForeignKeyTable() + ":" + col.getForeignKeyColumn() + ")";

						string += " [" + col.getType() + "]\n";
						out.write(string.getBytes());
					}
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
}
