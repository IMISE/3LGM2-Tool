package de.imise.tool3lgm.graphtools.view.graph;

public interface BasicGraphAreaChangeListener {

    public default void zoomChanged(final BasicGraphArea source) {
        graphAreaChanged(source);
    }

    public default void degreeChanged(final BasicGraphArea source) {
        graphAreaChanged(source);
    }

    public default void layerGapChanged(final BasicGraphArea source) {
        graphAreaChanged(source);
    }

    public default void layerViewChanged(final BasicGraphArea source) {
        graphAreaChanged(source);
    }

    public default void pageSizeChanged(final BasicGraphArea source) {
        graphAreaChanged(source);
    }

    public default void graphAreaChanged(final BasicGraphArea source) {
        graphAreaChanged();
    }

    public default void graphAreaChanged() {
    }

}
