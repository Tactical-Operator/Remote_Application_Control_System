package com.remotepulse.server.controller;

import com.remotepulse.server.entity.Tile;
import com.remotepulse.server.model.InstalledApp;
import com.remotepulse.server.model.InstalledAppRequest;
import com.remotepulse.server.service.InstalledAppSaveService;
import com.remotepulse.server.service.InstalledAppService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class InstalledAppController {

    private final InstalledAppService installedAppService;
    private final InstalledAppSaveService installedAppSaveService;

    public InstalledAppController(
            InstalledAppService installedAppService,
            InstalledAppSaveService installedAppSaveService) {

        this.installedAppService = installedAppService;
        this.installedAppSaveService = installedAppSaveService;
    }

    @GetMapping("/api/installed-apps")
    public List<InstalledApp> getInstalledApps() {

        return installedAppService.getInstalledApps();
    }

    @PostMapping("/api/installed-apps/select")
    public Tile selectInstalledApp(
            @RequestBody InstalledAppRequest request) {

        return installedAppSaveService.saveInstalledApp(
                request.getSlotNumber(),
                request.getName(),
                request.getAppId()
        );
    }
}