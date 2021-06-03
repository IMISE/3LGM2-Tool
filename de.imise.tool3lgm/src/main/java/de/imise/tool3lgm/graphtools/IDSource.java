package de.imise.tool3lgm.graphtools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import com.google.common.collect.Lists;

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

    /**
     * Creates a comparator that compares the IDs of ID sources
     */
    public static final Comparator<IDSource> ID_COMPARATOR = new Comparator<IDSource>() {
        @Override
        public int compare(final IDSource o1, final IDSource o2) {
            String id1 = o1.getID();
            String id2 = o2.getID();
            return id1.compareToIgnoreCase(id2);
        }
    };

    /**
     * @param <T>
     * @param elements
     * @return
     */
    public static <T extends IDSource> List<T> getSortedByID(final Iterable<T> elements) {
        ArrayList<T> elementsList = Lists.newArrayList(elements);
        Collections.sort(elementsList, ID_COMPARATOR);
        return elementsList;
    }

}
