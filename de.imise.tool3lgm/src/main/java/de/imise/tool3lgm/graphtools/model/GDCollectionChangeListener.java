package de.imise.tool3lgm.graphtools.model;

import de.imise.tool3lgm.graphtools.newmatrixview.MatrixViewInternalFrame;
import de.imise.tool3lgm.graphtools.userfield.UserFieldTarget;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.gui.InternalGraphFrame;

public interface GDCollectionChangeListener {

    /** Hier die generelle Nachricht - alle sollten sich komplett erneuern */
    public void dataChanged(GraphDocument source);

    /** Hier reagieren alle, die den Namen anzeigen */
    public void elementNameChanged(ElementContainer ec);

    /** Hier reagieren alle, die UserFields anzeigen */
    public void userFieldValueChanged(UserFieldTarget userFieldTarget);

    /** Hier reagiert nur die GraphArea */
    public void elementGraphicsChanged(ElementContainer ec);

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

    public static void distributeEvent(final GDCollectionChangeType changeType, final Iterable<GDCollectionChangeListener> listener, final GraphDocument source, final ElementContainer last_elem) {
        if (source != null) {
            GDCollection gdcoll = source.getCollection();
            if (gdcoll.isBulkMode()) {
                return;
            }
            if (source.isVerificationMode()) {
                System.out.println("distributeEvent: " + changeType);
            }
        }
        switch (changeType) {
        case DATA_CHANGED:
            for (GDCollectionChangeListener gdl : listener) {
                gdl.dataChanged(source);
            }
            break;
        case ELEMENT_GRAPHICS_CHANGED:
            for (GDCollectionChangeListener gdl : listener) {
                gdl.elementGraphicsChanged(last_elem);
            }
            break;
        case ELEMENT_NAME_CHANGED:
            for (GDCollectionChangeListener gdl : listener) {
                gdl.elementNameChanged(last_elem);
            }
            break;
        case USER_FIELD_VALUE_CHANGED:
            UserFieldTarget userFieldTarget = last_elem == null ? null : last_elem.getElement();
            for (GDCollectionChangeListener gdl : listener) {
                gdl.userFieldValueChanged(userFieldTarget);
            }
            break;
        case LAYOUT_CHANGED:
            for (GDCollectionChangeListener gdl : listener) {
                gdl.layoutChanged(source);
            }
            break;
        case GROUP_ORDER_CHANGED:
            for (GDCollectionChangeListener gdl : listener) {
                gdl.groupOrderChanged(source);
            }
            break;
        case ACTIVE_LAYER_CHANGED:
            for (GDCollectionChangeListener gdl : listener) {
                gdl.activeLayerChanged(source);
            }
            break;
        case COLORS_CHANGED:
            for (GDCollectionChangeListener gdl : listener) {
                gdl.colorsChanged(source);
            }
            break;
        case SELECTION_CHANGED:
            for (GDCollectionChangeListener gdl : listener) {
                gdl.selectionChanged(source);
            }
            break;
        case MODEL_OR_SZENARIO_RENAMED:
            for (GDCollectionChangeListener gdl : listener) {
                gdl.modelOrSzenarioRenamed(source);
            }
            break;
        default:
            break;
        }

    }

}
