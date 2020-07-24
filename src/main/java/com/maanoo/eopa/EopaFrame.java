package com.maanoo.eopa;

import java.nio.file.Path;

import javax.swing.JFrame;


public abstract class EopaFrame extends JFrame {

    public abstract void setImage(Path newPath);

    public abstract ImageDirectory getDirectory();

}
