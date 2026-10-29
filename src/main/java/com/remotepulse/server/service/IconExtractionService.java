
package com.remotepulse.server.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * Extracts icons from installed Windows applications.
 *
 * Executes the extract-icon.ps1 PowerShell script,
 * captures its output, and verifies that the PNG icon was created.
 */
@Service
public class IconExtractionService {

    /**
     * Extracts an installed application's icon to the specified path.
     *
     * @param appId      the identifier of the installed application
     * @param outputPath the destination path for the extracted PNG icon
     */
    public void extractInstalledAppIcon(
            String appId,
            String outputPath
    ) {

        try {
            // Represent the destination icon file.
            File outputFile = new File(outputPath);

            // Create the parent directory if it does not exist.
            File parentDirectory = outputFile.getParentFile();

            if (parentDirectory != null &&
                    !parentDirectory.exists()) {

                parentDirectory.mkdirs();
            }

            // Log the application identifier for troubleshooting.
            System.out.println(
                    "RemotePulse icon extraction AppID: [" +
                    appId +
                    "]"
            );

            // Path to the existing PowerShell icon extraction script.
            String scriptPath = "extract-icon.ps1";

            // Execute the script with the application ID and output path.
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

            // Read the combined standard output and error output.
            String output =
                    new String(
                            process.getInputStream().readAllBytes(),
                            StandardCharsets.UTF_8
                    );

            // Wait for PowerShell to finish and retrieve its exit code.
            int exitCode = process.waitFor();

            // Report an error if the extraction script failed.
            if (exitCode != 0) {

                throw new RuntimeException(
                        "PowerShell icon extraction failed:\n" +
                        output
                );
            }

            // Verify that the expected PNG file was actually created.
            if (!outputFile.exists()) {

                throw new RuntimeException(
                        "PowerShell completed successfully, " +
                        "but PNG was not created:\n" +
                        output
                );
            }

            // Log the script output and the final icon location.
            System.out.println(output);

            System.out.println(
                    "Icon created: " +
                    outputFile.getAbsolutePath()
            );

        } catch (Exception e) {

            // Propagate extraction failures with additional context.
            throw new RuntimeException(
                    "Failed to extract installed app icon",
                    e
            );
        }
    }
}
