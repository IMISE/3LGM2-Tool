package de.imise.util;

import java.util.regex.Pattern;

/**
 * @author AXS (19.03.2021)
 */
public interface NameAndDescriptionTarget extends NameAndDescriptionSource {

    /**
     * This pattern includes all control characters except Tabs (x09), Line feet
     * (x0a) and Carriage Return (x0d) <br>
     * Reference: https://unicode-table.com/en/#basic-latin
     */
    public static final Pattern CLEAN_STRING_FROM_CONTROL_CHARACTERS = Pattern.compile("[\\x00-\\x08]|[\\x0b-\\x0c]|[\\x0e-\\x1f]|[\\x7f-\\xa0]");

    /**
     * Sets the name s
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
        //removes all control characters except tab and enter
        String clean = CLEAN_STRING_FROM_CONTROL_CHARACTERS.matcher(s).replaceAll("");
        return clean;
    }

}
