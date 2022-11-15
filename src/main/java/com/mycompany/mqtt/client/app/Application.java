
/**
 *
 * Code adapted from code available at The Pi4J Project on GitHub:
 * https://github.com/Pi4J/pi4j-example-components
 * 
 */

package com.mycompany.mqtt.client.app;

import java.util.logging.Logger;

/**
 * This interface should be implemented by each CrowPi example / application
 */

public interface Application {
 
    /**
     * Utility function to sleep for the specified amount of milliseconds.
     * An {@link InterruptedException} will be catched and ignored while setting the interrupt flag again.
     *
     * @param milliseconds Time in milliseconds to sleep
     */
    default void delay(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Logger instance
     */
    Logger logger = Logger.getLogger("Pi4J-App");

    default void logInfo(String msg){
        logger.info(() -> msg);
    }
    default void logError(String msg){
        logger.severe(() -> msg);
    }
}
