package de.imise.util.swing.component;

import java.awt.Component;
import java.awt.Container;

/**
 * Opposite to the {@link ParentComponentFinder}
 *
 * @author AXS (09.06.2021)
 */
public class ChildComponentFinder {

    /**
     * Recursive seach for a child component that is assignable to the given
     * class or interface.
     *
     * @param <T>
     * @param container
     * @param childType
     * @return the first child compoent with the <code>childType</code>
     */
    public static final <T> T getFirstChild(final Container container, final Class<T> childType) {
        for (Component child : container.getComponents()) {
            Class<? extends Component> childClass = child.getClass();
            if (childType.isAssignableFrom(childClass)) {
                return (T) child;
            }
            if (child instanceof Container) {
                T firstChild = getFirstChild((Container) child, childType);
                if (firstChild != null) {
                    return firstChild;
                }
            }
        }
        return null;
    }

}
