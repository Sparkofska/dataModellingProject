package md.beans;

import java.util.ArrayList;
import java.util.List;

public class CollapsedTable {
    private List<HierarchyTable> hierarchyTables;
    private Table collapsedTable;
    private String selectQuery;
    private String createQuery;

    public Table getCollapsedTable() {
        return collapsedTable;
    }

    public String getCreateQuery() {
        return createQuery;
    }

    public String getSelectQuery() {
        return selectQuery;
    }

    public CollapsedTable(List<HierarchyTable> hierarchyTables) {
        this.hierarchyTables = hierarchyTables;
    }

    public void createCollapsedTable (){
        String fromPartQuery="";
        String selectPartQuery="";
        String createPartQuery="";
        String columnsPartQuery="";
        String primaryKeysPartQuery="";

        Boolean firstTable=true;
        List<Column> clonedColumns = new ArrayList<>();
        for (HierarchyTable table : this.hierarchyTables) {
            if (fromPartQuery.equals("")){
                fromPartQuery=table.getName();
            }
            if (createPartQuery.equals("")){
                createPartQuery=table.getName();
            }

            Column fkColumn = table.getSubEntity();
            if (fkColumn != null){
                fromPartQuery+=" join " + fkColumn.getForeignKeyTable() + " on " +table.getName()
                        + "." + fkColumn.getName() + "=" + fkColumn.getForeignKeyTable()
                        + "." + fkColumn.getForeignKeyColumn();

            }
            for (Column col : table.getCols()){
                try{
                    if (col == table.getSubEntity())
                        continue;
                    clonedColumns.add(Column.class.cast(col.clone()));
                    if (!selectPartQuery.equals("")){
                        selectPartQuery+=", ";
                    }
                    if (!columnsPartQuery.equals("")){
                        columnsPartQuery+=",\n";
                    }
                    if (firstTable && col.isPrimaryKey()){
                        if (!primaryKeysPartQuery.equals("")){
                            primaryKeysPartQuery+=", ";
                        }
                        primaryKeysPartQuery+= col.getName();
                    }
                    columnsPartQuery+="\t" + col.getName() + " " + col.getType();
                    selectPartQuery+= table.getName() + "." + col.getName() ;
                }
                catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
            firstTable=false;
        }
        this.collapsedTable = new Table();
        this.collapsedTable.setName(this.hierarchyTables.get(0).getName()+"_collapsed");
        this.collapsedTable.setCols(clonedColumns);
        this.selectQuery="SELECT " + selectPartQuery + " \nFROM " + fromPartQuery + ";";
        this.createQuery="CREATE TABLE IF NOT EXISTS " + createPartQuery + "_collapsed"
                + "(\n" + columnsPartQuery + ",\n\tPRIMARY KEY (" + primaryKeysPartQuery
                + "));";
        System.out.print("*****SELECT QUERY*******\n" + this.selectQuery + "\n");
        System.out.print("*****CREATE QUERY*******\n" + this.createQuery + "\n");
        System.out.print("collapsed: \n" +this.collapsedTable.getColsNames());

    }

}
