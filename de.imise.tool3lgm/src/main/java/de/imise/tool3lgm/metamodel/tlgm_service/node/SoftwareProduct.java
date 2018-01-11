package de.imise.tool3lgm.metamodel.tlgm_service.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AwpSwpVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.SwpAufVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Anwendungsprogramm;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.RechAnwendungsbaustein;

/**
 * @author AXS (26.12.2017)
 */
public final class SoftwareProduct extends Node {

    @Override
    public void setName(final String name) {
        super.setName(name);
        for (int c = 0; c < getEdgesCount(); c++) {
            ModelElement awp = getEdge(c).getOther(this);
            if (awp.getClass() == Anwendungsprogramm.class) {
                for (int a = 0; a < awp.getEdgesCount(); a++) {
                    ModelElement awb = awp.getEdge(a).getOther(awp);
                    if (awb.getClass() == RechAnwendungsbaustein.class) {
                        awb.setName(awb.getName());
                    }
                }
            }
        }
    }

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addPathConnectionPanel(SwpAufVerbindung.class);
        dialog.addPathConnectionPanel(AwpSwpVerbindung.class);
        return dialog;
    }

}
