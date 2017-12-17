package md.beans;


import java.util.ArrayList;
import java.util.List;

public class AggTable extends Table {
    private List<String> aggFormula;
    private List<String> aggFormulaResultType;
    private List<String> aggFormulaColumnNames;
    private List<Column> groupingAttributes;

    private String selectQuery;
    private String createQuery;
    private String insertQuery;

    public void setAggFormulaColumnNames(List<String> aggFormulaColumnNames) {
        this.aggFormulaColumnNames = aggFormulaColumnNames;
    }

    public List<String> getAggFormulaColumnNames() {
        return aggFormulaColumnNames;
    }

    public void setAggFormulaResultType(List<String> aggFormulaResultType) {
        this.aggFormulaResultType = aggFormulaResultType;
    }

    public void setAggFormula(List<String> aggFormula) {
        this.aggFormula = aggFormula;
    }

    public void setGroupingAttributes(List<Column> groupingAttributes) {
        this.groupingAttributes = groupingAttributes;
    }

    public String getSelectHeader(){
        String head="";
        for (Column col: this.groupingAttributes){
            if (!head.equals(""))
                head+=", ";
            head+=col.getName() + " ";
        }

        for (String formula: this.aggFormula){
            head+=", " + formula + " ";
        }
        return head;
    }

    public String getNewTableHeader(){
        String head="";
        for (Column col: this.groupingAttributes){
            if (!head.equals(""))
                head+=", ";
            head+=col.getName() + " ";
        }

        for (String formula: this.getAggFormulaColumnNames()){
            head+=", " + formula + " ";
        }
        return head;
    }

    private void createCreate(){

        String head="";
        for (Column col: this.groupingAttributes){
            if (!head.equals(""))
                head+=",\n\t";
            head+=col.getName() + " " + col.getType();
        }

        for (int i=0; i<this.aggFormulaColumnNames.size(); i++){
            head+=",\n\t" + this.aggFormulaColumnNames.get(i) + " " + this.aggFormulaResultType.get(i) + " ";
        }

        head+=",\n\tPRIMARY KEY(";
        boolean addCommaChar=false;
        for (Column col: this.groupingAttributes){
            if (addCommaChar)
                head+=", ";
            head+= col.getName() + " ";
            addCommaChar=true;
        }
        this.createQuery="CREATE TABLE IF NOT EXISTS " + this.getName() + "_agg" + "(" + head + "));\n";
    }

    public void createSelect(){
        this.selectQuery="SELECT " + this.getSelectHeader() + "\nfrom " + this.getName() + " \n";
        String group="";
        for (Column col: this.groupingAttributes){
            if (!group.equals(""))
                group+=", ";
            group+=col.getName() + " ";
        }
        this.selectQuery+="GROUP BY " + group +";\n";

    }

    public void createInsert(){
        this.insertQuery="INSERT INTO "+this.getName() +"_agg ("+ this.getNewTableHeader();
    }

    private void createGroupingColumnsNames(List<String> aggFormula){
        List<String> formColNames= new ArrayList<>();
        for (int i=0; i < aggFormula.size(); i++){
            String tabName=aggFormula.get(i);
            tabName=tabName.replaceAll("_", "");
            tabName=tabName.replaceFirst("\\(", "_");
            tabName=tabName.replaceAll("\\(", "");
            tabName=tabName.replaceAll("\\)", "");
            tabName=tabName.replaceAll("\\*", "");
            tabName=tabName.replaceAll("\\/", "");
            tabName=tabName.replaceAll("\\+", "");
            tabName=tabName.replaceAll("\\-", "");
            tabName=tabName.replaceAll("\\%", "");
            tabName=tabName.replaceAll(" +", ",");
            formColNames.add(tabName);
            System.out.print("\n---colname: " + tabName + "\n");
        }
        this.aggFormulaColumnNames=formColNames;
    }

    public AggTable(Table tab, List<String> aggFormula, List<String> aggFormulaResultType, List<Column> groupingAttributes){
        this.setName(tab.getName());
        this.setAggFormula(aggFormula);
        this.setAggFormulaResultType(aggFormulaResultType);
        this.setGroupingAttributes(groupingAttributes);
        this.createGroupingColumnsNames(aggFormula);

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
        this.createInsert();
        this.createSelect();
        this.createCreate();
        System.out.print(this.selectQuery);
        System.out.print(this.createQuery);
        System.out.print(this.insertQuery);
    }
}
