package de.imise.tool3lgm.graphtools.newmatrixview;

import java.awt.Color;

/**
 * @author Thomas Rudert
 */
public class TableCell {

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
    public TableCell(final int _rowIndex, final int _colIndex, final Color _color) {
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
        if (other instanceof TableCell) {
            return rowIndex == ((TableCell) other).getRowIndex() && colIndex == ((TableCell) other).getColIndex();
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
