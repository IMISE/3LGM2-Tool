package de.imise.tool3lgm.graphtools.path;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.path.meta.AbstractMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.ElementaryMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.SequenceMetaPath;
import de.imise.tool3lgm.graphtools.path.pathmodel.ElementaryPath;
import de.imise.tool3lgm.graphtools.path.pathmodel.PathResultTreeModel;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty;

/**
 * @author AXS
 * @create 25.07.2011
 */
public class MetaPathFunctions {

    /**
     * Status der möglichen Verbindungen über einen MetaPfad zwischen 2 Elementen.
     * <ul>
     * <li><code>SELF</code>: Das Element hat selbst die Verbindung zu mind. einem EndElement</li>
     * <li><code>PARENT</code>: Ein Parent des Elements hat die Verbindung zu mind. einem EndElement</li>
     * <li><code>PART</code>: Ein Part des Elements hat die Verbindung zu mind. einem EndElement</li>
     * <li><code>SELF_PARENT</code>: Das Element selbst und einer seiner Parents hat die Verbindung zu mind. einem EndElement</li>
     * <li><code>SELF_PART</code>: Das Element selbst und einer seiner Parts hat die Verbindung zu mind. einem EndElement</li>
     * <li><code>PARENT_PART</code>: Mind. ein Part und mind. ein Parent, aber nicht das Element selbst hat die Verbindung zu mind. einem
     * EndElement</li>
     * <li><code>SELF_PARENT_PART</code>: Sowohl das Element selbst als auch mind. ein Part und mind. ein Parent hat die Verbindung zu mind. einem
     * EndElement</li>
     * </ul>
     */
    public static enum PathConnectionState {
        SELF,
        PARENT,
        PART,
        SELF_PARENT,
        SELF_PART,
        PARENT_PART,
        SELF_PARENT_PART
    }

    /**
     * Liefert einen Ergebnisbaum, der alle eventuell vorhandenen Pfade ausgehend vom
     * übergebenen Element aufspannt
     *
     * @param startElement
     * @param metaPath
     * @return
     */
    public static final PathResultTreeModel getResultTree(final ModelElement startElement, final AbstractMetaPath metaPath) {
        return new PathResultTreeModel(metaPath, startElement);
    }

    /**
     * @param startElements
     * @param metaPath
     * @return
     */
    public static final PathResultTreeModel getResultTree(final Collection<ModelElement> startElements, final AbstractMetaPath metaPath) {
        return new PathResultTreeModel(metaPath, startElements);
    }

    /**
     * @param startElements
     * @param metaPath
     * @return
     */
    public static final PathResultTreeModel getResultTree(final List<Collection<ModelElement>> startElements, final AbstractMetaPath metaPath) {
        return new PathResultTreeModel(metaPath, startElements);
    }

    /**
     * Liefert alle mit dem übergebenen Element über diesen MetaPfad verbundenen Elemente.
     *
     * @param me
     *            Ausgangselement
     * @param metaPath
     * @param multiple
     *            Wenn <code>true</code> sind mehrfach verbundene Element auch mehrfach in der Ergebnisliste, bei <code>false</code> ist jedes Element
     *            nur einmal enthalten.
     * @return
     */
    public static final Collection<ModelElement> getConnectedElements(final ModelElement me, final AbstractMetaPath metaPath, final boolean multiple) {
        return getResultTree(me, metaPath).getConnectedElements(multiple);
    }

    /**
     * @param me
     * @param metaPath
     * @return
     */
    public static final Collection<ModelElement> getConnectedElements(final ModelElement me, final AbstractMetaPath metaPath) {
        return getConnectedElements(me, metaPath, false);
    }

    /**
     * Liefert eine Sammlung aller Elemente, die über diesen Pfad mit den übergebenen Elementen verbunden sind.
     *
     * @param modelElements
     * @param metaPath
     * @return
     */
    public static final Collection<ModelElement> getConnectedElements(final Collection<ModelElement> modelElements, final AbstractMetaPath metaPath) {
        return getConnectedElements(modelElements, metaPath, false);
    }

    /**
     * Liefert eine Sammlung aller Elemente, die über diesen Pfad mit den übergebenen Elementen verbunden sind.
     *
     * @param modelElements
     *            Ausgangselemente
     * @param metaPath
     * @param multiple
     *            Wenn <code>true</code> enthält die Rückgabesammlung dieselben Elemente sooft, wie sie mit Elementen der
     *            Ausgangliste über diesen Pfad verbunden sind. Bei <code>false</code> ist jedes Element nur einmal enthalten.
     * @return
     */
    private static final Collection<ModelElement> getConnectedElements(final Collection<ModelElement> modelElements, final AbstractMetaPath metaPath, final boolean multiple) {
        return getResultTree(modelElements, metaPath).getConnectedElements(multiple);
    }

    /**
     * Testet, ob das Modelelement this eine Edge der übergebenen Art haben haben kann,
     * wobei this das Startelement der Edge sein muss.
     *
     * @param edgeClass
     * @param testCardinality
     *            wenn <code>true</code>, wird auch noch getestet, ob die maximale Kardinalität der Verbindungen bereits erreicht ist
     * @return
     *         /
     *         public final boolean isForwardLinkable(Class<? extends Edge> edgeClass, boolean testCardinality) {
     *         //thsi muss Startklasse der Edge sein
     *         if (!Edge.isStartClass(edgeClass, getClass()))
     *         return false;
     *         //wenn das überschreiten der Kardinalität geprüft werden soll
     *         if (testCardinality){
     *         //für das Startelement ist die maximale Verbindungsanzahl bereits erreicht?
     *         if (Edge.getMaxStartToEndCardinality(edgeClass) <= countStartConnections(edgeClass))
     *         return false;
     *         }
     *         return true;
     *         }
     *         /**
     *         Testet, ob das Modelelement this eine Edge der übergebenen Art haben haben kann,
     *         wobei this das Endelement der Edge sein muss.
     * @param edgeClass
     * @param testCardinality
     *            wenn <code>true</code>, wird auch noch getestet, ob die maximale Kardinalität der Verbindungen bereits erreicht ist
     * @return
     *         /
     *         public final boolean isBackwardLinkable(Class<? extends Edge> edgeClass, boolean testCardinality) {
     *         //thsi muss Startklasse der Edge sein
     *         if (!Edge.isStartClass(edgeClass, getClass()))
     *         return false;
     *         //wenn das überschreiten der Kardinalität geprüft werden soll
     *         if (testCardinality){
     *         //für das Startelement ist die maximale Verbindungsanzahl bereits erreicht?
     *         if (Edge.getMaxEndToStartCardinality(edgeClass) <= countEndConnections(edgeClass))
     *         return false;
     *         }
     *         return true;
     *         }
     */

    /**
     * Liefert true, wenn für das übergebene Element der übergebene Pfad angelegt werden kann, ohne gegen die Konsistenz zu verstoßen
     *
     * @param modelElement
     *            Element, für das der Pfad angelegt werden soll
     * @param metaPath
     *            Elementarpfad, der angelegt werden soll
     * @param modelElementAsStartElement
     *            <code>true</code>, wenn das übergebene Element als Startelement getestet werden soll, <code>false</code>, wenn es Endelement sein
     *            soll
     * @return
     */
    private static final boolean isCreateable(final ModelElement modelElement, final ElementaryMetaPath metaPath, final boolean modelElementAsStartElement) {
        if (!metaPath.isCreateable()) {
            return false;
        }
        if (modelElementAsStartElement) {
            if (!metaPath.isStartClass(modelElement.getClass(), true, false)) {
                return false;
            }
        } else {
            if (!metaPath.isEndClass(modelElement.getClass(), true, false)) {
                return false;
            }
        }
        if (metaPath.getDirection() == Direction.FORWARD) {
            //für das Startelement ist die maximale Verbindungsanzahl bereits erreicht?
            if (Edge.getMaxForwardCardinality(metaPath.getEdgeClass()) <= modelElement.countStartConnections(metaPath.getEdgeClass())) {
                return false;
            }
        } else if (metaPath.getDirection() == Direction.BACKWARD) {
            //für das Endelement ist die maximale Verbindungsanzahl bereits erreicht?
            if (Edge.getMaxBackwardCardinality(metaPath.getEdgeClass()) <= modelElement.countEndConnections(metaPath.getEdgeClass())) {
                return false;
            }
        }
        return false;
    }

    /**
     * Liefert <code>true</code>, wenn sich der übergebene MetaPfad mit allen Zwischenelementen zwischen den
     * übergebenen Modellelementen ohne Konsistenzprobleme anlegen lässt.
     *
     * @param startElement
     * @param endElement
     * @param metaPath
     * @param checkConsistency
     * @return
     */
    public static final boolean isCreateable(final ModelElement startElement, final ModelElement endElement, final AbstractMetaPath metaPath, final boolean checkConsistency) {
        if (!metaPath.isCreateable()) {
            return false;
        }
        if (metaPath instanceof ElementaryMetaPath) {
            ElementaryMetaPath elemMetaPath = (ElementaryMetaPath) metaPath;
            if (elemMetaPath.getDirection() == Direction.FORWARD) {
                return startElement.isForwardLinkable(endElement, elemMetaPath.getEdgeClass(), checkConsistency);
            }
            return endElement.isForwardLinkable(startElement, elemMetaPath.getEdgeClass(), checkConsistency);
        } else if (metaPath instanceof SequenceMetaPath) {
            SequenceMetaPath sequenceMetaPath = (SequenceMetaPath) metaPath;
            AbstractMetaPath firstMetaPath = sequenceMetaPath.getMetaPaths().get(0);
            while (firstMetaPath instanceof SequenceMetaPath) {
                firstMetaPath = ((SequenceMetaPath) firstMetaPath).getMetaPaths().get(0);
            }
            if (!(firstMetaPath instanceof ElementaryMetaPath)) {
                return false;
            }
            AbstractMetaPath lastMetaPath = sequenceMetaPath.getMetaPaths().get(sequenceMetaPath.getMetaPaths().size() - 1);
            while (lastMetaPath instanceof SequenceMetaPath) {
                List<AbstractMetaPath> lastSequenceMetaPathList = ((SequenceMetaPath) lastMetaPath).getMetaPaths();
                lastMetaPath = lastSequenceMetaPathList.get(lastSequenceMetaPathList.size() - 1);
            }
            if (!(lastMetaPath instanceof ElementaryMetaPath)) {
                return false;
            }
            if (firstMetaPath == lastMetaPath) {
                return isCreateable(startElement, endElement, lastMetaPath, checkConsistency);
            }

            if (!isCreateable(startElement, (ElementaryMetaPath) firstMetaPath, true)) {
                return false;
            }
            if (!isCreateable(endElement, (ElementaryMetaPath) lastMetaPath, false)) {
                return false;
            }

            //TODO: anlegen von Sequenzmetapathes richtig implementieren
            /*
             * Das hier ist nicth fertig. Hier müsste man jetzt noch für jedes Zwischenelement testen, ob es nach dem
             * Anlegen zu allen Elementen genügend Edgen hat. Initialsubtypes müssen auch beachtet werden usw.
             * Das ist aber erstmal nicht so wichtig
             * if (checkConsistency) {
             * List<AbstractMetaPath> metaPaths = sequenceMetaPath.getMetaPaths();
             * for (int i = 0; i < metaPaths.size(); i++) {
             * AbstractMetaPath mp = metaPaths.get(i);
             * //prüfen, ob die Zwischenelemente angelegt werden können
             * if (i + 1 < metaPaths.size()) {
             * AbstractMetaPath nextMetaPath = metaPaths.get(i + 1);
             * boolean createableMetaPathEndClass = mp.getEndClasses().size() == 1;
             * boolean createableNextMetaPathStartClass = nextMetaPath.getStartClasses().size() == 1;
             * //Keine eindeutige Folgeklasse
             * if (!createableMetaPathEndClass && !createableNextMetaPathStartClass)
             * return false;
             * if (createableMetaPathEndClass && Modifier.isAbstract(mp.getEndClasses().get(0).getModifiers()))
             * createableMetaPathEndClass = false;
             * if (createableNextMetaPathStartClass && Modifier.isAbstract(nextMetaPath.getStartClasses().get(0).getModifiers()))
             * createableNextMetaPathStartClass = false;
             * if (!createableMetaPathEndClass && !createableNextMetaPathStartClass)
             * return false;
             * }
             * }
             * }
             */
            return true;
        }
        return false;
    }

    /**
     * @param startElement
     * @param endElement
     * @param metaPath
     * @return
     */
    public static final boolean pathExists(final ModelElement startElement, final ModelElement endElement, final AbstractMetaPath metaPath) {
        //TODO: implementieren
        if (true) {
            throw new Error("TODO: Implementieren");
        }
        return false;
    }

    /**
     * Liefert <code>true</code>, wenn der übergebene {@link PathConnectionState} angibt, dass das Element selbst mit einem
     * anderen Element verbunden ist und nicht nur seine Partents oder Parts.
     *
     * @param state
     * @return
     */
    public static final boolean isSelfConnected(final PathConnectionState state) {
        return state == PathConnectionState.SELF || state == PathConnectionState.SELF_PARENT || state == PathConnectionState.SELF_PART || state == PathConnectionState.SELF_PARENT_PART;
    }

    /**
     * @param startElement
     * @param endElement
     * @param metaPath
     * @return
     */
    public static final PathConnectionState getConnectionState(final ModelElement startElement, final ModelElement endElement, final AbstractMetaPath metaPath) {
        return getConnectionState(startElement, endElement, metaPath, UserProperties.is(BooleanProperty.OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARENTS), UserProperties.is(BooleanProperty.OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARTS));
    }

    /**
     * @param startElement
     * @param endElement
     * @param metaPath
     * @param searchParents
     * @param searchParts
     * @return
     */
    public static final PathConnectionState getConnectionState(final ModelElement startElement, final ModelElement endElement, final AbstractMetaPath metaPath, final boolean searchParents, final boolean searchParts) {
        List<ModelElement> startElements = null;
        startElements = new ArrayList<>(1);
        startElements.add(startElement);

        List<ModelElement> endElements = null;
        if (searchParts && searchParents) {
            endElements = endElement.getPartAndParentElements();
        } else if (searchParents) {
            endElements = endElement.getParentElements(true);
        } else if (searchParts) {
            endElements = endElement.getPartElements(true);
        } else {
            endElements = new ArrayList<>(1);
            endElements.add(endElement);
        }

        boolean self = isConnected(startElements, endElements, metaPath);
        boolean parent = false;
        boolean part = false;

        if (searchParents) {
            startElements = startElement.getParentElements(false);
            parent = isConnected(startElements, endElements, metaPath);
        }
        if (searchParts) {
            startElements = startElement.getPartElements(false);
            part = isConnected(startElements, endElements, metaPath);
        }
        if (self && parent && part) {
            return PathConnectionState.SELF_PARENT_PART;
        }
        if (self && parent) {
            return PathConnectionState.SELF_PARENT;
        }
        if (self && part) {
            return PathConnectionState.SELF_PART;
        }
        if (self) {
            return PathConnectionState.SELF;
        }
        if (parent && part) {
            return PathConnectionState.PARENT_PART;
        }
        if (parent) {
            return PathConnectionState.PARENT;
        }
        if (part) {
            return PathConnectionState.PART;
        }
        return null;
    }

    /**
     * @param startElement
     * @param endElement
     * @param metaPath
     * @return
     */
    public static final boolean isConnected(final ModelElement startElement, final ModelElement endElement, final AbstractMetaPath metaPath) {
        return getConnectionState(startElement, endElement, metaPath) != null;
    }

    /**
     * @param startElement
     * @param endElement
     * @param metaPath
     * @return
     */
    public static final boolean isDirectConnected(final ModelElement startElement, final ModelElement endElement, final AbstractMetaPath metaPath) {
        return getConnectionState(startElement, endElement, metaPath, false, false) != null;
    }

    /**
     * @param startElements
     * @param endElements
     * @param metaPath
     * @return
     */
    private static final boolean isConnected(final List<ModelElement> startElements, final List<ModelElement> endElements, final AbstractMetaPath metaPath) {
        Collection<ModelElement> connected = MetaPathFunctions.getConnectedElements(startElements, metaPath);
        for (ModelElement endElement : endElements) {
            if (connected.contains(endElement)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Achtung: Die Funktion geht davon aus, dass die aufrufende Funktion bereits eine Transaction gestartet hat. Hier wird keine Transaction
     * gestartet
     * oder beendet.
     *
     * @param startElement
     * @param endElement
     * @param metaPath
     * @param doc
     * @return
     */
    public static final ElementaryPath[] createPath(final ModelElement startElement, final ModelElement endElement, final AbstractMetaPath metaPath, final GraphDocument doc, final int pid) {
        //Achtung: der Pfad wird auch angelegt, wenn dadurch die Cardinalität von einigen Elementen verletzt wird! (das macht das false)
        if (!isCreateable(startElement, endElement, metaPath, false)) {
            return null;
        }
        GDCollection gdcoll = doc.getCollection();
        ElementaryPath[] returnPath = null;
        //Elementarpfad anlegen
        if (metaPath instanceof ElementaryMetaPath) {
            ElementaryMetaPath elemMetaPath = (ElementaryMetaPath) metaPath;
            Edge edge = null;
            if (elemMetaPath.getDirection() == Direction.FORWARD) {
                edge = gdcoll.link(elemMetaPath.getEdgeClass(), startElement, endElement, pid);
            } else {
                edge = gdcoll.link(elemMetaPath.getEdgeClass(), endElement, startElement, pid);
            }
            if (edge == null) {
                return null;
            }
            returnPath = new ElementaryPath[1];
            returnPath[0] = new ElementaryPath(startElement, endElement, edge, elemMetaPath);

            //Sequencepfad anlegen
        } else if (metaPath instanceof SequenceMetaPath) {
            //die Elementarmetapfade vom Metapfad holen
            List<ElementaryMetaPath> simpleMetaPath = metaPath.getElementaryMetaPaths();
            //wenn isCreateble() oben true liefert, sollte es diese Metapfadfolge eigentlich immer geben
            if (simpleMetaPath == null) {
                return null;
            }
            int pathLength = simpleMetaPath.size();
            if (pathLength == 1) {
                return createPath(startElement, endElement, simpleMetaPath.get(0), doc, pid);
            }
            //StartElement des ersten Pfades ist das übergebene StartElement
            ModelElement actualStartElement = startElement;
            //Liste aller tatsächlich angelegten Pfade
            returnPath = new ElementaryPath[pathLength];
            //alle MetaPfade durchlaufen und anlegen
            for (int i = 0; i < pathLength; i++) {
                ModelElement actualEndElement = null; //Endelement, das außer für die letzte Edge immer neu angelet werden muss
                Edge edge = null; //neu angelegte Edge
                ElementaryMetaPath elementaryMetaPath = simpleMetaPath.get(i);
                //wenn das noch nicht der letzte MetaPfad in der Liste ist
                if (i + 1 < pathLength) {
                    //neues Element für das EndElement des Pfades anlegen
                    NodeContainer nc = doc.createKnotenWithContainer(elementaryMetaPath.getEndClass(), pid);
                    if (nc == null) {
                        break;
                    }
                    actualEndElement = nc.getElement();
                    //beim letzten Metapfad ist das übergebene EndElement und kein neues das EndElement des Pfades
                } else {
                    actualEndElement = endElement;
                }
                //je nach Richtung des MetaPfades in der Collection die Edge anlegen
                if (elementaryMetaPath.getDirection() == Direction.FORWARD) {
                    edge = gdcoll.link(elementaryMetaPath.getEdgeClass(), actualStartElement, actualEndElement, pid);
                } else {
                    edge = gdcoll.link(elementaryMetaPath.getEdgeClass(), actualEndElement, actualStartElement, pid);
                }
                if (edge == null) {
                    break;
                }
                returnPath[i] = new ElementaryPath(actualStartElement, actualEndElement, edge, elementaryMetaPath);
                actualStartElement = actualEndElement;
            }
            //wenn nicht alle Verbindungen bis zur letzten angelegt wurden, dann alle angelegten zurückrollen
            if (returnPath[returnPath.length - 1] == null) {
                //einfach alle neuen Zwischenelemente löschen
                for (int i = 0; i < returnPath.length; i++) {
                    //beim ersten nicht mehr angelegten Pfad kann man abbrechen
                    if (returnPath[i] == null) {
                        break;
                    }
                    gdcoll.deleteElement(returnPath[i].getEndElement(), doc, pid);
                }
                returnPath = null;
            }
        }
        return returnPath;
    }

}
