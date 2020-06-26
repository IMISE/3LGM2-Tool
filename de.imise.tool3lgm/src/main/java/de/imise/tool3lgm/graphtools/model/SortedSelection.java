package de.imise.tool3lgm.graphtools.model;

import java.util.ArrayList;
import java.util.Collection;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;

/**
 * @author AXS (25.06.2020)
 */
public class SortedSelection extends ArrayList<ElementContainer> {

    /**
     *
     */
    public SortedSelection() {
    }

    /**
     * @param initialCapacity
     */
    public SortedSelection(final int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * @param c
     */
    public SortedSelection(final Collection<ElementContainer> c) {
        super(c);
    }

    /**
     * @param me
     * @return
     */
    public boolean contains(final ModelElement me) {
        for (ElementContainer ec : this) {
            ModelElement element = ec.getElement();
            if (element.equals(me)) {
                return true;
            }
        }
        return false;
    }

}
