package de.imise.tool3lgm.metamodel.tlgm_v3_0.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNIMITED;

import de.imise.tool3lgm.graphtools.metamodel.Edge;
import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.Node;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Aufgabe;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Objekttyp;

public final class AufObjVerbindung extends Edge {

    public static final Class<? extends ModelElement> stcl = Aufgabe.class;

    public static final EdgeCardinality scard = ZERO_UNIMITED;

    public static final EdgeCardinality ecard = ZERO_UNIMITED;

    public static final Class<? extends ModelElement> etcl = Objekttyp.class;

    public AufObjVerbindung() {
    }

    public AufObjVerbindung(final Node k1, final Node k2) {
        super(k1, k2);
    }

    public AufObjVerbindung(final Node k1, final Node k2, final boolean registerInKnots) {
        super(k1, k2, registerInKnots);
    }

}
