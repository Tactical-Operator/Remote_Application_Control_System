
package com.remotepulse.server.service;

import com.remotepulse.server.entity.Tile;
import com.remotepulse.server.repository.TileRepository;

import org.springframework.stereotype.Service;

import java.io.File;

/**
 * Handles saving custom executable application configurations.
 *
 * Validates the EXE path, updates the tile details,
 * extracts the executable's icon, and saves the tile.
 */
@Service
public class CustomAppSaveService {

    private final TileRepository tileRepository;

    public CustomAppSaveService(TileRepository tileRepository) {
        this.tileRepository = tileRepository;
    }

    /**
     * Configures a tile to launch a custom Windows executable.
     *
     * @param slotNumber the Remote Pulse slot number
     * @param name       the display name of the application
     * @param path       the full path to the executable
     * @return the saved tile configuration
     */
    public Tile saveCustomApp(
            Integer slotNumber,
            String name,
            String path) {

        // Ensure an executable path has been provided.
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException(
                    "EXE path cannot be empty.");
        }

        // Allow only paths ending with the .exe extension.
        if (!path.toLowerCase().endsWith(".exe")) {
            throw new IllegalArgumentException(
                    "Only .exe files are allowed.");
        }

        // Verify that the executable file exists.
        File exeFile = new File(path);

        if (!exeFile.exists()) {
            throw new IllegalArgumentException(
                    "EXE file does not exist: " + path);
        }

        // Find the existing tile using its slot number.
        Tile tile = tileRepository
                .findBySlotNumber(slotNumber)
                .orElseThrow(() -> new RuntimeException(
                        "Tile slot not found: " + slotNumber));

        // Update the tile with the custom application details.
        tile.setName(name);
        tile.setType("CUSTOM_APP");
        tile.setTarget(path);

        // Create the directory used to store extracted icons.
        File iconDirectory = new File("data/icons");

        if (!iconDirectory.exists()) {
            iconDirectory.mkdirs();
        }

        // Build the destination path for this tile's icon.
        String outputPath = new File(
                iconDirectory,
                "slot-" + slotNumber + ".png")
                .getAbsolutePath();

        // Extract the executable's icon using PowerShell.
        try {
            Process process = new ProcessBuilder(
                    "powershell.exe",
                    "-NoProfile",
                    "-ExecutionPolicy",
                    "Bypass",
                    "-File",
                    "extract-exe-icon.ps1",
                    path,
                    outputPath)
                    .redirectErrorStream(true)
                    .start();

            // Capture the script output for error reporting.
            String output = new String(
                    process.getInputStream().readAllBytes());

            // Wait for extraction to finish and check its exit code.
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new RuntimeException(
                        "EXE icon extraction failed:\n" + output);
            }

            // Verify that the extracted icon file exists.
            File iconFile = new File(outputPath);

            if (!iconFile.exists()) {
                throw new RuntimeException(
                        "Icon extraction completed, " +
                        "but icon file was not created.");
            }

            // Store the extracted icon filename in the tile.
            tile.setIcon("slot-" + slotNumber + ".png");

            // Log the location of the extracted icon.
            System.out.println(
                    "Custom App icon created: " +
                    iconFile.getAbsolutePath());

        } catch (Exception e) {
            // Report icon extraction failures to the caller.
            throw new RuntimeException(
                    "Failed to extract Custom App icon",
                    e);
        }

        // Save the updated tile configuration to the database.
        return tileRepository.save(tile);
    }
}
