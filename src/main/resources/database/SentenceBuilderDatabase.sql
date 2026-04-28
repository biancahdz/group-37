/*==============================================================*/
/*  Canonical MySQL schema for Sentence Builder (data_layer).   */
/*  Run this once in MySQL Workbench or CLI to create the DB.    */
/*  Java code connects via data_layer.Database (see db_config). */
/*==============================================================*/

DROP DATABASE IF EXISTS SentenceBuilder;
CREATE DATABASE IF NOT EXISTS SentenceBuilder;
USE SentenceBuilder;

-- One table for each txt file to hold different info about them
CREATE TABLE txt (
    txtID INT NOT NULL AUTO_INCREMENT,
    txtName VARCHAR(64) NOT NULL,
    numSentences INT,
    numWords INT,
    dateAdded DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (txtID)
) ENGINE=InnoDB;

-- One table for every unique word found in the txt files
CREATE TABLE words (
    wordID INT NOT NULL AUTO_INCREMENT,
    word VARCHAR(64) NOT NULL,
    wordCount INT NOT NULL DEFAULT 1,
    PRIMARY KEY (wordID),
    UNIQUE (word)
) ENGINE=InnoDB;

-- One table for each sentence in a txt file
-- This table allows us to track the first and last word of each sentence
CREATE TABLE sentence (
    sentenceID INT NOT NULL AUTO_INCREMENT,
    firstWordID INT NOT NULL,
    lastWordID INT NOT NULL,
    sentenceCount INT NOT NULL DEFAULT 1,
    PRIMARY KEY (sentenceID),
    FOREIGN KEY (firstWordID) REFERENCES words(wordID),
    FOREIGN KEY (lastWordID) REFERENCES words(wordID)
) ENGINE=InnoDB;

-- One table for every combination of two words to track
-- how often a word appears after another
CREATE TABLE nextWord (
    wordID INT NOT NULL,
    nextWordID INT NOT NULL,
    comboCount INT NOT NULL DEFAULT 1,
    PRIMARY KEY (wordID, nextWordID),
    FOREIGN KEY (wordID) REFERENCES words(wordID),
    FOREIGN KEY (nextWordID) REFERENCES words(wordID)
) ENGINE=InnoDB;

-- Per-import word and bigram counts (Word analytics scoped to one txt row)
CREATE TABLE txt_word (
    txtID INT NOT NULL,
    wordID INT NOT NULL,
    occurrenceCount INT NOT NULL DEFAULT 0,
    PRIMARY KEY (txtID, wordID),
    FOREIGN KEY (txtID) REFERENCES txt(txtID) ON DELETE CASCADE,
    FOREIGN KEY (wordID) REFERENCES words(wordID)
) ENGINE=InnoDB;

CREATE TABLE txt_nextword (
    txtID INT NOT NULL,
    wordID INT NOT NULL,
    nextWordID INT NOT NULL,
    comboCount INT NOT NULL DEFAULT 0,
    PRIMARY KEY (txtID, wordID, nextWordID),
    FOREIGN KEY (txtID) REFERENCES txt(txtID) ON DELETE CASCADE,
    FOREIGN KEY (wordID) REFERENCES words(wordID),
    FOREIGN KEY (nextWordID) REFERENCES words(wordID)
) ENGINE=InnoDB;

CREATE TABLE reports (
    reportID INT NOT NULL AUTO_INCREMENT,
    algorithmName VARCHAR(64) NOT NULL,
    dateAdded DATETIME DEFAULT CURRENT_TIMESTAMP,
    sentence VARCHAR(256) NOT NULL,
    PRIMARY KEY (reportID)
) ENGINE=InnoDB;

INSERT INTO words (word, wordID) VALUES ("<START>", 1);
INSERT INTO words (word, wordID) VALUES ("<END>", 2);
