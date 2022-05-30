package de.imise.tool3lgm.gui.viewpane;

import java.awt.BorderLayout;
import java.util.Objects;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.GraphDocumentOwner;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.util.swing.component.CenterableScrollPane;

/**
 * @author AXS (19.05.2020)
 */
public abstract class ViewPane extends JPanel implements GraphDocumentOwner {

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
    protected final JScrollPane scrollPane;

    /**
     * @param doc
     */
    public ViewPane(final GraphDocument doc) {
        this.doc = doc;
        szen = doc instanceof Szenario ? (Szenario) doc : null;
        scrollPane = new CenterableScrollPane();
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    public final GraphDocument getGraphDocument() {
        return doc;
    }

    public final Szenario getSzenario() {
        return szen;
    }

    public final JScrollPane getScrollPane() {
        return scrollPane;
    }

    @Override
    public int hashCode() {
        return Objects.hash(doc);
    }

    /**
     * @return
     */
    public abstract String getFullName();

    @Override
    public String getName() {
        return doc.getName();
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
        if (!Objects.equals(doc, other.doc)) {
            return false;
        }
        return true;
    }

    /**
     * @return if this is a panel that contains an other context compoenent then
     *         this function can return the 'real' content component.
     */
    public JComponent getContentComponent() {
        return this;
    }

}
