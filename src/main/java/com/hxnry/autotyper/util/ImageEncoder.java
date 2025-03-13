package com.hxnry.autotyper.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Hxnry
 * @since August 24, 2016
 */
public class ImageEncoder {

    public static String encodeToString(BufferedImage bufferedImage) {
        byte[] bytes = getImageBytes(bufferedImage);
        return Base64.encode(bytes, bytes.length);
    }

    public static byte [] getImageBytes(BufferedImage image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", baos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }
}

