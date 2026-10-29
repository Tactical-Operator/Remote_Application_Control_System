
package com.remotepulse.server.service;

import org.springframework.stereotype.Service;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * Executes Windows system-button actions for Remote Pulse.
 *
 * Uses JNA to access the Windows user32 API and simulate
 * key presses for audio controls, media playback, screenshots,
 * and fullscreen toggling.
 */
@Service
public class SystemButtonService {

    /**
     * JNA interface for the Windows user32 library.
     */
    private interface User32 extends Library {

        // Load the native Windows user32 library.
        User32 INSTANCE = Native.load("user32", User32.class);

        // Simulate a keyboard event in Windows.
        void keybd_event(
                byte bVk,
                byte bScan,
                int dwFlags,
                int dwExtraInfo);
    }

    /**
     * Executes the requested system action.
     *
     * @param action the identifier of the system action
     */
    public void execute(String action) {

        // Increase the system volume.
        if ("VOLUME_UP".equals(action)) {
            volumeUp();
            return;
        }

        // Decrease the system volume.
        if ("VOLUME_DOWN".equals(action)) {
            volumeDown();
            return;
        }

        // Toggle system mute.
        if ("MUTE_TOGGLE".equals(action)) {
            muteToggle();
            return;
        }

        // Toggle media playback.
        if ("MEDIA_PLAY_PAUSE".equals(action)) {
            mediaPlayPause();
            return;
        }

        // Simulate the Print Screen key.
        if ("PRINT_SCREEN".equals(action)) {
            printScreen();
            return;
        }

        // Simulate F11 to toggle fullscreen in supported applications.
        if ("FULLSCREEN".equals(action)) {
            fullscreen();
            return;
        }

        // Reject actions that are not supported.
        throw new IllegalArgumentException(
                "Unsupported system action: " + action);
    }

    // Simulates a press and release of the volume-up key.
    private void volumeUp() {

        byte VK_VOLUME_UP = (byte) 0xAF;
        int KEYEVENTF_KEYUP = 0x0002;

        User32.INSTANCE.keybd_event(
                VK_VOLUME_UP,
                (byte) 0,
                0,
                0);

        User32.INSTANCE.keybd_event(
                VK_VOLUME_UP,
                (byte) 0,
                KEYEVENTF_KEYUP,
                0);
    }

    // Simulates a press and release of the volume-down key.
    private void volumeDown() {

        byte VK_VOLUME_DOWN = (byte) 0xAE;
        int KEYEVENTF_KEYUP = 0x0002;

        User32.INSTANCE.keybd_event(
                VK_VOLUME_DOWN,
                (byte) 0,
                0,
                0);

        User32.INSTANCE.keybd_event(
                VK_VOLUME_DOWN,
                (byte) 0,
                KEYEVENTF_KEYUP,
                0);
    }

    // Simulates a press and release of the mute key.
    private void muteToggle() {

        byte VK_VOLUME_MUTE = (byte) 0xAD;
        int KEYEVENTF_KEYUP = 0x0002;

        User32.INSTANCE.keybd_event(
                VK_VOLUME_MUTE,
                (byte) 0,
                0,
                0);

        User32.INSTANCE.keybd_event(
                VK_VOLUME_MUTE,
                (byte) 0,
                KEYEVENTF_KEYUP,
                0);
    }

    // Simulates a press and release of the media play/pause key.
    private void mediaPlayPause() {

        byte VK_MEDIA_PLAY_PAUSE = (byte) 0xB3;
        int KEYEVENTF_KEYUP = 0x0002;

        User32.INSTANCE.keybd_event(
                VK_MEDIA_PLAY_PAUSE,
                (byte) 0,
                0,
                0);

        User32.INSTANCE.keybd_event(
                VK_MEDIA_PLAY_PAUSE,
                (byte) 0,
                KEYEVENTF_KEYUP,
                0);
    }

    // Simulates a press and release of the Print Screen key.
    private void printScreen() {

        byte VK_SNAPSHOT = (byte) 0x2C;
        int KEYEVENTF_KEYUP = 0x0002;

        User32.INSTANCE.keybd_event(
                VK_SNAPSHOT,
                (byte) 0,
                0,
                0);

        User32.INSTANCE.keybd_event(
                VK_SNAPSHOT,
                (byte) 0,
                KEYEVENTF_KEYUP,
                0);
    }

    // Simulates a press and release of F11.
    private void fullscreen() {

        byte VK_F11 = (byte) 0x7A;
        int KEYEVENTF_KEYUP = 0x0002;

        User32.INSTANCE.keybd_event(
                VK_F11,
                (byte) 0,
                0,
                0);

        User32.INSTANCE.keybd_event(
                VK_F11,
                (byte) 0,
                KEYEVENTF_KEYUP,
                0);
    }
}
