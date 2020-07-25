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

    public enum PaintMode { // TODO clean up
        Normal, Alpha;
    }

    @Override
    public void paintComponent(Graphics g) {
        paintComponent(g, PaintMode.Normal);
    }

    public void paintComponent(Graphics graphics, PaintMode mode) {
        super.paintComponent(graphics);
    }

    protected static void setInterpolation(Graphics g, boolean bilinear) {
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                bilinear ? RenderingHints.VALUE_INTERPOLATION_BILINEAR
                        : RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
    }

}
