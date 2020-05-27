package de.imise.tool3lgm.gui.internalframe;

import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.gui.ViewPaneToolbarManager;
import de.imise.tool3lgm.gui.matrixview.MatrixViewPane;
import de.imise.tool3lgm.gui.matrixview.MatrixViewPaneFrameComponent;

/**
 * Klasse zur Darstellung von Verbindungen zwischen Objekten in einer Tabelle
 *
 * @author Thomas Rudert, AXS
 */
public class MatrixViewInternalFrame extends ToolInternalFrame implements MatrixViewPaneFrameComponent {

    /**
     * @param doc
     * @param toolbarManager
     * @param titleIndex Zähler, der an den Titel des Fensters angehängt wird. Man kann beliebig viele Matrixfenster für dasselbe Teilmodell öffnen.
     *            Der Title soll unterscheidbar sein und das wird er durch diese Nummer.
     */
    public MatrixViewInternalFrame(final GraphDocument doc, final ViewPaneToolbarManager toolbarManager, final int titleIndex) {
        super(new MatrixViewPane(doc, toolbarManager, titleIndex));
        setClosable(true);
        updateTitle();
    }

    @Override
    public MatrixViewPane getViewPane() {
        return (MatrixViewPane) viewPane;
    }

}
