package md.beans;

public class TransTable {
    private Table table;

    private String selectQuery;
    private String createQuery;
    private String insertQuery;

    public void setTable(Table table) {
        this.table = table;
    }

    public Table getTable() {
        return table;
    }


    public void createQueries(){
        String head="";
        for (Column col: this.table.getCols()){
            if (!head.equals(""))
                head+=", ";
            head+=col.getName() + " ";
        }
        this.selectQuery="SELECT " + head + "\nfrom " + this.getTable().getName() + ";\n";
        this.insertQuery="INSERT INTO "+this.getTable().getName() + " VALUES (\n" ;

        head="";
        for (Column col: this.table.getCols()){
            if (!head.equals(""))
                head+=",\n\t";
            head+=col.getName() + " " + col.getType();
        }

        head+=",\n\tPRIMARY KEY(";
        boolean addCommaChar=false;
        for (Column col: this.table.getCols()){
            if (col.isPrimaryKey()) {
                if (addCommaChar)
                    head += ", ";
                head += col.getName() + " ";
                addCommaChar = true;
            }
        }
        this.createQuery="CREATE TABLE IF NOT EXISTS " + this.getTable().getName() + "(" + head + "));\n";
    }

    public void printQueries(){
        System.out.print("*****SELECT QUERY*******\n" + this.selectQuery + "\n");
        System.out.print("*****CREATE QUERY*******\n" + this.createQuery + "\n");
        System.out.print("*****INSERT QUERY*******\n" + this.insertQuery + "\n");
    }

    public TransTable(Table tab){
        this.setTable(tab);
        this.createQueries();
    }
}

