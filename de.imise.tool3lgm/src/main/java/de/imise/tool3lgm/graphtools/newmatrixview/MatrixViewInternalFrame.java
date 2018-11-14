package de.imise.tool3lgm.graphtools.newmatrixview;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.GraphDocumentListener;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.path.meta.AbstractMetaPath;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.gui.AbstractInternalFrame;
import de.imise.util.swing.component.UnfloatableToolBar;

/**
 * Klasse zur Darstellung von Verbindungen zwischen Objekten in einer Tabelle
 *
 * @author Thomas Rudert, AXS
 */
public class MatrixViewInternalFrame extends AbstractInternalFrame implements MouseMotionListener, MouseListener, GraphDocumentListener {

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
    public MatrixViewInternalFrame(final LGMGraphDocument graphDocument) {
        super(graphDocument, "");
        setClosable(true);
        tableModel = new TableModel(getGraphDocument());

        msgPanel = new JPanel();

        rowHeaderPanel = new RowPanel(tableModel.getRowHeaders());
        colHeaderPanel = new ColPanel(tableModel.getColHeaders());
        cellPanel = new CellPanel(tableModel, colHeaderPanel, rowHeaderPanel);
        cellPanel.addMouseMotionListener(this);
        cellPanel.addMouseListener(this);
        setComponents();

        if (getInternalFrameToolBar() == null) {
            setToolBar(new InternalMatrixFrameToolBar(this));
        }

    }

    /**
     * Füllt das TableModel
     *
     * @param rowClass
     *            Zeilenelementklasse
     * @param colClass
     *            Spaltenelementklasse
     * @param metaPaths
     *            Metapfade über den Elemente der Zeilen und Splaten miteinander
     *            verbunden sein können
     * @param showPartsOnly
     *            legt fest, ob nur absolute Teilelemente angezeigt werden sollen
     *            (absolut heiß, dass sie im Gesamtmodell keine Teile haben dürfen)
     */
    public void update(final Class<? extends ModelElement> rowClass, final Class<? extends ModelElement> colClass, final AbstractMetaPath[] metaPaths, final boolean showPartsOnly) {
        tableModel.fillTableModel(rowClass, colClass, metaPaths, showPartsOnly);
        rowHeaderPanel.setRows(tableModel.getRowHeaders());
        colHeaderPanel.setCols(tableModel.getColHeaders());
        cellPanel.revalidate();
        setComponents();
    }

    /**
     *
     */
    private void setComponents() {
        JScrollPane scrollPane = getScrollPane();
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
            sb.append(Tool3lgmConstants.getResString("empty_matrix_message"));
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
    public void mouseDragged(final MouseEvent e) {
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
        UnfloatableToolBar toolbar = Static.getTool().getToolBar();
        ModelElement colElement = colHeaderPanel.getCol(e.getX());
        ModelElement rowElement = rowHeaderPanel.getRow(e.getY());
        if (toolbar instanceof InternalMatrixFrameToolBar) {
            InternalMatrixFrameToolBar matrixFrameToolBar = (InternalMatrixFrameToolBar) toolbar;
            TableCell cell = tableModel.getCell(colHeaderPanel.getColIndex(e.getX()), rowHeaderPanel.getRowIndex(e.getY()));
            String pathName = cell == null ? null : matrixFrameToolBar.getPathName(cell.getColor());
            matrixFrameToolBar.positionChanged(colElement, rowElement, pathName);
            if (cell == null) {
                cellPanel.setToolTipText(null);
            } else {
                cellPanel.setToolTipText("<html>" + rowElement.toString() + " <b>" + pathName + "</b> " + colElement.toString() + "</html>");
            }
        }

    }

    @Override
    public void mouseClicked(final MouseEvent e) {
    }

    @Override
    public void mousePressed(final MouseEvent e) {
    }

    /**
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseReleased(final MouseEvent e) {
        mouseEventRoutine(e);
    }

    private void mouseEventRoutine(final MouseEvent e) {
        /* nur anlegbare Verbindungen */
        AbstractMetaPath[] metaPaths = tableModel.getMetaPaths();
        if (metaPaths == null) {
            return;
        }

        /* nur anlgebare Verbindungen */
        for (int i = 0; i < metaPaths.length; i++) {
            if (!metaPaths[i].isCreateable()) {
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

        mainDoc.select(cknot.getContainer(mainDoc), TransactionManager.STANDARD_PID);
        Tool3lgm.getContextGenerator().setModelElement(rknot.getContainer(mainDoc));
        Tool3lgm.getContextGenerator().setElementGetroffen(true);
        Tool3lgm.getContextGenerator().processMouseEvent(left_button, right_button, cellPanel, e.getX(), e.getY());
        Tool3lgm.getContextGenerator().setElementGetroffen(false);
    }

    /**
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseEntered(final MouseEvent arg0) {
    }

    /**
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseExited(final MouseEvent arg0) {
        UnfloatableToolBar toolbar = Static.getTool().getToolBar();
        if (toolbar instanceof InternalMatrixFrameToolBar) {
            ((InternalMatrixFrameToolBar) toolbar).positionChanged(null, null, null);
        }
    }

    //  Methoden des Interfaces GraphDocumentListener --- Anfang ---

    @Override
    public void dataChanged(final GraphDocument source) {
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
    public void elementGraphicsChanged(final GraphDocument source, final ElementContainer element) {
    }

    @Override
    public void layoutChanged(final GraphDocument source) {
    }

    @Override
    public void groupOrderChanged(final GraphDocument source) {
    }

    @Override
    public void activeLayerChanged(final GraphDocument source) {
    }

    @Override
    public void colorsChanged(final GraphDocument source) {
    }

    @Override
    public void selectionChanged(final GraphDocument source) {
    }

}
