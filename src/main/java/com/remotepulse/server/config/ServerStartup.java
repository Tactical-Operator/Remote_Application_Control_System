package com.remotepulse.server.config;

import com.remotepulse.server.service.ServerNetworkService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ServerStartup {

    @Value("${server.port:8080}")
    private int serverPort;

    private final ServerNetworkService serverNetworkService;

    public ServerStartup(
            ServerNetworkService serverNetworkService) {

        this.serverNetworkService = serverNetworkService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {

        String ipAddress = serverNetworkService.findLocalIpAddress();

        String url = "http://" +
                ipAddress +
                ":" +
                serverPort +
                "/index.html";

        System.out.println();

        System.out.println("========================================");
        System.out.println("          REMOTE PULSE SERVER");
        System.out.println("========================================");

        System.out.println();

        System.out.println(
                "The configuration page is available at:");

        System.out.println();

        System.out.println(url);

        System.out.println();

        System.out.println(
                "Opening configuration page in browser...");

        System.out.println();

        System.out.println("========================================");

        openBrowser(url);
    }

    private void openBrowser(String url) {

    try {

        new ProcessBuilder(
                "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe",
                "--new-window",
                "--window-size=516,914",
                "--window-position=100,50",
                url
        ).start();

    } catch (Exception e) {

        System.out.println(
                "Could not open Chrome automatically."
        );

        e.printStackTrace();
    }
}
}