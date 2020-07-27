package com.maanoo.eopa;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;


@SuppressWarnings("serial")
public abstract class Canvas extends JPanel {

    public static final float ViewBorder = 0;
    public static final int MaxScale = 32;

    public float scale = -1;
    public boolean locked = false;

    public float currentScale = 0;
    public boolean currentInter = false;

    private boolean painting;
    private boolean requestRepaint;

    public enum Side {
        None, R, D, L, U;
    }

    private Side highlight = Side.None;

    public enum PaintMode {
        Normal, Alpha;
    }

    @Override
    public final void paintComponent(Graphics g) {
        painting = true;
        paintComponent(g, PaintMode.Normal);
        painting = false;

        if (requestRepaint) repaint();
    }

    public void paintComponent(Graphics graphics, PaintMode mode) {
        super.paintComponent(graphics);
    }

    protected final void paintHighlight(Graphics g) {
        if (highlight == Side.None) return;

        g.setColor(Config.Active.HighlightColor);
        final int hwidth = Config.Active.HighlightWidth;

        if (highlight == Side.L) g.fillRect(0, 0, hwidth, getHeight());
        else if (highlight == Side.U) g.fillRect(0, 0, getWidth(), hwidth);
        else if (highlight == Side.R) g.fillRect(getWidth() - hwidth, 0, hwidth, getHeight());
        else if (highlight == Side.D) g.fillRect(0, getHeight() - hwidth, getWidth(), hwidth);
    }

    @Override
    public void repaint() {
        if (!painting) {
            requestRepaint = false;
            super.repaint();
        } else {
            requestRepaint = true;
        }
    }

    protected abstract float getFitScale();

    public final float getScale() {
        return (scale > 0) ? scale : getFitScale();
    }

    public final void setHighlight(Side highlight) {
        if (this.highlight == highlight) return;
        this.highlight = highlight;
        repaint();
    }

    public final Side getHighlight() {
        return highlight;
    }

    protected static void setInterpolation(Graphics g, boolean bilinear) {

        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                bilinear ? RenderingHints.VALUE_INTERPOLATION_BILINEAR
                        : RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
    }

}
