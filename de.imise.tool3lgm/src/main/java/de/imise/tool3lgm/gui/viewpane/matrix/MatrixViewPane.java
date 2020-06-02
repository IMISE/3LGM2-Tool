package de.imise.tool3lgm.gui.viewpane.matrix;

import static de.imise.tool3lgm.Static.contextGenerator;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.path.MetaPathSelector.MetaPathSelection;
import de.imise.tool3lgm.graphtools.path.metapaths.AbstractMetaPath;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.gui.viewpane.ViewPane;
import de.imise.tool3lgm.gui.viewpane.ViewPaneToolbarManager;

/**
 * @author AXS (21.05.2020)
 */
public class MatrixViewPane extends ViewPane implements MouseMotionListener, MouseListener {

    /** Liefert die aktuelle Toolbar */
    private final ViewPaneToolbarManager toolbarManager;

    /** Panel für die Zeilenbeschriftungen (Elementnamen) */
    private final RowPanel rowHeaderPanel;

    /** Panel für die Spaltenbeschriftungen (Elementnamen) */
    private final ColPanel colHeaderPanel;

    /** Panel für die Darstellung der Verbindungen */
    private final CellPanel cellPanel;

    /** Panel, das angezeigt wird, solange kein korrekter MetaPfad ausgewählt ist. */
    private final JPanel msgPanel;

    /** Das Model nach dem die Tabelle aufgebaut wird */
    private final TableModel tableModel;

    /** Einstellungen, welche Klassen und welche Pfade in der Matrix dargestellt werden sollen */
    private MetaPathSelection metaPathSelection;

    /**
     * Zähler, der an den Titel des Fensters angehängt wird. Man kann beliebig viele Matrixfenster für dasselbe Teilmodell öffnen. Der Title soll
     * unterscheidbar sein und das wird er durch diese Nummer.
     */
    private final int titleIndex;

    /**
     * @param doc
     * @param toolbarManager
     * @param titleIndex Zähler, der an den Titel des Fensters angehängt wird. Man kann beliebig viele Matrixfenster für dasselbe Teilmodell öffnen.
     *            Der Title soll unterscheidbar sein und das wird er durch diese Nummer.
     */
    public MatrixViewPane(final GraphDocument doc, final ViewPaneToolbarManager toolbarManager, final int titleIndex) {
        super(doc);
        this.toolbarManager = toolbarManager;
        this.titleIndex = titleIndex;
        tableModel = new TableModel(getGraphDocument());

        msgPanel = new JPanel();

        rowHeaderPanel = new RowPanel(tableModel.getRowHeaders());
        colHeaderPanel = new ColPanel(tableModel.getColHeaders());
        cellPanel = new CellPanel(tableModel, colHeaderPanel, rowHeaderPanel);
        cellPanel.addMouseMotionListener(this);
        cellPanel.addMouseListener(this);
        setComponents();

    }

    @Override
    public String getFullName() {
        GDCollection gdcoll = doc.getCollection();
        String gdcollName = gdcoll.getName();
        String docName = getName();
        String matrixLabel = getResString("matrix");
        String title = gdcollName + " - " + docName + " - " + matrixLabel + " s#" + titleIndex;
        return title;
    }

    /**
     * @return the name of this view. Default is the name of the {@link GraphDocument}
     */
    @Override
    public String getName() {
        GraphDocument doc = getGraphDocument();
        String matrixLabel = getResString("matrix");
        String docTitle = doc.getTitle();
        String name = "(" + matrixLabel + " " + titleIndex + ") " + docTitle;
        return name;
    }

    /**
     * @return
     */
    public int getTitleIndex() {
        return titleIndex;
    }

    /**
     * @param metaPathSelection
     */
    public void setMetaPathSelection(final MetaPathSelection metaPathSelection) {
        this.metaPathSelection = metaPathSelection;
        tableModel.fillTableModel(metaPathSelection);
        rowHeaderPanel.setRows(tableModel.getRowHeaders());
        colHeaderPanel.setCols(tableModel.getColHeaders());
        cellPanel.revalidate();
        setComponents();
    }

    /**
     * @return
     */
    public MetaPathSelection getMetaPathSelection() {
        return metaPathSelection;
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

    /** Werkzeugleiste zu diesem Fenster */
    private MatrixViewPaneToolbar matrixViewToolbar = null;

    /**
     * @param matrixViewToolbar
     */
    public void setMatrixViewToolbar(final MatrixViewPaneToolbar matrixViewToolbar) {
        this.matrixViewToolbar = matrixViewToolbar;
    }

    @Override
    public void mouseDragged(final MouseEvent e) {
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
        ModelElement colElement = colHeaderPanel.getCol(e.getX());
        ModelElement rowElement = rowHeaderPanel.getRow(e.getY());
        if (toolbarManager.isMatrixViewToolbar()) {
            MatrixViewPaneToolbar matrixFrameToolbar = toolbarManager.getMatrixViewToolbar();
            TableCell cell = tableModel.getCell(colHeaderPanel.getColIndex(e.getX()), rowHeaderPanel.getRowIndex(e.getY()));
            String pathName = cell == null ? null : matrixFrameToolbar.getPathName(cell.getColor());
            matrixFrameToolbar.positionChanged(colElement, rowElement, pathName);
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

    @Override
    public void mouseReleased(final MouseEvent e) {
        mouseEventRoutine(e);
    }

    private void mouseEventRoutine(final MouseEvent e) {
        /* nur anlegbare Verbindungen */
        List<AbstractMetaPath> metaPaths = tableModel.getMetaPaths();
        if (metaPaths == null) {
            return;
        }

        /* nur anlgebare Verbindungen */
        for (AbstractMetaPath metaPath : metaPaths) {
            if (!metaPath.isCreatable(false)) {
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

        Node rowNode = tableModel.getRowElement(rowHeaderPanel.getRowIndex(e.getY()));
        Node colNode = tableModel.getColElement(colHeaderPanel.getColIndex(e.getX()));

        if (rowNode == null || colNode == null) {
            return;
        }

        GraphDocument mainDoc = doc.getCollection().getMainDoc();

        mainDoc.select(colNode.getContainer(mainDoc), TransactionManager.STANDARD_PID);
        contextGenerator.setElementContainer(rowNode.getContainer(mainDoc));
        contextGenerator.setElementClicked(true);
        contextGenerator.processMouseEvent(left_button, right_button, cellPanel, e.getX(), e.getY());
        contextGenerator.setElementClicked(false);
    }

    @Override
    public void mouseEntered(final MouseEvent arg0) {
    }

    @Override
    public void mouseExited(final MouseEvent arg0) {
        matrixViewToolbar.positionChanged(null, null, null);
    }

    //  Methoden des Interfaces GDCollectionChangeListener --- Anfang ---

    public void update() {
        //Model des Tables aktualisieren
        tableModel.update();
        //Zeilen und Spalten neu aufbauen
        rowHeaderPanel.setRows(tableModel.getRowHeaders());
        colHeaderPanel.setCols(tableModel.getColHeaders());
        //das CellPanel zum resizen veranlassen und neu zeichen
        cellPanel.revalidate();
        cellPanel.repaint();
    }

}
