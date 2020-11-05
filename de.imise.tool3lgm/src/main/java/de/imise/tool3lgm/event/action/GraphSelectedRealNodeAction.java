package de.imise.tool3lgm.event.action;

import de.imise.tool3lgm.Static;

/**
 * Eine Action die enabled ist, wenn eine Grafik aktiv ist und in dieser Grafik
 * wenigstens ein sichtbarer Knoten (außer Kanten und Knickpunkten) selektiert
 * ist.
 *
 * @author AXS
 */
public class GraphSelectedRealNodeAction extends SelectedRealNodeAction {

    /**
     * @param identifier
     */
    public GraphSelectedRealNodeAction(final Object identifier) {
        super(identifier);
    }

    @Override
    public boolean isEnabled() {
        //es gib das Tool, es ist ein Modell geladen und ein Grafikfenster des aktuell
        //selektierten Teilmodells ist sichtbar
        if (!super.isEnabled()) {
            return false;
        }
        return Static.isActiveFrameGraphFrame();
    }

}
