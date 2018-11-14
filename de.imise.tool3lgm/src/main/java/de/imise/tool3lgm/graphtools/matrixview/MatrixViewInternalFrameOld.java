package de.imise.tool3lgm.graphtools.matrixview;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.path.meta.AbstractMetaPath;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.gui.AbstractInternalFrame;
import de.imise.util.swing.component.UnfloatableToolBar;

/**
 * Klasse zur Darstellung von Verbindungen zwischen Objekten in einer Tabelle
 *
 * @author Thomas Rudert, AXS
 */
public final class MatrixViewInternalFrameOld extends AbstractInternalFrame implements MouseMotionListener, MouseListener {

    /**
     * Panel für die Zeilenbeschriftungen (Elementnamen)
     */
    private final RowPanelOld rowHeaderPanel;

    /**
     * Panel für die Spaltenbeschriftungen (Elementnamen)
     */
    private final ColPanelOld colHeaderPanel;

    /**
     * Panel für die Darstellung der Verbindungen
     */
    private final CellPanelOld cellPanel;

    /**
     * Panel, das angezeigt wird, solange kein korrekter MetaPfad ausgewählt ist.
     */
    private final JPanel msgPanel;

    /**
     * Das Model nach dem die Tabelle aufgebaut wird
     */
    private final TableModelOld tableModel;

    /**
     * @param graphDocument
     */
    public MatrixViewInternalFrameOld(final LGMGraphDocument graphDocument) {
        super(graphDocument, "");
        setClosable(true);
        tableModel = new TableModelOld(getGraphDocument());

        msgPanel = new JPanel();

        rowHeaderPanel = new RowPanelOld(tableModel.getRowHeaders());
        colHeaderPanel = new ColPanelOld(tableModel.getColHeaders());
        cellPanel = new CellPanelOld(tableModel, colHeaderPanel, rowHeaderPanel);
        cellPanel.addMouseMotionListener(this);
        cellPanel.addMouseListener(this);
        setComponents();
    }

    /**
     * Füllt das TableModel
     *
     * @param rowClass Zeilenelementklasse
     * @param colClass Spaltenelementklasse
     * @param metaPath Metapfad über den Elemente der Zeilen und Splaten miteinander verbunden sein können
     * @param showPartsOnly legt fest, ob nur absolute Teilelemente angezeigt werden sollen (absolut heiß, dass sie im Gesamtmodell keine Teile haben
     *            dürfen)
     */
    public void update(final Class<? extends ModelElement> rowClass, final Class<? extends ModelElement> colClass, final AbstractMetaPath metaPath, final boolean showPartsOnly) {
        tableModel.fillTableModel(rowClass, colClass, metaPath, showPartsOnly);
        rowHeaderPanel.setRows(tableModel.getRowHeaders());
        colHeaderPanel.setCols(tableModel.getColHeaders());
        cellPanel.revalidate();
        setComponents();
    }

    /**
     *
     */
    private void setComponents() {
        if (tableModel.isValid() && tableModel.getColHeaders().size() > 0 && tableModel.getRowHeaders().size() > 0) {
            scrollPane.setVisible(false);
            scrollPane.setViewportView(cellPanel);
            scrollPane.setRowHeaderView(rowHeaderPanel);
            scrollPane.setColumnHeaderView(colHeaderPanel);
            scrollPane.setVisible(true);
            revalidate();
        } else {
            msgPanel.removeAll();
            StringBuilder sb = new StringBuilder();
            sb.append(getResString("empty_matrix_message"));
            msgPanel.add(new JLabel(sb.toString()));
            scrollPane.setVisible(false);
            scrollPane.setRowHeaderView(null);
            scrollPane.setColumnHeaderView(null);
            scrollPane.setViewportView(msgPanel);
            scrollPane.setVisible(true);
            revalidate();
        }
    }

    @Override
    public final void mouseDragged(final MouseEvent e) {
    }

    /** Werkzeugleiste zu diesem Fenster */
    private MatrixViewPathSelectorToolBarOld matrixViewToolBar = null;

    /**
     * gibt die Werkzeugleise zu diesem Fenster zurück
     *
     * @return Werkzeugleiste des Fensters
     */
    public UnfloatableToolBar getMatrixViewToolBar() {
        return matrixViewToolBar;
    }

    public void setMatrixViewToolBar(final MatrixViewPathSelectorToolBarOld matrixViewToolBar) {
        this.matrixViewToolBar = matrixViewToolBar;
    }

    @Override
    public final void mouseMoved(final MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        ModelElement col = colHeaderPanel.getCol(x);
        Node row = rowHeaderPanel.getRow(y);
        matrixViewToolBar.positionChanged(col, row);
    }

    @Override
    public final void mouseClicked(final MouseEvent e) {
    }

    @Override
    public final void mousePressed(final MouseEvent e) {
    }

    @Override
    public final void mouseReleased(final MouseEvent e) {
        if (tableModel.getMetaPath() == null) {
            return;
        }

        /* nur direkte Verbindungen */
        for (int i = 0; i < tableModel.getMetaPath().countPathes(); i++) {
            if (tableModel.getMetaPath() == null || !tableModel.getMetaPath().isImmediate(i)) {
                return;
            }
        }

        boolean left_button, right_button;
        if (e.isPopupTrigger()) {
            right_button = true;
            left_button = false;
        } else {
            right_button = false;
            left_button = true;
        }

        Node rknot = tableModel.getRowKnot(rowHeaderPanel.getRowIndex(e.getY()));
        Node cknot = tableModel.getColKnot(colHeaderPanel.getColIndex(e.getX()));

        if (rknot == null || cknot == null) {
            return;
        }

        GraphDocument mainDoc = doc.getCollection().getMainGraphDocument();

        mainDoc.select(rknot.getContainer(mainDoc), 0);
        Tool3lgm.getContextGenerator().setModelElement(cknot.getContainer(mainDoc));
        Tool3lgm.getContextGenerator().setElementGetroffen(true);
        Tool3lgm.getContextGenerator().processMouseEvent(left_button, right_button, cellPanel, e.getX(), e.getY());
        Tool3lgm.getContextGenerator().setElementGetroffen(false);
    }

    @Override
    public final void mouseEntered(final MouseEvent arg0) {
    }

    @Override
    public final void mouseExited(final MouseEvent arg0) {
        matrixViewToolBar.positionChanged(null, null);
    }

    //	Methoden des Interfaces GraphDocumentListener --- Anfang ---

    @Override
    public final void dataChanged(final GraphDocument source) {
        //Model des Tables aktualisieren
        tableModel.update();
        //Zeilen und Spalten neu aufbauen
        rowHeaderPanel.setRows(tableModel.getRowHeaders());
        colHeaderPanel.setCols(tableModel.getColHeaders());
        //das CellPanel zum resizen veranlassen und neu zeichen
        cellPanel.revalidate();
        cellPanel.repaint();
    }

    @Override
    public void elementNameChanged(final ElementContainer ec) {
        dataChanged(ec.getGraphDocument());
    }

    @Override
    public void userFieldValueChanged(final ElementContainer ec) {
    }

}
