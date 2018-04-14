/*
 * Created on 20.07.2004
 */
package de.imise.tool3lgm.graphtools.path;

import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.getOther;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.isStartOrEndClass;

import java.awt.Color;
import java.util.Arrays;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.util.ReflectionUtils;
import de.imise.util.collections.CollectionUtils;

/**
 * @author Thomas Rudert
 * @author AXS (5.10.2007)
 */
public class MetaPath {

    /** Startelementtyp des Pfades */
    private final Class<? extends ModelElement> startClass;

    /** Endelementtyp des Pfades */
    private final Class<? extends ModelElement> endClass;

    /**
     * Assoziationen des Pfades.<br>
     * Index 1: Index der Metapfade<br>
     * Index 2: Index der Metapfadschritte (Assoziationstypen) im Metpfad
     */
    private Class<? extends Edge>[][] associations;

    /**
     * COMMENTME
     */
    private final String[] pathNames;

    /**
     * COMMENTME
     */
    private final int control;

    /**
     * COMMENTME
     */
    private final boolean directional;

    /**
     * COMMENTME
     */
    private static final String[] defaultPathName = {
            ModelConstants.getForwardMetaAssociationName(Edge.class)
    };

    /**
     * Erzeugt einen MetaPath, der zu dem übergebenen MetaPath identisch ist, nur mit der neuen übergebenen Start- und Endklasse.
     *
     * @param newStartClass ModelElement class where associations starts
     * @param newEndClass ModelElement class where associations ends
     * @param originalMetaPath
     * @throws InvalidPathException
     */
    public MetaPath(final Class<? extends ModelElement> newStartClass, final Class<? extends ModelElement> newEndClass, final MetaPath originalMetaPath) {
        this(newStartClass, newEndClass, originalMetaPath.associations, originalMetaPath.pathNames, originalMetaPath.control, originalMetaPath.directional);
    }

    /**
     * @param startClass ModelElement class where associations starts
     * @param endClass ModelElement class where associations ends
     * @param description of the path or the resource key of the desciption
     * @param associations EdgeClasses for this path
     * @throws InvalidPathException
     */
    public MetaPath(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final String resourceKeyOrPathName, final Class<? extends Edge>... associations) {
        this(startClass, endClass, getPathForAssociations(associations), Tool3lgmConstants.getResStringWithoutError(resourceKeyOrPathName));
    }

    /**
     * @param startClass ModelElement class where associations starts
     * @param endClass ModelElement class where associations ends
     * @param associations EdgeClasses for this path
     * @throws InvalidPathException
     */
    public MetaPath(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final Class<? extends Edge>... associations) {
        this(startClass, endClass, getPathForAssociations(associations), defaultPathName);
    }

    /**
     * @param startClass ModelElement class where associations starts
     * @param endClass ModelElement class where associations ends
     * @param associations int[][] with type-constants for connections to come from start to end (int[] different possibilities to come from start to
     *            end)
     * @throws InvalidPathException
     */
    public MetaPath(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final Class<? extends Edge>[][] path) {
        this(startClass, endClass, path, defaultPathName);
    }

    /**
     * @param startClass ModelElement class where associations starts
     * @param endClass ModelElement class where associations ends
     * @param associations int[][] with type-constants for connections to come from start to end (int[] diverent possibilities to come from start to
     *            end)
     * @param resourceKeyOrPathName String with description for associations or the resource key for this
     * @throws InvalidPathException
     */
    public MetaPath(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final Class<? extends Edge>[][] path, final String resourceKeyOrPathName) {
        this(startClass, endClass, path, resourceKeyOrPathName, false);
    }

    /**
     * @param startClass ModelElement class where associations starts
     * @param endClass ModelElement class where associations ends
     * @param associations int[][] with type-constants for connections to come from start to end (int[] diverent possibilities to come from start to
     *            end)
     * @param resourceKeyOrPathName String with description for associations or the resource key for this description
     * @param directional boolean with true, if it is important which element is in row an which in column (exp: row is part of col; but not for
     *            function reads objecttype )
     * @throws InvalidPathException
     */
    public MetaPath(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final Class<? extends Edge>[][] path, final String resourceKeyOrPathName, final boolean directional) {
        this(startClass, endClass, path, CollectionUtils.toStringArray(resourceKeyOrPathName), 0, directional);
    }

    /**
     * @param startClass ModelElement class where associations starts
     * @param endClass ModelElement class where associations ends
     * @param associations int[][] with type-constants for connections to come from start to end (int[] diverent possibilities to come from start to
     *            end)
     * @param resourceKeyOrPathName String with description for associations or the resource key for this description
     * @throws InvalidPathException
     */
    public MetaPath(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final Class<? extends Edge>[][] path, final Color color, final String resourceKeyOrPathName) {
        this(startClass, endClass, path, CollectionUtils.toStringArray(resourceKeyOrPathName));
    }

    /**
     * @param startClass ModelElement class where associations starts
     * @param endClass ModelElement class where associations ends
     * @param associations int[][] with type-constants for connections to come from start to end (int[] diverent possibilities to come from start to
     *            end)
     * @param resourceKeyOrPathNames String[] with descriptions for associations (one description for DOUBLE / FORWARD / BACKWARD) or the resource
     *            keys for this strings
     * @throws InvalidPathException
     */
    public MetaPath(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final Class<? extends Edge>[][] path, final String[] resourceKeyOrPathNames) {
        this(startClass, endClass, path, resourceKeyOrPathNames, 0, false);
    }

    /**
     * @param startClass ModelElement class where associations starts
     * @param endClass ModelElement class where associations ends
     * @param associations int[][] with type-constants for connections to come from start to end (int[] diverent possibilities to come from start to
     *            end)
     * @param description String[] with descriptions for associations (in legend) (one description for DOUBLE / FORWARD / BACKWARD)
     * @parma int control index of connections in associations, which control direction of associations
     * @param directional boolean with true, if it is important which element is in row an which in column (exp: row is part of col; but not for
     *            function reads objecttype )
     * @throws InvalidPathException
     */
    public MetaPath(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final Class<? extends Edge>[][] path, final String[] resourceKeyOrPathNames, final int control, final boolean directional) {
        this.directional = directional;
        this.startClass = startClass;
        this.endClass = endClass;
        //        this.control = path[0].length - (control + 1);
        this.control = control;
        associations = path;
        ensureAssociationOrder();
        pathNames = resourceKeyOrPathNames;
        for (int i = 0; i < pathNames.length; i++) {
            pathNames[i] = Tool3lgmConstants.getResStringWithoutError(pathNames[i]);
        }
        for (int i = 0; i < countPathes(); i++) {
            if (control >= getLength(i)) {
                throw new Error("MetaPath: controlIndex is out of range!");
            }
        }
    }

    /**
     * Es wird sicher gestellt, dass die Assoziationen einen Pfad von der Start- zur Zielklasse des Pfades beschreiben. Im Moment wird noch nicht die
     * Konsistenz geprüft, sondern nur, ob die erste Assoziation zur Startklasse und die letzte Assoziation zur
     * Endklasse passen.
     */
    private final void ensureAssociationOrder() {
        boolean switchAssociations = false;
        for (int ep = 0; ep < countPathes(); ep++) {
            Class<? extends Edge>[] edgeClasses = getEdgeClasses(ep);
            int lastIndex = edgeClasses.length - 1;
            if (isStartOrEndClass(edgeClasses[0], startClass) && isStartOrEndClass(edgeClasses[lastIndex], endClass)) {
                return;
            }
            if (isStartOrEndClass(edgeClasses[0], endClass) && isStartOrEndClass(edgeClasses[lastIndex], startClass)) {
                switchAssociations = true;
            }
        }
        //wenn switchAssociations immer noch false ist und er bis hier gekommen ist, dann passt der MetaPath gar
        //nicht zu den Start und Zielklassen -> einfach raus gehen
        if (!switchAssociations) {
            throw new Error("MetaPath: start and end class doesn't match");
        }

        @SuppressWarnings("unchecked")
        Class<? extends Edge>[][] path = new Class[countPathes()][getEdgeClasses(0).length];
        //von allen Assoziationslisten alle Assoziationen umdrehen
        for (int ep = 0; ep < countPathes(); ep++) {
            Class<? extends Edge>[] edgeClasses = getEdgeClasses(ep);
            int lastIndex = edgeClasses.length - 1;
            for (int i = 0; i < edgeClasses.length; i++) {
                path[ep][lastIndex - i] = edgeClasses[i];
            }
        }
        associations = path;
    }

    /**
     * @param edgeClass
     * @return /
     *         @SuppressWarnings("unchecked") private static final Class<? extends Edge>[][] getPathForAssociation(Class<? extends Edge> edgeClass){
     *         Class<?
     *         extends Edge>[][] associations = new Class[1][1]; associations[0][0] = edgeClass; return associations;
     *         } /**
     * @param edgeClass
     * @return
     */
    @SuppressWarnings("unchecked")
    public static final Class<? extends Edge>[][] getPathForAssociations(final Class<? extends Edge>... edgeClasses) {
        Class<? extends Edge>[][] associations = new Class[1][edgeClasses.length];
        for (int i = 0; i < edgeClasses.length; i++) {
            associations[0][i] = edgeClasses[i];
        }
        return associations;
    }

    /**
     * Gibt die Länge des Metapfades an Position <code>pathIndex</code> zurück.
     *
     * @param Index des Metapfades, dessen Länge zurück gegeben werden soll
     * @return Länge des Metapfades an Position <code>pathIndex</code>
     */
    public final int getLength(final int pathIndex) {
        return associations[pathIndex].length;
    }

    /**
     * Gibt die Länge des ersten Metapfades zurück.
     *
     * @return Länge des Metapfades an Position 0
     */
    public final int getLength() {
        return associations[0].length;
    }

    /**
     * Liefert <code>true</code>, wenn der Pfad an Position <code>pathIndex</code> die Länge 1 besitzt.
     *
     * @param pathIndex
     * @return <code>true</code>, wenn der Metapfad an Position <code>pathIndex</code> die Länge 1 besitzt.
     */
    public final boolean isImmediate(final int pathIndex) {
        return getLength(pathIndex) == 1;
    }

    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder();
        boolean useDescrip = !"".equals(pathNames[0].trim());
        if (useDescrip) {
            sb.append(pathNames[0]);
            for (int i = 1; i < pathNames.length; i++) {
                sb.append(", ");
                sb.append(pathNames[i]);
            }
        } else {
            sb.append(Arrays.asList(associations[0]));
            for (int i = 1; i < associations.length; i++) {
                sb.append(", ");
                sb.append(Arrays.asList(associations[i]));
            }
        }
        return sb.toString();
    }

    /**
     * Liefert die Klasse des Startelementtyps dieses Pfades
     *
     * @see <code>ModelConstants</code>
     * @return ID des Startelementtyps dieses Pfades
     */
    public final Class<? extends ModelElement> getStartClass() {
        return startClass;
    }

    /**
     * Liefert die Klasse des Endelementtyps dieses Pfades
     *
     * @see <code>ModelConstants</code>
     * @return ID des Endelementtyps dieses Pfades
     */
    public final Class<? extends ModelElement> getEndClass() {
        return endClass;
    }

    /**
     * Liefert die Klasse des Endelementtyps dieses Pfades im ersten ElementarMetaPath
     *
     * @param edgeIndex Index der Edge im Elementarpfad, dessen Endklasse ermittelt werden soll
     * @return Elementklasse, bei der der Pfadteil endet
     */
    public final Class<? extends ModelElement> getEndClass(final int edgeIndex) {
        return getEndClass(0, edgeIndex);
    }

    /**
     * Liefert die Klasse des Endelementtyps dieses Pfades
     *
     * @param elementarPathIndex Index des Elementarpfades, dessen Endklasse ermittelt werden soll
     * @param edgeIndex Index der Edge im Elementarpfad, dessen Endklasse ermittelt werden soll
     * @return Elementklasse, bei der der Pfadteil endet
     */
    public final Class<? extends ModelElement> getEndClass(final int elementarPathIndex, final int edgeIndex) {
        Class<? extends Edge>[] currentElementarPath = associations[elementarPathIndex];
        Class<? extends Edge> currentEdgeClass = currentElementarPath[0];
        Class<? extends ModelElement> currentElementClass = startClass;
        Class<? extends ModelElement> foundEndClass = getOther(currentEdgeClass, currentElementClass);
        for (int i = 1; i <= edgeIndex; i++) {
            currentEdgeClass = currentElementarPath[i];
            currentElementClass = foundEndClass;
            foundEndClass = getOther(currentEdgeClass, currentElementClass);
        }
        return foundEndClass;
    }

    /**
     * Liefert die Anzahl der Metapfade des Pfades
     *
     * @return Anzahl der Metapfade
     */
    public final int countPathes() {
        return associations.length;
    }

    /**
     * Liefert die IDs der Assoziationstypen des ersten ElementarMetaPfades
     *
     * @return IDs der Assoziationstypen des Pfades an Position <code>pathIndex</code>
     */
    public final Class<? extends Edge>[] getEdgeClasses() {
        return associations[0];
    }

    /**
     * Liefert die IDs der Assoziationstypen des Pfades an Position <code>pathIndex</code>
     *
     * @param pathIndex Index des Pfadschrittes
     * @return IDs der Assoziationstypen des Pfades an Position <code>pathIndex</code>
     */
    public final Class<? extends Edge>[] getEdgeClasses(final int pathIndex) {
        return associations[pathIndex];
    }

    /**
     * @return
     */
    public int countOptions() {
        return pathNames.length;
    }

    /**
     * @return
     */
    public final int getControl() {
        return control;
    }

    /**
     * @return
     */
    public final boolean isDirectional() {
        return directional;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.deepHashCode(associations);
        result = prime * result + (endClass == null ? 0 : endClass.hashCode());
        result = prime * result + (startClass == null ? 0 : startClass.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!MetaPath.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        MetaPath mp = (MetaPath) obj;
        //Start- und Endklasse müssen gleich sein
        if (mp.startClass != startClass || mp.endClass != endClass) {
            return false;
        }
        //die Anzahl der verschiedenen Pfade muss gleich sein
        if (mp.associations.length != associations.length) {
            return false;
        }
        for (int x = 0; x < associations.length; x++) {
            //Anzahl der Assoziationen in den einzelnen Pfaden muss gleich sein
            if (mp.associations[x].length != associations[x].length) {
                return false;
            }
            //jede einzelne Assoziation der einzelnen Pfade muss identisch sein
            for (int y = 0; y < associations[x].length; y++) {
                if (mp.associations[x][y] != associations[x][y]) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Liefer den Namen des Pfades ohne Start- und Endklasse, d.h einfach nur die erste Beschreibung des Pfades aus der Lsute der Beschreibungen.
     *
     * @param direction
     * @return
     */
    public final String getDescription(final int direction) {
        return direction < 0 || direction > 2 ? null : pathNames[direction < pathNames.length ? direction : 0];
    }

    /**
     * Liefer die erste Beschreibung des Pfades. Dabei wird der übergebene Prefix vorangestellt und der übergebene Postfix angehängt.
     *
     * @param prefix
     * @param postfix
     * @return
     */
    public final String getDescription(final String prefix, final String postfix) {
        return getDescription(0, prefix, postfix);
    }

    /**
     * Liefert aus der Liste der Beschrebungen des Pfades diejenige mit dem übergebenen Index.
     * Dabei wird der übergebene Prefix vorangestellt und der übergebene Postfix angehängt.
     *
     * @param descriptionIndex
     * @param prefix
     * @param postfix
     * @return
     */
    public final String getDescription(final int descriptionIndex, final String prefix, final String postfix) {
        return prefix + " " + getDescription(descriptionIndex) + " " + postfix;
    }

    /**
     * Liefer den Namen des Pfades mit Start- und Endklasse. Es wird immer die Beschreibung aus der Liste der
     * Beschreibungen zurück gegeben, die den Index descriptionIndex hat.
     *
     * @param descriptionIndex
     * @return
     */
    public final String getFullDescription(final int descriptionIndex) {
        return getDescription(descriptionIndex, ModelConstants.getDisplayableName(startClass), ModelConstants.getDisplayableName(endClass));
    }

    /**
     * Liefer den Namen des Pfades mit Start- und Endklasse. Es wird immer nur die erste Beschreibung aus der Liste der
     * Beschreibungen zurück gegeben. Bei Pfaden mit doppelter Bedeutung ist das imemr die Beschreibung der Richtung DOUBLE.
     *
     * @return
     */
    public final String getFullDescription() {
        return getFullDescription(0);
    }

    /**
     * Liefert <code>true</code>, wenn die Start- und Endklasse des Pfades gleich oder die eine eine Oberklasse der anderen ist.
     *
     * @return
     */
    public boolean hasAssignableStartEndClass() {
        return ReflectionUtils.isAssingable(startClass, endClass);
    }

    /**
     * Liefert <code>true</code>, wenn jeder Einzelpfad dieses Pfades mind. eine {@link HierarchyEdge} enthält.
     *
     * @return
     */
    public boolean isRecursiveSubordinationPath() {
        for (Class<? extends Edge>[] singlePath : associations) {
            boolean singlePathHasRecursiveSubordinatioEdge = false;
            for (Class<? extends Edge> association : singlePath) {
                if (ModelConstants.isRecursiveSubordination(association)) {
                    singlePathHasRecursiveSubordinatioEdge = true;
                    break;
                }
            }
            if (!singlePathHasRecursiveSubordinatioEdge) {
                return false;
            }
        }
        return true;
    }

}