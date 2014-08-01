package de.imise.tool3lgm.graphtools.matrixview;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

/**
 * @author Thomas Rudert
 */
public class CellPanel extends JPanel {

	/** Das Model nach dem die Zellen sich aufbauen */
	private TableModel tableModel;
	
	/** Panel mit Spaltenüberschriften */
	private ColPanel colHeaderPanel;
	
	/** Panel mit Zeilenüberschriften */
	private RowPanel rowHeaderPanel;
	
	/** Parameter gesetzt ? */
	private boolean initialized = false;

	/**
	 * Konstruktor
	 * @param tableModel
	 * 			Model nach dem die Zellen sich aufbauen
	 * @param colHeaderPanel 
	 * 			Panel mit Spaltenüberschriften
	 * @param rowHeaderPanel 
	 * 			Panel mit Zeilenüberschriften
	 */
	public CellPanel(TableModel tableModel, ColPanel colHeaderPanel, RowPanel rowHeaderPanel){
		super();
		
		this.colHeaderPanel = colHeaderPanel;
		this.rowHeaderPanel = rowHeaderPanel;
		this.tableModel = tableModel;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	public void paintComponent(Graphics g) {		
		super.paintComponent(g);

		if (!initialized)
			setParameter(g);
			
		int width = colHeaderPanel.getColWidth();
		int height = rowHeaderPanel.getRowHeight();
		int j = rowHeaderPanel.getNumberOfRows();
		int i = colHeaderPanel.getNumberOfCols();

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, colHeaderPanel.getNumberOfCols()*width, j*height);
		g.setColor(Color.BLACK);
		
		for(; i>=0; i--)
			g.drawLine(i*width, 0, i*width, j*height);
		
		i = colHeaderPanel.getNumberOfCols();
		
		for(; j>=0; j--)
			g.drawLine(0, j*height, i*width, j*height);

		/* linke obere Ecke und Kantenlänge des Würfels zum Markieren der Zellen */
		int x, y, a = (width + height)/4;

		int dx = (width-a)/2;
		int dy = (height-a)/2;
		
		for (TableCell cell : tableModel) {
			x = cell.getColIndex()*width + dx;
			y = cell.getRowIndex()*height + dy;
			g.setColor(cell.getColor());
			g.fillRect(x, y, a, a);
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#revalidate()
	 */
	@Override
	public void revalidate() {
		initialized = false;
		super.revalidate();
	}
	/**
	 * bestimmt die benötigten Parameter und legt die Größe der Component fest
	 * @param g Graphics
	 */
	private void setParameter(Graphics g) {
		Dimension dim = new Dimension(colHeaderPanel.getWidth(), rowHeaderPanel.getHeight());
		
		setSize(dim);
		setPreferredSize(dim);
		
		initialized = true;
	}
	
}
