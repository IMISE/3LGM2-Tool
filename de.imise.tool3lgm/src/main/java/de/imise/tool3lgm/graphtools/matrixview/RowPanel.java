package de.imise.tool3lgm.graphtools.matrixview;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelElement;

/**
 * Klasse zum Zeichnen der Zeilenköpfe für die Analysetabelle
 * 
 * @author Thomas Rudert
 */
public class RowPanel extends JPanel {

	/** ArrayList mit Strings der Namen der Zeilenelement */
	private ArrayList<? extends ModelElement> rows;

	/** Integer der die Zeilenhöhe definiert */
	private int delta = -1;

	/** Integer der die maximal Breite der Zeilenköpfe definiert */
	private int max_length = 0;

	/** Anzahl der Zeilen */
	private int numberOfRows;

	/**
	 * <code>true</code>, wenn das Panel bereits initialisiert wurde, sonst
	 * <code>false</code>.
	 */
	private boolean initialized = false;

	/**
	 * Konstruktor
	 * 
	 * @param _rows
	 *            ArrayList mit Strings der Zeilenüberschriften
	 */
	public RowPanel(ArrayList<ModelElement> _rows) {
		super();
		rows = _rows;
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (!initialized)
			setRequiredParam(g);
		int y_pos = delta - 6;
		g.drawLine(max_length, 0, max_length, getRowHeight() * getNumberOfRows());
		g.drawLine(0, 0, max_length, 0);
		for (ModelElement me : rows) {
			g.drawString(me.toString().replace('\n', ' '), 2, y_pos);
			g.drawLine(0, y_pos + 6, max_length, y_pos + 6);
			y_pos += delta;
		}
	}

	/**
	 * Bestimmt die benötigten Parameter delta und max_length und legt die Größe
	 * der Component fest
	 * 
	 * @param g
	 *            Graphics
	 */
	private void setRequiredParam(Graphics g) {
		/* Zeilenhöhe */
		delta = g.getFontMetrics().getHeight() + 5;

		/* Spaltenbreite */
		String temp_string;
		for (ModelElement me : rows) {
			temp_string = me.toString().replace('\n', ' ');
			max_length = max_length < g.getFontMetrics().stringWidth(temp_string) ? g.getFontMetrics().stringWidth(temp_string) + 4 : max_length;
		}
		max_length = Math.min(300, max_length);

		/* Component-Größe festlegen */
		Dimension dim = new Dimension(max_length + 1, delta * rows.size() + 1);
		this.setSize(dim);
		this.setPreferredSize(dim);

		numberOfRows = rows.size();

		initialized = true;
	}

	/**
	 * Gibt Anzahl der Zeilen zurück
	 * 
	 * @return int Anzahl der Zeilen
	 */
	public int getNumberOfRows() {
		return numberOfRows;
	}

	/**
	 * Gibt Zeilenhöhe in Pixeln zurück
	 * 
	 * @return int Zeilenhöhe in Pixeln
	 */
	public int getRowHeight() {
		return delta;
	}

	/**
	 * Method getRow return String of header of row at position
	 * 
	 * @param i
	 *            int-position in pixels
	 * @return String header of column
	 */
	public Knoten getRow(int i) {
		i = getRowIndex(i);
		if (i < rows.size() && i >= 0)
			return (Knoten) rows.get(i);

		return null;
	}

	/**
	 * @param rows
	 */
	public void setRows(ArrayList<? extends ModelElement> rows){
		this.rows = rows;
		initialized = false;
	}
	
	/**
	 * Method getRowIndex return index of row at position
	 * 
	 * @param i
	 *            int-position in pixels
	 * @return int index of row
	 */
	public int getRowIndex(int i) {
		if (delta != 0)
			return (i - 1) / delta;
		return -1;
	}

}
