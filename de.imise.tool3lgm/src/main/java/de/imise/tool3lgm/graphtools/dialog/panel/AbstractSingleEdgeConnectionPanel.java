package de.imise.tool3lgm.graphtools.dialog.panel;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;

/**
 * Panel für alle einfachen Verbindungen zwischen 2 Elementen
 *
 * @author AXS
 * @created 24.04.2017
 */
public abstract class AbstractSingleEdgeConnectionPanel extends LGMDragNDropPanel {

    /** Die Kantenklasse zum anderen Element */
    protected final Class<? extends Kante> edgeClass;

    protected final Class<? extends ModelElement> searchElementClass;

    protected final boolean edgeIsForward;

    /**
     * Panel für eine einfache Assoziation
     *
     * @param edgeClass
     * @param dialog
     */
    public AbstractSingleEdgeConnectionPanel(final Class<? extends Kante> edgeClass, final ElementPropertyDialog dialog) {
        super(dialog);
        this.edgeClass = edgeClass;
        edgeIsForward = Kante.isStartClass(edgeClass, dialog.getModelElement().getClass());
        searchElementClass = edgeIsForward ? Kante.getEndClass(edgeClass) : Kante.getStartClass(edgeClass);
        setName(ModelConstants.getDisplayableName(searchElementClass));
    }

}
