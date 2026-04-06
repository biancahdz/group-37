-- Run once against an existing SentenceBuilder database that was created
-- before per-file analytics. Safe to re-run (IF NOT EXISTS).

USE SentenceBuilder;

CREATE TABLE IF NOT EXISTS txt_word (
    txtID INT NOT NULL,
    wordID INT NOT NULL,
    occurrenceCount INT NOT NULL DEFAULT 0,
    PRIMARY KEY (txtID, wordID),
    FOREIGN KEY (txtID) REFERENCES txt(txtID) ON DELETE CASCADE,
    FOREIGN KEY (wordID) REFERENCES words(wordID)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS txt_nextword (
    txtID INT NOT NULL,
    wordID INT NOT NULL,
    nextWordID INT NOT NULL,
    comboCount INT NOT NULL DEFAULT 0,
    PRIMARY KEY (txtID, wordID, nextWordID),
    FOREIGN KEY (txtID) REFERENCES txt(txtID) ON DELETE CASCADE,
    FOREIGN KEY (wordID) REFERENCES words(wordID),
    FOREIGN KEY (nextWordID) REFERENCES words(wordID)
) ENGINE=InnoDB;

-- Re-import .txt files after migration so txt_word / txt_nextword populate.
