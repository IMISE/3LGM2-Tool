package de.imise.tool3lgm.graphtools.matrixview;

import java.awt.Color;

/**
 * @author Thomas Rudert
 */
public class TableCellOld {

    /**
     * COMMENTME
     */
    private final int rowIndex;

    /**
     * COMMENTME
     */
    private final int colIndex;

    /**
     * COMMENTME
     */
    private final Color color;

    /**
     * @param _rowIndex
     * @param _colIndex
     */
    public TableCellOld(final int _rowIndex, final int _colIndex) {
        this(_rowIndex, _colIndex, Color.BLUE);
    }

    /**
     * @param _rowIndex
     * @param _colIndex
     * @param _color
     */
    public TableCellOld(final int _rowIndex, final int _colIndex, final Color _color) {
        rowIndex = _rowIndex;
        colIndex = _colIndex;
        color = _color;
    }

    @Override
    public int hashCode() {
        return rowIndex * 13 + colIndex * 17;
    }

    /**
     * <code>true</code>, if both are from class TableCell, other is not null and both have same rowIndex and colIndex.
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object other) {
        if (other instanceof TableCellOld) {
            return rowIndex == ((TableCellOld) other).getRowIndex() && colIndex == ((TableCellOld) other).getColIndex();
        }
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
