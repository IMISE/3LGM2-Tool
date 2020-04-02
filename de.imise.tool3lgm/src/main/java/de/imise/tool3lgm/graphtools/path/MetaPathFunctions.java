package de.imise.tool3lgm.graphtools.path;

import java.util.List;

import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.CompositionEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.InstanciationEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.metapaths.AbstractMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.ElementaryMetaPath;
import de.imise.util.ReflectionUtils;

/**
 * @author AXS
 * @create 25.07.2011
 */
public class MetaPathFunctions {

    /**
     * Gibt einen Elementarpfad anhand eines übergebenen Index zurück. Ist der Index >= 0, dann wird genau der Index zurück gegeben. Ist der Index <
     * 0, dann wird der übergebene Index von der Länge der Geamtliste der Elementarfade abgezogen. Möchte man also den letzten Elementarpfad haben,
     * muss man -1 übergeben, für den vorletzten -2 usw.
     *
     * @param metaPath
     * @param index
     *            Index der Kante im Pfad, wenn dieser eindeutig ist. Wird ein Wert < 0 übergeben, dann ergibt sich der Index aus der Summe der
     *            Gesamtanzahl der Elementarpfade und diesem Wert.
     * @return
     */
    public static final ElementaryMetaPath getElementaryMetaPathInPath(final AbstractMetaPath metaPath, final int index) {
        List<ElementaryMetaPath> elementaryMetaPaths = metaPath.getElementaryMetaPaths();
        if (elementaryMetaPaths.isEmpty()) {
            return null;
        }
        ElementaryMetaPath elementaryMetaPath = elementaryMetaPaths.get(index < 0 ? elementaryMetaPaths.size() + index : index);
        return elementaryMetaPath;
    }

    /**
     * Liefert die Verbindungsklasse der Elementarpfade am angegebenen Index. Es wird immer die Endklasse des Elementarpfades mit dem Index und die
     * Startklasse des nächsten genommen, wenn es einen nächsten gibt, und davon die speziellste gemeinsame Oberklasse zurück gegeben. Existiert für
     * den Pfad keine einfache Elementarpfadliste für diesen MetaPath, dann kommt <code>null</code> zurück.
     *
     * @param simpleMetaPath
     * @param pathStepIndex
     *            Index der Kante im Pfad, wenn dieser eindeutig ist. Wird ein Wert < 0 übergeben, dann ergibt sich der Index aus der Summe der
     *            Gesamtanzahl der Elementarpfade und diesem Wert.
     * @return
     */
    public static final Class<? extends ModelElement> getElementaryPathsConnectingClass(final AbstractMetaPath simpleMetaPath, final int pathStepIndex) {
        List<ElementaryMetaPath> elementaryMetaPaths = simpleMetaPath.getElementaryMetaPaths();
        if (elementaryMetaPaths.isEmpty()) {
            return null;
        }
        int index = pathStepIndex < 0 ? elementaryMetaPaths.size() + pathStepIndex : pathStepIndex;
        ElementaryMetaPath elementaryMetaPath1 = elementaryMetaPaths.get(index);
        ElementaryMetaPath elementaryMetaPath2 = null;
        if (index + 1 < elementaryMetaPaths.size()) {
            elementaryMetaPath2 = elementaryMetaPaths.get(index + 1);
        }
        return getElementaryPathsConnectingClass(elementaryMetaPath1, elementaryMetaPath2);
    }

    /**
     * Liefert die speziellere der Endklasse des ersten Pfades und der Startklasse des zweiten Pfades. Ist der zweite Pfad <code>null</code>, kommt
     * die Endklasse des ersten zurück.
     *
     * @param elementaryMetaPathToConnectingClass
     * @param elementaryMetaPathFromConnectionClass
     * @return
     */
    public static final Class<? extends ModelElement> getElementaryPathsConnectingClass(final ElementaryMetaPath elementaryMetaPathToConnectingClass, final ElementaryMetaPath elementaryMetaPathFromConnectionClass) {
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
     * Liefert die speziellere der Endklasse des ersten Pfades und der Startklasse des zweiten Pfades. Ist der zweite Pfad <code>null</code>, kommt
     * die Endklasse des ersten zurück.
     *
     * @param edgeClass1
     * @param direction1
     * @param edgeClass2
     * @param direction2
     * @return
     */
    public static final Class<? extends ModelElement> getElementaryPathsConnectingClass(final Class<? extends Edge> edgeClass1, final Direction direction1, final Class<? extends Edge> edgeClass2, final Direction direction2) {
        Class<? extends ModelElement> endClass = ElementaryMetaPath.getEndClass(edgeClass1, direction1);
        if (edgeClass2 == null) {
            return endClass;
        }
        Class<? extends ModelElement> nextStartClass = ElementaryMetaPath.getStartClass(edgeClass2, direction2);
        Class<? extends ModelElement> connectingClass = ReflectionUtils.getMostSpecialClass(endClass, nextStartClass);
        return connectingClass;
    }

    /**
     * Liefert true, wenn die Kantenklasse eine Composition ist und die zugehörige Richtung (direction) vom Master auf den Slave zeigt.
     *
     * @param edgeClass
     * @param direction
     * @return
     */
    static final boolean isCompositionFromMasterToSlave(final Class<? extends Edge> edgeClass, final Direction direction) {
        boolean isEdgeMasterToSlaveComposition = MetaModel.isComposition(edgeClass);
        if (!isEdgeMasterToSlaveComposition) {
            return false;
        }
        isEdgeMasterToSlaveComposition = direction == CompositionEdge.MASTER_TO_SLAVE_DIRECTION;
        return isEdgeMasterToSlaveComposition;
    }

    /**
     * Liefert true, wenn die Kantenklasse eine Composition ist und die zugehörige Richtung (direction) vom Master auf den Slave zeigt.
     *
     * @param edgeClass
     * @param direction
     * @return
     */
    static final boolean isInstanciationFromMasterToInstance(final Class<? extends Edge> edgeClass, final Direction direction) {
        boolean isEdgeMasterToInstanceInstanciation = MetaModel.isInstanciation(edgeClass);
        if (!isEdgeMasterToInstanceInstanciation) {
            return false;
        }
        isEdgeMasterToInstanceInstanciation = direction == InstanciationEdge.MASTER_TO_INSTANCE_DIRECTION;
        return isEdgeMasterToInstanceInstanciation;
    }

}
