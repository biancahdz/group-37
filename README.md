# group-37
Sentence Builder CS Project 
Advisor: John Cole 
Group Members: Bianca, Cortland, Huy Nhat, Sebastian, Asher, Amrita

Objective: 
This project is an experimental system that builds a database of words from various sources.  This database can then be used to construct sentences based upon various criteria. 

Tech Stack:
Language- Java
GUI Framework: JavaFX
Database: MySQL
IDE: IntelliJ

---
1. Make sure you have the following installed:
    * Java JDK
    * MySQL Server
    * MySQL Workbench
    * VS Code

2. Clone the repo

3. Set up MySQL Database
    * Open MySQL Workbench
    * Connect to:
        * Host: localhost
        * Port: 3306
        * User: root
    * Open and run:
        * src/database/SentenceBuilderDatabase.sql

4. Configure Database Connection
    * Open the db/DatabaseConnection.java file and update your MySQL password

5. Compile the Project:
```
javac -cp lib/mysql-connector-j-9.6.0.jar -d out src/Main.java src/ingestion/TextFileImporter.java src/DAO/*.java src/db/*.java
```

6. Run:
```
# Windows
java -cp "out;lib/mysql-connector-j-9.6.0.jar" src.Main

# Mac/Linux
java -cp "out:lib/mysql-connector-j-9.6.0.jar" src.Main
```