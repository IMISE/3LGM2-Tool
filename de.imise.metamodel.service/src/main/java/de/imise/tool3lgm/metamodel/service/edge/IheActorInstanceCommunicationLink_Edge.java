package de.imise.tool3lgm.metamodel.service.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNLIMITED;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.SimpleEdge;
import de.imise.tool3lgm.metamodel.service.node.InvokingInterface;
import de.imise.tool3lgm.metamodel.service.node.ProvidingInterface;

/**
 * @author AXS (11.01.2017)
 */
public final class IheActorInstanceCommunicationLink_Edge extends SimpleEdge {

    public static final Class<? extends ModelElement> STCL = InvokingInterface.class;

    public static final EdgeCardinality SCARD = ZERO_UNLIMITED;

    public static final EdgeCardinality ECARD = ZERO_UNLIMITED;

    public static final Class<? extends ModelElement> ETCL = ProvidingInterface.class;

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        //da es fast niemals mehr als 1 bis 2 Tansaktionen sind, die man nicht ändern kann, reicht hier eine Liste auf dem DescripPanel
        dialog.addDescripPanel(IheCommunicationLink_IheActorInstanceCommunicationLink_Edge.class, IheTransaction_IheCommunicationLink_Edge.class);

        return dialog;
    }

}
