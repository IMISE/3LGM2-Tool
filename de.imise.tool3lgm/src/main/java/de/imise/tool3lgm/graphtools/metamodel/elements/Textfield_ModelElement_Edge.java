package de.imise.tool3lgm.graphtools.metamodel.elements;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_ONE;
import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNIMITED;

import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;

/**
 * @author AXS (10.02.2017)
 */
public class Textfield_ModelElement_Edge extends SimpleEdge {

    public static final Class<? extends ModelElement> stcl = Textfield.class;

    public static final EdgeCardinality scard = ZERO_ONE;

    public static final EdgeCardinality ecard = ZERO_UNIMITED;

    public static final Class<? extends ModelElement> etcl = ModelElement.class;

}
