package de.imise.tool3lgm.graphtools.path.pathmodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge.ConnectionState;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.path.meta.AbstractMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.DifferenceMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.ElementaryMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.ElementaryMetaPath.Type;
import de.imise.tool3lgm.graphtools.path.meta.ParallelMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.SectionMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.SequenceMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.UnionMetaPath;
import de.imise.tool3lgm.graphtools.path.pathmodel.PathResultTreeNode.NodeType;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;

/**
 * Ein Model, das je nach übergebenem MetaPfad und Startelementen einen Baum aufbaut, in dem man ausgehend von den Startelementen alle
 * Instanzen der Pfade finden kann, die dem übergebenen MetaPfad entsprechen.
 * Der Baum hat dabei immer denselben Aufbau:
 * Ebene 0: Der Root-Knoten. Er hat keinerlei Bedeutung
 * Ebene 1: Für jedes übergebene Startelement, für das mind. ein Gesamtpfad existiert, wird in Ebene 1 ein PathResultTreeNode angelegt.
 * Der interne Pfad dieses Kotens hat keinen hinterlegten MetaPfad, die Kante ist null und Start- und Endelement sind immer das
 * Startelement, von welchem dieser Pfad los geht.
 * Ebene 2: PathResultTreeNode, der jeweils einen ersten Pfadschritt ausgehend von einem in Ebene 1 repräsentierten Startelement beschreibt.
 * Ebene 3+: ausgehend vom Endelement des darüber liegenden Knotens ein weiterer Pfadschritt
 * Ebene n: Blätter des Baumes enthalten alle Ergebniselemente, die über den durch den MetaPath festgelegten Pfad ausgehend von den
 * Startelementen erreichbar sind.
 * Zusätzlich wird jeder PathResultTreeNode mit der Information markiert, ob er ein Pfadschritt innerhalb eines MetaSequencePaths ist, der
 * selbst wieder in einem MetaSequncePath enthalten ist (Varibale isSubStep im PathResultTreeNode). Nur bei den äußersten MetaSequencePaths
 * ist diese Variable <code>false</code> und bei allen darin enthaltenen MetaSequencePaths und ihren enthaltenen SubPfaden ist diese Variable
 * <code>true</code>. Diese Information kann man zum Aufbau von anzuzeigenden Bäumen nutzen um SubPfade nicht mit anzuzeigen.
 *
 * @author AXS
 * @create 05.11.2010
 */
public class PathResultTreeModel extends DefaultTreeModel {

    /**
     * Der Gesamt-MetaPath, über den Pfadinstanzen von den Startelementen gesucht werden.
     */
    private final AbstractMetaPath metaPath;

    /**
     * Bei allen {@link ParallelMetaPath} kann man mehrere Startsets brauchen (für jeden der
     * parallelen Metapfade eins). Bei den {@link ElementaryMetaPath} und {@link SequenceMetaPath} steht immer nur eine Collection an Index 0 in der
     * Liste.
     */
    private List<Collection<ModelElement>> startElements = new ArrayList<>();

    /**
     * Alle Blattknoten, die ausgehend von einem Startknoten im Ergebnisbaum vorhanden sind, die nicht
     * den kompletten MetaPath darstellen. Hier können nur Knoten enthalten sein, falls <code>keepIncompleteBranches == true</code>. Alle anderen
     * Blattknoten, die einen kompletten
     * Pfad beschreiben sind immer in {@link #completePathLeafs}
     */
    private final List<PathResultTreeNode> incompletePathLeafs = new ArrayList<>();

    /**
     * Alle Blattknoten, die ausgehend von einem Startknoten im Ergebnisbaum und einen gesamten Pfad
     * repräsentieren.
     */
    private final List<PathResultTreeNode> completePathLeafs = new ArrayList<>();

    /**
     * Wenn <code>false</code> werden Zweige im Ergebnisbaum gelöscht, die nicht den gesamten Metapfad
     * darstellen.
     */
    private boolean keepIncompleteBranches = false;

    /**
     * Auf level 0 ist der Root und auf der 1 kommmen alle StartElement-Nodes
     */
    public static final int START_ELEMENTS_NODE_LEVEL = 1;

    /**
     * Nach den StartElement-Nodes kommen auf Level 2 die ersten Pfadschritte
     */
    public static final int FIRST_PATH_STEP_NODE_LEVEL = START_ELEMENTS_NODE_LEVEL + 1;

    /**
     * @param metaPath
     */
    public PathResultTreeModel(final AbstractMetaPath metaPath) {
        super(new PathResultTreeNode(null, PathResultTreeNode.NodeType.ROOT));
        this.metaPath = metaPath;
    }

    /**
     * @param metaPath
     * @param startElement
     */
    public PathResultTreeModel(final AbstractMetaPath metaPath, final ModelElement startElement) {
        this(metaPath);
        setStartElements(startElement);
    }

    /**
     * @param metaPath
     * @param startElements
     */
    public PathResultTreeModel(final AbstractMetaPath metaPath, final Collection<ModelElement> startElements) {
        this(metaPath);
        setStartElements(startElements);
    }

    /**
     * @param metaPath
     * @param startElementSets
     */
    public PathResultTreeModel(final AbstractMetaPath metaPath, final List<Collection<ModelElement>> startElementSets) {
        this(metaPath);
        setStartElementSets(startElementSets);
    }

    /**
     * @param metaPath
     * @param keepIncompleteBranches
     */
    public PathResultTreeModel(final AbstractMetaPath metaPath, final boolean keepIncompleteBranches) {
        this(metaPath);
        this.keepIncompleteBranches = keepIncompleteBranches;
    }

    /**
     * @param metaPath
     * @param startElement
     * @param keepIncompleteBranches
     */
    public PathResultTreeModel(final AbstractMetaPath metaPath, final ModelElement startElement, final boolean keepIncompleteBranches) {
        this(metaPath, keepIncompleteBranches);
        setStartElements(startElement);
    }

    /**
     * @param metaPath
     * @param startElements
     * @param keepIncompleteBranches
     */
    public PathResultTreeModel(final AbstractMetaPath metaPath, final Collection<ModelElement> startElements, final boolean keepIncompleteBranches) {
        this(metaPath, keepIncompleteBranches);
        setStartElements(startElements);
    }

    /**
     * @param metaPath
     * @param startElementSets
     * @param keepIncompleteBranches
     */
    public PathResultTreeModel(final AbstractMetaPath metaPath, final List<Collection<ModelElement>> startElementSets, final boolean keepIncompleteBranches) {
        this(metaPath, keepIncompleteBranches);
        setStartElementSets(startElementSets);
    }

    /**
     * @param startElementSets
     */
    public void setStartElementSets(final List<Collection<ModelElement>> startElements) {
        this.startElements.clear();
        if (startElements != null) {
            this.startElements = startElements;
        }

        //irgendwas wollte ich hier noch machen, weiß aber nicht mehr was...

        reload();
    }

    @Override
    public PathResultTreeNode getRoot() {
        return (PathResultTreeNode) super.getRoot();
    }

    /**
     * @param startElements
     */
    public void setStartElements(final Collection<ModelElement> startElements) {
        this.startElements.clear();
        if (startElements != null && startElements.size() > 0) {
            this.startElements.add(startElements);
        }
        reload();
    }

    /**
     * Setzt das übergebene Element als StartElement für die suche der Verbindungen über den gesetzten Pfad
     *
     * @param startElements
     */
    public void setStartElements(final ModelElement startElement) {
        Collection<ModelElement> startElements = new ArrayList<>();
        startElements.add(startElement);
        setStartElements(startElements);
    }

    /**
     * Liefert alle Knoten, die Blätter dieses Baumes sind und die Endelemente des gesamten Pfades enthalten
     *
     * @return the completePathLeafs
     */
    public final List<PathResultTreeNode> getCompletePathLeafs() {
        return completePathLeafs;
    }

    /**
     * Liefert alle Knoten, die Blätter dieses Baumes sind, aber nur Zwischenelemente des gesamten Pfades sind,
     * da sie nicht weiter mit den gesuchten Endelementen verbuden sind.
     *
     * @return the incompletePathLeafs
     */
    public final List<PathResultTreeNode> getIncompletePathLeafs() {
        return incompletePathLeafs;
    }

    /**
     * Liefert alle mit dem übergebenen Element über diesen MetaPfad verbundenen Elemente.
     *
     * @param multiple
     *            Wenn <code>true</code> sind mehrfach verbundene Element auch mehrfach in der Ergebnisliste, bei <code>false</code> ist jedes Element
     *            nur einmal enthalten.
     * @return
     */
    public final Collection<ModelElement> getConnectedElements(final boolean multiple) {
        return getConnectedElements(multiple, false);
    }

    /**
     * Liefert alle mit dem übergebenen Element über diesen MetaPfad verbundenen Elemente.
     *
     * @param multiple
     *            Wenn <code>true</code> sind mehrfach verbundene Element auch mehrfach in der Ergebnisliste, bei <code>false</code> ist jedes Element
     *            nur einmal enthalten.
     * @param forlast wenn <code>true</code>, werden nicht die letzten Elemente des Pfades zurück gegeben, sondern die vorletzten. Ist der Pfad nur 1
     *            lang, kommem die Ausgangselemente zurück
     * @return
     */
    public final Collection<ModelElement> getConnectedElements(final boolean multiple, final boolean forlast) {
        Collection<ModelElement> returnCollection = multiple ? new ArrayList<>() : new HashSet<>();
        for (PathResultTreeNode node : getCompletePathLeafs()) {
            returnCollection.add(forlast ? node.getStartElement() : node.getEndElement());
        }
        return returnCollection;
    }

    /**
     * Liefert alle mit dem übergebenen Element über diesen MetaPfad verbundenen Elemente.
     *
     * @return
     */
    public Collection<ModelElement> getConnectedElements() {
        return getConnectedElements(false, false);
    }

    /**
     * Liefert alle Parent-Elemente der mit dem übergebenen Element über diesen MetaPfad verbundenen Elemente. Besteht der Pfad nur aus einer Kante,
     * so kommt hier das Ausgangselement zurück.
     *
     * @return
     */
    public Collection<ModelElement> getForlastConnectedElements() {
        return getConnectedElements(false, true);
    }

    /**
     * Liefert alle mit dem übergebenen Element über diesen MetaPfad verbundenen Elemente.
     *
     * @param multiple
     *            Wenn <code>true</code> sind mehrfach verbundene Element auch mehrfach in der Ergebnisliste, bei <code>false</code> ist jedes Element
     *            nur einmal enthalten.
     * @return
     */
    public final Collection<ElementContainer> getConnectedContainer(final GraphDocument doc) {
        return getConnectedContainer(doc, false);
    }

    /**
     * Liefert alle mit dem übergebenen Element über diesen MetaPfad verbundenen Elemente.
     *
     * @param doc
     *            GraphDocument, in dem die Container gesucht werden
     * @param forlast
     *            wenn <code>true</code> werden nicht die letzten, sondern die vorletzten im
     *            Pfad zurück gegeben. Bei Pfaden, die nur aus einer Edge bestehen ist das das
     *            Ausgangselement des Pfades, also das ModelElement des Dialoges.
     * @return
     */
    public final Collection<ElementContainer> getConnectedContainer(final GraphDocument doc, final boolean forlast) {
        Collection<ElementContainer> returnCollection = new HashSet<>();
        for (PathResultTreeNode node : getCompletePathLeafs()) {
            ModelElement endElement = forlast ? node.getStartElement() : node.getEndElement();
            ElementContainer ec = endElement.getContainer(doc);
            if (ec != null) {
                returnCollection.add(ec);
            }
        }
        return returnCollection;
    }

    //Aufbau des Models

    @Override
    public void reload(final TreeNode irrelevantNode) {
        //MAXIME: es wird immer der ganze Baum neu aufgebaut - egal welcher Knoten übergeben wurde!
        PathResultTreeNode root = getRoot();
        root.removeAllChildren();
        completePathLeafs.clear();
        incompletePathLeafs.clear();
        if (startElements == null || startElements.size() == 0) {
            return;
        }
        //mehrere Startmemngen wurden übergeben (das geht nur bei einem alles umschließenden Parallelmetapfad)
        if (startElements.size() > 1) {
            AbstractMetaPath realFirstMetaPath = metaPath;
            //solange aus dem SequenceMetaPath den jeweils ersten MetaPath holen, bis man bei einem
            //MetaPath ist, der selbst kein SequenceMetaPath mehr ist
            while (realFirstMetaPath instanceof SequenceMetaPath) {
                realFirstMetaPath = ((SequenceMetaPath) realFirstMetaPath).getMetaPaths().get(0);
                //TODO: hier muss ein paralleler MetaPfad kommen, den wir später behandeln...
            }

            //eine einelementige Startelementliste wurde übergeben
        } else {
            for (ModelElement me : startElements.get(0)) {
                PathResultTreeNode node = new PathResultTreeNode(new ElementaryPath(null, me), PathResultTreeNode.NodeType.START_ELEMENT);
                completePathLeafs.addAll(addPath(node, metaPath, false));
                if (node.getChildCount() > 0) {
                    root.add(node);
                }
            }
        }
        super.reload(root);
    }

    /**
     * Funktion, die für den übergebenen {@link AbstractMetaPath} die richtige Unterfunktion aufruft.
     *
     * @param startNode
     * @param metaPath
     * @param isSubStep
     * @return
     */
    private List<PathResultTreeNode> addPath(final PathResultTreeNode startNode, final AbstractMetaPath metaPath, final boolean isSubStep) {
        if (metaPath instanceof ElementaryMetaPath) {
            return addPath(startNode, (ElementaryMetaPath) metaPath, isSubStep);
        } else if (metaPath instanceof SequenceMetaPath) {
            return addPath(startNode, (SequenceMetaPath) metaPath, isSubStep);
        } else if (metaPath instanceof UnionMetaPath) {
            return addPath(startNode, (UnionMetaPath) metaPath, isSubStep);
        } else if (metaPath instanceof SectionMetaPath) {
            return addPath(startNode, (SectionMetaPath) metaPath, isSubStep);
        } else if (metaPath instanceof DifferenceMetaPath) {
            return addPath(startNode, (DifferenceMetaPath) metaPath, isSubStep);
        }
        return null;
    }

    /**
     * Hängt an den übergebenen startNode einen Knoten an, der den übergebenen MetaPath und
     *
     * @param startNode
     * @param metaPath
     * @param isSubStep
     * @return
     */
    private List<PathResultTreeNode> addPath(final PathResultTreeNode startNode, final ElementaryMetaPath metaPath, final boolean isSubStep) {
        ModelElement endElement = startNode.getEndElement();
        List<PathResultTreeNode> connectedNodes = getConnectedNodes(endElement, metaPath, isSubStep);
        for (PathResultTreeNode connectedNode : connectedNodes) {
            startNode.add(connectedNode);
        }
        return connectedNodes;
    }

    /**
     * @param startNode
     * @param metaPath
     * @param isSubStep
     * @return
     */
    private List<PathResultTreeNode> addPath(final PathResultTreeNode startNode, final SequenceMetaPath metaPath, final boolean isSubStep) {
        List<PathResultTreeNode> resultNodes = new ArrayList<>();
        resultNodes.add(startNode);
        for (AbstractMetaPath subMetaPath : metaPath.getMetaPaths()) {
            List<PathResultTreeNode> actStartNodes = new ArrayList<>(resultNodes);
            resultNodes.clear();
            for (PathResultTreeNode actStartNode : actStartNodes) {
                List<PathResultTreeNode> subMetaPathEndNodes = null;
                subMetaPathEndNodes = addPath(actStartNode, subMetaPath, subMetaPath instanceof ElementaryMetaPath ? isSubStep : true);
                if (!keepIncompleteBranches) {
                    if (subMetaPathEndNodes == null || subMetaPathEndNodes.size() == 0) {
                        deleteBranch(actStartNode);
                        continue;
                    }
                } else {
                    incompletePathLeafs.add(actStartNode);
                }
                resultNodes.addAll(subMetaPathEndNodes);
            }
        }
        return resultNodes;
    }

    /**
     * @param startNode
     * @param metaPath
     * @param isSubStep
     * @return
     */
    private List<PathResultTreeNode> addPath(final PathResultTreeNode startNode, final UnionMetaPath metaPath, final boolean isSubStep) {
        List<PathResultTreeNode> resultNodes = new ArrayList<>();
        for (AbstractMetaPath subMetaPath : metaPath.getMetaPaths()) {
            resultNodes.addAll(addPath(startNode, subMetaPath, subMetaPath instanceof ElementaryMetaPath ? isSubStep : true));
        }
        return resultNodes;
    }

    /**
     * @param startNode
     * @param metaPath
     * @param isSubStep
     * @return
     */
    private List<PathResultTreeNode> addPath(final PathResultTreeNode startNode, final SectionMetaPath metaPath, final boolean isSubStep) {
        List<PathResultTreeNode> resultNodes = new ArrayList<>();
        AbstractMetaPath firstMetaPath = null;
        //1. Pfad anhängen, dann von allen weiteren die Endelemente prüfen, ob jedes der EndElemente in jedem der
        //weiteren Pfade auch vorkommt -> wenn nicht, das endElement löschen und ggf. deleteBranch mit aktualisierung der IncompleteLeafs anstoßen
        PathResultTreeNode startNodeClone = new PathResultTreeNode(startNode);
        List<PathResultTreeNode> nextPathResultNodes = new ArrayList<>();
        for (AbstractMetaPath subMetaPath : metaPath.getMetaPaths()) {
            if (firstMetaPath == null) {
                firstMetaPath = subMetaPath;
                resultNodes.addAll(addPath(startNode, subMetaPath, subMetaPath instanceof ElementaryMetaPath ? isSubStep : true));
            } else {
                nextPathResultNodes.addAll(addPath(startNodeClone, subMetaPath, isSubStep));
            }
        }
        //Set aller Elemente, die Ergebiselemente des Pfades sein können
        HashSet<ModelElement> possiblePathEndElements = new HashSet<>();
        for (PathResultTreeNode resultNode : resultNodes) {
            possiblePathEndElements.add(resultNode.getEndElement());
        }

        //alle Zweige löschen, bei denen das EndElement nicht in beiden Listen vorkommt
        for (PathResultTreeNode resultNode : resultNodes) {
            if (!possiblePathEndElements.contains(resultNode.getEndElement())) {
                deleteBranch(resultNode);
            }
        }

        return resultNodes;
    }

    /**
     * @param startNode
     * @param metaPath
     * @param isSubStep
     * @return
     */
    private List<PathResultTreeNode> addPath(final PathResultTreeNode startNode, final DifferenceMetaPath metaPath, final boolean isSubStep) {
        List<PathResultTreeNode> resultNodes = new ArrayList<>();
        return resultNodes;
    }

    /**
     * Löscht den übergebenen Knoten und alle darüber die nur 1 Kind (den jeweils zu löschenden Knoten) haben.
     *
     * @param node
     */
    private void deleteBranch(PathResultTreeNode node) {
        PathResultTreeNode parent = (PathResultTreeNode) node.getParent();
        while (parent != null && parent.getChildCount() == 1) {
            node = parent;
            parent = (PathResultTreeNode) node.getParent();
        }
        if (parent != null) {
            node.removeFromParent();
        }
    }

    //    /**
    //     * @param startNode
    //     * @param metaPath
    //     * @param isSubStep
    //     * @return
    //     */
    //    private List<PathResultTreeNode> addPath(final PathResultTreeNode startNode, final ParallelMetaPath metaPath, final boolean isSubStep) {
    //        List<PathResultTreeNode> resultNodes = new ArrayList<>();
    //        if (metaPath instanceof UnionMetaPath) {
    //            for (AbstractMetaPath subMetaPath : metaPath.getMetaPaths()) {
    //                resultNodes.addAll(resultNodes);
    //            }
    //        } else {
    //            List<AbstractMetaPath> metaPaths = metaPath.getMetaPaths();
    //            AbstractMetaPath firstSubMetaPath = metaPaths.get(0);
    //            if (firstSubMetaPath instanceof ElementaryMetaPath) {
    //                if (metaPath instanceof DifferenceMetaPath) {
    //                    for (AbstractMetaPath subMetaPath : metaPath.getMetaPaths()) {
    //                    }
    //                } else if (metaPath instanceof SectionMetaPath) {
    //                    for (AbstractMetaPath subMetaPath : metaPath.getMetaPaths()) {
    //                    }
    //                }
    //            }
    //        }
    //        return resultNodes;
    //    }

    /**
     * Liefert alle mit dem übergebenen Element verbundenen Elemente und Kanten als Ergebnisknoten zum einhängen in den
     * Ergebnisbaum zurück.
     *
     * @param me
     *            Element, für das die verbundenen Elemente gesucht werden sollen
     * @param metaPath
     *            Elementar-MetaPath, über den Elemente verbunden sein sollen
     * @param isSubStep
     *            Wenn <code>true</code> werden die Ergebnsiknoten als Unterpfadschritt markiert.
     * @return
     */
    private List<PathResultTreeNode> getConnectedNodes(final ModelElement me, final ElementaryMetaPath metaPath, final boolean isSubStep) {
        //Ergebnisliste
        List<PathResultTreeNode> resultNodes = new ArrayList<>();
        if (!metaPath.isValid()) {
            return resultNodes;
        }
        Class<? extends ModelElement> meClass = me.getClass();
        //In jedem Fall muss die Startklasse des MetaPfades zuweisungskompatibel zur Klasse des übergebenen Elementes sein
        if (!metaPath.isStartClass(meClass, true, false)) {
            return resultNodes;
        }
        Class<? extends Edge> edgeClass = metaPath.getEdgeClass();
        Class<? extends ModelElement> endClass = metaPath.getEndClass();
        Direction dir = metaPath.getDirection();
        ConnectionState connectionState = metaPath.getConnectionState();

        //wenn die Richtung BACKWARD ist, muss der connectionState umgedreht werden (wenn er FORWARD oder BACKWARD ist), damit die interpretierte Richtung stimmt
        if (connectionState != null && dir == Direction.BACKWARD) {
            connectionState = connectionState == ConnectionState.FORWARD ? ConnectionState.BACKWARD : connectionState == ConnectionState.BACKWARD ? ConnectionState.FORWARD : connectionState;
        }
        //wenn der MetaPfad eine 'normale' Verbindung zwischen 2 Elementarten über eine Kantenart beschreibt
        NodeType nodeType = isSubStep ? PathResultTreeNode.NodeType.SUBSTEP : PathResultTreeNode.NodeType.SUPERSTEP;
        if (metaPath.getType() == Type.ELEMENT_EDGE_ELEMENT) {
            List<Edge> edges;
            if (dir == Direction.FORWARD) {
                edges = me.getEdgesTo(endClass, edgeClass);
            } else if (dir == Direction.BACKWARD) {
                edges = me.getEdgesFrom(endClass, edgeClass);
            } else {
                edges = me.getEdgesWith(endClass, edgeClass);
            }
            for (Edge edge : edges) {
                if (connectionState != null) {// nur bei DoubleMeaningEdges kann der ConnectionState überhaupt nicht null sein
                    DoubleMeaningEdge doubleMeaningEdge = (DoubleMeaningEdge) edge;
                    ConnectionState edgeConnectionState = doubleMeaningEdge.getConnectionState();
                    if (edgeConnectionState != ConnectionState.DOUBLE && connectionState != edgeConnectionState) {
                        continue;
                    }
                }
                resultNodes.add(new PathResultTreeNode(new ElementaryPath(metaPath, me, edge.getOther(me), edge), nodeType));
                //wenn der MetaPfad nur eine Elementart beschreibt (Start und Endklasse sind gleich und die Kantenklasse ist null)
            }
        } else if (metaPath.getType() == Type.SINGLE_ELEMENT) {
            if (endClass.isAssignableFrom(me.getClass())) {
                resultNodes.add(new PathResultTreeNode(new ElementaryPath(metaPath, me), nodeType));
                //wenn der MetaPfad bei einer Kantenklasse startet und auf ein oder beide Enden dieser Kante verweist (je nach Richtung)
            }
        } else if (metaPath.getType() == Type.START_WITH_EDGE) {
            //das muss gehen, weil oben schon startElementClass und meClass überprüft werden
            Edge edge = (Edge) me;
            if (metaPath.getDirection() == Direction.FORWARD) {
                ModelElement endElement = edge.getEnd();
                if (endClass.isAssignableFrom(endElement.getClass())) {
                    resultNodes.add(new PathResultTreeNode(new ElementaryPath(metaPath, edge, endElement, edge), nodeType));
                }
            } else if (metaPath.getDirection() == Direction.BACKWARD) {
                ModelElement startElement = edge.getStart();
                if (endClass.isAssignableFrom(startElement.getClass())) {
                    resultNodes.add(new PathResultTreeNode(new ElementaryPath(metaPath, edge, startElement, edge), nodeType));
                }
            } else {
                ModelElement element = edge.getStart();
                if (endClass.isAssignableFrom(element.getClass())) {
                    resultNodes.add(new PathResultTreeNode(new ElementaryPath(metaPath, edge, element, edge), nodeType));
                }
                element = edge.getEnd();
                if (endClass.isAssignableFrom(element.getClass())) {
                    resultNodes.add(new PathResultTreeNode(new ElementaryPath(metaPath, edge, element, edge), nodeType));
                }
            }
            //wenn der MetaPfad bei einer zur Kantenklasse des MetaPfades zuweisungskompatiblen Elementklasse endet
        } else if (metaPath.getType() == Type.END_WITH_EDGE) {
            List<Edge> edges;
            if (dir == Direction.FORWARD) {
                edges = me.getEdgesTo(ModelElement.class, edgeClass);
            } else if (dir == Direction.BACKWARD) {
                edges = me.getEdgesFrom(ModelElement.class, edgeClass);
            } else {
                edges = me.getEdgesWith(ModelElement.class, edgeClass);
            }
            for (Edge edge : edges) {
                if (endClass.isAssignableFrom(edge.getClass())) {
                    resultNodes.add(new PathResultTreeNode(new ElementaryPath(metaPath, me, edge, edge), nodeType));
                }
            }
        }
        return resultNodes;
    }

}
