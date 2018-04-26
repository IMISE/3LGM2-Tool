package de.imise.tool3lgm.gui;

import java.awt.Rectangle;

import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.GraphDocumentListener;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.undoredo.InTransactionListener;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;

/**
 * Abstrakte Klasse für alle internen Fenster zur Darstellung von (Teil-)
 * Modellen
 *
 * @author Thomas Rudert
 */
public abstract class AbstractInternalFrame extends JInternalFrame implements GraphDocumentListener, InTransactionListener {
    /** darzustellendes (Teil-)Modell */
    protected LGMGraphDocument doc;

    /** die JScrollFläche für den Inhalt */
    protected JScrollPane scrollPane;

    /**
     * Konstruktor
     *
     * @param _graphDocument darzustellendes (Teil-)Modell
     * @param _title Fensterüberschrift
     */
    public AbstractInternalFrame(final LGMGraphDocument _graphDocument, final String _title) {

        /* JInternalFrame mit Titel, resizable, closable, maximizable, and iconifiable */
        super(_title, true, false, true, true);

        doc = _graphDocument;
        scrollPane = new JScrollPane();
        getContentPane().add(scrollPane);

        setFrameIcon(Tool3lgmConstants.getIcon("toolIcon.gif"));

        doc.addGraphDocumentListener(this);
        doc.addInTransactionListener(this);
    }

    @Override
    public Rectangle getNormalBounds() {
        if (getDesktopPane() == null) {
            return new Rectangle(0, 0);
        }
        return getDesktopPane().getBounds();
    }

    /**
     * @return
     */
    public GDCollection getCollection() {
        return getGraphDocument().getCollection();
    }

    /**
     * gibt das darzustellende (Teil-)Modell zurück
     *
     * @return GraphDocument
     */
    public LGMGraphDocument getGraphDocument() {
        return doc;
    }

    /**
     * gibt den scrollbaren Bereich zurück
     *
     * @return JScrollPane
     */
    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    @Override
    public void dataChanged(final GraphDocument source) {
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

    @Override
    public void dataChanged(final GraphDocument source, final int pid) {
        dataChanged(source);
    }

    /**
     * COMMENTME
     */
    double oldzoom = 0;

    /**
     * COMMENTME
     */
    int oldwidth = 0;

    /**
     * COMMENTME
     */
    int oldheight = 0;

    @Override
    public void dispose() {
        doc.removeGraphDocumentListener(this);
        doc.removeInTransactionListener(this);
        super.dispose();
    }

}