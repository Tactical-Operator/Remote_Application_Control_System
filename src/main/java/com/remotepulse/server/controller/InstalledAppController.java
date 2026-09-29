package com.remotepulse.server.controller;

import com.remotepulse.server.entity.Tile;
import com.remotepulse.server.model.InstalledApp;
import com.remotepulse.server.model.InstalledAppRequest;
import com.remotepulse.server.service.InstalledAppSaveService;
import com.remotepulse.server.service.InstalledAppService;
import com.remotepulse.server.service.TileUpdateService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class InstalledAppController {

    private final InstalledAppService installedAppService;
    private final InstalledAppSaveService installedAppSaveService;
    private final TileUpdateService tileUpdateService;

    public InstalledAppController(
            InstalledAppService installedAppService,
            InstalledAppSaveService installedAppSaveService, TileUpdateService tileUpdateService) {

        this.installedAppService = installedAppService;
        this.installedAppSaveService = installedAppSaveService;
        this.tileUpdateService = tileUpdateService;

    }

    @GetMapping("/api/installed-apps")
    public List<InstalledApp> getInstalledApps() {

        return installedAppService.getInstalledApps();
    }

    @PostMapping("/api/installed-apps/select")
    public Tile selectInstalledApp(
            @RequestBody InstalledAppRequest request) {

        Tile savedTile = installedAppSaveService.saveInstalledApp(
                request.getSlotNumber(),
                request.getName(),
                request.getAppId());

        /*
         * Tell Android that a tile
         * configuration has changed.
         */
        tileUpdateService.tilesChanged();

        return savedTile;
    }
}