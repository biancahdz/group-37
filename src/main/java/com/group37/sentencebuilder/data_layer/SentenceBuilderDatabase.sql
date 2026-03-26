/*==============================================================*/
/*  File Name   : SentenceBuilderDatabase.sql                   */
/*  Description : Creates the tabel of the database and links   */
/*                all them all together                         */
/*                                                              */
/*  Author      : Cortland Kimzey                               */
/*  Created On  : 2026-01-29                                    */
/*                                                              */
/*  Last Modified By : Cortland Kimzey                          */
/*  Last Modified On : 2026-02-02                               */
/*  Change History : 2026-02-02 - Add header and table          */
/*                                descriptions                  */
/*                                                              */
/*  Database    : SentenceBuilder                               */
/*==============================================================*/

DROP DATABASE SentenceBuilder;
CREATE DATABASE IF NOT EXISTS SentenceBuilder;
USE SentenceBuilder;

-- One table for each txt file to hold different info about them
CREATE TABLE txt (
    txtID INT NOT NULL AUTO_INCREMENT,
    txtName VARCHAR(64) NOT NULL,
    importance INT,
    numSentences INT,
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

INSERT INTO words (word, wordID) VALUES ("<START>", 1);
INSERT INTO words (word, wordID) VALUES ("<END>", 2);