package com.remotepulse.server.controller;

import com.remotepulse.server.entity.Tile;
import com.remotepulse.server.service.SystemButtonSaveService;
import com.remotepulse.server.service.TileUpdateService;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/system-button")
public class SystemButtonController {

    private final SystemButtonSaveService systemButtonSaveService;
    private final TileUpdateService tileUpdateService;

    public SystemButtonController(
            SystemButtonSaveService systemButtonSaveService, TileUpdateService tileUpdateService) {
        this.systemButtonSaveService = systemButtonSaveService;
        this.tileUpdateService = tileUpdateService;
    }

    @PostMapping("/save")
    public Tile saveSystemButton(
            @RequestBody Map<String, Object> request) {

        Integer slotNumber = Integer.valueOf(
                request.get("slotNumber").toString());

        String action = request.get("action").toString();

        Tile savedTile = systemButtonSaveService.saveSystemButton(
                slotNumber,
                action);

        /*
         * Tell Android that the
         * tile configuration changed.
         */
        tileUpdateService.tilesChanged();

        return savedTile;
    }
}