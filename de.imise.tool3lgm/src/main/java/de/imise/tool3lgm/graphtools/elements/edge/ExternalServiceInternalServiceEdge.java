package de.imise.tool3lgm.graphtools.elements.edge;

import de.imise.tool3lgm.graphtools.elements.Doppelkante;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.node.ExternalService;
import de.imise.tool3lgm.graphtools.elements.node.InternalService;

public class ExternalServiceInternalServiceEdge extends Doppelkante {

    public static final Class<? extends ModelElement> stcl = ExternalService.class;
    public static final int[] scard = {
            ModelConstants.ZERO, ModelConstants.UNLIMITED
    };

    public static final int[] ecard = {
            ModelConstants.ZERO, ModelConstants.UNLIMITED
    };
    public static final Class<? extends ModelElement> etcl = InternalService.class;

    /**
     * 
     */
    public ExternalServiceInternalServiceEdge() {
        super();
    }

    /**
     * @param knot1
     * @param knot2
     */
    public ExternalServiceInternalServiceEdge(final ModelElement knot1, final ModelElement knot2) {
        super(knot1, knot2);
    }

    /**
     * @param knot1
     * @param knot2
     * @param registerInKnots
     */
    public ExternalServiceInternalServiceEdge(final ModelElement knot1, final ModelElement knot2, final boolean registerInKnots) {
        super(knot1, knot2, registerInKnots);
    }

    @Override
    public int layerFor() {
        return ModelConstants.DOMAIN_LAYER;
    }

}
