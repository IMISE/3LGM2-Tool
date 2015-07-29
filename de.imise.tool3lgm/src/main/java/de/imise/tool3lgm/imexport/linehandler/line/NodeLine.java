package de.imise.tool3lgm.imexport.linehandler.line;

import java.util.List;

/**
 * Represents a line that defines a node.
 * 
 * @author AXS
 * @create 06.10.2014
 */
public class NodeLine extends AbstractImportLine {

    /**
     * Header line of this node line
     */
    private final NodeHeaderLine headerLine;

    /**
     * @param headerLine
     *            Header line of this node line
     * @param elementClass
     *            The element type this line is defined for
     * @param elementType
     *            Value of the element type column in this line
     * @param name
     *            Value of the name column in this line
     * @param description
     *            Value of the description column in this line
     * @param hash
     *            Value of the hash column in this line
     * @param userFields
     *            List of all values for userFields in this line
     * @param row
     *            Row number of this line
     */
    public NodeLine(final NodeHeaderLine headerLine, final String name, final String description, final String hash, final List<String> userFields, final int row) {
        super(headerLine == null ? null : headerLine.getElementClass(), "", name, description, hash, userFields, row);
        this.headerLine = headerLine;
    }

    /**
     * @return header line of this node line
     */
    public NodeHeaderLine getHeaderLine() {
        return headerLine;
    }

}
