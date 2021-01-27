package de.imise.tool3lgm.metamodel.original.edge;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.metamodel.original.node.Datenbanksystem;
import de.imise.tool3lgm.metamodel.original.node.Datensatztyp;

/**
 * @author Thomas (16.01.2004)
 */
public final class DbsDatVerbindung extends LogspReprVerbindung {

    public static final Class<? extends ModelElement> STCL = Datenbanksystem.class;

    public static final Class<? extends ModelElement> ETCL = Datensatztyp.class;

}
