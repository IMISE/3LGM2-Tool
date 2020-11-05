package de.imise.tool3lgm.event.action;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;

/**
 * @author Ich (23.05.2018)
 */
public class SelectedRealNodeAction extends GraphDocumentAction {

    /**
     * @param identifier
     */
    public SelectedRealNodeAction(final Object identifier) {
        super(identifier);
    }

    /**
     * @param identifier
     * @param arguments
     * @param text
     * @param textSuffix
     */
    public SelectedRealNodeAction(final Object identifier, final String arguments, final String text, final String textSuffix) {
        super(identifier, arguments, text, textSuffix);
    }

    /**
     * @param identifier
     * @param textSuffix
     */
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
            ModelElement me = ec.getElement();
            if (me.isPaintable() && ec.isVisible() || me.getMetaModel().hasSortedEdgeClassesToPaintable(me.getClass())) {
                return true;
            }
        }
        return false;
    }

}
