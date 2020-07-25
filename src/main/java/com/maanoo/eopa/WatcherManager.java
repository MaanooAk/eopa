package com.maanoo.eopa;

import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Collection;
import java.util.HashSet;


public final class WatcherManager {

    private WatcherManager() {}

    public interface Listener {
        void onFileChange(Path path);
    }

    private static WatchService watcher;

    public static void register(Collection<Path> paths, Listener listener) {
        unregister();

        final HashSet<Path> parents = new HashSet<Path>();
        final HashSet<String> names = new HashSet<String>();
        for (final Path i : paths) {
            parents.add(i.getParent());
            names.add(i.getFileName().toString());
        }
//        System.out.println(parents);
//        System.out.println(names);

        try {
            watcher = FileSystems.getDefault().newWatchService();
            for (final Path path : parents) {
                path.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_MODIFY);
            }

            new Thread(() -> {
                listen(watcher, names, listener);
            }).start();

        } catch (final IOException e) {
        }
    }

    public static void unregister() {
        if (watcher == null) return;
        try {
            watcher.close();
        } catch (final IOException e) {
        }
    }

    private static void listen(WatchService watcher, HashSet<String> names, Listener listener) {
        try {
            while (true) {
                final WatchKey key = watcher.take();
                for (final WatchEvent<?> event : key.pollEvents()) {
                    final Path context = (Path) event.context();
//                    System.out.println(context);
                    if (names.contains(context.toString()) || ImageLoader.filterImage(context)) {
                        listener.onFileChange(context);
                    }
                }
                key.reset();
            }
        } catch (final InterruptedException | ClosedWatchServiceException e) {
        }
    }

}
