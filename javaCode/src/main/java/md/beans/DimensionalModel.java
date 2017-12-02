package md.beans;

import java.util.List;

public class DimensionalModel implements Cloneable {

	private List<Table> transactionTables;

	@Override
	public Object clone() throws CloneNotSupportedException {
		DimensionalModel clone = (DimensionalModel) super.clone();
		throw new RuntimeException("work to do here!");
		// TODO Auto-generated method stub
	}

	public void setTransactionTables(List<Table> transactionTables) {
		this.transactionTables = transactionTables;
	}

	public List<Table> getTransactionTables() {
		return transactionTables;
	}
	
	
}
