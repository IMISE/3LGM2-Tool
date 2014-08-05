package de.imise.tool3lgm.graphtools.consistency.error;

import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;

/**
 * @author AXS
 * @created 13.09.2008
 */
public abstract class CardinalityError extends Error {

    /**
     * Element, das zu anderen Elementen zuviele Verbindungen hat
     */
    protected ModelElement me;

    /**
     * Klasse der Verbindungen, deren Instanzanzahl für das Modellelement zu hoch oder zu niedrig
     * ist.
     */
    protected Class<? extends Kante> edgeClass;

    /**
     * Wert der Cardinalität, die über oder unterschritten wurde
     */
    protected int cardValue;

    /**
     * Das Modell in dem der Fehler auftrat
     */
    protected GDCollection gdcoll;

    /**
     * @param me
     * @param edgeClass
     * @param cardValue
     * @param gdcoll
     */
    public CardinalityError(final ModelElement me, final Class<? extends Kante> edgeClass, final int cardValue, final GDCollection gdcoll) {
        super();
        this.me = me;
        this.edgeClass = edgeClass;
        this.cardValue = cardValue;
        this.gdcoll = gdcoll;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Throwable#getMessage()
     */
    @Override
    public String getMessage() {
        return getMessageBuilder().toString();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (super.equals(obj)) {
            return true;
        }
        if (!(obj.getClass() == getClass())) {
            return false;
        }
        CardinalityError ce = (CardinalityError) obj;
        if (me != ce.me || cardValue != ce.cardValue || edgeClass != ce.edgeClass) {
            return false;
        }
        return true;
    }

    /**
     * Liefert den StringBuilder der die toString()-Message zusammenbaut. Unterklassen können so
     * noch etwas hinzufügen.
     * 
     * @return
     */
    protected StringBuilder getMessageBuilder() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append(" (");
        sb.append(cardValue);
        sb.append(") ");
        sb.append(edgeClass.getSimpleName());
        sb.append(" <");
        sb.append(Kante.isStartClass(edgeClass, me.getClass()) ? ModelConstants.getFullForwardMetaAssociationName(edgeClass) : ModelConstants.getFullBackwardMetaAssociationName(edgeClass));
        sb.append(">\n\t");
        sb.append(me.getClearName());
        return sb;
    }

    /**
     * @return the me
     */
    public ModelElement getModelElement() {
        return me;
    }

    /**
     * @return the edgeClass
     */
    public Class<? extends Kante> getEdgeClass() {
        return edgeClass;
    }

    /**
     * @return the cardValue
     */
    public int getCardValue() {
        return cardValue;
    }

    /**
     * @return the gdcoll
     */
    public GDCollection getGdcoll() {
        return gdcoll;
    }

}