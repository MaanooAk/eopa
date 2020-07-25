package com.maanoo.eopa;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.nio.file.Path;

import javax.swing.JFrame;


@SuppressWarnings("serial")
public class EopaFrame extends JFrame implements WatcherManager.Listener {

    private final ImageDirectory directory;

    private Scene scene;

    public EopaFrame(Path path) {
        this(new ImageDirectory(path));
    }

    public EopaFrame(ImageDirectory directory) {
        this.directory = directory;

        getContentPane().setLayout(new BorderLayout());
        setImage(directory.getCurrent());

        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        WatcherManager.register(getDirectory().getAll(), this);
    }

    public void setScene(Scene newScene) {

        // get the current size in order to pass it to the new canvas
        final Dimension size = scene == null
                ? new Dimension(Config.Active.startWidth, Config.Active.startHeight)
                : scene.canvas.getSize();

        if (scene != null) {
            // remove the current scene
            removeMouseWheelListener(scene.mouseListener);
            removeKeyListener(scene.keyListener);
            getContentPane().remove(scene.canvas);
        }
        scene = newScene;

        // add the canvas
        scene.canvas.setPreferredSize(size);
        getContentPane().add(scene.canvas, BorderLayout.CENTER);
        // set up the listeners
        scene.canvas.addMouseListener(scene.mouseListener);
        addMouseWheelListener(scene.mouseListener);
        addKeyListener(scene.keyListener);

        pack(); // needed
    }

    public void setImage(Path newPath) {

        if (newPath == null) {

            final String name = directory.getDirectory().getFileName().toString();
            setTitle(name);

            final Scene scene = Scene.createGrid(this, directory.getCurrent());

            setScene(scene);

        } else {

            final String name = newPath.getFileName().toString();
            setTitle(name);

            if (scene != null && scene.canvas instanceof CanvasImage) {
                ((CanvasImage) scene.canvas).setImage(newPath);
            } else {
                final Scene scene = Scene.createImage(this, newPath);
                setScene(scene);
            }

            final BufferedImage image = ((CanvasImage) scene.canvas).getImage();

            scene.getHeaderMenuItem().setText(name + "  " + image.getWidth() + "x" + image.getHeight() + " "
                    + image.getColorModel().getComponentSize().length);

            directory.setCurrent(newPath);
        }

        scene.canvas.repaint();
    }

    @Override
    public void onFileChange(Path path) {
        // an image has been modified, force reload the image
        ImageLoader.load(directory.getDirectory().resolve(path), true);
        // repaint the current canvas, even if the reloaded image is not displayed
        scene.canvas.repaint();
    }

    public final ImageDirectory getDirectory() {
        return directory;
    }
}
