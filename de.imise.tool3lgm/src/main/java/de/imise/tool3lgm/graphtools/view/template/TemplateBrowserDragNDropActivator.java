package de.imise.tool3lgm.graphtools.view.template;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;

import javax.swing.tree.TreePath;

import org.apache.jena.ext.com.google.common.collect.Lists;

import de.imise.util.Sys;

/**
 * @author AXS (05.05.2020)
 */
public class TemplateBrowserDragNDropActivator {

    /**
     *
     */
    private final TemplateBrowserTree templateBrowserTree;

    /**
     * @param templateBrowserTree
     */
    private TemplateBrowserDragNDropActivator(final TemplateBrowserTree templateBrowserTree) {
        this.templateBrowserTree = templateBrowserTree;
        changeMouseListenerOrder();
    }

    private void changeMouseListenerOrder() {
        MouseListener[] mouseListeners = templateBrowserTree.getMouseListeners();
        for (MouseListener mouseListener : mouseListeners) {
            templateBrowserTree.removeMouseListener(mouseListener);
        }
        templateBrowserTree.addMouseListener(new TemplateBrowserTreeMouseAdapter());
        for (MouseListener mouseListener : mouseListeners) {
            templateBrowserTree.addMouseListener(mouseListener);
        }
        Sys.err1(Lists.newArrayList(templateBrowserTree.getMouseListeners()));
    }

    /**
     * @param templateBrowserTree
     */
    public static final void add(final TemplateBrowserTree templateBrowserTree) {
        new TemplateBrowserDragNDropActivator(templateBrowserTree);
    }

    private class TemplateBrowserTreeMouseAdapter extends MouseAdapter {
        @Override
        public void mouseClicked(final MouseEvent e) {
            //Sys.err1("mouseClicked " + e);
            int x = e.getX();
            int y = e.getY();
            TreePath treePathForLocation = templateBrowserTree.getPathForLocation(x, y);
            Sys.err1(treePathForLocation);

            super.mouseClicked(e);
        }

        @Override
        public void mousePressed(final MouseEvent e) {
            //Sys.err1("mousePressed " + e);
            int x = e.getX();
            int y = e.getY();
            TreePath treePathForLocation = templateBrowserTree.getPathForLocation(x, y);
            boolean drag = false;
            if (treePathForLocation != null) {
                int pathCount = treePathForLocation.getPathCount();
                if (pathCount > 3) {
                    drag = true;
                }
            }
            templateBrowserTree.setDragEnabled(drag);
            super.mousePressed(e);
        }

        @Override
        public void mouseReleased(final MouseEvent e) {
            //Sys.err1("mouseReleased " + e);
            super.mouseReleased(e);
        }

        @Override
        public void mouseEntered(final MouseEvent e) {
            //Sys.err1("mouseEntered " + e);
            super.mouseEntered(e);
        }

        @Override
        public void mouseExited(final MouseEvent e) {
            //Sys.err1("mouseExited " + e);
            super.mouseExited(e);
        }

        @Override
        public void mouseWheelMoved(final MouseWheelEvent e) {
            //Sys.err1("mouseMoved " + e);
            super.mouseWheelMoved(e);
        }

        @Override
        public void mouseDragged(final MouseEvent e) {
            //Sys.err1("mouseDragged " + e);
            super.mouseDragged(e);
        }

        @Override
        public void mouseMoved(final MouseEvent e) {
            //Sys.err1("mouseMoved " + e);
            super.mouseMoved(e);
        }

    }

}
