package com.maanoo.eopa;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.file.Path;


@SuppressWarnings("serial")
public class CanvasImage extends Canvas {

    private Path path;

    public CanvasImage(Path path) {
        this.path = path;

        setBackground(Config.Active.background);
        scale = Config.Active.scaleImage;
    }

    public void setImage(Path path) {
        this.path = path;
        repaint();
    }

    public BufferedImage getImage() {
        return ImageLoader.load(path, false);
    }

    // TODO simplify, this was refactored out of else
    @Override
    protected float getFitScale() {
        final BufferedImage image = getImage();

        final int cw = (int) (getWidth() * (1 - ViewBorder));
        final int ch = (int) (getHeight() * (1 - ViewBorder));
        final float cratio = cw * 1.f / ch;

        final int iw = image.getWidth();
        final int ih = image.getHeight();
        final float iratio = iw * 1.f / ih;

        float dw = cw;

        if (cratio > iratio) {
            dw = iw * ch / ih;
        }

        float scale = dw / iw;

        if (scale > 1 && iw * ih > 4096 && this.scale == -1) {
            scale = 1;
        } else if (scale > MaxScale) {
            scale = MaxScale;
        }

        // round
        scale = (int) (scale * iw) / 1f / iw;

        return scale;
    }

    @Override
    public void paintComponent(Graphics graphics, PaintMode mode) {
        super.paintComponent(graphics, mode);
        final Graphics2D g = (Graphics2D) graphics;

        final BufferedImage image = getImage();
        if (image == null) return;

        final float scale;
        if (!locked) {
            scale = getScale();
            currentScale = scale;
        } else {
            scale = currentScale;
        }
//        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (!locked) {
            currentInter = scale < 2 ? true : false;
        }
        setInterpolation(g, currentInter);

        final int dw = (int) (scale * image.getWidth());
        final int dh = (int) (scale * image.getHeight());

        final int dx = (getWidth() - dw) / 2;
        final int dy = (getHeight() - dh) / 2;

        if (mode == PaintMode.Alpha) {
            g.setColor(invertColor(getBackground()));
            g.fillRect(dx, dy, dw, dh);

        } else {
            g.drawImage(image, dx, dy, dw, dh, getBackground(), this);
        }
    }

    private static Color invertColor(Color color) {
        return color != Color.BLACK ? Color.BLACK : Color.WHITE;
    }
}