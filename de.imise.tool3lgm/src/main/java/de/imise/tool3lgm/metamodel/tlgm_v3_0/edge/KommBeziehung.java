package de.imise.tool3lgm.metamodel.tlgm_v3_0.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNIMITED;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.Edge;
import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Bausteinschnittstelle;

/**
 * @author Thomas (16.01.2004)
 */
public final class KommBeziehung extends Edge {

    public static final Class<? extends ModelElement> stcl = Bausteinschnittstelle.class;

    public static final EdgeCardinality scard = ZERO_UNIMITED;

    public static final EdgeCardinality ecard = ZERO_UNIMITED;

    public static final Class<? extends ModelElement> etcl = Bausteinschnittstelle.class;

    public KommBeziehung() {
    }

    public KommBeziehung(final ModelElement knot1, final ModelElement knot2) {
        super(knot1, knot2);
    }

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addEdgePanel(KommbezEtntVerbindung.class);
        return dialog;
    }

}
