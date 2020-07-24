package com.maanoo.eopa;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;

import javax.imageio.ImageIO;


public final class ImageManager {

    private ImageManager() {}

    public static BufferedImage loadImage(Path path) {

        try {
            return ImageIO.read(path.toFile());
        } catch (final IOException e) {
            return null;
        }
    }

    private static final HashSet<String> imageExtensions = new HashSet<String>(
            Arrays.asList("png", "jpg", "jpeg"));

    public static boolean filterImage(Path i) {

        if (Files.isDirectory(i)) return false;

        final String name = i.getFileName().toString();
        final int index = name.lastIndexOf('.');
        if (index < 1) return false;

        final String extension = name.substring(index + 1);
        return imageExtensions.contains(extension);
    }

}
