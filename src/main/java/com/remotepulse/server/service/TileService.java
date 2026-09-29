package com.remotepulse.server.service;

import com.remotepulse.server.entity.Tile;
import com.remotepulse.server.repository.TileRepository;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;


// The service sits between the controller and repository.

// PC interface / Android
//           ↓
//       Controller
//           ↓
//        Service
//           ↓
//       Repository
//           ↓
//         MySQL

@Service
public class TileService {

    private final TileRepository tileRepository;

    // Constructor Dependency Injection
    public TileService(TileRepository tileRepository) {
        this.tileRepository = tileRepository;
    }


    /*
     * Get all tiles.
     */
    public List<Tile> getAllTiles() {

        return tileRepository.findAll();
    }


    /*
     * Get a tile using its DATABASE ID.
     *
     * Example:
     * id = 15
     */
    public Tile getTile(Long id) {

        return tileRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Tile Not Found"
                        )
                );
    }


    /*
     * Get a tile using its REMOTE PULSE SLOT NUMBER.
     *
     * Example:
     * slotNumber = 15
     *
     * This will be used by the Android app.
     */
    public Tile getTileBySlotNumber(
            Integer slotNumber
    ) {

        return tileRepository
                .findBySlotNumber(slotNumber)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Tile slot not found: "
                                        + slotNumber
                        )
                );
    }


    /*
     * Save a tile.
     */
    public Tile saveTile(Tile tile) {

        return tileRepository.save(tile);
    }


    /*
     * Update an existing tile.
     */
    public Tile updateTile(
            Long id,
            Tile updatedTile
    ) {

        Tile existingTile =
                tileRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Tile not found"
                                )
                        );

        existingTile.setName(
                updatedTile.getName()
        );

        existingTile.setType(
                updatedTile.getType()
        );

        existingTile.setTarget(
                updatedTile.getTarget()
        );

        existingTile.setIcon(
                updatedTile.getIcon()
        );

        return tileRepository.save(
                existingTile
        );
    }


    /*
     * Clear a tile's configuration.
     *
     * The slot itself remains in the database.
     */
    public Tile clearTile(
            Integer slotNumber
    ) {

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
         * Delete the physical runtime icon file.
         */
        if (tile.getIcon() != null) {

            File iconFile =
                    new File(
                            "data/icons",
                            tile.getIcon()
                    );

            if (iconFile.exists()) {

                boolean deleted =
                        iconFile.delete();

                System.out.println(
                        "Icon deleted: "
                                + deleted
                );
            }
        }


        /*
         * Keep the slot itself.
         *
         * Only remove its configuration.
         */
        tile.setName(null);
        tile.setType(null);
        tile.setTarget(null);
        tile.setIcon(null);


        return tileRepository.save(tile);
    }
}