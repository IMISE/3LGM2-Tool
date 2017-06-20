package de.imise.tool3lgm.graphtools.view.container;

import java.util.HashSet;

import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.node.Anwendungsbaustein;
import de.imise.tool3lgm.graphtools.path.MetaPath;
import de.imise.tool3lgm.graphtools.path.PathFinder;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;

/**
 * Von {@link NodeContainer} abgeleitete Klasse, die den Container für {@link Aufgabe}n
 * und {@link Anwendungsbaustein}e darstellt.
 * <p>
 * Im Unterschied zur Oberklasse kann hier das Anzeigen der Interebenenbeziehungen gesteurt werden.
 * 
 * @author fstephan
 */
public class InterLayerConnectedNodeContainer extends NodeContainer {

    /** Gibt wieder, ob die Interebenenbeziehungen angezeigt werden sollen, oder nicht. */
    private boolean showInterLayerConnections = false;

    /**
     * @param neu
     * @param l
     * @param doc
     */
    public InterLayerConnectedNodeContainer(final Knoten neu, final GraphElementLayout l, final GraphDocument doc) {
        super(neu, l, doc);
        init(doc);
    }

    /**
     * @param alt
     * @param doc
     */
    public InterLayerConnectedNodeContainer(final NodeContainer alt, final GraphDocument doc) {
        super(alt, doc);
        init(doc);
    }

    /**
     * Konstruktor
     * <p>
     * Benötigt für cloning in {@link ElementContainer#clone(boolean, GraphDocument)}
     */
    public InterLayerConnectedNodeContainer() {
        super();
    }

    /**
     * Konstruktor
     * 
     * @param k
     * @param doc
     */
    public InterLayerConnectedNodeContainer(final Knoten k, final GraphDocument doc) {
        super(k, doc);
        init(doc);
    }

    /**
     * Überprüft initial ob Interebenenbeziehungen angezeigt werden sollen. <br>
     * Dafür wird überprüft, ob auf der jeweiligen Ebene das Anzeigen aller Interebenenbeziehungen
     * aktiviert ist oder nicht.
     * 
     * @param doc
     */
    protected void init(final GraphDocument doc) {
        LayerContainer lc = doc.getLayer(ModelConstants.layerFor(me.getClass()));
        showInterLayerConnections = lc.isShowInterLayerConnections();
    }

    /** Gibt wieder, ob die Konfigurationen angezeigt werden sollen, oder nicht. */
    public boolean isShowInterLayerConnections() {
        return showInterLayerConnections;
    }

    /**
     * (De-)aktiviert das Anzeigen der Interebenenbeziehungen
     * 
     * @param show
     * @param doc
     */
    public void setShowInterLayerConnections(final boolean show) {
        showInterLayerConnections = show;
        for (MetaPath metaPath : ModelConstants.INTER_LAYER_CONNECTED_ELEMENT_PATHES) {
            if (metaPath.getStartClass().isAssignableFrom(me.getClass())) {
                HashSet<ModelElement> dirCon = PathFinder.getDirectConnectedElements(me, metaPath, doc.getCollection());
                for (ModelElement connected : dirCon) {
                    ElementContainer ec = connected.getContainer(connected.isUnique() ? doc.getCollection().getMainGraphDocument() : doc);
                    if (ec != null) {
                        ec.setVisible(show);
                    }
                }
                if (!isExpanded()) {
                    for (ModelElement part : me.getPartElements()) {
                        for (ModelElement connected : PathFinder.getDirectConnectedElements(part, metaPath, doc.getCollection())) {
                            ElementContainer ec = connected.getContainer(connected.isUnique() ? doc.getCollection().getMainGraphDocument() : doc);
                            if (ec != null) {
                                ec.setVisible(show);
                            }
                        }
                    }
                }
            }
        }
    }
}
