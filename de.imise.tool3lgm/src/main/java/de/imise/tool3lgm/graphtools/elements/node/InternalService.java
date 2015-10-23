package de.imise.tool3lgm.graphtools.elements.node;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.NConnectionPanel;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.log.Log;

public final class InternalService extends Knoten {

    /**
     * COMMENTME
     */
    @SuppressWarnings({
        "rawtypes"
    })
    public static final Class[] COPY_DEPENDENCY = {
        AufOrgKombination.class,
    };

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends ModelElement>[] getCopyDependencies() {
        return COPY_DEPENDENCY;
    }

    /**
     * 
     */
    public InternalService() {
        super();
    }

    @Override
    public int layerFor() {
        return ModelConstants.DOMAIN_LAYER;
    }

    @Override
    public Object clone() {
        InternalService retVal;
        try {
            retVal = (InternalService) super.clone();
        } catch (Exception e) {
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
            return null;
        }
        return retVal;
    }

    @Override
    public ElementPropertyDialog createPropertyDialog(final GDCollection gdcoll) {
        ElementPropertyDialog dialog = new ElementPropertyDialog(this, gdcoll);
        //        dialog.addTab(getResString("strukt"), new StructurePanel(dialog));
        dialog.addTab(getResString("ExternalService_p"), new NConnectionPanel(ExternalService.class, dialog, true, true));
        return dialog;
    }

    @Override
    public boolean hasLayout() {
        return true;
    }

    @Override
    public boolean hasSortedKanten() {
        return false;
    }
}
