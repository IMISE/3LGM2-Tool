package de.imise.tool3lgm.graphtools.elements.edge;

import de.imise.tool3lgm.graphtools.elements.Doppelkante;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.node.Aufgabe;
import de.imise.tool3lgm.graphtools.elements.node.Objekttyp;

public final class AufObjVerbindung extends Doppelkante {

    // public static final Class[] stcl = {Aufgabe.class};
    public static final Class<? extends ModelElement> stcl = Aufgabe.class;
    public static final int[] scard = {
            ModelConstants.ZERO, ModelConstants.UNLIMITED
    };

    public static final int[] ecard = {
            ModelConstants.ZERO, ModelConstants.UNLIMITED
    };
    public static final Class<? extends ModelElement> etcl = Objekttyp.class;

    // public static final Class[] etcl = {Objekttyp.class};

    // private static Object[][] stcl = {{Aufgabe.class, ModelConstants.ZERO, ModelConstants.UNLIMITED}};
    // private static Object[][] etcl = {{Objekttyp.class, ModelConstants.ZERO, ModelConstants.UNLIMITED}};

    public AufObjVerbindung() {
        super();
    }

    public AufObjVerbindung(final Knoten k1, final Knoten k2) {
        super(k1, k2);
    }

    public AufObjVerbindung(final Knoten k1, final Knoten k2, final boolean registerInKnots) {
        super(k1, k2, registerInKnots);
    }

    @Override
    public int layerFor() {
        return ModelConstants.DOMAIN_LAYER;
    }

}
