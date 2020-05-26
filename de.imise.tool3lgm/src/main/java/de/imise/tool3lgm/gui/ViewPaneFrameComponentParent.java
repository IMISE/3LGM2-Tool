package de.imise.tool3lgm.gui;

import java.util.List;

import de.imise.tool3lgm.graphtools.model.GraphDocument;

/**
 * Interface of the component that contains all {@link ViewPaneFrameComponent}.
 *
 * @author AXS (16.05.2020)
 */
public interface ViewPaneFrameComponentParent {

    /**
     * Returns a list of all {@link ViewPaneFrameComponent} for the given
     * {@link GraphDocument}.
     *
     * @param doc
     * @return the activated graph view for the {@link GraphDocument}
     */
    public List<ViewPaneFrameComponent> getViewPaneFrameComponents(GraphDocument doc);

    /**
     * Activates or creates and activates the the graph view for a given
     * {@link GraphDocument}.
     *
     * @param doc
     * @return the activated graph view for the {@link GraphDocument}
     */
    public GraphViewPane getGraphViewPane(GraphDocument doc);

    /**
     * @param viewPaneFrameComponent
     * @return <code>true</code> if this component is the selected or active component
     */
    public boolean isSelected(ViewPaneFrameComponent viewPaneFrameComponent);

    /**
     * Sets component with the given {@link ViewPaneFrameComponent} as the selected or active
     * component.
     *
     * @param viewPaneFrameComponent
     */
    public void setSelected(ViewPaneFrameComponent viewPaneFrameComponent);

}
