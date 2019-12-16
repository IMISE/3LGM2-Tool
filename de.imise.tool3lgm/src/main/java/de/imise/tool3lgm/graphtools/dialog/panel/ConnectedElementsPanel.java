package de.imise.tool3lgm.graphtools.dialog.panel;

import de.imise.tool3lgm.graphtools.dialog.AbstractElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;

public abstract class ConnectedElementsPanel extends ElementDialogPanel {

    /**
     * Die Elementklasse die im Panel angezeigt wird.
     */
    protected Class<? extends ModelElement> searchElementClass;

    public ConnectedElementsPanel(final AbstractElementPropertyDialog dialog) {
        this(dialog, null, null);
    }

    public ConnectedElementsPanel(final AbstractElementPropertyDialog dialog, final String name) {
        this(dialog, name, null);
    }

    public ConnectedElementsPanel(final AbstractElementPropertyDialog dialog, final Class<? extends ModelElement> searchElementClass) {
        this(dialog, null, searchElementClass);
    }

    public ConnectedElementsPanel(final AbstractElementPropertyDialog dialog, final String name, final Class<? extends ModelElement> searchElementClass) {
        super(dialog, name);
        this.searchElementClass = searchElementClass;
    }

}
