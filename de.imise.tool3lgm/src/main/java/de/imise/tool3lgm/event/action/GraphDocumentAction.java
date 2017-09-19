package de.imise.tool3lgm.event.action;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.event.ActionIdentifier;
import de.imise.tool3lgm.graphtools.model.GDCommands;

/**
 * Eine Aktion, die enabled ist, wenn irgendein Teilmodell oder ein Gesamtmodell aktiv (also geöffnet) ist.
 *
 * @author AXS
 */
public class GraphDocumentAction extends StaticActionNew {

    /**
     * @param identifier
     */
    public GraphDocumentAction(final ActionIdentifier identifier) {
        super(identifier);
    }

    /**
     * @param identifier
     */
    public GraphDocumentAction(final GDCommands identifier) {
        super(identifier);
    }

    /**
     * @param identifier
     * @param arguments
     * @param text
     */
    public GraphDocumentAction(final GDCommands identifier, final String arguments, final String text) {
        super(identifier, arguments, text);
    }

    /**
     * @param identifier
     * @param appendThreePoints
     */
    public GraphDocumentAction(final ActionIdentifier identifier, final Boolean appendThreePoints) {
        super(identifier, appendThreePoints);
    }

    /**
     * @param identifier
     * @param appendThreePoints
     */
    public GraphDocumentAction(final GDCommands identifier, final Boolean appendThreePoints) {
        super(identifier, appendThreePoints);
    }

    /**
     * @param identifier
     * @param appendThreePoints
     * @param initialSelectionState
     */
    public GraphDocumentAction(final ActionIdentifier identifier, final Boolean appendThreePoints, final Boolean initialSelectionState) {
        super(identifier, appendThreePoints, initialSelectionState);
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && Static.getSelectedDoc() != null;
    }

}
