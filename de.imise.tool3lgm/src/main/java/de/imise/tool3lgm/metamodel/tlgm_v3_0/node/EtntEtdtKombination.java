package de.imise.tool3lgm.metamodel.tlgm_v3_0.node;

import java.util.List;
import java.util.StringTokenizer;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.Node;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.EtntEtVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.EtntKommstVerbindung;

/**
 * @author N.N.
 * @create Long time ago
 */
public abstract class EtntEtdtKombination extends Node {

    /**
     *
     */
    public EtntEtdtKombination() {
        super();
    }

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
        dialog.addDescripDescriptedSingleConnectionPanel(EtntEtVerbindung.class);
        dialog.addPathConnectionPanel(EtntKommstVerbindung.class);
        return dialog;
    }

}
