package com.maanoo.eopa;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.nio.file.Path;


public final class ClipboardManager {

    private ClipboardManager() {}

    public static void set(String text) {
        final Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
        final StringSelection selection = new StringSelection(text);
        c.setContents(selection, selection);
    }

    public static void set(Path path) {
        final Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
        // TODO change, this impl is linux only?
        final StringSelection selection = new StringSelection("x-special/nautilus-clipboard\n" +
                "copy\n" +
                "file://" + path.toString() + "\n");
        c.setContents(selection, selection);
    }

}
