package de.imise.tool3lgm.userproperties;

public interface BooleanUserProperty {

    public default boolean getDefault() {
        return false;
    }

}
