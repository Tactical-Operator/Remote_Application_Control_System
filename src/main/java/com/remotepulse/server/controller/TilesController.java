
package com.remotepulse.server.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.remotepulse.server.entity.Tile;
import com.remotepulse.server.service.TileLaunchService;
import com.remotepulse.server.service.TileService;
import com.remotepulse.server.service.TileUpdateService;

/**
 * Responsibility:
 * Handles REST API requests for retrieving, updating, launching,
 * and clearing Remote Pulse tiles.
 *
 * Delegates tile operations to the appropriate service classes
 * and notifies connected Android devices when configurations change.
 */
@RestController
@RequestMapping("/api/tiles")
public class TilesController {

    private final TileService tileService;
    private final TileLaunchService tileLaunchService;
    private final TileUpdateService tileUpdateService;

    public TilesController(
            TileService tileService,
            TileLaunchService tileLaunchService,
            TileUpdateService tileUpdateService) {
        this.tileService = tileService;
        this.tileLaunchService = tileLaunchService;
        this.tileUpdateService = tileUpdateService;
    }

    /**
     * Retrieves all configured tiles.
     *
     * GET /api/tiles
     */
    @GetMapping
    public List<Tile> getAllTiles() {
        return tileService.getAllTiles();
    }

    /**
     * Retrieves a tile using its database ID.
     *
     * GET /api/tiles/{id}
     */
    @GetMapping("/{id}")
    public Tile getTile(@PathVariable Long id) {
        return tileService.getTile(id);
    }

    /**
     * Updates a tile and notifies connected Android devices
     * that the configuration has changed.
     *
     * PUT /api/tiles/{id}
     */
    @PutMapping("/{id}")
    public Tile updateTile(
            @PathVariable Long id,
            @RequestBody Tile tile) {

        Tile updatedTile = tileService.updateTile(id, tile);

        // Notify connected Android devices about the change.
        tileUpdateService.tilesChanged();

        return updatedTile;
    }

    /**
     * Launches a tile using its database ID.
     *
     * POST /api/tiles/{id}/launch
     */
    @PostMapping("/{id}/launch")
    public String launchTile(@PathVariable Long id) {
        Tile tile = tileService.getTile(id);

        tileLaunchService.launch(tile);

        return "Tile '" + tile.getName() + "' Launched Successfully";
    }

    /**
     * Launches a tile using its Remote Pulse slot number.
     * This endpoint is used by the Android app.
     *
     * Example: POST /api/tiles/slot/15/launch
     */
    @PostMapping("/slot/{slotNumber}/launch")
    public String launchTileBySlot(
            @PathVariable Integer slotNumber) {

        Tile tile = tileService.getTileBySlotNumber(slotNumber);

        tileLaunchService.launch(tile);

        return "Slot "
                + slotNumber
                + " ('"
                + tile.getName()
                + "') launched successfully";
    }

    /**
     * Clears a tile's configuration while preserving its slot,
     * then notifies connected Android devices about the change.
     *
     * DELETE /api/tiles/slot/{slotNumber}/configuration
     */
    @DeleteMapping("/slot/{slotNumber}/configuration")
    public Tile clearTile(@PathVariable Integer slotNumber) {
        Tile clearedTile = tileService.clearTile(slotNumber);

        // Notify connected Android devices about the change.
        tileUpdateService.tilesChanged();

        return clearedTile;
    }
}
