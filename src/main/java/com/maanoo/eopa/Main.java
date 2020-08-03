package com.maanoo.eopa;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


public final class Main {

    private Main() {}

    // TODO unload images
    // TODO clone window
    // TODO handle max offset

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
        }

        final ArrayList<Path> paths = new ArrayList<>();

        if (args.length > 0) {
            for (final String i : args) {
                paths.add(Paths.get(i).toAbsolutePath());
            }

        } else {
            final Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()) {
                paths.add(Paths.get(scanner.nextLine()).toAbsolutePath());
            }
            scanner.close();
        }

        if (paths.size() == 0) {
            System.exit(1);
        }

        Config.load();
        Canvas.Background = Config.Active.Background1;

        if (paths.size() == 1) {
            final EopaFrame frame = new EopaFrame(new ImageDirectory(paths.get(0)));
            frame.setVisible(true);

        } else {
            final EopaFrame frame = new EopaFrame(new ImageList(paths));
            frame.setVisible(true);
        }
    }

}
