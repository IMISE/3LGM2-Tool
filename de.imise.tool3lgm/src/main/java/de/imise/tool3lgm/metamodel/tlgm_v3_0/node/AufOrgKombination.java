package de.imise.tool3lgm.metamodel.tlgm_v3_0.node;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AufAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.OrgAufOrgVerbindung;

/**
 * @author N.N.
 * @create Long time ago
 */
public class AufOrgKombination extends Knoten {

    /**
     * COMMENTME
     */
    @SuppressWarnings({
            "rawtypes"
    })
    public static final Class[] COPY_DEPENDENCY = {
            ABKonfiguration.class,
            Organisationseinheit.class,
    };

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends ModelElement>[] getCopyDependencies() {
        return COPY_DEPENDENCY;
    }

    /**
     *
     */
    public AufOrgKombination() {
        super();
    }

    @Override
    public int layerFor() {
        return ModelConstants.DOMAIN_LAYER;
    }

    @Override
    public String toString() {
        StringBuilder retVal = new StringBuilder(Tool3lgmConstants.getResString("in_oes"));
        retVal.append(": ");
        boolean first = true;
        for (ModelElement oe : getConnectedElements(Organisationseinheit.class)) {
            if (!first) {
                retVal.append(", ");
            } else {
                first = false;
            }
            retVal.append(oe);
        }
        return retVal.toString();
    }

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addPathConnectionPanel(OrgAufOrgVerbindung.class);
        dialog.addPathConnectionPanel(AufAufOrgVerbindung.class);
        return dialog;
    }

}
