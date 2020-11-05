package de.imise.tool3lgm.gui.internalframe;

import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.gui.viewpane.ViewPaneToolbarManager;
import de.imise.tool3lgm.gui.viewpane.matrix.MatrixViewPane;
import de.imise.tool3lgm.gui.viewpane.matrix.MatrixViewPaneFrameComponent;

/**
 * Klasse zur Darstellung von Verbindungen zwischen Objekten in einer Tabelle
 *
 * @author Thomas Rudert, AXS
 */
public class MatrixViewInternalFrame extends ToolInternalFrame implements MatrixViewPaneFrameComponent {

    /**
     * @param doc
     * @param toolBarManager
     * @param titleIndex Zähler, der an den Titel des Fensters angehängt wird.
     *            Man kann beliebig viele Matrixfenster für dasselbe Teilmodell
     *            öffnen. Der Title soll unterscheidbar sein und das wird er
     *            durch diese Nummer.
     */
    public MatrixViewInternalFrame(final GraphDocument doc, final ViewPaneToolbarManager toolBarManager, final int titleIndex) {
        super(new MatrixViewPane(doc, toolBarManager, titleIndex));
        setClosable(true);
        updateTitle();
    }

    @Override
    public MatrixViewPane getViewPane() {
        return (MatrixViewPane) viewPane;
    }

}
