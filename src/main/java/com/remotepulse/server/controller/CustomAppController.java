package com.remotepulse.server.controller;

import com.remotepulse.server.entity.Tile;
import com.remotepulse.server.model.CustomAppRequest;
import com.remotepulse.server.service.CustomAppSaveService;
import com.remotepulse.server.service.TileUpdateService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/custom-app")
public class CustomAppController {

    private final CustomAppSaveService customAppSaveService;
    private final TileUpdateService tileUpdateService;

    public CustomAppController(
            CustomAppSaveService customAppSaveService, TileUpdateService tileUpdateService) {

        this.customAppSaveService = customAppSaveService;

        this.tileUpdateService = tileUpdateService;
    }

    @PostMapping("/save")
    public Tile saveCustomApp(
            @RequestBody CustomAppRequest request) {

        Tile savedTile = customAppSaveService.saveCustomApp(
                request.getSlotNumber(),
                request.getName(),
                request.getPath());

        /*
         * Tell Android that the
         * tile configuration changed.
         */
        tileUpdateService.tilesChanged();

        return savedTile;
    }
}