package de.imise.tool3lgm.metamodel.tlgm_v3_0.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ONE_ONE;

import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.elements.Composition;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.KonAnwendungsbaustein;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Organisationsplan;

/**
 * @author Thomas (16.01.2004)
 */
public final class KawbOrgpVerbindung extends Composition {

    public static final Class<? extends ModelElement> stcl = KonAnwendungsbaustein.class;

    public static final EdgeCardinality ecard = ONE_ONE;

    public static final Class<? extends ModelElement> etcl = Organisationsplan.class;

}
