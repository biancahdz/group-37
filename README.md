# group-37

Sentence Builder CS Project  
Advisor: John Cole  
Group Members: Bianca, Cortland, Huy Nhat, Sebastian, Asher, Amrita

## Objective

This project builds a database of words from text sources and uses it to construct sentences under various criteria.

## Tech stack

- Language: Java 17
- GUI: JavaFX
- Database: MySQL (single schema: **`SentenceBuilder`**)
- Build: Maven

---

## Prerequisites

- JDK 17
- Maven
- MySQL Server (listening on `localhost:3306`)

## 1. Create the database (one canonical schema)

Run the script **once** in MySQL Workbench or the `mysql` CLI:

`src/main/resources/database/SentenceBuilderDatabase.sql`

This creates the **`SentenceBuilder`** database and tables (`txt`, `words`, `sentence`, `nextWord`) used by `data_layer.Database`.

> **Note:** The script includes `DROP DATABASE SentenceBuilder;` — do not run it against data you need to keep.

The ER diagram PDF is in the same folder: `src/main/resources/database/SentenceBuilder ER Diagram.pdf`

## 2. Configure credentials

The app reads **`data/db_config.txt`** at the **working directory** when you launch it (project root if you use `mvn javafx:run`):

- Line 1: MySQL username (e.g. `root`)
- Line 2: MySQL password

If the file is missing, defaults are used until you log in from the title screen (which writes `data/db_config.txt` on success).

All JDBC access goes through **`com.group37.sentencebuilder.data_layer.Database`** and targets **`Database.DEFAULT_DATABASE_NAME`** (`SentenceBuilder`).

## 3. Build and run

```bash
mvn -q compile
mvn javafx:run
```

---

## Legacy note

Older docs referred to `src/database/SentenceBuilderDatabase.sql`, `DatabaseConnection.java`, and a separate DAO package. Those have been removed; use the paths and class above only.
