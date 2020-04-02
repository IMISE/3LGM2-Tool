package de.imise.tool3lgm.graphtools.consistency.error;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.path.metapaths.ElementaryMetaPath;

/**
 * @author AXS (Sa, 21.03.2020, 8:14' (Corona time...))
 */
public abstract class AbstractElementaryPathError extends AbstractPathError {

    /**
     * @param me
     * @param elementaryMetaPath
     * @param cardValue
     * @param gdcoll
     */
    public AbstractElementaryPathError(final ModelElement me, final ElementaryMetaPath elementaryMetaPath, final GDCollection gdcoll) {
        super(me, elementaryMetaPath, gdcoll);
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
        return (ElementaryMetaPath) errorField;
    }

    @Override
    public String getErrorFieldString() {
        Class<? extends Edge> edgeClass = getEdgeClass();
        MetaModel metaModel = gdcoll.getMetaModel();
        ElementsNameBuilder elementsNameBuilder = metaModel.getElementsNameBuilder();
        return elementsNameBuilder.getFullForwardMetaAssociationName(edgeClass);
    }

}