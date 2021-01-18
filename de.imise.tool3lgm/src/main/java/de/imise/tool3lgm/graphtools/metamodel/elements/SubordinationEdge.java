package de.imise.tool3lgm.graphtools.metamodel.elements;

import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction.BACKWARD;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction.FORWARD;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.log.Log;

/**
 * Oberklasse aller Kanten, die ein Element einem anderen unterorden. Dieses
 * Interface geht an mehreren Stellen davon aus, dass die eigentliche Klasse, an
 * der es hängt, auf jeden Fall eine Unterklasse von {@link Edge} ist. Es finden
 * keine Checks statt, um an dieser Stelle CastExceptions abzufangen.
 *
 * @author AXS (10.04.2018)
 */
public abstract class SubordinationEdge extends Edge {

    /** Richtung, in der die Kante vom Oberelement auf das Unterelement zeigt */
    public static final Direction SUPER_TO_SUB_DIRECTION = FORWARD;

    /** Richtung, in der die Kante vom Unterelement auf das Oberelement zeigt */
    public static final Direction SUB_TO_SUPER_DIRECTION = BACKWARD;

    public ModelElement getSubElement() {
        return endElement;
    }

    public ModelElement getSuperElement() {
        return startElement;
    }

    protected void setSubElement(final ModelElement sub) {
        endElement = sub;
    }

    protected void setSuperElement(final ModelElement sup) {
        startElement = sup;
    }

    /**
     * Gibt die untergeordnete Klasse der Beziehung zurück
     *
     * @param subordinationEdgeClass
     * @return
     */
    public static final Class<? extends ModelElement> getSubClass(final Class<? extends SubordinationEdge> subordinationEdgeClass) {
        return getEndClass(subordinationEdgeClass);
    }

    /**
     * Gibt die übergeordnete Klasse der Beziehung zurück
     *
     * @param subordinationEdgeClass
     * @return
     */
    public static final Class<? extends ModelElement> getSuperClass(final Class<? extends SubordinationEdge> subordinationEdgeClass) {
        return getStartClass(subordinationEdgeClass);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Elementklasse mit der
     * Unterlementklasse der übergebenen Edge zuweisungskompatibel ist.
     *
     * @param subordinationEdgeClass
     * @param meClass
     * @return
     */
    public static final boolean isSubClass(final Class<? extends SubordinationEdge> subordinationEdgeClass, final Class<? extends ModelElement> meClass) {
        return getSubClass(subordinationEdgeClass).isAssignableFrom(meClass);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Elementklasse mit der
     * Elementklasse der übergebenen Edge zuweisungskompatibel ist, die nicht
     * die Teilelementklasse ist.
     *
     * @param subordinationEdgeClass
     * @param meClass
     * @return
     */
    public static final boolean isSuperClass(final Class<? extends SubordinationEdge> subordinationEdgeClass, final Class<? extends ModelElement> meClass) {
        return getSuperClass(subordinationEdgeClass).isAssignableFrom(meClass);
    }

    @Override
    protected boolean checkValidity() {
        if (!super.checkValidity()) {
            return false;
        }
        Class<? extends SubordinationEdge> subordinationEdgeClass = getClass();
        boolean endElementIsSuperOfStartElement = endElement.isSuperElementOf(startElement, subordinationEdgeClass);
        if (endElementIsSuperOfStartElement) {
            MetaModel metaModel = getMetaModel();
            ElementsNameBuilder elementsNameBuilder = metaModel.getElementsNameBuilder();
            String edgeClassPluralName = elementsNameBuilder.getDisplayablePluralName(subordinationEdgeClass);
            String message = getResString("part_of_circle_error");
            String elements = elementsNameBuilder.getDisplayablePluralName(ModelElement.class);
            String elementType1 = elementsNameBuilder.getDisplayableName(startElement);
            String elementType2 = elementsNameBuilder.getDisplayableName(endElement);
            Log.show(Log.INFO, edgeClassPluralName + " " + message + "\n" + elements + ":\n" + elementType1 + ": " + startElement.getName() + "\n" + elementType2 + ": " + endElement.getName());
        }
        return !endElementIsSuperOfStartElement;
    }

    /**
     * Liefert true, wenn ein über diese Kantenart verbundenes Oberelement
     * gleichzeitig solche Unterelemente haben kann.
     *
     * @return
     */
    public final boolean isRecursive() {
        MetaModel metaModel = getMetaModel();
        Class<? extends SubordinationEdge> edgeClass = getClass();
        return metaModel.isRecursiveSubordination(edgeClass);
    }

}
