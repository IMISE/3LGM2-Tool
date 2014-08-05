package de.imise.tool3lgm.graphtools.elements.node;

import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.NConnectionPanel;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;

public final class Datenbanksystem extends LogischerSpeicher {

    /**
     * COMMENTME
     */
    @SuppressWarnings("rawtypes")
    public static final Class[] COPY_DEPENDENCY = {
            DBVerwaltungssystem.class, Datensatztyp.class,
    };

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends ModelElement>[] getCopyDependencies() {
        return COPY_DEPENDENCY;
    }

    /**
	 * 
	 */
    public Datenbanksystem() {
        super();
    }

    @Override
    public int layerFor() {
        return ModelConstants.LOGICAL_LAYER;
    }

    @Override
    public ElementPropertyDialog createPropertyDialog(final GDCollection gdcoll) {
        ElementPropertyDialog dialog = new ElementPropertyDialog(this, gdcoll);
        dialog.addTab(getResString("Datensatztyp"), new NConnectionPanel(Datensatztyp.class, dialog, true, true));
        dialog.addTab(getResString("masterfuer"), new NConnectionPanel(Objekttyp.class, dialog, true, true));
        return dialog;
    }

    //	public static final Class[] MASTER_TYPES = {
    //		Anwendungsbaustein.class,
    //		RechAnwendungsbaustein.class,
    //	};
    //	
    //	public final Class[] getMasterTypes()  {
    //		return MASTER_TYPES;
    //	}

}
