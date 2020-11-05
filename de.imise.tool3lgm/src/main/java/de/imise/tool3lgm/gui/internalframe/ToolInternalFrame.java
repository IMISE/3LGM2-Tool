package de.imise.tool3lgm.gui.internalframe;

import java.awt.Rectangle;
import java.beans.PropertyVetoException;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMChangeListenerSimple;
import de.imise.tool3lgm.gui.viewpane.ViewPane;
import de.imise.tool3lgm.gui.viewpane.ViewPaneFrameComponent;

/**
 * Abstrakte Klasse für alle internen Fenster zur Darstellung von
 * (Teil-)Modellen
 *
 * @author Thomas Rudert
 */
public abstract class ToolInternalFrame extends JInternalFrame implements ViewPaneFrameComponent, LGMChangeListenerSimple {

    /** the view to display */
    protected final ViewPane viewPane;

    /**
     * Konstruktor
     *
     * @param viewPane the view to display
     */
    public ToolInternalFrame(final ViewPane viewPane) {
        /*
         * JInternalFrame mit Titel, resizable, closable, maximizable, and
         * iconifiable
         */
        super("", true, false, true, true);
        this.viewPane = viewPane;
        getContentPane().add(viewPane);
        setFrameIcon(Tool3lgmConstants.TOOL_ICON_16);
        GraphDocument doc = getGraphDocument();
        doc.addAllTransactionsListener(this);
    }

    @Override
    public Rectangle getNormalBounds() {
        JDesktopPane desktopPane = getDesktopPane();
        if (desktopPane == null) {
            return new Rectangle(0, 0);
        }
        return desktopPane.getBounds();
    }

    @Override
    public ViewPane getViewPane() {
        return viewPane;
    }

    /**
     * gibt den scrollbaren Bereich zurück
     *
     * @return JScrollPane
     */
    public JScrollPane getScrollPane() {
        return viewPane.getScrollPane();
    }

    /**
     * @return the viewport of the scrollpane
     */
    public JViewport getViewport() {
        JScrollPane scrollPane = getScrollPane();
        return scrollPane.getViewport();
    }

    @Override
    public final void modelOrSzenarioNameChanged(final GraphDocument source) {
        updateTitle();
    }

    /**
     * Sets the frame title
     */
    public final void updateTitle() {
        String fullName = viewPane.getFullName();
        setTitle(fullName);
    }

    @Override
    public void szenarioRemoved(final GraphDocument source) {
        if (source == getGraphDocument()) {
            dispose();
        }
    }

    @Override
    public void dispose() {
        GraphDocument doc = getGraphDocument();
        doc.removeAllTransactionsListener(this);
        super.dispose();
    }

    @Override
    public String toString() {
        GraphDocument doc = getGraphDocument();
        return getClass().getName() + " " + doc;
    }

    @Override
    public final void setSelected(final boolean selected) {
        try {
            super.setSelected(selected);
        } catch (PropertyVetoException e) {
            //ignore the possible error
        }
    }

}