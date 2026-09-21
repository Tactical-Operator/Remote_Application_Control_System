package com.remotepulse.server.repository;

import com.remotepulse.server.entity.Tile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TileRepository extends JpaRepository<Tile, Long>{

        Optional<Tile> findBySlotNumber(Integer slotNumber);
}

    
// This gives us database operations such as:

// findAll()
// findById()
// save()
// delete()

// without writing any sql query
