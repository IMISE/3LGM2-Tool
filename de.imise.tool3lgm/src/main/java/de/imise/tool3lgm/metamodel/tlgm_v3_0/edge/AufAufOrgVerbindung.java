package de.imise.tool3lgm.metamodel.tlgm_v3_0.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ONE_ONE;
import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNIMITED;

import de.imise.tool3lgm.graphtools.metamodel.Edge;
import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.AufOrgKombination;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Aufgabe;

/**
 * @author Thomas (16.01.2004)
 */
public final class AufAufOrgVerbindung extends Edge {

    public static final Class<? extends ModelElement> stcl = Aufgabe.class;

    public static final EdgeCardinality scard = ONE_ONE;

    public static final EdgeCardinality ecard = ZERO_UNIMITED;

    public static final Class<? extends ModelElement> etcl = AufOrgKombination.class;

    public AufAufOrgVerbindung() {
    }

    public AufAufOrgVerbindung(final ModelElement knot1, final ModelElement knot2) {
        super(knot1, knot2);
    }

    public AufAufOrgVerbindung(final ModelElement knot1, final ModelElement knot2, final boolean registerInKnots) {
        super(knot1, knot2, registerInKnots);
    }

}
