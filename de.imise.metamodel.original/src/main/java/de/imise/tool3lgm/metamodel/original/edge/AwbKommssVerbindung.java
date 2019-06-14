package de.imise.tool3lgm.metamodel.original.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNLIMITED;

import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.elements.CompositionEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.metamodel.original.node.Anwendungsbaustein;
import de.imise.tool3lgm.metamodel.original.node.Schnittstelle;

/**
 * @author Thomas (16.01.2004)
 */
public final class AwbKommssVerbindung extends CompositionEdge {

    public static final Class<? extends ModelElement> STCL = Anwendungsbaustein.class;

    public static final EdgeCardinality ECARD = ZERO_UNLIMITED;

    public static final Class<? extends ModelElement> ETCL = Schnittstelle.class;

}
