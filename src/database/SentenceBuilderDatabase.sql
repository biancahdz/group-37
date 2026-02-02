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

USE SentenceBuilder;

-- One table for each txt file to hold different info about them
CREATE TABLE txt (
	txtID INT NOT NULL IDENTITY(1,1) PRIMARY KEY,
	txtName VARCHAR(64) NOT NULL,
	importance INT,
	numSentences INT
);

-- One table for every unique word found in the txt files
CREATE TABLE word (
	wordID INT NOT NULL IDENTITY(1,1) PRIMARY KEY,
	word VARCHAR(64) NOT NULL,
	UNIQUE (word)
);

-- One table for each sentence in a txt file
-- This table allows us to track the first and last word of each sentece
CREATE TABLE sentence (
  sentenceID INT NOT NULL IDENTITY(1,1),
  txtID INT NOT NULL,
  firstWordID INT NOT NULL,
  lastWordID INT NOT NULL,
  PRIMARY KEY (sentenceID, txtID),
  FOREIGN KEY (txtID) REFERENCES txt(txtID),
  FOREIGN KEY (firstWordID) REFERENCES word(wordID),
  FOREIGN KEY (lastWordID) REFERENCES word(wordID)
);

-- One table for every combination of two word to track
-- how often a word appears after
CREATE TABLE nextWord (
  wordID INT NOT NULL,
  nextWordID INT NOT NULL,
  count INT NOT NULL DEFAULT 1,
  PRIMARY KEY (wordID, nextWordID),
  FOREIGN KEY (wordID) REFERENCES word(wordID),
  FOREIGN KEY (nextWordID) REFERENCES word(wordID)
);
