package de.imise.tool3lgm.event.action;

import de.imise.tool3lgm.graphtools.model.GraphDocument;

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
    public SelectedElementsAction(final Object identifier) {
        super(identifier);
    }

    /**
     * @param identifier
     * @param templateContextAction
     */
    public SelectedElementsAction(final Object identifier, final boolean templateContextAction) {
        super(identifier, templateContextAction);
    }

    @Override
    public boolean isEnabled() {
        if (!super.isEnabled()) {
            return false;
        }
        GraphDocument selectedDoc = getActiveDoc();
        boolean enabled = selectedDoc.isSelectedAtLeastOneRealNode() || selectedDoc.isSelectedAtLeastOneEdge();
        return enabled;
    }

}
