
package com.remotepulse.server.entity;

import jakarta.persistence.*;

/**
 * Represents a tile in the Remote Pulse application.
 *
 * This entity maps to the "tiles" table in the database and stores
 * each tile's position, display name, type, launch target, and icon.
 * It is used by the application's services and repository to manage
 * tile configurations.
 */
@Entity
@Table(name = "tiles")
public class Tile {

    // Unique database ID generated automatically when a tile is inserted.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Position of the tile in the Remote Pulse layout.
    // Each slot number must be present and unique in the database.
    @Column(name = "slot_number", nullable = false, unique = true)
    private Integer slotNumber;

    // Display name shown to the user, such as "Notepad" or "Spotify".
    @Column
    private String name;

    // Identifies the tile type, such as WEBSITE, CUSTOM_APP, or SYSTEM_BUTTON.
    @Column
    private String type;

    // Stores the launch target, such as an executable path, URL, or action ID.
    // TEXT allows longer values to be stored in the database.
    @Column(columnDefinition = "TEXT")
    private String target;

    // Stores the icon path or reference used to display the tile's icon.
    @Column
    private String icon;

    /**
     * No-argument constructor required by JPA for entity instantiation.
     */
    public Tile() {
    }

    // Returns the database ID of this tile.
    public Long getId() {
        return id;
    }

    // Returns the tile's layout position.
    public Integer getSlotNumber() {
        return slotNumber;
    }

    // Updates the tile's layout position.
    public void setSlotNumber(Integer slotNumber) {
        this.slotNumber = slotNumber;
    }

    // Returns the tile's display name.
    public String getName() {
        return name;
    }

    // Updates the tile's display name.
    public void setName(String name) {
        this.name = name;
    }

    // Returns the tile type.
    public String getType() {
        return type;
    }

    // Updates the tile type.
    public void setType(String type) {
        this.type = type;
    }

    // Returns the tile's launch target or action identifier.
    public String getTarget() {
        return target;
    }

    // Updates the tile's launch target or action identifier.
    public void setTarget(String target) {
        this.target = target;
    }

    // Returns the tile's icon reference.
    public String getIcon() {
        return icon;
    }

    // Updates the tile's icon reference.
    public void setIcon(String icon) {
        this.icon = icon;
    }
}
