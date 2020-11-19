package de.imise.tool3lgm.imexport.csv.linehandler.line;

import java.util.List;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;

/**
 * Represents a header line for edges
 *
 * @author AXS
 * @create 06.10.2014
 */
public class EdgeHeaderLine extends AbstractEdgeLine {

    /**
     * @param elementClass The element class of this header line
     * @param elementType Value of the element type column in this line. Only in
     *            header lines this should be a non empty value and a string
     *            representation of the element class
     * @param nameHeader Name of the name column in this header line
     * @param descriptionHeader Name of the description column in this header
     *            line
     * @param idHeader Name of the ID column in this header line
     * @param startIDHeader Name column with the start IDs for the following
     *            edge lines
     * @param endIDHeader Name column with the end IDs for the following edge
     *            lines
     * @param userFieldNames List of all names for userFields in this line
     * @param row Row number of this line
     */
    public EdgeHeaderLine(final Class<? extends ModelElement> elementClass, final String elementType, final String nameHeader, final String descriptionHeader, final String idHeader, final String startIDHeader, final String endIDHeader,
            final List<String> userFieldNames, final int row) {
        super(elementClass, elementType, nameHeader, descriptionHeader, idHeader, startIDHeader, endIDHeader, userFieldNames, row);
    }

}
