/**
 * File: HomeController.java
 * Description: 
 *
 * Author: 
 * Created: 
 * Last Modified: 2026-03-26
 *
 * Version: 1.0
 */

package com.group37.sentencebuilder.ui_layer;

import com.group37.sentencebuilder.ui_layer.ApplicationPage;
import com.group37.sentencebuilder.ui_layer.DatabasePage;

import com.group37.sentencebuilder.DebugLog;
import com.group37.sentencebuilder.data_layer.Database;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Label;

import java.util.Map;
import java.util.function.Consumer;

/** Dashboard / welcome screen (placeholder content). */
public class HomeController implements ApplicationPage, DatabasePage {

    private Database database;

    private Consumer<ViewKey> navigator = k -> { };

    public void setNavigator(Consumer<ViewKey> navigator) {
        this.navigator = navigator != null ? navigator : k -> { };
    }

    @FXML
    private Label txtCount;

    @FXML
    private Label sentenceCount;

    @FXML
    private void onQuickImport(MouseEvent e) {
        navigator.accept(ViewKey.IMPORT);
    }

    @FXML
    private void onQuickGenerate(MouseEvent e) {
        navigator.accept(ViewKey.GENERATE);
    }

    @FXML
    private void onQuickAutocomplete(MouseEvent e) {
        navigator.accept(ViewKey.AUTOCOMPLETE);
    }

    @FXML
    private void onQuickReports(MouseEvent e) {
        navigator.accept(ViewKey.REPORTS);
    }

    @FXML
    private void onQuickCorpusStats(MouseEvent e) {
        navigator.accept(ViewKey.CORPUS_STATS);
    }

    @Override
    public void onPageEnter()
    {
        // #region agent log
        DebugLog.agent("pre-fix", "H2", "HomeController:onPageEnter", "enter", Map.of(
                "dbNull", String.valueOf(database == null)));
        // #endregion
        if (database == null) {
            txtCount.setText("—");
            sentenceCount.setText("—");
            // #region agent log
            DebugLog.agent("pre-fix", "H2", "HomeController:onPageEnter", "branch db null", Map.of());
            // #endregion
            return;
        }
        if (!database.connect()) {
            txtCount.setText("—");
            sentenceCount.setText("—");
            database.disconnect();
            // #region agent log
            DebugLog.agent("pre-fix", "H2", "HomeController:onPageEnter", "branch connect failed", Map.of());
            // #endregion
            return;
        }
        try {
            txtCount.setText(String.valueOf(database.getTxtCount()));
            sentenceCount.setText(String.valueOf(database.getTxtSentenceCount()));
            // #region agent log
            DebugLog.agent("pre-fix", "H2", "HomeController:onPageEnter", "db query ok", Map.of());
            // #endregion
        } finally {
            database.disconnect();
        }
    }

    @Override
    public void onPageLeave()
    {
    }

    @Override
    public void setDatabase(Database database)
    {
        this.database = database;
    }
}
