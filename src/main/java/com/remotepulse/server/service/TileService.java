package com.remotepulse.server.service;

import com.remotepulse.server.entity.Tile;
import com.remotepulse.server.repository.TileRepository;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;


// The service sits between the controller and repository.

// PC interface / Android
//           ↓
//       Controller
//           ↓
//        Service
//           ↓
//       Repository
//           ↓
//         MySQL

@Service
public class TileService{

    private final TileRepository tileRepository;// every tile service object needds a tilerepository object to work

    public TileService(TileRepository tileRepository){ // This is constructor dependency injection ie using tileRepository object 

        this.tileRepository = tileRepository;
    }

    public List<Tile> getAllTiles(){
        return tileRepository.findAll();
    }

    public Tile getTile(Long id){
        return tileRepository.findById(id).orElseThrow(()-> new RuntimeException("Tile Not Found"));
    }

    public Tile saveTile(Tile tile){
        return tileRepository.save(tile);
    }

    public Tile updateTile(Long id, Tile updatedTile){

        Tile existingTile= tileRepository.findById(id)
        .orElseThrow(()-> new RuntimeException("Tile not found"));

        existingTile.setName(updatedTile.getName());
        existingTile.setType(updatedTile.getType());
        existingTile.setTarget(updatedTile.getTarget());
        existingTile.setIcon(updatedTile.getIcon());

        return tileRepository.save(existingTile);
    }
    public Tile clearTile(Integer slotNumber) {

    Tile tile =
            tileRepository
                    .findBySlotNumber(slotNumber)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Tile slot not found: "
                                            + slotNumber
                            )
                    );

    /*
     * Delete the physical icon file.
     */
    if (tile.getIcon() != null) {

        File iconFile =
                new File(
                        "src/main/resources/static/icons",
                        tile.getIcon()
                );

        if (iconFile.exists()) {

            boolean deleted =
                    iconFile.delete();

            System.out.println(
                    "Icon deleted: " + deleted
            );
        }
    }


    /*
     * Keep the slot itself.
     *
     * Only remove its configuration.
     */
    tile.setName(null);
    tile.setType(null);
    tile.setTarget(null);
    tile.setIcon(null);


    return tileRepository.save(tile);
}
}