package de.imise.tool3lgm.gui;

import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.GraphDocumentOwner;
import de.imise.tool3lgm.graphtools.view.graph.InputGraphArea;
import de.imise.tool3lgm.graphtools.view.graph.ViewParameter;

/**
 * Interface for a graph view of a Szenario.
 *
 * @author AXS (10.05.2020)
 */
public interface GraphViewContainer extends GraphDocumentOwner {

    /**
     * @return the {@link GraphDocument} this GraphViewContainer is displaying
     */
    @Override
    public default GraphDocument getGraphDocument() {
        InputGraphArea inputGraphArea = getInputGraphArea();
        return inputGraphArea.getGraphDocument();
    }

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

    /**
     * @return the InputGraphArea this GraphViewContainer is displaying
     */
    public InputGraphArea getInputGraphArea();

}