package com.remotepulse.server.service;

import com.remotepulse.server.entity.Tile;
import com.remotepulse.server.repository.TileRepository;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
public class WebsiteSaveService {

        private final TileRepository tileRepository;

        public WebsiteSaveService(
                        TileRepository tileRepository) {
                this.tileRepository = tileRepository;
        }

        public Tile saveWebsite(
                        Integer slotNumber,
                        String name,
                        String url) {

                if (url == null || url.isBlank()) {
                        throw new IllegalArgumentException(
                                        "Website URL cannot be empty.");
                }

                if (!url.startsWith("http://") &&
                                !url.startsWith("https://")) {

                        throw new IllegalArgumentException(
                                        "Website URL must start with http:// or https://");
                }

                Tile tile = tileRepository
                                .findBySlotNumber(slotNumber)
                                .orElseThrow(() -> new RuntimeException(
                                                "Tile slot not found: "
                                                                + slotNumber));

                tile.setName(name);
                tile.setType("WEBSITE");
                tile.setTarget(url);

                try {

                        /*
                         * Use the SAME icon path pattern
                         * as InstalledAppSaveService.
                         */
                        Path iconDirectory = Path.of(
                                        "data",
                                        "icons");

                        java.nio.file.Files.createDirectories(
                                        iconDirectory);
                                        
                        String outputPath = Path.of(
                                        "data",
                                        "icons",
                                        "slot-" +
                                                        slotNumber +
                                                        ".png")
                                        .toAbsolutePath()
                                        .toString();

                        /*
                         * Run the existing website-icon
                         * PowerShell script.
                         */
                        Process process = new ProcessBuilder(
                                        "powershell.exe",
                                        "-NoProfile",
                                        "-ExecutionPolicy",
                                        "Bypass",
                                        "-File",
                                        "extract-web-icon.ps1",
                                        url,
                                        outputPath)
                                        .inheritIO()
                                        .start();

                        /*
                         * IMPORTANT:
                         * Wait until PowerShell has completely
                         * finished creating the icon.
                         */
                        int exitCode = process.waitFor();

                        if (exitCode != 0) {
                                throw new RuntimeException(
                                                "Website icon extraction failed.");
                        }

                        /*
                         * Same naming system used by
                         * InstalledAppSaveService.
                         */
                        tile.setIcon(
                                        "slot-" +
                                                        slotNumber +
                                                        ".png");

                } catch (Exception e) {

                        System.out.println(
                                        "Could not extract website icon for: "
                                                        + name);

                        e.printStackTrace();
                }

                return tileRepository.save(tile);
        }
}