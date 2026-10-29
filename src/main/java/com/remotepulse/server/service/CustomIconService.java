
package com.remotepulse.server.service;

import com.remotepulse.server.entity.Tile;
import com.remotepulse.server.repository.TileRepository;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;

/**
 * Handles custom icon uploads for Remote Pulse tiles.
 *
 * Decodes uploaded Base64 image data, resizes the image while
 * preserving its aspect ratio, saves it as a PNG, and updates
 * the tile configuration in the database.
 */
@Service
public class CustomIconService {

        private final TileRepository tileRepository;

        public CustomIconService(TileRepository tileRepository) {
                this.tileRepository = tileRepository;
        }

        /**
         * Saves a custom icon for the specified tile.
         *
         * @param slotNumber  the Remote Pulse slot number
         * @param imageBase64 the uploaded image encoded as Base64
         * @return the updated tile configuration
         */
        public Tile saveCustomIcon(
                        Integer slotNumber,
                        String imageBase64) {

                // Find the existing tile using its slot number.
                Tile tile = tileRepository
                                .findBySlotNumber(slotNumber)
                                .orElseThrow(() -> new RuntimeException(
                                                "Tile slot not found: " + slotNumber));

                // Remove the data URL prefix if one is present.
                if (imageBase64.contains(",")) {
                        imageBase64 = imageBase64.substring(
                                        imageBase64.indexOf(",") + 1);
                }

                try {
                        // Decode the Base64 string into image bytes.
                        byte[] imageBytes = Base64.getDecoder().decode(
                                        imageBase64);

                        // Read the uploaded bytes as an image.
                        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);

                        BufferedImage originalImage = ImageIO.read(inputStream);

                        // Reject image data that Java cannot read as an image.
                        if (originalImage == null) {
                                throw new RuntimeException(
                                                "Unsupported image format.");
                        }

                        // Create a standard 256 x 256 transparent icon canvas.
                        int canvasSize = 256;

                        BufferedImage icon = new BufferedImage(
                                        canvasSize,
                                        canvasSize,
                                        BufferedImage.TYPE_INT_ARGB);

                        Graphics2D graphics = icon.createGraphics();

                        try {
                                // Clear the canvas to make its background transparent.
                                graphics.setComposite(AlphaComposite.Clear);

                                graphics.fillRect(
                                                0,
                                                0,
                                                canvasSize,
                                                canvasSize);

                                graphics.setComposite(AlphaComposite.SrcOver);

                                // Enable high-quality image rendering.
                                graphics.setRenderingHint(
                                                RenderingHints.KEY_INTERPOLATION,
                                                RenderingHints.VALUE_INTERPOLATION_BICUBIC);

                                graphics.setRenderingHint(
                                                RenderingHints.KEY_RENDERING,
                                                RenderingHints.VALUE_RENDER_QUALITY);

                                graphics.setRenderingHint(
                                                RenderingHints.KEY_ANTIALIASING,
                                                RenderingHints.VALUE_ANTIALIAS_ON);

                                // Calculate the scale while preserving the image's aspect ratio.
                                double scale = Math.min(
                                                (double) canvasSize / originalImage.getWidth(),
                                                (double) canvasSize / originalImage.getHeight());

                                int newWidth = (int) (originalImage.getWidth() * scale);

                                int newHeight = (int) (originalImage.getHeight() * scale);

                                // Calculate coordinates to center the resized image.
                                int x = (canvasSize - newWidth) / 2;

                                int y = (canvasSize - newHeight) / 2;

                                // Draw the resized image onto the transparent canvas.
                                graphics.drawImage(
                                                originalImage,
                                                x,
                                                y,
                                                newWidth,
                                                newHeight,
                                                null);

                        } finally {
                                // Release graphics resources after rendering.
                                graphics.dispose();
                        }

                        // Create the directory used to store custom icons.
                        File iconsDirectory = new File("data/icons");

                        if (!iconsDirectory.exists()) {
                                iconsDirectory.mkdirs();
                        }

                        // Generate the icon filename for this tile.
                        File outputFile = new File(
                                        iconsDirectory,
                                        "slot-" + slotNumber + ".png");

                        // Save the processed icon in PNG format.
                        ImageIO.write(
                                        icon,
                                        "png",
                                        outputFile);

                        // Store the icon filename in the tile configuration.
                        tile.setIcon(
                                        "slot-" + slotNumber + ".png");

                        // Save the updated tile to the database.
                        return tileRepository.save(tile);

                } catch (Exception e) {

                        // Report failures encountered while processing or saving the icon.
                        throw new RuntimeException(
                                        "Failed to save custom icon",
                                        e);
                }
        }
}
