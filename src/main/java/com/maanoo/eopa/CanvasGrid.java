package com.maanoo.eopa;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.List;


@SuppressWarnings("serial")
public class CanvasGrid extends Canvas {

    private ImageCollection directory;

    private int gridW = 8;
    private int gridH = 6;
    private int offset = 0;

    public CanvasGrid(ImageCollection directory) {
        this.directory = directory;

        setBackground(Config.Active.Background);
        scale = -1;
        gridW = Config.Active.GridCols;
        gridH = Config.Active.GridRows;

        final List<Path> all = directory.getAll();
        final int index = all.indexOf(directory.getCurrent());
        if (index != -1) {
            addOffset(index - gridH * gridW / 2 + gridW);
        }

        // shrink the grid for small number of images
        while ((gridW > gridH ? (gridW - 1) * gridH : gridW * (gridH - 1)) >= all.size()) {
            if (gridW > gridH) gridW--;
            else gridH--;
            offset = 0;
        }
    }

    // TODO simplify, this was re-factored out of else
    @Override
    protected float getFitScale() {

        final BufferedImage image = ImageLoader.load(directory.getCurrent(), false).getImage();
        if (image == null) return 1;

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

        for (int iy = 0; iy < gridH; iy++) {
            for (int ix = 0; ix < gridW; ix++) {
                final int index = ix + iy * gridW + offset;
                if (index < 0 || index >= all.size()) continue;

                final LazyImage lazyImage = ImageLoader.load(all.get(index), false);

                if (lazyImage.isLoaded() || Config.Active.BackgroundLoading == 0) {
                    final BufferedImage image = lazyImage.getImage();

                    if (image != null) {
                        drawImage(g, image, scale, ix, iy);
                    } else {
                        drawImage(g, Images.Failed, 1, ix, iy);
                    }
                } else {
                    drawImage(g, Images.Loading, 1, ix, iy);

                    lazyImage.loadInBackground(t -> repaint());
                }
            }
        }
        g.setClip(0, 0, getWidth(), getHeight());

        paintHighlight(g);
    }

    private void drawImage(Graphics2D g, BufferedImage image, float scale, int ix, int iy) {

        if (image == null) return;

        final int dw = (int) (scale * image.getWidth());
        final int dh = (int) (scale * image.getHeight());

        final int dx = ix * getWidth() / gridW + (getWidth() / gridW - dw) / 2;
        final int dy = iy * getHeight() / gridH + (getHeight() / gridH - dh) / 2;

        g.setClip(ix * getWidth() / gridW, iy * getHeight() / gridH,
                getWidth() / gridW - 1, getHeight() / gridH - 1);

        g.drawImage(image, dx, dy, dw, dh, getBackground(), this);

    }

    public int getOffset() {
        return offset;
    }

    public void addOffset(int d) {
        offset += d;

        final int screenOffset = gridW * (gridH / 2);
        if (offset < -screenOffset) offset = -screenOffset;

        // TODO limit also the end

        offset = (offset / gridW) * gridW;
        repaint();
    }

    public void addOffsetGrid(int d) {
        addOffset(d * gridW);
    }

    public void changeGrid(int dw, int dh) {
        gridW += dw;
        gridH += dh;

        offset = (offset / gridW) * gridW;
        repaint();

        Config.Active.GridCols = gridW;
        Config.Active.GridRows = gridH;
    }

    public int getGridW() {
        return gridW;
    }

    public int getGridH() {
        return gridH;
    }

}
