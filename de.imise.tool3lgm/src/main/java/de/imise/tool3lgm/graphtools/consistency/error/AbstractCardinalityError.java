package de.imise.tool3lgm.graphtools.consistency.error;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelInstance;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.path.meta.ElementaryMetaPath;

/**
 * @author AXS
 * @created 13.09.2008
 */
public abstract class AbstractCardinalityError extends AbstractError {

    /**
     * Wert der Kardinalität, die über oder unterschritten wurde
     */
    protected int cardValue;

    /**
     * @param me
     * @param elementaryMetaPath
     * @param cardValue
     * @param gdcoll
     */
    public AbstractCardinalityError(final ModelElement me, final ElementaryMetaPath elementaryMetaPath, final GDCollection gdcoll, final int cardValue) {
        super(me, elementaryMetaPath, gdcoll);
        this.cardValue = cardValue;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        AbstractCardinalityError ce = (AbstractCardinalityError) obj;
        return cardValue == ce.cardValue;
    }

    @SuppressWarnings("unchecked")
    public Class<? extends Edge> getEdgeClass() {
        if (errorField instanceof ElementaryMetaPath) {
            return ((ElementaryMetaPath) errorField).getEdgeClass();
        }
        return (Class<? extends Edge>) errorField;
    }

    @Override
    public String getErrorFieldString() {
        Class<? extends Edge> edgeClass = getEdgeClass();
        MetaModelInstance metaModel = gdcoll.getMetaModel();
        ElementsNameBuilder elementsNameBuilder = metaModel.getElementsNameBuilder();
        return edgeClass != null ? elementsNameBuilder.getFullForwardMetaAssociationName(edgeClass) : "";
    }

    /**
     * @return
     */
    public ElementaryMetaPath getElementaryMetaPath() {
        return (ElementaryMetaPath) errorField;
    }

}