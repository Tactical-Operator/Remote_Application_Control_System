package com.remotepulse.server.controller;

import com.remotepulse.server.model.InstalledApp;
import com.remotepulse.server.service.InstalledAppService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController 
public class InstalledAppContoller {

    private final InstalledAppService installedAppService;

    public InstalledAppContoller(InstalledAppService installedAppService){

        this.installedAppService = installedAppService;
    }

    @GetMapping("/api/installed-apps")
    public List<InstalledApp> getInstalledApps(){
        return installedAppService.getInstalledApps();
    }
    
}
