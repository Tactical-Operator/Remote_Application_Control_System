package com.remotepulse.server.controller;

import com.remotepulse.server.service.TileUpdateService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@RestController
@RequestMapping("/api/tiles")
public class TileUpdateController {

    private final TileUpdateService tileUpdateService;


    public TileUpdateController(
            TileUpdateService tileUpdateService
    ) {
        this.tileUpdateService =
                tileUpdateService;
    }


    /*
     * Android connects here and keeps
     * listening for tile configuration changes.
     *
     * GET /api/tiles/events
     */
    @GetMapping("/events")
    public SseEmitter subscribeToTileUpdates() {

        return tileUpdateService.subscribe();
    }
}