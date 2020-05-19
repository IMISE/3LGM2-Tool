package de.imise.tool3lgm.gui;

import de.imise.tool3lgm.graphtools.model.GraphDocument;

/**
 * Interface of the component that contains all {@link GraphViewContainer}.
 *
 * @author AXS (16.05.2020)
 */
public interface GraphViewContainerParent {

    /**
     * Activates or creates and activates the the graph view for a given
     * {@link GraphDocument}.
     *
     * @param doc
     * @return the activated graph view for the {@link GraphDocument}
     */
    public GraphViewContainer getGraphViewContainer(GraphDocument doc);

    /**
     * @param graphViewContainer
     * @return <code>true</code> if this component is the selected or active component
     */
    public boolean isSelected(GraphViewContainer graphViewContainer);

    /**
     * Sets component with the given {@link GraphDocument} as the selected or active
     * component.
     *
     * @param graphViewContainer
     */
    public void setSelected(GraphViewContainer graphViewContainer);

}
