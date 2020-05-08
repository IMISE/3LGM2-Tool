package de.imise.tool3lgm.gui;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

/**
 * @author AXS (08.05.2020)
 */
public class MainFrameDesktopPane extends JDesktopPane implements ComponentListener {

    /** if the desktop size changes the frames will be resized too */
    private int desktopWidth = -1;
    private int desktopHeight = -1;

    /**
     *
     */
    public MainFrameDesktopPane() {
        addComponentListener(this); //resize desktop -> resize frames
    }

    //////////////////////////////////////////////
    // resize desktop -> resize internal frames //
    //////////////////////////////////////////////

    /**
     * @return all internal frames with the 0 position and max width of the desktop
     */
    private Iterable<JInternalFrame> getFramesWithMaxSize() {
        Set<JInternalFrame> framesWithMaxSize = new HashSet<>();
        for (JInternalFrame frame : getAllFrames()) {
            Point location = frame.getLocation();
            if (location.x == 0 && location.y == 0) {
                Rectangle frameBounds = frame.getBounds();
                if (frameBounds.width == desktopWidth && frameBounds.height == desktopHeight) {
                    framesWithMaxSize.add(frame);
                }
            }
        }
        return framesWithMaxSize;
    }

    /**
     * Resize all given frames to the maximum with of the desktop
     *
     * @param frames
     */
    private void setFramesToMaxSize(final Iterable<JInternalFrame> frames) {
        for (JInternalFrame frame : frames) {
            Rectangle frameBounds = frame.getBounds();
            frameBounds.width = desktopWidth;
            frameBounds.height = desktopHeight;
            frame.setBounds(frameBounds);
        }
    }

    @Override
    public void componentResized(final ComponentEvent e) {
        Object source = e.getSource();
        if (source == this) {
            if (desktopWidth != -1) {
                Iterable<JInternalFrame> framesWithMaxSize = getFramesWithMaxSize();
                desktopWidth = getWidth();
                desktopHeight = getHeight();
                setFramesToMaxSize(framesWithMaxSize);
            } else {
                desktopWidth = getWidth();
                desktopHeight = getHeight();
            }
        }
    }

    @Override
    public void componentMoved(final ComponentEvent e) {
        //do nothing
    }

    @Override
    public void componentShown(final ComponentEvent e) {
        //do nothing
    }

    @Override
    public void componentHidden(final ComponentEvent e) {
        //do nothing
    }

}
