
package com.remotepulse.server.service;

import com.remotepulse.server.entity.Tile;
import com.remotepulse.server.repository.TileRepository;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class CustomAppSaveService {

    private final TileRepository tileRepository;

    public CustomAppSaveService(
            TileRepository tileRepository
    ) {
        this.tileRepository = tileRepository;
    }

    public Tile saveCustomApp(
            Integer slotNumber,
            String name,
            String path
    ) {

        /*
         * Make sure the path is not empty.
         */
        if (path == null || path.isBlank()) {

            throw new IllegalArgumentException(
                    "EXE path cannot be empty."
            );
        }

        /*
         * Make sure the path points to an EXE file.
         */
        if (!path.toLowerCase().endsWith(".exe")) {

            throw new IllegalArgumentException(
                    "Only .exe files are allowed."
            );
        }

        /*
         * Make sure the EXE actually exists.
         */
        File exeFile = new File(path);

        if (!exeFile.exists()) {

            throw new IllegalArgumentException(
                    "EXE file does not exist: " + path
            );
        }

        /*
         * Find the tile using its slot number.
         */
        Tile tile =
                tileRepository
                        .findBySlotNumber(slotNumber)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Tile slot not found: "
                                                + slotNumber
                                )
                        );

        /*
         * Update the tile information.
         */
        tile.setName(name);

        tile.setType("CUSTOM_APP");

        tile.setTarget(path);

        /*
         * Create the output path for this tile's icon.
         *
         * Example:
         * slotNumber = 3
         *
         * Result:
         * src/main/resources/static/icons/slot-3.png
         */
        String outputPath =
                new File(
                        "src/main/resources/static/icons",
                        "slot-" + slotNumber + ".png"
                )
                .getAbsolutePath();

        /*
         * Extract the EXE icon.
         */
        try {

            Process process =
                    new ProcessBuilder(
                            "powershell.exe",
                            "-NoProfile",
                            "-ExecutionPolicy",
                            "Bypass",
                            "-File",
                            "extract-exe-icon.ps1",
                            path,
                            outputPath
                    )
                    .redirectErrorStream(true)
                    .start();

            String output =
                    new String(
                            process.getInputStream()
                                    .readAllBytes()
                    );

            int exitCode =
                    process.waitFor();

            if (exitCode != 0) {

                throw new RuntimeException(
                        "EXE icon extraction failed:\n"
                                + output
                );
            }

            File iconFile =
                    new File(outputPath);

            if (!iconFile.exists()) {

                throw new RuntimeException(
                        "Icon extraction completed, "
                                + "but icon file was not created."
                );
            }

            /*
             * Store the icon filename in the database.
             */
            tile.setIcon(
                    "slot-" + slotNumber + ".png"
            );

            System.out.println(
                    "Custom App icon created: "
                            + iconFile.getAbsolutePath()
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to extract Custom App icon",
                    e
            );
        }

        /*
         * Save the tile.
         */
        return tileRepository.save(tile);
    }
}

