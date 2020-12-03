package de.imise.tool3lgm.gui.viewpane.matrix;

import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.path.MetaPathSelector.MetaPathSelection;
import de.imise.tool3lgm.gui.viewpane.ViewPaneFrameComponent;

/**
 * @author AXS (26.05.2020)
 */
public interface MatrixViewPaneFrameComponent extends ViewPaneFrameComponent {

    /**
     * @return
     */
    public default int getTitleIndex() {
        MatrixViewPane viewPane = getViewPane();
        return viewPane.getTitleIndex();
    }

    @Override
    public MatrixViewPane getViewPane();

    /**
     * @return
     */
    public default MetaPathSelection getMetaPathSelection() {
        MatrixViewPane viewPane = getViewPane();
        MetaPathSelection metaPathSelection = viewPane.getMetaPathSelection();
        return metaPathSelection;
    }

    /**
     * @param metaPathSelection
     */
    public default void setMetaPathSelection(final MetaPathSelection metaPathSelection) {
        MatrixViewPane viewPane = getViewPane();
        viewPane.setMetaPathSelection(metaPathSelection);
    }

    /**
     * @param mainFrameToolbar
     */
    public default void setMatrixViewToolbar(final MatrixViewPaneToolbar matrixViewToolbar) {
        MatrixViewPane viewPane = getViewPane();
        viewPane.setMatrixViewToolbar(matrixViewToolbar);
    }

    @Override
    default void dataChanged(final GraphDocument source) {
        MatrixViewPane viewPane = getViewPane();
        viewPane.update();
    }

}
