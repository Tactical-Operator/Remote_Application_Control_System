
package com.remotepulse.server.service;

import com.remotepulse.server.entity.Tile;
import com.remotepulse.server.repository.TileRepository;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class WebsiteSaveService {

    private final TileRepository tileRepository;

    public WebsiteSaveService(TileRepository tileRepository) {
        this.tileRepository = tileRepository;
    }

    public Tile saveWebsite(
            Integer slotNumber,
            String name,
            String url
    ) {

        if (url == null || url.isBlank()) {

            throw new IllegalArgumentException(
                    "Website URL cannot be empty."
            );
        }

        if (!url.startsWith("http://") &&
                !url.startsWith("https://")) {

            throw new IllegalArgumentException(
                    "Website URL must start with http:// or https://"
            );
        }

        Tile tile =
                tileRepository
                        .findBySlotNumber(slotNumber)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Tile slot not found: "
                                                + slotNumber
                                )
                        );

        tile.setName(name);

        tile.setType("WEBSITE");

        tile.setTarget(url);


        String outputPath =
                new File(
                        "src/main/resources/static/icons",
                        "slot-" + slotNumber + ".png"
                )
                .getAbsolutePath();


        try {

            Process process =
                    new ProcessBuilder(
                            "powershell.exe",
                            "-NoProfile",
                            "-ExecutionPolicy",
                            "Bypass",
                            "-File",
                            "extract-web-icon.ps1",
                            url,
                            outputPath
                    )
                    .redirectErrorStream(true)
                    .start();


            String output =
                    new String(
                            process.getInputStream()
                                    .readAllBytes()
                    );


            int exitCode =
                    process.waitFor();


            if (exitCode != 0) {

                throw new RuntimeException(
                        "Website icon extraction failed:\n"
                                + output
                );
            }


            File iconFile =
                    new File(outputPath);


            if (!iconFile.exists()) {

                throw new RuntimeException(
                        "Icon extraction completed, "
                                + "but icon file was not created."
                );
            }


            tile.setIcon(
                    "slot-" + slotNumber + ".png"
            );


            System.out.println(
                    "Website icon created: "
                            + iconFile.getAbsolutePath()
            );


        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to extract website icon",
                    e
            );
        }


        return tileRepository.save(tile);
    }
}

