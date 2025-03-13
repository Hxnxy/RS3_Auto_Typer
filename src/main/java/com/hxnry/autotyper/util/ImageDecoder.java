package com.hxnry.autotyper.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Hxnry
 * @since August 24, 2016
 */
public class ImageDecoder {

    public static BufferedImage decodeToImage(String resource) {
        byte[] bytes = Base64.decode(resource, resource.length());
        InputStream in = new ByteArrayInputStream(bytes);
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(in);
        } catch (IOException e) {
            //e.printStackTrace();
        }
        return bufferedImage;
    }
}
