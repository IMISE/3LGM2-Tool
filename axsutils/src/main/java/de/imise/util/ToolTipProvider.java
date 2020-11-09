package de.imise.util;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.TreePath;

/**
 * The main function provides a string for a given object. The string can be
 * used as tooltip. The other function prov
 *
 * @author AXS (09.06.2020)
 */
public interface ToolTipProvider {

    /**
     * @param e
     * @return
     */
    public default String getToolTip(final MouseEvent event) {
        Object source = event.getSource();
        if (source instanceof JTree) {
            JTree tree = (JTree) source;
            int x = event.getX();
            int y = event.getY();
            TreePath pathForLocation = tree.getPathForLocation(x, y);
            if (pathForLocation != null) {
                Object lastPathComponent = pathForLocation.getLastPathComponent();
                return getToolTip(lastPathComponent);
            }
        }
        return null;
    }

    /**
     * Returns a string that can be used as tooltip for the given object.
     *
     * @param o
     * @return
     */
    public String getToolTip(Object o);

    /**
     * This call registers the component as a toolTip component. Registering
     * just means that the <code>ToolTipManager.sharedInstance()</code> adds
     * special {@link MouseListener} to the component. It does not store an
     * instance of the component, so you do not need to unregister the component
     * when closing or removing it.
     *
     * @param target
     */
    public default void addToolTipMouseListeners(final JComponent target) {
        ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
        //this function only adds mouse listeners - nothing else
        toolTipManager.registerComponent(target);
    }

    /**
     * Removes the special MouseListener, which displays the ToolTips, from the
     * component.
     *
     * @param target
     */
    public default void removeToolTipMouseListeners(final JComponent target) {
        ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
        toolTipManager.unregisterComponent(target);
    }

}
