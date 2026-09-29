
package com.remotepulse.server.controller;

import com.remotepulse.server.entity.Tile;
import com.remotepulse.server.model.CustomIconRequest;
import com.remotepulse.server.service.CustomIconService;
import com.remotepulse.server.service.TileUpdateService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/custom-icon")
public class CustomIconController {

        private final CustomIconService customIconService;
        private final TileUpdateService tileUpdateService;

        public CustomIconController(
                        CustomIconService customIconService, TileUpdateService tileUpdateService) {
                this.customIconService = customIconService;
                this.tileUpdateService = tileUpdateService;
        }

        @PostMapping("/save")
        public Tile saveCustomIcon(
                        @RequestBody CustomIconRequest request) {

                Tile savedTile = customIconService.saveCustomIcon(
                                request.getSlotNumber(),
                                request.getImageBase64());

                /*
                 * Tell Android that the
                 * tile icon changed.
                 */
                tileUpdateService.tilesChanged();

                return savedTile;
        }
}
