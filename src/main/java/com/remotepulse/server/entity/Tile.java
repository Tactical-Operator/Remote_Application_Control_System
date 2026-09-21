package com.remotepulse.server.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tiles")
public class Tile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "slot_number", nullable = false, unique = true)
    private Integer slotNumber;

    @Column
    private String name;

    @Column
    private String type;

    @Column(columnDefinition = "TEXT")
    private String target;

    @Column
    private String icon;

    public Tile() {
    }

    public Long getId() {
        return id;
    }

    public Integer getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(Integer slotNumber) {
        this.slotNumber = slotNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}

// What we're representing

// A database record will eventually look like:

// id:          1
// slotNumber:  1
// name:        Notepad
// type:        APPLICATION
// target:      C:\Windows\System32\notepad.exe
// icon:        notepad.png

// But an empty tile will be something like:

// id:          1
// slotNumber:  1
// name:        null
// type:        null
// target:      null
// icon:        null
