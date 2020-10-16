package de.imise.tool3lgm.graphtools.consistency.error.solution;

import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.path.metapaths.MetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPath;

/**
 * @author AXS (12.10.2020)
 */
public class CardinalityErrorSolution extends ErrorSolution {

    /**
     *
     */
    public final Class<? extends Edge> edgeClass;

    /**
     * @param edgeClass
     * @param pathToPropertyDialogElement
     * @param panelMetaPath
     */
    public CardinalityErrorSolution(final Class<? extends Edge> edgeClass, final MetaPath pathToPropertyDialogElement, final SimpleMetaPath panelMetaPath) {
        super(pathToPropertyDialogElement, panelMetaPath);
        this.edgeClass = edgeClass;
    }

}
