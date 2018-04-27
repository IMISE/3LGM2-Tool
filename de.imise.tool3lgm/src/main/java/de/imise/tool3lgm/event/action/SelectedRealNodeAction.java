package de.imise.tool3lgm.event.action;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;

public class SelectedRealNodeAction extends GraphDocumentAction {

    public SelectedRealNodeAction(final Object identifier) {
        super(identifier);
    }

    public SelectedRealNodeAction(final Object identifier, final String arguments, final String text, final String textSuffix) {
        super(identifier, arguments, text, textSuffix);
    }

    public SelectedRealNodeAction(final Object identifier, final String textSuffix) {
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
            if (ec.getElement().isPaintable() && ec.isVisible() || ModelConstants.hasSortedEdgeClassesToPaintable(ec.getElement().getClass())) {
                return true;
            }
        }
        return false;
    }

}
