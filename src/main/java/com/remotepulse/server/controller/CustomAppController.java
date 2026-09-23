package com.remotepulse.server.controller;

import com.remotepulse.server.entity.Tile;
import com.remotepulse.server.model.CustomAppRequest;
import com.remotepulse.server.service.CustomAppSaveService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/custom-app")
public class CustomAppController {

    private final CustomAppSaveService customAppSaveService;

    public CustomAppController(
            CustomAppSaveService customAppSaveService) {

        this.customAppSaveService =
                customAppSaveService;
    }

    @PostMapping("/save")
    public Tile saveCustomApp(
            @RequestBody CustomAppRequest request) {

        return customAppSaveService.saveCustomApp(
                request.getSlotNumber(),
                request.getName(),
                request.getPath()
        );
    }
}