package de.imise.tool3lgm.graphtools.metamodel.elements;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.getDisplayableName;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.getDisplayablePluralName;

import de.imise.tool3lgm.log.Log;

public abstract class HierarchyEdge extends Edge {

    public final ModelElement getSubElement() {
        return getStart();
    }

    public final ModelElement getSuperElement() {
        return getEnd();
    }

    /**
     * Gibt die Untergeordnete Klasse der Beziehung zurück
     *
     * @param hierarchyEdgeClass
     * @return
     */
    public static final Class<? extends ModelElement> getSubClass(final Class<? extends HierarchyEdge> hierarchyEdgeClass) {
        return getStartClass(hierarchyEdgeClass);
    }

    /**
     * Gibt die Übergeordnete Klasse der Beziehung zurück
     *
     * @param hierarchyEdgeClass
     * @return
     */
    public static final Class<? extends ModelElement> getSuperClass(final Class<? extends HierarchyEdge> hierarchyEdgeClass) {
        return getEndClass(hierarchyEdgeClass);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Elementklasse mit der Unterlementklasse der übergebenen Edge zuweisungskompatibel ist.
     *
     * @param hierarchyEdgeClass
     * @param meClass
     * @return
     */
    public static final boolean isSubClass(final Class<? extends HierarchyEdge> hierarchyEdgeClass, final Class<? extends ModelElement> meClass) {
        return getSubClass(hierarchyEdgeClass).isAssignableFrom(meClass);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Elementklasse mit der Elementklasse der übergebenen Edge zuweisungskompatibel ist, die
     * nicht die Teilelementklasse ist.
     *
     * @param hierarchyEdgeClass
     * @param meClass
     * @return
     */
    public static final boolean isSuperClass(final Class<? extends HierarchyEdge> hierarchyEdgeClass, final Class<? extends ModelElement> meClass) {
        return getSuperClass(hierarchyEdgeClass).isAssignableFrom(meClass);
    }

    /**
     * Richtung, in der die Edge vom Unterelement auf das Oberelement zeigt.
     */
    public static final int SUB_TO_SUPER_DIRECTION = FORWARD;

    /**
     * Richtung, in der die Edge vom Oberelement auf das Unterelement zeigt.
     */
    public static final int SUPER_TO_SUB_DIRECTION = BACKWARD;

    @Override
    public final int getDirection() {
        return SUB_TO_SUPER_DIRECTION;
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
    public final void setKnots(final ModelElement subElement, final ModelElement superElement, final boolean registerInKnots) {
        ModelElement start = k1;
        ModelElement end = k2;
        super.setKnots(subElement, superElement, registerInKnots);
        if (isInCircle()) {
            subElement.removeEdge(this);
            superElement.removeEdge(this);
            super.setKnots(start, end, registerInKnots);
        }
    }

    @Override
    public final void setKnotsAndInsert(final ModelElement subElement, final int partEdgePos, final ModelElement superElement, final int parentEdgePos) {
        ModelElement start = k1;
        ModelElement end = k2;
        k1 = subElement;
        k2 = superElement;
        subElement.insertEdge(this, partEdgePos);
        superElement.insertEdge(this, parentEdgePos);
        if (isInCircle()) {
            subElement.removeEdge(this);
            superElement.removeEdge(this);
            super.setKnotsAndInsert(start, partEdgePos, end, parentEdgePos);
        }
    }

    @Override
    public final void setStartAndInsert(final ModelElement subElement) {
        ModelElement start = k1;
        k1 = subElement;
        k1.addEdge(this);
        if (isInCircle()) {
            subElement.removeEdge(this);
            super.setStartAndInsert(start);
        }
    }

    @Override
    public final void setEndAndInsert(final ModelElement superElement) {
        ModelElement end = k2;
        k2 = superElement;
        k2.addEdge(this);
        if (isInCircle()) {
            superElement.removeEdge(this);
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
                Log.show(Log.INFO, getResString("part_of_circle_error") + "\n" + getDisplayablePluralName(ModelElement.class) + ":\n" + getDisplayableName(k1) + ": " + k1.getName() + "\n" + getDisplayableName(k2) + ": " + k2.getName());
            }
            return retVal;
        }
        return false;
    }

}
