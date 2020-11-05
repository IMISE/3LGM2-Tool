package de.imise.tool3lgm.imexport.csv.linehandler.line;

import java.util.List;

/**
 * Represents a line that defines an edge.
 *
 * @author AXS
 * @create 06.10.2014
 */
public class EdgeLine extends AbstractEdgeLine {

    /**
     * Header line of this edge line
     */
    final EdgeHeaderLine headerLine;

    /**
     * @param headerLine Header line of this edge line
     * @param elementClass The element type this line is defined for
     * @param elementType Value of the element type column in this line
     * @param name Value of the name column in this line
     * @param description Value of the description column in this line
     * @param hash Value of the hash column in this line
     * @param startHash Hash of the element where this edge starts
     * @param endHash Hash of the element where this edge ends
     * @param userFields List of all values for userFields in this line
     * @param row Row number of this line
     */
    public EdgeLine(final EdgeHeaderLine headerLine, final String name, final String description, final String hash, final String startHash, final String endHash, final List<String> userFields, final int row) {
        super(headerLine == null ? null : headerLine.getElementClass(), "", name, description, hash, startHash, endHash, userFields, row);
        this.headerLine = headerLine;
    }

    /**
     * @return header line of this edge line
     */
    public EdgeHeaderLine getHeaderLine() {
        return headerLine;
    }

}
