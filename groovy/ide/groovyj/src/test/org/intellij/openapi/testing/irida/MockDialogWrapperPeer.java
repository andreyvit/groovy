package org.intellij.openapi.testing.irida;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.JRootPane;

import com.intellij.openapi.ui.DialogWrapperPeer;

class MockDialogWrapperPeer extends DialogWrapperPeer {

    private JRootPane rootPane = new JRootPane();
    private Container contentPane = new Container();
    private String title;

    public void setUndecorated(boolean undecorated) {}

    public void addMouseListener(MouseListener listener) {}

    public void addMouseListener(MouseMotionListener listener) {}

    public void addKeyListener(KeyListener listener) {}

    public void toFront() {}

    public void toBack() {}

    protected void dispose() {}

    public Container getContentPane() {
        return contentPane;
    }

    public Window getOwner() {
        return null;
    }

    public Window getWindow() {
        return null;
    }

    public JRootPane getRootPane() {
        return rootPane;
    }

    public Dimension getSize() {
        return null;
    }

    public String getTitle() {
        return title;
    }

    public Dimension getPreferredSize() {
        return null;
    }

    public void setModal(boolean modal) {}

    public boolean isVisible() {
        return false;
    }

    public boolean isShowing() {
        return false;
    }

    public void setSize(int width, int height) {}

    public void setTitle(String title) {
        this.title = title;
    }

    public void isResizable() {}

    public void setResizable(boolean resizable) {}

    public Point getLocation() {
        return null;
    }

    public void setLocation(Point p) {}

    public void setLocation(int x, int y) {}

    public void show() {}

    public void setContentPane(JComponent content) {}

    public void centerInParent() {}

    public void validate() {}

    public void repaint() {}

    public void pack() {}
}
