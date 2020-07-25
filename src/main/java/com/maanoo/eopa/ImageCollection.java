package com.maanoo.eopa;

import java.nio.file.Path;
import java.util.List;


public abstract class ImageCollection {

    private Path current;

    public ImageCollection(Path current) {
        this.current = current;
    }

    public void setCurrent(Path current) {
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

    public abstract List<Path> getAll();

    public Path getDirectory() {
        return current.getParent();
    }

    public Path getCurrent() {
        return current;
    }
}
