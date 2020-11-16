package de.imise.util;

/**
 * Interface for boolean values
 *
 * @author Ich (16.11.2020)
 */
public interface BooleanOption {

    /**
     * Sets the value for this property to the value
     *
     * @param value the new value
     * @return the old value
     */
    public boolean set(final boolean value);

    /**
     * @return <code>true</code>, if this option is set to <code>true</code>
     */
    public boolean is();

    /**
     * @return <code>true</code>, if this option is set to <code>false</code>
     */
    public default boolean isNot() {
        return !is();
    }

}
