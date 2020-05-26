package de.imise.tool3lgm.gui;

import de.imise.tool3lgm.graphtools.newmatrixview.MatrixViewPaneToolBar;
import de.imise.tool3lgm.graphtools.path.MetaPathSelector.MetaPathSelection;

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
     * @param mainFrameToolBar
     */
    public default void setMatrixViewToolBar(final MatrixViewPaneToolBar matrixViewToolBar) {
        MatrixViewPane viewPane = getViewPane();
        viewPane.setMatrixViewToolBar(matrixViewToolBar);
    }

}
