package de.imise.tool3lgm.metamodel.original.node;

import java.util.List;
import java.util.StringTokenizer;

import de.imise.tool3lgm.graphtools.dialog.element.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.metamodel.original.edge.EtntEtVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.EtntKommstVerbindung;

/**
 * @author N.N.
 * @create Long time ago
 */
public abstract class EtntEtdtKombination extends Node {

    @Override
    public String getName() {
        String name = "";
        List<ModelElement> connected = getConnectedElements(Ereignistyp.class);
        if (!connected.isEmpty()) {
            name += ((Ereignistyp) connected.get(0)).getName();
        }
        name += " - ";
        connected = getConnectedElements(Nachrichtentyp.class);
        if (!connected.isEmpty()) {
            name += ((Nachrichtentyp) connected.get(0)).getName();
        }
        connected = getConnectedElements(Dokumententyp.class);
        if (!connected.isEmpty()) {
            name += ((Dokumententyp) connected.get(0)).getName();
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
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripDescriptedPanel(EtntEtVerbindung.class);
        dialog.addDescriptedSingleConnectionPanel(EtntKommstVerbindung.class);
        return dialog;
    }

}
