/**
 
------------------------------------------------------------
Project: Sentence Builder
File:    ViewKey.java
Author:  Huy Nong
Description:
Enum of view identifiers used for UI navigation within the main shell.
Version: 1.0
Created: 2026-03-22
Last Modified: 2026-05-07
Responsibilities:
Define stable keys for each top-level UI screen
Support routing and selection logic in navigation components
------------------------------------------------------------*/

package com.group37.sentencebuilder.ui;

/**
 * Identifiers for main shell views (UI navigation only).
 */
public enum ViewKey {
    // Keep names aligned with sidebar/nav config so routing remains stable.
    HOME,
    IMPORT,
    GENERATE,
    AUTOCOMPLETE,
    REPORTS,
    SETTINGS
}
