
/**
 *
 * Code adapted from code available at The Pi4J Project on GitHub:
 * https://github.com/Pi4J/pi4j-example-components
 * 
 */

package com.mycompany.mqtt.client.app;

import java.util.logging.Logger;

public abstract class Component {
    /**
     * Logger instance
     */
    private final Logger logger = Logger.getLogger(getClass().getName());

    protected void logInfo(String msg) {
        logger.info(() -> msg);
    }

    protected void logError(String msg) {
        logger.severe(() -> msg);
    }

    protected void logConfig(String msg) {
        logger.config(() -> msg);
    }

    protected void logDebug(String msg) {
        logger.fine(() -> msg);
    }

    /**
     * Utility function to sleep for the specified amount of milliseconds.
     * An {@link InterruptedException} will be catched and ignored while setting the interrupt flag again.
     *
     * @param milliseconds Time in milliseconds to sleep
     */
    void delay(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
