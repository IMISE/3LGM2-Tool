package de.imise.tool3lgm.metamodel.tlgm_service.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNLIMITED;

import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.elements.BooleanAttributeEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.metamodel.tlgm_service.node.ApplicationComponent;
import de.imise.tool3lgm.metamodel.tlgm_service.node.ObjectType;

/**
 * @author AXS (07.01.2018)
 */
public final class StorageLink_Edge extends BooleanAttributeEdge {

    public static final Class<? extends ModelElement> stcl = ApplicationComponent.class;

    public static final EdgeCardinality scard = ZERO_UNLIMITED;

    public static final EdgeCardinality ecard = ZERO_UNLIMITED;

    public static final Class<? extends ModelElement> etcl = ObjectType.class;

    public StorageLink_Edge() {
        super("StorageEdge_isMaster_Attribute", 1);
    }

}