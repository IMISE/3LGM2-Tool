package de.imise.tool3lgm.graphtools.path.paths;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge.ConnectionState;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.path.metapaths.DifferenceMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.ElementaryMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.ElementaryMetaPath.Type;
import de.imise.tool3lgm.graphtools.path.metapaths.ListMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.MetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.ParallelMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.SectionMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.SerialMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.UnionMetaPath;
import de.imise.tool3lgm.graphtools.path.paths.PathResultTreeNode.NodeType;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;

/**
 * Ein Model, das je nach übergebenem MetaPfad und Startelementen einen Baum
 * aufbaut, in dem man ausgehend von den Startelementen alle Instanzen der Pfade
 * finden kann, die dem übergebenen MetaPfad entsprechen. Der Baum hat dabei
 * immer denselben Aufbau: Ebene 0: Der Root-Knoten. Er hat keinerlei Bedeutung
 * Ebene 1: Für jedes übergebene Startelement, für das mind. ein Gesamtpfad
 * existiert, wird in Ebene 1 ein PathResultTreeNode angelegt. Der interne Pfad
 * dieses Kotens hat keinen hinterlegten MetaPfad, die Kante ist null und Start-
 * und Endelement sind immer das Startelement, von welchem dieser Pfad los geht.
 * Ebene 2: PathResultTreeNode, der jeweils einen ersten Pfadschritt ausgehend
 * von einem in Ebene 1 repräsentierten Startelement beschreibt. Ebene 3+:
 * ausgehend vom Endelement des darüber liegenden Knotens ein weiterer
 * Pfadschritt Ebene n: Blätter des Baumes enthalten alle Ergebniselemente, die
 * über den durch den MetaPath festgelegten Pfad ausgehend von den
 * Startelementen erreichbar sind. Zusätzlich wird jeder PathResultTreeNode mit
 * der Information markiert, ob er ein Pfadschritt innerhalb eines
 * SerialMetaPaths ist, der selbst wieder in einem MetaSequncePath enthalten ist
 * (Varibale isSubStep im PathResultTreeNode). Nur bei den äußersten
 * SerialMetaPaths ist diese Variable <code>false</code> und bei allen darin
 * enthaltenen SerialMetaPaths und ihren enthaltenen SubPfaden ist diese
 * Variable <code>true</code>. Diese Information kann man zum Aufbau von
 * anzuzeigenden Bäumen nutzen um SubPfade nicht mit anzuzeigen.
 *
 * @author AXS
 * @create 05.11.2010
 */
public class PathResultTreeModel extends DefaultTreeModel {

    /**
     * Der Gesamt-MetaPath, über den Pfadinstanzen von den Startelementen
     * gesucht werden.
     */
    private final MetaPath metaPath;

    /**
     * Bei allen {@link ParallelMetaPath} kann man mehrere Startsets brauchen
     * (für jeden der parallelen Metapfade eins). Bei den
     * {@link ElementaryMetaPath} und {@link SerialMetaPath} steht immer nur
     * eine Collection an Index 0 in der Liste.
     */
    private List<Collection<ModelElement>> startElementsCollections = new ArrayList<>();

    /**
     * If these endelements are set so only paths to these elements counts as
     * complete. If it is empty then all paths with all metapath steps are
     * complete paths.
     */
    private final Collection<ModelElement> endElements = new HashSet<>();

    /**
     * Alle Blattknoten, die ausgehend von einem Startknoten im Ergebnisbaum
     * vorhanden sind, die nicht den kompletten MetaPath darstellen. Hier können
     * nur Knoten enthalten sein, falls
     * <code>keepIncompleteBranches == true</code>. Alle anderen Blattknoten,
     * die einen kompletten Pfad beschreiben sind immer in
     * {@link #completePathLeafs}
     */
    private final List<PathResultTreeNode> incompletePathLeafs = new ArrayList<>();

    /**
     * Alle Blattknoten, die ausgehend von einem Startknoten im Ergebnisbaum und
     * einen gesamten Pfad repräsentieren.
     */
    private final List<PathResultTreeNode> completePathLeafs = new ArrayList<>();

    /**
     * Wenn <code>false</code> werden Zweige im Ergebnisbaum gelöscht, die nicht
     * den gesamten Metapfad darstellen.
     */
    private boolean keepIncompleteBranches = false;

    /**
     * Wenn <code>true</code> werden alle Branches im Baum gelassen die gefunden
     * werden (auch doppelte), bei <code>false</code> werden gleiche entfernt
     */
    private boolean keepMultipleEqualsBranches = false;

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
    public PathResultTreeModel(final MetaPath metaPath) {
        super(new PathResultTreeNode(null, PathResultTreeNode.NodeType.ROOT));
        this.metaPath = metaPath;
    }

    /**
     * @param metaPath
     * @param startElement
     */
    public PathResultTreeModel(final MetaPath metaPath, final ModelElement startElement) {
        this(metaPath);
        setStartElement(startElement);
    }

    /**
     * @param metaPath
     * @param startElement
     * @param endElement
     */
    public PathResultTreeModel(final MetaPath metaPath, final ModelElement startElement, final ModelElement endElement) {
        this(metaPath);
        setStartAndEndElement(startElement, endElement);
    }

    /**
     * @param metaPath
     * @param startElements
     */
    public PathResultTreeModel(final MetaPath metaPath, final Collection<ModelElement> startElements) {
        this(metaPath);
        setStartElements(startElements);
    }

    /**
     * @param metaPath
     * @param startElementSets
     */
    public PathResultTreeModel(final MetaPath metaPath, final List<Collection<ModelElement>> startElementSets) {
        this(metaPath);
        setStartElementSets(startElementSets);
    }

    /**
     * @param metaPath
     * @param keepIncompleteBranches
     */
    public PathResultTreeModel(final MetaPath metaPath, final boolean keepIncompleteBranches) {
        this(metaPath);
        this.keepIncompleteBranches = keepIncompleteBranches;
    }

    /**
     * @param metaPath
     * @param keepIncompleteBranches
     * @param keepMultipleEqualsBranches
     */
    public PathResultTreeModel(final MetaPath metaPath, final boolean keepIncompleteBranches, final boolean keepMultipleEqualsBranches) {
        this(metaPath);
        this.keepIncompleteBranches = keepIncompleteBranches;
        this.keepMultipleEqualsBranches = keepMultipleEqualsBranches;
    }

    /**
     * @param metaPath
     * @param startElement
     * @param keepIncompleteBranches
     */
    public PathResultTreeModel(final MetaPath metaPath, final ModelElement startElement, final boolean keepIncompleteBranches) {
        this(metaPath, startElement, keepIncompleteBranches, false);
    }

    /**
     * @param metaPath
     * @param startElement
     * @param keepIncompleteBranches
     * @param keepMultipleEqualsBranches
     */
    public PathResultTreeModel(final MetaPath metaPath, final ModelElement startElement, final boolean keepIncompleteBranches, final boolean keepMultipleEqualsBranches) {
        this(metaPath, keepIncompleteBranches);
        setStartElement(startElement);
    }

    /**
     * @param metaPath
     * @param startElements
     * @param keepIncompleteBranches
     */
    public PathResultTreeModel(final MetaPath metaPath, final Collection<ModelElement> startElements, final boolean keepIncompleteBranches) {
        this(metaPath, startElements, keepIncompleteBranches, false);
    }

    /**
     * @param metaPath
     * @param startElements
     * @param keepIncompleteBranches
     * @param keepMultipleEqualsBranches
     */
    public PathResultTreeModel(final MetaPath metaPath, final Collection<ModelElement> startElements, final boolean keepIncompleteBranches, final boolean keepMultipleEqualsBranches) {
        this(metaPath, keepIncompleteBranches, keepMultipleEqualsBranches);
        setStartElements(startElements);
    }

    /**
     * @param metaPath
     * @param startElementSets
     * @param keepIncompleteBranches
     */
    public PathResultTreeModel(final MetaPath metaPath, final List<Collection<ModelElement>> startElementSets, final boolean keepIncompleteBranches) {
        this(metaPath, startElementSets, keepIncompleteBranches, false);
    }

    /**
     * @param metaPath
     * @param startElementSets
     * @param keepIncompleteBranches
     * @param keepMultipleEqualsBranches
     */
    public PathResultTreeModel(final MetaPath metaPath, final List<Collection<ModelElement>> startElementSets, final boolean keepIncompleteBranches, final boolean keepMultipleEqualsBranches) {
        this(metaPath, keepIncompleteBranches, keepMultipleEqualsBranches);
        setStartElementSets(startElementSets);
    }

    /**
     * @param startElementSets
     */
    public void setStartElementSets(final List<Collection<ModelElement>> startElements) {
        startElementsCollections.clear();
        if (startElements != null) {
            startElementsCollections = startElements;
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
        setStartAndEndElements(startElements, null);
    }

    /**
     * Setzt das übergebene Element als StartElement für die suche der
     * Verbindungen über den gesetzten Pfad
     *
     * @param startElement
     */
    public void setStartElement(final ModelElement startElement) {
        setStartAndEndElement(startElement, null);
    }

    /**
     * @param startElement
     * @param endElement
     */
    public void setStartAndEndElement(final ModelElement startElement, final ModelElement endElement) {
        Collection<ModelElement> startElements = List.of(startElement);
        Collection<ModelElement> endElements = endElement == null ? null : List.of(endElement);
        setStartAndEndElements(startElements, endElements);
    }

    /**
     * @param startElements
     */
    public void setStartAndEndElements(final Collection<ModelElement> startElements, final Collection<ModelElement> endElements) {
        startElementsCollections.clear();
        this.endElements.clear();
        if (startElements != null && !startElements.isEmpty()) {
            startElementsCollections.add(startElements);
        }
        if (endElements != null && !endElements.isEmpty()) {
            this.endElements.addAll(endElements);
        }
        reload();
    }

    /**
     * Liefert alle Knoten, die Blätter dieses Baumes sind und die Endelemente
     * des gesamten Pfades enthalten
     *
     * @return the completePathLeafs
     */
    public final List<PathResultTreeNode> getCompletePathLeafs() {
        return completePathLeafs;
    }

    /**
     * Liefert alle Knoten, die Blätter dieses Baumes sind, aber nur
     * Zwischenelemente des gesamten Pfades sind, da sie nicht weiter mit den
     * gesuchten Endelementen verbuden sind.
     *
     * @return the incompletePathLeafs
     */
    public final List<PathResultTreeNode> getIncompletePathLeafs() {
        return incompletePathLeafs;
    }

    /**
     * Liefert alle mit dem übergebenen Element über diesen MetaPfad verbundenen
     * Elemente.
     *
     * @param multiple Wenn <code>true</code> sind mehrfach verbundene Element
     *            auch mehrfach in der Ergebnisliste, bei <code>false</code> ist
     *            jedes Element nur einmal enthalten.
     * @return
     */
    public final List<ModelElement> getConnectedElements(final boolean multiple) {
        return getConnectedElements(multiple, false);
    }

    /**
     * Liefert alle mit dem übergebenen Element über diesen MetaPfad verbundenen
     * Elemente.
     *
     * @param multiple Wenn <code>true</code> sind mehrfach verbundene Element
     *            auch mehrfach in der Ergebnisliste, bei <code>false</code> ist
     *            jedes Element nur einmal enthalten.
     * @param forlast wenn <code>true</code>, werden nicht die letzten Elemente
     *            des Pfades zurück gegeben, sondern die vorletzten. Ist der
     *            Pfad nur 1 lang, kommem die Ausgangselemente zurück
     * @return
     */
    public final List<ModelElement> getConnectedElements(final boolean multiple, final boolean forlast) {
        List<ModelElement> returnCollection = new ArrayList<>();
        for (PathResultTreeNode node : getCompletePathLeafs()) {
            ModelElement me = forlast ? node.getStartElement() : node.getEndElement();
            if (multiple || !returnCollection.contains(me)) {
                returnCollection.add(me);
            }
        }
        return returnCollection;
    }

    /**
     * Liefert alle mit dem übergebenen Element über diesen MetaPfad verbundenen
     * Elemente.
     *
     * @return
     */
    public List<ModelElement> getConnectedElements() {
        return getConnectedElements(false, false);
    }

    /**
     * Liefert alle Parent-Elemente der mit dem übergebenen Element über diesen
     * MetaPfad verbundenen Elemente. Besteht der Pfad nur aus einer Kante, so
     * kommt hier das Ausgangselement zurück.
     *
     * @return
     */
    public List<ModelElement> getForlastConnectedElements() {
        return getConnectedElements(false, true);
    }

    /**
     * Liefert alle mit dem übergebenen Element über diesen MetaPfad verbundenen
     * Elemente.
     *
     * @param multiple Wenn <code>true</code> sind mehrfach verbundene Element
     *            auch mehrfach in der Ergebnisliste, bei <code>false</code> ist
     *            jedes Element nur einmal enthalten.
     * @return
     */
    public final List<ElementContainer> getConnectedContainer(final GraphDocument doc) {
        return getConnectedContainer(doc, false);
    }

    /**
     * Liefert alle mit dem übergebenen Element über diesen MetaPfad verbundenen
     * Elemente.
     *
     * @param doc GraphDocument, in dem die Container gesucht werden
     * @param forlast wenn <code>true</code> werden nicht die letzten, sondern
     *            die vorletzten im Pfad zurück gegeben. Bei Pfaden, die nur aus
     *            einer Edge bestehen ist das das Ausgangselement des Pfades,
     *            also das ModelElement des Dialoges.
     * @return
     */
    public final List<ElementContainer> getConnectedContainer(final GraphDocument doc, final boolean forlast) {
        List<ElementContainer> returnCollection = new ArrayList<>();
        for (PathResultTreeNode node : completePathLeafs) {
            ModelElement endElement = forlast ? node.getStartElement() : node.getEndElement();
            ElementContainer ec = endElement.getContainer(doc);
            if (ec != null && !returnCollection.contains(ec)) {
                returnCollection.add(ec);
            }
        }
        return returnCollection;
    }

    /**
     * @return a list of all {@link SimplePath}s from root the complete path
     *         leafs
     */
    public final List<SimplePath> getCompletePaths() {
        List<SimplePath> simplePaths = new ArrayList<>();
        List<PathResultTreeNode> completePathLeafs = getCompletePathLeafs();
        for (PathResultTreeNode leaf : completePathLeafs) {
            PathResultTreeNode[] pathToRoot = leaf.getPathToRoot();
            List<ElementaryPath> elementaryPaths = new ArrayList<>();
            for (int i = 2; i < pathToRoot.length; i++) { //the first node is root and the secont the startelement node -> start with third node
                PathResultTreeNode node = pathToRoot[i];
                ElementaryPath elementaryPath = node.getElementaryPath();
                elementaryPaths.add(elementaryPath);
            }
            SimplePath simplePath = SimplePath.create(elementaryPaths);
            simplePaths.add(simplePath);
        }
        return simplePaths;
    }

    //Aufbau des Models

    @Override
    public void reload(final TreeNode irrelevantNode) {
        //MAXIME: es wird immer der ganze Baum neu aufgebaut - egal welcher Knoten übergeben wurde!
        PathResultTreeNode root = getRoot();
        root.removeAllChildren();
        completePathLeafs.clear();
        incompletePathLeafs.clear();
        if (startElementsCollections == null || startElementsCollections.isEmpty()) {
            return;
        }
        //mehrere Startmemngen wurden übergeben (das geht nur bei einem alles umschließenden Parallelmetapfad)
        if (startElementsCollections.size() > 1) {
            MetaPath realFirstMetaPath = metaPath;
            //solange aus dem SerialMetaPath den jeweils ersten MetaPath holen, bis man bei einem
            //MetaPath ist, der selbst kein SerialMetaPath mehr ist
            while (realFirstMetaPath instanceof SerialMetaPath) {
                realFirstMetaPath = ((SerialMetaPath) realFirstMetaPath).getSubMetaPaths().get(0);
                //TODO: hier muss ein paralleler MetaPfad kommen, den wir später behandeln...
            }

            //eine einelementige Startelementliste wurde übergeben
        } else {
            Collection<ModelElement> startElements = startElementsCollections.get(0);
            for (ModelElement me : startElements) {
                ElementaryPath elementaryPath = new ElementaryPath(null, me);
                PathResultTreeNode node = new PathResultTreeNode(elementaryPath, PathResultTreeNode.NodeType.START_ELEMENT);
                List<PathResultTreeNode> pathNodes = addPath(node, metaPath, false);
                addCompleteLeafs(pathNodes);
                if (!keepMultipleEqualsBranches) {
                    removeIncompleteBranchesContainedInCompleteBranches();
                }
                if (node.getChildCount() > 0) {
                    root.add(node);
                }
            }
        }
        super.reload(root);
    }

    /**
     * Removes all incomplete branches which are contained in a complete branch.
     * Such branches are created by UnionMetaPaths (and maybe other parallel
     * MetaPaths) where at least two parallel metaPaths are partly equals and
     * for one of the metaPaths you can find a complete path and for another you
     * can only find an incomplete path for the equals part of the first
     * metaPath. These incomplete and so on multiple paths are removed here. It
     * is not possible to check this already during the pathTree creation.
     */
    private void removeIncompleteBranchesContainedInCompleteBranches() {
        for (int i = incompletePathLeafs.size() - 1; i >= 0; i--) {
            PathResultTreeNode incompletePathNode = incompletePathLeafs.get(i);
            for (PathResultTreeNode completePathNode : completePathLeafs) {
                if (completePathNode.containsPath(incompletePathNode)) {
                    deleteBranch(incompletePathNode);
                    incompletePathLeafs.remove(i);
                    break;
                }
            }
        }
    }

    /**
     * Funktion, die für den übergebenen {@link MetaPath} die richtige
     * Unterfunktion aufruft.
     *
     * @param startNode
     * @param metaPath
     * @param isSubStep
     * @return
     */
    private List<PathResultTreeNode> addPath(final PathResultTreeNode startNode, final MetaPath metaPath, final boolean isSubStep) {
        if (metaPath instanceof ElementaryMetaPath) {
            return addPath(startNode, (ElementaryMetaPath) metaPath, isSubStep);
        } else if (metaPath instanceof SerialMetaPath) {
            return addPath(startNode, (SerialMetaPath) metaPath, isSubStep);
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
     * Hängt an den übergebenen startNode einen Knoten an, der den übergebenen
     * MetaPath und
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
    private List<PathResultTreeNode> addPath(final PathResultTreeNode startNode, final SerialMetaPath metaPath, final boolean isSubStep) {
        List<PathResultTreeNode> resultNodes = new ArrayList<>();
        resultNodes.add(startNode);
        for (MetaPath subMetaPath : metaPath.getSubMetaPaths()) {
            List<PathResultTreeNode> actStartNodes = new ArrayList<>(resultNodes);
            resultNodes.clear();
            for (PathResultTreeNode actStartNode : actStartNodes) {
                List<PathResultTreeNode> subMetaPathEndNodes;
                subMetaPathEndNodes = addPath(actStartNode, subMetaPath, subMetaPath instanceof ElementaryMetaPath ? isSubStep : true);
                if (subMetaPathEndNodes == null || subMetaPathEndNodes.size() == 0) {
                    if (!keepIncompleteBranches) {
                        deleteBranch(actStartNode);
                    } else {
                        addIncompleteLeaf(actStartNode);
                    }
                } else {
                    resultNodes.addAll(subMetaPathEndNodes);
                }
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
        for (MetaPath subMetaPath : metaPath.getSubMetaPaths()) {
            List<PathResultTreeNode> pathResultTreeNodes = addPath(startNode, subMetaPath, subMetaPath instanceof ElementaryMetaPath ? isSubStep : true);
            resultNodes.addAll(pathResultTreeNodes);
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
        return addPath(startNode, metaPath, true, isSubStep);
    }

    /**
     * @param startNode
     * @param metaPath
     * @param isSubStep
     * @return
     */
    private List<PathResultTreeNode> addPath(final PathResultTreeNode startNode, final DifferenceMetaPath metaPath, final boolean isSubStep) {
        return addPath(startNode, metaPath, false, isSubStep);
    }

    /**
     * @param startNode
     * @param metaPath
     * @param sectionAndNotDifference
     * @param isSubStep
     * @return
     */
    private List<PathResultTreeNode> addPath(final PathResultTreeNode startNode, final ListMetaPath metaPath, final boolean sectionAndNotDifference, final boolean isSubStep) {
        List<PathResultTreeNode> resultNodes = new ArrayList<>();
        MetaPath firstMetaPath = null;
        //1. Pfad anhängen, dann von allen weiteren die Endelemente prüfen, ob jedes der EndElemente in jedem der
        //weiteren Pfade auch vorkommt -> wenn nicht, das endElement löschen und ggf. deleteBranch mit aktualisierung der IncompleteLeafs anstoßen
        PathResultTreeNode startNodeClone = new PathResultTreeNode(startNode);
        List<PathResultTreeNode> nextPathResultNodes = new ArrayList<>();
        for (MetaPath subMetaPath : metaPath.getSubMetaPaths()) {
            if (firstMetaPath == null) {
                firstMetaPath = subMetaPath;
                List<PathResultTreeNode> pathResultTreeNodes = addPath(startNode, subMetaPath, subMetaPath instanceof ElementaryMetaPath ? isSubStep : true);
                resultNodes.addAll(pathResultTreeNodes);
            } else {
                List<PathResultTreeNode> pathResultTreeNodes = addPath(startNodeClone, subMetaPath, isSubStep);
                nextPathResultNodes.addAll(pathResultTreeNodes);
            }
        }
        //Set aller Elemente, die Ergenis der MetaPfade nach dem ersten MetaPfad sind
        Set<ModelElement> nextPathsEndElements = new HashSet<>();
        for (PathResultTreeNode notResultNode : nextPathResultNodes) {
            ModelElement endElement = notResultNode.getEndElement();
            nextPathsEndElements.add(endElement);
        }

        //alle Zweige im Baum löschen, bei denen das EndElement in der 2. Liste
        //vorkommt oder nicht vorkommt je nachdem, ob man eine Schnitt- oder eine
        //Differenzmenge brechnet und auch von hinten aus der Gesamtliste löschen
        for (int i = resultNodes.size() - 1; i >= 0; i--) {
            PathResultTreeNode resultNode = resultNodes.get(i);
            ModelElement endElement = resultNode.getEndElement();
            boolean contains = nextPathsEndElements.contains(endElement);
            if (sectionAndNotDifference && !contains || !sectionAndNotDifference && contains) {
                deleteBranch(resultNode);
                resultNodes.remove(resultNode);
            }
        }
        return resultNodes;
    }

    /**
     * @param leafNode
     * @return
     */
    private void addCompleteLeafs(final Collection<PathResultTreeNode> leafNodes) {
        for (PathResultTreeNode leafNode : leafNodes) {
            addCompleteLeaf(leafNode);
        }
    }

    /**
     * @param leafNode
     * @return
     */
    private void addCompleteLeaf(final PathResultTreeNode leafNode) {
        addLeaf(leafNode, completePathLeafs);
    }

    /**
     * @param leafNode
     * @return
     */
    private void addIncompleteLeaf(final PathResultTreeNode leafNode) {
        addLeaf(leafNode, incompletePathLeafs);
    }

    /**
     * @param leafNode
     * @param resultList
     * @return
     */
    private void addLeaf(final PathResultTreeNode leafNode, final List<PathResultTreeNode> resultList) {
        if (nodeElementIsDefinedEndElement(leafNode) && (keepMultipleEqualsBranches || !listContainsNodeEqualsTo(leafNode, resultList))) {
            resultList.add(leafNode);
        }
    }

    /**
     * @param node
     * @return <code>true</code> if the nodes endElement is contained in the
     *         defined {@value #endElements} or the defined endElements are
     *         empty
     */
    private boolean nodeElementIsDefinedEndElement(final PathResultTreeNode node) {
        if (endElements.isEmpty()) {
            return true;
        }
        ModelElement endElement = node.getEndElement();
        return endElements.contains(endElement);
    }

    /**
     * Compares the {@link PathResultTreeNode} with the other ones in the list
     * with the {@link PathResultTreeNode#equalsTo(Object)} method.
     *
     * @param node
     * @param nodeList
     * @return
     */
    private boolean listContainsNodeEqualsTo(final PathResultTreeNode node, final List<PathResultTreeNode> nodeList) {
        for (PathResultTreeNode nodeFromList : nodeList) {
            if (node.equalsTo(nodeFromList)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Löscht den übergebenen Knoten und alle darüber die nur 1 Kind (den
     * jeweils zu löschenden Knoten) haben.
     *
     * @param node
     */
    private void deleteBranch(PathResultTreeNode node) {
        PathResultTreeNode parent = node.getParent();
        while (parent != null && parent.getChildCount() == 1) {
            node = parent;
            parent = node.getParent();
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
    //            for (MetaPath subMetaPath : metaPath.getMetaPaths()) {
    //                resultNodes.addAll(resultNodes);
    //            }
    //        } else {
    //            List<MetaPath> metaPaths = metaPath.getMetaPaths();
    //            MetaPath firstSubMetaPath = metaPaths.get(0);
    //            if (firstSubMetaPath instanceof ElementaryMetaPath) {
    //                if (metaPath instanceof DifferenceMetaPath) {
    //                    for (MetaPath subMetaPath : metaPath.getMetaPaths()) {
    //                    }
    //                } else if (metaPath instanceof SectionMetaPath) {
    //                    for (MetaPath subMetaPath : metaPath.getMetaPaths()) {
    //                    }
    //                }
    //            }
    //        }
    //        return resultNodes;
    //    }

    /**
     * Liefert alle mit dem übergebenen Element verbundenen Elemente und Kanten
     * als Ergebnisknoten zum einhängen in den Ergebnisbaum zurück.
     *
     * @param me Element, für das die verbundenen Elemente gesucht werden sollen
     * @param metaPath Elementar-MetaPath, über den Elemente verbunden sein
     *            sollen
     * @param isSubStep Wenn <code>true</code> werden die Ergebnsiknoten als
     *            Unterpfadschritt markiert.
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
        if (!metaPath.isStartClass(meClass)) {
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
