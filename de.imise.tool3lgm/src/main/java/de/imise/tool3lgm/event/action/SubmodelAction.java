package de.imise.tool3lgm.event.action;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.model.Szenario;

/**
 * Eine Aktion, die enabled ist, wenn ein Teilmodell (und nicht eine
 * Gesamtmodellansicht) aktiv ist.
 *
 * @author imise
 */
public class SubmodelAction extends GraphDocumentAction {

    /**
     * @param identifier
     */
    public SubmodelAction(final Object identifier) {
        super(identifier);
    }

    /**
     * @param identifier
     * @param textSuffix
     */
    public SubmodelAction(final Object identifier, final String textSuffix) {
        super(identifier, textSuffix);
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && Static.getSelectedDoc() instanceof Szenario;
    }

}
