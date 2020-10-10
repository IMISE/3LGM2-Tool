package de.imise.tool3lgm.graphtools.consistency.error;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.metapaths.ElementaryMetaPath;

/**
 * @author AXS (Sa, 21.03.2020, 8:14' (Corona time...))
 */
public abstract class AbstractElementaryPathError extends AbstractPathError {

    /**
     * @param me
     * @param elementaryMetaPath
     * @param cardValue
     */
    public AbstractElementaryPathError(final ModelElement me, final ElementaryMetaPath elementaryMetaPath) {
        super(me, elementaryMetaPath, null); //these errors get their solutions alsways directly from the checker or from the ErrorSolutionLibrary
    }

    /**
     * @return
     */
    private Class<? extends Edge> getEdgeClass() {
        ElementaryMetaPath elementaryMetaPath = getMetaPath();
        return elementaryMetaPath.getEdgeClass();
    }

    /**
     * @return
     */
    @Override
    public ElementaryMetaPath getMetaPath() {
        return (ElementaryMetaPath) metaPath;
    }

    @Override
    public String getErrorFieldString() {
        Class<? extends Edge> edgeClass = getEdgeClass();
        ElementsNameBuilder elementsNameBuilder = getElementsNameBuilder();
        return elementsNameBuilder.getFullForwardMetaAssociationName(edgeClass);
    }

}