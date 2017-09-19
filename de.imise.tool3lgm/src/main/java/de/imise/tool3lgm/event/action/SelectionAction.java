package de.imise.tool3lgm.event.action;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.event.ActionIdentifier;
import de.imise.tool3lgm.graphtools.model.GDCommands;

/**
 * Eine Aktion, die enabled ist, wenn in einem Modell irgendwas selektiert ist.
 *
 * @author imise
 */
public class SelectionAction extends GraphDocumentAction {

    /**
     * @param identifier
     */
    public SelectionAction(final GDCommands identifier) {
        super(identifier);
    }

    /**
     * @param identifier
     */
    public SelectionAction(final ActionIdentifier identifier) {
        super(identifier);
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && Static.getSelectedDoc().isSelection();
    }

}
