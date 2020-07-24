package com.maanoo.eopa;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class ImageDirectory {

    private Path directory;
    private Path current;

    public ImageDirectory(Path current) {
        this.directory = current.getParent();
        this.current = current;
    }

    public Path next(int direction) {

        final List<Path> list = getAll();
        if (list.size() == 0) return current;

        final int index = list.indexOf(current);

        final int newIndex;
        if (index == -1 || index + direction == list.size() || index + direction == -1) {
            newIndex = direction < 0 ? (list.size() - 1) : 0;
        } else {
            newIndex = index + direction;
        }

        return current = list.get(newIndex);
    }

    public List<Path> getAll() {
        try {
            return Files.list(directory)
                    .filter(ImageManager::filterImage)
                    .sorted()
                    .collect(Collectors.toList());

        } catch (final IOException e) {
            e.printStackTrace();
            return Arrays.asList();
        }
    }

    public Path getDirectory() {
        return directory;
    }

    public Path getCurrent() {
        return current;
    }

}
