package de.imise.tool3lgm.gui.viewpane.matrix;

import java.awt.Color;

/**
 * @author Thomas Rudert
 */
public class MatrixViewTableCell {

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
     * @param _color
     */
    public MatrixViewTableCell(final int _rowIndex, final int _colIndex, final Color _color) {
        rowIndex = _rowIndex;
        colIndex = _colIndex;
        color = _color;
    }

    @Override
    public int hashCode() {
        return rowIndex * 13 + colIndex * 17;
    }

    /**
     * true, if both are from class TableCell, other is not null and both have
     * same rowIndex and colIndex
     */
    @Override
    public boolean equals(final Object other) {
        if (other instanceof MatrixViewTableCell) {
            return rowIndex == ((MatrixViewTableCell) other).getRowIndex() && colIndex == ((MatrixViewTableCell) other).getColIndex();
        } else {
            return false;
        }
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
