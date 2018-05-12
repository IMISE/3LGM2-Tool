package de.imise.tool3lgm.graphtools.model;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.util.Alphabetical;

/**
 * @author AXS (09.05.2018)
 */
public class GraphDocumentElementsContext {

    private final Multimap<Class<? extends ModelElement>, ElementContainer> myTreeMultimap = TreeMultimap.create(Alphabetical.getLocalizedComparator(), Alphabetical.getLocalizedComparator());

    public void addContainer(final ElementContainer ec) {
    }

}
