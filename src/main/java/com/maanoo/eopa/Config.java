package com.maanoo.eopa;

import java.awt.Color;


public class Config {

    public static final Config Active = new Config();

    // ===

    public int Width = 800;
    public int Height = 600;

    public boolean BackgroundImageLoading = true;

    public int GridCols = 8;
    public int GridRows = 6;

    public Color Background = Color.BLACK;

    public Color HighlightColor = new Color(0x20_0ba2cd, true);
    public int HighlightWidth = 50;

//    public float ScaleImage = -1;
//    public float ScaleGrid = -1;

    // ===

}
