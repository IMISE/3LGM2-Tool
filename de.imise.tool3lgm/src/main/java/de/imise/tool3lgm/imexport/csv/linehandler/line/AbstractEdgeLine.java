package de.imise.tool3lgm.imexport.csv.linehandler.line;

import java.util.List;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;

public abstract class AbstractEdgeLine extends AbstractImportLine {

    /** Hash of the element where this edge starts */
    protected String startHash;

    /** Hash of the element where this edge ends */
    protected String endHash;

    /**
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
     * @param startHash
     *            Hash of the element where this edge starts
     * @param endHash
     *            Hash of the element where this edge ends
     * @param userFields
     *            List of all values for userFields in this line
     * @param row
     *            Row number of this line
     */
    protected AbstractEdgeLine(final Class<? extends ModelElement> elementClass, final String elementType, final String name, final String description, final String hash, final String startHash, final String endHash, final List<String> userFields,
            final int row) {
        super(elementClass, elementType, name, description, hash, userFields, row);
        this.startHash = startHash;
        this.endHash = endHash;
    }

    //    /**
    //     * @param startHash the startHash to set
    //     */
    //    public void setStartHash(final String startHash) {
    //        this.startHash = startHash;
    //    }

    /**
     * @return the startHash
     */
    public String getStartHash() {
        return startHash;
    }

    /**
     * @return <code>true</code> if the value in the start hash column is not <code>null</code>
     */
    public boolean hasStartHashColumn() {
        return startHash != null;
    }

    //    /**
    //     * @param endHash the endHash to set
    //     */
    //    public void setEndHash(final String endHash) {
    //        this.endHash = endHash;
    //    }

    /**
     * @return the endHash
     */
    public String getEndHash() {
        return endHash;
    }

    /**
     * @return <code>true</code> if the value in the end hash column is not <code>null</code>
     */
    public boolean hasEndHashColumn() {
        return endHash != null;
    }

}
