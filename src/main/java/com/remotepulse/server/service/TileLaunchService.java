package com.remotepulse.server.service;

import com.remotepulse.server.entity.Tile;
import org.springframework.stereotype.Service;

@Service
public class TileLaunchService {

    public void launch(Tile tile) {

        try {

            if (tile.getType().equals("EXE")) {

                new ProcessBuilder(tile.getTarget()).start();

            } else if (tile.getType().equals("WEBSITE")) {

                new ProcessBuilder(
                        "cmd", "/c", "start", "", tile.getTarget()
                ).start();

            } else if (tile.getType().equals("STORE_APP")) {

                new ProcessBuilder(
                        "cmd", "/c", "start", "", tile.getTarget()
                ).start();
            }

        } catch (Exception e) {

            throw new RuntimeException("Failed to launch tile", e);

        }
    }
}