package com.remotepulse.server.service;

import com.remotepulse.server.entity.Tile;
import com.remotepulse.server.repository.TileRepository;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class CustomAppSaveService {

    private final TileRepository tileRepository;

    public CustomAppSaveService(TileRepository tileRepository) {
        this.tileRepository = tileRepository;
    }

    public Tile saveCustomApp(
            Integer slotNumber,
            String name,
            String path
    ) {

        /*
         * Make sure the path points to an EXE file.
         */
        if (path == null ||
                !path.toLowerCase().endsWith(".exe")) {

            throw new IllegalArgumentException(
                    "Only .exe files are allowed."
            );
        }

        /*
         * Make sure the path is not empty.
         */
        if (path.isBlank()) {

            throw new IllegalArgumentException(
                    "EXE path cannot be empty."
            );
        }

        /*
         * Find the tile using its slot number.
         */
        Tile tile = tileRepository
                .findBySlotNumber(slotNumber)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Tile slot not found: " + slotNumber
                        )
                );

        /*
         * Update the tile.
         */
        tile.setName(name);
        tile.setType("CUSTOM_APP");
        tile.setTarget(path);

        /*
         * Save the updated tile.
         */
        return tileRepository.save(tile);
    }
}