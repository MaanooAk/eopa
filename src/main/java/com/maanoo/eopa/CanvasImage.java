package com.maanoo.eopa;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.file.Path;


@SuppressWarnings("serial")
public class CanvasImage extends Canvas {

    private Path path;

    private int cx;
    private int cy;

    private int rotation;

    public CanvasImage(Path path) {
        this.path = path;

        setBackground(Background);
        scale = -1;
    }

    public void setImage(Path path) {
        this.path = path;
        repaint();
    }

    public BufferedImage getImage() {
        return ImageLoader.load(path, false).getImage();
    }

    // TODO simplify, this was re-factored out of else
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
        } else {
            final int maxScale = Config.Active.MaxScale;
            if (scale > maxScale) scale = maxScale;
        }

        // round
        scale = (int) (scale * iw) / 1f / iw;

        return scale;
    }

    @Override
    public void setScale(float scale) {

        if (this.scale > 0 && scale > 0) {
            cx *= scale / this.scale;
            cy *= scale / this.scale;
        }

        this.scale = scale;
    }

    public void moveCenter(int dx, int dy) {
        cx += dx;
        cy += dy;
        repaint();
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

        g.translate(cx, cy);
        if (rotation != 0) {
            g.rotate(rotation * Math.PI / 2, getWidth() / 2, getHeight() / 2);
        }

        if (mode == PaintMode.Alpha) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.fillRect(dx, dy, dw, dh);

        } else {
            g.drawImage(image, dx, dy, dw, dh, getBackground(), this);
        }

        if (rotation != 0) {
            g.rotate(-rotation * Math.PI / 2, getWidth() / 2, getHeight() / 2);
        }
        g.translate(-cx, -cy);

        paintHighlight(g);
    }

    public int getImageWidth() {
        return (rotation % 2 == 0) ? getImage().getWidth() : getImage().getHeight();
    }

    public int getImageHeight() {
        return (rotation % 2 == 1) ? getImage().getWidth() : getImage().getHeight();
    }

    public void rotate(int d) {
        rotation = (rotation + d + 4) % 4;
    }

    public void resetCenter() {
        cx = cy = 0;
        repaint();
    }

    public void resetRotation() {
        rotation = 0;
        repaint();
    }

    public void reset() {
        cx = cy = 0;
        rotation = 0;
        repaint();
    }
}