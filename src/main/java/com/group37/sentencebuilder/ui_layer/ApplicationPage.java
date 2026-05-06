/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    ApplicationPage.java
 *  Author:  Cortland Kimzey
 *
 *  Description:
 *      <description>
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
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    void onPageEnter();


    /**
     * Author: Cortland Kimzey
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    void onPageLeave();

}