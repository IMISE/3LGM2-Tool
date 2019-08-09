package de.imise.tool3lgm.graphtools.path;

import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction.BACKWARD;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction.FORWARD;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARENTS;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARTS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.CompositionEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.InstanciationEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.path.meta.AbstractMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.ElementaryMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.SequenceMetaPath;
import de.imise.tool3lgm.graphtools.path.pathmodel.ElementaryPath;
import de.imise.tool3lgm.graphtools.path.pathmodel.PathResultTreeModel;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.util.ReflectionUtils;

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
     * @param me
     * @param doc
     * @param metaPath
     * @param forlast
     * @return
     */
    public static final Collection<ElementContainer> getConnectedContainer(final ModelElement me, final GraphDocument doc, final AbstractMetaPath metaPath, final boolean forlast) {
        return getResultTree(me, metaPath).getConnectedContainer(doc, forlast);
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
    private static final boolean isCreatable(final ModelElement modelElement, final ElementaryMetaPath metaPath, final boolean modelElementAsStartElement) {
        if (!metaPath.isCreatable()) {
            return false;
        }
        if (modelElementAsStartElement) {
            if (!metaPath.isStartClass(modelElement.getClass())) {
                return false;
            }
        } else {
            if (!metaPath.isEndClass(modelElement.getClass())) {
                return false;
            }
        }
        MetaModel metaModel = metaPath.getMetaModel();
        if (metaPath.getDirection() == Direction.FORWARD) {
            //für das Startelement ist die maximale Verbindungsanzahl bereits erreicht?
            if (metaModel.getMaxForwardCardinality(metaPath.getEdgeClass()) <= modelElement.countStartConnections(metaPath.getEdgeClass())) {
                return false;
            }
        } else if (metaPath.getDirection() == Direction.BACKWARD) {
            //für das Endelement ist die maximale Verbindungsanzahl bereits erreicht?
            if (metaModel.getMaxBackwardCardinality(metaPath.getEdgeClass()) <= modelElement.countEndConnections(metaPath.getEdgeClass())) {
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
    public static final boolean isCreatable(final ModelElement startElement, final ModelElement endElement, final AbstractMetaPath metaPath, final boolean checkConsistency) {
        if (!metaPath.isCreatable()) {
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
                return isCreatable(startElement, endElement, lastMetaPath, checkConsistency);
            }

            if (!isCreatable(startElement, (ElementaryMetaPath) firstMetaPath, true)) {
                return false;
            }
            if (!isCreatable(endElement, (ElementaryMetaPath) lastMetaPath, false)) {
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
             * boolean creatableMetaPathEndClass = mp.getEndClasses().size() == 1;
             * boolean creatableNextMetaPathStartClass = nextMetaPath.getStartClasses().size() == 1;
             * //Keine eindeutige Folgeklasse
             * if (!creatableMetaPathEndClass && !creatableNextMetaPathStartClass)
             * return false;
             * if (creatableMetaPathEndClass && Modifier.isAbstract(mp.getEndClasses().get(0).getModifiers()))
             * creatableMetaPathEndClass = false;
             * if (creatableNextMetaPathStartClass && Modifier.isAbstract(nextMetaPath.getStartClasses().get(0).getModifiers()))
             * creatableNextMetaPathStartClass = false;
             * if (!creatableMetaPathEndClass && !creatableNextMetaPathStartClass)
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
        return getConnectionState(startElement, endElement, metaPath, OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARENTS.is(), OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARTS.is());
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
    public static final ElementaryPath[] _createPath(final ModelElement startElement, final ModelElement endElement, final AbstractMetaPath metaPath, final GraphDocument doc, final int pid) {
        //Achtung: der Pfad wird auch angelegt, wenn dadurch die Cardinalität von einigen Elementen verletzt wird! (das macht das false)
        if (!isCreatable(startElement, endElement, metaPath, false)) {
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
            returnPath[0] = new ElementaryPath(elemMetaPath, startElement, endElement, edge);

            //Sequencepfad anlegen
        } else if (metaPath instanceof SequenceMetaPath) {
            //die Elementarmetapfade vom Metapfad holen
            List<ElementaryMetaPath> simpleMetaPath = metaPath.getElementaryMetaPaths();
            //wenn isCreateble() oben true liefert, sollte es diese Metapfadfolge eigentlich immer geben
            if (simpleMetaPath.isEmpty()) {
                return null;
            }
            int pathLength = simpleMetaPath.size();
            if (pathLength == 1) {
                return _createPath(startElement, endElement, simpleMetaPath.get(0), doc, pid);
            }
            //StartElement des ersten Pfades ist das übergebene StartElement
            ModelElement currentStartElement = startElement;
            //Liste aller tatsächlich angelegten Pfade
            returnPath = new ElementaryPath[pathLength];
            //alle MetaPfade durchlaufen und anlegen
            for (int i = 0; i < pathLength; i++) {
                ModelElement currentEndElement = null; //Endelement, das außer für die letzte Edge immer neu angelet werden muss
                Edge edge = null; //neu angelegte Edge
                ElementaryMetaPath elementaryMetaPath = simpleMetaPath.get(i);
                //wenn das noch nicht der letzte MetaPfad in der Liste ist
                if (i + 1 < pathLength) {
                    //neues Element für das EndElement des Pfades anlegen
                    NodeContainer nc = doc.createNodeAndContainer(elementaryMetaPath.getEndClass(), pid);
                    if (nc == null) {
                        break;
                    }
                    currentEndElement = nc.getElement();
                    //beim letzten Metapfad ist das übergebene EndElement und kein neues das EndElement des Pfades
                } else {
                    currentEndElement = endElement;
                }
                //je nach Richtung des MetaPfades in der Collection die Edge anlegen
                if (elementaryMetaPath.getDirection() == Direction.FORWARD) {
                    edge = gdcoll.link(elementaryMetaPath.getEdgeClass(), currentStartElement, currentEndElement, pid);
                } else {
                    edge = gdcoll.link(elementaryMetaPath.getEdgeClass(), currentEndElement, currentStartElement, pid);
                }
                if (edge == null) {
                    break;
                }
                returnPath[i] = new ElementaryPath(elementaryMetaPath, currentStartElement, currentEndElement, edge);
                currentStartElement = currentEndElement;
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
     * @param elementaryMetaPath1
     * @param elementaryMetaPath2
     * @return
     */
    public static final Class<? extends ModelElement> getElementaryPathsConnectingClass(final ElementaryMetaPath elementaryMetaPath1, final ElementaryMetaPath elementaryMetaPath2) {
        //ACHTUNG: diese Funktion nicht einfach durch die andere mit den EdgeClasses laufen lassen, da die Start- und Endklasse der ElementaryMetaPaths was anderes sein können, als die Start- bzw. die Endklasse der enthaltenen Kantenklasse
        if (elementaryMetaPath1 == null) { //tritt auf, wenn es um den ersten Pfaschritt geht, also den Anfang des Pfades
            return elementaryMetaPath2.getStartClass();
        }
        if (elementaryMetaPath2 == null) { //tritt auf, wenn es um den letzten Pfadschritt geht, also das Ende des Pfades
            return elementaryMetaPath1.getEndClass();
        }
        Class<? extends ModelElement> lastEndClass = elementaryMetaPath1.getEndClass();
        Class<? extends ModelElement> nextStartClass = elementaryMetaPath2.getStartClass();
        Class<? extends ModelElement> connectingClass = ReflectionUtils.getMostSpecialElementClass(lastEndClass, nextStartClass);
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
        Class<? extends ModelElement> connectingClass = ReflectionUtils.getMostSpecialElementClass(endClass, nextStartClass);
        return connectingClass;
    }

    /**
     * Liefert true, wenn die Kantenklasse eine Composition ist und die zugehörige Richtung (direction) vom Master auf den Slave zeigt.
     *
     * @param edgeClass
     * @param direction
     * @return
     */
    private static final boolean isCompositionFromMasterToSlave(final Class<? extends Edge> edgeClass, final Direction direction) {
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
    private static final boolean isInstanciationFromMasterToInstance(final Class<? extends Edge> edgeClass, final Direction direction) {
        boolean isEdgeMasterToInstanceInstanciation = MetaModel.isInstaciation(edgeClass);
        if (!isEdgeMasterToInstanceInstanciation) {
            return false;
        }
        isEdgeMasterToInstanceInstanciation = direction == InstanciationEdge.MASTER_TO_INSTANCE_DIRECTION;
        return isEdgeMasterToInstanceInstanciation;
    }

    //    /**
    //     * Erzeugt ein neues Element und verknüpft es mit dem übergebenen Startelelement. Für das neue Element werden alle anderen
    //     * Elemente angelegt, die es braucht, damit keine Verletzung irgendwelcher Kardinalitäten bestehen.
    //     *
    //     * @param startElement Element, von dem aus die Kanten angelegt werden sollen. Ist dieses Element null, dann wird nur das neue Element angelegt,
    //     *            aber nichts verknüpft.
    //     * @param edgeClassToNewElement Kantenklasse, die zwischen dem startElement und dem anzulegenden Element bestehen soll. Diese Klasse und die
    //     *            directionToNewElement geben vor, welche Elementart neu angelegt werden soll
    //     * @param directionToNewElement Richtung der neu anzulegenden Edge ausgehend vom startContainer
    //     * @param edgeClassFromNewElement Kantenklasse, die nicht neu angelegt wird, auch wenn die Kardinalität das bedingen würde. Da diese Funktion hier
    //     *            für einen anzulegenden Pfad aufgerufen wird, dürfen die Edge, dieses Pfades eben nicht schon hier automatisch angelegt werden.
    //     * @param doc GraphDocument, in dem die anzulegenden Container landen sollen (wenn sie teilmodellspezifisch sind)
    //     * @param pid Process-ID des Dialoges
    //     * @return den neu angelegtes ModelElement mit allen davon abhängigen Elementen (außer denen, die evtl. auf dem Pfad liegen, der insgesamt
    //     *         angelegt werden soll)
    //     */
    //    public static final ModelElement createNodeWithContainerAndDependents(final GraphDocument doc, final ModelElement startElement, final SimpleMetaPath metaPath, final int pathStepIndex, final int pid) {
    //        List<ElementaryMetaPath> elementaryMetaPaths = metaPath.getElementaryMetaPaths();
    //        ElementaryMetaPath elementaryMetaPath = elementaryMetaPaths.get(pathStepIndex);
    //        Class<? extends Edge> edgeClassToNewElement = elementaryMetaPath.getEdgeClass();
    //        Direction directionToNewElement = elementaryMetaPath.getDirection();
    //        Class<? extends Edge> edgeClassFromNewElement = null;
    //        Direction directionFromNewElement = null;
    //        if (pathStepIndex + 1 < elementaryMetaPaths.size()) {
    //            elementaryMetaPath = elementaryMetaPaths.get(pathStepIndex + 1);
    //            edgeClassFromNewElement = elementaryMetaPath.getEdgeClass();
    //            directionFromNewElement = elementaryMetaPath.getDirection();
    //        }
    //        return createNodeWithContainerAndDependents(doc, startElement, edgeClassToNewElement, directionToNewElement, edgeClassFromNewElement, directionFromNewElement, pid);
    //    }
    //
    /**
     * Erzeugt ein neues Element und verknüpft es mit dem übergebenen Startelelement. Für das neue Element werden alle anderen
     * Elemente angelegt, die es braucht, damit keine Verletzung irgendwelcher Kardinalitäten bestehen.
     *
     * @param startElement Element, von dem aus die Kanten angelegt werden sollen. Ist dieses Element null, dann wird nur das neue Element angelegt,
     *            aber nichts verknüpft.
     * @param edgeClassToNewElement Kantenklasse, die zwischen dem startElement und dem anzulegenden Element bestehen soll. Diese Klasse und die
     *            directionToNewElement geben vor, welche Elementart neu angelegt werden soll
     * @param directionToNewElement Richtung der neu anzulegenden Edge ausgehend vom startContainer
     * @param edgeClassFromNewElement Kantenklasse, die nicht neu angelegt wird, auch wenn die Kardinalität das bedingen würde. Da diese Funktion hier
     *            für einen anzulegenden Pfad aufgerufen wird, dürfen die Edge, dieses Pfades eben nicht schon hier automatisch angelegt werden.
     * @param doc GraphDocument, in dem die anzulegenden Container landen sollen (wenn sie teilmodellspezifisch sind)
     * @param pid Process-ID des Dialoges
     * @return den neu angelegtes ModelElement mit allen davon abhängigen Elementen (außer denen, die evtl. auf dem Pfad liegen, der insgesamt
     *         angelegt werden soll)
     */
    public static final ModelElement createNodeWithContainerAndDependents(final GraphDocument doc, final ModelElement startElement, final Class<? extends Edge> edgeClassToNewElement, final Direction directionToNewElement,
            final Class<? extends Edge> edgeClassFromNewElement, final Direction directionFromNewElement, final int pid) {

        //Collection des übergebenen doc holen
        GDCollection gdcoll = doc.getCollection();

        //den interactiveMode auf false setzen, damit man nicht nach den Namen für die Zwischenelemente gefragt wird,
        //bei denen der Namen normalerweise nicht generiert wird
        boolean lastEdge = edgeClassFromNewElement == null;
        //bei der letzten Edge sollte man bei neuen Elementen nach dem Namen fragen
        boolean newInteractiveMode = lastEdge;
        //Ausnahme für Mac-Java-Bug: wenn Dialoge auf dem MAC aus einem Drag&Drop-Ereignis heraus gestartet werden, kann man sie nicht mehr mit der Maus ansprechen. Nur mit Tasten.
        //Da dieser Bug nicht so einfach zu umgehen ist, wird in diesem Fall der Dialog einfach nicht angezeigt und der Name generiert.
        if (Static.isDragNDropOnMac()) {
            newInteractiveMode = false;
        }
        boolean lastInteractiveMode = gdcoll.setInteractiveMode(newInteractiveMode);

        Class<? extends ModelElement> elementClass2Create = getElementaryPathsConnectingClass(edgeClassToNewElement, directionToNewElement, edgeClassFromNewElement, directionFromNewElement);
        //abstracte Elemente können nicht angelegt werden! hier wird nicht auf null gecheckt, weil man diese Funktion nur mit SimpleMetaPaths aufrufen sollte, die creatable sind!
        if (MetaModel.isAbstract(elementClass2Create)) {
            return null;
        }

        ModelElement createdDependent;
        ElementContainer createdContainer = null;
        //wenn ein gültiges startElement übergeben wurde und die Kantenart eine Composition ist
        if (startElement != null && isCompositionFromMasterToSlave(edgeClassToNewElement, directionToNewElement)) {
            //erzeuge ein untergeordnetes Element
            Class<? extends CompositionEdge> compositionEdgeClass = edgeClassToNewElement.asSubclass(CompositionEdge.class);
            createdDependent = GraphDocument.createAddicted(doc, startElement, compositionEdgeClass, elementClass2Create, pid);
        } else if (startElement != null && isInstanciationFromMasterToInstance(edgeClassToNewElement, directionToNewElement)) {
            //erzeuge eine Instanz aus dem Master
            Class<? extends InstanciationEdge> instanciationEdgeClass = edgeClassToNewElement.asSubclass(InstanciationEdge.class);
            NodeContainer createdInstance = doc.createInstance(doc, instanciationEdgeClass, startElement, pid);
            createdDependent = createdInstance != null ? createdInstance.getElement() : null;
        } else {
            //das neue Element gleich mit Container im doc anlegen
            createdContainer = doc.createNodeAndContainer(elementClass2Create, pid);
            if (createdContainer == null) {
                return null;
            }
            //das Element des neu angelgten Containers holen
            createdDependent = createdContainer.getElement();

            //das neue Element mit dem startElement verknüpfen. Dast Startelement kann null sein, wenn nur das neue Element angelegt werden soll
            if (startElement != null) {
                link(gdcoll, startElement, createdDependent, edgeClassToNewElement, directionToNewElement, pid);
            }
        }

        //falls hier beim Anlegen irgendwas schief gegangen ist -> raus
        if (createdDependent == null) {
            return null;
        }

        //wenn das neu angelegte Element ein übergerodnetes Element von dem startElement ist, dann sollte es in der Grafik unter dem startElement liegen
        if (startElement != null && startElement.isSubElementOf(createdDependent)) {
            if (createdContainer == null) {
                createdContainer = createdDependent.getContainer(doc);
            }
            doc.raiseSlaves(createdContainer);
        }

        //alle Kantentpyen der neu angelegten Elementart holen
        MetaModel metaModel = gdcoll.getMetaModel();
        Class<? extends Edge>[] edgeTypes = metaModel.getEdgeTypes(elementClass2Create);
        //für jede dieser Kantenarten
        boolean interrupted = false;
        for (int i = 0; i < edgeTypes.length && !interrupted; i++) {
            //aktuelle Kantenart holen
            Class<? extends Edge> edgeType = edgeTypes[i];
            //die Kanten, die über den Pfad als nächstes angelegt werden sollen, dürfen hier nicht angelegt werden
            if (edgeType == edgeClassFromNewElement) {
                continue;
            }
            //wenn das neu angelegte Element StartElement der Edge ist
            if (metaModel.isStartClass(edgeType, elementClass2Create)) {
                //hole die MinKardnalität zu dem anderen Element der Edge
                int minCardinalityForwardToOther = metaModel.getMinForwardCardinality(edgeType);
                if (minCardinalityForwardToOther > 0) {
                    //hole alle Kanten des neu angelgten Elementes, die denselben Typ haben
                    List<Edge> edgesForwardTo = createdDependent.getEdgesTo(ModelElement.class, edgeType);
                    //Anzahl der bestehenden Kanten der aktuellen Kantenart zu anderen Elementen
                    int edgesForwardToCount = edgesForwardTo.size();
                    //wenn weitere Kanten angelegt werden müssen
                    while (minCardinalityForwardToOther - edgesForwardToCount > 0) {
                        //für das neu angelegte Element müssen auch alle abhängigen Elemente angelegt werden. Da der Pfad von hier nicht weiter
                        //geht, ist die edgeCLassFromNewElement null.
                        ModelElement created = createNodeWithContainerAndDependents(doc, createdDependent, edgeType, FORWARD, null, null, pid);
                        if (created == null) {
                            interrupted = true;
                            break;
                        }
                        edgesForwardToCount++;
                    }
                }
                //wenn das neu angelegte Element EndElement der Edge ist
            } else {
                //hole die MinKardnalität zu dem anderen Element der Edge
                int minCardinalityBackwardToOther = metaModel.getMinBackwardCardinality(edgeType);
                if (minCardinalityBackwardToOther > 0) {
                    //hole alle Kanten des neu angelgten Elementes, die denselben Typ haben
                    List<Edge> edgesBackwardTo = createdDependent.getEdgesFrom(ModelElement.class, edgeType);
                    //Anzahl der bestehenden Kanten der aktuellen Kantenart zu anderen Elementen
                    int edgesBackwardToCount = edgesBackwardTo.size();
                    //wenn weitere Kanten angelegt werden müssen
                    while (minCardinalityBackwardToOther - edgesBackwardToCount > 0) {
                        //für das neu angelegte Elemente, müssen auch alle abhängigen Elemente angelegt werden. Da der Pfad von hier nicht weiter
                        //geht, ist die edgeClassFromNewElement null.
                        ModelElement created = createNodeWithContainerAndDependents(doc, createdDependent, edgeType, BACKWARD, null, null, pid);
                        if (created == null) {
                            interrupted = true;
                            break;
                        }
                        edgesBackwardToCount++;
                    }
                }
            }

        }
        gdcoll.setInteractiveMode(lastInteractiveMode);
        return createdDependent;
    }

    /**
     * Verbindet die beiden Elemente je nach übergebener Richtung vorwärts oder rückwärts. Richtung und Kantenklasse ergeben sich aus dem
     * Elementarpfad.
     *
     * @param gdcoll
     * @param startElement
     * @param endElement
     * @param edgeClass
     * @param direction
     * @param pid
     */
    private static void link(final GDCollection gdcoll, final ModelElement startElement, final ModelElement endElement, final Class<? extends Edge> edgeClass, final Direction direction, final int pid) {
        //das neue Element mit dem startElement verknüpfen
        if (direction == FORWARD) {
            gdcoll.link(edgeClass, startElement, endElement, pid);
        } else {
            gdcoll.link(edgeClass, endElement, startElement, pid);
        }
    }

}
