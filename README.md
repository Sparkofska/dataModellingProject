# dataModellingProject
Curso "Modelação de Dados", Ano letivo 17/18

interesting link to database parser: https://stackoverflow.com/a/5477203/4649452

# Project Specification

**"A tool that transforms a given OLTP database into a multidimensional model."**

## Steps To Do

1. find recent work on that topic ("State of the art")
    - lecture "from OLTP to Dimensional Models"
    - "Moody"-paper
    - be experts on these
2. read Metadata(Tables, Columns, PrimaryKeys...) from arbitrary OLTP database
3. present suggestions of fact-tables to the user
    - use appropriate heuristics for that
    - find heuristics in the paper
4. let the process be saveable at any appropriate step
    - *user can save, interupt the process and return after lunch to continue his work*
    - save state of progress to file or similar and parse it to continue
5. Let the user select (make agreement between user and machine) which tables to use as fact-tables and which scheme to use
6. Decide about a specific approach to construct the multidimensional model. Can be the one from Moody's paper or any other. Need to be discussed and presented in the report.
7. build the tables according to multidimensional scheme. Several options here: generate many SQLscripts, that let the user build the database incrementally, create all tables in one big rush, or somehow else
8. populate data

The last two steps are not well defined until now, we need to discuss with professor about that again.

Share all our work (pdf report, and stuff) with professor in the Google Drive folder. So he can observe it and give us hints or arrange another appointment in his office to speak about it.

We need to submit:
 - pdf report
 - 20min presentation on 19.-21. December

# scripts

**mysql Cheatsheet**
```
mysql -u root -p
show databases;
use <dbName>;
show tables;
SELECT * FROM <tableName>;
exit
```

**build script**
```
mvn package
```

**run script**
```
java -cp target/project-0.0.1-SNAPSHOT.jar md.Hello
```
