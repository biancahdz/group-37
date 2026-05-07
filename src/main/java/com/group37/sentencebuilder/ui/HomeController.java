/**
 
------------------------------------------------------------
Project: Sentence Builder
File:    HomeController.java
Author:  Huy Nong
Description:
Controller for the Home dashboard page; forwards quick actions to navigate to other screens.
Version: 1.0
Created: 2026-03-22
Last Modified: 2026-05-07
Responsibilities:
Accept and store a navigation callback for the shell
Handle quick-action clicks and route to the requested view
------------------------------------------------------------*/

package com.group37.sentencebuilder.ui;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

import java.util.function.Consumer;

/** Dashboard / welcome screen (placeholder content). */
public class HomeController {

    private static final Consumer<ViewKey> NOOP_NAVIGATOR = k -> { };

    private Consumer<ViewKey> navigator = NOOP_NAVIGATOR;

    public void setNavigator(Consumer<ViewKey> navigator) {
        this.navigator = navigator != null ? navigator : NOOP_NAVIGATOR;
    }

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
}
