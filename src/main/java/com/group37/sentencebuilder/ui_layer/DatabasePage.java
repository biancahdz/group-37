/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    DatabasePage.java
 *  Author:  Cortland Kimzey
 *
 *  Description:
 *      Allows different pages to create a database object after they have been initialized and we have confirmed we can connect to the database
 *
 *  Version: 1.0
 *  Created: 2026-03-26
 *  Last Modified: 2026-03-27
 *
 *  Responsibilities:
 *      - A setDatabase function to be overwriten by a controller so they can create a database object
 * ------------------------------------------------------------
 */

package com.group37.sentencebuilder.ui_layer;

import com.group37.sentencebuilder.data_layer.Database;

public interface DatabasePage
{

    /**
     * Author: Cortland Kimzey
     * Description:
     *      Allows the controller to overwrite and create a database object
     *
     * @param db active database object
     */
    public void setDatabase(Database db);

}
