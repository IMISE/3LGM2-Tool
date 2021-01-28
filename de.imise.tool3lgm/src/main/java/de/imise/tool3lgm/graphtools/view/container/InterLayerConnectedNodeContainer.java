package de.imise.tool3lgm.graphtools.view.container;

import java.awt.Color;
import java.awt.Graphics;

import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;

/**
 * Von {@link NodeContainer} abgeleitete Klasse, die der Container für Elemente
 * ist, die in der Mehrebenenansicht Verbindungen zwischen den Ebenen anzeigen.
 * <p>
 * Im Unterschied zur Oberklasse kann hier das Anzeigen der
 * Interebenenbeziehungen gesteurt werden.
 *
 * @author fstephan
 */
public final class InterLayerConnectedNodeContainer extends NodeContainer {

    /**
     * Gibt wieder, ob die Interebenenbeziehungen angezeigt werden sollen, oder
     * nicht.
     */
    private boolean showInterLayerConnections = false;

    /**
     * The color of interlayer connections of this container are painted in the
     * graph.
     */
    private Color interLayerConnectionColor = null;

    /**
     * @param neu
     * @param l
     * @param doc
     */
    public InterLayerConnectedNodeContainer(final Node neu, final GraphElementLayout l, final GraphDocument doc) {
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
     * Benötigt für cloning in
     * {@link ElementContainer#clone(boolean, GraphDocument)}
     */
    public InterLayerConnectedNodeContainer() {
    }

    /**
     * Konstruktor
     *
     * @param k
     * @param doc
     */
    public InterLayerConnectedNodeContainer(final Node k, final GraphDocument doc) {
        super(k, doc);
        init(doc);
    }

    /**
     * Überprüft initial ob Interebenenbeziehungen angezeigt werden sollen. <br>
     * Dafür wird überprüft, ob auf der jeweiligen Ebene das Anzeigen aller
     * Interebenenbeziehungen aktiviert ist oder nicht.
     *
     * @param doc
     */
    protected void init(final GraphDocument doc) {
        MetaModel metaModel = doc.getMetaModel();
        Class<? extends ModelElement> elementClass = me.getClass();
        int layer = metaModel.layerFor(elementClass);
        LayerContainer lc = doc.getLayer(layer);
        showInterLayerConnections = lc.isShowInterLayerConnections();
    }

    /**
     * Gibt wieder, ob die Konfigurationen angezeigt werden sollen, oder nicht.
     */
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
    }

    @Override
    protected void paintComponent(final Graphics g) {
        if (showInterLayerConnections) {
            ConfigurationRenderer.render(g, this);
        }
        super.paintComponent(g);
    }

    /**
     * @return the color of interlayer connections of this container are painted
     *         in the graph.
     */
    public Color getInterLayerConnectionColor() {
        return interLayerConnectionColor;
    }

    /**
     * Set the color of interlayer connections of this container are painted in
     * the graph.
     *
     * @param interLayerConnectionColor
     */
    public void setInterLayerConnectionColor(final Color interLayerConnectionColor) {
        this.interLayerConnectionColor = interLayerConnectionColor;
    }

}
