package de.imise.tool3lgm.graphtools.matrixview;

import java.awt.Color;

/**
 * @author Thomas Rudert
 */
public class TableCell {

	/**
	 * COMMENTME
	 */
	private int rowIndex;

	/**
	 * COMMENTME
	 */
	private int colIndex;

	/**
	 * COMMENTME
	 */
	private Color color;

	/**
	 * @param _rowIndex
	 * @param _colIndex
	 */
	public TableCell(int _rowIndex, int _colIndex) {
		this(_rowIndex, _colIndex, Color.BLUE);
	}

	/**
	 * @param _rowIndex
	 * @param _colIndex
	 * @param _color
	 */
	public TableCell(int _rowIndex, int _colIndex, Color _color) {
		rowIndex = _rowIndex;
		colIndex = _colIndex;
		color = _color;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return rowIndex * 13 + colIndex * 17;
	}

	/**
	 * <code>true</code>, if both are from class TableCell, other is not null and both have
	 * same rowIndex and colIndex.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		if (other instanceof TableCell)
			return ((rowIndex == ((TableCell) other).getRowIndex()) && (colIndex == ((TableCell) other).getColIndex()));
		return false;
	}

	/**
	 * @return
	 */
	public int getRowIndex() {
		return rowIndex;
	}

	/**
	 * @return
	 */
	public int getColIndex() {
		return colIndex;
	}

	/**
	 * @return
	 */
	public Color getColor() {
		return color;
	}
}
