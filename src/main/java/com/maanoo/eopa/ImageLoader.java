package com.maanoo.eopa;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;


public final class ImageLoader {

    private ImageLoader() {}

    // ===

    private static final HashMap<Path, LazyImage> store = new HashMap<Path, LazyImage>();

    public static LazyImage load(Path path, boolean force) {
        if (!force) {
            final LazyImage storedImage = store.get(path);
            if (storedImage != null) return storedImage;
        }
        final LazyImage image = new LazyImage(path);
        store.put(path, image);
        return image;
    }

    // ===

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
