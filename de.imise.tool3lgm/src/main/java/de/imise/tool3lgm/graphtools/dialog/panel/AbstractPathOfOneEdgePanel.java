package de.imise.tool3lgm.graphtools.dialog.panel;

import static de.imise.tool3lgm.graphtools.elements.Doppelkante.FORWARD;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelElement;

/**
 * Panel für alle einfachen Verbindungen zwischen 2 Elementen, also der Kantenpfad ist genau eine Kante lang.
 *
 * @author AXS
 * @created 24.04.2017
 */
public abstract class AbstractPathOfOneEdgePanel extends AbstractExpandablePanel {

    /** Die Kantenklasse zum anderen Element */
    protected final Class<? extends Kante> edgeClass;

    protected final boolean edgeIsForward;

    /**
     * Panel für eine einfache Assoziation
     *
     * @param dialog
     * @param searchElementClass
     * @param edgeClass
     */
    public AbstractPathOfOneEdgePanel(final ElementPropertyDialog dialog, final Class<? extends ModelElement> searchElementClass, final Class<? extends Kante> edgeClass) {
        this(dialog, false, searchElementClass, edgeClass);
    }

    /**
     * Panel für eine einfache Assoziation
     *
     * @param dialog
     * @param labelEdgeName
     * @param searchElementClass
     * @param edgeClass
     */
    public AbstractPathOfOneEdgePanel(final ElementPropertyDialog dialog, final boolean labelEdgeName, final Class<? extends ModelElement> searchElementClass, final Class<? extends Kante> edgeClass) {
        super(dialog, labelEdgeName, searchElementClass, edgeClass);
        this.edgeClass = edgeClass;
        edgeIsForward = directions[0] == FORWARD;
    }

}
