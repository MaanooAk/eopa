package com.maanoo.eopa;

import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import com.maanoo.eopa.Canvas.Side;


public class Scene {

    public final JPopupMenu menu;
    public final Canvas canvas;
    public final MouseAdapter mouseListener;
    public final KeyAdapter keyListener;

    public Scene(JPopupMenu menu, Canvas canvas, MouseAdapter mouseListener, KeyAdapter keyListener) {
        this.menu = menu;
        this.canvas = canvas;
        this.mouseListener = mouseListener;
        this.keyListener = keyListener;
    }

    public JMenuItem getHeaderMenuItem() {
        return (JMenuItem) menu.getComponent(0);
    }

    // ===

    public static Scene createImage(EopaFrame frame, Path path) {

        final CanvasImage c = new CanvasImage(path);

        final String name = path.getFileName().toString();
        final JPopupMenu menu = createMenu(frame, path, c, name, true, true);

        final MouseAdapter mouseListener = new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                mouseMoved(e);

                if (e.getButton() == MouseEvent.BUTTON1) {
                    // TODO on drag, move image

                    if (c.getHighlight() == Side.L) {
                        frame.setImage(frame.getDirectory().next(-1));

                    } else if (c.getHighlight() == Side.R) {
                        frame.setImage(frame.getDirectory().next(+1));

                    } else if (c.getHighlight() == Side.D || c.getHighlight() == Side.U) {
                        frame.setImage(null);
                    }

                } else if (e.getButton() == MouseEvent.BUTTON2) {
                    menu.show(c, 0, 0);

                } else if (e.getButton() == MouseEvent.BUTTON3) {

                    final int colors[] = new int[4];
                    final BufferedImage buffer = new BufferedImage(c.getWidth(), c.getHeight(), c.getImage().getType());

                    c.paintComponent(buffer.getGraphics(), CanvasImage.PaintMode.Alpha);
                    buffer.getData().getPixel(e.getX(), e.getY(), colors);

                    if (colors[0] != c.getBackground().getRed()) {

                        c.paintComponent(buffer.getGraphics(), CanvasImage.PaintMode.Normal);
                        buffer.getData().getPixel(e.getX(), e.getY(), colors);

                        final String[] options = new String[] {
                                hex(colors[0]) + hex(colors[1]) + hex(colors[2]),
                        };
                        final int selected = JOptionPane.showOptionDialog(null,
                                String.format("R: %d\nG: %d\nB: %d", colors[0], colors[1], colors[2]), "Color",
                                JOptionPane.OK_OPTION,
                                JOptionPane.INFORMATION_MESSAGE, null, options, null);

                        if (selected != -1) {
                            ClipboardManager.set(options[selected]);
                        }

                    } else {
                        menu.show(c, 0, 0);
                    }

                }
            }

            private String hex(int num) {
                if (num < 0x10) return "0" + Integer.toString(num, 16);
                return Integer.toString(num, 16);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                final int edgeBorder = Config.Active.edgeBorder;

                final Side side;
                if (e.getY() < edgeBorder) side = Side.U;
                else if (e.getX() < edgeBorder) side = Side.L;
                else if (e.getY() > c.getHeight() - edgeBorder) side = Side.D;
                else if (e.getX() > c.getWidth() - edgeBorder) side = Side.R;
                else side = Side.None;

                c.setHighlight(side);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                c.setHighlight(Side.None);
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {

                if (e.getWheelRotation() > 0) {
                    c.scale = c.getScale() / 1.1f;
                } else {
                    c.scale = c.getScale() * 1.1f;
                }
                c.repaint();
            }
        };

        final KeyAdapter keyListener = new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);

                for (int i = 0; i < menu.getComponentCount(); i++) {
                    final Component comp = menu.getComponent(i);
                    if (comp == null || !(comp instanceof JMenuItem)) continue;
                    final JMenuItem item = (JMenuItem) comp;

                    final KeyStroke acce = item.getAccelerator();
                    if (acce == null) continue;

                    if (e.getKeyCode() == acce.getKeyCode()) {
//                        System.out.println(item.getText());
                        item.getActionListeners()[0].actionPerformed(null);
                        return;
                    }
                }

                if (e.getKeyCode() == KeyEvent.VK_F1) {
                    menu.show(c, 0, 0);

                } else if (e.getKeyCode() == KeyEvent.VK_F5) {
                    frame.setImage(path);

                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    frame.setImage(null);
//                    frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));

                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    frame.setImage(frame.getDirectory().next(+1));

                } else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_UP) {
                    frame.setImage(frame.getDirectory().next(-1));
                }

            }

        };

        return new Scene(menu, c, mouseListener, keyListener);
    }

    public static Scene createGrid(EopaFrame frame, Path path) {

        final CanvasGrid c = new CanvasGrid(frame.getDirectory());

        final String name = path.getParent().getFileName().toString();
        final JPopupMenu menu = createMenu(frame, path, c, name, false, true);

        final MouseAdapter mouseListener = new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                mouseMoved(e);

                if (e.getButton() == MouseEvent.BUTTON1) {

                    if (c.getHighlight() == Side.U || c.getHighlight() == Side.L) {
                        c.addOffset(-c.gridW);

                    } else if (c.getHighlight() == Side.D || c.getHighlight() == Side.R) {
                        c.addOffset(+c.gridW);

                    } else {

                        final int x = c.gridW * e.getX() / c.getWidth();
                        final int y = c.gridH * e.getY() / c.getHeight();
                        final int index = y * c.gridW + x + c.getOffset();

                        final List<Path> all = frame.getDirectory().getAll();
                        if (index >= 0 && index < all.size()) {
                            frame.setImage(all.get(index));
                        }
                    }

                } else if (e.getButton() == MouseEvent.BUTTON2) {
                    menu.show(c, 0, 0);

                } else if (e.getButton() == MouseEvent.BUTTON3) {

                    final int x = c.gridW * e.getX() / c.getWidth();
                    final int y = c.gridH * e.getY() / c.getHeight();
                    final int index = y * c.gridW + x + c.getOffset();

                    final List<Path> all = frame.getDirectory().getAll();
                    if (index >= 0 && index < all.size()) {
                        final Path path = all.get(index);

                        final JPopupMenu smenu = createMenu(null, path, null,
                                path.getFileName().toString(), true, false);
                        smenu.show(c, e.getX(), e.getY());
                    }

                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                final int edgeBorder = Config.Active.edgeBorder;

                final Side side;
                if (e.getY() < edgeBorder) side = Side.U;
                else if (e.getX() < edgeBorder) side = Side.L;
                else if (e.getY() > c.getHeight() - edgeBorder) side = Side.D;
                else if (e.getX() > c.getWidth() - edgeBorder) side = Side.R;
                else side = Side.None;

                c.setHighlight(side);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                c.setHighlight(Side.None);
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                final boolean pos = e.getWheelRotation() > 0;

                if (e.isShiftDown() || e.isAltDown() || e.isControlDown() || c.getHighlight() != Side.None) {

                    if (pos) {
                        c.addOffset(+c.gridW);
                    } else {
                        c.addOffset(-c.gridW);
                    }

                } else {
                    if (pos) {
                        c.scale = c.getScale() / 1.1f;
                    } else {
                        c.scale = c.getScale() * 1.1f;
                    }
                    c.repaint();
                }
            }
        };

        final KeyAdapter keyListener = new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);

                for (int i = 0; i < menu.getComponentCount(); i++) {
                    final Component comp = menu.getComponent(i);
                    if (comp == null || !(comp instanceof JMenuItem)) continue;
                    final JMenuItem item = (JMenuItem) comp;

                    final KeyStroke acce = item.getAccelerator();
                    if (acce == null) continue;

                    if (e.getKeyCode() == acce.getKeyCode()) {
//                        System.out.println(item.getText());
                        item.getActionListeners()[0].actionPerformed(null);
                        return;
                    }
                }

                if (e.getKeyCode() == KeyEvent.VK_F1) {
                    menu.show(c, 0, 0);

                } else if (e.getKeyCode() == KeyEvent.VK_F5) {
                    c.repaint();

                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));

                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    c.addOffset(+c.gridW);

                } else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_UP) {
                    c.addOffset(-c.gridW);
                }

            }

        };

        return new Scene(menu, c, mouseListener, keyListener);
    }

    private static JPopupMenu createMenu(EopaFrame frame, Path path, Canvas c, String name,
            boolean single, boolean viewing) {
        final JPopupMenu menu = new JPopupMenu();

        final JMenuItem nameMenuItem = new JMenuItem(name);
        menu.add(nameMenuItem).setEnabled(false);

        if (viewing) {
            menu.add(new JSeparator());
            createMenuItem(menu, "Scale to Fit", KeyEvent.VK_F, () -> {
                c.scale = -2;
            }, c);
            createMenuItem(menu, "Scale of 1", KeyEvent.VK_1, () -> {
                c.scale = 1;
            }, c);
            createMenuItem(menu, "Scale of 4", KeyEvent.VK_2, () -> {
                c.scale = 4;
            }, c);
            createMenuItem(menu, "Scale of 8", KeyEvent.VK_3, () -> {
                c.scale = 8;
            }, c);
            createMenuItem(menu, "Scale of 16", KeyEvent.VK_4, () -> {
                c.scale = 16;
            }, c);
        }

        menu.add(new JSeparator());
        if (single) {
            createMenuItem(menu, "Copy image", KeyEvent.VK_I, () -> {
                ClipboardManager.set(path);
            }, c);
            createMenuItem(menu, "Copy image filename", KeyEvent.VK_C, () -> {
                ClipboardManager.set(path.getFileName().toString());
            }, c);
            createMenuItem(menu, "Copy image path", KeyEvent.VK_P, () -> {
                ClipboardManager.set(path.toString());
            }, c);
            createMenuItem(menu, "Copy directory path", KeyEvent.VK_D, () -> {
                ClipboardManager.set(path.getParent().toString());
            }, c);
        } else {
            createMenuItem(menu, "Copy directory path", KeyEvent.VK_D, () -> {
                ClipboardManager.set(path.toString());
            }, c);
        }

        if (viewing) {

            if (!single) {
                menu.add(new JSeparator());
                final CanvasGrid cg = (CanvasGrid) c;

                createMenuItem(menu, "Horizontal Expand", KeyEvent.VK_U, () -> {
                    cg.changeGrid(1, 0);
                }, c);
                createMenuItem(menu, "Horizontal Shrink", KeyEvent.VK_Y, () -> {
                    cg.changeGrid(-1, 0);
                }, c);
                createMenuItem(menu, "Vertical Expand", KeyEvent.VK_6, () -> {
                    cg.changeGrid(0, -1);
                }, c);
                createMenuItem(menu, "Vertical Shrink", KeyEvent.VK_H, () -> {
                    cg.changeGrid(0, 1);
                }, c);
            }

            menu.add(new JSeparator());
            createMenuItem(menu, "Change Background", KeyEvent.VK_B, () -> {
                final Color newBackground = invertColor(c.getBackground());
                c.setBackground(Config.Active.background = newBackground);
                c.repaint();
            }, c);

//            createMenuItem(menu, "Change Grid Lines", KeyEvent.VK_G, () -> {
//                JOptionPane.showMessageDialog(frame, "TODO"); // TODO
//            }, c);

            createMenuItem(menu, "Lock Dynamic Attributes", KeyEvent.VK_L, () -> {
                c.locked = !c.locked;
            }, c);

            menu.add(new JSeparator());

            createMenuItem(menu, "Top Most Window", KeyEvent.VK_T, () -> {
                frame.setAlwaysOnTop(!frame.isAlwaysOnTop());
            }, c);

            if (single) {
                final CanvasImage ci = (CanvasImage) c;

                createMenuItem(menu, "Fit Window", KeyEvent.VK_S, () -> {
                    fitWindow(frame, ci, 0);
                }, c);
                createMenuItem(menu, "Fit Window Padded", KeyEvent.VK_W, () -> {
                    fitWindow(frame, ci, 2);
                }, c);
                createMenuItem(menu, "Scale of 1 and Fit Window", KeyEvent.VK_Q, () -> {
                    c.scale = 1;
                    c.currentScale = 1;
                    fitWindow(frame, ci, 0);
                }, c);
            }

            createMenuItem(menu, "Fullscreen", KeyEvent.VK_F11, () -> {
                final GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];
                device.setFullScreenWindow(device.getFullScreenWindow() == null ? frame : null);
            }, c);

        }
        return menu;
    }

    // ===

    private static void fitWindow(JFrame frame, final CanvasImage c, int padding) {

        frame.setExtendedState(Frame.NORMAL);

        final BufferedImage image = c.getImage();
        if (image == null) return;

        final int borderW = frame.getWidth() - c.getWidth();
        final int borderH = frame.getHeight() - c.getHeight();

        final float scale = c.currentScale;
        final float pad = padding == 0 ? 0 : Math.max(scale * padding, 10);
        frame.setSize((int) (image.getWidth() * scale + borderW + pad),
                (int) (image.getHeight() * scale + borderH + pad));
    }

    private static Color invertColor(Color color) {
        return color != Color.BLACK ? Color.BLACK : Color.WHITE;
    }

    private static void createMenuItem(JPopupMenu menu, String text, int shortcut, Runnable action, JPanel c) {

        final JMenuItem item = new JMenuItem(text);

        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
                if (c != null) {
                    c.invalidate();
                    c.repaint();
                }
            }
        });

        if (shortcut != 0) {
            item.setAccelerator(KeyStroke.getKeyStroke(shortcut, 0));
        }
        menu.add(item);
    }

}
