package md.beans;

public interface TableSQLCreator {

	public String getSelectQuery();
	public String getCreateQuery();
	public String getInsertQuery();
}
