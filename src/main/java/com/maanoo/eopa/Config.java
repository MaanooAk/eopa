package com.maanoo.eopa;

import java.awt.Color;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public final class Config {

    public static final Config Active = new Config();

    // ===

    public int Width = 800;
    public int Height = 600;

    public int BackgroundLoading = 1;

    public int GridCols = 8;
    public int GridRows = 6;

    public Color Background = Color.BLACK;

    public Color HighlightColor = new Color(0x20_0ba2cd, true);
    public int HighlightWidth = 50;

//    public float ScaleImage = -1;
//    public float ScaleGrid = -1;

    // ===

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (final Field field : getClass().getFields()) {
            if (Modifier.isStatic(field.getModifiers())) continue;
            try {
                Object value = field.get(this);
                if (field.getType() == Color.class) {
                    value = Integer.toHexString(((Color) value).getRGB());
                }

                sb.append(field.getName()).append("=").append(value).append('\n');
            } catch (IllegalArgumentException | IllegalAccessException e) {
                System.err.println(e.getMessage());
            }
        }
        return sb.toString();
    }

    public void fromString(String text) {
        final String[] lines = text.split("\n");
        for (final String line : lines) {
            if (line.startsWith("#")) continue;
            final int index = line.indexOf('=');
            if (index == -1) continue;

            final String name = line.substring(0, index);
            final String value = line.substring(index + 1);
            try {
                final Field field = getClass().getField(name);
                if (field.getType() == Color.class) {
                    field.set(this, new Color(Integer.parseUnsignedInt(value, 16), true));
                } else {
                    field.set(this, Integer.parseInt(value));
                }
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public static void load() {
        final Config config = Config.Active;
        final Path path = Paths.get(System.getenv("HOME"), ".config/eopa.conf");
        final String charset = "UTF-8";
        try {
            if (Files.exists(path)) {
                config.fromString(new String(Files.readAllBytes(path), charset));
            } else {
                Files.write(path, config.toString().getBytes(charset));
            }
        } catch (final IOException e) {
            System.err.println(e.getMessage());
        }
    }

}
