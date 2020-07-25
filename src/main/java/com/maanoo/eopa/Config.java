package com.maanoo.eopa;

import java.awt.Color;

public class Config {

    public static final Config Active = new Config();

    public Color background = Color.BLACK;

    public float scaleImage = -1;
    public float scaleGrid = -1;

    public int edgeBorder = 50;

    public int startWidth = 800;
    public int startHeight = 600;
}
