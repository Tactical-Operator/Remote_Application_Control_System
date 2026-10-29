
package com.remotepulse.server.service;

import com.remotepulse.server.model.InstalledApp;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Discovers installed Windows applications for Remote Pulse.
 *
 * Uses PowerShell to retrieve application names and IDs,
 * then returns them as a list of InstalledApp objects.
 */
@Service
public class InstalledAppService {

    /**
     * Retrieves installed applications from the Windows Start menu.
     *
     * @return a list of discovered applications
     */
    public List<InstalledApp> getInstalledApps() {

        // Store the applications discovered by PowerShell.
        List<InstalledApp> apps = new ArrayList<>();

        try {
            // Execute PowerShell to retrieve application names and IDs.
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "Powershell.exe",
                    "-NoProfile",
                    "-Command",
                    "Get-StartApps | ForEach-Object { $_.Name + [char]9 + $_.AppID }"
            );

            Process process = processBuilder.start();

            // Read the command output line by line.
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String line;

            while ((line = reader.readLine()) != null) {

                // Ignore empty output lines.
                if (line.isBlank()) {
                    continue;
                }

                // Split each line into the application name and App ID.
                String[] parts = line.split("\t", 2);

                if (parts.length == 2) {

                    String name = parts[0].trim();
                    String appId = parts[1].trim();

                    // Add the discovered application to the results.
                    apps.add(new InstalledApp(name, appId));
                }
            }

            // Wait for PowerShell to finish execution.
            process.waitFor();

        } catch (Exception e) {

            // Log errors encountered during application discovery.
            e.printStackTrace();
        }

        // Return all applications successfully discovered.
        return apps;
    }
}
