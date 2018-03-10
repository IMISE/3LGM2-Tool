package de.imise.tool3lgm.event.action;

import de.imise.tool3lgm.Static;

/**
 * Eine Aktion, die aktiv ist, wenn in ein Teilmodell aktiv ist und darin etwas selektiert ist.
 *
 * @author AXS (10.04.2018)
 */
public class SubmodelSelectionAction extends SubmodelAction {

    public SubmodelSelectionAction(final Object identifier) {
        super(identifier);
    }

    public SubmodelSelectionAction(final Object identifier, final String textSuffix) {
        super(identifier, textSuffix);
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && Static.getSelectedDoc().isSelection();
    }

}
