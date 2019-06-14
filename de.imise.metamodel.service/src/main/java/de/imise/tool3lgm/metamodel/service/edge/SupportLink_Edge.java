package de.imise.tool3lgm.metamodel.service.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNLIMITED;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.SimpleEdge;
import de.imise.tool3lgm.metamodel.service.node.ApplicationComponent;
import de.imise.tool3lgm.metamodel.service.node.Use;

/**
 * @author AXS (31.12.2017)
 */
public final class SupportLink_Edge extends SimpleEdge {

    public static final Class<? extends ModelElement> STCL = ApplicationComponent.class;

    public static final EdgeCardinality SCARD = ZERO_UNLIMITED;

    public static final EdgeCardinality ECARD = ZERO_UNLIMITED;

    public static final Class<? extends ModelElement> ETCL = Use.class;

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addEdgePanel(OrganisationalUnit_SupportLink_Edge.class);
        dialog.addEdgePanel(ApplicationComponent_SupportLink_Edge.class);
        return dialog;
    }

}