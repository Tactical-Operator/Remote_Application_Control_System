package com.remotepulse.server.service;

import com.remotepulse.server.entity.Tile;
import com.remotepulse.server.repository.TileRepository;
import org.springframework.stereotype.Service;

@Service
public class InstalledAppSaveService {

    private final TileRepository tileRepository;

    public InstalledAppSaveService(TileRepository tileRepository) {
        this.tileRepository = tileRepository;
    }

    public Tile saveInstalledApp(
            Integer slotNumber,
            String name,
            String appId
    ) {

        Tile tile = tileRepository
                .findBySlotNumber(slotNumber)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Tile slot not found: " + slotNumber
                        )
                );

        tile.setName(name);
        tile.setType("INSTALLED_APP");
        tile.setTarget(appId);

        return tileRepository.save(tile);
    }
}