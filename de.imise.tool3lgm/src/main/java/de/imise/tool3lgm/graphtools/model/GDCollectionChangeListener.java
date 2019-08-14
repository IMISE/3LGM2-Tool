package de.imise.tool3lgm.graphtools.model;

import de.imise.tool3lgm.graphtools.newmatrixview.MatrixViewInternalFrame;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.gui.InternalGraphFrame;

public interface GDCollectionChangeListener {

    /** Hier die generelle Nachricht - alle sollten sich komplett erneuern */
    public void dataChanged(GraphDocument source);

    /** Hier reagiert nur die GraphArea */
    public void elementGraphicsChanged(GraphDocument source, ElementContainer element);

    /** Wenn sich das Standard-GraphElementLayout geaendert hat (nur GraphArea) */
    public void layoutChanged(GraphDocument source);

    /** Hier "sollte" sich meiner Ansicht nach der ModelBrowser erneuern */
    public void groupOrderChanged(GraphDocument source);

    /** Wichtig fuer die Werkzeugleiste, GraphArea sollte sich auch erneueern */
    public void activeLayerChanged(GraphDocument source);

    /** Hier die GraphArea und evtl. Farb-Buttons neu machen */
    public void colorsChanged(GraphDocument source);

    /** Hier GraphArea und evtl. ModelBrowser */
    public void selectionChanged(GraphDocument source);

    /**
     * Aktualisiert je nach Parameter alle Components, die den Title eines Modells oder Teilmodells anzeigen. Wenn (<code>source == null</code>), nur
     * die Fenster eines bestimmtem Modells (<code!(source instanceof Szenario)</code>) oder nur die Fenster eines bestimmten Szenarios
     * (<code>source instanceof Szenario</code>). Ein Szenario hat max. einen {@link InternalGraphFrame} und dann noch beliebig viele
     * {@link MatrixViewInternalFrame}. ModelBrowser + FrameTitle + Fenster-Actions im Menü + ...
     *
     * @param source <code>null</code> = alle Modelle, Szenario = nur dieses Teilmodell, GraphDocument = Hauptmodell des Modells, bei dem alle
     *            Teilmodelle betroffen sind
     */
    public void modelOrSzenarioRenamed(GraphDocument source);

}
