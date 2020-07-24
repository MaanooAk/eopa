package com.maanoo.eopa;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.List;


@SuppressWarnings("serial")
public class CanvasGrid extends Canvas {

    private BufferedImage image;
    private ImageDirectory directory;

    public final float ViewBorder = 0;
    public final int MaxScale = 32;

    public float scale = -1;
    public boolean locked = false;

    public float currentScale = 0;
    public boolean currentInter = false;

    public int gridW = 8;
    public int gridH = 6;
    public int offset = 0;

    public CanvasGrid(ImageDirectory directory) {
        this.directory = directory;

        image = ImageLoader.load(directory.getCurrent());
    }

    public BufferedImage getImage() {
        return image;
    }

    private float getFitScale() { // TODO simplify, this was refactored out of else

        final int cw = (int) (getWidth() * (1 - ViewBorder)) / gridW;
        final int ch = (int) (getHeight() * (1 - ViewBorder)) / gridH;
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

        final List<Path> all = directory.getAll();

        for (int ix = 0; ix < gridW; ix++) {
            for (int iy = 0; iy < gridH; iy++) {
                final int index = ix + iy * gridW + offset;
                if (index < 0 || index >= all.size()) continue;

                final BufferedImage image = ImageLoader.load(all.get(index));
                if (image == null) continue;

                final int dw = (int) (scale * image.getWidth());
                final int dh = (int) (scale * image.getHeight());

                final int dx = ix * getWidth() / gridW + (getWidth() / gridW - dw) / 2;
                final int dy = iy * getHeight() / gridH + (getHeight() / gridH - dh) / 2;

                g.setClip(ix * getWidth() / gridW, iy * getHeight() / gridH,
                        getWidth() / gridW - 1, getHeight() / gridH - 1);

                g.drawImage(image, dx, dy, dw, dh, this);
            }
        }

    }

    public float getScale() {
        return (scale > 0) ? scale : getFitScale();
    }

    private static Color invertColor(Color color) {
        return color != Color.BLACK ? Color.BLACK : Color.WHITE;
    }

}
