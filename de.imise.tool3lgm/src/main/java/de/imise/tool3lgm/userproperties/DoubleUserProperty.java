package de.imise.tool3lgm.userproperties;

public interface DoubleUserProperty {

    public default double getDefault() {
        return 0d;
    }

}
