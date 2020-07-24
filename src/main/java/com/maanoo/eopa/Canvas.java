package com.maanoo.eopa;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;


@SuppressWarnings("serial")
public class Canvas extends JPanel {

    protected static void setInterpolation(Graphics g, boolean bilinear) {
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                bilinear ? RenderingHints.VALUE_INTERPOLATION_BILINEAR
                        : RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
    }

}
