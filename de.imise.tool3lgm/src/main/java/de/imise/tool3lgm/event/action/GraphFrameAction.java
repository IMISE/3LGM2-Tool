package de.imise.tool3lgm.event.action;

import de.imise.tool3lgm.Static;

/**
 * Eine Aktion, die enabled ist, wenn der grafische Frame des aktiven Teilmodells sichtbar ist.
 *
 * @author AXS
 */
public class GraphFrameAction extends GraphDocumentAction {

    /**
     * @param identifier
     */
    public GraphFrameAction(final Object identifier) {
        super(identifier);
    }

    /**
     * @param identifier
     * @param textSuffix
     */
    public GraphFrameAction(final Object identifier, final String textSuffix) {
        super(identifier, textSuffix);
    }

    /**
     * @param identifier
     * @param initialSelectionState
     */
    public GraphFrameAction(final Object identifier, final Boolean initialSelectionState) {
        super(identifier, initialSelectionState);
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && Static.isActiveFrameGraphFrame();
    }

}
