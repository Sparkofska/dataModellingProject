package md.beans;

public class Column implements Cloneable{

	private String name;
	private String type;
	private boolean primaryKey;
	private boolean foreignKey;
	private String foreignKeyTable; // only set when foreignKey is true
	private String foreignKeyColumn; // only set when foreignKey is true

	@Override
	public Object clone() throws CloneNotSupportedException {
		Column clone = (Column) super.clone();
	
		clone.setName(getName()); // String is immutable
		clone.setType(getType());
		clone.setPrimaryKey(isPrimaryKey());
		clone.setForeignKey(isForeignKey());
		clone.setForeignKeyTable(getForeignKeyTable());
		clone.setForeignKeyColumn(getForeignKeyColumn());
		
		return clone;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	public boolean isForeignKey() {
		return foreignKey;
	}

	public void setForeignKey(boolean foreignKey) {
		this.foreignKey = foreignKey;
	}

	public String getForeignKeyTable() {
		return foreignKeyTable;
	}

	public void setForeignKeyTable(String foreignKeyTable) {
		this.foreignKeyTable = foreignKeyTable;
	}

	public String getForeignKeyColumn() {
		return foreignKeyColumn;
	}

	public void setForeignKeyColumn(String foreignKeyColumn) {
		this.foreignKeyColumn = foreignKeyColumn;
	}

	@Override
	public String toString() {
		return "Column [name=" + name + ", type=" + type + ", primaryKey=" + primaryKey + ", foreignKey=" + foreignKey
				+ ", foreignKeyTable=" + foreignKeyTable + ", foreignKeyColumn=" + foreignKeyColumn + "]";
	}

}
