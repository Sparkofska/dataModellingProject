package md.beans;

import java.util.ArrayList;
import java.util.List;

public class HierarchyTable extends Table {
    Column SubEntity;
    List<HierarchyTable> referencedTabs;
    List<Table> classificationTables;
    String tabPrefix;
    String tabPrefixToDistribute;
    String selectTablesPart;
    String selectFromPart;
    String createColumnsPart;
    String createPKPart;
    String insertPart;
    String sqlSelect;
    String sqlCreate;
    String sqlInsert;

    public String getSqlCreate() {
        return sqlCreate;
    }

    public String getSqlInsert() {
        return sqlInsert;
    }

    public String getSqlSelect() {
        return sqlSelect;
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
        if (this.getName().length() > 3){
            setTabPrefixToDistribute(this.getTabPrefix() + this.getName().substring(0,3)+ "_");
        }
        else
            setTabPrefixToDistribute(this.getTabPrefix() + this.getName()+ "_");
    }

    public void createSqlScripts(){
        String select=this.aggregateSelectTabs();
        String selectFrom=this.aggregateSelectFrom();
        String createCols=this.aggregateCreateColumns();
        String createPK=this.aggregateCreatePK();
        String insert=this.aggregateInsert();

        this.sqlSelect=select + selectFrom + ";\n";
        this.sqlCreate=createCols + ",\n\tPRIMARY KEY (" +createPK + "));";
        this.sqlInsert=insert + ")";

        this.sqlSelect=this.sqlSelect.replaceFirst("^ SELECT +,", " SELECT ");
        this.sqlInsert=this.sqlInsert.replaceFirst("\\(\\s*,", "\\(");
        this.sqlCreate=this.sqlCreate.replaceFirst("\\(\\s*,", "\\(");
    }

    public void printSqlScripts(){
        System.out.print("\n");
        System.out.print("--------SELECT:\n");
        System.out.print(this.getSqlSelect());
        System.out.print("\n");
        System.out.print("--------CREATE:\n");
        System.out.print(this.getSqlCreate());
        System.out.print("\n");
        System.out.print("--------INSERT:\n");
        System.out.print(this.getSqlInsert());
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

    public HierarchyTable (Table tab){
        this.setName(tab.getName());
        this.setTabPrefix("");
        this.createDistributedPrefix();
        this.setReferencedTabs(new ArrayList<>());
        this.selectTablesPart="";
        this.createColumnsPart="CREATE TABLE IF NOT EXISTS " + this.getName() + "_collapsed" + "(";
        this.selectFromPart=" FROM " + tab.getName() + " ";
        this.createPKPart="";
        this.insertPart="";

        List<Column> clonedColumns = new ArrayList<>();
        for (Column col : tab.getCols()) {
            try{
                clonedColumns.add(Column.class.cast(col.clone()));
            }
            catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
        this.setCols(clonedColumns);
        this.createFirstTabSelectPart();
        this.createFirstTabColumnsPart();;
        this.createPKPartFirstTable();
        this.createFirstTabInsertPart();
    }

    public HierarchyTable (Table tab, String prefix){
        this.setName(tab.getName());
        this.setTabPrefix(prefix);
        this.createDistributedPrefix();
        this.setReferencedTabs(new ArrayList<>());
        this.selectFromPart="";
        this.createColumnsPart="";
        this.selectTablesPart="";
        this.createPKPart="";
        this.insertPart="";

        List<Column> clonedColumns = new ArrayList<>();
        for (Column col : tab.getCols()) {
            try{
                clonedColumns.add(Column.class.cast(col.clone()));
            }
            catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
        this.setCols(clonedColumns);
    }

    private void createFirstTabSelectPart(){
        String select="";
        for (Column col: this.getCols()){
            if (!col.isForeignKey()){
                if (!select.equals("")){
                    select+=",";
                }
                select+= this.getTabPrefix() + this.getName() + "."+col.getName()+ " ";
            }
        }
        this.selectTablesPart+="SELECT " + select;
    }

    private void createPKPartFirstTable(){
        String pk="";
        for (Column col: this.getCols()){
            if (col.isPrimaryKey() && !col.isForeignKey()){
                if (!pk.equals(""))
                    pk+=", ";
                pk+=  col.getName();
            }
        }
        this.createPKPart+= pk;
    }

    private void createPKParts(){
        String pk="";
        for (Column col: this.getCols()){
            if (col.isPrimaryKey() && !col.isForeignKey()){
                pk+= ", " +this.getTabPrefix() + this.getName() + "_" + col.getName();
            }
        }
        this.createPKPart+= pk;
    }

    private void createSelectPart(){
        String select="";
        for (Column col: this.getCols()){
            if (!col.isForeignKey()){
                select+= "," + this.getTabPrefix() + this.getName() + "."+col.getName()+" AS "
                        +this.getTabPrefix() + this.getName() + "_" + col.getName() + " ";
            }
        }
        this.selectTablesPart+= select;
    }

    private void createFirstTabInsertPart(){
        String insert="";
        for (Column col: this.getCols()){
            if (!col.isForeignKey()){
                if (!insert.equals("")){
                    insert+=",";
                }
                insert+= col.getName()+ " ";
            }
        }
        this.insertPart+="INSERT INTO "+this.getName() +"_collapsed ("+ insert;
    }

    private void createInsertPart(){
        String insert="";
        for (Column col: this.getCols()){
            if (!col.isForeignKey()){
                insert+= "," +this.getTabPrefix() + this.getName() + "_" + col.getName() + " ";
            }
        }
        this.insertPart+= insert;
    }

    private void createFirstTabColumnsPart(){
        String columns="";
        for (Column col: this.getCols()){
            if (!col.isForeignKey()){
                if (!columns.equals("")){
                    columns+=",";
                }
                columns+= "\n\t"+ col.getName() + " " +  col.getType();
            }
        }
        this.createColumnsPart+=columns;
    }

    private void createColumns(){
        String columns="";
        for (Column col: this.getCols()){
            if (!col.isForeignKey()){
                columns+= ",\n\t" +this.getTabPrefix() + this.getName() + "_" + col.getName() + " " + col.getType();
            }
        }
        this.createColumnsPart+= columns;
    }


    public void createReferencedTableList(List<Table> allTabs){
        HierarchyTable ht;
        for (Column col : this.getFKColumns()){
            for (Table tab: allTabs){
                if (tab.getName().equals(col.getForeignKeyTable())){
                    int sameTableReferenceCount = 0;
                    for (Column c : this.getFKColumns()){
                        if (c.getForeignKeyTable().equals(col.getForeignKeyTable()))
                            sameTableReferenceCount+=1;
                    }
                    String tableToJoin="";
                    if (sameTableReferenceCount >1){
                        tableToJoin=  getTabPrefixToDistribute()+"ON_" +col.getName()+"_"+col.getForeignKeyTable();
                        ht=new HierarchyTable(tab,  getTabPrefixToDistribute()+"ON_" +col.getName()+"_");
                        //ht=new HierarchyTable(tab,  this.createFKPrefix(col.getForeignKeyTable())+"ON_" +col.getName()+"_");
                    }
                    else{
                        tableToJoin=  getTabPrefixToDistribute() + col.getForeignKeyTable();
                        ht=new HierarchyTable(tab, this.getTabPrefixToDistribute());
                    }

                    this.selectFromPart+="JOIN " + col.getForeignKeyTable() + " AS " + tableToJoin + " ON " + this.getTabPrefix()
                            + this.getName() +"." +col.getName() + "=" + tableToJoin + "." + col.getForeignKeyColumn() + " \n";
                   // System.out.print("created HT " + ht.getName() + this.selectFromPart);
                    ht.createReferencedTableList(allTabs);
                    //System.out.print("***referendec from " + ht.getName() + " created and added ***");
                    this.referencedTabs.add(ht);
                }
            }
        }
        if (this.selectTablesPart.equals("")){
            this.createColumns();
            this.createSelectPart();
            this.createPKParts();
            this.createInsertPart();
        }
        //System.out.print(getName()+"% select " + this.selectTablesPart +"\n");
        //System.out.print(getName()+"% from  " + this.selectFromPart + "\n");
    }

    public void setReferencedTabs(List<HierarchyTable> referencedTabs) {
        this.referencedTabs = referencedTabs;
    }

    public List<HierarchyTable> getReferencedTabs() {
        return referencedTabs;
    }

    private void setSubEntity(Column subEntity){
        this.SubEntity=subEntity;
    }

    public Column getSubEntity() {
        return SubEntity;
    }
}
