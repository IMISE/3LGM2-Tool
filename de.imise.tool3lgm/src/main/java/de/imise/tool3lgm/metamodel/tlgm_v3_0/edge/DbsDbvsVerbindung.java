package de.imise.tool3lgm.metamodel.tlgm_v3_0.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_ONE;
import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNLIMITED;

import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.SimpleEdge;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.DBVerwaltungssystem;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Datenbanksystem;

/**
 * @author Thomas (16.01.2004)
 */
public final class DbsDbvsVerbindung extends SimpleEdge {

    public static final Class<? extends ModelElement> STCL = Datenbanksystem.class;

    public static final EdgeCardinality SCARD = ZERO_UNLIMITED;

    public static final EdgeCardinality ECARD = ZERO_ONE;

    public static final Class<? extends ModelElement> ETCL = DBVerwaltungssystem.class;

}
