
package com.remotepulse.server.controller;

import com.remotepulse.server.entity.Tile;
import com.remotepulse.server.model.WebsiteRequest;
import com.remotepulse.server.service.TileUpdateService;
import com.remotepulse.server.service.WebsiteSaveService;

import org.springframework.web.bind.annotation.*;

/**
 * REST controller for configuring website tiles.
 *
 * Receives website configuration requests, delegates saving to
 * WebsiteSaveService, and notifies connected Android clients
 * when a tile configuration changes.
 */
@RestController
@RequestMapping("/api/website")
public class WebsiteController {

    private final WebsiteSaveService websiteSaveService;
    private final TileUpdateService tileUpdateService;

    /**
     * Creates the controller with its required services.
     *
     * @param websiteSaveService service for saving website tile configurations
     * @param tileUpdateService service for notifying connected clients
     */
    public WebsiteController(
            WebsiteSaveService websiteSaveService,
            TileUpdateService tileUpdateService) {

        this.websiteSaveService = websiteSaveService;
        this.tileUpdateService = tileUpdateService;
    }

    /**
     * Saves the website configuration for the specified tile slot.
     *
     * @param request contains the slot number, website name, and URL
     * @return the saved tile configuration
     */
    @PostMapping("/save")
    public Tile saveWebsite(@RequestBody WebsiteRequest request) {

        // Save the website configuration through the service layer.
        Tile savedTile = websiteSaveService.saveWebsite(
                request.getSlotNumber(),
                request.getName(),
                request.getUrl());

        // Notify connected Android clients that the tile configuration changed.
        tileUpdateService.tilesChanged();

        return savedTile;
    }
}
