package com.remotepulse.server.service;

import com.remotepulse.server.entity.Tile;
import com.remotepulse.server.repository.TileRepository;
import org.springframework.stereotype.Service;

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
}