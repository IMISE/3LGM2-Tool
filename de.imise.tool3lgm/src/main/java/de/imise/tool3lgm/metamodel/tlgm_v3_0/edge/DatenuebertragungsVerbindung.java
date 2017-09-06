package de.imise.tool3lgm.metamodel.tlgm_v3_0.edge;

import de.imise.tool3lgm.graphtools.elements.Edge;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.Node;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.PhysischerDVBaustein;

public final class DatenuebertragungsVerbindung extends Edge {

    public static final Class<? extends ModelElement> stcl = PhysischerDVBaustein.class;

    public static final int[] scard = {
            ModelConstants.ZERO,
            ModelConstants.UNLIMITED
    };

    public static final int[] ecard = {
            ModelConstants.ZERO,
            ModelConstants.UNLIMITED
    };

    public static final Class<? extends ModelElement> etcl = PhysischerDVBaustein.class;

    public DatenuebertragungsVerbindung() {
        super();
    }

    public DatenuebertragungsVerbindung(final Node k1, final Node k2) {
        super(k1, k2);
    }

}