package com.remotepulse.server.service;

import com.remotepulse.server.entity.Tile;
import com.remotepulse.server.repository.TileRepository;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SystemButtonSaveService {

    private final TileRepository tileRepository;

    private static final Map<String, String> BUTTON_NAMES =
            Map.of(
                    "MIC_TOGGLE", "Microphone On/Off",
                    "VOLUME_UP", "Volume Up",
                    "VOLUME_DOWN", "Volume Down",
                    "MUTE_TOGGLE", "Mute",
                    "MEDIA_PLAY_PAUSE", "Media Play/Pause",
                    "PRINT_SCREEN", "Print Screen",
                    "FULLSCREEN", "Fullscreen"
            );

    private static final Map<String, String> BUTTON_ICONS =
            Map.of(
                    "MIC_TOGGLE", "system-icons/mic-toggle.png",
                    "VOLUME_UP", "system-icons/volume-up.png",
                    "VOLUME_DOWN", "system-icons/volume-down.png",
                    "MUTE_TOGGLE", "system-icons/mute.png",
                    "MEDIA_PLAY_PAUSE", "system-icons/play-pause.png",
                    "PRINT_SCREEN", "system-icons/printscreen.png",
                    "FULLSCREEN", "system-icons/fullscreen.png"
            );

    public SystemButtonSaveService(
            TileRepository tileRepository
    ) {
        this.tileRepository = tileRepository;
    }

    public Tile saveSystemButton(
            Integer slotNumber,
            String action
    ) {

        if (!BUTTON_NAMES.containsKey(action)) {
            throw new IllegalArgumentException(
                    "Unknown system button: " + action
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

        tile.setName(
                BUTTON_NAMES.get(action)
        );

        tile.setType(
                "SYSTEM_BUTTON"
        );

        tile.setTarget(
                action
        );

        tile.setIcon(
                BUTTON_ICONS.get(action)
        );

        return tileRepository.save(tile);
    }
}