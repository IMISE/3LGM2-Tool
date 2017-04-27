package de.imise.tool3lgm.graphtools.elements.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.NConnectionPanel;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;

/**
 * @author N.N.
 * @create Long time ago
 */
public final class Softwareprodukt extends Knoten {

    /**
     * 
     */
    public Softwareprodukt() {
        super();
    }

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
        dialog.addTab(getResString("Aufgabe"), new NConnectionPanel(Aufgabe.class, dialog, true, true));
        dialog.addTab(getResString("Anwendungsprogramm"), new NConnectionPanel(Anwendungsprogramm.class, dialog, false, true));
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

    @Override
    public int layerFor() {
        return ModelConstants.LOGICAL_LAYER;
    }

}
