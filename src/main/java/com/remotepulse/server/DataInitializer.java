package com.remotepulse.server;

import com.remotepulse.server.entity.Tile;
import com.remotepulse.server.repository.TileRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final TileRepository tileRepository;

    public DataInitializer(TileRepository tileRepository) {
        this.tileRepository = tileRepository;
    }

    @Override
    public void run(String... args) {

        for (int i = 1; i <= 8; i++) {

            if (tileRepository.findBySlotNumber(i).isEmpty()) {

                Tile tile = new Tile();

                tile.setSlotNumber(i);

                tileRepository.save(tile);
            }
        }

        System.out.println("RemotePulse: 8 tile slots ready.");
    }
}