package de.imise.tool3lgm.gui.tabbedframe;

import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.gui.viewpane.ViewPaneToolbarManager;
import de.imise.tool3lgm.gui.viewpane.matrix.MatrixViewPane;
import de.imise.tool3lgm.gui.viewpane.matrix.MatrixViewPaneFrameComponent;

/**
 * @author AXS (27.05.2020)
 */
public class MainFrameDesktopMatrixTabComponent extends MainFrameDesktopTabComponent implements MatrixViewPaneFrameComponent {

    /**
     * @param doc
     * @param toolBarManager
     * @param titleIndex Zähler, der an den Titel des Fensters angehängt wird. Man kann beliebig viele Matrixfenster für dasselbe Teilmodell öffnen.
     *            Der Title soll unterscheidbar sein und das wird er durch diese Nummer.
     */
    public MainFrameDesktopMatrixTabComponent(final GraphDocument doc, final ViewPaneToolbarManager toolBarManager, final int titleIndex) {
        super(new MatrixViewPane(doc, toolBarManager, titleIndex));
    }

    @Override
    public MatrixViewPane getViewPane() {
        return (MatrixViewPane) super.getViewPane();
    }

}
