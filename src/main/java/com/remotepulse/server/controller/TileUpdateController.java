
package com.remotepulse.server.controller;

import com.remotepulse.server.service.TileUpdateService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Handles the SSE endpoint used by the Remote Pulse Android client
 * to subscribe to tile configuration updates.
 */
@RestController
@RequestMapping("/api/tiles")
public class TileUpdateController {

    private final TileUpdateService tileUpdateService;

    /**
     * Injects the service responsible for managing SSE connections.
     *
     * @param tileUpdateService manages subscriptions and tile-change events
     */
    public TileUpdateController(
            TileUpdateService tileUpdateService) {
        this.tileUpdateService = tileUpdateService;
    }

    /**
     * Establishes an SSE subscription for the Android client.
     *
     * GET /api/tiles/events
     *
     * The returned emitter keeps the HTTP connection open so the
     * server can send events when tile configurations change.
     *
     * @return the SSE emitter for the client connection
     */
    @GetMapping("/events")
    public SseEmitter subscribeToTileUpdates() {
        return tileUpdateService.subscribe();
    }
}
