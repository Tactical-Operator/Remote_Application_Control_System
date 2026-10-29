
package com.remotepulse.server.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.remotepulse.server.entity.Tile;
import com.remotepulse.server.service.TileLaunchService;
import com.remotepulse.server.service.TileService;
import com.remotepulse.server.service.TileUpdateService;

/**
 * Handles REST API requests for Remote Pulse tile operations.
 *
 * Supports retrieving, updating, launching, and clearing tiles.
 * Delegates business logic to the appropriate service classes
 * and notifies connected Android clients when configurations change.
 */
@RestController
@RequestMapping("/api/tiles")
public class TilesController {

    private final TileService tileService;
    private final TileLaunchService tileLaunchService;
    private final TileUpdateService tileUpdateService;

    /**
     * Injects the services required to manage and launch tiles.
     *
     * @param tileService handles tile data operations
     * @param tileLaunchService executes tile actions
     * @param tileUpdateService sends tile-change notifications
     */
    public TilesController(
            TileService tileService,
            TileLaunchService tileLaunchService,
            TileUpdateService tileUpdateService) {

        this.tileService = tileService;
        this.tileLaunchService = tileLaunchService;
        this.tileUpdateService = tileUpdateService;
    }

    /**
     * Retrieves all tile records.
     *
     * GET /api/tiles
     *
     * @return list of tiles
     */
    @GetMapping
    public List<Tile> getAllTiles() {
        return tileService.getAllTiles();
    }

    /**
     * Retrieves a tile using its database ID.
     *
     * GET /api/tiles/{id}
     *
     * @param id the database ID of the tile
     * @return the requested tile
     */
    @GetMapping("/{id}")
    public Tile getTile(@PathVariable Long id) {
        return tileService.getTile(id);
    }

    /**
     * Updates a tile's configuration and notifies connected
     * Android clients about the change.
     *
     * PUT /api/tiles/{id}
     *
     * @param id the database ID of the tile to update
     * @param tile the updated tile data from the request body
     * @return the updated tile
     */
    @PutMapping("/{id}")
    public Tile updateTile(
            @PathVariable Long id,
            @RequestBody Tile tile) {

        Tile updatedTile = tileService.updateTile(id, tile);

        // Notify the connected Android client that tiles have changed.
        tileUpdateService.tilesChanged();

        return updatedTile;
    }

    /**
     * Launches a tile using its database ID.
     *
     * POST /api/tiles/{id}/launch
     *
     * @param id the database ID of the tile to launch
     * @return a success message containing the tile name
     */
    @PostMapping("/{id}/launch")
    public String launchTile(@PathVariable Long id) {

        Tile tile = tileService.getTile(id);

        // Delegate the actual launch operation to the launch service.
        tileLaunchService.launch(tile);

        return "Tile '" + tile.getName() + "' Launched Successfully";
    }

    /**
     * Launches a tile using its Remote Pulse slot number.
     *
     * POST /api/tiles/slot/{slotNumber}/launch
     *
     * This endpoint is used by the Android app to launch
     * the tile associated with a particular slot.
     *
     * @param slotNumber the Remote Pulse slot number
     * @return a success message containing the slot and tile name
     */
    @PostMapping("/slot/{slotNumber}/launch")
    public String launchTileBySlot(
            @PathVariable Integer slotNumber) {

        Tile tile = tileService.getTileBySlotNumber(slotNumber);

        // Execute the action configured for this tile.
        tileLaunchService.launch(tile);

        return "Slot "
                + slotNumber
                + " ('"
                + tile.getName()
                + "') launched successfully";
    }

    /**
     * Clears a tile's configuration while preserving its slot.
     *
     * DELETE /api/tiles/slot/{slotNumber}/configuration
     *
     * @param slotNumber the slot whose configuration should be cleared
     * @return the tile with its configuration cleared
     */
    @DeleteMapping("/slot/{slotNumber}/configuration")
    public Tile clearTile(@PathVariable Integer slotNumber) {

        Tile clearedTile = tileService.clearTile(slotNumber);

        // Notify the connected Android client about the change.
        tileUpdateService.tilesChanged();

        return clearedTile;
    }
}
