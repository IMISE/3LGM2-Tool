package de.imise.tool3lgm.graphtools.path.metapaths;

import java.util.HashMap;
import java.util.Map;

import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge.ConnectionState;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.metapaths.ElementaryMetaPath.Type;
import de.imise.util.ReflectionUtils;

/**
 * Dieser Handller merkt sich alle definierten Elementarpfade.
 *
 * @author AXS (5 Dec 2018)
 */
public final class ElementaryMetaPathHandler {

    /** Das MetaModel, für das dieser Handler die Pfade verwaltet */
    private final MetaModel metaModel;

    /** Platzhaltermetapfad für die Definition einer beliebigen Verbindung z. B. in einem {@link SequenceMetaPath} */
    public final ElementaryMetaPath generalElementarySuperPath;

    /**
     * Mappt von einer Edgenklasse auf ein 2-elementiges Array von MetaPathes, wobei der erste MetaPath
     * im Array der zur Edge gehörige Metapath in Richtung Edge.Direction.FORWARD und der zweite in
     * Richtung Edge.Direction.BACKWARD ist.
     */
    private static final Map<Class<? extends Edge>, ElementaryMetaPath[]> EDGE_CLASS_TO_FORWARD_AND_BACKWARD_METAPATHES = new HashMap<>();

    /**
     * @param metaModel
     */
    public ElementaryMetaPathHandler(final MetaModel metaModel) {
        this.metaModel = metaModel;
        generalElementarySuperPath = new ElementaryMetaPath(metaModel, Edge.class, Direction.FORWARD);
    }

    /**
     * Liefert für eine Edge den dazugehörigen ElementarMetaPfad. Wenn der Rückgabepfad noch nicht in der Map für die
     * Vorwärts- und Rückwärtsrichtung der Elementarpfade enthalten ist, dann wird er hinzugefügt.
     *
     * @param edgeClass
     * @param direction
     * @return
     */
    public final ElementaryMetaPath getMetaPath(final Class<? extends Edge> edgeClass, final Direction direction) {
        return getMetaPath(edgeClass, direction, (ConnectionState) null);
    }

    /**
     * @param edgeClass
     * @param direction
     * @param connectionState
     * @return
     */
    private final ElementaryMetaPath getMetaPath(final Class<? extends Edge> edgeClass, final Direction direction, final ConnectionState connectionState) {
        ElementaryMetaPath[] metaPathes = EDGE_CLASS_TO_FORWARD_AND_BACKWARD_METAPATHES.get(edgeClass);
        if (metaPathes == null) {
            boolean isDoubleMeaningEdge = MetaModel.isDoubleMeaningEdge(edgeClass);
            //Kanten mit doppelter Bedeutung haben für jeden ConnectionState (null, FORWARD, BACKWARD, DOUBLE) und jede Richtung (FORWARD,
            //BACKWARD) je einen Elementarmetapfad mit eigener Bedeutung. Alle anderen haben nur für jede Richtung eine Bedeutung.
            //Index des Elementarpfades ergibt sich aus dem ConnectionState = connectionState == null ? 0 : connectionState.ordinal() + 1
            metaPathes = new ElementaryMetaPath[isDoubleMeaningEdge ? 4 : 1];
            metaPathes[0] = new ElementaryMetaPath(metaModel, edgeClass, Direction.FORWARD); //0 = Index des Pfades = Direction.FORWARD.ordinal(). Das hier entspricht bei DoubleMeaningEdges dem ConnectionState.null
            if (isDoubleMeaningEdge) {
                metaPathes[1] = new ElementaryMetaPath(metaModel, edgeClass, Direction.FORWARD, ConnectionState.FORWARD);
                metaPathes[2] = new ElementaryMetaPath(metaModel, edgeClass, Direction.FORWARD, ConnectionState.BACKWARD);
                metaPathes[3] = new ElementaryMetaPath(metaModel, edgeClass, Direction.FORWARD, ConnectionState.DOUBLE);
            }
            EDGE_CLASS_TO_FORWARD_AND_BACKWARD_METAPATHES.put(edgeClass, metaPathes);
        }
        int metaPathIndex = connectionState == null ? 0 : connectionState.ordinal() + 1;
        //je nach Richtung den Backward-Pfad zurück geben
        ElementaryMetaPath returnPath = direction != Direction.FORWARD ? metaPathes[metaPathIndex].getOtherDirection() : metaPathes[metaPathIndex];
        return returnPath;
    }

    /**
     * @param startClass
     * @param edgeClass
     * @return
     */
    public final ElementaryMetaPath getMetaPath(final Class<? extends ModelElement> startClass, final Class<? extends Edge> edgeClass) {
        Direction direction = MetaModel.isStartClass(edgeClass, startClass) ? Direction.FORWARD : Direction.BACKWARD;
        ElementaryMetaPath returnMetaPath = getMetaPath(startClass, edgeClass, direction);
        return returnMetaPath;
    }

    /**
     * Gibt einen ElementaryMetaPath zurück, der bis auf die Start- und Zielklasse identisch ist mit dem übergebenen Elementarpfad. Sind die Start-
     * und Zielklassen dieselben wie beim übergebenen Elementarpfad, so kommt dieser unverändert zurück. Startklasse des zurück gegebenen Pfades ist
     * die speziellere Klasse aus der übergebenen startClass und der Startklasse des übergebenen Elementarpfades. Endklasse analog.
     *
     * @param startClass
     * @param originalMetaPath
     * @param endClass
     */
    public final ElementaryMetaPath getMetaPath(final Class<? extends ModelElement> startClass, final ElementaryMetaPath originalMetaPath, final Class<? extends ModelElement> endClass) {
        Class<? extends ModelElement> originalMetaPathStartClass = originalMetaPath.getStartClass();
        Class<? extends ModelElement> originalMetaPathEndClass = originalMetaPath.getEndClass();
        Class<? extends ModelElement> realStartClass = startClass == null ? originalMetaPathEndClass : ReflectionUtils.getMostSpecialClass(startClass, originalMetaPathStartClass);
        Class<? extends ModelElement> realEndClass = endClass == null ? null : ReflectionUtils.getMostSpecialClass(endClass, originalMetaPathEndClass);
        if (originalMetaPathStartClass == realStartClass && originalMetaPathEndClass == realEndClass) {
            return originalMetaPath;
        }
        return new ElementaryMetaPath(metaModel, realStartClass, originalMetaPath, realEndClass);
    }

    /**
     * Gibt einen ElementaryMetaPath zurück, der bis auf die Startklasse identisch ist mit dem übergebenen Elementarpfad. Ist die Startklasse
     * dieselben wie beim übergebenen Elementarpfad, so kommt dieser unverändert zurück.
     *
     * @param startClass
     * @param originalElementaryMetaPath
     * @return
     */
    public final ElementaryMetaPath getMetaPath(final Class<? extends ModelElement> startClass, final ElementaryMetaPath originalElementaryMetaPath) {
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
    public final ElementaryMetaPath getMetaPath(final ElementaryMetaPath originalElementaryMetaPath, final Class<? extends ModelElement> endClass) {
        return getMetaPath(originalElementaryMetaPath.getStartClass(), originalElementaryMetaPath, endClass);
    }

    /**
     * @param startClass
     * @param edgeClass
     * @param direction
     * @return
     */
    public final ElementaryMetaPath getMetaPath(final Class<? extends ModelElement> startClass, final Class<? extends Edge> edgeClass, final Direction direction) {
        ElementaryMetaPath metaPath = getMetaPath(edgeClass, direction);
        return getMetaPath(startClass, metaPath, metaPath.getEndClass());
    }

    /**
     * @param edgeClass
     * @param direction
     * @param endClass
     * @return
     */
    public final ElementaryMetaPath getMetaPath(final Class<? extends Edge> edgeClass, final Direction direction, final Class<? extends ModelElement> endClass) {
        ElementaryMetaPath metaPath = getMetaPath(edgeClass, direction);
        return getMetaPath(metaPath.getStartClass(), metaPath, endClass);
    }

    /**
     * @param startClass
     * @param edgeClass
     * @param direction
     * @param endClass
     * @return
     */
    public final ElementaryMetaPath getMetaPath(final Class<? extends ModelElement> startClass, final Class<? extends Edge> edgeClass, final Direction direction, final Class<? extends ModelElement> endClass) {
        ElementaryMetaPath metaPath = getMetaPath(edgeClass, direction);
        return getMetaPath(startClass, metaPath, endClass);
    }

    /**
     * @param startClass
     * @param edgeClass
     * @param direction
     * @param connectionState
     * @param endClass
     * @return
     */
    public final ElementaryMetaPath getMetaPath(final Class<? extends ModelElement> startClass, final Class<? extends DoubleMeaningEdge> edgeClass, final Direction direction, final ConnectionState connectionState,
            final Class<? extends ModelElement> endClass) {
        ElementaryMetaPath metaPath = getMetaPath(edgeClass, direction, connectionState);
        return getMetaPath(startClass, metaPath, endClass);
    }

    /**
     * @param edgeClass
     * @return
     */
    public final ElementaryMetaPath getForwardMetaPath(final Class<? extends Edge> edgeClass) {
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
    public final ElementaryMetaPath getForwardMetaPath(final Class<? extends DoubleMeaningEdge> doubleMeaningEdgeClass, final ConnectionState connectionState) {
        return getMetaPath(doubleMeaningEdgeClass, Direction.FORWARD, connectionState);
    }

    /**
     * Liefert einen Elementarpfad ausgehend von der Start- hin zur Endklasse verläuft. Dabei ist die Kantenklasse die speziellste gemeinsame
     * Oberklasse aller Kantenklasse, die zwischen Start- und Endklasse liegen.
     *
     * @param startClass
     * @param endClass
     * @return
     */
    public final ElementaryMetaPath _getForwardMetaPath(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass) {
        Class<? extends Edge>[] edgeTypes = metaModel.getEdgeTypes(startClass, endClass);
        Class<? extends Edge> commonSuperClass = null;
        for (Class<? extends Edge> edgeClass : edgeTypes) {
            if (MetaModel.isConnectingForward(edgeClass, startClass, endClass)) {
                if (commonSuperClass == null) {
                    commonSuperClass = edgeClass;
                } else {
                    commonSuperClass = ReflectionUtils.getCommonSuperClassOfClasses(commonSuperClass, edgeClass);
                }
            }
        }
        ElementaryMetaPath elementaryMetaPath = null;
        if (commonSuperClass != null) {
            elementaryMetaPath = getForwardMetaPath(commonSuperClass);
            elementaryMetaPath = getMetaPath(startClass, elementaryMetaPath, endClass);
        }
        return elementaryMetaPath;
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

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Factory-Funktionen zum Erzeugen von ElemenatrMetaPfaden, die von Kanten zu einem ihrer Enden laufen //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Edge to End (Direction = BACKWARD) = START_WITH_EDGE: edgeClass -> edgeClass -> BACKWARD -> startClass

    /**
     * @param edgeClass
     * @return
     */
    public final ElementaryMetaPath getEdgeToStartElementMetaPath(final Class<? extends Edge> edgeClass) {
        return getEdgeToStartElementMetaPath(edgeClass, Edge.getStartClass(edgeClass));
    }

    /**
     * @param edgeClass
     * @param edgeStartClassAsPathEndClass
     * @return
     */
    public final ElementaryMetaPath getEdgeToStartElementMetaPath(final Class<? extends Edge> edgeClass, Class<? extends ModelElement> edgeStartClassAsPathEndClass) {
        if (edgeStartClassAsPathEndClass == null) {
            edgeStartClassAsPathEndClass = Edge.getStartClass(edgeClass);
        } else {
            edgeStartClassAsPathEndClass = ReflectionUtils.getMostSpecialClass(edgeStartClassAsPathEndClass, Edge.getStartClass(edgeClass));
        }
        return new ElementaryMetaPath(metaModel, edgeClass, edgeClass, edgeStartClassAsPathEndClass, Direction.BACKWARD, null, Type.START_WITH_EDGE);
    }

    // Edge to End (Direction = FORWARD) = START_WITH_EDGE: edgeClass -> edgeClass -> FORWARD -> endClass

    /**
     * @param edgeClass
     * @return
     */
    public final ElementaryMetaPath getEdgeToEndElementMetaPath(final Class<? extends Edge> edgeClass) {
        return getEdgeToEndElementMetaPath(edgeClass, null);
    }

    /**
     * @param edgeClass
     * @param edgeEndClassAsPathEndClass
     * @return
     */
    public final ElementaryMetaPath getEdgeToEndElementMetaPath(final Class<? extends Edge> edgeClass, Class<? extends ModelElement> edgeEndClassAsPathEndClass) {
        if (edgeEndClassAsPathEndClass == null) {
            edgeEndClassAsPathEndClass = Edge.getEndClass(edgeClass);
        } else {
            edgeEndClassAsPathEndClass = ReflectionUtils.getMostSpecialClass(edgeEndClassAsPathEndClass, Edge.getEndClass(edgeClass));
        }
        return new ElementaryMetaPath(metaModel, edgeClass, edgeClass, edgeEndClassAsPathEndClass, Direction.FORWARD, null, Type.START_WITH_EDGE);
    }

    // Start to Edge (Direction = FORWARD) = END_WITH_EDGE: startClass -> edgeClass -> FORWARD -> edgeClass

    /**
     * @param edgeClass
     * @return
     */
    public final ElementaryMetaPath getStartElementToEdgeMetaPath(final Class<? extends Edge> edgeClass) {
        return getStartElementToEdgeMetaPath(null, edgeClass);
    }

    /**
     * @param edgeStartClassAsPathStartClass
     * @param edgeClass
     * @return
     */
    public final ElementaryMetaPath getStartElementToEdgeMetaPath(Class<? extends ModelElement> edgeStartClassAsPathStartClass, final Class<? extends Edge> edgeClass) {
        if (edgeStartClassAsPathStartClass == null) {
            edgeStartClassAsPathStartClass = Edge.getStartClass(edgeClass);
        } else {
            edgeStartClassAsPathStartClass = ReflectionUtils.getMostSpecialClass(edgeStartClassAsPathStartClass, Edge.getStartClass(edgeClass));
        }
        return new ElementaryMetaPath(metaModel, edgeStartClassAsPathStartClass, edgeClass, edgeClass, Direction.FORWARD, null, Type.END_WITH_EDGE);
    }

    // End to Edge (Direction = BACKWARD) = END_WITH_EDGE: endClass -> edgeClass -> BACKWARD -> edgeClass

    /**
     * @param edgeClass
     * @return
     */
    public final ElementaryMetaPath getEndElementToEdgeMetaPath(final Class<? extends Edge> edgeClass) {
        return getEndElementToEdgeMetaPath(edgeClass, null);
    }

    /**
     * @param edgeEndClassAsPathStartClass
     * @param edgeClass
     * @return
     */
    public final ElementaryMetaPath getEndElementToEdgeMetaPath(Class<? extends ModelElement> edgeEndClassAsPathStartClass, final Class<? extends Edge> edgeClass) {
        if (edgeEndClassAsPathStartClass == null) {
            edgeEndClassAsPathStartClass = Edge.getEndClass(edgeClass);
        } else {
            edgeEndClassAsPathStartClass = ReflectionUtils.getMostSpecialClass(edgeEndClassAsPathStartClass, Edge.getEndClass(edgeClass));
        }
        return new ElementaryMetaPath(metaModel, edgeEndClassAsPathStartClass, edgeClass, edgeClass, Direction.BACKWARD, null, Type.END_WITH_EDGE);
    }

}
