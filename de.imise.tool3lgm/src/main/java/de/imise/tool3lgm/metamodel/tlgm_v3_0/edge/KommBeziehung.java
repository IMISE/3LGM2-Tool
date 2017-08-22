/*
 * Created on 12.01.2004 To change the template for this generated file go to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package de.imise.tool3lgm.metamodel.tlgm_v3_0.edge;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.elements.Edge;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Bausteinschnittstelle;

/**
 * @author thomas
 */
public final class KommBeziehung extends Edge {

    public static final Class<? extends ModelElement> stcl = Bausteinschnittstelle.class;

    public static final int[] scard = {
            ModelConstants.ZERO,
            ModelConstants.UNLIMITED
    };

    public static final int[] ecard = {
            ModelConstants.ZERO,
            ModelConstants.UNLIMITED
    };

    public static final Class<? extends ModelElement> etcl = Bausteinschnittstelle.class;

    public KommBeziehung() {
        super();
    }

    /**
     * @param knot1
     * @param knot2
     */
    public KommBeziehung(final ModelElement knot1, final ModelElement knot2) {
        super(knot1, knot2);
    }

    @Override
    public int layerFor() {
        return ModelConstants.LOGICAL_LAYER;
    }

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addEdgePanel(KommbezEtntVerbindung.class);
        return dialog;
    }

}
