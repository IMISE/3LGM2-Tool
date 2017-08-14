package de.imise.tool3lgm.metamodel.tlgm_v3_0.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.PdvbSubnVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.SubnNetzpVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.SubnNetztVerbindung;

public final class Subnetz extends Knoten {

    /**
     * COMMENTME
     */
    @SuppressWarnings("rawtypes")
    public static final Class[] COPY_DEPENDENCY = {
            Netzprotokoll.class,
    };

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends ModelElement>[] getCopyDependencies() {
        return COPY_DEPENDENCY;
    }

    /**
     *
     */
    public Subnetz() {
        super();
    }

    @Override
    public int layerFor() {
        return ModelConstants.PHYSICAL_LAYER;
    }

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addPathConnectionPanel(PdvbSubnVerbindung.class);
        dialog.addPathConnectionPanel(SubnNetztVerbindung.class);
        dialog.addPathConnectionPanel(SubnNetzpVerbindung.class);
        return dialog;
    }

    @Override
    public boolean avoidDuplicates() {
        return true;
    }
}
