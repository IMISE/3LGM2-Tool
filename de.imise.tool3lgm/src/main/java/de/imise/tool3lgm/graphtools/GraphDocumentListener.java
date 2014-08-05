package de.imise.tool3lgm.graphtools;

import de.imise.tool3lgm.graphtools.view.container.ElementContainer;

public interface GraphDocumentListener {
    /** Hier die generelle Nachricht - alle sollten sich komplett erneuern */
    public void dataChanged(GraphDocument source);

    /** Hier reagiert nur die GraphArea */
    public void elementGraphicsChanged(GraphDocument source, ElementContainer element);

    /** Wenn sich das Standard-GraphElementLayout geaendert hat (nur GraphArea) */
    public void layoutChanged(GraphDocument source);

    /** Hier reagieren GraphArea und ModelBrowser */
    public void elementAdded(GraphDocument source, ElementContainer element);

    /** Hier reagieren GraphArea und ModelBrowser */
    public void elementDeleted(GraphDocument source, ElementContainer element);

    /** Hier "sollte" sich meiner Ansicht nach der ModelBrowser erneuern */
    public void groupOrderChanged(GraphDocument source);

    /** Wichtig fuer die Werkzeugleiste, GraphArea sollte sich auch erneueern */
    public void activeLayerChanged(GraphDocument source);

    /** Hier die GraphArea und evtl. Farb-Buttons neu machen */
    public void colorsChanged(GraphDocument source);

    /** Hier GraphArea und evtl. ModelBrowser */
    public void selectionChanged(GraphDocument source);
}
