package de.imise.tool3lgm.event.action;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.event.ActionIdentifier;
import de.imise.tool3lgm.graphtools.model.GDCommands;

/**
 * Eine Aktion, die enabled ist, wenn (außer evtl. auch selektierten Knickpunten) wenigstens
 * eine Knoten oder eine Kante selketiert ist (egal ob in der Grafik oder im Baum).
 *
 * @author imise
 */
public class SelectedElementsAction extends GraphDocumentAction {

    /**
     * @param identifier
     */
    public SelectedElementsAction(final GDCommands identifier) {
        super(identifier);
    }

    /**
     * @param identifier
     */
    public SelectedElementsAction(final ActionIdentifier identifier) {
        super(identifier);
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && Static.getSelectedDoc() != null && (Static.getSelectedDoc().isSelectedAtLeastOneRealNode() || Static.getSelectedDoc().isSelectedAtLeastOneEdge());
    }

}
