package de.imise.tool3lgm.graphtools.model;

import java.util.Date;

import de.imise.tool3lgm.graphtools.elements.Composition;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.graphtools.view.container.BendpointContainer;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.LayerContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;
import de.imise.tool3lgm.graphtools.view.graph.ViewParameter;
import de.imise.util.StringUtils;

public class Szenario extends LGMGraphDocument {

    /**
     * COMMENTME
     */
    private static int szenCounter = 0;

    /**
     * COMMENTME
     */
    private ViewParameter viewParam = null;

    /**
     * @param _gdcoll
     * @param title
     */
    public Szenario(final GDCollection _gdcoll, final String title, final String description, final String hashString) {
        super(_gdcoll);
        setTitle(title == null ? "" : title);
        setDescription(description);
        //wenn der HashString gültig und noch nicht vergeben ist -> setze ihn
        if (StringUtils.isValid(hashString, "null") && gdcoll.getGraphDocumentCoded(hashString) == null) {
            this.hashString = hashString;
        } else {
            this.hashString = "SZN" + "_" + new Date().getTime() + "_" + szenCounter++;
        }
    }

    /**
     * Fügt eine Kopie des übergebene ElementContainers in dieses Szenario ein.
     *
     * @param ec
     * @return
     */
    public ElementContainer addContainerCopy(final ElementContainer ec) {
        if (ec.getParent() == null) {
            return null;
        }

        ModelElement me = ec.getElement();

        ElementContainer retVal = me.getContainer(this);
        if (retVal != null) {
            return retVal;
        }

        retVal = ec.clone(false, this);
        if (retVal == null) {
            return null;
        }
        if (!(ec.getGraphDocument() instanceof Szenario)) {
            retVal.setFont(null);
            retVal.setAlpha(GraphElementLayout.NICHT_TRANSPARENT);
            retVal.setForm(null);
            retVal.setSize(mapping.getStandardWidth(retVal.getElement().getClass()), mapping.getStandardHeight(retVal.getElement().getClass()));
        }

        retVal.setParent(null);
        int layernum = ((LayerContainer) ec.getParent()).getLayerNumber();
        layer[layernum].add(retVal);
        if (retVal instanceof EdgeContainer && !retVal.getElement().isUnpaintable()) {
            for (BendpointContainer kpC : ((EdgeContainer) retVal).iterateBendpointContainers()) {
                layer[layernum].add(kpC);
                getCollection().addNodeToMainDoc(kpC, layernum);
            }
            ((EdgeContainer) retVal).computeBorderPoints();
        }
        retVal.setVisible(true);
        retVal.refreshText();

        return retVal;
    }

    /**
     * Holt das Slave-Element der übergegebenen <code>Composition</code> in dieses Szenario
     *
     * @param k
     * @param sourceDoc
     */
    private final void updateSlaveContainers(final Composition com, final GraphDocument sourceDoc) {
        ModelElement master = com.getMaster();
        if (master == null || master.isUnique()) {
            return;
        }
        ModelElement slave = com.getSlave();
        if (slave == null || slave.isUnique()) {
            return;
        }
        if (isMyElement(master)) {
            if (!isMyElement(slave)) {
                ElementContainer slaveCont = slave.getContainer(sourceDoc);
                if (slaveCont == null) {
                    slaveCont = slave.getContainer(gdcoll.getMainGraphDocument());
                }
                if (!(slaveCont instanceof NodeContainer)) {
                    return;
                }
                addElementToSzenario(getHashString(), (NodeContainer) slaveCont, TransactionManager.STANDARD_PID);
                //wenn der Container aus dem Hauptdokument übernommen wurde -> initiale Grafik setzen
                if (sourceDoc == getCollection().getMainGraphDocument()) {
                    addict(hashString, com.getClass().getName(), com.getHashString(), master, slave, GDCommands.INVALID_EDGE_INDEX, TransactionManager.STANDARD_PID);
                }
            }
        }
    }

    //	/**
    //	 * Erzeugt alle Kanten für den übergebenen ElementContainer.
    //	 *
    //	 * @param egdeStartOrEndContainer
    //	 * @param sourceDoc
    //	 * 			Haupt- oder Teilmodell, aus dem das Element und die Kanten übernommen werden sollen
    //	 * @param select
    //	 * 			wenn <code>true</code> sind alle neuen Kanten- und Element-Container nach dieser Aktion selektiert (zusätzlich
    //	 * 			zur ursprünglichen Selektion)
    //	 * @param pid
    //	 */
    //	public void createEdgeContainer(ElementContainer egdeStartOrEndContainer, GraphDocument sourceDoc, boolean select, int pid) {
    //
    //		Diese Version der Funktion funktioniert nicht richtig -> es werdfen keine Kanten auf dem Layer hinzugefügt -> die alte Version wieder eingebaut
    //
    //		start_transaction(pid, false);
    //		//wenn das Element, dessen Kanten hinzugefügt werden sollen, nicht leer und nicht einmalig ist
    //		if ((egdeStartOrEndContainer != null) && (!egdeStartOrEndContainer.getElement().isUnique())) {
    //			//für alle Kanten des Elements
    //			for (Kante ka : egdeStartOrEndContainer.getElement().getEdges()) {
    //				ElementContainer edgeCont = ka.getContainer(this);
    //
    //				//wenn die Kante nicht bereits in diesem Szenario vorkommt
    //				if (edgeCont != null) {
    //					//bei Compositions auch das Slave-Element in dieses Szenario holen (wenn sie es nicht unique ist)
    //					if (ka instanceof Composition)
    //						updateSlaveContainers((Composition)ka, sourceDoc);
    //					//wenn Start und End-Element der Kante einen Container in diesem Szenario haben
    //					if ((endsAreMine(ka))) {
    //						//hole den Container der Kante aus dem Quelldokument
    //						EdgeContainer oldKC = (EdgeContainer)ka.getContainer(sourceDoc);
    //						//wenn es keinen gibt, hole den Container aus dem Hauptmodell
    //						if (oldKC == null)
    //							oldKC = (EdgeContainer)ka.getContainer(sourceDoc.getCollection().getGraphDocument());
    //						//füge eine Kopie des Kante-Containers in dieses Szenario ein
    //						edgeCont = (EdgeContainer)addContainerCopy(oldKC);
    //						if (edgeCont == null)
    //							continue;
    //						edgeCont.refreshText();
    //						if (select)
    //							addToSelection(edgeCont, pid);
    //					}
    //				}
    //			}
    //		}
    //		finish_transaction(pid, false);
    //		distributeEvent(DATA_CHANGED);
    //	}

    /**
     * Erzeugt alle Kanten für den übergebenen ElementContainer.
     *
     * @param egdeStartOrEndContainer
     * @param sourceDoc
     *            Haupt- oder Teilmodell, aus dem das Element und die Kanten übernommen werden sollen
     * @param select
     *            wenn <code>true</code> sind alle neuen Kanten- und Element-Container nach dieser Aktion selektiert (zusätzlich
     *            zur ursprünglichen Selektion)
     * @param pid
     */
    public void createEdgeContainer(final ElementContainer egdeStartOrEndContainer, final GraphDocument sourceDoc, final boolean select, final int pid) {

        start_transaction(pid, false);
        //wenn das Element, dessen Kanten hinzugefügt werden sollen, nicht leer und nicht einmalig ist
        if (egdeStartOrEndContainer != null && !egdeStartOrEndContainer.getElement().isUnique()) {
            //für alle Kanten des Elements
            for (Kante ka : egdeStartOrEndContainer.getElement().getEdges()) {
                //wenn die Kante nicht bereits in diesem Szenario vorkommt
                boolean b = !isMyElement(ka);
                if (b) {
                    //bei Compositions auch das Slave-Element in dieses Szenario holen (wenn sie es nicht unique ist)
                    if (ka instanceof Composition) {
                        updateSlaveContainers((Composition) ka, sourceDoc);
                    }
                    //wenn Start und End-Element der Kante einen Container in diesem Szenario haben

                    b = endsAreMine(ka);
                    if (b) {
                        //hole den Container der Kante aus dem Quelldokument
                        EdgeContainer oldKC = (EdgeContainer) ka.getContainer(sourceDoc);
                        //wenn es keinen gibt, hole den Container aus dem Hauptmodell
                        if (oldKC == null) {
                            oldKC = (EdgeContainer) ka.getContainer(sourceDoc.getCollection().getMainGraphDocument());
                        }
                        //füge eine Kopie des Kante-Containers in dieses Szenario ein
                        EdgeContainer kc = (EdgeContainer) addContainerCopy(oldKC);
                        if (kc == null) {
                            continue;
                        }
                        kc.refreshText();
                        if (select) {
                            addToSelection(kc, pid);
                        }
                    }
                }
            }
            //wenn das Element, dessen Kanten hinzugefügt werden sollen, leer oder einmalig ist
        }
        //AXS 05.09.2008: else-Fall mal heraus genommen, weil der nur irgendwelche Symptome bei Inkonsistenzen mit dem
        //  Holzhammer behebt. wenn alles richtig läuft, darf das hier meiner meinung gar nicht auftreten
        //
        //
        //		else {
        //			//für jede Ebene dieses Szenarios
        //			for (LayerContainer lc : layer) {
        //				//für alle Knoten der Ebene (das können im Schleifendurchlauf mehr werden -> nicht über den Iterator gehen)
        //				for (int j = 0; j < lc.getKnotenCount(); j++) {
        //					egdeStartOrEndContainer = lc.getNodeContainer(j);
        //					ModelElement el = egdeStartOrEndContainer.getElement();
        //					//einmalige Elemente überspringen
        //					if (el.isUnique())
        //						continue;
        //					//für alle Kanten des Elementes
        //					for (Kante ka : el.getEdges()) {
        //						//wenn die aktuelle Kante noch nicht in diesem Szenario ist
        //						if (!isMyElement(ka)) {
        //							//wenn es sich um eine Composition handelt
        //							if (ka instanceof Composition)
        //								//bringe die Slave-Container ins Szenario (wenn sie nicht unique sind)
        //								updateSlaveContainers((Composition)ka, sourceDoc);
        //							//wenn Start- und Endelement jetzt in diesem Szenario sind
        //							if ((endsAreMine(ka))) {
        //								//hole den Container der Kante aus dem Quelldokument
        //								EdgeContainer oldKC = (EdgeContainer)ka.getContainer(sourceDoc);
        //								//wenn es keine gab
        //								if (oldKC == null){
        //									//hole den Container der Kante aus dem hauptdokument des Quelldokuments (das muss nicht dasslebe
        //									//Hauptdokument dieses Szenarios sein
        //									oldKC = (EdgeContainer)ka.getContainer(sourceDoc.getCollection().getGraphDocument());
        //								}
        //								//füge eine Kopie des Kante-Containers in dieses Szenario ein
        //								EdgeContainer kc = (EdgeContainer)addContainerCopy(oldKC);
        //								if (kc == null)
        //									continue;
        //								kc.refreshText();
        //								if (select)
        //									addToSelection(kc, pid);
        //							}
        //						}
        //					}
        //				}
        //				//für alle Kanten des aktuellen Layers
        //				for (int j = 0; j < lc.getKantenCount(); j++) {
        //					//
        //					egdeStartOrEndContainer = lc.getEdgeContainer(j);
        //					ModelElement el = egdeStartOrEndContainer.getElement();
        //					if (el.isUnique())
        //						continue;
        //					for (Kante ka : el.getEdges()) {
        //						if (!isMyElement(ka)) {
        //							if ((endsAreMine(ka))) {
        //								EdgeContainer oldKC = (EdgeContainer)ka.getContainer(sourceDoc);
        //								if (oldKC == null)
        //									oldKC = (EdgeContainer)ka.getContainer(sourceDoc.getCollection().getGraphDocument());
        //								EdgeContainer kc = (EdgeContainer)addContainerCopy(oldKC);
        //								if (kc == null)
        //									continue;
        //								kc.refreshText();
        //								if (select)
        //									addToSelection(kc, pid);
        //							}
        //						}
        //					}
        //				}
        //			}
        //		}

        finish_transaction(pid, false);
        distributeEvent(DATA_CHANGED);
    }

    /**
     * @param t
     * @return
     */
    public boolean endsAreMine(final Kante t) {
        return isMyElement(t.getStart()) && isMyElement(t.getEnd());
    }

    @Override
    public boolean isMyElement(final ModelElement me) {
        if (me == null) {
            return false;
        }
        if (me.isUnique()) {
            return false;
        }

        return super.isMyElement(me);
    }

    /**
     * @param _view
     */
    public void setViewParameter(final ViewParameter _view) {
        viewParam = _view;
        if (_view.pageSizeFactor != 0) {
            setPageSizeFactor(_view.pageSizeFactor);
        }
    }

    /**
     * @return
     */
    public ViewParameter getViewParameter() {
        return viewParam;
    }

}