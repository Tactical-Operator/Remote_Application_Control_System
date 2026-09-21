package com.remotepulse.service;

import com.remotepulse.server.entity.Tile;
import com.remotepulse.server.repository.TileRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TileService{

    private final TileRepository tileRepository;

    public TileService(TileRepository tileRepository){
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