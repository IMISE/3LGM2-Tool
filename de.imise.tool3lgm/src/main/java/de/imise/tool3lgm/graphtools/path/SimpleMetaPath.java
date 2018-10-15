package de.imise.tool3lgm.graphtools.path;

import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.util.ReflectionUtils;

/**
 * @author AXS (01.10.2018)
 */
public class SimpleMetaPath extends MetaPath {

    /**
     * Für jede Kante die einmal am Anfang ermittelte Richtung, in der die Kante den nächsten Pfadschritt beschreibt. Es wird immer zuerst auf
     * FORWARD getestet und wenn das nicht passt, dann wird automatisch BACKWARD gesetzt.
     */
    private int[] edgeDirections;

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
    public SimpleMetaPath(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final Class<? extends Edge>... associations) {
        super(startClass, endClass, associations);
        init();
    }

    @SuppressWarnings("unchecked")
    private void init() {
        Class<? extends Edge>[] edgeClasses = getEdgeClasses();
        edgeDirections = new int[edgeClasses.length];
        realPathStepClasses = new Class[edgeClasses.length + 1];
        Class<? extends ModelElement> lastStepEndClass = getStartClass();
        for (int i = 0; i < edgeClasses.length; i++) {
            Class<? extends Edge> edgeClass = edgeClasses[i];
            Class<? extends ModelElement> edgeStartClass = Edge.getStartClass(edgeClass);
            Class<? extends ModelElement> edgeEndClass = Edge.getEndClass(edgeClass);
            Class<?> pathStepStartClass = ReflectionUtils.getMostSpecialElementClass(lastStepEndClass, edgeStartClass);
            int direction = Edge.FORWARD;
            if (pathStepStartClass == null) {
                pathStepStartClass = ReflectionUtils.getMostSpecialElementClass(lastStepEndClass, edgeEndClass).asSubclass(ModelElement.class);
                direction = Edge.BACKWARD;
            }
            edgeDirections[i] = direction;
            realPathStepClasses[i] = pathStepStartClass.asSubclass(ModelElement.class);
            lastStepEndClass = direction == Edge.FORWARD ? edgeEndClass : edgeStartClass;
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
    public SimpleMetaPath getSubPath(final int pathStepStartIndex) {
        return getSubPath(pathStepStartIndex, getLength());
    }

    /**
     * Liefert einen Sub-Path beginnend vom angegebenen Start-Index bis zu der Kante vor dem End-Index (exklusive).
     *
     * @param pathStepStartIndex
     * @param pathStepEndIndex
     * @return
     */
    public SimpleMetaPath getSubPath(final int pathStepStartIndex, final int pathStepEndIndex) {
        if (pathStepStartIndex >= pathStepEndIndex || pathStepStartIndex < 0 || pathStepStartIndex >= edgeDirections.length || pathStepEndIndex < 0 || pathStepEndIndex > edgeDirections.length) {
            throw new IllegalArgumentException("Invalid pathStepStartIndex=" + pathStepStartIndex + " and pathStepEndIndex=" + pathStepEndIndex);
        }
        Class<? extends Edge>[] associations = new Class[pathStepEndIndex - pathStepStartIndex];
        System.arraycopy(getEdgeClasses(), pathStepStartIndex, associations, 0, associations.length);
        return new SimpleMetaPath(realPathStepClasses[pathStepStartIndex], realPathStepClasses[pathStepEndIndex], associations);
    }

    /**
     * Liefert <code>true</code>, wenn die Kante mit des Pfadschrittes mit dem angegebenen Index in Vorwärtsrichtung auf dem Pfad liegt.
     *
     * @param pathStepIndex
     * @return
     */
    public boolean pathStepEdgeIsForward(final int pathStepIndex) {
        return edgeDirections[pathStepIndex] == Edge.FORWARD;
    }

    @Override
    public SimpleMetaPath getReversePath() {
        MetaPath reverseMetaPath = super.getReversePath();
        return new SimpleMetaPath(reverseMetaPath.getStartClass(), reverseMetaPath.getEndClass(), reverseMetaPath.getEdgeClasses());
    }

}
