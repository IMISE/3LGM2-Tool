package de.imise.tool3lgm.event.action;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.event.ActionIdentifier;
import de.imise.tool3lgm.graphtools.model.GDCommands;
import de.imise.tool3lgm.graphtools.model.Szenario;

/**
 * Eine Aktion, die enabled ist, wenn ein Teilmodell (und nicht eine Gesamtmodellansicht) aktiv ist.
 *
 * @author imise
 */
public class SubmodelAction extends GraphDocumentAction {

    public SubmodelAction(final GDCommands identifier) {
        super(identifier);
    }

    public SubmodelAction(final GDCommands identifier, final String textSuffix) {
        super(identifier, textSuffix);
    }

    public SubmodelAction(final ActionIdentifier identifier) {
        super(identifier);
    }

    public SubmodelAction(final ActionIdentifier identifier, final String textSuffix) {
        super(identifier, textSuffix);
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && Static.getSelectedDoc() instanceof Szenario;
    }

}
