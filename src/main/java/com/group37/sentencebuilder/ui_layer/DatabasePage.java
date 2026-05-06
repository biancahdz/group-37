/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    DatabasePage.java
 *  Author:  Cortland Kimzey
 *
 *  Description:
 *      <description>
 *
 *  Version: 1.0
 *  Created: 2026-03-26
 *  Last Modified: 2026-03-27
 *
 *  Responsibilities:
 *      - Allow a page to create a database object
 * ------------------------------------------------------------
 */

package com.group37.sentencebuilder.ui_layer;

import com.group37.sentencebuilder.data_layer.Database;

public interface DatabasePage
{

    /**
     * Author: Cortland Kimzey
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public void setDatabase(Database db);
    
}