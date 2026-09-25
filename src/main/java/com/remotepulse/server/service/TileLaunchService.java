package com.remotepulse.server.service;

import com.remotepulse.server.entity.Tile;
import org.springframework.stereotype.Service;

@Service
public class TileLaunchService {

    public void launch(Tile tile) {

        try {

            if (tile.getType().equals("CUSTOM_APP")) {

                new ProcessBuilder(
                        "wt.exe",
                        "new-tab",
                        tile.getTarget()).start();

            } else if (tile.getType().equals("WEBSITE")) {

                new ProcessBuilder(
                        "cmd",
                        "/c",
                        "start",
                        "",
                        tile.getTarget()).start();

            } else if (tile.getType().equals("INSTALLED_APP")) {

                System.out.println("=== NEW INSTALLED APP CODE ===");

                new ProcessBuilder(
                        "explorer.exe",
                        "shell:AppsFolder\\" + tile.getTarget()).start();

            } else {

                throw new RuntimeException(
                        "Unknown tile type: " + tile.getType());
            }

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to launch tile",
                    e);
        }
    }
}