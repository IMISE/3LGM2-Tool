package de.imise.tool3lgm.graphtools;

import de.imise.util.IDStringGenerator;

/**
 * @author AXS (19.11.2020)
 */
public interface IDSource {

    /**
     * @return the ID-String of this object
     */
    public String getID();

    /**
     * @return
     */
    public default String createID() {
        return createID("ID");
    }

    /**
     * @param idPrefix
     * @return
     */
    public default String createID(final String idPrefix) {
        return IDStringGenerator.createIDString(idPrefix);
    }

}
