package de.imise.tool3lgm.graphtools.metamodel.elements;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNLIMITED;

import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;

/**
 * @author AXS (27.11.2022)
 */
public class Group_HasPartEdge extends PartitioningEdge {

    public static final Class<? extends ModelElement> STCL = Group.class;

    public static final EdgeCardinality SCARD = ZERO_UNLIMITED;

    public static final EdgeCardinality ECARD = ZERO_UNLIMITED;

    public static final Class<? extends ModelElement> ETCL = Node.class;

}
