package io.alerium.playershops.util;

import java.util.logging.Logger;

public final class Logs {
    
    private static final Logger logger = UtilServer.getPlugin().getLogger();

    private Logs() {
    }

    public static void info(String msg) {
        logger.info(msg);
    }

    public static void error(String msg) {
        logger.severe(msg);
    }
}
