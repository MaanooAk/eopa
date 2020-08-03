package com.maanoo.eopa;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


/**
 * Main entry point for eopa.
 *
 * TODO doc
 *
 * @author Akritas Akritidis
 */
public final class Main {

    private Main() {}

    // TODO unload images
    // TODO clone window
    // TODO handle max offset

    public static void main(String[] args) {

        // load and apply config
        Config.load();
        Canvas.Background = Config.Active.Background1;

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
        }

        final ArrayList<Path> paths = new ArrayList<>();

        for (final String i : args) {
            if (i.startsWith("-")) {
                // parse option argument
                if (i.equals("-")) {
                    readInput(paths);
                } else {
                    System.err.println("Unknown option: " + i);
                }
            } else {
                // add argument as path
                paths.add(Paths.get(i).toAbsolutePath());
            }
        }

        if (paths.size() == 0) {
            System.exit(1); // no paths to show
        }

        show(paths);
    }

    public static void show(List<Path> paths) {
        // create frame and show
        final EopaFrame frame;
        if (paths.size() == 1) {
            frame = new EopaFrame(new ImageDirectory(paths.get(0)));
        } else {
            frame = new EopaFrame(new ImageList(paths));
        }
        frame.setVisible(true);
    }

    private static void readInput(List<Path> paths) {
        // add every input line as a path
        final Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            paths.add(Paths.get(scanner.nextLine()).toAbsolutePath());
        }
        scanner.close();
    }

}
