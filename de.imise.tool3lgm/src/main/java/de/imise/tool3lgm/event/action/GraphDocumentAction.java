package de.imise.tool3lgm.event.action;

import de.imise.tool3lgm.Static;

/**
 * Eine Aktion, die enabled ist, wenn irgendein Teilmodell oder ein Gesamtmodell aktiv (also geöffnet) ist.
 *
 * @author AXS
 */
public class GraphDocumentAction extends StaticActionNew {

    /**
     * @param identifier
     */
    public GraphDocumentAction(final Object identifier) {
        super(identifier);
    }

    /**
     * @param identifier
     * @param arguments
     * @param text
     */
    public GraphDocumentAction(final Object identifier, final String arguments, final String text) {
        super(identifier, arguments, text);
    }

    /**
     * @param identifier
     * @param textSuffix
     */
    public GraphDocumentAction(final Object identifier, final String textSuffix) {
        super(identifier, textSuffix);
    }

    /**
     * @param identifier
     * @param initialSelectionState
     */
    public GraphDocumentAction(final Object identifier, final Boolean initialSelectionState) {
        super(identifier, initialSelectionState);
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && Static.getSelectedDoc() != null;
    }

}
