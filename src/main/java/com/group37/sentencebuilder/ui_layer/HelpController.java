package com.group37.sentencebuilder.ui_layer;

import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;

/**
 * Static help / documentation view (no extra state).
 */
public class HelpController implements ApplicationPage {

    @FXML
    private Hyperlink supportMailHyperlink;

    @FXML
    private void initialize() {
        supportMailHyperlink.setText(Mailto.SUPPORT_EMAIL);
    }

    @FXML
    private void onSupportMailClicked() {
        Mailto.openSupportInbox();
    }

    @Override
    public void onPageEnter() {
    }

    @Override
    public void onPageLeave() {
    }
}
