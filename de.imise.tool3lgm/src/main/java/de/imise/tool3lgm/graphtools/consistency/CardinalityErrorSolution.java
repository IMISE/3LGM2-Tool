package de.imise.tool3lgm.graphtools.consistency;

import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.path.metapaths.MetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPath;

public class CardinalityErrorSolution extends ErrorSolution {

    public final Class<? extends Edge> edgeClass;

    public CardinalityErrorSolution(final Class<? extends Edge> edgeClass, final MetaPath pathToPropertyDialogElement, final SimpleMetaPath panelMetaPath) {
        super(pathToPropertyDialogElement, panelMetaPath);
        this.edgeClass = edgeClass;
    }

}
