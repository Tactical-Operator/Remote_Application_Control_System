
package com.remotepulse.server.service;

import com.remotepulse.server.entity.Tile;
import com.remotepulse.server.repository.TileRepository;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;

@Service
public class CustomIconService {

    private final TileRepository tileRepository;

    public CustomIconService(TileRepository tileRepository) {
        this.tileRepository = tileRepository;
    }

    public Tile saveCustomIcon(
            Integer slotNumber,
            String imageBase64
    ) {

        /*
         * Find the tile using its slot number.
         */
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
         * Remove the Base64 data prefix.
         *
         * Example:
         *
         * data:image/png;base64,ABC123...
         *
         * becomes:
         *
         * ABC123...
         */
        if (imageBase64.contains(",")) {

            imageBase64 =
                    imageBase64.substring(
                            imageBase64.indexOf(",") + 1
                    );
        }

        try {

            /*
             * Convert Base64 into bytes.
             */
            byte[] imageBytes =
                    Base64.getDecoder().decode(
                            imageBase64
                    );

            /*
             * Convert bytes into an image.
             */
            ByteArrayInputStream inputStream =
                    new ByteArrayInputStream(
                            imageBytes
                    );

            BufferedImage originalImage =
                    ImageIO.read(inputStream);

            if (originalImage == null) {

                throw new RuntimeException(
                        "Unsupported image format."
                );
            }

            /*
             * Create our standard 256 x 256 icon.
             */
            int canvasSize = 256;

            BufferedImage icon =
                    new BufferedImage(
                            canvasSize,
                            canvasSize,
                            BufferedImage.TYPE_INT_ARGB
                    );

            Graphics2D graphics =
                    icon.createGraphics();

            try {

                /*
                 * Transparent background.
                 */
                graphics.setComposite(
                        AlphaComposite.Clear
                );

                graphics.fillRect(
                        0,
                        0,
                        canvasSize,
                        canvasSize
                );

                graphics.setComposite(
                        AlphaComposite.SrcOver
                );

                /*
                 * High-quality image rendering.
                 */
                graphics.setRenderingHint(
                        RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BICUBIC
                );

                graphics.setRenderingHint(
                        RenderingHints.KEY_RENDERING,
                        RenderingHints.VALUE_RENDER_QUALITY
                );

                graphics.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                /*
                 * Calculate the size while
                 * preserving the original
                 * aspect ratio.
                 */
                double scale =
                        Math.min(
                                (double) canvasSize /
                                        originalImage.getWidth(),

                                (double) canvasSize /
                                        originalImage.getHeight()
                        );

                int newWidth =
                        (int) (
                                originalImage.getWidth()
                                        * scale
                        );

                int newHeight =
                        (int) (
                                originalImage.getHeight()
                                        * scale
                        );

                /*
                 * Center the image.
                 */
                int x =
                        (canvasSize - newWidth) / 2;

                int y =
                        (canvasSize - newHeight) / 2;

                /*
                 * Draw the resized image.
                 */
                graphics.drawImage(
                        originalImage,
                        x,
                        y,
                        newWidth,
                        newHeight,
                        null
                );

            } finally {

                graphics.dispose();
            }

            /*
             * Create icons directory.
             */
            File iconsDirectory =
                    new File(
                            "src/main/resources/static/icons"
                    );

            if (!iconsDirectory.exists()) {

                iconsDirectory.mkdirs();
            }

            /*
             * Create the final filename.
             *
             * Example:
             *
             * slot-3.png
             */
            File outputFile =
                    new File(
                            iconsDirectory,
                            "slot-" +
                                    slotNumber +
                                    ".png"
                    );

            /*
             * Save as PNG.
             */
            ImageIO.write(
                    icon,
                    "png",
                    outputFile
            );

            /*
             * Store filename in database.
             */
            tile.setIcon(
                    "slot-" +
                            slotNumber +
                            ".png"
            );

            /*
             * Save tile.
             */
            return tileRepository.save(tile);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to save custom icon",
                    e
            );
        }
    }
}

