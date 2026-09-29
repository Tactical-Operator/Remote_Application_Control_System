package com.remotepulse.server.controller;

import com.remotepulse.server.entity.Tile;
import com.remotepulse.server.model.WebsiteRequest;
import com.remotepulse.server.service.WebsiteSaveService;
import com.remotepulse.server.service.TileUpdateService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/website")
public class WebsiteController {

    private final WebsiteSaveService websiteSaveService;
    private final TileUpdateService tileUpdateService;

    public WebsiteController(
            WebsiteSaveService websiteSaveService, TileUpdateService tileUpdateService) {

        this.websiteSaveService = websiteSaveService;
        this.tileUpdateService = tileUpdateService;
    }

    @PostMapping("/save")
    public Tile saveWebsite(
            @RequestBody WebsiteRequest request) {

        Tile savedTile = websiteSaveService.saveWebsite(
                request.getSlotNumber(),
                request.getName(),
                request.getUrl());

        /*
         * Tell Android that the
         * tile configuration changed.
         */
        tileUpdateService.tilesChanged();

        return savedTile;
    }
}