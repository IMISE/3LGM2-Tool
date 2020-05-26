package de.imise.tool3lgm.gui;

import de.imise.tool3lgm.graphtools.newmatrixview.InternalMatrixFrameToolBar;
import de.imise.tool3lgm.graphtools.path.MetaPathSelector.MetaPathSelection;

/**
 * @author AXS (20.05.2020)
 */
public interface MatrixViewContainer extends ViewContainer {

    public int getTitleIndex();

    public MetaPathSelection getMetaPathSelection();

    public void setMetaPathSelection(MetaPathSelection metaPathSelection);

    public void setMatrixViewToolBar(InternalMatrixFrameToolBar matrixViewToolbar);

}
