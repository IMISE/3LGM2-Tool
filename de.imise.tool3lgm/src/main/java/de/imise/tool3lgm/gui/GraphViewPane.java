package de.imise.tool3lgm.gui;

import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.graph.GraphViewParameter;
import de.imise.tool3lgm.graphtools.view.graph.InputGraphArea;

/**
 * @author AXS (19.05.2020)
 */
public final class GraphViewPane extends ViewPane implements GraphViewContainer {

    /**
     *
     */
    private final InputGraphArea area;

    /**
     *
     */
    public GraphViewPane(final GraphDocument doc) {
        super(doc);
        area = new InputGraphArea(doc);
        scrollPane.setViewportView(area);
    }

    @Override
    public GraphViewParameter getGraphViewParameter() {
        return area.getGraphViewParameter();
    }

    @Override
    public InputGraphArea getInputGraphArea() {
        return area;
    }

}
