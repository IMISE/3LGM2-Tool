package de.imise.tool3lgm.metamodel.service.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNLIMITED;

import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.elements.BooleanAttributeEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.metamodel.service.node.ApplicationComponent;
import de.imise.tool3lgm.metamodel.service.node.ObjectType;

/**
 * @author AXS (07.01.2018)
 */
public final class ApplicationComponent_ObjectType_Edge extends BooleanAttributeEdge {

    public static final Class<? extends ModelElement> STCL = ApplicationComponent.class;

    public static final EdgeCardinality SCARD = ZERO_UNLIMITED;

    public static final EdgeCardinality ECARD = ZERO_UNLIMITED;

    public static final Class<? extends ModelElement> ETCL = ObjectType.class;

    public ApplicationComponent_ObjectType_Edge() {
        super("StorageEdge_isMaster_Attribute", 1);
    }

}