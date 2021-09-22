package de.imise.util;

/**
 * @author AXS (19.03.2021)
 */
public interface NameAndDescriptionTarget extends NameAndDescriptionSource {

    /**
     * Sets the name
     *
     * @param name
     */
    public void setName(String name);

    /**
     * Sets the description
     *
     * @param description
     */
    public void setDescription(String description);

    /**
     * Removes all non printable characters from the string s
     *
     * @param s
     * @return
     */
    public default String getCleanString(final String s) {
        //Table https://unicode-table.com/en/#basic-latin
        //removes all control characters
        String clean = s.replaceAll("[\\x00-\\x1f]|[\\x7f-\\xa0]", "");
        return clean;
    }

}
