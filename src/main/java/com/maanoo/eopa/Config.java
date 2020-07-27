package com.maanoo.eopa;

import java.awt.Color;


public class Config {

    public static final Config Active = new Config();

    // TODO normalize names

    public Color background = Color.BLACK;

    public float scaleImage = -1;
    public float scaleGrid = -1;

    public int edgeBorder = 50;

    public int startWidth = 800;
    public int startHeight = 600;

    public int gridW = 8;
    public int gridH = 6;

    public boolean BackgroundImageLoading = true;

    public Color HighlightColor = new Color(0x20_0ba2cd, true);
    public int HighlightWidth = edgeBorder;

}
