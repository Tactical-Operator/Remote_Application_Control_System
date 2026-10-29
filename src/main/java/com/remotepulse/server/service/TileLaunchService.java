
package com.remotepulse.server.service;

import com.remotepulse.server.entity.Tile;
import org.springframework.stereotype.Service;

/**
 * Handles the execution of configured Remote Pulse tiles.
 *
 * Determines the appropriate launch operation based on the
 * tile type and delegates system-button actions to
 * SystemButtonService.
 */
@Service
public class TileLaunchService {

    private final SystemButtonService systemButtonService;

    /**
     * Injects the service responsible for Windows system actions.
     *
     * @param systemButtonService handles system-button execution
     */
    public TileLaunchService(
            SystemButtonService systemButtonService) {
        this.systemButtonService = systemButtonService;
    }

    /**
     * Launches or executes a tile according to its configured type.
     *
     * @param tile the tile containing its type and target
     */
    public void launch(Tile tile) {

        try {

            // Launch a custom application through Windows Terminal.
            if (tile.getType().equals("CUSTOM_APP")) {

                new ProcessBuilder(
                        "wt.exe",
                        "new-tab",
                        tile.getTarget()
                ).start();

            // Open the configured website using the Windows command.
            } else if (tile.getType().equals("WEBSITE")) {

                new ProcessBuilder(
                        "cmd",
                        "/c",
                        "start",
                        "",
                        tile.getTarget()
                ).start();

            // Launch an installed application through the AppsFolder.
            } else if (tile.getType().equals("INSTALLED_APP")) {

                System.out.println("=== NEW INSTALLED APP CODE ===");

                new ProcessBuilder(
                        "explorer.exe",
                        "shell:AppsFolder\\" + tile.getTarget()
                ).start();

            // Delegate system-button actions to the system service.
            } else if (tile.getType().equals("SYSTEM_BUTTON")) {

                systemButtonService.execute(
                        tile.getTarget()
                );

            // Reject tile types that are not supported.
            } else {

                throw new RuntimeException(
                        "Unknown tile type: " + tile.getType()
                );
            }

        } catch (Exception e) {

            // Wrap launch failures with a consistent error message.
            throw new RuntimeException(
                    "Failed to launch tile",
                    e
            );
        }
    }
}
