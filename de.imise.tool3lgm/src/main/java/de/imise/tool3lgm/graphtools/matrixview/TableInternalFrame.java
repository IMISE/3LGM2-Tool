package de.imise.tool3lgm.graphtools.matrixview;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.Node;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.path.MetaPath;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.gui.AbstractInternalFrame;

/**
 * Klasse zur Darstellung von Verbindungen zwischen Objekten in einer Tabelle
 *
 * @author Thomas Rudert, AXS
 */
public final class TableInternalFrame extends AbstractInternalFrame implements MouseMotionListener, MouseListener {

    /**
     * Panel für die Zeilenbeschriftungen (Elementnamen)
     */
    private final RowPanel rowHeaderPanel;

    /**
     * Panel für die Spaltenbeschriftungen (Elementnamen)
     */
    private final ColPanel colHeaderPanel;

    /**
     * Panel für die Darstellung der Verbindungen
     */
    private final CellPanel cellPanel;

    /**
     * Panel, das angezeigt wird, solange kein korrekter MetaPfad ausgewählt ist.
     */
    private final JPanel msgPanel;

    /**
     * Das Model nach dem die Tabelle aufgebaut wird
     */
    private final TableModel tableModel;

    /**
     * @param graphDocument
     */
    public TableInternalFrame(final LGMGraphDocument graphDocument) {
        super(graphDocument, "");
        setClosable(true);
        tableModel = new TableModel(getGraphDocument());

        setToolBar(new TableToolBar(this));

        msgPanel = new JPanel();

        rowHeaderPanel = new RowPanel(tableModel.getRowHeaders());
        colHeaderPanel = new ColPanel(tableModel.getColHeaders());
        cellPanel = new CellPanel(tableModel, colHeaderPanel, rowHeaderPanel);
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
    public void update(final Class<? extends ModelElement> rowClass, final Class<? extends ModelElement> colClass, final MetaPath metaPath, final boolean showPartsOnly) {
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

    @Override
    public final void mouseMoved(final MouseEvent e) {
        ((TableToolBar) getToolBar()).positionChanged(colHeaderPanel.getCol(e.getX()), rowHeaderPanel.getRow(e.getY()));
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
        ((TableToolBar) getToolBar()).positionChanged(null, null);
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
    public final void elementGraphicsChanged(final GraphDocument source, final ElementContainer element) {
    }

    @Override
    public final void layoutChanged(final GraphDocument source) {
    }

    @Override
    public final void elementAdded(final GraphDocument source, final ElementContainer element) {
        dataChanged(source);
    }

    @Override
    public final void elementDeleted(final GraphDocument source, final ElementContainer element) {
        dataChanged(source);
    }

    @Override
    public final void groupOrderChanged(final GraphDocument source) {
    }

    @Override
    public final void activeLayerChanged(final GraphDocument source) {
    }

    @Override
    public final void colorsChanged(final GraphDocument source) {
    }

    @Override
    public final void selectionChanged(final GraphDocument source) {
    }

    @Override
    public void elementNameChanged(final ElementContainer ec) {
        dataChanged(ec.getGraphDocument());
    }

    @Override
    public void userFieldValueChanged(final ElementContainer ec) {
    }

}
