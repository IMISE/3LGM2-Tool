package de.imise.tool3lgm.metamodel.original.edge;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.metamodel.original.node.Dokumentensammlung;
import de.imise.tool3lgm.metamodel.original.node.Dokumententyp;

/**
 * @author Thomas (16.01.2004)
 */
public final class DoksDokVerbindung extends LogspReprVerbindung {

    public static final Class<? extends ModelElement> STCL = Dokumentensammlung.class;

    public static final Class<? extends ModelElement> ETCL = Dokumententyp.class;

}
