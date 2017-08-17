package de.imise.tool3lgm.graphtools.view.container;

import java.awt.Color;
import java.awt.Graphics;

import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Anwendungsbaustein;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Aufgabe;

/**
 * Von {@link NodeContainer} abgeleitete Klasse, die den Container für {@link Aufgabe}n
 * und {@link Anwendungsbaustein}e darstellt.
 * <p>
 * Im Unterschied zur Oberklasse kann hier das Anzeigen der Interebenenbeziehungen gesteurt werden.
 *
 * @author fstephan
 */
public final class InterLayerConnectedNodeContainer extends NodeContainer {

    /** Gibt wieder, ob die Interebenenbeziehungen angezeigt werden sollen, oder nicht. */
    private boolean showInterLayerConnections = false;

    /** Gibt wieder, ob die Interebenenbeziehungen gehighlighted werden sollen, oder nicht. */
    private boolean highlightInterLayerConnections = false;

    private Color interLayerConnectionColor = null;

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
    }

    public boolean isHighlightInterLayerConnections() {
        return highlightInterLayerConnections;
    }

    public void setHighlightInterLayerConnections(final boolean highlightInterLayerConnections) {
        this.highlightInterLayerConnections = highlightInterLayerConnections;
    }

    @Override
    protected void paintComponent(final Graphics g) {
        if (showInterLayerConnections) {
            ConfigurationRenderer.render(g, this, doc);
        }
        super.paintComponent(g);
    }

    public Color getInterLayerConnectionColor() {
        return interLayerConnectionColor;
    }

    public void setInterLayerConnectionColor(final Color interLayerConnectionColor) {
        this.interLayerConnectionColor = interLayerConnectionColor;
    }

}
