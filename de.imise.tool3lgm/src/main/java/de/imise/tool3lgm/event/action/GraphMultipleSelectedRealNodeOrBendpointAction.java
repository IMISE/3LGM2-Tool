package de.imise.tool3lgm.event.action;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.model.GDCommands;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;

/**
 * Eine Action die enabled ist, wenn eine Grafik aktiv ist und in dieser Grafik
 * wenigstens 2 sichtbare Knoten (außer Kanten und Knickpunkten) selektiert
 * sind.
 *
 * @author AXS (25.03.2018)
 */
public class GraphMultipleSelectedRealNodeOrBendpointAction extends GraphFrameAction {

    /**
     * @param identifier
     * @param i
     */
    public GraphMultipleSelectedRealNodeOrBendpointAction(final GDCommands identifier) {
        super(identifier);
    }

    @Override
    public boolean isEnabled() {
        if (!super.isEnabled()) {
            return false;
        }
        int selected = 0;
        for (ElementContainer ec : Static.iterableSelectedRealElementContainer()) {
            if (ec.getElement().isPaintable() && ec.isVisible()) {
                if (++selected == 2) {
                    return true;
                }
            }
        }
        for (ElementContainer ec : Static.iterableSelectedBendpointContainer()) {
            if (ec.isVisible()) {
                if (++selected == 2) {
                    return true;
                }
            }
        }
        return false;
    }

}
