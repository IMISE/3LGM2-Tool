package de.imise.tool3lgm.graphtools.matrixview;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.MetaPath;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.gui.AbstractInternalFrame;

/**
 * Klasse zur Darstellung von Verbindungen zwischen Objekten in einer Tabelle
 * @author Thomas Rudert, AXS
 */
public final class TableInternalFrame extends AbstractInternalFrame implements MouseMotionListener, MouseListener {

	/**
	 * Panel für die Zeilenbeschriftungen (Elementnamen)
	 */
	private RowPanel rowHeaderPanel;

	/**
	 * Panel für die Spaltenbeschriftungen (Elementnamen)
	 */
	private ColPanel colHeaderPanel;

	/**
	 * Panel für die Darstellung der Verbindungen
	 */
	private CellPanel cellPanel;

	/**
	 * Panel, das angezeigt wird, solange kein korrekter MetaPfad ausgewählt ist.
	 */
	private JPanel msgPanel;

	/**
	 * Das Model nach dem die Tabelle aufgebaut wird
	 */
	private TableModel tableModel;

	/**
	 * @param graphDocument
	 */
	public TableInternalFrame(LGMGraphDocument graphDocument) {
		super(graphDocument, "");
		setClosable(true);
		tableModel = new TableModel(getGraphDocument());

		setToolBar(new TableToolBar(this));

		msgPanel = new JPanel();

		rowHeaderPanel = new RowPanel(tableModel.getRowHeaders());
		colHeaderPanel = new ColPanel(tableModel.getColHeaders());
		cellPanel = new CellPanel(tableModel, colHeaderPanel, rowHeaderPanel);
		cellPanel.addMouseMotionListener(this);
		cellPanel.addMouseListener(this);
		setComponents();
	}

	/**
	 * Füllt das TableModel
	 * @param rowClass
	 * 			Zeilenelementklasse
	 * @param colClass
	 * 			Spaltenelementklasse
	 * @param metaPath
	 * 			Metapfad über den Elemente der Zeilen und Splaten miteinander
	 * 			verbunden sein können
	 * @param showPartsOnly
	 * 			legt fest, ob nur absolute Teilelemente angezeigt werden sollen
	 * 			(absolut heiß, dass sie im Gesamtmodell keine Teile haben dürfen)
	 */
	public void update(Class<? extends ModelElement> rowClass, Class<? extends ModelElement> colClass, MetaPath metaPath, boolean showPartsOnly){
		tableModel.fillTableModel(rowClass, colClass, metaPath, showPartsOnly);
		rowHeaderPanel.setRows(tableModel.getRowHeaders());
		colHeaderPanel.setCols(tableModel.getColHeaders());
		cellPanel.revalidate();
		setComponents();
	}
	
	/**
	 * 
	 */
	private void setComponents() {
		if (tableModel.isValid() && tableModel.getColHeaders().size()>0 && tableModel.getRowHeaders().size()>0) {
			scrollPane.setVisible(false);
			scrollPane.setViewportView(cellPanel);
			scrollPane.setRowHeaderView(rowHeaderPanel);
			scrollPane.setColumnHeaderView(colHeaderPanel);
			scrollPane.setVisible(true);
			revalidate();
		} else {
			msgPanel.removeAll();
			StringBuilder sb = new StringBuilder();
			sb.append(Tool3lgmConstants.getResString("empty_matrix_message"));
			msgPanel.add(new JLabel(sb.toString()));
			scrollPane.setVisible(false);
			scrollPane.setRowHeaderView(null);
			scrollPane.setColumnHeaderView(null);
			scrollPane.setViewportView(msgPanel);
			scrollPane.setVisible(true);
			revalidate();
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	@Override
	public final void mouseDragged(MouseEvent e) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public final void mouseMoved(MouseEvent e) {
		((TableToolBar) getToolBar()).positionChanged(colHeaderPanel.getCol(e.getX()), rowHeaderPanel.getRow(e.getY()));
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public final void mouseClicked(MouseEvent e) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public final void mousePressed(MouseEvent e) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public final void mouseReleased(MouseEvent e) {
		if (tableModel.getMetaPath() == null)
			return;

		/* nur direkte Verbindungen */
		for (int i = 0; i < tableModel.getMetaPath().countPathes(); i++)
			if (tableModel.getMetaPath() == null || !tableModel.getMetaPath().isImmediate(i))
				return;

		boolean left_button, right_button;
		if (e.isPopupTrigger()) {
			right_button = true;
			left_button = false;
		} else {
			right_button = false;
			left_button = true;
		}

		Knoten rknot = tableModel.getRowKnot(rowHeaderPanel.getRowIndex(e.getY()));
		Knoten cknot = tableModel.getColKnot(colHeaderPanel.getColIndex(e.getX()));

		if (rknot == null || cknot == null)
			return;

		GraphDocument mainDoc = doc.getCollection().getMainGraphDocument();

		mainDoc.select(rknot.getContainer(mainDoc), 0);
		Tool3lgm.getContextGenerator().setModelElement(cknot.getContainer(mainDoc));
		Tool3lgm.getContextGenerator().setElementGetroffen(true);
		Tool3lgm.getContextGenerator().processMouseEvent(left_button, right_button, cellPanel, e.getX(), e.getY());
		Tool3lgm.getContextGenerator().setElementGetroffen(false);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public final void mouseEntered(MouseEvent arg0) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public final void mouseExited(MouseEvent arg0) {
		((TableToolBar) getToolBar()).positionChanged(null, null);
	}

	//	Methoden des Interfaces GraphDocumentListener --- Anfang ---

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.GraphDocumentListener#dataChanged(tool3lgm.graphtools.GraphDocument)
	 */
	@Override
	public final void dataChanged(GraphDocument source) {
		//Model des Tables aktualisieren
		tableModel.update();
		//Zeilen und Spalten neu aufbauen
		rowHeaderPanel.setRows(tableModel.getRowHeaders());
		colHeaderPanel.setCols(tableModel.getColHeaders());
		//das CellPanel zum resizen veranlassen und neu zeichen
		cellPanel.revalidate();
		cellPanel.repaint();
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.GraphDocumentListener#elementGraphicsChanged(tool3lgm.graphtools.GraphDocument, tool3lgm.graphtools.view.container.ElementContainer)
	 */
	@Override
	public final void elementGraphicsChanged(GraphDocument source, ElementContainer element) {
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.GraphDocumentListener#layoutChanged(tool3lgm.graphtools.GraphDocument)
	 */
	@Override
	public final void layoutChanged(GraphDocument source) {
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.GraphDocumentListener#elementAdded(tool3lgm.graphtools.GraphDocument, tool3lgm.graphtools.view.container.ElementContainer)
	 */
	@Override
	public final void elementAdded(GraphDocument source, ElementContainer element) {
		dataChanged(source);
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.GraphDocumentListener#elementDeleted(tool3lgm.graphtools.GraphDocument, tool3lgm.graphtools.view.container.ElementContainer)
	 */
	@Override
	public final void elementDeleted(GraphDocument source, ElementContainer element) {
		dataChanged(source);
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.GraphDocumentListener#groupOrderChanged(tool3lgm.graphtools.GraphDocument)
	 */
	@Override
	public final void groupOrderChanged(GraphDocument source) {
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.GraphDocumentListener#activeLayerChanged(tool3lgm.graphtools.GraphDocument)
	 */
	@Override
	public final void activeLayerChanged(GraphDocument source) {
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.GraphDocumentListener#colorsChanged(tool3lgm.graphtools.GraphDocument)
	 */
	@Override
	public final void colorsChanged(GraphDocument source) {
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.GraphDocumentListener#selectionChanged(tool3lgm.graphtools.GraphDocument)
	 */
	@Override
	public final void selectionChanged(GraphDocument source) {
	}


}
