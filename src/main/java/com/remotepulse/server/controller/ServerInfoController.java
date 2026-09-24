package com.remotepulse.server.controller;

import com.remotepulse.server.model.ServerInfo;
import com.remotepulse.server.service.ServerNetworkService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServerInfoController {

    @Value("${server.port:8080}")
    private int serverPort;


    private final ServerNetworkService serverNetworkService;


    public ServerInfoController(
            ServerNetworkService serverNetworkService) {

        this.serverNetworkService =
                serverNetworkService;
    }


    @GetMapping("/api/server-info")
    public ServerInfo getServerInfo() {

        String ipAddress =
                serverNetworkService.findLocalIpAddress();


        return new ServerInfo(
                ipAddress,
                serverPort
        );
    }
}