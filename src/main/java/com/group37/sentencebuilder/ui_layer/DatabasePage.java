/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    DatabasePage.java
 *  Author:  Cortland Kimzey
 *
 *  Description:
 *      Contract for controllers that require a {@link Database} instance from the application shell.
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
     *      Injects the active {@link Database} instance so the page can query or mutate corpus data.
     *
     * @param db active database connection wrapper
     */
    public void setDatabase(Database db);
    
}