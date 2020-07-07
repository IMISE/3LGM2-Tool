package de.imise.tool3lgm.metamodel.service.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNLIMITED;

import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.SimpleEdge;
import de.imise.tool3lgm.metamodel.service.node.IheActor;
import de.imise.util.Sys;

/**
 * @author AXS (19.03.2020)
 */
public class IheActor_IheActor_MustBeGroupedWith_Edge extends SimpleEdge {

    public static final Class<? extends ModelElement> STCL = IheActor.class;

    public static final EdgeCardinality SCARD = ZERO_UNLIMITED;

    public static final EdgeCardinality ECARD = ZERO_UNLIMITED;

    public static final Class<? extends ModelElement> ETCL = IheActor.class;

    public IheActor_IheActor_MustBeGroupedWith_Edge() {
        Sys.err("IheActor_IheActor_MustBeGroupedWith_Edge: " + this);
    }

}
