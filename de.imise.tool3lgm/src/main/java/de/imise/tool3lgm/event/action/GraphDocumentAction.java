package de.imise.tool3lgm.event.action;

import de.imise.tool3lgm.Static;

/**
 * Eine Aktion, die enabled ist, wenn irgendein Teilmodell oder ein Gesamtmodell aktiv (also geöffnet) ist.
 *
 * @author AXS
 */
public class GraphDocumentAction extends StaticAction {

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
     * @param textSuffix
     */
    public GraphDocumentAction(final Object identifier, final String arguments, final String text, final String textSuffix) {
        super(identifier, arguments, text, textSuffix);
    }

    /**
     * @param identifier
     * @param textSuffix
     */
    public GraphDocumentAction(final Object identifier, final String textSuffix) {
        super(identifier, textSuffix);
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && Static.getSelectedDoc() != null;
    }

}
