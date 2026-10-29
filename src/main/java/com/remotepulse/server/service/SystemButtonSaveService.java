
package com.remotepulse.server.service;

import com.remotepulse.server.entity.Tile;
import com.remotepulse.server.repository.TileRepository;

import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Handles saving system-button configurations for Remote Pulse tiles.
 *
 * Maps supported actions to their display names and built-in icons,
 * then saves the selected configuration to the database.
 */
@Service
public class SystemButtonSaveService {

    private final TileRepository tileRepository;

    // Maps each supported action to its display name.
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

    // Maps each supported action to its built-in icon path.
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

    /**
     * Configures a tile for the selected system action.
     *
     * @param slotNumber the Remote Pulse slot number
     * @param action     the identifier of the selected system action
     * @return the saved tile configuration
     */
    public Tile saveSystemButton(
            Integer slotNumber,
            String action
    ) {

        // Reject actions that are not supported by Remote Pulse.
        if (!BUTTON_NAMES.containsKey(action)) {
            throw new IllegalArgumentException(
                    "Unknown system button: " + action
            );
        }

        // Find the existing tile using its slot number.
        Tile tile =
                tileRepository
                        .findBySlotNumber(slotNumber)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Tile slot not found: "
                                                + slotNumber
                                )
                        );

        // Set the display name for the selected action.
        tile.setName(
                BUTTON_NAMES.get(action)
        );

        // Identify the tile as a system button.
        tile.setType(
                "SYSTEM_BUTTON"
        );

        // Store the action identifier for execution by the backend.
        tile.setTarget(
                action
        );

        // Assign the corresponding built-in icon.
        tile.setIcon(
                BUTTON_ICONS.get(action)
        );

        // Save the updated tile configuration to the database.
        return tileRepository.save(tile);
    }
}
