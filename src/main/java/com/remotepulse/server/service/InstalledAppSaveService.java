
package com.remotepulse.server.service;

import com.remotepulse.server.entity.Tile;
import com.remotepulse.server.repository.TileRepository;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
public class InstalledAppSaveService {

    private final TileRepository tileRepository;

    private final IconExtractionService iconExtractionService;

    public InstalledAppSaveService(
            TileRepository tileRepository,
            IconExtractionService iconExtractionService
    ) {

        this.tileRepository =
                tileRepository;

        this.iconExtractionService =
                iconExtractionService;
    }

    public Tile saveInstalledApp(
            Integer slotNumber,
            String name,
            String appId
    ) {

        Tile tile =
                tileRepository
                        .findBySlotNumber(slotNumber)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Tile slot not found: " +
                                        slotNumber
                                )
                        );

        tile.setName(name);

        tile.setType("INSTALLED_APP");

        tile.setTarget(appId);

        try {

            String outputPath =
                    Path.of(
                            "src",
                            "main",
                            "resources",
                            "static",
                            "icons",
                            "slot-" +
                                    slotNumber +
                                    ".png"
                    )
                    .toAbsolutePath()
                    .toString();

            iconExtractionService.extractInstalledAppIcon(
                    appId,
                    outputPath
            );

            tile.setIcon(
                    "slot-" +
                            slotNumber +
                            ".png"
            );

        } catch (Exception e) {

            System.out.println(
                    "Could not extract icon for: " +
                    name
            );

            e.printStackTrace();
        }

        return tileRepository.save(tile);
    }
}

