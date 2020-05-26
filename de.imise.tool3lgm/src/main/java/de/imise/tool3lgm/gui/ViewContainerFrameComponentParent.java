package de.imise.tool3lgm.gui;

import java.util.List;

import de.imise.tool3lgm.graphtools.model.GraphDocument;

/**
 * Interface of the component that contains all {@link GraphViewContainer}.
 *
 * @author AXS (16.05.2020)
 */
public interface ViewContainerFrameComponentParent {

    /**
     * Returns a list of all {@link ViewContainer} for the given
     * {@link GraphDocument}.
     *
     * @param doc
     * @return the activated graph view for the {@link GraphDocument}
     */
    public List<ViewContainerFrameComponent> getViewContainerFrameComponents(GraphDocument doc);

    /**
     * Activates or creates and activates the the graph view for a given
     * {@link GraphDocument}.
     *
     * @param doc
     * @return the activated graph view for the {@link GraphDocument}
     */
    public GraphViewContainer getGraphViewContainer(GraphDocument doc);

    /**
     * @param viewContainerFrameComponent
     * @return <code>true</code> if this component is the selected or active component
     */
    public boolean isSelected(ViewContainerFrameComponent viewContainerFrameComponent);

    /**
     * Sets component with the given {@link ViewContainerFrameComponent} as the selected or active
     * component.
     *
     * @param viewContainerFrameComponent
     */
    public void setSelected(ViewContainerFrameComponent viewContainerFrameComponent);

}
