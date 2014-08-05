package de.imise.tool3lgm.graphtools.elements.node;

import de.imise.tool3lgm.graphtools.elements.ModelElement;

/**
 * @author Thomas Rudert
 */
public final class EreignisDokumentenTyp extends EtntEtdtKombination {

    /**
     * COMMENTME
     */
    @SuppressWarnings("rawtypes")
    public static final Class[] COPY_DEPENDENCY = {
            Ereignistyp.class, Dokumententyp.class, Kommunikationsstandard.class,
    };

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends ModelElement>[] getCopyDependencies() {
        return COPY_DEPENDENCY;
    }

    /**
	 * 
	 */
    public EreignisDokumentenTyp() {
        super();
    }

}
