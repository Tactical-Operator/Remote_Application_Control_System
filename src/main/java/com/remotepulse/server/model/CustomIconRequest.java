
package com.remotepulse.server.model;

/**
 * Represents the request data received when uploading a custom tile icon.
 *
 * Used by CustomIconController to receive the target tile's slot number
 * and the image data encoded as a Base64 string from the frontend.
 */
public class CustomIconRequest {

    // Slot number of the tile whose icon is being customized.
    private Integer slotNumber;

    // Base64-encoded image data submitted by the frontend.
    private String imageBase64;

    /**
     * No-argument constructor used to create the request object.
     */
    public CustomIconRequest() {
    }

    // Returns the slot number of the tile.
    public Integer getSlotNumber() {
        return slotNumber;
    }

    // Sets the slot number of the tile.
    public void setSlotNumber(Integer slotNumber) {
        this.slotNumber = slotNumber;
    }

    // Returns the Base64-encoded image data.
    public String getImageBase64() {
        return imageBase64;
    }

    // Sets the Base64-encoded image data received from the frontend.
    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }
}
