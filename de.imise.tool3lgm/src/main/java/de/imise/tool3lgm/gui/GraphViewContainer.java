package de.imise.tool3lgm.gui;

import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.graph.ViewParameter;

/**
 * @author AXS (10.05.2020)
 */
public interface GraphViewContainer {

    /**
     * @return the {@link GraphDocument} this GraphViewContainer is displaying
     */
    public GraphDocument getGraphDocument();

    /**
     * @return the {@link ViewParameter} of this graph view
     */
    public ViewParameter getViewParameter();

    /**
     * @return <code>true</code> if this component is the selected or focused component
     */
    public boolean isSelected();

    /**
     * @param selected
     */
    public void setSelected(boolean selected);

}
