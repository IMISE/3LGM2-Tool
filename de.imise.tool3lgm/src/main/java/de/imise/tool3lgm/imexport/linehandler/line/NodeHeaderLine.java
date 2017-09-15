package de.imise.tool3lgm.imexport.linehandler.line;

import java.util.List;

import de.imise.tool3lgm.graphtools.metamodel.ModelElement;

/**
 * Represents a header line for nodes
 * 
 * @author AXS
 * @create 06.10.2014
 */
public final class NodeHeaderLine extends AbstractImportLine {

    /**
     * @param elementClass
     *            The element class of this header line
     * @param elementType
     *            Value of the element type column in this line. Only in header lines this should be a non empty value and a string representation of
     *            the element class
     * @param nameHeader
     *            Name of the name column in this header line
     * @param descriptionHeader
     *            Name of the description column in this header line
     * @param hashHeader
     *            Name of the hash column in this header line
     * @param userFieldNames
     *            List of all names for userFields in this line
     * @param row
     *            Row number of this line
     */
    public NodeHeaderLine(final Class<? extends ModelElement> elementClass, final String elementType, final String nameHeader, final String descriptionHeader, final String hashHeader, final List<String> userFieldNames, final int row) {
        super(elementClass, elementType, nameHeader, descriptionHeader, hashHeader, userFieldNames, row);
    }

}
