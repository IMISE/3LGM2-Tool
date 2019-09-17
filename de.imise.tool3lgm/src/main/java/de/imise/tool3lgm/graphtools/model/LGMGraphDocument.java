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
import java.util.Set;

import javax.swing.JOptionPane;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.analyse.redundancy.SimpleRedundancyAnalysis;
import de.imise.tool3lgm.graphtools.analyse.redundancy.SimpleRedundancyAnalysisDefinitions.SingleSimpleRedundancyAnalysisDefinition;
import de.imise.tool3lgm.graphtools.dialog.OverwriteDialog;
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
    //	public void copySelectedToModel(LGMGraphDocument dest) {
    //
    //		GraphDocument mainDoc =  getCollection().getGraphDocument();
    //		GDCollection destGDColl = dest.getCollection();
    //		GraphDocument destMainDoc = destGDColl.getGraphDocument();
    //
    //
    //		if (destGDColl.isChanged()) {
    //			int value = JOptionPane.showConfirmDialog(null, getResString("join_speicherfrage"), getResString("tool3lgm"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null);
    //			if (value == JOptionPane.YES_OPTION) {
    //				try {
    //					if (!destGDColl.saveToFile())
    //						return;
    //				} catch (IOException exp) {
    //					Log.show(Log.FATAL, getResString("FehlerAllgemein"), exp);
    //					return;
    //				}
    //			} else
    //				return;
    //		}
    //
    //		ArrayList<ModelElement> copyElements = new ArrayList<ModelElement>();
    //		HashSet<UserField> userFields = new HashSet<UserField>();
    //		gdcoll.resolveCopyDependencies(selectedContainer, copyElements, userFields);
    //
    //		//Liste aller Kanten, bei denen das das eine Endelement gerade kopiert werden soll und das
    //		//andere nicht kopiert werden soll aber bereits im Zielmodell vorkommt
    //		ArrayList<ModelElement> splitEdges = new ArrayList<ModelElement>();
    //
    //		for (int i=0; i<copyElements.size(); i++) {
    //			ModelElement me = copyElements.get(i);
    //			if (me instanceof Knickpunkt)
    //				continue;
    //			for (Edge edge : me.getEdges()) {
    //				if (copyElements.contains(edge))
    //					continue;
    //				ModelElement other = edge.getOther(me);
    //				if (destMainDoc.findElementCoded(other.getHashString())!=null)
    //					splitEdges.add(edge);
    //			}
    //		}
    //
    //		for (UserField uf : userFields){
    //			if (uf != null)
    //				destGDColl.getUserFieldDefinitions().add(uf);
    //		}
    //
    //		ModelElement newE;
    //
    //		ArrayList<ElementContainer> tmpActive = new ArrayList<ElementContainer>(selectedContainer);
    //		ArrayList<Edge> edges = new ArrayList<Edge>();
    //		ArrayList<BendpointContainer> knickpunkte = new ArrayList<BendpointContainer>();
    //
    //		mainDoc.deselectAll(false);
    //
    //		int pid = TransactionManager.STANDARD_PID;
    //		destMainDoc.start_transaction(pid);
    //
    //		try {
    //			destMainDoc.deselectAll(true);
    //			destGDColl.getIconTable().putAll(getCollection().getIconTable());
    //
    //			// lowest bit determin whether ask user about what to do, when hashcode already exists in dest (1 == do not ask / remember last decision)
    //			// OverwriteDialog.OVERWRITE
    //			// OverwriteDialog.JOIN
    //			// OverwriteDialog.NOTHING
    //			int overwriteJoinNothing = 0;
    //			for (ModelElement insert : copyElements){
    //				ElementContainer insertC = insert.getContainer(this);
    //				if (insertC == null)
    //					insertC = insert.getContainer(mainDoc);
    //
    //				//wenn bereits ein Element mit dem gleichen Hash-Wert im Zieldokument existiert
    //				if ((newE = destMainDoc.findElementCoded(insert.getHashString())) != null) {
    //					if ((overwriteJoinNothing & 1) == 0) {
    //						select(insertC, pid);
    //						distributeEvent(SELECTION_CHANGED, insertC, null, pid);
    //						overwriteJoinNothing = OverwriteDialog.showDialog(Tool3lgm.tool, newE, insert);
    //					}
    //
    //					if ((overwriteJoinNothing & OverwriteDialog.OVERWRITE) > 0) {
    //
    //					} else if ((overwriteJoinNothing & OverwriteDialog.JOIN) > 0) {
    //						dest.joinElements(newE, insert, this, false);
    //						if (newE instanceof Edge) {
    //							((Edge)newE).reconnect(destGDColl);
    //							((Edge)newE).refreshText();
    //						}
    //					} else if ((overwriteJoinNothing & OverwriteDialog.DONOTHING) > 0) {
    //						continue;
    //					}
    //				//wenn der Hash des zu kopierenden Elementes noch nicht im Modell vorkommt
    //				} else {
    //					ElementContainer newC;
    //					newC = insertC.clone(true, dest);
    //					newE = newC.getElement();
    //					newE.setHashString(insert.getHashString());
    //					ElementContainer newMainC = newC.clone(false, destMainDoc);
    //					newMainC.setVisible(true);
    //					newMainC.setExpanded(true);
    //					newMainC.setHighLight(false);
    //					newMainC.refreshText();
    //					destMainDoc.getLayer(newE.layerFor()).add(newMainC);
    //					if (newE instanceof Edge)
    //						edges.add((Edge)newE);
    //					else if (newE instanceof Knickpunkt)
    //						knickpunkte.add((BendpointContainer)newC);
    //					else {
    //						if (!newE.isUnique() && (dest instanceof Szenario)) {
    //							newC.refreshText();
    //							dest.getLayer(newE.layerFor()).add(newC);
    //						}
    //						destMainDoc.addToSelection(newMainC, pid);
    //					}
    //				}
    //			}
    //			for (Edge kante : edges){
    //				if (!kante.reconnect(destGDColl))
    //					destGDColl.deleteElement(kante, pid);
    //				else {
    //					EdgeContainer edgeCont = (EdgeContainer)kante.getContainer(destMainDoc);
    //					destGDColl.addEdge(edgeCont, kante.layerFor(), pid);
    //					if (!kante.isUnique() && (dest instanceof Szenario)) {
    //						EdgeContainer newC = (EdgeContainer)kante.getContainer(dest);
    //						if (newC == null) {
    //							throw new Exception(getResString("fehler"));
    //						}
    //						ArrayList<BendpointContainer> kpList = newC.getBendpointContainerList();
    //						for (int j = 0; j < kpList.size(); j++) {
    //							dest.getLayer(kante.layerFor()).add((BendpointContainer)kpList.get(j));
    //						}
    //						newC.computeBorderPoints();
    //					}
    //					destMainDoc.addToSelection(kante.getContainer(destMainDoc), pid);
    //				}
    //			}
    //			ArrayList<EdgeContainer> edgeConts = new ArrayList<EdgeContainer>();
    //			while (!knickpunkte.isEmpty()) {
    //				BendpointContainer kp = (BendpointContainer)knickpunkte.remove(0);
    //				BendpointContainer oldKP = this.findBendpointContainerCoded(kp.getHashString());
    //				EdgeContainer kC = dest.findEdgeContainerCoded(kp.getKnickpunktKnoten().getKantenHash());
    //				EdgeContainer oldKC = oldKP.getKnickpunktKnoten().getOwner();
    //				if (oldKC == null)
    //					oldKC = this.findEdgeContainerCoded(kC.getHashString());
    //				if (kC != null) {
    //					if (! (edges.contains(kC)))
    //						edgeConts.add(kC);
    //					kp.getKnickpunktKnoten().setOwner(kC);
    //					kC.setKnickpunkt(kp, oldKC.getIndexOfKnickpunkt(oldKP.getKnickpunktKnoten()));
    //					dest.getLayer(kC.layerFor()).add(kp);
    //				}
    //			}
    //			for (EdgeContainer kc : edgeConts)
    //				kc.computeBorderPoints();
    //
    //			destMainDoc.finish_transaction(pid);
    //		} catch (Exception ex) {
    //			destMainDoc.undo(pid);
    //			Log.show(Log.ERROR, getResString("FehlerKorrupt") + "\n" + destGDColl.getName(), ex);
    //		}
    //		destGDColl.setChanged(true);
    //		start_transaction(TransactionManager.STANDARD_PID, false);
    //		deselectAll (true);
    //		for (int j = 0; j < tmpActive.size(); j++) {
    //			addToSelection((ElementContainer)tmpActive.get(j), TransactionManager.STANDARD_PID);
    //		}
    //		finish_transaction(TransactionManager.STANDARD_PID, false);
    //		distributeEvent(SELECTION_CHANGED);
    //		dest.distributeEvent(DATA_CHANGED);
    //	}

    /**
     * @param dest
     */
    public void copySelectedToModel(final LGMGraphDocument dest) {

        GraphDocument mainDoc = getCollection().getMainGraphDocument();
        GDCollection destGDColl = dest.getCollection();
        GraphDocument destMainDoc = destGDColl.getMainGraphDocument();

        if (destGDColl.isChanged()) {
            int value = JOptionPane.showConfirmDialog(null, getResString("join_speicherfrage"), getResString("tool3lgm"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null);
            if (value == JOptionPane.YES_OPTION) {
                if (!destGDColl.getFileHandler().saveToFile()) {
                    return;
                }
            } else {
                return;
            }
        }

        List<ModelElement> copyElements = new ArrayList<>();
        Set<UserField> userFields = new HashSet<>();
        gdcoll.resolveCopyDependencies(getSortedSelection(), copyElements, userFields);

        for (UserField uf : userFields) {
            if (uf != null) {
                destGDColl.getUserFieldDefinitions().add(uf);
            }
        }

        ModelElement newE;

        List<Edge> edges = new ArrayList<>();
        List<BendpointContainer> knickpunkte = new ArrayList<>();

        List<ElementContainer> tmpActive = new ArrayList<>(selectedContainer);

        mainDoc.deselectAll(false);

        destMainDoc.start_transaction(STANDARD_PID);

        try {
            destMainDoc.deselectAll(true);
            destGDColl.getIconTable().putAll(getCollection().getIconTable());

            // lowest bit determin whether ask user about what to do, when hashcode already exists in dest (1 == do not ask / remember last decision)
            // OverwriteDialog.OVERWRITE
            // OverwriteDialog.JOIN
            // OverwriteDialog.NOTHING

            int overwriteJoinNothing = 0;
            for (ModelElement insert : copyElements) {
                ElementContainer insertC = insert.getContainer(this);
                if (insertC == null) {
                    insertC = insert.getContainer(mainDoc);
                }

                if ((newE = destMainDoc.findElementCoded(insert.getHashString())) != null) {
                    if ((overwriteJoinNothing & 1) == 0) {
                        select(insertC, STANDARD_PID);
                        distributeEvent(SELECTION_CHANGED, insertC, STANDARD_PID);
                        overwriteJoinNothing = OverwriteDialog.showDialog(Static.getMainFrame(), newE, insert);
                    }

                    if ((overwriteJoinNothing & OverwriteDialog.OVERWRITE) > 0) {

                    } else if ((overwriteJoinNothing & OverwriteDialog.JOIN) > 0) {
                        dest.joinElements(newE, insert, this, false);
                        if (newE instanceof Edge) {
                            ((Edge) newE).reconnect(destGDColl);
                            ((Edge) newE).refreshText();
                        }
                    } else if ((overwriteJoinNothing & OverwriteDialog.DONOTHING) > 0) {
                        continue;
                    }
                } else {
                    ElementContainer newC;
                    newC = insertC.clone(true, dest);
                    if (newC != null) {
                        newE = newC.getElement();
                        newE.setHashString(insert.getHashString());
                        ElementContainer newMainC = newC.clone(false, destMainDoc);
                        if (newMainC != null) {
                            newMainC.setVisible(true);
                            newMainC.setExpanded(true);
                            newMainC.setHighLight(false);
                            newMainC.refreshText();
                            int layerIndex = insertC.layerFor();
                            LayerContainer layer = destMainDoc.getLayer(layerIndex);
                            layer.add(newMainC);
                            if (newE instanceof Edge) {
                                edges.add((Edge) newE);
                            } else if (newE instanceof Bendpoint) {
                                knickpunkte.add((BendpointContainer) newC);
                            } else {
                                if (!newE.isUnique() && dest instanceof Szenario) {
                                    newC.refreshText();
                                    dest.getLayer(newE.layerFor()).add(newC);
                                }
                                destMainDoc.addToSelection(newMainC, STANDARD_PID);
                            }
                        }
                    }
                }
            }
            for (Edge edge : edges) {
                if (!edge.reconnect(destGDColl)) {
                    destGDColl.deleteElement(edge, STANDARD_PID);
                } else {
                    int edgeLayer = edge.layerFor();
                    destGDColl.addEdge((EdgeContainer) edge.getContainer(destMainDoc), STANDARD_PID);
                    if (!edge.isUnique() && dest instanceof Szenario) {
                        EdgeContainer newC = (EdgeContainer) edge.getContainer(dest);
                        if (newC == null) {
                            throw new Exception(getResString("fehler"));
                        }
                        dest.getLayer(edgeLayer).add(newC);
                        LayerContainer layerContainer = dest.getLayer(edgeLayer);
                        for (BendpointContainer bendpointContainer : newC.iterateBendpointContainers()) {
                            layerContainer.add(bendpointContainer);
                        }
                        newC.computeBorderPoints();
                    }
                    destMainDoc.addToSelection(edge.getContainer(destMainDoc), STANDARD_PID);
                }
            }
            List<EdgeContainer> edgeConts = new ArrayList<>();
            while (!knickpunkte.isEmpty()) {
                BendpointContainer kp = knickpunkte.remove(0);
                BendpointContainer oldKP = findBendpointContainerCoded(kp.getHashString());
                //der Container kann null sein, wenn die zu kopierende Kante auch noch mind. einen Knickpunkt in einem
                //anderen Teilmodell hat, denn es werden beim resolven der CopyDependencies alle Knickpunkte der Kante aus
                //allen Teilmodellen eingesammelt
                if (oldKP == null) {
                    continue;
                }
                EdgeContainer kC = dest.findEdgeContainerCoded(kp.getBendpoint().getEdgeHash());
                EdgeContainer oldKC = oldKP.getBendpoint().getOwner();
                if (oldKC == null) {
                    oldKC = findEdgeContainerCoded(kC.getHashString());
                }
                if (kC != null) {
                    if (!edges.contains(kC.getElement())) {
                        edgeConts.add(kC);
                    }
                    kp.getBendpoint().setOwner(kC);
                    kC.setBendpointContainer(kp, oldKC.getIndexOfBendpoint(oldKP.getBendpoint()));
                    dest.getLayer(kC.layerFor()).add(kp);
                }
            }
            for (EdgeContainer kc : edgeConts) {
                kc.computeBorderPoints();
            }

            destMainDoc.finish_transaction(STANDARD_PID);
        } catch (Exception ex) {
            destMainDoc.undo(STANDARD_PID);
            Log.show(Log.ERROR, getResString("FehlerKorrupt") + "\n" + destGDColl.getName(), ex);
        }
        start_transaction(STANDARD_PID, false);
        deselectAll(true);
        for (int j = 0; j < tmpActive.size(); j++) {
            addToSelection(tmpActive.get(j), STANDARD_PID);
        }
        finish_transaction(STANDARD_PID, false);
        distributeEvent(SELECTION_CHANGED);
        dest.distributeEvent(DATA_CHANGED);
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