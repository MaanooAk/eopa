package com.maanoo.eopa;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;


public final class Images {

    private Images() {};

    public static final BufferedImage Loading = loadResourceImage("image-loading.png");
    public static final BufferedImage Failed = loadResourceImage("image-failed.png");

    private static BufferedImage loadResourceImage(String path) {
        try {
            return ImageIO.read(Images.class.getResourceAsStream(path));
        } catch (final IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
