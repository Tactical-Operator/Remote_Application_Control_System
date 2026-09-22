package com.remotepulse.server.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Service
public class AppDiscoveryService {

    public List<Map<String, String>> discoverApps() {

        List<Map<String, String>> apps = new ArrayList<>();

        List<Path> startMenuFolders = List.of(
                Paths.get(System.getenv("ProgramData"),
                        "Microsoft", "Windows", "Start Menu", "Programs"),

                Paths.get(System.getenv("APPDATA"),
                        "Microsoft", "Windows", "Start Menu", "Programs"));

        for (Path folder : startMenuFolders) {

            if (!Files.exists(folder)) {
                continue;
            }

            try {
                Files.walk(folder)
                        .filter(path -> path.toString().toLowerCase().endsWith(".lnk"))
                        .forEach(path -> {

                            Map<String, String> app = new HashMap<>();

                            String fileName = path.getFileName()
                                    .toString()
                                    .replaceFirst("(?i)\\.lnk$", "");

                            app.put("name", fileName);
                            app.put("shortcut", path.toString());

                            apps.add(app);
                        });

            } catch (IOException e) {
                throw new RuntimeException(
                        "Failed to discover installed applications", e);
            }
        }

        return apps;
    }
}