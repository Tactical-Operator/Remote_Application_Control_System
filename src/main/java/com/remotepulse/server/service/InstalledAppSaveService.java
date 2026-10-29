
package com.remotepulse.server.service;

import com.remotepulse.server.entity.Tile;
import com.remotepulse.server.repository.TileRepository;

import org.springframework.stereotype.Service;

import java.nio.file.Path;

/**
 * Handles saving installed application configurations for Remote Pulse tiles.
 *
 * Updates the tile details, extracts the application's icon,
 * and saves the configuration to the database.
 */
@Service
public class InstalledAppSaveService {

    private final TileRepository tileRepository;
    private final IconExtractionService iconExtractionService;

    public InstalledAppSaveService(
            TileRepository tileRepository,
            IconExtractionService iconExtractionService) {

        this.tileRepository = tileRepository;
        this.iconExtractionService = iconExtractionService;
    }

    /**
     * Configures a tile to launch an installed application.
     *
     * @param slotNumber the Remote Pulse slot number
     * @param name       the application's display name
     * @param appId      the identifier used to launch the application
     * @return the saved tile configuration
     */
    public Tile saveInstalledApp(
            Integer slotNumber,
            String name,
            String appId) {

        // Find the existing tile using its slot number.
        Tile tile = tileRepository
                .findBySlotNumber(slotNumber)
                .orElseThrow(() -> new RuntimeException(
                        "Tile slot not found: " + slotNumber));

        // Store the installed application's configuration.
        tile.setName(name);
        tile.setType("INSTALLED_APP");
        tile.setTarget(appId);

        try {
            // Create the directory used to store custom tile icons.
            Path iconDirectory = Path.of("data", "icons");

            java.nio.file.Files.createDirectories(iconDirectory);

            // Build the output path for this slot's application icon.
            String outputPath = Path.of(
                    "data",
                    "icons",
                    "slot-" + slotNumber + ".png")
                    .toAbsolutePath()
                    .toString();

            // Extract the application's icon and save it to the output path.
            iconExtractionService.extractInstalledAppIcon(
                    appId,
                    outputPath);

            // Store the extracted icon filename in the tile.
            tile.setIcon("slot-" + slotNumber + ".png");

        } catch (Exception e) {
            // Continue saving the tile even if icon extraction fails.
            System.out.println(
                    "Could not extract icon for: " + name);

            e.printStackTrace();
        }

        // Save the updated tile configuration to the database.
        return tileRepository.save(tile);
    }
}
