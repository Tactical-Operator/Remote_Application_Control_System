
package com.remotepulse.server.service;

import com.remotepulse.server.entity.Tile;
import com.remotepulse.server.repository.TileRepository;

import org.springframework.stereotype.Service;

import java.nio.file.Path;

/**
 * Handles saving website configurations for Remote Pulse tiles.
 *
 * Validates the website URL, updates the tile details,
 * attempts to extract the website icon, and saves the tile.
 */
@Service
public class WebsiteSaveService {

    private final TileRepository tileRepository;

    public WebsiteSaveService(TileRepository tileRepository) {
        this.tileRepository = tileRepository;
    }

    /**
     * Configures a tile to open the specified website.
     *
     * @param slotNumber the Remote Pulse slot number
     * @param name       the display name of the website
     * @param url        the website URL
     * @return the saved tile configuration
     */
    public Tile saveWebsite(
            Integer slotNumber,
            String name,
            String url) {

        // Ensure a website URL has been provided.
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException(
                    "Website URL cannot be empty.");
        }

        // Accept only URLs beginning with HTTP or HTTPS.
        if (!url.startsWith("http://") &&
                !url.startsWith("https://")) {

            throw new IllegalArgumentException(
                    "Website URL must start with http:// or https://");
        }

        // Find the existing tile using its slot number.
        Tile tile = tileRepository
                .findBySlotNumber(slotNumber)
                .orElseThrow(() -> new RuntimeException(
                        "Tile slot not found: " + slotNumber));

        // Update the tile with the website configuration.
        tile.setName(name);
        tile.setType("WEBSITE");
        tile.setTarget(url);

        try {
            // Create the directory used to store custom tile icons.
            Path iconDirectory = Path.of("data", "icons");

            java.nio.file.Files.createDirectories(iconDirectory);

            // Build the output path for this slot's icon.
            String outputPath = Path.of(
                    "data",
                    "icons",
                    "slot-" + slotNumber + ".png")
                    .toAbsolutePath()
                    .toString();

            // Run the PowerShell script that extracts the website icon.
            Process process = new ProcessBuilder(
                    "powershell.exe",
                    "-NoProfile",
                    "-ExecutionPolicy",
                    "Bypass",
                    "-File",
                    "extract-web-icon.ps1",
                    url,
                    outputPath)
                    .inheritIO()
                    .start();

            // Wait until the icon extraction process finishes.
            int exitCode = process.waitFor();

            // Treat an unsuccessful extraction as an error.
            if (exitCode != 0) {
                throw new RuntimeException(
                        "Website icon extraction failed.");
            }

            // Store the extracted icon filename in the tile.
            tile.setIcon("slot-" + slotNumber + ".png");

        } catch (Exception e) {
            // Icon extraction errors do not prevent saving the tile.
            System.out.println(
                    "Could not extract website icon for: " + name);

            e.printStackTrace();
        }

        // Save the updated tile configuration to the database.
        return tileRepository.save(tile);
    }
}
