package de.imise.tool3lgm.graphtools.newmatrixview;

import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.path.MetaPathSelector.MetaPathSelection;
import de.imise.tool3lgm.gui.AbstractInternalFrame;
import de.imise.tool3lgm.gui.InternalFrameToolbarManager;
import de.imise.tool3lgm.gui.MatrixViewContainer;
import de.imise.tool3lgm.gui.MatrixViewPane;

/**
 * Klasse zur Darstellung von Verbindungen zwischen Objekten in einer Tabelle
 *
 * @author Thomas Rudert, AXS
 */
public class MatrixViewInternalFrame extends AbstractInternalFrame {

    /**
     * @param doc
     * @param toolbarManager
     * @param titleIndex Zähler, der an den Titel des Fensters angehängt wird. Man kann beliebig viele Matrixfenster für dasselbe Teilmodell öffnen.
     *            Der Title soll unterscheidbar sein und das wird er durch diese Nummer.
     */
    public MatrixViewInternalFrame(final GraphDocument doc, final InternalFrameToolbarManager toolbarManager, final int titleIndex) {
        super(new MatrixViewPane(doc, toolbarManager, titleIndex));
        setClosable(true);
        updateTitle();
    }

    /**
     * @return
     */
    public int getTitleIndex() {
        MatrixViewContainer viewContainer = getViewContainer();
        return viewContainer.getTitleIndex();
    }

    @Override
    public MatrixViewContainer getViewContainer() {
        return (MatrixViewContainer) super.getViewContainer();
    }

    /**
     * @return
     */
    public MetaPathSelection getMetaPathSelection() {
        MatrixViewContainer viewContainer = getViewContainer();
        MetaPathSelection metaPathSelection = viewContainer.getMetaPathSelection();
        return metaPathSelection;
    }

    /**
     * @param metaPathSelection
     */
    public void setMetaPathSelection(final MetaPathSelection metaPathSelection) {
        MatrixViewContainer viewContainer = getViewContainer();
        viewContainer.setMetaPathSelection(metaPathSelection);
    }

    /**
     * @param mainFrameToolBar
     */
    public void setMatrixViewToolBar(final InternalMatrixFrameToolBar matrixViewToolBar) {
        MatrixViewContainer viewContainer = getViewContainer();
        viewContainer.setMatrixViewToolBar(matrixViewToolBar);
    }

}
