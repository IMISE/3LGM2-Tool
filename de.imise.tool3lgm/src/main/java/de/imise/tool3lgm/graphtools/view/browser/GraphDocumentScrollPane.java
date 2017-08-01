/*
 * Created on 09.08.2004
 */
package de.imise.tool3lgm.graphtools.view.browser;

import javax.swing.JScrollPane;

import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.GraphDocumentOwner;

/**
 * @author imi0wendt
 */
public class GraphDocumentScrollPane extends JScrollPane implements GraphDocumentOwner {

    /**
     * COMMENTME
     */
    private GraphDocument doc = null;

    public GraphDocumentScrollPane(final GraphDocument doc) {
        this.doc = doc;
    }

    @Override
    public GraphDocument getGraphDocument() {
        return doc;
    }

    @Override
    public GDCollection getCollection() {
        return doc == null ? null : doc.getCollection();
    }

}
