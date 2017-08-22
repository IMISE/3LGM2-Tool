package de.imise.tool3lgm.graphtools.elements;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.log.Log;

/**
 * Die PartOf-Beziehung ist eine Bezihung zwischen zwei Elementen, die von der selben Elementklasse sind. Start der Edge ist immer das Kindelement,
 * das Ende der Edge ist immer das Elternelement. Dir Richtung der Edge ist immer forward.
 */
public abstract class PartOfBeziehung extends Edge {

    public PartOfBeziehung() {
        super();
    }

    public PartOfBeziehung(final ModelElement part, final ModelElement parent) {
        super(part, parent);
    }

    public PartOfBeziehung(final ModelElement part, final ModelElement parent, final boolean registerInKnots) {
        super(part, parent, registerInKnots);
    }

    /**
     * Gibt das Element zurück, welches durch diese Edge Teil des anderen Elementes ist.
     *
     * @return Partelement der Edge
     */
    public final ModelElement getPart() {
        return getStart();
    }

    /**
     * Gibt das Element zurück, welches durch diese Edge das Oberelement des anderen Elementes ist.
     *
     * @return Parentelement der Edge
     */
    public final ModelElement getParent() {
        return getEnd();
    }

    /**
     * Gibt die Teilelementklasse der Teil-Von-Beziehung zurück
     *
     * @param poClass
     * @return
     */
    public static final Class<? extends ModelElement> getPartClass(final Class<? extends PartOfBeziehung> poClass) {
        return getStartClass(poClass);
    }

    /**
     * Gibt die Elementklasse der Teil-Von-Beziehung zurück, die nicht die Teilelementklasse ist
     *
     * @param poClass
     * @return
     */
    public static final Class<? extends ModelElement> getParentClass(final Class<? extends PartOfBeziehung> poClass) {
        return getEndClass(poClass);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Elementklasse mit der Teilelementklasse der übergebenen Edge zuweisungskompatibel ist.
     *
     * @param poClass
     * @param meClass
     * @return
     */
    public static final boolean isPartClass(final Class<? extends PartOfBeziehung> poClass, final Class<? extends ModelElement> meClass) {
        return getPartClass(poClass).isAssignableFrom(meClass);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Elementklasse mit der Elementklasse der übergebenen Edge zuweisungskompatibel ist, ide nicht
     * die Teilelementklasse ist.
     *
     * @param poClass
     * @param meClass
     * @return
     */
    public static final boolean isParentClass(final Class<? extends PartOfBeziehung> poClass, final Class<? extends ModelElement> meClass) {
        return getParentClass(poClass).isAssignableFrom(meClass);
    }

    /**
     * Richtung, in der die Edge vom Part auf den Parent zeigt.
     */
    public static final int PART_TO_PARENT_DIRECTION = FORWARD;

    /**
     * Richtung, in der die Edge vom Prent auf den Part zeigt.
     */
    public static final int PARENT_TO_PART_DIRECTION = BACKWARD;

    @Override
    public final int getDirection() {
        return PART_TO_PARENT_DIRECTION;
    }

    @Override
    public final void setDirection(final int _state) {
        ModelElement start = k1;
        ModelElement end = k2;
        switch (_state) {
        case DOUBLE:
            break;
        case FORWARD:
            super.setDirection(FORWARD);
            break;
        case BACKWARD:
            ModelElement temp = k1;
            k1 = k2;
            k2 = temp;
            super.setDirection(FORWARD);
            break;
        }
        if (isInCircle()) {
            k1 = start;
            k2 = end;
        }
        return;
    }

    @Override
    public final void setKnots(final ModelElement part, final ModelElement parent, final boolean registerInKnots) {
        ModelElement start = k1;
        ModelElement end = k2;
        super.setKnots(part, parent, registerInKnots);
        if (isInCircle()) {
            part.removeEdge(this);
            parent.removeEdge(this);
            super.setKnots(start, end, registerInKnots);
        }
    }

    @Override
    public final void setKnotsAndInsert(final ModelElement part, final int partEdgePos, final ModelElement parent, final int parentEdgePos) {
        ModelElement start = k1;
        ModelElement end = k2;
        k1 = part;
        k2 = parent;
        part.insertEdge(this, partEdgePos);
        parent.insertEdge(this, parentEdgePos);
        if (isInCircle()) {
            part.removeEdge(this);
            parent.removeEdge(this);
            super.setKnotsAndInsert(start, partEdgePos, end, parentEdgePos);
        }
    }

    @Override
    public final void setStartAndInsert(final ModelElement part) {
        ModelElement start = k1;
        k1 = part;
        k1.addEdge(this);
        if (isInCircle()) {
            part.removeEdge(this);
            super.setStartAndInsert(start);
        }
    }

    @Override
    public final void setEndAndInsert(final ModelElement parent) {
        ModelElement end = k2;

        k2 = parent;
        k2.addEdge(this);

        if (isInCircle()) {
            parent.removeEdge(this);
            super.setEndAndInsert(end);
        }
    }

    /**
     * @return
     */
    public final boolean isInCircle() {
        if (k1 != null && k2 != null) {
            boolean retVal = k2.isPartOf(k1);
            if (retVal) {
                Log.show(Log.INFO, Tool3lgmConstants.getErrString("kreis") + "\n" + Tool3lgmConstants.getResString("ModelElement_p") + ":\n" + ModelConstants.getDisplayableName(k1) + ": " + k1.getName() + "\n" + ModelConstants.getDisplayableName(k2) + ": "
                        + k2.getName());
            }
            return retVal;
        }
        return false;
    }

}
