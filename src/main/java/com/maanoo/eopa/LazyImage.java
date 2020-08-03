package com.maanoo.eopa;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.SwingUtilities;


public final class LazyImage {

    private final Path path;

    public enum State {
        None, Size, Image, Failed,
    }

    private State state;

    private ImageInputStream stream;
    private ImageReader reader;
    private Dimension size;
    private BufferedImage image;


    public LazyImage(Path path) {
        this.path = path;
        state = State.None;
    }

    // ===

    private void loadReader() {
        try {

            if (Files.isDirectory(path)) {
                state = State.Failed;
                return;
            }

            stream = ImageIO.createImageInputStream(path.toFile());

            final Iterator<ImageReader> imageReaderIter = ImageIO.getImageReaders(stream);
            if (!imageReaderIter.hasNext()) {
                dispose();
                state = State.Failed;
                return;
            }

            reader = imageReaderIter.next();

        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private void loadSize() {
        if (state == State.None) loadReader();
        if (reader != null) try {

            size = new Dimension(reader.getWidth(0), reader.getHeight(0));
            state = State.Size;

        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private void loadImage() {
        if (state == State.None) loadReader();
        if (reader != null) try {

            reader.setInput(stream);
            try {
                image = reader.read(0, reader.getDefaultReadParam());
                size = new Dimension(image.getWidth(), image.getHeight());
                state = State.Image;
            } finally {
                dispose();
            }

        } catch (final Exception e) {
//            e.printStackTrace();
        }
    }

    public void dispose() {

        if (reader != null) {
            reader.dispose();
            reader = null;
        }
        if (stream != null) {
            try {
                stream.close();
            } catch (final IOException e) {
            }
            stream = null;
        }
    }

    // ===

    private static final ExecutorService backgroundService = Executors
            .newFixedThreadPool(Math.max(2, Runtime.getRuntime().availableProcessors() - 1));

    public void loadInBackground(Consumer<LazyImage> listener) {

        backgroundService.execute(() -> {
            getImage();
            SwingUtilities.invokeLater(() -> {
                listener.accept(LazyImage.this);
            });
        });
    }

    public boolean isLoaded() {
        return state == State.Image || state == State.Failed;
    }

    // ===

    public Dimension getSize() {
        if (state == State.None) loadSize();
        return size;
    }

    public BufferedImage getImage() {
        if (state == State.None || state == State.Size) loadImage();
        return image;
    }

    public int getWidth() {
        return (int) getSize().getWidth();
    }

    public int getHeight() {
        return (int) getSize().getHeight();
    }

    public Path getPath() {
        return path;
    }

}
