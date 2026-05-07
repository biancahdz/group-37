/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    ApplicationPage.java
 *  Author:  Cortland Kimzey
 *
 *  Description:
 *      Lifecycle interface for page controllers hosted by the main shell.
 *
 *  Version: 1.0
 *  Created: 2026-03-26
 *  Last Modified: 2026-03-24
 *
 *  Responsibilities:
 *      - Creates functions to be overwritten by the page using them
 *      - Allows the page to perform actions when entered and left
 * ------------------------------------------------------------
 */


package com.group37.sentencebuilder.ui_layer;

public interface ApplicationPage {

    /**
     * Author: Cortland Kimzey
     * Description: 
     *      Called when the page becomes visible/active in the shell.
     */
    void onPageEnter();


    /**
     * Author: Cortland Kimzey
     * Description: 
     *      Called when the page is being hidden/deactivated in the shell.
     */
    void onPageLeave();

}