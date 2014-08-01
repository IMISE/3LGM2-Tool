/*
 * Created on 09.08.2004
 *
 */
package de.imise.tool3lgm.graphtools.view.browser;

import javax.swing.JScrollPane;

import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.GraphDocumentOwner;

/**
 * @author imi0wendt
 */
public class GraphDocumentScrollPane extends JScrollPane implements GraphDocumentOwner{
	
	/**
	 * COMMENTME
	 */
	private GraphDocument doc = null;
	
	public GraphDocumentScrollPane (GraphDocument doc) {
		this.doc = doc;
	}
	
	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.GraphDocumentOwner#getGraphDocument()
	 */
    @Override
	public GraphDocument getGraphDocument() {
		return doc;
	}

    /* (non-Javadoc)
     * @see tool3lgm.graphtools.GDCollectionOwner#getCollection()
     */
    @Override
    public GDCollection getCollection() {
	    return doc==null?null:doc.getCollection();
    }
	

}
