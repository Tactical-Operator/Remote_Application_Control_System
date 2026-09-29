package com.remotepulse.server.service;

import org.springframework.stereotype.Service;

@Service
public class SystemButtonService {

    public void execute(String action) {

        if ("VOLUME_UP".equals(action)) {

            volumeUp();

            return;
        }
        if ("VOLUME_DOWN".equals(action)) {

            volumeDown();

            return;
        }
        if ("MUTE_TOGGLE".equals(action)) {

            muteToggle();

            return;
        }

        if ("MEDIA_PLAY_PAUSE".equals(action)) {

            mediaPlayPause();

            return;
        }

        throw new IllegalArgumentException(
                "Unsupported system action: " + action);
    }

    private void volumeUp() {

        try {

            new ProcessBuilder(
                    "powershell.exe",
                    "-NoProfile",
                    "-Command",
                    "$wshell = New-Object -ComObject WScript.Shell; " +
                            "$wshell.SendKeys([char]175)")
                    .start()
                    .waitFor();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to increase system volume",
                    e);
        }
    }

    private void volumeDown() {

        try {

            new ProcessBuilder(
                    "powershell.exe",
                    "-NoProfile",
                    "-Command",
                    "$wshell = New-Object -ComObject WScript.Shell; " +
                            "$wshell.SendKeys([char]174)")
                    .start()
                    .waitFor();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to decrease system volume",
                    e);
        }
    }

    private void muteToggle() {

        try {

            new ProcessBuilder(
                    "powershell.exe",
                    "-NoProfile",
                    "-Command",
                    "$wshell = New-Object -ComObject WScript.Shell; " +
                            "$wshell.SendKeys([char]173)")
                    .start()
                    .waitFor();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to toggle system mute",
                    e);
        }
    }

    private void mediaPlayPause() {

        try {

            new ProcessBuilder(
                    "powershell.exe",
                    "-NoProfile",
                    "-Command",
                    "$wshell = New-Object -ComObject WScript.Shell; " +
                            "$wshell.SendKeys([char]179)")
                    .start()
                    .waitFor();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to toggle media play/pause",
                    e);
        }
    }
}