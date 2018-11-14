package de.imise.tool3lgm.graphtools.matrixview;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import de.imise.util.swing.component.BlockScrollableJPanel;

/**
 * @author Thomas Rudert
 */
public class CellPanelOld extends BlockScrollableJPanel implements MouseMotionListener {

    /** Das Model nach dem die Zellen sich aufbauen */
    private final TableModelOld tableModel;

    /** Panel mit Spaltenüberschriften */
    private final ColPanelOld colHeaderPanel;

    /** Panel mit Zeilenüberschriften */
    private final RowPanelOld rowHeaderPanel;

    /** Parameter gesetzt ? */
    private boolean initialized = false;

    /**
     * Konstruktor
     * 
     * @param tableModel Model nach dem die Zellen sich aufbauen
     * @param colHeaderPanel Panel mit Spaltenüberschriften
     * @param rowHeaderPanel Panel mit Zeilenüberschriften
     */
    public CellPanelOld(final TableModelOld tableModel, final ColPanelOld colHeaderPanel, final RowPanelOld rowHeaderPanel) {
        super();

        this.colHeaderPanel = colHeaderPanel;
        this.rowHeaderPanel = rowHeaderPanel;
        this.tableModel = tableModel;
    }

    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);

        if (!initialized) {
            setParameter(g);
        }

        int width = colHeaderPanel.getColWidth();
        int height = rowHeaderPanel.getRowHeight();
        int j = rowHeaderPanel.getNumberOfRows();
        int i = colHeaderPanel.getNumberOfCols();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, colHeaderPanel.getNumberOfCols() * width, j * height);
        g.setColor(Color.BLACK);

        for (; i >= 0; i--) {
            g.drawLine(i * width, 0, i * width, j * height);
        }

        i = colHeaderPanel.getNumberOfCols();

        for (; j >= 0; j--) {
            g.drawLine(0, j * height, i * width, j * height);
        }

        /* linke obere Ecke und Kantenlänge des Würfels zum Markieren der Zellen */
        int x, y, a = (width + height) / 4;

        int dx = (width - a) / 2;
        int dy = (height - a) / 2;

        for (TableCellOld cell : tableModel) {
            x = cell.getColIndex() * width + dx;
            y = cell.getRowIndex() * height + dy;
            g.setColor(cell.getColor());
            g.fillRect(x, y, a, a);
        }
    }

    @Override
    public void revalidate() {
        initialized = false;
        super.revalidate();
    }

    /**
     * bestimmt die benötigten Parameter und legt die Größe der Component fest
     * 
     * @param g Graphics
     */
    private void setParameter(final Graphics g) {
        Dimension dim = new Dimension(colHeaderPanel.getWidth(), rowHeaderPanel.getHeight());
        setMaxUnitIncrement(rowHeaderPanel.getRowHeight());
        setSize(dim);
        setPreferredSize(dim);

        initialized = true;
    }

    @Override
    public void mouseDragged(final MouseEvent e) {
    }

    @Override
    public void mouseMoved(final MouseEvent e) {

    }

}
