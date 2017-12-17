package md.beans;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AggTable implements TableSQLCreator{
    private Table table;

    private List<String> aggFormula;
    private List<String> aggFormulaResultType;
    private List<String> aggFormulaColumnNames;
    private List<Column> groupingAttributes;

    private String selectQuery;
    private String createQuery;
    private String insertQuery;

    public void setTable(Table table) {
        this.table = table;
    }

    public Table getTable() {
        return table;
    }

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
        this.createQuery="CREATE TABLE IF NOT EXISTS " + this.getTable().getName() + "_agg" + "(" + head + "));\n";
    }

    public void createSelect(){
        this.selectQuery="SELECT " + this.getSelectHeader() + "\nfrom " + this.getTable().getName() + " \n";
        String group="";
        for (Column col: this.groupingAttributes){
            if (!group.equals(""))
                group+=", ";
            group+=col.getName() + " ";
        }
        this.selectQuery+="GROUP BY " + group +";\n";

    }

    public void createInsert(){
        this.insertQuery="INSERT INTO "+this.getTable().getName() +"_agg ("+ this.getNewTableHeader() + ")";
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
            tabName=tabName.replaceAll(" +", "_");
            formColNames.add(tabName);
        }
        this.aggFormulaColumnNames=formColNames;
    }

    private void createAggColumnTypes(){
        this.aggFormulaResultType= new ArrayList<>();
        List<String> floatTypes = Arrays.asList("FLOAT","REAL", "DOUBLE");

        for (String form: this.aggFormula){
            String type = "BIGINT";
            for(Column col: this.getTable().getCols()){
                if (!type.equals("BIGINT"))
                    break;
                if (form.contains(col.getName())){
                    for(String floatType: floatTypes){
                        if (col.getType().contains(floatType)){
                            type=col.getType();
                            this.aggFormulaResultType.add(new String(type));
                            break;
                        }
                    }
                }

            }
        }
    }

    public void printQueries(){
        System.out.print("*****SELECT QUERY*******\n" + this.selectQuery + "\n");
        System.out.print("*****CREATE QUERY*******\n" + this.createQuery + "\n");
        System.out.print("*****INSERT QUERY*******\n" + this.insertQuery + "\n");
    }

    public AggTable(Table tab, List<String> aggFormula, List<Column> groupingAttributes){
        this.setTable(tab);
        this.setAggFormula(aggFormula);
        this.setGroupingAttributes(groupingAttributes);
        this.createGroupingColumnsNames(aggFormula);
        this.createAggColumnTypes();

        this.createInsert();
        this.createSelect();
        this.createCreate();
    }

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
}
