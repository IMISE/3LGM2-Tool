package de.imise.tool3lgm.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JToggleButton;

import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.node.Aufgabe;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;

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
	 * @param gd
	 */
	public ToolButton(Knoten k, GraphDocument gd) {
		super();
		knot = new NodeContainer(k, gd);
		knot.setSizeForButtons(18, 14);
	}

	/**
	 * @param k
	 * @param gd
	 */
	public ToolButton(Kante k, GraphDocument gd) {
		super();
		knot = new NodeContainer(new Aufgabe(), gd);
		knot.setSizeForButtons(18, 2);
		knot.setForm(GraphElementLayout.SHAPE.rechteck);
		knot.setColor(Color.BLACK);
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		GraphDocument gd = Tool3lgm.tool.getSelectedDoc();
		if (gd == null)
			return;
		knot.setLocation((getWidth() / 2), (getHeight() / 2));
		knot.paint(g);
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(30, 30);
	}
}
