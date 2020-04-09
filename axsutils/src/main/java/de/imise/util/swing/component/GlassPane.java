package de.imise.util.swing.component;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

/**
 * Ein GlassPane, das man auf jedes RotPane legen kann.
 *
 * @author AXS (12.12.2019)
 */
public class GlassPane extends JComponent implements MouseListener, MouseMotionListener {

    private final JRootPane rootPane;

    /**
     * @param frame
     * @param menuBar
     */
    private GlassPane(final JRootPane rootPane) {
        this.rootPane = rootPane;
        setOpaque(false);
        addMouseListener(this);
        addMouseMotionListener(this);

    }

    /**
     * @param frame
     * @param menuBar
     */
    public static void addGlassPane(final JRootPane rootPane) {
        GlassPane glassPane = new GlassPane(rootPane);
        rootPane.setGlassPane(glassPane);
        glassPane.setVisible(true);
    }

    @Override
    protected void paintComponent(final Graphics g) {
    }

    /**
     * @param e
     */
    private void dispatchEvent(final MouseEvent e) {
        Point glassPanePoint = e.getPoint();
        Container container = rootPane.getContentPane();
        Point containerPoint = SwingUtilities.convertPoint(this, glassPanePoint, container);

        // The mouse event is probably over the content pane.
        // Find out exactly which component it's over.
        Component component = SwingUtilities.getDeepestComponentAt(container, containerPoint.x, containerPoint.y);

        if (component != null) {
            // Forward events to component below
            Point componentPoint = SwingUtilities.convertPoint(this, glassPanePoint, component);
            component.dispatchEvent(new MouseEvent(component, e.getID(), e.getWhen(), e.getModifiers(), componentPoint.x, componentPoint.y, e.getClickCount(), e.isPopupTrigger()));
        }
    }

    @Override
    public void mousePressed(final MouseEvent e) {
        dispatchEvent(e);
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
        dispatchEvent(e);
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        dispatchEvent(e);
    }

    @Override
    public void mouseEntered(final MouseEvent e) {
        dispatchEvent(e);
    }

    @Override
    public void mouseExited(final MouseEvent e) {
        dispatchEvent(e);
    }

    @Override
    public void mouseDragged(final MouseEvent e) {
        dispatchEvent(e);
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
        dispatchEvent(e);
    }

}
