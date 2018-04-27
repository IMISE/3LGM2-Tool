package de.imise.tool3lgm.graphtools.model;

import java.util.Date;

import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.CompositionEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
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
    private ViewParameter viewParam = new ViewParameter();

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
        Class<? extends ModelElement> meClass = me.getClass();
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
            retVal.setAlpha(GraphElementLayout.TRANSPARENCY_NONE);
            retVal.setForm(null);
            retVal.setSize(mapping.getStandardWidth(meClass), mapping.getStandardHeight(meClass));
        }

        retVal.setParent(null);
        int layernum = ((LayerContainer) ec.getParent()).getLayerNumber();
        layer[layernum].add(retVal);
        if (retVal instanceof EdgeContainer && ModelConstants.getGraphViewDefinition().isPaintable(meClass)) {
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
    private final void updateSlaveContainers(final CompositionEdge com, final GraphDocument sourceDoc) {
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
            for (Edge ka : egdeStartOrEndContainer.getElement().getEdges()) {
                //wenn die Edge nicht bereits in diesem Szenario vorkommt
                boolean b = !isMyElement(ka);
                if (b) {
                    //bei Compositions auch das Slave-Element in dieses Szenario holen (wenn sie es nicht unique ist)
                    if (ka instanceof CompositionEdge) {
                        updateSlaveContainers((CompositionEdge) ka, sourceDoc);
                    }
                    //wenn Start und End-Element der Edge einen Container in diesem Szenario haben

                    b = endsAreMine(ka);
                    if (b) {
                        //hole den Container der Edge aus dem Quelldokument
                        EdgeContainer oldKC = (EdgeContainer) ka.getContainer(sourceDoc);
                        //wenn es keinen gibt, hole den Container aus dem Hauptmodell
                        if (oldKC == null) {
                            oldKC = (EdgeContainer) ka.getContainer(sourceDoc.getCollection().getMainGraphDocument());
                        }
                        //füge eine Kopie des Edge-Containers in dieses Szenario ein
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
        }
        finish_transaction(pid, false);
        distributeEvent(GDCollectionChangeType.DATA_CHANGED);
    }

    /**
     * @param t
     * @return
     */
    public boolean endsAreMine(final Edge t) {
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
     * @return
     */
    public ViewParameter getViewParameter() {
        return viewParam;
    }

    public void deleteViewParameter() {
        viewParam = null;
    }

}