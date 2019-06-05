package de.imise.tool3lgm.graphtools.metamodel.elements;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction.BACKWARD;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction.FORWARD;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelInstance;
import de.imise.tool3lgm.log.Log;

/**
 * Oberklasse aller Kanten, die ein Element einem anderen unterorden. Dieses Interface geht an mehreren Stellen davon aus,
 * dass die eigentliche Klasse, an der es hängt, auf jeden Fall eine Unterklasse von {@link Edge} ist. Es finden keine Checks statt,
 * um an dieser Stelle CastExceptions abzufangen.
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
     * Liefert <code>true</code>, wenn die übergebene Elementklasse mit der Unterlementklasse der übergebenen Edge zuweisungskompatibel ist.
     *
     * @param subordinationEdgeClass
     * @param meClass
     * @return
     */
    public static final boolean isSubClass(final Class<? extends SubordinationEdge> subordinationEdgeClass, final Class<? extends ModelElement> meClass) {
        return getSubClass(subordinationEdgeClass).isAssignableFrom(meClass);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Elementklasse mit der Elementklasse der übergebenen Edge zuweisungskompatibel ist, die
     * nicht die Teilelementklasse ist.
     *
     * @param subordinationEdgeClass
     * @param meClass
     * @return
     */
    public static final boolean isSuperClass(final Class<? extends SubordinationEdge> subordinationEdgeClass, final Class<? extends ModelElement> meClass) {
        return getSuperClass(subordinationEdgeClass).isAssignableFrom(meClass);
    }

    @Override
    public final void setNodes(final ModelElement superElement, final ModelElement subElement, final boolean registerInKnots) {
        ModelElement start = startElement;
        ModelElement end = endElement;
        super.setNodes(superElement, subElement, registerInKnots);
        if (isInCircle()) {
            subElement.removeEdge(this);
            superElement.removeEdge(this);
            super.setNodes(start, end, registerInKnots);
        }
    }

    @Override
    public void setNodesAndInsert(final ModelElement startElement, final int startElementEdgeIndex, final ModelElement endElement, final int endElementEdgeIndex) {
        ModelElement oldStartElement = startElement;
        ModelElement oldEndElement = endElement;
        int oldStartElementEdgeIndex = oldStartElement == null ? 0 : oldStartElement.removeEdge(this);
        int oldEndElementEdgeIndex = oldEndElement == null ? 0 : oldEndElement.removeEdge(this);
        this.startElement = startElement;
        this.endElement = endElement;
        startElement.insertEdge(this, startElementEdgeIndex);
        endElement.insertEdge(this, endElementEdgeIndex);
        if (isInCircle()) {
            startElement.removeEdge(this);
            endElement.removeEdge(this);
            super.setNodesAndInsert(oldStartElement, oldStartElementEdgeIndex, oldEndElement, oldEndElementEdgeIndex);
        }
    }

    @Override
    public final void setStartAndInsert(final ModelElement startElement) {
        ModelElement oldStartElement = startElement;
        oldStartElement.removeEdge(this);
        this.startElement = startElement;
        startElement.addEdge(this);
        if (isInCircle()) {
            startElement.removeEdge(this);
            super.setStartAndInsert(oldStartElement);
        }
    }

    @Override
    public final void setEndAndInsert(final ModelElement endElement) {
        ModelElement oldEndElement = endElement;
        oldEndElement.removeEdge(this);
        this.endElement = endElement;
        endElement.addEdge(this);
        if (isInCircle()) {
            endElement.removeEdge(this);
            super.setEndAndInsert(oldEndElement);
        }
    }

    /**
     * @return
     */
    public final boolean isInCircle() {
        ModelElement k1 = getSuperElement();
        ModelElement k2 = getSubElement();
        if (k1 != null && k2 != null) {
            boolean retVal = k2.isSuperElementOf(k1, getClass());
            if (retVal) {
                ElementsNameBuilder elementsNameBuilder = getMetaModel().getElementsNameBuilder();
                Log.show(Log.INFO, getResString("part_of_circle_error") + "\n" + elementsNameBuilder.getDisplayablePluralName(ModelElement.class) + ":\n" + elementsNameBuilder.getDisplayableName(k1) + ": " + k1.getName() + "\n"
                        + elementsNameBuilder.getDisplayableName(k2) + ": " + k2.getName());
            }
            return retVal;
        }
        return false;
    }

    /**
     * Liefert true, wenn ein über diese Kantenart verbundenes Oberelement gleichzeitig Unterelement eines anderen Elementes sein kann.
     *
     * @return
     */
    public final boolean isRecursive() {
        return MetaModelInstance.isRecursiveSubordination(getClass());
    }

}
