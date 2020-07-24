package com.maanoo.eopa;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;


public class CanvasImage extends Canvas {

    private BufferedImage image;

    public final float ViewBorder = 0;
    public final int MaxScale = 32;

    public float scale = -1;
    public boolean locked = false;

    public float currentScale = 0;
    public boolean currentInter = false;

    public CanvasImage(BufferedImage image) {
        setImage(image);
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        repaint();
    }

    public BufferedImage getImage() {
        return image;
    }

    private float getFitScale() {

        final int cw = (int) (getWidth() * (1 - ViewBorder));
        final int ch = (int) (getHeight() * (1 - ViewBorder));
        final float cratio = cw * 1.f / ch;

        final int iw = image.getWidth();
        final int ih = image.getHeight();
        final float iratio = iw * 1.f / ih;

        float dw = cw;
        float dh = ch;

        if (cratio > iratio) {
            dw = iw * ch / ih;
        } else {
            dh = ih * cw / iw;
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

    public enum PaintMode {
        Normal, Alpha;
    }

    @Override
    public void paintComponent(Graphics g) {
        paintComponent(g, PaintMode.Normal);
    }

    public void paintComponent(Graphics graphics, PaintMode mode) {
        super.paintComponent(graphics);
        final Graphics2D g = (Graphics2D) graphics;

        if (image == null) return;

        final float scale = getScale();
        currentScale = scale;

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
            g.drawImage(image, dx, dy, dw, dh, this);
        }
    }

    public float getScale() {
        return (scale > 0) ? scale : getFitScale();
    }

//    @Override
//    public void setSize(int width, int height) {
//        super.setSize(width, height);
//        invalidate();
//    }

    private static Color invertColor(Color color) {
        return color != Color.BLACK ? Color.BLACK : Color.WHITE;
    }
}