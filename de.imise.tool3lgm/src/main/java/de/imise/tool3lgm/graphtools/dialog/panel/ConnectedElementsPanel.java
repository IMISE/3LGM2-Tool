package de.imise.tool3lgm.graphtools.dialog.panel;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.elements.ModelElement;

public abstract class ConnectedElementsPanel extends ElementDialogPanel {

    /**
     * Die Elementklasse die im Panel angezeigt wird. Das muss nicht das Ende des durch die edgeClasses vorgegebenen Pfades
     * sein, sondern kann auch eine Kante in der Mitte sein.
     *
     * @see #searchEdgeIndex
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
