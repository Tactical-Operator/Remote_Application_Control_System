package com.remotepulse.server.controller;

import org.springframework.web.bind.annotation.*;

import com.remotepulse.server.entity.Tile;
import com.remotepulse.server.service.TileService;
import com.remotepulse.server.service.TileLaunchService;

import java.util.List;

@RestController
@RequestMapping("/api/tiles")
public class TilesController {

    private final TileService tileService;
    private final TileLaunchService tileLaunchService;

    public TilesController(TileService tileService, TileLaunchService tileLaunchService) {
        this.tileService = tileService;
        this.tileLaunchService = tileLaunchService;
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

        return tileService.updateTile(id, tile);
    }

    @PostMapping("/{id}/launch")
    public String launchTile(@PathVariable Long id) {

        Tile tile = tileService.getTile(id);

        tileLaunchService.launch(tile);

        return "Tile '" + tile.getName() + "' Launched Successfully";
    }

}
