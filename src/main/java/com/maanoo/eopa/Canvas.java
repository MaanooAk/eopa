package com.maanoo.eopa;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;


@SuppressWarnings("serial")
public abstract class Canvas extends JPanel {

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
