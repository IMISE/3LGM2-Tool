package de.imise.tool3lgm.graphtools.elements.node;

import java.util.ArrayList;
import java.util.StringTokenizer;

import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.NConnectionPanel;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;

/**
 * @author N.N.
 * @create Long time ago
 */
public abstract class EtntEtdtKombination extends Knoten {

    /**
	 * 
	 */
    public EtntEtdtKombination() {
        super();
    }

    @Override
    public final int layerFor() {
        return ModelConstants.LOGICAL_LAYER;
    }

    @Override
    public String getName() {
        String name = "";
        ArrayList<ModelElement> arrayList = getConnectedElements(Ereignistyp.class);
        if (arrayList.size() > 0) {
            name += ((Ereignistyp) arrayList.get(0)).getName();
        }
        name += " - ";
        arrayList = getConnectedElements(Nachrichtentyp.class);
        if (arrayList.size() > 0) {
            name += ((Nachrichtentyp) arrayList.get(0)).getName();
        }
        arrayList = getConnectedElements(Dokumententyp.class);
        if (arrayList.size() > 0) {
            name += ((Dokumententyp) arrayList.get(0)).getName();
        }
        return name;
    }

    @Override
    public String toString() {
        String name = getName();
        int index = name.indexOf("-\n");
        while (index > 0) {
            name = name.substring(0, index) + name.substring(index + 2);
            index = name.indexOf("-\n");
        }
        String string = "";
        StringTokenizer st = new StringTokenizer(name, "\n");
        while (st.hasMoreTokens()) {
            string += " " + st.nextToken();
        }

        return string;
    }

    @Override
    public ElementPropertyDialog createPropertyDialog(final GDCollection gdcoll) {
        ElementPropertyDialog dialog = new ElementPropertyDialog(this, gdcoll);
        dialog.addTab(getResString("Kommunikationsstandard"), new NConnectionPanel(Kommunikationsstandard.class, dialog, true, true));
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

    @Override
    public boolean avoidDuplicates() {
        return true;
    }

}
