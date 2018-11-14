package de.imise.tool3lgm.graphtools.path;

import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction.BACKWARD;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction.FORWARD;

import com.google.common.base.Strings;

import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.util.ReflectionUtils;

/**
 * @author AXS (01.10.2018)
 */
public class SimpleMetaPathOld extends MetaPathOld {

    /**
     * Für jede Kante die einmal am Anfang ermittelte Richtung, in der die Kante den nächsten Pfadschritt beschreibt. Es wird immer zuerst auf
     * FORWARD getestet und wenn das nicht passt, dann wird automatisch BACKWARD gesetzt.
     */
    private Direction[] edgeDirections;

    /**
     * Für jeden Pfadschritt die speziellste der beiden Klassen, die die Verbindung zwischen 2 Schritten sind. Dieses Array ist immer genau 1 größer
     * als die Anzahl der Kanten.
     */
    private Class<? extends ModelElement>[] realPathStepClasses;

    /**
     * @param startClass
     * @param endClass
     * @param associations
     */
    @SafeVarargs
    public SimpleMetaPathOld(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final Class<? extends Edge>... associations) {
        this(startClass, endClass, null, associations);
    }

    /**
     * @param startClass
     * @param endClass
     * @param forwardAndBackwardResourceKeyPrefix
     * @param associations
     */
    @SafeVarargs
    public SimpleMetaPathOld(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final String forwardAndBackwardResourceKeyPrefix, final Class<? extends Edge>... associations) {
        this(Strings.isNullOrEmpty(forwardAndBackwardResourceKeyPrefix) ? null : forwardAndBackwardResourceKeyPrefix + "_f", startClass, endClass, associations);
        reversePath = new SimpleMetaPathOld(Strings.isNullOrEmpty(forwardAndBackwardResourceKeyPrefix) ? null : forwardAndBackwardResourceKeyPrefix + "_b", endClass, startClass, createReverseAssociations()[0]);
        reversePath.reversePath = this;
    }

    @SafeVarargs
    private SimpleMetaPathOld(final String forwardResourceKeyOrPathName, final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final Class<? extends Edge>... associations) {
        super(startClass, endClass, forwardResourceKeyOrPathName, associations);
        init();
    }

    @SuppressWarnings("unchecked")
    private void init() {
        Class<? extends Edge>[] edgeClasses = getEdgeClasses();
        edgeDirections = new Direction[edgeClasses.length];
        realPathStepClasses = new Class[edgeClasses.length + 1];
        Class<? extends ModelElement> lastStepEndClass = getStartClass();
        for (int i = 0; i < edgeClasses.length; i++) {
            Class<? extends Edge> edgeClass = edgeClasses[i];
            Class<? extends ModelElement> edgeStartClass = Edge.getStartClass(edgeClass);
            Class<? extends ModelElement> edgeEndClass = Edge.getEndClass(edgeClass);
            Class<?> pathStepStartClass = ReflectionUtils.getMostSpecialElementClass(lastStepEndClass, edgeStartClass);
            Direction direction = FORWARD;
            if (pathStepStartClass == null) {
                pathStepStartClass = ReflectionUtils.getMostSpecialElementClass(lastStepEndClass, edgeEndClass).asSubclass(ModelElement.class);
                direction = BACKWARD;
            }
            edgeDirections[i] = direction;
            realPathStepClasses[i] = pathStepStartClass.asSubclass(ModelElement.class);
            lastStepEndClass = direction == FORWARD ? edgeEndClass : edgeStartClass;
            //Endklasse des letzten Pfades und Endklasse des Gesamt-Pfades -> Speziellere beider Klassen ermitteln und in der Liste speichern
            if (i == edgeClasses.length - 1) {
                lastStepEndClass = ReflectionUtils.getMostSpecialElementClass(getEndClass(), lastStepEndClass).asSubclass(ModelElement.class);
                realPathStepClasses[i + 1] = lastStepEndClass;
            }
        }
    }

    /**
     * Liefert die Startklasse des Pfadschrittes mit dem übergebenen Index.
     * Dies ist gleichzeitig die Endklasse des vorherigen Pfadschrittes (wenn es einen solchen gibt).
     *
     * @param pathStepIndex
     * @return
     */
    public Class<? extends ModelElement> getPathStepStartClass(final int pathStepIndex) {
        return realPathStepClasses[pathStepIndex];
    }

    /**
     * Liefert die Endklasse des Pfadschrittes mit dem übergebenen Index.
     * Dies ist gleichzeitig die Startklasse des nächsten Pfadschrittes (wenn es einen solchen gibt).
     *
     * @param pathStepIndex
     * @return
     */
    public Class<? extends ModelElement> getPathStepEndClass(final int pathStepIndex) {
        return realPathStepClasses[pathStepIndex + 1];
    }

    /**
     * Liefert einen Sub-Path beginnend vom angegebenen Start-Index bis zur letzten Kante.
     *
     * @param pathStepStartIndex
     * @return
     */
    public SimpleMetaPathOld getSubPath(final int pathStepStartIndex) {
        return getSubPath(pathStepStartIndex, getLength());
    }

    /**
     * Liefert einen Sub-Path beginnend vom angegebenen Start-Index bis zu der Kante vor dem End-Index (exklusive).
     *
     * @param pathStepStartIndex
     * @param pathStepEndIndex
     * @return
     */
    public SimpleMetaPathOld getSubPath(final int pathStepStartIndex, final int pathStepEndIndex) {
        if (pathStepStartIndex >= pathStepEndIndex || pathStepStartIndex < 0 || pathStepStartIndex >= edgeDirections.length || pathStepEndIndex < 0 || pathStepEndIndex > edgeDirections.length) {
            throw new IllegalArgumentException("Invalid pathStepStartIndex=" + pathStepStartIndex + " and pathStepEndIndex=" + pathStepEndIndex);
        }
        @SuppressWarnings("unchecked")
        Class<? extends Edge>[] associations = new Class[pathStepEndIndex - pathStepStartIndex];
        System.arraycopy(getEdgeClasses(), pathStepStartIndex, associations, 0, associations.length);
        return new SimpleMetaPathOld(realPathStepClasses[pathStepStartIndex], realPathStepClasses[pathStepEndIndex], associations);
    }

    /**
     * Liefert <code>true</code>, wenn die Kante mit des Pfadschrittes mit dem angegebenen Index in Vorwärtsrichtung auf dem Pfad liegt.
     *
     * @param pathStepIndex
     * @return
     */
    public boolean pathStepEdgeIsForward(final int pathStepIndex) {
        return edgeDirections[pathStepIndex] == FORWARD;
    }

    @Override
    public SimpleMetaPathOld getReversePath() {
        return (SimpleMetaPathOld) super.getReversePath();
    }

    @Override
    public boolean isCreateable() {
        return true;
    }

}
