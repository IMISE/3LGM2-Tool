package de.imise.tool3lgm.graphtools.elements.node;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.NConnectionPanel;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.log.Log;

public final class ExternalService extends Knoten {

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
    public ExternalService() {
        super();
    }

    @Override
    public int layerFor() {
        return ModelConstants.DOMAIN_LAYER;
    }

    @Override
    public Object clone() {
        ExternalService retVal;
        try {
            retVal = (ExternalService) super.clone();
        } catch (Exception e) {
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
            return null;
        }
        return retVal;
    }

    @Override
    public ElementPropertyDialog createPropertyDialog(final GDCollection gdcoll) {
        ElementPropertyDialog dialog = new ElementPropertyDialog(this, gdcoll);
        //dialog.addTab(getResString("strukt"), new StructurePanel(dialog));
        dialog.addTab(getResString("InternalService_p"), new NConnectionPanel(InternalService.class, dialog, true, true));
        dialog.addTab(getResString("Client_p"), new NConnectionPanel(Client.class, dialog, true, true));
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
