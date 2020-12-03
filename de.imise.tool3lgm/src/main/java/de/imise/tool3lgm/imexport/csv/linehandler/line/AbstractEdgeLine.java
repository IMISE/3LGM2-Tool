package de.imise.tool3lgm.imexport.csv.linehandler.line;

import java.util.List;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;

public abstract class AbstractEdgeLine extends AbstractImportLine {

    /** ID of the element where this edge starts */
    protected String startID;

    protected String endID;

    /**
     * @param elementClass The element type this line is defined for
     * @param elementType Value of the element type column in this line
     * @param name Value of the name column in this line
     * @param description Value of the description column in this line
     * @param id Value of the ID column in this line
     * @param startID ID of the element where this edge starts
     * @param endID ID of the element where this edge ends
     * @param userFields List of all values for userFields in this line
     * @param row Row number of this line
     */
    protected AbstractEdgeLine(final Class<? extends ModelElement> elementClass, final String elementType, final String name, final String description, final String id, final String startID, final String endID, final List<String> userFields,
            final int row) {
        super(elementClass, elementType, name, description, id, userFields, row);
        this.startID = startID;
        this.endID = endID;
    }

    //    /**
    //     * @param startID the startID to set
    //     */
    //    public void setStartID(final String startID) {
    //        this.startID = startID;
    //    }

    /**
     * @return the startID
     */
    public String getStartID() {
        return startID;
    }

    /**
     * @return <code>true</code> if the value in the start ID column is not
     *         <code>null</code>
     */
    public boolean hasStartIDColumn() {
        return startID != null;
    }

    //    /**
    //     * @param endID the endID to set
    //     */
    //    public void setEndID(final String endID) {
    //        this.endID = endID;
    //    }

    /**
     * @return the endID
     */
    public String getEndID() {
        return endID;
    }

    /**
     * @return <code>true</code> if the value in the end ID column is not
     *         <code>null</code>
     */
    public boolean hasEndIDColumn() {
        return endID != null;
    }

}
