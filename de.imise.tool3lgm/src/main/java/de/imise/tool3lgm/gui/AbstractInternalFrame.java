package de.imise.tool3lgm.gui;

import java.awt.Rectangle;

import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMChangeListenerSimple;

/**
 * Abstrakte Klasse für alle internen Fenster zur Darstellung von (Teil-)Modellen
 *
 * @author Thomas Rudert
 */
public abstract class AbstractInternalFrame extends JInternalFrame implements LGMChangeListenerSimple {

    /** darzustellendes (Teil-)Modell */
    protected final GraphDocument doc;

    /** die JScrollFläche für den Inhalt */
    protected JScrollPane scrollPane;

    /**
     * Konstruktor
     *
     * @param doc darzustellendes (Teil-)Modell
     */
    public AbstractInternalFrame(final GraphDocument doc) {

        /* JInternalFrame mit Titel, resizable, closable, maximizable, and iconifiable */
        super("", true, false, true, true);

        this.doc = doc;
        scrollPane = new JScrollPane();
        getContentPane().add(scrollPane);

        setFrameIcon(Tool3lgmConstants.TOOL_ICON_16);

        doc.addAllTransactionsListener(this);
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
    public final GraphDocument getGraphDocument() {
        return doc;
    }

    /**
     * @param doc
     * @return <code>true</code> if the GraphDocument of this frame is teh same like the parameter.
     */
    public final boolean hasGraphDocument(final GraphDocument doc) {
        return this.doc == doc;
    }

    /**
     * gibt den scrollbaren Bereich zurück
     *
     * @return JScrollPane
     */
    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    /**
     * @return the viewport of the scrollpane
     */
    public JViewport getViewport() {
        return scrollPane.getViewport();
    }

    @Override
    public final void modelOrSzenarioNameChanged(final GraphDocument source) {
        updateTitle();
    }

    public abstract void updateTitle();

    @Override
    public void szenarioRemoved(final GraphDocument source) {
        if (source == getGraphDocument()) {
            dispose();
        }
    }

    @Override
    public void dispose() {
        doc.removeAllTransactionsListener(this);
        super.dispose();
    }

    @Override
    public String toString() {
        return getClass().getName() + " " + doc;
    }

}