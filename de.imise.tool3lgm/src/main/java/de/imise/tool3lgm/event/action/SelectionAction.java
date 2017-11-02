package de.imise.tool3lgm.event.action;

import de.imise.tool3lgm.Static;

/**
 * Eine Aktion, die enabled ist, wenn in einem Modell irgendwas selektiert ist.
 *
 * @author imise
 */
public class SelectionAction extends GraphDocumentAction {

    /**
     * @param identifier
     */
    public SelectionAction(final Object identifier) {
        super(identifier);
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && Static.getSelectedDoc().isSelection();
    }

}
