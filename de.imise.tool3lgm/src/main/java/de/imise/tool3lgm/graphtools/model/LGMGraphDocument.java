package de.imise.tool3lgm.graphtools.model;

import static de.imise.tool3lgm.Static.getMainFrame;
import static de.imise.tool3lgm.Tool3lgmConstants.CLIPBOARD_PATH;
import static de.imise.tool3lgm.Tool3lgmModelType.ModelCategory.CLIPBOARD;
import static de.imise.tool3lgm.graphtools.dialog.OverwriteDialog.OverwriteOption.IGNORE;
import static de.imise.tool3lgm.graphtools.dialog.OverwriteDialog.OverwriteOption.JOIN;
import static de.imise.tool3lgm.graphtools.dialog.OverwriteDialog.OverwriteOption.OVERWRITE;
import static de.imise.tool3lgm.graphtools.metamodel.elements.CompositionEdge.MASTER_TO_SLAVE_DIRECTION;
import static de.imise.tool3lgm.graphtools.metamodel.elements.CompositionEdge.SLAVE_TO_MASTER_DIRECTION;
import static de.imise.tool3lgm.graphtools.model.CopyDependencyResolver.resolveCopyDependencies;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_DELETE_FROM_MODEL;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_PASTE;
import static de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType.DATA_CHANGED;
import static de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType.ELEMENT_GRAPHICS_CHANGED;
import static de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType.SELECTION_CHANGED;
import static de.imise.tool3lgm.graphtools.undoredo.TransactionManager.STANDARD_PID;

import java.awt.Point;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmModelType.ModelCategory;
import de.imise.tool3lgm.graphtools.ModelCopyAndPasteHandler;
import de.imise.tool3lgm.graphtools.analyse.redundancy.SimpleRedundancyAnalysis;
import de.imise.tool3lgm.graphtools.analyse.redundancy.SimpleRedundancyAnalysisDefinitions.SingleSimpleRedundancyAnalysisDefinition;
import de.imise.tool3lgm.graphtools.dialog.OverwriteDialog;
import de.imise.tool3lgm.graphtools.dialog.OverwriteDialog.OverwriteOption;
import de.imise.tool3lgm.graphtools.dialog.OverwriteDialog.OverwriteQuestionAnswer;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Bendpoint;
import de.imise.tool3lgm.graphtools.metamodel.elements.CompositionEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge.ConnectionState;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.InstanciationEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.model.CopyDependencyResolver.CopyDependencyResolverResultSimple;
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
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.view.container.BendpointContainer;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.LayerContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.graphtools.view.graph.GraphFunctions;
import de.imise.tool3lgm.log.Log;
import de.imise.util.Sys;

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
        setName(getResString("uebersicht"));
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
        if (selectedContainer.isEmpty()) {
            return;
        }
        ModelCopyAndPasteHandler.copy();
    }

    /**
     *
     */
    private synchronized void cutToClipboard() {
        start_transaction(STANDARD_PID);
        copyToClipboard();
        //man muss die Selektion clonen, da sie sich wärend des Löschens ändert
        gdcoll.deleteElements(getSelectedElements(), this, STANDARD_PID);
        finish_transaction(STANDARD_PID, DATA_CHANGED);
    }

    /**
     * @return
     */
    public static synchronized final boolean isClipboardAvailable() {
        return CLIPBOARD_PATH.exists();
    }

    /**
     * @return
     */
    public final SortedSelection getSortedSelection() {
        @SuppressWarnings("unchecked")
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
        if (!ModelCopyAndPasteHandler.canPaste()) {
            return;
        }
        start_transaction(STANDARD_PID);
        try {
            deselectAll(true);
            ModelCopyAndPasteHandler.paste();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        addRedo(STANDARD_PID, MODEL_ACTION_PASTE);
        addUndo(STANDARD_PID, MODEL_ACTION_DELETE_FROM_MODEL);
        finish_transaction(STANDARD_PID, DATA_CHANGED);
    }

    /**
     * @param istream
     */
    public synchronized void pasteInputStream(final InputStream istream) {
        start_transaction(STANDARD_PID);
        addUndo(STANDARD_PID, MODEL_ACTION_DELETE_FROM_MODEL);
        deselectAll(true);
        try {
            gdcoll.loadFile(istream);
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
        finish_transaction(STANDARD_PID, DATA_CHANGED);
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
                    String otherID = other.getID();
                    ModelElement destElement = targetMainDoc.findElementCoded(otherID);
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
        if (sourceElements.size() > 100) {
            Static.showProgressDialog();
            Static.setProgressDialogTitle("Kopiere Elemente (AUSLAGERN)");
            Static.setProgressDialogStatusLabel("Ermittle Referenzen...(AUSLAGERN)");
        }
        GDCollection sourceCollection = sourceDoc.getCollection();
        GraphDocument sourceMainDoc = sourceCollection.getMainDoc();
        GDCollection targetCollection = targetDoc.getCollection();
        GraphDocument targetMainDoc = targetCollection.getMainDoc();

        //        Sys.out1("\n##############\n# STARTZUSTAND #\n##############");
        //        GDCollectionPrinter.print(sourceCollection, true, true);
        //        GDCollectionPrinter.print(targetCollection, true, true);
        //
        boolean debug = false; //enable if you want to debug the execution time
        int iii = 0;
        long start = System.currentTimeMillis();
        //set both collections to bulk mode to prevent any selection update events
        boolean sourceBulkMode = sourceCollection.setBulkMode(true);
        boolean targetBulkMode = targetCollection.setBulkMode(true);

        Sys.err1debug(debug, ++iii + " " + (System.currentTimeMillis() - start));
        start = System.currentTimeMillis();

        sourceCollection.removeInferenceEdges(true, STANDARD_PID);
        targetCollection.removeInferenceEdges(true, STANDARD_PID);

        Sys.err1debug(debug, ++iii + " " + (System.currentTimeMillis() - start));
        start = System.currentTimeMillis();

        CopyDependencyResolverResultSimple resolvedCopyDependencies = resolveCopyDependencies(sourceElements);
        Sys.err1debug(debug, ++iii + " " + (System.currentTimeMillis() - start));
        start = System.currentTimeMillis();

        //Liste aller Kanten, bei denen das das eine Endelement gerade kopiert werden soll und das
        //andere nicht kopiert werden soll aber bereits im Zielmodell vorkommt hinzufügen
        addSplittedSourceEdgesToCopy(resolvedCopyDependencies.elements, targetDoc);

        Sys.err1debug(debug, ++iii + " " + (System.currentTimeMillis() - start));
        start = System.currentTimeMillis();

        if (targetCollection.hasModelCategory(CLIPBOARD)) { //copy to clipboard
            UserFieldDefinitions sourceUserFieldDefinitions = sourceCollection.getUserFieldDefinitions();
            UserFieldDefinitions clonedSourceUserFieldDefinitions = sourceUserFieldDefinitions.cloneForTargetCollection(targetCollection);
            clonedSourceUserFieldDefinitions.retain(resolvedCopyDependencies.usedUserFields);
            targetCollection.setUserFieldDefinitions(clonedSourceUserFieldDefinitions);
        } else if (sourceCollection.hasModelCategory(CLIPBOARD)) { //copy from clipboard

        } else { // copy from model to model
            UserFieldDefinitions userFieldDefinitions = targetCollection.getUserFieldDefinitions();
            UserFieldDefinitions userFieldDefinitions2Add = resolvedCopyDependencies.getUserFieldDefinitions();
            userFieldDefinitions.addAll(userFieldDefinitions2Add);
        }

        Sys.err1debug(debug, ++iii + " " + (System.currentTimeMillis() - start));
        start = System.currentTimeMillis();

        //selection will be lost during copying ->
        List<ElementContainer> tmpActive = new ArrayList<>(sourceDoc.selectedContainer);
        List<Edge> edges = new ArrayList<>();
        List<BendpointContainer> bendpoints = new ArrayList<>();

        Sys.err1debug(debug, ++iii + " " + (System.currentTimeMillis() - start));
        start = System.currentTimeMillis();

        sourceMainDoc.deselectAll(false);

        Sys.err1debug(debug, ++iii + " " + (System.currentTimeMillis() - start));
        start = System.currentTimeMillis();

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

            Sys.err1debug(debug, ++iii + " " + (System.currentTimeMillis() - start));
            start = System.currentTimeMillis();

            /**
             * changed IDs (with copyAndPaste). Key is old ID, value is new ID.
             */
            DualHashBidiMap<String, String> oldToNewID = new DualHashBidiMap<>();

            List<ElementContainer> elementContainerssInGraphOrder = getElementsInGraphOrder(resolvedCopyDependencies.elements, sourceDoc);

            for (ElementContainer sourceContainer : elementContainerssInGraphOrder) {
                ModelElement sourceElement = sourceContainer.getElement();

                //we need the layer index in every case -> use
                //this index to find the elements faster than
                //with the general function targetMainDoc.findElementCoded(id)
                int layer = sourceElement.layerFor();
                String sourceID = sourceElement.getID();
                ModelElement existingTargetElement = null;
                if (sourceElement instanceof Bendpoint) {
                    existingTargetElement = targetMainDoc.findBendpointCoded(sourceID, layer);
                } else if (sourceElement instanceof Node) {
                    existingTargetElement = targetMainDoc.findNodeCoded(sourceID, layer);
                } else if (sourceElement instanceof Edge) {
                    existingTargetElement = targetMainDoc.findEdgeCoded(sourceID, layer);
                }

                if (existingTargetElement != null && !sourceCollection.hasModelCategory(CLIPBOARD)) {
                    if (!answer.applyToAll) {
                        sourceDoc.select(sourceContainer, STANDARD_PID);
                        sourceDoc.distributeEvent(SELECTION_CHANGED, sourceContainer, STANDARD_PID);
                        answer = OverwriteDialog.showDialog(existingTargetElement, sourceElement);
                    }

                    if (answer.overwriteOption == OVERWRITE) {
                        //hier müsste das alte evtl. noch gelöscht werden !?
                    } else if (answer.overwriteOption == JOIN) {
                        targetDoc.joinElements(existingTargetElement, sourceElement, sourceDoc, true);
                        if (existingTargetElement instanceof Edge) {
                            Edge edge = (Edge) existingTargetElement;
                            edge.reconnect(targetCollection);
                            edge.refreshText();
                        }
                    } else if (answer.overwriteOption == IGNORE) {
                        continue;
                    }
                    //wenn die ID des zu kopierenden Elementes noch nicht im Modell vorkommt
                } else {
                    //                    Sys.out1("\nVOR EINFÜGEN VON ELEMENT " + sourceContainer);
                    //                    GDCollectionPrinter.print(targetCollection, true, true);

                    //first create the element in the mainDoc
                    ElementContainer targetMainContainer = sourceContainer.clone(true, targetMainDoc);
                    ModelElement targetElement = targetMainContainer.getElement();
                    LayerContainer targetMainDocLayer = targetMainDoc.getLayer(layer);
                    if (existingTargetElement == null) {
                        targetElement.setID(sourceID);
                    } else {
                        String targetID = targetElement.getID();
                        oldToNewID.put(sourceID, targetID);
                    }
                    if (targetElement instanceof Bendpoint) {
                        Bendpoint bendpoint = (Bendpoint) targetElement;
                        String edgeID = bendpoint.getEdgeID();
                        edgeID = oldToNewID.getOrDefault(edgeID, edgeID);
                        EdgeContainer edgeC = targetMainDocLayer.getEdgeContainer(edgeID);
                        Edge edge = edgeC.getEdge();
                        bendpoint.addEdge(edge);
                    }

                    targetMainDocLayer.add(targetMainContainer);

                    //                    Sys.out1("\nNACH EINFÜGEN VON ELEMENT " + targetMainContainer + " (" + targetMainContainer.getID() + ")");
                    //                    GDCollectionPrinter.print(targetCollection, true, true);

                    if (targetElement instanceof Edge) {
                        Edge edge = (Edge) targetElement;

                        ModelElement targetEdgeStart = edge.getStart();
                        ModelElement targetEdgeEnd = edge.getEnd();
                        String targetEdgeStartID = targetEdgeStart.getID();
                        String targetEdgeEndID = targetEdgeEnd.getID();
                        String newStartID = oldToNewID.get(targetEdgeStartID);
                        String newEndID = oldToNewID.get(targetEdgeEndID);
                        if (newStartID != null || newEndID != null) {
                            if (newStartID == null) {
                                newStartID = targetEdgeStartID;
                            }
                            if (newEndID == null) {
                                newEndID = targetEdgeEndID;
                            }
                            edge.setStartAndEndByIDs(newStartID, newEndID, targetMainDoc);
                        }

                        //edges are inserted in the szenario separately
                        edges.add(edge);

                        //                        Sys.out1("\nNACH UMHÄNGEN DER EDGE " + targetMainContainer);
                        //                        GDCollectionPrinter.print(targetCollection, true, true);

                        //all not unique elements must be inserted to the szenario
                    } else if (!targetElement.isUnique() && targetDoc instanceof Szenario) {
                        //create container for the szenario and adds this container to the
                        //container map of the element
                        ElementContainer targetContainer = targetMainContainer.clone(false, targetDoc);
                        targetContainer.setElement(targetElement);
                        if (targetElement instanceof Bendpoint) {
                            //bendpoints separately too
                            bendpoints.add((BendpointContainer) targetContainer);
                        } else {
                            targetElement.updateGraphName(targetContainer);
                            //add the container to the layer of the szenario
                            targetContainer.refreshText();
                            LayerContainer targetDocLayer = targetDoc.getLayer(layer);
                            targetDocLayer.add(targetContainer);
                            targetDoc.addSimpleToSelection(targetContainer);
                        }
                    }
                    targetMainContainer.setVisible(true);
                    targetMainContainer.setExpanded(true);
                    targetMainContainer.setHighLight(false);
                    targetMainContainer.refreshText();
                    targetMainDoc.addSimpleToSelection(targetMainContainer);
                }
            }

            //            Sys.out1("\n##############\n# 1 VOR ENDZUSTAND #\n##############");
            //            GDCollectionPrinter.print(targetCollection, true, true);

            Sys.err1debug(debug, ++iii + " " + (System.currentTimeMillis() - start));
            start = System.currentTimeMillis();

            for (Edge edge : edges) {
                if (!edge.reconnect(targetCollection)) {

                    //                    Sys.out1("\n##############\n# 1a VOR ENDZUSTAND #\n##############");
                    //                    GDCollectionPrinter.print(targetCollection, true, true);

                    targetCollection.deleteElement(edge, STANDARD_PID);

                    //                    Sys.out1("\n##############\n# 1b VOR ENDZUSTAND #\n##############");
                    //                    GDCollectionPrinter.print(targetCollection, true, true);

                } else {
                    EdgeContainer edgeCont = edge.getContainer(targetMainDoc);
                    //this call adds a edgeContainer to all szenarios where it mus be added
                    //the tarte mainDoc and the target szenarion already contain the edge container
                    targetCollection.addEdge(edgeCont, STANDARD_PID);

                    //                    Sys.out1("\n##############\n# 1c VOR ENDZUSTAND #\n##############");
                    //                    GDCollectionPrinter.print(targetCollection, true, true);

                    ElementContainer edgeC = edge.getContainer(targetMainDoc);
                    targetMainDoc.addSimpleToSelection(edgeC);
                }
            }

            //            Sys.out1("\n##############\n# 2 VOR ENDZUSTAND #\n##############");
            //            GDCollectionPrinter.print(targetCollection, true, true);

            Sys.err1debug(debug, ++iii + " " + (System.currentTimeMillis() - start));
            start = System.currentTimeMillis();

            List<EdgeContainer> edgeConts = new ArrayList<>();
            while (!bendpoints.isEmpty()) {
                BendpointContainer targetBpc = bendpoints.remove(0);
                String targetBendpointID = targetBpc.getID();
                String sourceBendpointID = oldToNewID.getKey(targetBendpointID);
                if (sourceBendpointID == null) {
                    sourceBendpointID = targetBendpointID;
                }
                BendpointContainer sourceBpc = sourceDoc.findBendpointContainerCoded(sourceBendpointID);
                //der Container kann null sein, wenn die zu kopierende Kante auch noch mind. einen Knickpunkt in einem
                //anderen Teilmodell hat, denn es werden beim resolven der CopyDependencies alle Knickpunkte der Kante aus
                //allen Teilmodellen eingesammelt
                if (sourceBpc == null) {
                    continue;
                }
                Bendpoint targetBendpoint = targetBpc.getBendpoint();
                String targetEdgeID = targetBendpoint.getEdgeID();
                targetEdgeID = oldToNewID.getOrDefault(targetEdgeID, targetEdgeID);
                EdgeContainer targetEdgeC = targetDoc.findEdgeContainerCoded(targetEdgeID);

                Bendpoint sourceBendpoint = sourceBpc.getBendpoint();
                EdgeContainer sourceEdgeC = sourceBendpoint.getOwner();
                if (sourceEdgeC == null) {
                    sourceEdgeC = sourceDoc.findEdgeContainerCoded(targetEdgeC.getID());
                }
                if (targetEdgeC != null) {
                    edgeConts.add(targetEdgeC);
                    int indexOfBendpoint = sourceEdgeC.getIndexOfBendpoint(sourceBendpoint);
                    targetEdgeC.setBendpointContainer(targetBpc, indexOfBendpoint);
                    //                    Sys.out1(targetEdgeC + " (EdgeContainerID=" + System.identityHashCode(targetEdgeC) + ")   " + targetBpc + " (" + System.identityHashCode(targetBpc) + ")");
                    int layer = targetEdgeC.layerFor();
                    LayerContainer targetDocLayer = targetDoc.getLayer(layer);
                    targetDocLayer.add(targetBpc);
                }

                //                Sys.out1("\n##############\n# 3 VOR ENDZUSTAND #\n##############");
                //                GDCollectionPrinter.print(targetCollection, true, true);

            }

            Sys.err1debug(debug, ++iii + " " + (System.currentTimeMillis() - start));
            start = System.currentTimeMillis();

            for (EdgeContainer edgeC : edgeConts) {
                //                Sys.out1(edgeC + " AXS=" + System.identityHashCode(edgeC));
                edgeC.computeBorderPoints();
            }

            Sys.err1debug(debug, ++iii + " " + (System.currentTimeMillis() - start));
            start = System.currentTimeMillis();

            targetMainDoc.finish_transaction(STANDARD_PID);

            Sys.err1debug(debug, ++iii + " " + (System.currentTimeMillis() - start));
            start = System.currentTimeMillis();

        } catch (Exception ex) {
            targetMainDoc.undo(STANDARD_PID);
            Log.show(Log.ERROR, sourceDoc.getResString("FehlerKorrupt") + "\n" + targetCollection.getName(), ex);
        }
        sourceDoc.start_transaction(STANDARD_PID, false);

        Sys.err1debug(debug, ++iii + " " + (System.currentTimeMillis() - start));
        start = System.currentTimeMillis();

        sourceDoc.deselectAll(true);
        for (int j = 0; j < tmpActive.size(); j++) {
            ElementContainer ec = tmpActive.get(j);
            sourceDoc.addSimpleToSelection(ec);
        }

        Sys.err1debug(debug, ++iii + " " + (System.currentTimeMillis() - start));
        start = System.currentTimeMillis();

        sourceDoc.finish_transaction(STANDARD_PID, false);
        sourceDoc.distributeEvent(SELECTION_CHANGED);

        Sys.err1debug(debug, ++iii + " " + (System.currentTimeMillis() - start));
        start = System.currentTimeMillis();

        sourceCollection.createInferenceEdges(true, STANDARD_PID);
        targetCollection.createInferenceEdges(true, STANDARD_PID);

        //bei Bedarf anschalten, um zu sehen, wie das Modell danach aussieht
        //GDCollectionPrinter.print(targetCollection);

        sourceCollection.setBulkMode(sourceBulkMode);

        Sys.err1debug(debug, ++iii + " " + (System.currentTimeMillis() - start));
        start = System.currentTimeMillis();

        targetCollection.setBulkMode(targetBulkMode);

        Sys.err1debug(debug, ++iii + " " + (System.currentTimeMillis() - start));
        start = System.currentTimeMillis();

        targetDoc.distributeEvent(DATA_CHANGED);

        Sys.err1debug(debug, ++iii + " " + (System.currentTimeMillis() - start));
        start = System.currentTimeMillis();

        Static.closeProgressDialog();

        //        Sys.out1("\n##############\n# ENDZUSTAND #\n##############");
        //        GDCollectionPrinter.print(sourceCollection, true, true);
        //        GDCollectionPrinter.print(targetCollection, true, true);
    }

    /**
     * In the function the containers of all passed nodes are searched in the
     * passed sourceDoc and inserted first of all in the return list in exactly
     * the order in which they also are present in the soureDoc in the graphic.
     * After that, all other main model containers of the elements that do not
     * occur in the partial model (unique nodes, edges and inflection points)
     * are added to this list.
     *
     * @param elements
     * @param sourceDoc
     * @return a list of element containers. The first elements are all
     *         NodeContainers
     */
    private static List<ElementContainer> getElementsInGraphOrder(List<ModelElement> elements, GraphDocument sourceDoc) {
        HashSet<ModelElement> elementsCopyAsSet = new HashSet<>(elements);
        List<ElementContainer> elementsInGraphOrder = new ArrayList<>();
        //first add all real graph node containers from the submodel in the correct graph order
        for (ElementContainer ec : sourceDoc.getNodeContainersInGraphOrder()) {
            ModelElement me = ec.getElement();
            if (elementsCopyAsSet.contains(me)) {
                elementsInGraphOrder.add(ec);
            }
        }
        //now add all
        for (ModelElement me : elements) {
            ElementContainer ec = me.getContainer(sourceDoc);
            if (ec != null) {
                if (ec instanceof BendpointContainer || ec instanceof EdgeContainer) {
                    elementsInGraphOrder.add(ec);
                }
            } else {
                GraphDocument sourceMainDoc = sourceDoc.getMainDoc();
                ec = me.getContainer(sourceMainDoc);
                if (ec != null) { // ec == null should never happen if the model is correct :)
                    elementsInGraphOrder.add(ec);
                }
            }
        }
        return elementsInGraphOrder;
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

        String me2ID = me2.getID();
        ModelElement me3 = findElementCoded(me2ID);
        boolean overwriteID = me3 == null || me3 == me2;
        if (me1.join(me2, overwriteID, joinNameDescriptionAndUserfields) == null) {
            return;
        }
        me1.refreshText();

        for (Edge edge : me2.getEdges()) {
            Edge oldEdge;
            /* vorwaerts */
            if (edge.getStart().equals(me2)) {
                me3 = findElementCoded(edge.getEnd().getID());
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
                me3 = findElementCoded(edge.getStart().getID());
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
        String masterID = master.getID();
        master = targetDoc.findElementCoded(masterID);

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
        targetDoc.finish_transaction(pid, DATA_CHANGED);
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
        }
        if (metaPath instanceof ParallelMetaPath) {
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
            Class<? extends ModelElement> pathEndClass = metaPath.getElementaryMetaPathStepConnectingClass(lastPathStepIndex);
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
                    Class<? extends ModelElement> pathStepEndClass = metaPath.getElementaryMetaPathStepConnectingClass(i);
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
        SimplePath simplePath = createSubPath ? createdSubPath : SimplePath.create(createdElementaryPaths);
        if (simplePath != null) { //is null if the function is called with null as endElement (in dialogs)
            repositioningOfSubordniatedInGraph(simplePath, pid);
        }
        finish_transaction(pid, DATA_CHANGED);
        return simplePath;
    }

    /**
     * @param startElement
     * @param endElement
     * @param elementaryMetaPath
     * @param pid
     * @return
     */
    public ElementaryPath createElementaryPath(final ModelElement startElement, ModelElement endElement, final ElementaryMetaPath elementaryMetaPath, final int pid) {
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
            Direction direction = elementaryMetaPath.getDirection();
            edge = gdcoll.link(startElement, endElement, edgeClass, direction, pid);
            ConnectionState connectionState = elementaryMetaPath.getConnectionState();
            if (connectionState != null && edge instanceof DoubleMeaningEdge) {
                ((DoubleMeaningEdge) edge).setConnectionState(connectionState);
            }

        }
        if (CompositionEdge.class.isAssignableFrom(edgeClass)) {
            Class<? extends CompositionEdge> compositionEdgeClass = edgeClass.asSubclass(CompositionEdge.class);
            if (elementaryMetaPath.hasDirectionForward()) {
                subordinate(startElement, endElement, compositionEdgeClass, pid);
            } else {
                subordinate(endElement, startElement, compositionEdgeClass, pid);
            }
        }
        if (edge == null) {
            return null;
        }
        ElementaryPath resultPath = new ElementaryPath(elementaryMetaPath, startElement, endElement, edge);
        return resultPath;
    }

    /**
     * @param startElement
     * @param endElement
     * @param metaPath
     * @param pid
     */
    public final void removePath(final ModelElement startElement, final ModelElement endElement, final MetaPath metaPath, final int pid) {
        if (metaPath instanceof SimpleMetaPath) {
            removeSimplePath(startElement, endElement, (SimpleMetaPath) metaPath, pid);
        } else if (metaPath instanceof ParallelMetaPath) {
            ParallelMetaPath parallelMetaPath = (ParallelMetaPath) metaPath;
            for (MetaPath internalMetaPath : parallelMetaPath.iterableSubMetaPaths()) {
                removePath(startElement, endElement, internalMetaPath, pid);
            }
        }
    }

    /**
     * @param startElement
     * @param endElement
     * @param metaPath
     * @param pid
     */
    public final void removeSimplePath(final ModelElement startElement, final ModelElement endElement, final SimpleMetaPath metaPath, final int pid) {
        List<SimplePath> simplePaths = metaPath.getSimplePaths(startElement, endElement);
        for (SimplePath simplePath : simplePaths) {
            removeSimplePath(simplePath, pid);
        }
    }

    /**
     * @param simplePath
     * @param pid
     */
    public final void removeSimplePath(final SimplePath simplePath, final int pid) {
        start_transaction(pid);
        //at the moment this funtion only removes the edges. If the elements
        //in the path are not invalid through the removing of the edges they
        //will not be deleted. Maybe in future we need the functionality to
        //delete the inner path elements too. Then this function needs one
        //more parameter.
        List<ElementaryPath> elementaryPaths = simplePath.getElementaryPaths();
        for (ElementaryPath elementaryPath : elementaryPaths) {
            Edge edge = elementaryPath.getEdge();
            gdcoll.deleteElement(edge, pid);
        }
        finish_transaction(pid);
    }

    /**
     * If the given path contains subordinated elements that are visible in the
     * graphic and have a visible connection between them, then these
     * subordinated elements are rearranged so that they lie on the edge of
     * their superordinated elements but in the shortest available distance to
     * the connected element.
     *
     * @param simplePath
     * @param pid
     */
    private void repositioningOfSubordniatedInGraph(final SimplePath simplePath, final int pid) {
        //If there are 2 subordinated elements in the path then in
        //the first round the first subordinated is repositioned
        //relatively to old position of the second subordinated.
        //So we simply have to call the same function at least twice.
        //In general this function converges to the ideal points
        //after 3 rounds if there are 2 subordinated elements
        //in the path. But we only take 2 rounds, because the
        //subordinate elements then land slightly offset due to their
        //different starting positions and not directly on top of
        //each other.
        //If there is only 1 subordinated element then one round
        //would be enough but it is very fast -> always two rounds.
        repositioningOfSubordniatedInGraph(simplePath, true, pid);
        repositioningOfSubordniatedInGraph(simplePath, true, pid);
    }

    /**
     * If the given path contains subordinated elements that are visible in the
     * graphic and have a visible connection between them, then these
     * subordinated elements are rearranged so that they lie on the edge of
     * their superordinated elements but in the shortest available distance to
     * each other.
     *
     * @param simplePath The path with elements that can potentially be
     *            rearranged.
     * @param checkOtherDirection
     * @param pid
     */
    private void repositioningOfSubordniatedInGraph(final SimplePath simplePath, final boolean checkOtherDirection, final int pid) {
        List<ElementaryPath> elementaryPaths = simplePath.getElementaryPaths();
        for (int i = 1; i < elementaryPaths.size(); i++) {
            ElementaryPath elementaryPath = elementaryPaths.get(i - 1);
            Edge edge = elementaryPath.getEdge();
            //If the first edge in the graph is displayed and is a subordination from master to slave
            if (edge.isPaintable() && edge instanceof CompositionEdge && elementaryPath.hasDirection(MASTER_TO_SLAVE_DIRECTION)) {

                ElementaryPath nextElementaryPath = elementaryPaths.get(i);
                Edge nextEdge = nextElementaryPath.getEdge();
                //Check the next edge if it paintable too but not a subordination
                //for the same element.
                //(It wouldn't make much sense, but an element could theoretical
                //be subordinated to more than one other element in the graph in
                //a metamodel -> reposition only if it is subordinated to only
                //one graph displayed element.)
                if (nextEdge.isPaintable() && !(nextEdge instanceof CompositionEdge && nextElementaryPath.hasDirection(SLAVE_TO_MASTER_DIRECTION))) {
                    CompositionEdge compositionEdge = (CompositionEdge) edge;
                    ModelElement master = compositionEdge.getMaster();
                    ModelElement slave = compositionEdge.getSlave();
                    ModelElement other = nextElementaryPath.getEndElement();
                    ElementContainer slaveContainer = slave.getContainer(this);
                    if (slaveContainer instanceof NodeContainer) {
                        ElementContainer masterContainer = master.getContainer(this);
                        ElementContainer otherContainer = other.getContainer(this);
                        //we point from the middle of the master of the element that should
                        //be repositioned to the middle of the connected other element
                        Point closestCoordinatesOnBorderOfContainerToOther = GraphFunctions.getClosestCoordinatesOnBorderOfContainerToOther(masterContainer, otherContainer);
                        NodeContainer slaveNodeContainer = (NodeContainer) slaveContainer;
                        moveNodeContainer(slaveNodeContainer, closestCoordinatesOnBorderOfContainerToOther.x, closestCoordinatesOnBorderOfContainerToOther.y, pid);
                    }
                }
            }
        }
        //check the path in the other direction if there is an
        //element that must be repositioned, too
        if (checkOtherDirection) {
            SimplePath otherDirection = simplePath.getOtherDirection();
            repositioningOfSubordniatedInGraph(otherDirection, false, pid);
        }
    }

}
