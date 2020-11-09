package de.imise.util;

import java.awt.event.MouseEvent;

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
     * @param target
     */
    public default void registerComponent(final JComponent target) {
        ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
        toolTipManager.registerComponent(target);
    }

    /**
     * @param target
     */
    public default void unregisterComponent(final JComponent target) {
        ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
        toolTipManager.unregisterComponent(target);
    }

}
