package de.imise.tool3lgm.gui.matrixview;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;

/**
 * @author Thomas Rudert
 */
public class ColPanel extends JPanel {

    /** ArrayList mit den Strings der Namen der Spaltenelemente */
    private List<ModelElement> cols;

    /** Integer, der die Spaltenbreite definiert */
    private double deltaY = Double.NaN;

    /** Integer, der die horizontale Verschiebung zwischen zwei Einträgen definiert */
    private double deltaX = 0;

    /** Integer der die maximal Länge der Strings in Pixeln */
    private int maxLength = 0;

    /** Winkel, um welchen die Schrift gekippt werden soll (Standard: 45° gegen Uhrzeigersinn) */
    private final double theta = Math.PI / 4.;

    /** affine Transformation zum kippen der Spaltenüberschrift */
    private AffineTransform transform = new AffineTransform();
    private static AffineTransform nullTransform = new AffineTransform();

    /** Spaltenbreite */
    private int colWidth;

    /** Anzahl der Spalten */
    private int numberOfCols;

    private boolean initialized = false;

    /**
     * Konstruktor
     *
     * @param _cols ArrayList mit Strings der Spaltenüberschriften
     */
    public ColPanel(final List<ModelElement> _cols) {
        super();
        cols = _cols;
    }

    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double x = 0;
        double y = 0;

        if (!initialized) {
            setRequiredParam(g);
        }

        g2.drawLine(1, getHeight() - 1, getNumberOfCols() * getColWidth(), getHeight() - 1);

        /* Koordinatentransformation zum Kippen der Spaltenüberschriften */
        g2.transform(transform);

        Iterator<ModelElement> i = cols.iterator();
        g2.drawLine(round(x), round(y), round(x + maxLength), round(y));
        while (i.hasNext()) {
            g2.drawString(i.next().toString().replace('\n', ' '), round(x + deltaX), round(y + deltaY * 3. / 4.));
            x += deltaX;
            y += deltaY;
            g2.drawLine(round(x), round(y), round(x + maxLength), round(y));
        }

        g2.setTransform(nullTransform);
    }

    /**
     * @param value
     * @return
     */
    private static int round(final double value) {
        return (int) Math.round(value);
    }

    /**
     * Bestimmt die benötigten Parameter delta und max_height und legt die Größe der Component fest
     *
     * @param g Graphics
     */
    private void setRequiredParam(final Graphics g) {
        /* Höhe der Komponente */
        double height = 0;

        /* Spaltenbreite */
        deltaY = g.getFontMetrics().getHeight() + 4;

        /* Zeilenlänge ermitteln */
        Iterator<ModelElement> i = cols.iterator();
        String temp_string;
        while (i.hasNext()) {
            temp_string = i.next().toString().replace('\n', ' ');
            maxLength = maxLength < g.getFontMetrics().stringWidth(temp_string) ? g.getFontMetrics().stringWidth(temp_string) + 4 : maxLength;
        }
        maxLength = Math.min(300, maxLength);

        /* Anzahl der Spalten */
        numberOfCols = cols.size();

        double ml = maxLength + deltaY;

        height = Math.sin(theta) * ml;

        colWidth = (int) (deltaY / Math.sin(theta));
        deltaY = colWidth * Math.sin(theta);
        deltaX = colWidth * Math.cos(theta);

        /* Component-Größe festlegen */
        Dimension dim = new Dimension((int) (getColWidth() * numberOfCols + ml * Math.cos(theta)), (int) height);

        setSize(dim);
        setPreferredSize(dim);

        transform = new AffineTransform();
        transform.translate(0, getHeight());
        transform.rotate(-theta);

        initialized = true;
    }

    /**
     * gibt Spaltenbreite in Pixel zurück
     *
     * @return int Spaltenbreite in Pixeln
     */
    public int getColWidth() {
        return colWidth;
    }

    /**
     * gibt Anzahl der Spalten zurück
     *
     * @return int Anzahl der Spalten
     */
    public int getNumberOfCols() {
        return numberOfCols;
    }

    /**
     * Method getCol return String of header of column at position
     *
     * @param i int-position in pixels
     * @return String header of column
     */
    public ModelElement getCol(int i) {
        i = getColIndex(i);
        if (i < cols.size() && i >= 0) {
            return cols.get(i);
        }
        return null;
    }

    /**
     * @param cols
     */
    public void setCols(final List<ModelElement> cols) {
        this.cols = cols;
        initialized = false;
    }

    /**
     * Method getColIndex return index of column at position
     *
     * @param i int-position in pixels
     * @return int index of column
     */
    public int getColIndex(final int i) {
        if (getColWidth() != 0) {
            return (i - 1) / getColWidth();
        }
        return -1;
    }

}