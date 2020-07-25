package com.maanoo.eopa;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public final class ImageDirectory extends ImageCollection {

    private Path directory;

    public ImageDirectory(Path current) {
        super(current);
        this.directory = current.getParent();
    }

    @Override
    public List<Path> getAll() {
        try {
            return Files.list(directory)
                    .filter(ImageLoader::filterImage)
                    .sorted()
                    .collect(Collectors.toList());

        } catch (final IOException e) {
            e.printStackTrace();
            return Arrays.asList();
        }
    }
}
