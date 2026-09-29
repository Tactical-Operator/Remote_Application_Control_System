package com.remotepulse.server.controller;

import org.springframework.web.bind.annotation.*;

import com.remotepulse.server.entity.Tile;
import com.remotepulse.server.service.TileService;
import com.remotepulse.server.service.TileLaunchService;

import com.remotepulse.server.service.TileUpdateService;

import java.util.List;

@RestController
@RequestMapping("/api/tiles")
public class TilesController {

    private final TileService tileService;
    private final TileLaunchService tileLaunchService;
    private final TileUpdateService tileUpdateService;

    public TilesController(TileService tileService, TileLaunchService tileLaunchService,
            TileUpdateService tileUpdateService) {
        this.tileService = tileService;
        this.tileLaunchService = tileLaunchService;
        this.tileUpdateService = tileUpdateService;
    }

    // get all tiles
    @GetMapping
    public List<Tile> getAllTiles() {
        return tileService.getAllTiles();
    }

    // get one tile
    @GetMapping("/{id}")
    public Tile getTile(@PathVariable Long id) {
        return tileService.getTile(id);
    }

    @PutMapping("/{id}")
    public Tile updateTile(
            @PathVariable Long id,
            @RequestBody Tile tile) {

        Tile updatedTile = tileService.updateTile(
                id,
                tile);

        /*
         * Tell connected Android devices
         * that tile configuration changed.
         */
        tileUpdateService.tilesChanged();

        return updatedTile;
    }

    @PostMapping("/{id}/launch")
    public String launchTile(@PathVariable Long id) {

        Tile tile = tileService.getTile(id);

        tileLaunchService.launch(tile);

        return "Tile '" + tile.getName() + "' Launched Successfully";
    }

    /*
     * Launch a tile using its Remote Pulse slot number.
     *
     * This endpoint will be used by the Android app.
     *
     * Example:
     * POST /api/tiles/slot/15/launch
     */
    @PostMapping("/slot/{slotNumber}/launch")
    public String launchTileBySlot(
            @PathVariable Integer slotNumber) {

        Tile tile = tileService.getTileBySlotNumber(
                slotNumber);

        tileLaunchService.launch(tile);

        return "Slot "
                + slotNumber
                + " ('"
                + tile.getName()
                + "') launched successfully";
    }

    @DeleteMapping("/slot/{slotNumber}/configuration")
    public Tile clearTile(
            @PathVariable Integer slotNumber) {

        Tile clearedTile = tileService.clearTile(
                slotNumber);

        /*
         * Tell connected Android devices
         * that tile configuration changed.
         */
        tileUpdateService.tilesChanged();

        return clearedTile;
    }

}
