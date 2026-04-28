/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    .java
 *  Author:  
 *
 *  Description:
 *      <description>
 *
 *  Version: 1.0
 *  Created: 
 *  Last Modified: 
 *
 *  Responsibilities:
 *      - <responsibilities 1>
 *      - <responsibilities 2>
 * ------------------------------------------------------------
 */

package com.group37.sentencebuilder.ui_layer;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

/**
 * Opens the system default mail client with a {@code mailto:} URI.
 */
public final class Mailto {

    public static final String SUPPORT_EMAIL = "sentencebuildersupport@gmail.com";

    private Mailto() {
    }

    public static void openSupportInbox() {
        open(SUPPORT_EMAIL);
    }

    /**
     * @param address recipient local-part@domain (no {@code mailto:} prefix)
     */
    public static void open(String address) {
        if (address == null || address.isBlank()) {
            return;
        }
        String trimmed = address.trim();
        URI uri = URI.create("mailto:" + trimmed);
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.MAIL)) {
                    desktop.mail(uri);
                    return;
                }
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(uri);
                    return;
                }
            }
        } catch (IOException | UnsupportedOperationException ignored) {
        }
    }
}
