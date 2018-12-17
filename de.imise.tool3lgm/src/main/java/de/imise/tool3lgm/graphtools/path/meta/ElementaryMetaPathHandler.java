package de.imise.tool3lgm.graphtools.path.meta;

import java.util.HashMap;

import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge.ConnectionState;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;

/**
 * Dieser Hanldler merkt sich alle
 *
 * @author AXS (5 Dec 2018)
 */
public class ElementaryMetaPathHandler {

    /** Platzhaltermetapfad für die Definition einer beliebigen Verbindung z. B. in einem {@link SequenceMetaPath} */
    public static final ElementaryMetaPath GENERAL_ELEMENTARY_SUPER_PATH = new ElementaryMetaPath(Edge.class, Direction.FORWARD);

    /**
     * Mappt von einer Edgenklasse auf ein 2-elementiges Array von MetaPathes, wobei der erste MetaPath
     * im Array der zur Edge gehörige Metapath in Richtung Edge.Direction.FORWARD und der zweite in
     * Richtung Edge.Direction.BACKWARD ist.
     */
    private static final HashMap<Class<? extends Edge>, ElementaryMetaPath[]> EDGE_CLASS_TO_FORWARD_AND_BACKWARD_METAPATHES = new HashMap<>();

    /**
     * Liefert für eine Edge den dazugehörigen ElementarMetaPfad. Wenn der Rückgabepfad noch nicht in der Map für die
     * Vorwärts- und Rückwärtsrichtung der Elementarpfade enthalten ist, dann wird er hinzugefügt.
     *
     * @param edgeClass
     * @param direction
     * @return
     */
    public static final ElementaryMetaPath getMetaPath(final Class<? extends Edge> edgeClass, final Direction direction) {
        return getMetaPath(edgeClass, direction, (ConnectionState) null);
    }

    private static final ElementaryMetaPath getMetaPath(final Class<? extends Edge> edgeClass, final Direction direction, final ConnectionState connectionState) {
        ElementaryMetaPath[] metaPathes = EDGE_CLASS_TO_FORWARD_AND_BACKWARD_METAPATHES.get(edgeClass);
        if (metaPathes == null) {
            boolean isDoubleMeaningEdge = ModelConstants.isDoubleMeaningEdge(edgeClass);
            //Kanten mit doppelter Bedeutung haben für jeden ConnectionState (null, FORWARD, BACKWARD, DOUBLE) und jede Richtung (FORWARD,
            //BACKWARD) je einen Elementarmetapfad mit eigener Bedeutung. Alle anderen haben nur für jede Richtung eine Bedeutung.
            //Index des Elementarpfades ergibt sich aus dem ConnectionState = connectionState == null ? 0 : connectionState.ordinal() + 1
            metaPathes = new ElementaryMetaPath[isDoubleMeaningEdge ? 4 : 1];
            metaPathes[0] = new ElementaryMetaPath(edgeClass, Direction.FORWARD); //0 = Index des Pfades = Direction.FORWARD.ordinal(). Das hier entspricht bei DoubleMeaningEdges dem ConnectionState.null
            if (isDoubleMeaningEdge) {
                metaPathes[1] = new ElementaryMetaPath(edgeClass, Direction.FORWARD, ConnectionState.FORWARD);
                metaPathes[2] = new ElementaryMetaPath(edgeClass, Direction.FORWARD, ConnectionState.BACKWARD);
                metaPathes[3] = new ElementaryMetaPath(edgeClass, Direction.FORWARD, ConnectionState.DOUBLE);
            }
            EDGE_CLASS_TO_FORWARD_AND_BACKWARD_METAPATHES.put(edgeClass, metaPathes);
        }
        int metaPathIndex = connectionState == null ? 0 : connectionState.ordinal() + 1;
        //je nach Richtung den Backward-Pfad zurück geben
        ElementaryMetaPath returnPath = direction != Direction.FORWARD ? metaPathes[metaPathIndex].getOtherDirection() : metaPathes[metaPathIndex];
        return returnPath;
    }

    /**
     * Gibt einen ElementaryMetaPath zurück, der bis auf die Start- und Zielklasse identisch ist mit dem übergebenen Elementarpfad. Sind die Start-
     * und Zielklassen dieselben wie beim übergebenen Elementarpfad, so kommt dieser unverändert zurück.
     *
     * @param startClass
     * @param originalElementaryMetaPath
     * @param endClass
     */
    public static final ElementaryMetaPath getMetaPath(final Class<? extends ModelElement> startClass, final ElementaryMetaPath originalElementaryMetaPath, final Class<? extends ModelElement> endClass) {
        Class<? extends ModelElement> originalStartClass = originalElementaryMetaPath.getStartClass();
        Class<? extends ModelElement> originalEndClass = originalElementaryMetaPath.getEndClass();
        if (startClass == originalStartClass && endClass == originalEndClass) {
            return originalElementaryMetaPath;
        }
        return new ElementaryMetaPath(startClass, originalElementaryMetaPath, endClass);
    }

    /**
     * Gibt einen ElementaryMetaPath zurück, der bis auf die Startklasse identisch ist mit dem übergebenen Elementarpfad. Ist die Startklasse
     * dieselben wie beim übergebenen Elementarpfad, so kommt dieser unverändert zurück.
     *
     * @param startClass
     * @param originalElementaryMetaPath
     * @return
     */
    public static final ElementaryMetaPath getMetaPath(final Class<? extends ModelElement> startClass, final ElementaryMetaPath originalElementaryMetaPath) {
        return getMetaPath(startClass, originalElementaryMetaPath, originalElementaryMetaPath.getEndClass());
    }

    /**
     * Gibt einen ElementaryMetaPath zurück, der bis auf die Endklasse identisch ist mit dem übergebenen Elementarpfad. Ist die Endklasse
     * dieselben wie beim übergebenen Elementarpfad, so kommt dieser unverändert zurück.
     *
     * @param originalElementaryMetaPath
     * @param endClass
     * @return
     */
    public static final ElementaryMetaPath getMetaPath(final ElementaryMetaPath originalElementaryMetaPath, final Class<? extends ModelElement> endClass) {
        return getMetaPath(originalElementaryMetaPath.getStartClass(), originalElementaryMetaPath, endClass);
    }

    /**
     * @param startClass
     * @param edgeClass
     * @param direction
     * @return
     */
    public static final ElementaryMetaPath getMetaPath(final Class<? extends ModelElement> startClass, final Class<? extends Edge> edgeClass, final Direction direction) {
        ElementaryMetaPath metaPath = getMetaPath(edgeClass, direction);
        return getMetaPath(startClass, metaPath, metaPath.getEndClass());
    }

    /**
     * @param edgeClass
     * @param direction
     * @param endClass
     * @return
     */
    public static final ElementaryMetaPath getMetaPath(final Class<? extends Edge> edgeClass, final Direction direction, final Class<? extends ModelElement> endClass) {
        ElementaryMetaPath metaPath = getMetaPath(edgeClass, direction);
        return getMetaPath(metaPath.getStartClass(), metaPath, endClass);
    }

    /**
     * @param edgeClass
     * @return
     */
    public static final ElementaryMetaPath getForwardMetaPath(final Class<? extends Edge> edgeClass) {
        return getMetaPath(edgeClass, Direction.FORWARD);
    }

    //    /**
    //     * @param edgeClass
    //     * @return
    //     */
    //    public static final ElementaryMetaPath getBackwardMetaPath(final Class<? extends Edge> edgeClass) {
    //        return getMetaPath(edgeClass, Direction.BACKWARD);
    //    }

    /**
     * @param doubleMeaningEdgeClass
     * @param connectionState
     * @return
     */
    public static final ElementaryMetaPath getForwardMetaPath(final Class<? extends DoubleMeaningEdge> doubleMeaningEdgeClass, final ConnectionState connectionState) {
        return getMetaPath(doubleMeaningEdgeClass, Direction.FORWARD, connectionState);
    }

    //    /**
    //     * @param doubleMeaningEdgeClass
    //     * @param connectionState
    //     * @return
    //     */
    //    public static final ElementaryMetaPath getBackwardMetaPath(final Class<? extends DoubleMeaningEdge> doubleMeaningEdgeClass, final ConnectionState connectionState) {
    //        return getMetaPath(doubleMeaningEdgeClass, Direction.BACKWARD, connectionState);
    //    }
    //
    //    public static final ElementaryMetaPath _eF(final Class<? extends Edge> edgeClass) {
    //        return getMetaPath(edgeClass, Direction.FORWARD);
    //    }
    //
    //    public static final ElementaryMetaPath _eB(final Class<? extends Edge> edgeClass) {
    //        return getMetaPath(edgeClass, Direction.BACKWARD);
    //    }
    //
    //    public static final ElementaryMetaPath _eFF(final Class<? extends DoubleMeaningEdge> doubleMeaningEdgeClass) {
    //        return getMetaPath(doubleMeaningEdgeClass, Direction.FORWARD, ConnectionState.FORWARD);
    //    }
    //
    //    public static final ElementaryMetaPath _eBF(final Class<? extends DoubleMeaningEdge> doubleMeaningEdgeClass) {
    //        return getMetaPath(doubleMeaningEdgeClass, Direction.BACKWARD, ConnectionState.FORWARD);
    //    }
    //
    //    public static final ElementaryMetaPath _eFB(final Class<? extends DoubleMeaningEdge> doubleMeaningEdgeClass) {
    //        return getMetaPath(doubleMeaningEdgeClass, Direction.FORWARD, ConnectionState.BACKWARD);
    //    }
    //
    //    public static final ElementaryMetaPath _eBB(final Class<? extends DoubleMeaningEdge> doubleMeaningEdgeClass) {
    //        return getMetaPath(doubleMeaningEdgeClass, Direction.BACKWARD, ConnectionState.BACKWARD);
    //    }
    //
    //    public static final ElementaryMetaPath _eFD(final Class<? extends DoubleMeaningEdge> doubleMeaningEdgeClass) {
    //        return getMetaPath(doubleMeaningEdgeClass, Direction.FORWARD, ConnectionState.DOUBLE);
    //    }
    //
    //    public static final ElementaryMetaPath _eBD(final Class<? extends DoubleMeaningEdge> doubleMeaningEdgeClass) {
    //        return getMetaPath(doubleMeaningEdgeClass, Direction.BACKWARD, ConnectionState.DOUBLE);
    //    }

}
