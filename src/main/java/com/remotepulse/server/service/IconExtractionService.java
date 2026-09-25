
package com.remotepulse.server.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.charset.StandardCharsets;

@Service
public class IconExtractionService {

    public void extractInstalledAppIcon(
            String appId,
            String outputPath
    ) {

        try {

            File outputFile = new File(outputPath);

            File parentDirectory = outputFile.getParentFile();

            if (parentDirectory != null &&
                    !parentDirectory.exists()) {

                parentDirectory.mkdirs();
            }

            System.out.println(
                    "RemotePulse icon extraction AppID: [" +
                    appId +
                    "]"
            );

            /*
             * Location of our tested PowerShell script.
             */
            String scriptPath =
                    "extract-icon.ps1";

            Process process =
                    new ProcessBuilder(
                            "powershell.exe",
                            "-NoProfile",
                            "-ExecutionPolicy",
                            "Bypass",
                            "-File",
                            scriptPath,
                            appId,
                            outputPath
                    )
                    .redirectErrorStream(true)
                    .start();

            String output =
                    new String(
                            process.getInputStream().readAllBytes(),
                            StandardCharsets.UTF_8
                    );

            int exitCode =
                    process.waitFor();

            if (exitCode != 0) {

                throw new RuntimeException(
                        "PowerShell icon extraction failed:\n" +
                        output
                );
            }

            if (!outputFile.exists()) {

                throw new RuntimeException(
                        "PowerShell completed successfully, " +
                        "but PNG was not created:\n" +
                        output
                );
            }

            System.out.println(output);

            System.out.println(
                    "Icon created: " +
                    outputFile.getAbsolutePath()
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to extract installed app icon",
                    e
            );
        }
    }
}

