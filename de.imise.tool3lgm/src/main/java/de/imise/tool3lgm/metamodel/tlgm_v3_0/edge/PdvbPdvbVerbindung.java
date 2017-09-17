package de.imise.tool3lgm.metamodel.tlgm_v3_0.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNIMITED;

import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.PartOfBeziehung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.PhysischerDVBaustein;

/**
 * @author Thomas (16.01.2004)
 */
public class PdvbPdvbVerbindung extends PartOfBeziehung {

    public static final Class<? extends ModelElement> stcl = PhysischerDVBaustein.class;

    public static final EdgeCardinality scard = ZERO_UNIMITED;

    public static final EdgeCardinality ecard = ZERO_UNIMITED;

    public static final Class<? extends ModelElement> etcl = PhysischerDVBaustein.class;

}