package de.imise.tool3lgm.graphtools.path.metapaths;

import java.util.List;

import de.imise.tool3lgm.graphtools.metamodel.CoreMetaModel;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.CompositionEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.InstanciationEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.util.ReflectionUtils;

/**
 * @author AXS
 * @create 25.07.2011
 */
public class MetaPathFunctions {

    /**
     * Gibt einen Elementarpfad anhand eines übergebenen Index zurück. Ist der
     * Index >= 0, dann wird genau der Index zurück gegeben. Ist der Index < 0,
     * dann wird der übergebene Index von der Länge der Geamtliste der
     * Elementarfade abgezogen. Möchte man also den letzten Elementarpfad haben,
     * muss man -1 übergeben, für den vorletzten -2 usw.
     *
     * @param metaPath
     * @param index Index der Kante im Pfad, wenn dieser eindeutig ist. Wird ein
     *            Wert < 0 übergeben, dann ergibt sich der Index aus der Summe
     *            der Gesamtanzahl der Elementarpfade und diesem Wert.
     * @return
     */
    public static final ElementaryMetaPath getElementaryMetaPathInPath(final MetaPath metaPath, final int index) {
        List<ElementaryMetaPath> elementaryMetaPaths = metaPath.getElementaryMetaPaths();
        if (elementaryMetaPaths.isEmpty()) {
            return null;
        }
        ElementaryMetaPath elementaryMetaPath = elementaryMetaPaths.get(index < 0 ? elementaryMetaPaths.size() + index : index);
        return elementaryMetaPath;
    }

    /**
     * Liefert die Verbindungsklasse der Elementarpfade am angegebenen Index. Es
     * wird immer die Endklasse des Elementarpfades mit dem Index und die
     * Startklasse des nächsten genommen, wenn es einen nächsten gibt, und davon
     * die speziellste gemeinsame Oberklasse zurück gegeben. Existiert für den
     * Pfad keine einfache Elementarpfadliste für diesen MetaPath, dann kommt
     * <code>null</code> zurück.
     *
     * @param metaPath
     * @param pathStepIndex Index der Kante im Pfad, wenn dieser eindeutig ist.
     *            Wird ein Wert < 0 übergeben, dann ergibt sich der Index aus
     *            der Summe der Gesamtanzahl der Elementarpfade und diesem Wert.
     * @return
     */
    public static final Class<? extends ModelElement> getElementaryMetaPathsConnectingClass(final MetaPath metaPath, final int pathStepIndex) {
        return getMetaPathsConnectingClass(metaPath, pathStepIndex, true);
    }

    /**
     * Liefert die Verbindungsklasse der Elementarpfade am angegebenen Index. Es
     * wird immer die Endklasse des Elementarpfades mit dem Index und die
     * Startklasse des nächsten genommen, wenn es einen nächsten gibt, und davon
     * die speziellste gemeinsame Oberklasse zurück gegeben. Existiert für den
     * Pfad keine einfache Elementarpfadliste für diesen MetaPath, dann kommt
     * <code>null</code> zurück.
     *
     * @param metaPath
     * @param pathStepIndex Index der Kante im Pfad, wenn dieser eindeutig ist.
     *            Wird ein Wert < 0 übergeben, dann ergibt sich der Index aus
     *            der Summe der Gesamtanzahl der Elementarpfade und diesem Wert.
     * @return
     */
    public static final Class<? extends ModelElement> getSubMetaPathsConnectingClass(final MetaPath metaPath, final int pathStepIndex) {
        return getMetaPathsConnectingClass(metaPath, pathStepIndex, false);
    }

    /**
     * Returns the connection class of the path step with the passed index in
     * the element path list of this path. With index 0, this is the more
     * special of the end class of the first elementary path and the start class
     * of the next elementary path. The path step with the index of path length
     * -1 is the end class of the last elementary path = end class of the whole
     * elementary path list. The start class of the complete path is not
     * accessible through this function.<br>
     * Liefert die Verbindungsklasse der Elementarpfade am angegebenen Index. Es
     * wird immer die Endklasse des Elementarpfades mit dem Index und die
     * Startklasse des nächsten genommen, wenn es einen nächsten gibt, und davon
     * die speziellste gemeinsame Oberklasse zurück gegeben. Existiert für den
     * Pfad keine einfache Elementarpfadliste für diesen MetaPath, dann kommt
     * <code>null</code> zurück.
     *
     * @param metaPath
     * @param pathStepIndex Index der Kante im Pfad, wenn dieser eindeutig ist.
     *            Wird ein Wert < 0 übergeben, dann ergibt sich der Index aus
     *            der Summe der Gesamtanzahl der Elementarpfade und diesem Wert.
     * @param pathStepIndex
     * @param checkElementaryMetaPaths
     * @return
     */
    @SuppressWarnings("unchecked")
    private static final Class<? extends ModelElement> getMetaPathsConnectingClass(final MetaPath metaPath, final int pathStepIndex, final boolean checkElementaryMetaPaths) {
        List<MetaPath> subMetaPaths = checkElementaryMetaPaths ? (List<MetaPath>) (List<?>) metaPath.getElementaryMetaPaths() : metaPath.getSubMetaPaths();
        if (subMetaPaths.isEmpty()) {
            return null;
        }
        int index = pathStepIndex < 0 ? subMetaPaths.size() + pathStepIndex : pathStepIndex;
        MetaPath elementaryMetaPath1 = subMetaPaths.get(index);
        MetaPath elementaryMetaPath2 = null;
        if (index + 1 < subMetaPaths.size()) {
            elementaryMetaPath2 = subMetaPaths.get(index + 1);
        }
        return getMetaPathsConnectingClass(elementaryMetaPath1, elementaryMetaPath2);
    }

    /**
     * Liefert die speziellere der Endklasse des ersten Pfades und der
     * Startklasse des zweiten Pfades. Ist der zweite Pfad <code>null</code>,
     * kommt die Endklasse des ersten zurück.
     *
     * @param elementaryMetaPathToConnectingClass
     * @param elementaryMetaPathFromConnectionClass
     * @return
     */
    public static final Class<? extends ModelElement> getMetaPathsConnectingClass(final MetaPath elementaryMetaPathToConnectingClass, final MetaPath elementaryMetaPathFromConnectionClass) {
        //ACHTUNG: diese Funktion nicht einfach durch die andere mit den EdgeClasses laufen lassen, da die Start- und Endklasse der ElementaryMetaPaths was anderes sein können, als die Start- bzw. die Endklasse der enthaltenen Kantenklasse
        if (elementaryMetaPathToConnectingClass == null) { //tritt auf, wenn es um den ersten Pfaschritt geht, also den Anfang des Pfades
            return elementaryMetaPathFromConnectionClass.getStartClass();
        }
        if (elementaryMetaPathFromConnectionClass == null) { //tritt auf, wenn es um den letzten Pfadschritt geht, also das Ende des Pfades
            return elementaryMetaPathToConnectingClass.getEndClass();
        }
        Class<? extends ModelElement> lastEndClass = elementaryMetaPathToConnectingClass.getEndClass();
        Class<? extends ModelElement> nextStartClass = elementaryMetaPathFromConnectionClass.getStartClass();
        Class<? extends ModelElement> connectingClass = ReflectionUtils.getMostSpecialClass(lastEndClass, nextStartClass);
        return connectingClass;
    }

    /**
     * Liefert die speziellere der Endklasse des ersten Pfades und der
     * Startklasse des zweiten Pfades. Ist der zweite Pfad <code>null</code>,
     * kommt die Endklasse des ersten zurück.
     *
     * @param edgeClass1
     * @param direction1
     * @param edgeClass2
     * @param direction2
     * @return
     */
    public static final Class<? extends ModelElement> getEdgeClassesConnectingClass(final Class<? extends Edge> edgeClass1, final Direction direction1, final Class<? extends Edge> edgeClass2, final Direction direction2) {
        Class<? extends ModelElement> endClass = ElementaryMetaPath.getEndClass(edgeClass1, direction1);
        if (edgeClass2 == null) {
            return endClass;
        }
        Class<? extends ModelElement> nextStartClass = ElementaryMetaPath.getStartClass(edgeClass2, direction2);
        Class<? extends ModelElement> connectingClass = ReflectionUtils.getMostSpecialClass(endClass, nextStartClass);
        return connectingClass;
    }

    /**
     * Liefert true, wenn die Kantenklasse eine Composition ist und die
     * zugehörige Richtung (direction) vom Master auf den Slave zeigt.
     *
     * @param edgeClass
     * @param direction
     * @return
     */
    static final boolean isCompositionFromMasterToSlave(final Class<? extends Edge> edgeClass, final Direction direction) {
        boolean isEdgeMasterToSlaveComposition = CoreMetaModel.isComposition(edgeClass);
        if (!isEdgeMasterToSlaveComposition) {
            return false;
        }
        isEdgeMasterToSlaveComposition = direction == CompositionEdge.MASTER_TO_SLAVE_DIRECTION;
        return isEdgeMasterToSlaveComposition;
    }

    /**
     * Liefert true, wenn die Kantenklasse eine Composition ist und die
     * zugehörige Richtung (direction) vom Master auf den Slave zeigt.
     *
     * @param edgeClass
     * @param direction
     * @return
     */
    static final boolean isInstanciationFromMasterToInstance(final Class<? extends Edge> edgeClass, final Direction direction) {
        boolean isEdgeMasterToInstanceInstanciation = CoreMetaModel.isInstanciation(edgeClass);
        if (!isEdgeMasterToInstanceInstanciation) {
            return false;
        }
        isEdgeMasterToInstanceInstanciation = direction == InstanciationEdge.MASTER_TO_INSTANCE_DIRECTION;
        return isEdgeMasterToInstanceInstanciation;
    }

    /**
     * @param metaPath
     * @return <code>true</code> if the first {@link ElementaryMetaPath} is
     *         between pure template elements
     * @see {@link MetaModel#isPureTemplateElementClass(Class)}
     */
    public static final boolean startsWitTemplateElementsElementaryMetaPath(final MetaPath metaPath) {
        List<MetaPath> subMetaPaths = metaPath.getSubMetaPaths();
        MetaModel metaModel = metaPath.getMetaModel();
        for (MetaPath subMetaPath : subMetaPaths) {
            List<ElementaryMetaPath> elementaryMetaPaths = metaPath.getElementaryMetaPaths();
            if (!elementaryMetaPaths.isEmpty()) {
                //At the moment only the first elementary metaPath counts. In special cases
                //this can be wrong and must be expanded to the whole metaPath. At the moment
                //this condition work for all cases we have.
                ElementaryMetaPath firstElementaryMetaPath = elementaryMetaPaths.get(0);
                Class<? extends ModelElement> startClass = firstElementaryMetaPath.getStartClass();
                if (!metaModel.isPureTemplateElementClass(startClass)) {
                    return false;
                }
                Class<? extends ModelElement> endClass = firstElementaryMetaPath.getEndClass();
                if (!metaModel.isPureTemplateElementClass(endClass)) {
                    return false;
                }
            } else if (!startsWitTemplateElementsElementaryMetaPath(subMetaPath)) {
                return false;
            }
        }
        return true;
    }

}
