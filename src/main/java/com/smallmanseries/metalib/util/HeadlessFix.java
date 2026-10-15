package com.smallmanseries.metalib.util;

import com.smallmanseries.metalib.MetaLib;

import java.awt.*;

public class HeadlessFix {
    private static volatile boolean fixed = false;

    /**
     * Ensure that the game is not in Headless mode.
     * Call this before AWT operation to avoid {@link HeadlessException}.
     */
    public static synchronized void ensureNonHeadless() {
        if (fixed) return;
        try {
            System.setProperty("java.awt.headless", "false");
            // trigger AWT initialize
            Toolkit.getDefaultToolkit();
            fixed = true;
            MetaLib.LOGGER.info("AWT initialized in non-headless mode.");
        } catch (Throwable t) {
            MetaLib.LOGGER.error("Failed to initialize AWT", t);
        }
    }
}
