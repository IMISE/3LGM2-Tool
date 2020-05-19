package de.imise.tool3lgm.graphtools.model;

import static de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType.DATA_CHANGED;
import static de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType.MODEL_OR_SZENARIO_NAME_CHANGED;

import java.util.Date;

import de.imise.tool3lgm.graphtools.metamodel.elements.CompositionEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
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
            retVal.setSize(mapping.getStandardWidth(meClass), mapping.getStandardHeight(meClass));
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
        retVal.setVisible(true);
        retVal.refreshText();
        me.updateHTMLName(retVal);
        return retVal;
    }

    /**
     * Holt das Slave-Element der übergegebenen <code>Composition</code> in dieses Szenario
     *
     * @param k
     * @param sourceDoc
     */
    private final void updateSlaveContainers(final Edge edge, final boolean forward, final GraphDocument sourceDoc) {
        ModelElement master = forward ? edge.getStart() : edge.getEnd();
        if (master == null || master.isUnique()) {
            return;
        }
        ModelElement slave = edge.getOther(master);
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
                    addict(hashString, edge.getClass().getName(), master, slave, TransactionManager.STANDARD_PID);
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
        //wenn das Element, dessen Kanten hinzugefügt werden sollen, nicht leer und nicht einmalig ist
        if (egdeStartOrEndContainer != null && !egdeStartOrEndContainer.getElement().isUnique()) {
            start_transaction(pid, false);
            //für alle Kanten des Elements
            for (Edge ka : egdeStartOrEndContainer.getElement().getEdges()) {
                //wenn die Edge nicht bereits in diesem Szenario vorkommt
                boolean b = !isMyElement(ka);
                if (b) {
                    //bei Compositions auch das Slave-Element in dieses Szenario holen (wenn sie es nicht unique ist)
                    if (ka instanceof CompositionEdge) {
                        //hier werden mit Absicht identische Konstanten verglichen, falls sich MASTER_TO_SLAVE_DIRECTION mal auf BACKWARD ändert (was sehr unwarhscheinlich ist)
                        updateSlaveContainers(ka, CompositionEdge.MASTER_TO_SLAVE_DIRECTION == Direction.FORWARD, sourceDoc);
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
    public final void setTitle(final String newTitle) {
        super.setTitle(newTitle);
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