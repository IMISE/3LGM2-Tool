package de.imise.tool3lgm.metamodel.tlgm_service.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNIMITED;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.metamodel.tlgm_service.node.ApplicationComponent;
import de.imise.tool3lgm.metamodel.tlgm_service.node.Use;

/**
 * @author AXS (31.12.2017)
 */
public final class SupportLink_Edge extends Edge {

    public static final Class<? extends ModelElement> stcl = ApplicationComponent.class;

    public static final EdgeCardinality scard = ZERO_UNIMITED;

    public static final EdgeCardinality ecard = ZERO_UNIMITED;

    public static final Class<? extends ModelElement> etcl = Use.class;

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addEdgePanel(OrganisationalUnit_SupportLink_Edge.class);
        dialog.addEdgePanel(ApplicationComponent_SupportLink_Edge.class);
        return dialog;
    }

}