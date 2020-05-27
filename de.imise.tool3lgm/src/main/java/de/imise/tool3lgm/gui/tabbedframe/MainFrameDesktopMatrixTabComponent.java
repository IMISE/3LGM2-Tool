package de.imise.tool3lgm.gui.tabbedframe;

import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.gui.viewpane.graph.GraphViewPane;
import de.imise.tool3lgm.gui.viewpane.matrix.MatrixViewPane;
import de.imise.tool3lgm.gui.viewpane.matrix.MatrixViewPaneFrameComponent;

/**
 * @author AXS (27.05.2020)
 */
public class MainFrameDesktopMatrixTabComponent extends MainFrameDesktopTabComponent implements MatrixViewPaneFrameComponent {

    /**
     * @param doc
     */
    public MainFrameDesktopMatrixTabComponent(final GraphDocument doc) {
        super(new GraphViewPane(doc));
        updateTitle();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public MatrixViewPane getViewPane() {
        return (MatrixViewPane) super.getViewPane();
    }

}
