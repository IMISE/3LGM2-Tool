package de.imise.tool3lgm.graphtools.model;

import static de.imise.tool3lgm.Static.getMainFrame;
import static de.imise.tool3lgm.graphtools.dialog.OverwriteDialog.OverwriteOption.IGNORE;
import static de.imise.tool3lgm.graphtools.dialog.OverwriteDialog.OverwriteOption.JOIN;
import static de.imise.tool3lgm.graphtools.dialog.OverwriteDialog.OverwriteOption.OVERWRITE;
import static de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType.DATA_CHANGED;
import static de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType.ELEMENT_GRAPHICS_CHANGED;
import static de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType.SELECTION_CHANGED;
import static de.imise.tool3lgm.graphtools.undoredo.TransactionManager.STANDARD_PID;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.swing.JOptionPane;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.Tool3lgmModelType.ModelCategory;
import de.imise.tool3lgm.graphtools.analyse.redundancy.SimpleRedundancyAnalysis;
import de.imise.tool3lgm.graphtools.analyse.redundancy.SimpleRedundancyAnalysisDefinitions.SingleSimpleRedundancyAnalysisDefinition;
import de.imise.tool3lgm.graphtools.dialog.OverwriteDialog;
import de.imise.tool3lgm.graphtools.dialog.OverwriteDialog.OverwriteOption;
import de.imise.tool3lgm.graphtools.dialog.OverwriteDialog.OverwriteQuestionAnswer;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Bendpoint;
import de.imise.tool3lgm.graphtools.metamodel.elements.CompositionEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.InstanciationEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.metapaths.ElementaryMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.ElementaryMetaPath.Type;
import de.imise.tool3lgm.graphtools.path.metapaths.ElementaryMetaPathHandler;
import de.imise.tool3lgm.graphtools.path.metapaths.MetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.ParallelMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.path.paths.AbstractPath;
import de.imise.tool3lgm.graphtools.path.paths.ElementaryPath;
import de.imise.tool3lgm.graphtools.path.paths.ParallelPath;
import de.imise.tool3lgm.graphtools.path.paths.SimplePath;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.view.container.BendpointContainer;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.LayerContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.xml.ToolXMLClipboardWriter;

/**
 * @author thomas
 * @created 16.02.2004
 */
public class LGMGraphDocument extends GraphDocument {

    /**
     * @param _gdcoll
     */
    public LGMGraphDocument(final GDCollection _gdcoll) {
        super(_gdcoll);
        setTitle(getResString("uebersicht"));
    }

    @Override
    protected void dispatch_command(final GDCommands command, final String[] argv, final int pid) {
        switch (command) {
        case MODEL_ACTION_COPY:
            copyToClipboard();
            break;

        case MODEL_ACTION_CUT:
            cutToClipboard();
            break;

        case MODEL_ACTION_PASTE:
            pasteClipboard();
            break;

        case MODEL_ACTION_HIDE_UNASSOCIATED: {
            MetaModel metaModel = getMetaModel();
            Class<? extends ModelElement> elementClass = metaModel.getClassForName(argv[0]);
            Class<? extends Edge> egdeClass = metaModel.getClassForName(argv[1]).asSubclass(Edge.class);
            for (ElementContainer ec : getElementContainers(elementClass, true)) {
                ModelElement me = ec.getElement();
                List<ElementContainer> connectedContainer = me.getConnectedContainers(this, egdeClass);
                if (connectedContainer.isEmpty()) {
                    ec.setVisible(false);
                }
            }
            distributeEvent(ELEMENT_GRAPHICS_CHANGED, pid);
            break;
        }
        case MODEL_ACTION_UNHIDE_ALL: {
            MetaModel metaModel = getMetaModel();
            Class<? extends ModelElement> elementClass = metaModel.getClassForName(argv[0]);
            for (ElementContainer ec : getElementContainers(elementClass, true)) {
                ec.setVisible(true);
            }
            distributeEvent(ELEMENT_GRAPHICS_CHANGED, pid);
            break;
        }
        case MODEL_ACTION_CREATE_INSTANCIATION: {
            GraphDocument sourceDoc = Static.getGraphDocument(argv[0]);
            LGMGraphDocument targetDoc = Static.getGraphDocument(argv[1]);
            Class<? extends ModelElement> edgeClass = metaModel.getClassForName(argv[2]);
            Class<? extends InstanciationEdge> instanciationEdgeClass = edgeClass.asSubclass(InstanciationEdge.class);
            ModelElement master = sourceDoc.findElementCoded(argv[3]);
            createInstance(sourceDoc, targetDoc, instanciationEdgeClass, master, pid);
            break;
        }
        default:
            super.dispatch_command(command, argv, pid);
        }
    }

    private final List<SimpleRedundancyAnalysis> simpleRedundancyAnalysis = new ArrayList<>();

    /**
     * Wenn es bereits eine {@link SimpleRedundancyAnalysis} mit den übergebenen
     * Pfaden gibt, wird diese entfernt. Ansonsten wird eine neue hinzugefügt.
     * Sobald es sie gibt, wird sie auch ausgeführt.
     *
     * @param singleSimpleRedundancyDefinition
     */
    public final void setSimpleRedundancyAnalysisState(final SingleSimpleRedundancyAnalysisDefinition singleSimpleRedundancyDefinition, final boolean state) {
        for (int i = simpleRedundancyAnalysis.size() - 1; i >= 0; i--) {
            SimpleRedundancyAnalysis analyse = simpleRedundancyAnalysis.get(i);
            if (analyse.hasDefinition(singleSimpleRedundancyDefinition)) {
                if (state) {
                    return;
                }
                analyse.removeGraphTexts();
                simpleRedundancyAnalysis.remove(i);
            }
        }
        if (state) {
            SimpleRedundancyAnalysis analyse = new SimpleRedundancyAnalysis(singleSimpleRedundancyDefinition, this);
            simpleRedundancyAnalysis.add(analyse);
            analyse.computeRedundancy();
        }
    }

    /**
     * @param singleSimpleRedundancyDefinition
     * @return
     */
    public boolean isSimpleRedundancyAnalysis(final SingleSimpleRedundancyAnalysisDefinition singleSimpleRedundancyDefinition) {
        for (SimpleRedundancyAnalysis analysis : simpleRedundancyAnalysis) {
            if (analysis.hasDefinition(singleSimpleRedundancyDefinition)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     */
    public void updateSimpleRedundancyAnalysis() {
        for (SimpleRedundancyAnalysis redundancyAnalysis : simpleRedundancyAnalysis) {
            redundancyAnalysis.computeRedundancy();
        }
    }

    /**
     *
     */
    private synchronized void copyToClipboard() {
        if (selectedContainer.size() == 0) {
            return;
        }
        ToolXMLClipboardWriter.writeClipboard(this);
    }

    /**
     *
     */
    private synchronized void cutToClipboard() {
        start_transaction(STANDARD_PID);
        copyToClipboard();
        //man muss die Selektion clonen, da sie sich wärend des Löschens ändert
        gdcoll.deleteElements(getSelectedElements(), this, STANDARD_PID);
        finish_transaction(STANDARD_PID);
        distributeEvent(DATA_CHANGED);
    }

    /**
     * @return
     */
    public static synchronized final boolean isClipboardAvailable() {
        return new File(Tool3lgmConstants.CLIPBOARD_PATH).exists();
    }

    public final SortedSelection getSortedSelection() {
        Iterable<NodeContainer>[] sortingElements = new Iterable[layer.length];
        for (int i = 0; i < layer.length; i++) {
            sortingElements[i] = layer[i].getGraphNodeContainers();
        }
        return selectedContainer.getSortedSelection(sortingElements);
    }

    /**
     *
     */
    private synchronized void pasteClipboard() {
        File file = new File(Tool3lgmConstants.CLIPBOARD_PATH);
        if (!file.exists()) {
            return;
        }

        int pid = TransactionManager.STANDARD_PID;
        try {
            start_transaction(pid);
            addRedoCommand(GDCommands.MODEL_ACTION_PASTE + " ", pid);
            addUndoCommand(GDCommands.MODEL_ACTION_DELETE_FROM_MODEL + " ", pid);
            deselectAll(true);
            getCollection().loadClipboard(file);
        } catch (Exception e) {
            Log.show(Log.ERROR, getResString("FehlerAllgemein"), e);
            Object[] buttons = new Object[] {
                    getResString("ok")
            };
            JOptionPane.showOptionDialog(Static.getMainFrame(), getResString("oeffnenfehler") + "\n" + file.getPath() + "\n" + e.getMessage(), getResString("tool3lgm"), JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, buttons, null);
            e.printStackTrace();
            return;
        }

        finish_transaction(pid);
        distributeEvent(DATA_CHANGED, pid);
    }

    /**
     * @param istream
     */
    public synchronized void pasteInputStream(final InputStream istream) {
        start_transaction(STANDARD_PID);
        addUndoCommand(GDCommands.MODEL_ACTION_DELETE_FROM_MODEL + " ", STANDARD_PID);
        deselectAll(true);
        try {
            getCollection().loadFile(istream);
        } catch (Exception e) {
            undo(STANDARD_PID);
            Log.show(Log.ERROR, getResString("FehlerAllgemein"), e);
            Object[] buttons = new Object[] {
                    getResString("ok")
            };
            JOptionPane.showOptionDialog(getMainFrame(), "", getResString("tool3lgm"), JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, buttons, null);
            e.printStackTrace();
            return;
        }

        finish_transaction(STANDARD_PID);
        distributeEvent(DATA_CHANGED, STANDARD_PID);
    }

    /**
     * Liste aller Kanten, bei denen das das eine Endelement gerade kopiert
     * werden soll und das andere nicht kopiert werden soll aber bereits im
     * Zielmodell vorkommt
     *
     * @param sourceElements
     * @param targetDoc
     */
    private static final void addSplittedSourceEdgesToCopy(final List<ModelElement> sourceElements, final GraphDocument targetDoc) {
        GraphDocument targetMainDoc = targetDoc.getCollection().getMainDoc();
        for (int i = 0; i < sourceElements.size(); i++) {
            ModelElement me = sourceElements.get(i);
            if (me instanceof Bendpoint) {
                continue;
            }
            for (Edge edge : me.getEdges()) {
                if (!sourceElements.contains(edge)) {
                    ModelElement other = edge.getOther(me);
                    String otherHash = other.getHashString();
                    ModelElement destElement = targetMainDoc.findElementCoded(otherHash);
                    if (destElement != null) {
                        sourceElements.add(edge);
                    }
                }
            }
        }
    }

    /**
     * @param me
     * @param targetDoc
     */
    public static final void copyToModel(final ModelElement me, final LGMGraphDocument targetDoc) {
        GDCollection gdcoll = me.getCollection();
        GraphDocument selectedDoc = gdcoll.getSelectedDoc();
        ElementContainer ec = selectedDoc.getElementContainer(me);
        copyToModel(ec, targetDoc);
    }

    /**
     * @param ec
     * @param targetDoc
     */
    public static final void copyToModel(final ElementContainer ec, final LGMGraphDocument targetDoc) {
        Collection<ElementContainer> sourceElements = ImmutableList.of(ec);
        GraphDocument sourceDoc = ec.getGraphDocument(); //hard cast (every GraphDocument is a LGMGraphDocument)
        copyToModel(sourceElements, sourceDoc, targetDoc);
    }

    /**
     * @param sourceDoc
     * @param targetDoc
     */
    public static final void copySelectedToModel(final GraphDocument sourceDoc, final LGMGraphDocument targetDoc) {
        copyToModel(sourceDoc.selectedContainer, sourceDoc, targetDoc);
    }

    /**
     * @param sourceElements
     * @param sourceDoc
     * @param targetDoc
     */
    private static final void copyToModel(final Collection<ElementContainer> sourceElements, final GraphDocument sourceDoc, final LGMGraphDocument targetDoc) {

        GDCollection sourceCollection = sourceDoc.getCollection();
        GraphDocument sourceMainDoc = sourceCollection.getMainDoc();
        GDCollection targetCollection = targetDoc.getCollection();
        GraphDocument targetMainDoc = targetCollection.getMainDoc();

        sourceCollection.removeInferenceEdges(true, STANDARD_PID);
        targetCollection.removeInferenceEdges(true, STANDARD_PID);

        List<ModelElement> sourceElementsAndDependents = new ArrayList<>();
        HashSet<UserField> sourceUserFields = new HashSet<>();
        CopyDependencyResolver copyDependencyResolver = sourceCollection.getCopyDependencyResolver();
        copyDependencyResolver.resolveCopyDependencies(sourceElements, sourceElementsAndDependents, sourceUserFields);
        //Liste aller Kanten, bei denen das das eine Endelement gerade kopiert werden soll und das
        //andere nicht kopiert werden soll aber bereits im Zielmodell vorkommt hinzufügen
        addSplittedSourceEdgesToCopy(sourceElementsAndDependents, targetDoc);

        for (UserField uf : sourceUserFields) {
            if (uf != null) {
                targetCollection.getUserFieldDefinitions().add(uf); // es könnte sein, dass hier Mist passiert und UserFields immer wieder geaddet werden, wenn man ein Element übernimmt
            }
        }

        List<ElementContainer> tmpActive = new ArrayList<>(sourceDoc.selectedContainer);
        List<Edge> edges = new ArrayList<>();
        List<BendpointContainer> bendpoints = new ArrayList<>();

        sourceMainDoc.deselectAll(false);

        targetMainDoc.start_transaction(STANDARD_PID);

        try {
            targetMainDoc.deselectAll(true);

            //Icons kopieren
            GDCollectionIconTable sourceIconTable = sourceCollection.getIconTable();
            GDCollectionIconTable targetIconTable = targetCollection.getIconTable();
            targetIconTable.putAll(sourceIconTable); // Warum werden hier einfach alle Icons übernommen? Überprüfen!

            ModelCategory sourceModelCategory = sourceCollection.getModelCategory();
            OverwriteOption defaultOverwriteOption = sourceModelCategory == ModelCategory.TEMPLATE ? JOIN : null;
            //bei Templates soll nicht nachgefragt (applyToAll == true) und immer gejoint werden
            OverwriteDialog.OverwriteQuestionAnswer answer = new OverwriteQuestionAnswer(defaultOverwriteOption, defaultOverwriteOption != null);
            for (ModelElement sourceElement : sourceElementsAndDependents) {
                ElementContainer sourceContainer = sourceElement.getContainer(sourceDoc);
                if (sourceContainer == null) {
                    sourceContainer = sourceElement.getContainer(sourceMainDoc);
                }

                String sourceHash = sourceElement.getHashString();
                ModelElement targetElement = targetMainDoc.findElementCoded(sourceHash);
                if (targetElement != null) {
                    if (!answer.applyToAll) {
                        sourceDoc.select(sourceContainer, STANDARD_PID);
                        sourceDoc.distributeEvent(SELECTION_CHANGED, sourceContainer, STANDARD_PID);
                        answer = OverwriteDialog.showDialog(targetElement, sourceElement);
                    }

                    if (answer.overwriteOption == OVERWRITE) {
                        //hier müsste das alte evtl. noch gelöscht werden !?
                    } else if (answer.overwriteOption == JOIN) {
                        targetDoc.joinElements(targetElement, sourceElement, sourceDoc, true);
                        if (targetElement instanceof Edge) {
                            Edge edge = (Edge) targetElement;
                            edge.reconnect(targetCollection);
                            edge.refreshText();
                        }
                    } else if (answer.overwriteOption == IGNORE) {
                        continue;
                    }
                    //wenn der Hash des zu kopierenden Elementes noch nicht im Modell vorkommt
                } else {
                    //first create the element in the mainDoc
                    ElementContainer targetMainContainer = sourceContainer.clone(true, targetMainDoc);
                    targetElement = targetMainContainer.getElement();
                    targetElement.setHashString(sourceHash);
                    targetMainContainer.setVisible(true);
                    targetMainContainer.setExpanded(true);
                    targetMainContainer.setHighLight(false);
                    targetMainContainer.refreshText();
                    int layer = targetElement.layerFor();
                    LayerContainer targetMainDocLayer = targetMainDoc.getLayer(layer);
                    targetMainDocLayer.add(targetMainContainer);
                    if (targetElement instanceof Edge) {
                        //edges are inserted in the szenario separately
                        edges.add((Edge) targetElement);
                        //all not unique elements must be inserted to the szenario
                    } else if (!targetElement.isUnique() && targetDoc instanceof Szenario) {
                        //create container for the szenario and adds this container to the
                        //container map of the element
                        ElementContainer targetContainer = sourceContainer.clone(false, targetDoc);
                        targetContainer.setElement(targetElement);
                        if (targetElement instanceof Bendpoint) {
                            //bendpoints separately too
                            bendpoints.add((BendpointContainer) targetContainer);
                        } else {
                            targetElement.updateHTMLName(targetContainer);
                            //add the container to the layer of the szenario
                            targetContainer.refreshText();
                            LayerContainer targetDocLayer = targetDoc.getLayer(layer);
                            targetDocLayer.add(targetContainer);
                        }
                    }
                    targetMainDoc.addToSelection(targetMainContainer, STANDARD_PID);
                }
            }
            for (Edge edge : edges) {
                if (!edge.reconnect(targetCollection)) {
                    targetCollection.deleteElement(edge, STANDARD_PID);
                } else {
                    EdgeContainer edgeCont = (EdgeContainer) edge.getContainer(targetMainDoc);
                    //this call adds a edgeContainer to all szenarios where it mus be added
                    //the tarte mainDoc and the target szenarion already contain the edge container
                    targetCollection.addEdge(edgeCont, STANDARD_PID);
                    if (!edge.isUnique() && targetDoc instanceof Szenario) {
                        EdgeContainer newC = (EdgeContainer) edge.getContainer(targetDoc);
                        if (newC == null) {
                            throw new Exception(sourceDoc.getResString("fehler"));
                        }
                        for (BendpointContainer bc : newC.iterateBendpointContainers()) {
                            int layer = edge.layerFor();
                            LayerContainer targetDocLayer = targetDoc.getLayer(layer);
                            targetDocLayer.add(bc);
                        }
                        newC.computeBorderPoints();
                    }
                    ElementContainer edgeC = edge.getContainer(targetMainDoc);
                    targetMainDoc.addToSelection(edgeC, STANDARD_PID);
                }
            }
            List<EdgeContainer> edgeConts = new ArrayList<>();
            while (!bendpoints.isEmpty()) {
                BendpointContainer kp = bendpoints.remove(0);
                BendpointContainer oldKP = sourceDoc.findBendpointContainerCoded(kp.getHashString());
                //der Container kann null sein, wenn die zu kopierende Kante auch noch mind. einen Knickpunkt in einem
                //anderen Teilmodell hat, denn es werden beim resolven der CopyDependencies alle Knickpunkte der Kante aus
                //allen Teilmodellen eingesammelt
                if (oldKP == null) {
                    continue;
                }
                Bendpoint bendpoint = kp.getBendpoint();
                String edgeHash = bendpoint.getEdgeHash();
                EdgeContainer kC = targetDoc.findEdgeContainerCoded(edgeHash);

                Bendpoint oldBendpoint = oldKP.getBendpoint();
                EdgeContainer oldKC = oldBendpoint.getOwner();
                if (oldKC == null) {
                    oldKC = sourceDoc.findEdgeContainerCoded(kC.getHashString());
                }
                if (kC != null) {
                    if (!edges.contains(kC.getElement())) {
                        edgeConts.add(kC);
                    }
                    bendpoint.setOwner(kC);
                    int indexOfBendpoint = oldKC.getIndexOfBendpoint(oldBendpoint);
                    kC.setBendpointContainer(kp, indexOfBendpoint);
                    int layer = kC.layerFor();
                    LayerContainer targetDocLayer = targetDoc.getLayer(layer);
                    targetDocLayer.add(kp);
                }
            }
            for (EdgeContainer edgeC : edgeConts) {
                edgeC.computeBorderPoints();
            }

            targetMainDoc.finish_transaction(STANDARD_PID);
        } catch (Exception ex) {
            targetMainDoc.undo(STANDARD_PID);
            Log.show(Log.ERROR, sourceDoc.getResString("FehlerKorrupt") + "\n" + targetCollection.getName(), ex);
        }
        sourceDoc.start_transaction(STANDARD_PID, false);
        sourceDoc.deselectAll(true);
        for (int j = 0; j < tmpActive.size(); j++) {
            sourceDoc.addToSelection(tmpActive.get(j), STANDARD_PID);
        }
        sourceDoc.finish_transaction(TransactionManager.STANDARD_PID, false);
        sourceDoc.distributeEvent(SELECTION_CHANGED);

        sourceCollection.createInferenceEdges(true, STANDARD_PID);
        targetCollection.createInferenceEdges(true, STANDARD_PID);

        //bei Bedarf anschalten, um zu sehen, wie das Modell danach aussieht
        //GDCollectionPrinter.print(targetCollection);

        targetDoc.distributeEvent(DATA_CHANGED);

    }

    /**
     * Fuehrt selektierte ModelElemente in diesem oder in beiden Modellen
     * zusammen
     *
     * @param doc2
     * @param saveInBoth
     */
    public void joinSelectedElements(final GraphDocument doc2) {
        if (selectedContainer.size() != 1 || doc2.selectedContainer.size() != 1) {
            return;
        }
        ElementContainer lastSelected = selectedContainer.getLastSelected();
        ModelElement me1 = lastSelected.getElement();
        lastSelected = doc2.selectedContainer.getLastSelected();
        ModelElement me2 = lastSelected.getElement();

        joinElements(me1, me2, doc2, true);
        distributeEvent(DATA_CHANGED);
    }

    /**
     * @param me1
     * @param me2
     * @param doc2
     * @param joinNameDescriptionAndUserfields parameter for
     */
    private void joinElements(final ModelElement me1, final ModelElement me2, final GraphDocument doc2, final boolean joinNameDescriptionAndUserfields) {
        if (me1 instanceof Bendpoint) {
            return;
        }

        String me2hash = me2.getHashString();
        ModelElement me3 = findElementCoded(me2hash);
        boolean overwriteHashString = me3 == null || me3 == me2;
        if (me1.join(me2, overwriteHashString, joinNameDescriptionAndUserfields) == null) {
            return;
        }
        me1.refreshText();

        for (Edge edge : me2.getEdges()) {
            Edge oldEdge;
            /* vorwaerts */
            if (edge.getStart().equals(me2)) {
                me3 = findElementCoded(edge.getEnd().getHashString());
                if (me3 == null || me3 == me1) {
                    continue;
                }
                if (me1.isConnectedWith(me3, edge.getClass())) {
                    continue;
                }

                oldEdge = edge;
                edge = edge.clone();
                edge.setStartAndInsert(me1);
                edge.setEndAndInsert(me3);
                /* rueckwaerts */
            } else if (edge.getEnd().equals(me2)) {
                me3 = findElementCoded(edge.getStart().getHashString());
                if (me3 == null || me3 == me1) {
                    continue;
                }
                if (me1.isConnectedWith(me3, edge.getClass())) {
                    continue;
                }

                oldEdge = edge;
                edge = edge.clone();
                edge.setStartAndInsert(me3);
                edge.setEndAndInsert(me1);
            } else {
                continue;
            }

            //Main Doc
            LGMGraphDocument mainDoc = gdcoll.getMainDoc();
            int edgeLayer = edge.layerFor();
            LayerContainer lc = mainDoc.getLayer(edgeLayer);
            ElementContainer edgeContainer = edge.createContainer(mainDoc);
            lc.add(edgeContainer);
            //Szenario
            if (this != mainDoc) {
                if (me1.getContainer(this) != null) {
                    if (me3.getContainer(this) != null) {
                        edgeContainer = edge.createContainer(this);
                        lc = getLayer(edgeLayer);
                        lc.add(edgeContainer);
                    }
                }
            }

            joinElements(edge, oldEdge, doc2, joinNameDescriptionAndUserfields);
        }
    }

    /**
     * @param targetDoc
     * @param instanciationEdgeClass
     * @param master
     * @param pid
     * @return
     */
    public NodeContainer createInstance(final LGMGraphDocument targetDoc, final Class<? extends InstanciationEdge> instanciationEdgeClass, final ModelElement master, final int pid) {
        return createInstance(null, targetDoc, instanciationEdgeClass, master, pid);
    }

    /**
     * @param sourceDoc
     * @param targetDoc
     * @param instanciationEdgeClass
     * @param master
     * @param pid
     * @return
     */
    private NodeContainer createInstance(GraphDocument sourceDoc, LGMGraphDocument targetDoc, final Class<? extends InstanciationEdge> instanciationEdgeClass, ModelElement master, final int pid) {
        if (targetDoc == null) {
            targetDoc = this;
        }
        if (sourceDoc == null) {
            sourceDoc = master != null ? master.getSelectedDoc() : this;
        }
        if (master == null) {
            ElementContainer lastSelected = sourceDoc.getLastSelected();
            ModelElement lastSelectedElement = lastSelected.getElement();
            master = lastSelectedElement;
        }
        targetDoc.start_transaction(pid);
        //if the source doc differs from target doc (e.g. it's a template) then
        //first copy the element in the targetDoc and then create the instance
        if (sourceDoc != targetDoc) {
            ElementContainer sourceElementContainer = sourceDoc.getElementContainer(master);
            copyToModel(sourceElementContainer, targetDoc);
        }
        String masterHashString = master.getHashString();
        master = targetDoc.findElementCoded(masterHashString);

        //Hauptkante anlegen
        Class<? extends ModelElement> class2Create = Edge.getEndClass(instanciationEdgeClass);
        String name = master.getName();
        name = targetDoc.getNextNewName(name, class2Create, false);
        name = GENERATED_NAME_PREFIX + name;
        NodeContainer instanceContainer = targetDoc.createNodeAndContainer(class2Create, name, pid);
        if (instanceContainer != null) { // kann null sein, wenn der Dialog zur Namenseingabe abgebrochen wurde
            ModelElement instance = instanceContainer.getElement();
            String masterDescription = master.getDescription();
            instance.setDescription(masterDescription);
            GDCollection targetGDColl = targetDoc.getCollection();
            targetGDColl.link(instanciationEdgeClass, master, instance, pid);

            //Ebenfalls zu instanziierende Nebenpfade anlegen
            MetaModel metaModel = getMetaModel();
            for (SimpleMetaPath metaPath : metaModel.getInstanciablePath(instanciationEdgeClass)) {
                int path2CreateStartIndex = 0;
                for (; path2CreateStartIndex < metaPath.getSubMetaPathCount(); path2CreateStartIndex++) {
                    List<ElementaryMetaPath> elementaryMetaPaths = metaPath.getElementaryMetaPaths();
                    ElementaryMetaPath elementaryMetaPath = elementaryMetaPaths.get(path2CreateStartIndex);
                    if (elementaryMetaPath.hasEdgeClass(InstanciationEdge.class)) {
                        break;
                    }
                }
                //für diesen Pfadteil müssen die verbundenen Elemente herausgesucht werden
                SimpleMetaPath subPathConnected = metaPath.getSubPath(0, path2CreateStartIndex);
                Collection<ModelElement> connectedElements = subPathConnected.getConnectedElements(master);
                for (ModelElement me : connectedElements) {
                    //ab diesem Pfadteil muss neu angelegt werden
                    SimpleMetaPath subPathCreate = metaPath.getSubPath(path2CreateStartIndex);
                    targetDoc.createPath(me, instance, subPathCreate, pid);
                }
            }
        }
        targetDoc.finish_transaction(pid);
        targetDoc.distributeEvent(DATA_CHANGED, pid);
        return instanceContainer;
    }

    /**
     * @param startElement
     * @param endElement
     * @param metaPath
     * @param pid
     * @return
     */
    public final AbstractPath createPath(final ModelElement startElement, final ModelElement endElement, final MetaPath metaPath, final int pid) {
        return createPath(startElement, endElement, metaPath, false, pid);
    }

    /**
     * @param startElement
     * @param endElement
     * @param metaPath
     * @param askNameForNewEndElement
     * @param pid
     * @return
     */
    public final AbstractPath createPath(final ModelElement startElement, final ModelElement endElement, final MetaPath metaPath, final boolean askNameForNewEndElement, final int pid) {
        if (metaPath instanceof SimpleMetaPath) {
            return createSimplePath(startElement, endElement, (SimpleMetaPath) metaPath, askNameForNewEndElement, pid);
        } else if (metaPath instanceof ParallelMetaPath) {
            ParallelMetaPath parallelMetaPath = (ParallelMetaPath) metaPath;
            List<AbstractPath> paths = new ArrayList<>();
            for (MetaPath internalMetaPath : parallelMetaPath.iterableSubMetaPaths()) {
                AbstractPath subPath = createPath(startElement, endElement, internalMetaPath, pid);
                paths.add(subPath);
            }
            ParallelPath returnPath = new ParallelPath(parallelMetaPath, startElement, endElement, paths);
            return returnPath;
        }
        return null;
    }

    /**
     * @param startElement
     * @param endElement
     * @param metaPath
     * @param askNameForNewEndElement
     * @param pid
     * @return
     */
    public final SimplePath createSimplePath(final ModelElement startElement, final ModelElement endElement, final SimpleMetaPath metaPath, final boolean askNameForNewEndElement, final int pid) {
        List<ElementaryPath> createdElementaryPaths = new ArrayList<>();
        SimplePath createdSubPath = null;
        start_transaction(pid);
        final int lastPathStepIndex = metaPath.getSubMetaPathCount() - 1;
        //wenn ein EndElement ex. und die letzte Kante eine InstanciationEdge ist, wobei das EndElement der Master dieser InstanciationEdge ist, dann
        //wird das EndElement über diese Kante instanziiert und der Restpfad bis zu dieser Instanz dann wieder über diese Funktion angelegt
        List<ElementaryMetaPath> elementaryMetaPaths = metaPath.getElementaryMetaPaths();
        boolean createSubPath = false;

        //set the insertPosition to the center of the end element if existst and is visible
        //maybe this is not correct for future paths, but now (05.05.2020) there is only a
        //path between an actor and an application system and the actor instance should be
        //created in the center of the application system
        setNodeContainerInsertPosition(endElement);

        if (lastPathStepIndex > 0 && endElement != null) {
            ElementaryMetaPath lastElementaryMetaPath = elementaryMetaPaths.get(lastPathStepIndex);
            if (lastElementaryMetaPath.hasEdgeClass(InstanciationEdge.class)) {
                if (!lastElementaryMetaPath.hasDirection(InstanciationEdge.MASTER_TO_INSTANCE_DIRECTION)) {
                    Class<? extends Edge> edgeClass = lastElementaryMetaPath.getEdgeClass();
                    NodeContainer createdInstanceContainer = createInstance(this, edgeClass.asSubclass(InstanciationEdge.class), endElement, pid);
                    ModelElement createdInstance = createdInstanceContainer.getElement();
                    Edge createdInstanceEdge = endElement.getEdgeTo(createdInstance, edgeClass);
                    ElementaryPath createdInstanceEdgeElementaryPath = new ElementaryPath(lastElementaryMetaPath, createdInstanceEdge, endElement, createdInstanceEdge);
                    SimpleMetaPath subMetaPath = metaPath.getSubPath(0, lastPathStepIndex);
                    createdSubPath = createSimplePath(startElement, createdInstance, subMetaPath, false, pid);
                    createdSubPath = createdSubPath.append(createdInstanceEdgeElementaryPath);
                    createSubPath = true;
                }
            }
        }
        if (!createSubPath && endElement == null) {
            Class<? extends ModelElement> pathEndClass = metaPath.getPathStepElementClass(lastPathStepIndex);
            boolean oldAutomaticMode = gdcoll.setAutomaticMode(!askNameForNewEndElement);
            NodeContainer pathEndElementContainer = createNodeAndContainer(pathEndClass, pid);
            gdcoll.setAutomaticMode(oldAutomaticMode);
            if (pathEndElementContainer != null) { // kann passieren, wenn der Benutzer abbrechen im Namensdialog drückt
                ModelElement pathEndElement = pathEndElementContainer.getElement();
                createPath(startElement, pathEndElement, metaPath, pid);
            }
            createSubPath = true;
        }
        if (!createSubPath) {
            ModelElement pathStepStartElement = startElement;
            for (int i = 0; i <= lastPathStepIndex; i++) {
                ElementaryMetaPath elementaryMetaPath = elementaryMetaPaths.get(i);
                Type type = elementaryMetaPath.getType();
                ElementaryPath createdElementaryPath = null;
                if (i == 0) {
                    if (type == Type.START_WITH_EDGE) {
                        createdElementaryPath = ElementaryPath.createStartsWithEdgePath(elementaryMetaPath, (Edge) startElement);
                    } else if (type == Type.END_WITH_EDGE) {
                        createdElementaryPath = ElementaryPath.createEndsWithEdgePath(elementaryMetaPath, (Edge) startElement);
                    }
                } else if (i == lastPathStepIndex) {
                    if (type == Type.START_WITH_EDGE) {
                        createdElementaryPath = ElementaryPath.createStartsWithEdgePath(elementaryMetaPath, (Edge) endElement);
                    } else if (type == Type.END_WITH_EDGE) {
                        createdElementaryPath = ElementaryPath.createEndsWithEdgePath(elementaryMetaPath, (Edge) endElement);
                    }
                }
                if (createdElementaryPath == null) {
                    ModelElement pathStepEndElement = i == lastPathStepIndex ? endElement : null;
                    Class<? extends ModelElement> pathStepEndClass = metaPath.getPathStepElementClass(i);
                    MetaModel metaModel = getMetaModel();
                    ElementaryMetaPathHandler emph = metaModel.getElementaryMetaPathHandler();
                    Class<? extends ModelElement> pathStepStartClass = pathStepStartElement.getClass();
                    ElementaryMetaPath pathStepElementaryMetaPath = emph.getMetaPath(pathStepStartClass, elementaryMetaPath, pathStepEndClass);
                    createdElementaryPath = createElementaryPath(pathStepStartElement, pathStepEndElement, pathStepElementaryMetaPath, pid);
                }
                createdElementaryPaths.add(createdElementaryPath);
                pathStepStartElement = createdElementaryPath.getEndElement();
            }
        }
        finish_transaction(pid);
        distributeEvent(DATA_CHANGED, pid);
        SimplePath simplePath = createSubPath ? createdSubPath : SimplePath.create(createdElementaryPaths);
        return simplePath;
    }

    /**
     * @param startElement
     * @param endElement
     * @param elementaryMetaPath
     * @param pid
     * @return
     */
    private ElementaryPath createElementaryPath(final ModelElement startElement, ModelElement endElement, final ElementaryMetaPath elementaryMetaPath, final int pid) {
        //wenn ein endElement angegeben wurde, dann das im letzten Pfadschritt verknüpfen
        boolean alreadyLinked = false;
        Class<? extends Edge> edgeClass = elementaryMetaPath.getEdgeClass();
        Edge edge = null;
        //endElement auch erzeugen?
        if (endElement == null) {
            //wenn die Kante eine InstanciationEdge ist und diese Kante vorwärts im Pfad liegt (also vom zu instanzieerenden
            //Element auf das Instanz-Element zeigt), dann wird diese auch selbst über den Instanziierungsmechanismus initialisiert
            if (InstanciationEdge.class.isAssignableFrom(edgeClass) && elementaryMetaPath.hasDirectionForward()) {
                boolean oldAutomaticMode = gdcoll.setAutomaticMode(true);
                NodeContainer createdInstanceContainer = createInstance(this, edgeClass.asSubclass(InstanciationEdge.class), startElement, pid);
                ModelElement createdInstance = createdInstanceContainer.getElement();
                edge = startElement.getEdgeTo(createdInstance, edgeClass);
                gdcoll.setAutomaticMode(oldAutomaticMode);
                endElement = createdInstance;
                alreadyLinked = true;
            } else { // nächstes Pfadschrittelement anlegen
                Class<? extends ModelElement> pathStepEndClass = elementaryMetaPath.getEndClass();
                boolean oldAutomaticMode = gdcoll.setAutomaticMode(true);
                NodeContainer pathStepEndElementContainer = createNodeAndContainer(pathStepEndClass, pid);
                gdcoll.setAutomaticMode(oldAutomaticMode);
                endElement = pathStepEndElementContainer.getElement();
            }
        }
        if (!alreadyLinked) {
            edge = gdcoll.link(edgeClass, startElement, endElement, pid);
        }
        if (CompositionEdge.class.isAssignableFrom(edgeClass)) {
            Class<? extends CompositionEdge> compositionEdgeClass = edgeClass.asSubclass(CompositionEdge.class);
            if (elementaryMetaPath.hasDirectionForward()) {
                addict(startElement, endElement, compositionEdgeClass, pid);
            } else {
                addict(endElement, startElement, compositionEdgeClass, pid);
            }
        }
        if (edge == null) {
            return null;
        }
        ElementaryPath resultPath = new ElementaryPath(elementaryMetaPath, startElement, endElement, edge);
        return resultPath;
    }

}