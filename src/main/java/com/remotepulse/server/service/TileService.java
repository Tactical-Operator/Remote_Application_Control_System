
package com.remotepulse.server.service;

import com.remotepulse.server.entity.Tile;
import com.remotepulse.server.repository.TileRepository;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

/**
 * Manages tile data for the Remote Pulse application.
 *
 * Acts as the service layer between the controller and the
 * repository, handling tile retrieval, creation, updates,
 * and configuration clearing.
 */
@Service
public class TileService {

    private final TileRepository tileRepository;

    /**
     * Injects the repository used to access tile records.
     *
     * @param tileRepository repository for tile database operations
     */
    public TileService(TileRepository tileRepository) {
        this.tileRepository = tileRepository;
    }

    /**
     * Retrieves all tile records from the database.
     *
     * @return list of all tiles
     */
    public List<Tile> getAllTiles() {
        return tileRepository.findAll();
    }

    /**
     * Retrieves a tile using its database ID.
     *
     * @param id the database ID of the tile
     * @return the matching tile
     * @throws RuntimeException if the tile is not found
     */
    public Tile getTile(Long id) {
        return tileRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Tile Not Found")
                );
    }

    /**
     * Retrieves a tile using its Remote Pulse slot number.
     *
     * Slot numbers identify positions in the Remote Pulse
     * interface and are used by the Android client.
     *
     * @param slotNumber the tile's slot number
     * @return the matching tile
     * @throws RuntimeException if the slot is not found
     */
    public Tile getTileBySlotNumber(Integer slotNumber) {
        return tileRepository
                .findBySlotNumber(slotNumber)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Tile slot not found: " + slotNumber
                        )
                );
    }

    /**
     * Saves a tile record to the database.
     *
     * @param tile the tile to save
     * @return the saved tile
     */
    public Tile saveTile(Tile tile) {
        return tileRepository.save(tile);
    }

    /**
     * Updates the configurable fields of an existing tile.
     *
     * The existing database record is retrieved first, and its
     * name, type, target, and icon are replaced with the supplied
     * values before the record is saved.
     *
     * @param id the database ID of the tile to update
     * @param updatedTile the tile containing the updated values
     * @return the saved tile
     * @throws RuntimeException if the tile is not found
     */
    public Tile updateTile(Long id, Tile updatedTile) {

        Tile existingTile = tileRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Tile not found")
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

        return tileRepository.save(existingTile);
    }

    /**
     * Clears a tile's configuration without deleting its slot.
     *
     * If an icon filename is configured and the corresponding
     * file exists in data/icons, the file is deleted. The tile's
     * name, type, target, and icon fields are then cleared.
     *
     * @param slotNumber the slot number to clear
     * @return the saved tile with its configuration cleared
     * @throws RuntimeException if the slot is not found
     */
    public Tile clearTile(Integer slotNumber) {

        Tile tile = tileRepository
                .findBySlotNumber(slotNumber)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Tile slot not found: " + slotNumber
                        )
                );

        // Delete the associated icon file if one is configured.
        if (tile.getIcon() != null) {

            File iconFile = new File(
                    "data/icons",
                    tile.getIcon()
            );

            if (iconFile.exists()) {

                boolean deleted = iconFile.delete();

                System.out.println(
                        "Icon deleted: " + deleted
                );
            }
        }

        // Preserve the database slot while clearing its configuration.
        tile.setName(null);
        tile.setType(null);
        tile.setTarget(null);
        tile.setIcon(null);

        return tileRepository.save(tile);
    }
}
