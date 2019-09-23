package de.imise.tool3lgm.graphtools.model;

import static de.imise.tool3lgm.Static.getMainFrame;
import static de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType.DATA_CHANGED;
import static de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType.ELEMENT_GRAPHICS_CHANGED;
import static de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType.SELECTION_CHANGED;
import static de.imise.tool3lgm.graphtools.undoredo.TransactionManager.STANDARD_PID;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.JOptionPane;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.analyse.redundancy.SimpleRedundancyAnalysis;
import de.imise.tool3lgm.graphtools.analyse.redundancy.SimpleRedundancyAnalysisDefinitions.SingleSimpleRedundancyAnalysisDefinition;
import de.imise.tool3lgm.graphtools.dialog.OverwriteDialog;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Bendpoint;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
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
        default:
            super.dispatch_command(command, argv, pid);
        }
    }

    private final List<SimpleRedundancyAnalysis> simpleRedundancyAnalysis = new ArrayList<>();

    /**
     * Wenn es bereits eine {@link SimpleRedundancyAnalysis} mit den übergebenen Pfaden gibt, wird diese
     * entfernt. Ansonsten wird eine neue hinzugefügt. Sobald es sie gibt, wird sie auch ausgeführt.
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

    public final List<ElementContainer> getSortedSelection() {
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
     * Liste aller Kanten, bei denen das das eine Endelement gerade kopiert werden soll und das
     * andere nicht kopiert werden soll aber bereits im Zielmodell vorkommt
     *
     * @param sourceElements
     * @param targetDoc
     */
    private static final void addSplittedSourceEdgesToCopy(final List<ModelElement> sourceElements, final LGMGraphDocument targetDoc) {
        LGMGraphDocument targetMainDoc = targetDoc.getCollection().getMainGraphDocument();
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

    //	/**
    //	 *
    //	 * TODO:Bug beim Übenehmen von Elementen in ein anderes Modell
    //	 *
    //	 * Kanten werden nicht richtig in das neue Modell übenommen, d.h. der Container wird in diesem
    //	 * Fall nicht im Layer abgelegt, so dass sie in der Grafik nicht auftauchen. Die Edge-Container
    //	 * werden aber richtig in den Elementen eingetragen.
    //	 *
    //	 * Die untere Funktion ist die alte Variante; Die hier auskommentierte sollte die neue werden.
    //	 * Allerdings liegt der Fehler irgendwo anders. Hier sollte unbeding auch beachtet werden, dass
    //	 * wenn man ein Element in ein anderes Modell übernimmt, dass im Ursprungsmodell Verbindungen
    //	 * zu anderen Elementen hat, auch Verbindungen zu Elementen übernommen werden, die sowohl im
    //	 * Urpsungsmodell als auc im Zielmodell vorkommen. (Z.B. übernimmt man erst eine Aufgabe in ein
    //	 * Modell und danach bei einer 2. Übernahme eine Unteraufgabe dieser Aufgabe in das gleiche Modell,
    //	 * dann geht die Unterordnungsbeziehung im Zielmodell verloren.)
    //	 *
    //	 * @param dest
    //	 * /
    /**
     * @param targetDoc
     */
    public static final void copySelectedToModel(final LGMGraphDocument sourceDoc, final LGMGraphDocument targetDoc) {

        GDCollection sourceCollection = sourceDoc.getCollection();
        GraphDocument sourceMainDoc = sourceCollection.getMainGraphDocument();
        GDCollection targetCollection = targetDoc.getCollection();
        GraphDocument targetMainDoc = targetCollection.getMainGraphDocument();

        //Keine Ahnung warum hier mal irgendwer ein Speichern erzwingen wollte!?
        //        if (destGDColl.isChanged()) {
        //            int value = JOptionPane.showConfirmDialog(null, getResString("join_speicherfrage"), getResString("tool3lgm"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null);
        //            if (value == JOptionPane.YES_OPTION) {
        //                if (!destGDColl.getFileHandler().saveToFile()) {
        //                    return;
        //                }
        //            } else {
        //                return;
        //            }
        //        }

        List<ModelElement> sourceElements = new ArrayList<>();
        HashSet<UserField> sourceUserFields = new HashSet<>();
        sourceCollection.resolveCopyDependencies(sourceDoc.selectedContainer, sourceElements, sourceUserFields);
        //Liste aller Kanten, bei denen das das eine Endelement gerade kopiert werden soll und das
        //andere nicht kopiert werden soll aber bereits im Zielmodell vorkommt hinzufügen
        addSplittedSourceEdgesToCopy(sourceElements, targetDoc);

        for (UserField uf : sourceUserFields) {
            if (uf != null) {
                targetCollection.getUserFieldDefinitions().add(uf); // es könnte sein, dass hier Mist passiert und UserFields immer wieder geaddet werden, wenn man ein Element übernimmt
            }
        }

        List<ElementContainer> tmpActive = new ArrayList<>(sourceDoc.selectedContainer);
        List<Edge> edges = new ArrayList<>();
        List<BendpointContainer> bendpoints = new ArrayList<>();

        sourceMainDoc.deselectAll(false);

        int pid = TransactionManager.STANDARD_PID;
        targetMainDoc.start_transaction(pid);

        try {
            targetMainDoc.deselectAll(true);

            //Icons kopieren
            GDCollectionIconTable sourceIconTable = sourceCollection.getIconTable();
            GDCollectionIconTable targetIconTable = targetCollection.getIconTable();
            targetIconTable.putAll(sourceIconTable); // Warum werden hier einfach alle Icons übernommen? Überprüfen!

            // lowest bit determin whether ask user about what to do, when hashcode already exists in dest (1 == do not ask / remember last decision)
            // OverwriteDialog.OVERWRITE
            // OverwriteDialog.JOIN
            // OverwriteDialog.NOTHING
            int overwriteJoinNothing = 0;
            for (ModelElement sourceElement : sourceElements) {
                ElementContainer sourceContainer = sourceElement.getContainer(sourceDoc);
                if (sourceContainer == null) {
                    sourceContainer = sourceElement.getContainer(sourceMainDoc);
                }

                String sourceHash = sourceElement.getHashString();
                ModelElement targetElement = targetMainDoc.findElementCoded(sourceHash);
                if (targetElement != null) {
                    if ((overwriteJoinNothing & 1) == 0) {
                        sourceDoc.select(sourceContainer, STANDARD_PID);
                        sourceDoc.distributeEvent(SELECTION_CHANGED, sourceContainer, STANDARD_PID);
                        overwriteJoinNothing = OverwriteDialog.showDialog(targetElement, sourceElement);
                    }

                    if ((overwriteJoinNothing & OverwriteDialog.OVERWRITE) > 0) {

                    } else if ((overwriteJoinNothing & OverwriteDialog.JOIN) > 0) {
                        targetDoc.joinElements(targetElement, sourceElement, sourceDoc, false);
                        if (targetElement instanceof Edge) {
                            ((Edge) targetElement).reconnect(targetCollection);
                            ((Edge) targetElement).refreshText();
                        }
                    } else if ((overwriteJoinNothing & OverwriteDialog.DONOTHING) > 0) {
                        continue;
                    }
                    //wenn der Hash des zu kopierenden Elementes noch nicht im Modell vorkommt
                } else {
                    ElementContainer targetContainer = sourceContainer.clone(true, targetDoc);
                    targetElement = targetContainer.getElement();
                    targetElement.setHashString(sourceHash);
                    ElementContainer targetMainContainer = targetContainer.clone(false, targetMainDoc);
                    targetMainContainer.setVisible(true);
                    targetMainContainer.setExpanded(true);
                    targetMainContainer.setHighLight(false);
                    targetMainContainer.refreshText();
                    int layer = targetElement.layerFor();
                    LayerContainer targetMainDocLayer = targetMainDoc.getLayer(layer);
                    targetMainDocLayer.add(targetMainContainer);
                    if (targetElement instanceof Edge) {
                        edges.add((Edge) targetElement);
                    } else if (targetElement instanceof Bendpoint) {
                        bendpoints.add((BendpointContainer) targetContainer);
                    } else if (!targetElement.isUnique() && targetDoc instanceof Szenario) {
                        targetContainer.refreshText();
                        LayerContainer targetDocLayer = targetDoc.getLayer(layer);
                        targetDocLayer.add(targetContainer);
                    }
                    targetMainDoc.addToSelection(targetMainContainer, pid);
                }
            }
            for (Edge edge : edges) {
                if (!edge.reconnect(targetCollection)) {
                    targetCollection.deleteElement(edge, pid);
                } else {
                    EdgeContainer edgeCont = (EdgeContainer) edge.getContainer(targetMainDoc);
                    targetCollection.addEdge(edgeCont, pid);
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
                    targetMainDoc.addToSelection(edgeC, pid);
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

            targetMainDoc.finish_transaction(pid);
        } catch (Exception ex) {
            targetMainDoc.undo(pid);
            Log.show(Log.ERROR, sourceDoc.getResString("FehlerKorrupt") + "\n" + targetCollection.getName(), ex);
        }
        sourceDoc.start_transaction(TransactionManager.STANDARD_PID, false);
        sourceDoc.deselectAll(true);
        for (int j = 0; j < tmpActive.size(); j++) {
            sourceDoc.addToSelection(tmpActive.get(j), TransactionManager.STANDARD_PID);
        }
        sourceDoc.finish_transaction(TransactionManager.STANDARD_PID, false);
        sourceDoc.distributeEvent(SELECTION_CHANGED);
        targetDoc.distributeEvent(DATA_CHANGED);
    }

    /**
     * Fuehrt selektierte ModelElemente in diesem oder in beiden Modellen zusammen
     *
     * @param doc2
     * @param saveInBoth
     */
    public void joinElements(final GraphDocument doc2, final boolean saveInBoth) {
        if (selectedContainer.size() != 1 || doc2.selectedContainer.size() != 1) {
            return;
        }

        ModelElement me1 = selectedContainer.getLastSelected().getElement();
        ModelElement me2 = doc2.selectedContainer.getLastSelected().getElement();

        joinElements(me1, me2, doc2, saveInBoth);

        distributeEvent(DATA_CHANGED);
    }

    /**
     * @param me1
     * @param me2
     * @param doc2
     * @param saveInBoth
     */
    private void joinElements(final ModelElement me1, final ModelElement me2, final GraphDocument doc2, final boolean saveInBoth) {
        if (me1 instanceof Bendpoint) {
            return;
        }

        ModelElement me3 = findElementCoded(me2.getHashString());
        if (me3 != null && me3 != me2) {
            if (!me1.join(me2, false)) {
                return;
            }
        } else {
            if (!me1.join(me2, true)) {
                return;
            }
        }

        me1.refreshText();

        MetaModel metaModel = getMetaModel();
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
                edge = (Edge) metaModel.createElement(edge, true);
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
                edge = (Edge) metaModel.createElement(edge, true);
                edge.setStartAndInsert(me3);
                edge.setEndAndInsert(me1);
            } else {
                continue;
            }

            gdcoll.getMainGraphDocument().getLayer(edge.layerFor()).add(edge.createContainer(gdcoll.getMainGraphDocument()));

            if (this != gdcoll.getMainGraphDocument() && me1.getContainer(this) != null && me3.getContainer(this) != null) {
                getLayer(edge.layerFor()).add(edge.createContainer(this));
            }

            joinElements(edge, oldEdge, doc2, saveInBoth);
        }
    }

}