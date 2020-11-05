package de.imise.tool3lgm.gui.internalframe;

import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.graph.GraphViewParameter;
import de.imise.tool3lgm.graphtools.view.graph.InputGraphArea;
import de.imise.tool3lgm.gui.viewpane.graph.GraphViewPane;
import de.imise.tool3lgm.gui.viewpane.graph.GraphViewPaneFrameComponent;

/**
 * Erzeugt InternalFrame für 3lgm mit bestimmter Größe und und Lage. Es werden
 * freie Stellen gesucht und eingfügt.
 */
public final class GraphViewInternalFrame extends ToolInternalFrame implements GraphViewPaneFrameComponent {

    private final InputGraphArea area;

    /**
     * @param doc
     */
    public GraphViewInternalFrame(final GraphDocument doc) {
        super(new GraphViewPane(doc));
        GraphViewPane graphViewPane = getViewPane();
        area = graphViewPane.getInputGraphArea();
        setClosable(true);
        updateTitle();
    }

    @Override
    public GraphViewPane getViewPane() {
        return (GraphViewPane) viewPane;
    }

    @Override
    public InputGraphArea getInputGraphArea() {
        return area;
    }

    @Override
    public void dispose() {
        super.dispose();
        area.dispose();
    }

    public GraphViewParameter getGraphViewParameter() {
        return area.getGraphViewParameter();
    }

}
