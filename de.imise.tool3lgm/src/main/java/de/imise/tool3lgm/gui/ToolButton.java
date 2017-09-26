package de.imise.tool3lgm.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JToggleButton;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.metamodel.Edge;
import de.imise.tool3lgm.graphtools.metamodel.Node;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Aufgabe;

/**
 * @author N.N.
 * @create Very long time ago
 */
public class ToolButton extends JToggleButton {

    /**
     * COMMENTME
     */
    NodeContainer knot;

    /**
     * @param k
     * @param doc
     */
    public ToolButton(final Node k, final GraphDocument doc, final int i) {
        super();
        knot = new NodeContainer(k, doc);
        knot.setSizeForButtons(18, 14);
    }

    /**
     * @param k
     * @param doc
     */
    public ToolButton(final Edge k, final GraphDocument doc, final int i) {
        super();
        knot = new NodeContainer(new Aufgabe(), doc);
        knot.setSizeForButtons(18, 2);
        knot.setForm(GraphElementLayout.SHAPE.rechteck);
        knot.setColor(Color.BLACK);
    }

    @Override
    public void paint(final Graphics g) {
        super.paint(g);
        GraphDocument gd = Static.getSelectedDoc();
        if (gd == null) {
            return;
        }
        knot.setLocation(getWidth() / 2, getHeight() / 2);
        knot.paint(g);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(30, 30);
    }

    public void setGraphDocument(final GraphDocument doc) {

    }
}
