package com.remotepulse.server.service;

import org.springframework.stereotype.Service;

import com.sun.jna.Library;
import com.sun.jna.Native;

@Service
public class SystemButtonService {

    private interface User32 extends Library {

    User32 INSTANCE = Native.load("user32", User32.class);

    void keybd_event(byte bVk, byte bScan, int dwFlags, int dwExtraInfo);
}


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

    byte VK_VOLUME_UP = (byte) 0xAF;
    int KEYEVENTF_KEYUP = 0x0002;

    User32.INSTANCE.keybd_event(
            VK_VOLUME_UP,
            (byte) 0,
            0,
            0
    );

    User32.INSTANCE.keybd_event(
            VK_VOLUME_UP,
            (byte) 0,
            KEYEVENTF_KEYUP,
            0
    );
}

    private void volumeDown() {

    byte VK_VOLUME_DOWN = (byte) 0xAE;
    int KEYEVENTF_KEYUP = 0x0002;

    User32.INSTANCE.keybd_event(
            VK_VOLUME_DOWN,
            (byte) 0,
            0,
            0
    );

    User32.INSTANCE.keybd_event(
            VK_VOLUME_DOWN,
            (byte) 0,
            KEYEVENTF_KEYUP,
            0
    );
}

private void muteToggle() {

    byte VK_VOLUME_MUTE = (byte) 0xAD;
    int KEYEVENTF_KEYUP = 0x0002;

    User32.INSTANCE.keybd_event(
            VK_VOLUME_MUTE,
            (byte) 0,
            0,
            0
    );

    User32.INSTANCE.keybd_event(
            VK_VOLUME_MUTE,
            (byte) 0,
            KEYEVENTF_KEYUP,
            0
    );
}

private void mediaPlayPause() {

    byte VK_MEDIA_PLAY_PAUSE = (byte) 0xB3;
    int KEYEVENTF_KEYUP = 0x0002;

    User32.INSTANCE.keybd_event(
            VK_MEDIA_PLAY_PAUSE,
            (byte) 0,
            0,
            0
    );

    User32.INSTANCE.keybd_event(
            VK_MEDIA_PLAY_PAUSE,
            (byte) 0,
            KEYEVENTF_KEYUP,
            0
    );
}
}