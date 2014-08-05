package de.imise.tool3lgm.graphtools.elements.node;

import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.AufOrgPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.StructurePanel;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;

public final class Organisationseinheit extends Knoten {

    /**
	 * 
	 */
    public Organisationseinheit() {
        super();
    }

    @Override
    public int layerFor() {
        return ModelConstants.DOMAIN_LAYER;
    }

    @Override
    public ElementPropertyDialog createPropertyDialog(final GDCollection gdcoll) {
        ElementPropertyDialog dialog = new ElementPropertyDialog(this, gdcoll);
        dialog.addTab(getResString("strukt"), new StructurePanel(dialog));
        //		dialog.addTab(getResString("Aufgabe_p"),new OrgAufPanel(new
        // Aufgabe(),dialog));
        dialog.addTab(getResString("Aufgabe_p"), new AufOrgPanel(Aufgabe.class, dialog, true));
        return dialog;
    }

    @Override
    public boolean hasLayout() {
        return false;
    }

    @Override
    public boolean hasSortedKanten() {
        return false;
    }

}
