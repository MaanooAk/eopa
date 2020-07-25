package com.maanoo.eopa;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public final class ImageList extends ImageCollection {

    private ArrayList<Path> paths;

    public ImageList(ArrayList<Path> paths) {
        super(paths.get(0));
        this.paths = paths;
    }

    @Override
    public List<Path> getAll() {
        return paths;
    }

}
