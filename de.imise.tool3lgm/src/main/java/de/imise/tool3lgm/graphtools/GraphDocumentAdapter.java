/*
 * Created on 21.02.2008
 */
package de.imise.tool3lgm.graphtools;

import de.imise.tool3lgm.graphtools.view.container.ElementContainer;

/**
 * Abstracte Implementeirung von <code>GraphDocumentListener</code>. In keiner
 * der Methoden wird etwas ausgeführt.
 * 
 * @author AXS
 */
public abstract class GraphDocumentAdapter implements GraphDocumentListener {

	/**
	 * 
	 */
	public GraphDocumentAdapter() {
		super();
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.GraphDocumentListener#dataChanged(tool3lgm.graphtools.GraphDocument)
	 */
	@Override
	public void dataChanged(GraphDocument source) {
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.GraphDocumentListener#elementGraphicsChanged(tool3lgm.graphtools.GraphDocument, tool3lgm.graphtools.view.container.ElementContainer)
	 */
	@Override
	public void elementGraphicsChanged(GraphDocument source, ElementContainer element) {
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.GraphDocumentListener#layoutChanged(tool3lgm.graphtools.GraphDocument)
	 */
	@Override
	public void layoutChanged(GraphDocument source) {
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.GraphDocumentListener#elementAdded(tool3lgm.graphtools.GraphDocument, tool3lgm.graphtools.view.container.ElementContainer)
	 */
	@Override
	public void elementAdded(GraphDocument source, ElementContainer element) {
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.GraphDocumentListener#elementDeleted(tool3lgm.graphtools.GraphDocument, tool3lgm.graphtools.view.container.ElementContainer)
	 */
	@Override
	public void elementDeleted(GraphDocument source, ElementContainer element) {
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.GraphDocumentListener#groupOrderChanged(tool3lgm.graphtools.GraphDocument)
	 */
	@Override
	public void groupOrderChanged(GraphDocument source) {
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.GraphDocumentListener#activeLayerChanged(tool3lgm.graphtools.GraphDocument)
	 */
	@Override
	public void activeLayerChanged(GraphDocument source) {
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.GraphDocumentListener#colorsChanged(tool3lgm.graphtools.GraphDocument)
	 */
	@Override
	public void colorsChanged(GraphDocument source) {
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.GraphDocumentListener#selectionChanged(tool3lgm.graphtools.GraphDocument)
	 */
	@Override
	public void selectionChanged(GraphDocument source) {
	}

}
