package de.imise.tool3lgm.graphtools.view.graph;

public interface BasicGraphAreaChangeListener {

    public default void zoomChanged(final BasicGraphArea source) {
    }

    public default void degreeChanged(final BasicGraphArea source) {
    }

    public default void layerGapChanged(final BasicGraphArea source) {
    }

    public default void layerViewChanged(final BasicGraphArea source) {
    }

    public default void pageSizeChanged(final BasicGraphArea source) {
    }

}
