package de.imise.tool3lgm.imexport.linehandler.line;

import java.util.List;

import de.imise.tool3lgm.graphtools.metamodel.ModelElement;

/**
 * An abstract representation of one line of an import
 * 
 * @author AXS
 * @create 06.10.2014
 */
public abstract class AbstractImportLine {

    /** The element class this line is defined for */
    private final Class<? extends ModelElement> elementClass;

    /** Value of the element type column in this line */
    private final String elementType;

    /** Value of the name column in this line */
    private final String name;

    /** Value of the description column in this line */
    private final String description;

    /** Value of the hash column in this line */
    private final String hash;

    /** List of all values for userFields in this line */
    private final List<String> userFields;

    /** Row number of this line */
    private final int row;

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
     * @param userFields
     *            List of all values for userFields in this line
     * @param row
     *            Row number of this line
     */
    protected AbstractImportLine(final Class<? extends ModelElement> elementClass, final String elementType, final String name, final String description, final String hash, final List<String> userFields, final int row) {
        this.elementClass = elementClass;
        this.elementType = elementType;
        this.name = name;
        this.description = description;
        this.hash = hash;
        this.userFields = userFields;
        this.row = row;
    }

    /**
     * @return The element class this line is defined for
     */
    public final Class<? extends ModelElement> getElementClass() {
        return elementClass;
    }

    //    /**
    //     * @param elementClass
    //     *            The element class this line is defined for
    //     */
    //    public void setElementClass(final Class<? extends ModelElement> elementClass) {
    //        this.elementClass = elementClass;
    //    }

    /**
     * @return <code>true</code> if the element class is not <code>null</code>
     */
    public boolean hasElementClass() {
        return elementClass != null;
    }

    //    /**
    //     * @param elementType
    //     *            Value of the element type in this line
    //     */
    //    public void setElementType(final String elementType) {
    //        this.elementType = elementType;
    //    }

    /**
     * @return Value of the element type in this line
     */
    public String getElementType() {
        return elementType;
    }

    /**
     * @return <code>true</code> if the value of the element type column in this line is not <code>null</code>
     */
    public boolean hasElementTypeColumn() {
        return elementType != null;
    }

    //    /**
    //     * @param name
    //     *            Value of the name column in this line
    //     */
    //    public void setName(final String name) {
    //        this.name = name;
    //    }

    /**
     * @return Value of the name column in this line
     */
    public String getName() {
        return name;
    }

    /**
     * @return <code>true</code> if the value of the name column in this line is not <code>null</code>
     */
    public boolean hasName() {
        return name != null;
    }

    //    /**
    //     * @param description
    //     *            Value of the description column in this line
    //     */
    //    public void setDescription(final String description) {
    //        this.description = description;
    //    }

    /**
     * @return Value of the description column in this line
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return <code>true</code> if the value of the description column in this line is not <code>null</code>
     */
    public boolean hasDescriptionColumn() {
        return getDescription() != null;
    }

    //    /**
    //     * @param hash
    //     *            Value of the hash column in this line
    //     */
    //    public void setHash(final String hash) {
    //        this.hash = hash;
    //    }

    /**
     * @return Value of the hash column in this line
     */
    public String getHash() {
        return hash;
    }

    /**
     * @return <code>true</code> if the value of the hash column in this line is not <code>null</code>
     */
    public boolean hasHash() {
        return hash != null;
    }

    /**
     * @return List of all values for userFields in this line
     */
    public List<String> getUserFields() {
        return userFields;
    }

    //    /**
    //     * @param userFields
    //     *            List of all values for userFields in this line
    //     */
    //    public void setUserFields(final List<String> userFields) {
    //        this.userFields = userFields;
    //    }

    //    /**
    //     * @param row
    //     *            Row number of this line
    //     */
    //    public void setRow(final int row) {
    //        this.row = row;
    //    }

    /**
     * @return Row number of this line
     */
    public int getRow() {
        return row;
    }

}
