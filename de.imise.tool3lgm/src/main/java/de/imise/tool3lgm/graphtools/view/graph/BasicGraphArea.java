package de.imise.tool3lgm.graphtools.view.graph;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSeparator;

import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.LayerContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.util.image.ComponentAsImageExportHandler.ZoomableComponent;

/**
 * @author N.N.
 */
public class BasicGraphArea extends JComponent implements ZoomableComponent {

    /** Teilmodell das dieses Area darstellt */
    protected GraphDocument doc;

    /**
     * Factor, mit dem die Zeichenfläche ausgehend von ihrer ursprünglichen Größe gestreckt wird
     */
    protected double zoom = 1.0;

    /**
     * Anzahl der Pixel, um die die Ebenen jeweils zueinander nach rechts verschoben werden (mit der
     * untersten Ebene beginnend).
     */
    protected double effective_x_shift = 0;

    /**
     * Anzahl der Pixel, um die die Ebenen jeweils zueinander nach oben verschoben werden (mit der
     * untersten Ebene beginnend).
     */
    protected double effective_y_shift = 0;

    /**
     * Faktor, um die die Darstellung in Y-Richtung skaliert wird. Dieser Faktor wird für den aktuellen
     * Winkel jeweils neu berechnet. Je größer der Winkel wird, desto kleiner wird dieser Wert, wodurch
     * die Ebenen in der Höhe gestaucht werden.
     */
    protected double y_y_factor;

    /**
     * Faktor, um den sich in der Ebenendarstellung mit größeren Y-Werten die X-Werte vergrößern.
     * Dadurch kann man die Ebene nach (hinten) links (positive Werte) oder nach (hinten) rechts
     * (negative Werte) kippen.
     * Im Moment wird dieser Wert nicht genutzt, da keine Notwendigkeit besteht, die Darstellung zu kippen.
     */
    protected double y_x_factor;

    /**
     * Faktor, um die die Darstellung in X-Richtung skaliert wird. Im Moment wird dieser Wert nicht
     * genutzt, da keine Notwendigkeit besteht, die Darstellung in die Breite zu ziehen.
     */
    protected final double X_X_FACTOR = 1.0;

    /**
     * Faktor, um den sich in der Ebenendarstellung mit größeren X-Werten die Y-Werte vergrößern.
     * Dadurch kann man die Ebene nach unten (positive Werte) oder nach oben (negative Werte) kippen.
     * Im Moment wird dieser Wert nicht genutzt, da keine Notwendigkeit besteht, die Darstellung zu kippen.
     */
    protected final double X_Y_FACTOR = 0.0;

    /**  */
    protected int page_degree = 60;

    /**  */
    private final AffineTransform transformation;

    /**  */
    protected boolean multi_view = true;

    /**  */
    protected int middle_x = 0, middle_y = 0;

    /**  */
    private final Insets i, c;

    /**  */
    protected int page_width = 0, page_height = 0;

    /** Anzahl der Pixel des Abstandes zwischen den Ebenen in der Mehrebenenansicht */
    protected int interLayerSpace = 200;

    /**  */
    private final int frame_width = page_width, frame_height = page_height;

    /**  */
    private int old_degree = 60, old_pitch_shift = 200;

    //	private BasicStroke dick, duenn, strichel;

    /**  */
    protected int left_sel_x, left_sel_y, right_sel_x, right_sel_y;

    /**  */
    protected boolean mouse_selection = false;

    /** Aktuelle Abstände der Ebenendarstellung vom Gesamtrand dieser Komponente */
    private final Insets graphBorder = new Insets(50, 50, 50, 50);

    /** Minimaler interner Zoom-Wert */
    private static final double ZOOM_FACTOR_MINIMUM = 0d;
    /** Maximaler interner Zoom-Wert */
    private static final double ZOOM_FACTOR_MAXIMUM = 2d;

    private final List<BasicGraphAreaChangeListener> changeListener = new ArrayList<>();

    /** Mögliche Statusse für das Zeichnen */
    public enum PaintState {
        /** Alles wird mit gezeichnet */
        REGULAR,
        /** Der aktive Layer in der 3-Ebenen-Ansicht wird nicht hervorgehoben */
        SAVE_IMAGE_AS_FILE,
        /** Das Raster und evtl. vorhandene Selektionen von Elementen werden nicht mitgezeichnet */
        WEBEXPORT
    }

    /** Status für das Zeichnen */
    private PaintState paintState = PaintState.REGULAR;

    /**
     * @param gdoc
     */
    public BasicGraphArea(final GraphDocument gdoc) {
        setLayout(null);
        doc = gdoc;
        page_width = doc.getPageWidth();
        page_height = doc.getPageHeight();
        transformation = new AffineTransform(1, 0, -0.5, 0.4, 0, 0);
        i = new Insets(0, 0, 0, 0);
        c = new Insets(0, 0, 0, 0);
        setMultiViewEnabled(true);
        refreshTransformation();
        check_size();

        if (!(gdoc instanceof Szenario)) {
            setLayout(new GridLayout(5, 1));
            add(new JLabel("<html><h1>&nbsp;" + getResString("uebersicht_html") + "</h1></html>"));
            add(new JSeparator());
            add(new JLabel("<html>&nbsp;" + getResString("uebersicht_descr") + "</html>"));
            add(new JSeparator());
            add(new JLabel(" "));
        }
    }

    // --- ChangeListener --- Anfang ---

    public void addChangeListener(final BasicGraphAreaChangeListener listener) {
        if (!changeListener.contains(listener)) {
            changeListener.add(listener);
        }
    }

    public void removeChangeListener(final BasicGraphAreaChangeListener listener) {
        changeListener.remove(listener);
    }

    private void fireZoomChanged() {
        for (BasicGraphAreaChangeListener listener : changeListener) {
            listener.zoomChanged(this);
        }
    }

    private void fireDegreeChanged() {
        for (BasicGraphAreaChangeListener listener : changeListener) {
            listener.degreeChanged(this);
        }
    }

    private void fireLayerViewChanged() {
        for (BasicGraphAreaChangeListener listener : changeListener) {
            listener.layerViewChanged(this);
        }
    }

    private void fireLayerGapChanged() {
        for (BasicGraphAreaChangeListener listener : changeListener) {
            listener.layerGapChanged(this);
        }
    }

    private void firePageSizeChangedChanged() {
        for (BasicGraphAreaChangeListener listener : changeListener) {
            listener.pageSizeChanged(this);
        }
    }

    // --- ChangeListener --- Ende ---

    // --- Dokumentenverwaltung --- Anfang ---

    /**
     * @param gd
     */
    public final void setDocument(final GraphDocument gd) {
        doc = gd;
        revalidate();
        repaint();
    }

    /**
     * @return
     */
    public final GraphDocument getDocument() {
        return doc;
    }

    // --- Dokumentenverwaltung --- Ende ---

    // --- GraphElementLayout-Verwaltung --- Anfang ---

    /**
     * @return aktuelle Seitenbreite
     */
    public final int getPageWidth() {
        return page_width;
    }

    /**
     * @return aktuelle Seitenhoehe
     */
    public final int getPageHeight() {
        return page_height;
    }

    /**
     * @return aktuelle Hoehenverschiebung in Pixeln
     */
    public final int getPitchShift() {
        return interLayerSpace;
    }

    /**
     * @return aktueller Zoom-Faktor
     */
    public final double getZoomFactor() {
        return zoom;
    }

    /**
     * Prueft, ob Einzel- oder Multi-Sicht eingestellt ist.
     *
     * @return <code>true</code>, wenn Mehrebenenansicht eingestellt ist, sonst <code>false</code>.
     */
    public final boolean isMultiViewEnabled() {
        return multi_view;
    }

    /**
     * Setzt die Seitenbreite auf <code>width</code> Pixel
     *
     * @param width
     *            /
     *            public final void setPageWidth(int width) {
     *            page_width = width;
     *            check_size();
     *            }
     *            /**
     *            Setzt die Seitenhoehe auf height Pixel
     * @param height
     *            /
     *            public final void setPageHeight(int height) {
     *            page_height = height;
     *            check_size();
     *            }
     *            /**
     *            Setzt die Seitengroesse auf width mal height Pixel
     */
    public final void setPageSize(final int width, final int height) {
        if (width == page_width && height == page_height) {
            revalidate();
            repaint();
            return;
        }
        page_height = height;
        page_width = width;
        check_size();
        firePageSizeChangedChanged();
    }

    /**
     * Schaltet die Sicht auf alle Ebenen ein oder aus.
     *
     * @param b <code>true</code> schaltet die Mehrebenenansicht ein, <code>false</code> aus.
     */
    public void setMultiViewEnabled(final boolean b) {
        multi_view = b;
        if (b) {
            recallSettings();
        } else {
            storeSettings();
        }
        revalidate();
        repaint();
        fireLayerViewChanged();
    }

    /**
     * Gibt den aktuellen Zeichenwinkel zurueck. Dabei bedeutet 0 normal, und
     * 90° ist dann eine flache Scheibe von der Seite.
     */
    public final int getDegree() {
        return page_degree;
    }

    /**
     * Setzt den aktuellen Zeichenwinkel
     *
     * @param degree
     */
    public void setDegree(final int degree) {
        if (degree <= 80 && degree >= 0) {
            page_degree = degree;
            y_x_factor = -degree / 360d;
            y_y_factor = (90d - degree) / 90d;
            if (y_y_factor == 0.0) {
                y_y_factor = 0.01;
            }
            refreshTransformation();
            adjustInterLayerSpace();
            adjust_size();
            fireDegreeChanged();
        }
    }

    /**
     * Speichert den aktuellen Darstellungswinkel und die Höhenverschiebung und
     * setzt beide Werte auf den Standard (0).
     *
     * @see #recallSettings()
     */
    private final void storeSettings() {
        old_degree = page_degree;
        old_pitch_shift = interLayerSpace;
        setDegree(0);
        setInterLayerSpace(0);
    }

    /**
     * Stellt die alten Werte des Darstellungswinkels und der Höhenverschiebung wieder her.
     *
     * @see #storeSettings()
     */
    private final void recallSettings() {
        setInterLayerSpace(old_pitch_shift);
        setDegree(old_degree);
    }

    /**
     * Setzt die Hoehenverschiebung der GraphPane auf shift Pixel.
     *
     * @param shift
     */
    public final void setInterLayerSpace(final int shift) {
        interLayerSpace = shift;
        adjustInterLayerSpace();
        adjust_size();
        fireLayerGapChanged();
    }

    @Override
    public double setZoomToMaximum() {
        double oldZoom = zoom;
        setZoom(ZOOM_FACTOR_MAXIMUM);
        return oldZoom;
    }

    /**
     * Setzt den aktuellen Zoom-Faktor auf (factor * 100 %)
     *
     * @param factor
     */
    @Override
    public void setZoom(double zoom) {
        if (zoom < ZOOM_FACTOR_MINIMUM) {
            zoom = ZOOM_FACTOR_MINIMUM;
        }
        if (zoom > ZOOM_FACTOR_MAXIMUM) {
            zoom = ZOOM_FACTOR_MAXIMUM;
        }
        this.zoom = zoom;
        refreshTransformation();
        adjustInterLayerSpace();
        adjust_size();
        fireZoomChanged();
    }

    /**
     *
     */
    protected final void adjustInterLayerSpace() {
        //Diese ausführliche Berechnung ist nur notwendig, wenn man auch die im Moment konstanten Faktoren X_X_FACTOR = 1.0
        //und X_Y_FACTOR = 0.0 berücksichtigen will (siehe Beschreibung der Werte oben)
        //		effective_x_shift = (-pitch_shift / y_y_factor) * (-y_x_factor) * ((X_X_FACTOR) - (y_x_factor * X_Y_FACTOR));
        //		effective_y_shift = (-pitch_shift / y_y_factor) * (X_X_FACTOR) * ((X_X_FACTOR) - (y_x_factor * X_Y_FACTOR));
        //		Das oben ist das Original und das hier drunter dasselbe nur umgestellt bzw. ein paar Klammern weggelassen
        //		effective_x_shift =  pitch_shift / y_y_factor * y_x_factor * (X_X_FACTOR - y_x_factor * X_Y_FACTOR);
        //		effective_y_shift = -pitch_shift / y_y_factor * X_X_FACTOR * (X_X_FACTOR - y_x_factor * X_Y_FACTOR);

        //Unter der Annahme, dass X_X_FACTOR = 1.0 und X_Y_FACTOR = 0.0 bleibt von der Berechnung das hier unten übrig
        effective_y_shift = -interLayerSpace / y_y_factor;
        effective_x_shift = -effective_y_shift * y_x_factor;
    }

    /**
     *
     */
    private final void refreshTransformation() {
        //Diese ausführliche Berechnung ist nur notwendig, wenn man auch die im Moment konstanten Faktoren X_X_FACTOR = 1.0
        //und X_Y_FACTOR = 0.0 berücksichtigen will (siehe Beschreibung der Werte oben)
        //		transformation = new AffineTransform(zoom * X_X_FACTOR, zoom * X_Y_FACTOR, zoom * y_x_factor, zoom * y_y_factor, 0, 0);
        //Unter der Annahme, dass X_X_FACTOR = 1.0 und X_Y_FACTOR = 0.0 bleibt von der Berechnung das hier unten übrig
        transformation.setTransform(zoom, 0d, zoom * y_x_factor, zoom * y_y_factor, 0d, 0d);
    }

    /**
     *
     */
    protected final void check_size() {
        if (!(doc instanceof Szenario)) {
            return;
        }
        getExtensionSize();
        adjust_size();
    }

    /**
     *
     */
    private final void getExtensionSize() {
        i.left = -page_width / 2;
        i.right = page_width / 2;
        i.top = -page_height / 2;
        i.bottom = page_height / 2;

        NodeContainer knoten;
        LayerContainer lay;

        int upper_border = 5;
        int lower_border = 0;
        int anzahl;

        if (!multi_view) {
            lower_border = doc.getCollection().getActiveLayer();
        }
        upper_border = lower_border + 1;

        for (int b = lower_border; b < upper_border; b += 2) {
            lay = doc.getLayer(b);
            anzahl = lay.getKnotenCount();
            for (int co = 0; co < anzahl; co++) {
                knoten = lay.getNodeContainer(co);
                if (knoten.getX() - knoten.getWidth() / 2 < i.left) {
                    i.left = knoten.getX() - knoten.getWidth() / 2;
                }
                if (knoten.getX() + knoten.getWidth() / 2 > i.right) {
                    i.right = knoten.getX() + knoten.getWidth() / 2;
                }
                if (knoten.getY() - knoten.getHeight() / 2 < i.top) {
                    i.top = knoten.getY() - knoten.getHeight() / 2;
                }
                if (knoten.getY() + knoten.getHeight() / 2 > i.bottom) {
                    i.bottom = knoten.getY() + knoten.getHeight() / 2;
                }
            }
        }
    }

    /**
     *
     */
    protected void adjust_size() {
        Point p1, p2;

        c.top = 0;
        c.bottom = 0;
        c.left = 0;
        c.right = 0;
        p2 = new Point(0, 0);

        p1 = new Point(i.left, i.top);
        transformation.transform(p1, p2);
        if (p2.getY() < c.top) {
            c.top = (int) p2.getY();
        }
        if (p2.getX() < c.left) {
            c.left = (int) p2.getX();
        }

        p1 = new Point(i.right, i.top);
        transformation.transform(p1, p2);
        if (p2.getY() < c.top) {
            c.top = (int) p2.getY();
        }
        if (p2.getX() > c.right) {
            c.right = (int) p2.getX();
        }

        p1 = new Point(i.right, i.bottom);
        transformation.transform(p1, p2);
        if (p2.getY() > c.bottom) {
            c.bottom = (int) p2.getY();
        }
        if (p2.getX() > c.right) {
            c.right = (int) p2.getX();
        }

        p1 = new Point(i.left, i.bottom);
        transformation.transform(p1, p2);
        if (p2.getY() > c.bottom) {
            c.bottom = (int) p2.getY();
        }
        if (p2.getX() < c.left) {
            c.left = (int) p2.getX();
        }

        // Abstand der Ebenen mit einbringen
        c.top -= (int) (zoom * interLayerSpace);
        c.bottom += (int) (zoom * interLayerSpace);

        // Rand addieren
        c.top -= graphBorder.top;
        c.bottom += graphBorder.bottom;
        c.left -= graphBorder.left;
        c.right += graphBorder.right;

        // Wenn das dann noch kleiner ist als der Rahmen, dann
        if (c.bottom < frame_height / 2) {
            c.bottom = frame_height / 2;
        }
        if (c.top > -frame_height / 2) {
            c.top = -frame_height / 2;
        }
        if (c.right < frame_width / 2) {
            c.right = frame_width / 2;
        }
        if (c.left > -frame_width / 2) {
            c.left = -frame_width / 2;
        }
        setPreferredSize(new Dimension(c.right - c.left, c.bottom - c.top));

        middle_x = -c.left;
        middle_y = -c.top;
        revalidate();
        repaint();
    }

    // --- GraphElementLayout-Verwaltung --- Ende ---

    // --- Operation zur grafischen Ausgabe --- Anfang ---

    @Override
    protected void paintChildren(final Graphics g) {
        if (doc == null) {
            return;
        }

        if (!(doc instanceof Szenario)) {
            super.paintChildren(g);
            return;
        }

        Graphics2D gc = (Graphics2D) g;
        if (gc == null) {
            return;
        }

        setRenderingHints(gc);

        synchronized (getTreeLock()) {
            if (!multi_view) {
                gc.translate(middle_x + effective_x_shift, middle_y + zoom * interLayerSpace + effective_y_shift);
                gc.transform(transformation);

                doc.getActiveLayer().setShift(effective_x_shift, effective_y_shift);
                doc.getActiveLayer().setMultiView(false);
                doc.getActiveLayer().paint(gc);

                if (doc.getCollection().getActiveLayer() < 4) {
                    doc.getLayer(doc.getCollection().getActiveLayer() + 1).paint(gc);
                }

                //Selektion nicht darstellen, wenn das Ergebnisbild als Datei gepsiechert werden soll
                if (paintState == PaintState.REGULAR && mouse_selection) {
                    int minx, miny, maxx, maxy;
                    minx = Math.min(left_sel_x, right_sel_x);
                    miny = Math.min(left_sel_y, right_sel_y);
                    maxx = Math.max(left_sel_x, right_sel_x);
                    maxy = Math.max(left_sel_y, right_sel_y);
                    gc.drawRect(minx, miny, maxx - minx, maxy - miny);
                }
            } else {
                gc.translate(middle_x, middle_y + zoom * interLayerSpace);
                gc.transform(transformation);

                for (int c = 0; c < ModelConstants.LAYERS.length; c++) {
                    LayerContainer active_graph = doc.getLayer(ModelConstants.LAYERS[c]);
                    active_graph.setShift(effective_x_shift, effective_y_shift);
                    active_graph.setMultiView(true);
                    active_graph.paint(gc);

                    if (c != 1 && c != 3) {
                        //Selektion nicht darstellen, wenn das Ergebnisbild als Datei gespeichert werden soll
                        if (paintState == PaintState.REGULAR && active_graph == doc.getActiveLayer() && mouse_selection) {
                            int minx, miny, maxx, maxy;
                            minx = Math.min(left_sel_x, right_sel_x);
                            miny = Math.min(left_sel_y, right_sel_y);
                            maxx = Math.max(left_sel_x, right_sel_x);
                            maxy = Math.max(left_sel_y, right_sel_y);
                            gc.drawRect(minx, miny, maxx - minx, maxy - miny);
                        }
                    } else {
                        gc.translate(effective_x_shift, effective_y_shift);
                    }
                }
            }
        }
    }

    /**
     * Setzt den Export-Modus, in dem Selektionen und das Raster nicht mitgezeichnet werden sollen.
     *
     * @param exportMode the exportMode to set
     */
    public void setPaintState(final PaintState paintState) {
        this.paintState = paintState;
        //wenn die Selektion und das Raster nicht gemalt werden soll
        for (int i = 0; i < ModelConstants.LAYERS.length; i++) {
            doc.getLayer(ModelConstants.LAYERS[i]).setPaintState(paintState);
        }
    }

    /**
     * set the Graphics2D to the current RenderingHints
     *
     * @param g2
     *            Graphics2D which RenderingHints are to set
     */
    public static void setRenderingHints(final Graphics2D g2) {
        int renderingHints = UserProperties.getRenderingHints();
        if ((renderingHints & 1) == 1) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        if ((renderingHints >> 1 & 1) == 1) {
            g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        }
        if ((renderingHints >> 2 & 1) == 1) {
            g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        }
        if ((renderingHints >> 3 & 1) == 1) {
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        }
        if ((renderingHints >> 4 & 1) == 1) {
            g2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        }
        if ((renderingHints >> 5 & 1) == 1) {
            g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        }
        if ((renderingHints >> 6 & 1) == 1) {
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        }
        if ((renderingHints >> 7 & 1) == 1) {
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }

    }

    public void adaptSettings(final BasicGraphArea source) {
        setDegree(source.page_degree);
        setMultiViewDegree(source.getMultiViewDegree());
        setMultiViewEnabled(source.multi_view);
        setMultiViewPitchShift(source.getMultiViewPitchShift());
        setPageSize(source.getPageWidth(), source.getPageHeight());
        setZoom(source.getZoomFactor());
    }

    /**
     *
     */
    public void dataChanged() {
        revalidate();
        repaint();
    }

    /**
     * @param element
     */
    public void elementGraphicsChanged(final ElementContainer element) {
        if (element != null) {
            element.getParent().validate();
            element.getParent().repaint();
        } else {
            revalidate();
            repaint();
        }
    }

    /**
     *
     */
    public void layoutChanged() {
        setPageSize(doc.getPageWidth(), doc.getPageHeight());
    }

    /**
     * @param element
     */
    public void elementAdded(final ElementContainer element) {
        if (element != null) {
            element.getParent().validate();
            element.getParent().repaint();
        } else {
            revalidate();
            repaint();
        }
    }

    /**
     *
     */
    public void elementDeleted() {
        revalidate();
        repaint();
    }

    /**
     *
     */
    public void groupOrderChanged() {
        revalidate();
        repaint();
    }

    /**
     *
     */
    public void activeLayerChanged() {
        revalidate();
        repaint();
    }

    /**
     *
     */
    public void colorsChanged() {
        revalidate();
        repaint();
    }

    /**
     *
     */
    public void selectionChanged() {
        revalidate();
        repaint();
    }

    /**
     * @return
     */
    public int getMultiViewPitchShift() {
        return isMultiViewEnabled() ? getPitchShift() : old_pitch_shift;
    }

    /**
     * @return
     */
    public int getMultiViewDegree() {
        return isMultiViewEnabled() ? getDegree() : old_degree;
    }

    /**
     * @param value
     */
    public void setMultiViewPitchShift(final int value) {
        if (isMultiViewEnabled()) {
            setInterLayerSpace(value);
        } else {
            old_pitch_shift = value;
        }
    }

    /**
     * @param value
     */
    public void setMultiViewDegree(final int value) {
        if (isMultiViewEnabled()) {
            setDegree(value);
        } else {
            old_degree = value;
        }
    }

}
