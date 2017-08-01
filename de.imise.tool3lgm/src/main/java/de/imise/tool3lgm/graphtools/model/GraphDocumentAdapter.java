/*
 * Created on 21.02.2008
 */
package de.imise.tool3lgm.graphtools.model;

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
    public void elementAdded(final GraphDocument source, final ElementContainer element) {
    }

    @Override
    public void elementDeleted(final GraphDocument source, final ElementContainer element) {
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

}
