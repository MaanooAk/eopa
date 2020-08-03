package com.maanoo.eopa;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public final class ImageList extends ImageCollection {

    private ArrayList<Path> paths;

    public ImageList(List<Path> paths) {
        super(paths.get(0));
        this.paths = new ArrayList<Path>(paths);
    }

    @Override
    public List<Path> getAll() {
        return paths;
    }

}
