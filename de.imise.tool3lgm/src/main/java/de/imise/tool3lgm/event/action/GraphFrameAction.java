package de.imise.tool3lgm.event.action;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.event.ActionIdentifier;
import de.imise.tool3lgm.graphtools.model.GDCommands;

/**
 * Eine Aktion, die enabled ist, wenn der grafische Frame des aktiven Teilmodells sichtbar ist.
 *
 * @author AXS
 */
public class GraphFrameAction extends GraphDocumentAction {

    /**
     * @param identifier
     */
    public GraphFrameAction(final ActionIdentifier identifier) {
        super(identifier);
    }

    /**
     * @param identifier
     */
    public GraphFrameAction(final GDCommands identifier) {
        super(identifier);
    }

    /**
     * @param identifier
     * @param textSuffix
     */
    public GraphFrameAction(final ActionIdentifier identifier, final String textSuffix) {
        super(identifier, textSuffix);
    }

    /**
     * @param identifier
     * @param textSuffix
     */
    public GraphFrameAction(final GDCommands identifier, final String textSuffix) {
        super(identifier, textSuffix);
    }

    /**
     * @param identifier
     * @param initialSelectionState
     */
    public GraphFrameAction(final ActionIdentifier identifier, final Boolean initialSelectionState) {
        super(identifier, initialSelectionState);
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && Static.isActiveFrameGraphFrame();
    }

}
