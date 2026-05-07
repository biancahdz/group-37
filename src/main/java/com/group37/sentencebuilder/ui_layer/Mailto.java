/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    Mailto.java
 *  Author:  Huy Nong, Cortland Kimzey
 *
 *  Description:
 *      Utility for opening the system default mail client to a support email address using a mailto: URI.
 *
 *  Version: 1.0
 *  Created: 2026-05-06
 *  Last Modified: 2026-05-06
 *
 *  Responsibilities:
 *      - Provide a single support email constant used across the UI
 *      - Open the OS mail handler safely when available
 * ------------------------------------------------------------
 */

package com.group37.sentencebuilder.ui_layer;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

/** Opens the system default mail client with a mailto: URI. */
public final class Mailto {

    public static final String SUPPORT_EMAIL = "sentencebuildersupport@gmail.com";

    /**
     * Author: Huy Nong, Cortland Kimzey
     * Description:
     *      Prevents instantiation; all members are static.
     */
    private Mailto() {
    }

    /**
     * Author: Huy Nong, Cortland Kimzey
     * Description:
     *      Opens the system default mail client addressed to the support inbox.
     */
    public static void openSupportInbox() {
        open(SUPPORT_EMAIL);
    }

    /**
     * Author: Huy Nong, Cortland Kimzey
     * Description:
     *      Opens the OS default mail handler for the given address, falling back to the
     *      browser if MAIL action is unsupported. Silently does nothing if the platform
     *      has no desktop support or the address is blank.
     *
     * @param address recipient address in local-part@domain form (no mailto: prefix)
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
