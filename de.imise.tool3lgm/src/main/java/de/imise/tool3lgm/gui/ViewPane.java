package de.imise.tool3lgm.gui;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.util.swing.component.CenterableScrollPane;

/**
 * @author AXS (19.05.2020)
 */
public abstract class ViewPane extends JPanel implements ViewContainer {

    /**
     *
     */
    protected final GraphDocument doc;

    /**
     *
     */
    protected final Szenario szen;

    /**
     *
     */
    private final JScrollPane scrollPane;

    /**
    *
    */
    public ViewPane(final GraphDocument doc) {
        this(doc, true);
    }

    public ViewPane(final GraphDocument doc, final boolean insertScrollPanel) {
        this.doc = doc;
        szen = doc instanceof Szenario ? (Szenario) doc : null;
        scrollPane = insertScrollPanel ? new CenterableScrollPane() : null;
    }

    @Override
    public final GraphDocument getGraphDocument() {
        return doc;
    }

    @Override
    public final Szenario getSzenario() {
        return szen;
    }

    /**
     * @param view
     */
    protected final void setViewComponent(final Component view) {
        if (scrollPane == null) {
            add(view);
        } else {
            scrollPane.setViewportView(view);
        }
    }

    @Override
    public final JComponent getRealViewComponent() {
        return scrollPane != null ? scrollPane : this;
    }

    @Override
    public final JScrollPane getScrollPane() {
        return scrollPane;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (doc == null ? 0 : doc.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ViewPane other = (ViewPane) obj;
        if (doc == null) {
            if (other.doc != null) {
                return false;
            }
        } else if (!doc.equals(other.doc)) {
            return false;
        }
        return true;
    }

}
