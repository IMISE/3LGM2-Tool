package de.imise.util.swing.component;

import java.awt.Component;

import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 * @author AXS (26.05.2020)
 */
public class ParentComponentFinder {

    /**
     * Returns the parent with the given type
     *
     * @param <T>
     * @param component
     * @param parentType
     * @return
     */
    @SuppressWarnings("unchecked") //it's checked!
    public static final <T> T getParent(final Component component, final Class<T> parentType) {
        Component parent = component.getParent();
        while (parent != null) {
            Class<? extends Component> parentClass = parent.getClass();
            if (parentType.isAssignableFrom(parentClass)) {
                return (T) parent;
            }
            parent = parent.getParent();
        }
        return null;
    }

    /**
     * @param <T>
     * @param component
     * @param parentType
     * @return
     */
    public static final <T> boolean hasParent(final Component component, final Class<T> parentType) {
        T parent = getParent(component, parentType);
        return parent != null;
    }

    /**
     * @param comp
     * @return the {@link JDialog} or {@link JFrame} that contains the given
     *         component
     */
    public static final Component getFrameOrDialog(final Component comp) {
        Component parent = comp.getParent();
        while (parent != null) {
            if (parent instanceof JDialog || parent instanceof JFrame) {
                return parent;
            }
            parent = parent.getParent();
        }
        return null;
    }

    /**
     * @param comp
     * @return the {@link JDialog} or {@link JFrame} that contains the given
     *         component. Otherwise if the given component is already a Frame or
     *         Dialog return itself.
     */
    public static final Component getFrameOrDialog(final Object comp) {
        if (!(comp instanceof Component)) {
            return null;
        }
        Component parent = (Component) comp;
        while (parent != null) {
            if (parent instanceof JDialog || parent instanceof JFrame) {
                return parent;
            }
            parent = parent.getParent();
        }
        return null;
    }
}
