package de.imise.tool3lgm.gui;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.Szenario;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.graph.InputGraphArea;

/**
 * Erzeugt InternalFrame für 3lgm mit bestimmter Größe und und Lage. Es werden freie Stellen gesucht und eingfügt.
 */
public class ToolInternalFrame extends AbstractInternalFrame implements ActionListener{
	
	/** Anzahl der jemals geöffneten Teilmodelle */
	static int docCount = 0;

	/**
	 * COMMENTME
	 */
	private InputGraphArea area;
	
	/**
	 * COMMENTME
	 */
	private JButton but;

	/**
	 * @param pane
	 * @param inputGraphArea
	 * @param doc
	 */
	public ToolInternalFrame(JDesktopPane pane, InputGraphArea inputGraphArea, LGMGraphDocument doc) {
		super(doc, "");

		// Diese Reihenfolge ist wichtig! Im Konstruktor von Werkzeugleiste
		// braucht man das Area, um die Regler bei neuen Modellen auf den dann
		// dargestellten Wert zu setzen
		doc.setFrame(this);
		area = inputGraphArea;
		setToolBar(new Werkzeugleiste(this));

		if (!(doc instanceof Szenario))
			docCount++;

		updateTitle();

		setClosable(false);

		JScrollPane sp = getScrollPane();
		sp.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
//		sp.getViewport().setScrollMode(JViewport.BLIT_SCROLL_MODE );
//		sp.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
		sp.setViewportView(inputGraphArea);
		sp.getHorizontalScrollBar().setUnitIncrement(10);
		sp.getVerticalScrollBar().setUnitIncrement(10);
		but = new JButton(Tool3lgmConstants.getIcon("zent.gif"));
		but.setActionCommand("z");
		getScrollPane().setCorner(JScrollPane.LOWER_RIGHT_CORNER, but);
		but.addActionListener(this);
		
	}

	/**
	 * @return
	 */
	public InputGraphArea getInputGraphArea() {
		return area;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 * implementiert ActionListener zum selbständigen zentrieren der Frames 
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String str = e.getActionCommand();
		if (str == "z") {
			center();
		}
	}

	/**
	 * 
	 */
	public void center() {
		JViewport view = getScrollPane().getViewport();
		Rectangle vp = view.getViewRect();
		int x = (int) (area.getWidth() - vp.getWidth()) / 2;
		int y = (int) (area.getHeight() - vp.getHeight()) / 2;
		view.setViewPosition(new Point(x, y));
	}

	/* (non-Javadoc)
	 * @see tool3lgm.gui.AbstractInternalFrame#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		area.dispose();
	}

	/**
	 * Setzt den Titel des Frames
	 */
	public void updateTitle() {
		super.setTitle(getCollection().getName() + " - " + getGraphDocument().getTitle());
	}

	/* (non-Javadoc)
	 * @see tool3lgm.gui.AbstractInternalFrame#dataChanged(tool3lgm.graphtools.GraphDocument)
	 */
	@Override
	public void dataChanged(GraphDocument source) {
		if (Tool3lgm.DEBUG)
			System.err.println(getClass().getSimpleName() + "dataChanged() " + source);
		area.dataChanged();
	}

	/* (non-Javadoc)
	 * @see tool3lgm.gui.AbstractInternalFrame#elementGraphicsChanged(tool3lgm.graphtools.GraphDocument, tool3lgm.graphtools.view.container.ElementContainer)
	 */
	@Override
	public void elementGraphicsChanged(GraphDocument source, ElementContainer element) {
		area.elementGraphicsChanged(element);
	}

	/* (non-Javadoc)
	 * @see tool3lgm.gui.AbstractInternalFrame#layoutChanged(tool3lgm.graphtools.GraphDocument)
	 */
	@Override
	public void layoutChanged(GraphDocument source) {
		area.layoutChanged();
	}

	/* (non-Javadoc)
	 * @see tool3lgm.gui.AbstractInternalFrame#elementAdded(tool3lgm.graphtools.GraphDocument, tool3lgm.graphtools.view.container.ElementContainer)
	 */
	@Override
	public void elementAdded(GraphDocument source, ElementContainer element) {
		area.elementAdded(element);
	}

	/* (non-Javadoc)
	 * @see tool3lgm.gui.AbstractInternalFrame#elementDeleted(tool3lgm.graphtools.GraphDocument, tool3lgm.graphtools.view.container.ElementContainer)
	 */
	@Override
	public void elementDeleted(GraphDocument source, ElementContainer element) {
		area.elementDeleted();
	}

	/* (non-Javadoc)
	 * @see tool3lgm.gui.AbstractInternalFrame#groupOrderChanged(tool3lgm.graphtools.GraphDocument)
	 */
	@Override
	public void groupOrderChanged(GraphDocument source) {
		area.groupOrderChanged();
	}

	/* (non-Javadoc)
	 * @see tool3lgm.gui.AbstractInternalFrame#activeLayerChanged(tool3lgm.graphtools.GraphDocument)
	 */
	@Override
	public void activeLayerChanged(GraphDocument source) {
		area.activeLayerChanged();
	}

	/* (non-Javadoc)
	 * @see tool3lgm.gui.AbstractInternalFrame#colorsChanged(tool3lgm.graphtools.GraphDocument)
	 */
	@Override
	public void colorsChanged(GraphDocument source) {
		area.repaint();
	}

	/* (non-Javadoc)
	 * @see tool3lgm.gui.AbstractInternalFrame#selectionChanged(tool3lgm.graphtools.GraphDocument)
	 */
	@Override
	public void selectionChanged(GraphDocument source) {
		area.repaint();
	}

	/**
	 * @return
	 */
	public LGMGraphDocument getSzenario() {
		return getGraphDocument();
	}

}
