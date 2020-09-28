package de.imise.tool3lgm.metamodel.original.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNLIMITED;

import de.imise.tool3lgm.graphtools.dialog.element.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.metamodel.original.node.Bausteinschnittstelle;

/**
 * @author Thomas (16.01.2004)
 */
public final class KommBeziehung extends DoubleMeaningEdge {

    public static final Class<? extends ModelElement> STCL = Bausteinschnittstelle.class;

    public static final EdgeCardinality SCARD = ZERO_UNLIMITED;

    public static final EdgeCardinality ECARD = ZERO_UNLIMITED;

    public static final Class<? extends ModelElement> ETCL = Bausteinschnittstelle.class;

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addEdgePanel(KommbezEtntVerbindung.class);
        return dialog;
    }

}