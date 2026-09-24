package com.remotepulse.server.controller;

import com.remotepulse.server.entity.Tile;
import com.remotepulse.server.model.WebsiteRequest;
import com.remotepulse.server.service.WebsiteSaveService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/website")
public class WebsiteController {

    private final WebsiteSaveService websiteSaveService;

    public WebsiteController(
            WebsiteSaveService websiteSaveService) {

        this.websiteSaveService =
                websiteSaveService;
    }

    @PostMapping("/save")
    public Tile saveWebsite(
            @RequestBody WebsiteRequest request) {

        return websiteSaveService.saveWebsite(
                request.getSlotNumber(),
                request.getName(),
                request.getUrl()
        );
    }
}