package de.imise.tool3lgm.graphtools.dialog.panel;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.ModelElement;

public abstract class ConnectedElementsPanel extends ElementDialogPanel {

    /**
     * Die Elementklasse die im Panel angezeigt wird.
     */
    protected Class<? extends ModelElement> searchElementClass;

    public ConnectedElementsPanel(final ElementPropertyDialog dialog) {
        this(dialog, null, null);
    }

    public ConnectedElementsPanel(final ElementPropertyDialog dialog, final String name) {
        this(dialog, name, null);
    }

    public ConnectedElementsPanel(final ElementPropertyDialog dialog, final Class<? extends ModelElement> searchElementClass) {
        this(dialog, null, searchElementClass);
    }

    public ConnectedElementsPanel(final ElementPropertyDialog dialog, final String name, final Class<? extends ModelElement> searchElementClass) {
        super(dialog, name);
        this.searchElementClass = searchElementClass;
    }

}
