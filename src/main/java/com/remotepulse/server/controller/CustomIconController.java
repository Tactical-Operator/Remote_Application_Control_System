
package com.remotepulse.server.controller;

import com.remotepulse.server.entity.Tile;
import com.remotepulse.server.model.CustomIconRequest;
import com.remotepulse.server.service.CustomIconService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/custom-icon")
public class CustomIconController {

    private final CustomIconService customIconService;

    public CustomIconController(
            CustomIconService customIconService
    ) {
        this.customIconService =
                customIconService;
    }

    @PostMapping("/save")
    public Tile saveCustomIcon(
            @RequestBody CustomIconRequest request
    ) {

        return customIconService.saveCustomIcon(
                request.getSlotNumber(),
                request.getImageBase64()
        );
    }
}
