package de.imise.tool3lgm.graphtools.model;

import static de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType.DATA_CHANGED;
import static de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType.MODEL_OR_SZENARIO_NAME_CHANGED;

import java.util.Date;

import de.imise.tool3lgm.graphtools.metamodel.elements.CompositionEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.view.container.BendpointContainer;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.LayerContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;
import de.imise.tool3lgm.graphtools.view.graph.GraphViewParameter;
import de.imise.util.StringUtils;

/**
 * Speizielle Unterklasse für echte Teilmodelle mit grafischer Repräsentation.
 */
public class Szenario extends LGMGraphDocument {

    /**
     * COMMENTME
     */
    private static int szenCounter = 0;

    /**
     * COMMENTME
     */
    private final GraphViewParameter graphViewParameter = new GraphViewParameter();

    /**
     * @param _gdcoll
     * @param title
     */
    public Szenario(final GDCollection _gdcoll, final String title, final String description, final String id) {
        super(_gdcoll);
        setName(title == null ? "" : title);
        setDescription(description);
        //wenn die ID gültig und noch nicht vergeben ist -> setze ihn
        if (StringUtils.isValid(id, "null") && gdcoll.getGraphDocumentCoded(id) == null) {
            this.id = id;
        } else {
            this.id = "SZN" + "_" + new Date().getTime() + "_" + szenCounter++;
        }
    }

    /**
     * Fügt eine Kopie des übergebene ElementContainers in dieses Szenario ein.
     *
     * @param ec
     * @return
     */
    public ElementContainer addContainerCopy(final ElementContainer ec) {
        if (isMyElement(ec)) {
            return ec;
        }
        ModelElement me = ec.getElement();
        Class<? extends ModelElement> meClass = me.getClass();
        ElementContainer retVal = me.getContainer(this);
        if (retVal == null) {
            retVal = ec.clone(false, this);
        }
        if (retVal == null) {
            return null;
        }
        if (!(ec.getGraphDocument() instanceof Szenario)) {
            retVal.setFont(null);
            retVal.setAlpha(GraphElementLayout.TRANSPARENCY_NONE);
            retVal.setForm(null);
            int width = defaultElementsLayout.getStandardWidth(meClass);
            int height = defaultElementsLayout.getStandardHeight(meClass);
            retVal.setSize(width, height);
        }

        retVal.setParent(null);
        int layer = ec.layerFor();
        LayerContainer lc = this.layer[layer];
        lc.add(retVal);
        if (retVal instanceof EdgeContainer && me.isPaintable()) {
            for (BendpointContainer kpC : ((EdgeContainer) retVal).iterateBendpointContainers()) {
                lc.add(kpC);
                getCollection().addNodeToMainDoc(kpC, layer);
            }
            ((EdgeContainer) retVal).computeBorderPoints();
        }
        retVal.refreshText();
        me.updateGraphName(retVal);
        retVal.setVisible(ec.isVisible());
        return retVal;
    }

    /**
     * Brings the slave element of the given {@link CompositionEdge} into this
     * scenario if it does not already occur in it.
     *
     * @param compositionEdge
     * @param sourceDoc
     * @param pid
     */
    private final void updateSlaveContainers(final CompositionEdge compositionEdge, final GraphDocument sourceDoc, final int pid) {
        ModelElement master = compositionEdge.getMaster();
        if (master == null || master.isUnique()) {
            return;
        }
        ModelElement slave = compositionEdge.getSlave();
        if (slave == null || slave.isUnique()) {
            return;
        }
        if (isMyElement(master)) {
            if (!isMyElement(slave)) {
                //we add the container from the mainDoc to this szenario
                //because this container should have the default layout
                //but is invisible by default
                ElementContainer slaveCont = slave.getContainer(sourceDoc);
                if (slaveCont == null) {
                    GraphDocument mainDoc = gdcoll.getMainDoc();
                    slaveCont = slave.getContainer(mainDoc);
                }
                if (!(slaveCont instanceof NodeContainer)) {
                    return;
                }
                //add a copy of the mainDoc container to this szenario
                NodeContainer addedSlaveContainer = addElementToSzenario(getID(), (NodeContainer) slaveCont, false, pid); //dont' change the selection because linkSelected(...) iterates over it
                //set the container visible
                addedSlaveContainer.setVisible(true);

                //raiseSlaves(..) brings '
                ElementContainer masterInSourceDocContainer = master.getContainer(sourceDoc);
                raiseSlaves(masterInSourceDocContainer, pid);
            }
        }
    }

    /**
     * Erzeugt alle Kanten für den übergebenen ElementContainer.
     *
     * @param egdeStartOrEndContainer
     * @param sourceDoc Haupt- oder Teilmodell, aus dem das Element und die
     *            Kanten übernommen werden sollen
     * @param select wenn <code>true</code> sind alle neuen Kanten- und
     *            Element-Container nach dieser Aktion selektiert (zusätzlich
     *            zur ursprünglichen Selektion)
     * @param pid
     */
    public void createEdgeContainer(final ElementContainer egdeStartOrEndContainer, final GraphDocument sourceDoc, final boolean select, final int pid) {
        if (egdeStartOrEndContainer == null) {
            return;
        }
        ModelElement me = egdeStartOrEndContainer.getElement();
        //wenn das Element, dessen Kanten hinzugefügt werden sollen, nicht leer und nicht einmalig ist
        if (!me.isUnique()) {
            start_transaction(pid, false);
            //für alle Kanten des Elements
            for (Edge edge : me.getEdges()) {
                //wenn die Edge nicht bereits in diesem Szenario vorkommt
                if (!isMyElement(edge)) {
                    //bei Compositions auch das Slave-Element in dieses Szenario holen (wenn sie es nicht unique ist)
                    if (edge instanceof CompositionEdge) {
                        updateSlaveContainers((CompositionEdge) edge, sourceDoc, pid);
                    }
                    //wenn Start und End-Element der Edge einen Container in diesem Szenario haben
                    if (endsAreMine(edge)) {
                        //hole den Container der Edge aus dem Quelldokument
                        EdgeContainer oldKC = edge.getContainer(sourceDoc);
                        //wenn es keinen gibt, hole den Container aus dem Hauptmodell
                        if (oldKC == null) {
                            GraphDocument mainDoc = sourceDoc.getMainDoc();
                            oldKC = edge.getContainer(mainDoc);
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
            finish_transaction(pid, false);
            distributeEvent(DATA_CHANGED);
        }
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
     * @param newTitle
     */
    @Override
    public final void setName(final String newTitle) {
        super.setName(newTitle);
        distributeEvent(MODEL_OR_SZENARIO_NAME_CHANGED);
    }

    /**
     * @return
     */
    public GraphViewParameter getGraphViewParameter() {
        return graphViewParameter;
    }

    /**
     * @param graphViewParameter
     */
    public void adaptGraphViewParameter(final GraphViewParameter graphViewParameter) {
        this.graphViewParameter.adapt(graphViewParameter);
    }

}