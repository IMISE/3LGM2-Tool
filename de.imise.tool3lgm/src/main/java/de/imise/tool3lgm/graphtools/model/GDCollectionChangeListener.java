package de.imise.tool3lgm.graphtools.model;

import de.imise.tool3lgm.graphtools.newmatrixview.MatrixViewInternalFrame;
import de.imise.tool3lgm.graphtools.userfield.UserFieldTarget;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.gui.InternalGraphFrame;

public interface GDCollectionChangeListener {

    public enum GDCollectionChangeType {

        DATA_CHANGED {
            @Override
            protected void deliverEvent(final GDCollectionChangeListener gdl, final GraphDocument source, final ElementContainer last_elem) {
                gdl.dataChanged(source);
            }
        },
        ELEMENT_NAME_CHANGED {
            @Override
            protected void deliverEvent(final GDCollectionChangeListener gdl, final GraphDocument source, final ElementContainer last_elem) {
                gdl.elementNameChanged(last_elem);
            }
        },
        USER_FIELD_VALUE_CHANGED {
            @Override
            protected void deliverEvent(final GDCollectionChangeListener gdl, final GraphDocument source, final ElementContainer last_elem) {
                UserFieldTarget userFieldTarget = last_elem == null ? null : last_elem.getElement();
                gdl.userFieldValueChanged(userFieldTarget);
            }
        },
        ELEMENT_GRAPHICS_CHANGED {
            @Override
            protected void deliverEvent(final GDCollectionChangeListener gdl, final GraphDocument source, final ElementContainer last_elem) {
                gdl.elementGraphicsChanged(last_elem);
            }
        },
        LAYOUT_CHANGED {
            @Override
            protected void deliverEvent(final GDCollectionChangeListener gdl, final GraphDocument source, final ElementContainer last_elem) {
                gdl.layoutChanged(source);
            }
        },
        GROUP_ORDER_CHANGED {
            @Override
            protected void deliverEvent(final GDCollectionChangeListener gdl, final GraphDocument source, final ElementContainer last_elem) {
                gdl.groupOrderChanged(source);
            }
        },
        ACTIVE_LAYER_CHANGED {
            @Override
            protected void deliverEvent(final GDCollectionChangeListener gdl, final GraphDocument source, final ElementContainer last_elem) {
                gdl.activeLayerChanged(source);
            }
        },
        COLORS_CHANGED {
            @Override
            protected void deliverEvent(final GDCollectionChangeListener gdl, final GraphDocument source, final ElementContainer last_elem) {
                gdl.colorsChanged(source);
            }
        },
        SELECTION_CHANGED {
            @Override
            protected void deliverEvent(final GDCollectionChangeListener gdl, final GraphDocument source, final ElementContainer last_elem) {
                gdl.selectionChanged(source);
            }
        },
        MODEL_OR_SZENARIO_RENAMED {
            @Override
            protected void deliverEvent(final GDCollectionChangeListener gdl, final GraphDocument source, final ElementContainer last_elem) {
                gdl.modelOrSzenarioRenamed(source);
            }
        };

        protected abstract void deliverEvent(GDCollectionChangeListener l, final GraphDocument source, final ElementContainer last_elem);

        protected void deliverEvent(final Iterable<GDCollectionChangeListener> listeners, final GraphDocument source, final ElementContainer last_elem) {
            for (GDCollectionChangeListener l : listeners) {
                deliverEvent(l, source, last_elem);
            }
        }

    }

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

    /**
     * @param listeners
     * @param source
     * @param last_elem
     */
    public static void distributeEvent(final GDCollectionChangeType changeType, final Iterable<GDCollectionChangeListener> listeners, final GraphDocument source, final ElementContainer last_elem) {
        if (source != null) {
            GDCollection gdcoll = source.getCollection();
            if (gdcoll.isBulkMode()) {
                return;
            }
            if (source.isVerificationMode()) {
                System.out.println("distributeEvent: " + changeType);
            }
        }
        changeType.deliverEvent(listeners, source, last_elem);
    }

}
