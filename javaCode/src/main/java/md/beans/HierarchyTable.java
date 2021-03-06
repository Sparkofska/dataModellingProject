package md.beans;

import java.util.ArrayList;
import java.util.List;

public class HierarchyTable implements TableSQLCreator{

    private Table table;

    private String selectQuery;
    private String createQuery;
    private String insertQuery;

    private Boolean isRootTable;

    List<HierarchyTable> referencedTabs;
    List<Table> allTables;
    List<Table> transTables;
    String tabPrefix;
    String tabPrefixToDistribute;
    String selectTablesPart;
    String selectFromPart;
    String createColumnsPart;
    String createPKPart;
    String insertPart;

    @Override
    public String getSelectQuery() {
        return this.selectQuery;
    }

    @Override
    public String getCreateQuery() {
        return this.createQuery;
    }

    @Override
    public String getInsertQuery() {
        return this.insertQuery;
    }

    public void setTransTables(List<Table> transTables) {
        this.transTables = transTables;
    }

    public List<Table> getTransTables() {
        return transTables;
    }

    private void setSelectQuery(String selectQuery){
        this.selectQuery=selectQuery;
    }
    private void setCreateQuery(String createQuery){
        this.createQuery=createQuery;
    }
    private void setInsertQuery(String insertQuery){
        this.insertQuery=insertQuery;
    }

    public void setIsRootTable(Boolean isRootTable) {
        this.isRootTable = isRootTable;
    }

    public Boolean getIsRootTable() {
        return isRootTable;
    }

    public void setInsertPart(String insertPart) {
        this.insertPart = insertPart;
    }

    public String getInsertPart() {
        return insertPart;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public Table getTable() {
        return table;
    }

    public void setReferencedTabs(List<HierarchyTable> referencedTabs) {
        this.referencedTabs = referencedTabs;
    }

    public List<HierarchyTable> getReferencedTabs() {
        return referencedTabs;
    }

    public void setCreateColumnsPart(String createColumnsPart) {
        this.createColumnsPart = createColumnsPart;
    }

    public void setCreatePKPart(String createPKPart) {
        this.createPKPart = createPKPart;
    }

    public void setSelectFromPart(String selectFromPart) {
        this.selectFromPart = selectFromPart;
    }

    public void setSelectTablesPart(String selectTablesPart) {
        this.selectTablesPart = selectTablesPart;
    }

    public HierarchyTable (Table tab, List<Table> transTables){
        this.setTransTables(transTables);
        this.setTable(tab.duplicateTable(tab));
        this.setIsRootTable(true);
        this.setTabPrefix("");
        this.createDistributedPrefix();
        this.setReferencedTabs(new ArrayList<>());

        this.createFirstTabSqlParts();
    }

    public HierarchyTable (Table tab, List<Table> transTables, String prefix){
        this.setTransTables(transTables);
        this.setTable(tab.duplicateTable(tab));
        this.setIsRootTable(false);
        this.setTabPrefix(prefix);
        this.createDistributedPrefix();
        this.setReferencedTabs(new ArrayList<>());
        this.initializeEmptySqlParts();

    }

    public String aggregateSelectTabs(){

        String agg= " " + this.selectTablesPart;
        for(HierarchyTable t: this.getReferencedTabs()){
            agg+=t.aggregateSelectTabs();
        }
        return agg;
    }

    public String aggregateInsert(){

        String agg= " " + this.insertPart;
        for(HierarchyTable t: this.getReferencedTabs()){
            agg+=t.aggregateInsert();
        }
        return agg;
    }

    public String aggregateCreateColumns(){

        String agg= " " + this.createColumnsPart;
        for(HierarchyTable t: this.getReferencedTabs()){
            agg+=t.aggregateCreateColumns();
        }
        return agg;
    }

    public String aggregateCreatePK(){

        String agg= " " + this.createPKPart;
        for(HierarchyTable t: this.getReferencedTabs()){
            agg+=t.aggregateCreatePK();
        }
        return agg;
    }

    public String aggregateSelectFrom(){

        String agg= " " + this.selectFromPart;
        for(HierarchyTable t: this.getReferencedTabs()){
            agg+=t.aggregateSelectFrom();
        }
        return agg;
    }

    public void setTabPrefix(String tabPrefix) {
        this.tabPrefix = tabPrefix;
    }

    public String getTabPrefix() {
        return tabPrefix;
    }

    public void setTabPrefixToDistribute(String tabPrefixToDistribute) {
        this.tabPrefixToDistribute = tabPrefixToDistribute;
    }

    public String getTabPrefixToDistribute() {
        return tabPrefixToDistribute;
    }

    private void createDistributedPrefix(){
        if (this.table.getName().length() > 3){
            setTabPrefixToDistribute(this.getTabPrefix() + this.table.getName().substring(0,3)+ "_");
        }
        else
            setTabPrefixToDistribute(this.getTabPrefix() + this.table.getName()+ "_");
    }

    public void createFinalSqlScripts(){
        String select=this.aggregateSelectTabs();
        String selectFrom=this.aggregateSelectFrom();
        String createCols=this.aggregateCreateColumns();
        String createPK=this.aggregateCreatePK();
        String insert=this.aggregateInsert();

        String sqlSelect=select + selectFrom + ";\n";
        String sqlCreate=createCols + ",\n\tPRIMARY KEY (" +createPK + "));";
        String sqlInsert=insert + ")";

        sqlSelect=sqlSelect.replaceFirst("SELECT\\s*,", " SELECT ");
        sqlSelect=sqlSelect.replaceFirst(",\\s*FROM", " FROM");
        sqlInsert=sqlInsert.replaceAll("\\(\\s*,", "\\(");
        sqlCreate=sqlCreate.replaceAll("\\(\\s*,", "\\(");
        sqlCreate=sqlCreate.replaceAll(",\\s*\\)", "\\)");
        sqlInsert=sqlInsert.replaceAll(",\\s*\\)", "\\)");
        sqlCreate=sqlCreate.replaceAll(",\\s*,", ",");
        sqlSelect=sqlSelect.replaceAll(",\\s*,", ",");
        sqlInsert=sqlInsert.replaceAll(",\\s*,", ",");

        this.setSelectQuery(sqlSelect);
        this.setInsertQuery(sqlInsert);
        this.setCreateQuery(sqlCreate);
    }

    public void printSqlScripts(){
        System.out.print("\n");
        System.out.print("--------SELECT:\n");
        System.out.print(this.getSelectQuery());
        System.out.print("\n");
        System.out.print("--------CREATE:\n");
        System.out.print(this.getCreateQuery());
        System.out.print("\n");
        System.out.print("--------INSERT:\n");
        System.out.print(this.getInsertQuery());
        System.out.print("\n");
    }

    private String createFKPrefix(String fk){
        String prefix = "";
        if (!this.getTabPrefixToDistribute().equals(""))
            prefix = this.getTabPrefixToDistribute()+"_";

        if (fk.length() > 3){
            return (prefix + fk.substring(0,3)+ "_");
        }
        else
            return (prefix + fk + "_");
    }


    private void initializeEmptySqlParts(){
        this.setSelectFromPart("");
        this.setCreateColumnsPart("");
        this.setSelectTablesPart("");
        this.setCreatePKPart("");
        this.setInsertPart("");
    }


    private void createFirstTabSqlParts(){
        String insert="";
        String columns="";
        String select="";
        Boolean firstCol=true;
        for (Column col: this.table.getCols()){
            if (!col.isForeignKey()){
                if (firstCol){
                    insert+=",";
                    columns+=",";
                }
                firstCol=false;

                insert+= col.getName()+ ", ";
                columns+= "\n\t"+ col.getName() + " " +  col.getType() + ",";
                select+= this.getTabPrefix() + this.table.getName() + "."+col.getName()+ " " + ",";
            }
        }
        this.setSelectFromPart(" FROM " + this.table.getName() + " ");
        this.setInsertPart("INSERT INTO "+this.table.getName() +"_collapsed ("+ insert);
        this.setCreateColumnsPart("CREATE TABLE IF NOT EXISTS " + this.table.getName() + "_collapsed" + "(" + columns);
        this.setSelectTablesPart("SELECT " + select);
        this.createFirstPKParts();
    }

    private void createFirstPKParts(){
        String pk="";
        for (Column col: this.table.getCols()){
            if (col.isPrimaryKey() && !col.isForeignKey()){
                pk+= ", " + col.getName();
            }
        }
        this.createPKPart= pk;
    }

    private void createPKParts(){
        String pk="";
        for (Column col: this.table.getCols()){
            if (col.isPrimaryKey() && !col.isForeignKey()){
                pk+= ", " +this.getTabPrefix() + this.table.getName() + "_" + col.getName();
            }
        }
        this.createPKPart= pk;
    }

    private void createChildSqlParts(){
        String insert="";
        String columns="";
        String select="";
        for (Column col: this.table.getCols()){
            insert+= "," +this.getTabPrefix() + this.table.getName() + "_" + col.getName() + " ";
            columns+= ",\n\t" +this.getTabPrefix() + this.table.getName() + "_" + col.getName() + " " + col.getType();
            select+= "," + this.getTabPrefix() + this.table.getName() + "."+col.getName()+" AS "
                    +this.getTabPrefix() + this.table.getName() + "_" + col.getName() + " ";
        }
        this.setSelectTablesPart(select);
        this.setInsertPart(insert);
        this.setCreateColumnsPart(columns);
        this.createPKParts();
    }

    private Boolean hasMultipleKeysToTheSameTable(Column fKColumn){
        int sameTableReferenceCount = 0;
        for (Column c : this.table.getFKColumns()){
            if (c.getForeignKeyTable().equals(fKColumn.getForeignKeyTable()))
                sameTableReferenceCount+=1;
        }
        if (sameTableReferenceCount>1){
            return true;
        }
        else
            return false;
    }

    private void addTableToSelectFromPart(Column fKColumnToChildTable, String referredTableNewName){
        this.selectFromPart+="JOIN " + fKColumnToChildTable.getForeignKeyTable() + " AS " + referredTableNewName
                + " ON " + this.getTabPrefix() + this.table.getName() +"." + fKColumnToChildTable.getName() + "="
                + referredTableNewName + "." + fKColumnToChildTable.getForeignKeyColumn() + " \n";
    }

    public void createReferencedTableList(List<Table> allTabs){
        HierarchyTable ht;
        for (Column col : this.table.getFKColumns()){
            for (Table tab: allTabs){
                if (tab.getName().equals(col.getForeignKeyTable())){
                    Boolean skipTransactionTable=false;
                    for (Table tranTab: this.getTransTables()) {
                        if (("TT"+col.getForeignKeyTable()).equals(tranTab.getName()))
                            skipTransactionTable=true;
                    }
                    if (skipTransactionTable)
                        break;

                    String tableToJoin;
                    if (hasMultipleKeysToTheSameTable(col)){
                        tableToJoin=  getTabPrefixToDistribute()+"ON_" +col.getName()+"_"+col.getForeignKeyTable();
                        ht=new HierarchyTable(tab, this.getTransTables(),  getTabPrefixToDistribute()+"ON_" +col.getName()+"_");
                    }
                    else{
                        tableToJoin=  getTabPrefixToDistribute() + col.getForeignKeyTable();
                        ht=new HierarchyTable(tab, this.getTransTables(), this.getTabPrefixToDistribute());
                    }

                    this.addTableToSelectFromPart(col, tableToJoin);
                    ht.createReferencedTableList(allTabs);
                    this.referencedTabs.add(ht);
                }
            }
        }
        if (!this.getIsRootTable()){
            this.createChildSqlParts();
        }
        else
            this.createFinalSqlScripts();
    }

}
