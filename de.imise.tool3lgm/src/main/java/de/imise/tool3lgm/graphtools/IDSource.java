package de.imise.tool3lgm.graphtools;

import java.util.Objects;

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

    /**
     * @param id
     * @return <code>true</code> id is equals to {@link #getID()}
     * @see Objects#equals(Object, Object)
     */
    public default boolean hasID(final String id) {
        return Objects.equals(id, getID());
    }

}
