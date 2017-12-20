package de.imise.tool3lgm.event.action;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;

/**
 * Eine Action die enabled ist, wenn eine Grafik aktiv ist und in dieser Grafik wenigstens
 * ein sichtbarer Knoten (außer Kanten und Knickpunkten) selektiert ist.
 *
 * @author imise
 */
public class GraphSelectedRealNodeAction extends GraphFrameAction {

    /**
     * @param identifier
     */
    public GraphSelectedRealNodeAction(final Object identifier) {
        super(identifier);
    }

    /**
     * @param identifier
     * @param textSuffix
     */
    public GraphSelectedRealNodeAction(final Object identifier, final String textSuffix) {
        super(identifier, textSuffix);
    }

    @Override
    public boolean isEnabled() {
        //es gib das Tool, es ist ein Modell geladen und ein Grafikfenster des aktuell
        //selektierten Teilmodells ist sichtbar
        if (!super.isEnabled()) {
            return false;
        }
        for (ElementContainer ec : Static.iterableSelectedRealElementContainer()) {
            //TODO: testen, ob visible hier reicht
            if (!ec.isUnpaintable() && ec.isVisible() || ModelConstants.hasSortedEdgesToPaintable(ec.getElement().getClass())) {
                return true;
            }
        }
        return false;
    }

}
