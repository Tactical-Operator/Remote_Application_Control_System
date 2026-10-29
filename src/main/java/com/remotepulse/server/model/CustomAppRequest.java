
package com.remotepulse.server.model;

/**
 * Represents the request data received when configuring a custom application tile.
 *
 * Used by the CustomAppController to receive the selected tile slot,
 * application display name, and executable file path from the frontend.
 */
public class CustomAppRequest {

    // Slot number where the custom application will be configured.
    private Integer slotNumber;

    // Display name to show on the tile in the Remote Pulse interface.
    private String name;

    // Full filesystem path to the custom application's executable.
    private String path;

    /**
     * No-argument constructor used when creating the request object.
     */
    public CustomAppRequest() {
    }

    // Returns the selected tile slot number.
    public Integer getSlotNumber() {
        return slotNumber;
    }

    // Sets the tile slot number received in the request.
    public void setSlotNumber(Integer slotNumber) {
        this.slotNumber = slotNumber;
    }

    // Returns the custom application's display name.
    public String getName() {
        return name;
    }

    // Sets the custom application's display name.
    public void setName(String name) {
        this.name = name;
    }

    // Returns the executable's filesystem path.
    public String getPath() {
        return path;
    }

    // Sets the executable's filesystem path.
    public void setPath(String path) {
        this.path = path;
    }
}
