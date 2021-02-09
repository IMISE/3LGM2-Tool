package de.imise.tool3lgm.graphtools.model;

import static de.imise.tool3lgm.Tool3lgmChangeListener.Tool3lgmChangeType.MODEL_CHANGE_SELECTED_SZENARIO_CHANGED;
import static de.imise.tool3lgm.Tool3lgmChangeListener.Tool3lgmChangeType.MODEL_CHANGE_SZENARIO_ADDED;
import static de.imise.tool3lgm.Tool3lgmChangeListener.Tool3lgmChangeType.MODEL_CHANGE_SZENARIO_REMOVED;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableSet;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmChangeListener;
import de.imise.tool3lgm.graphtools.userfield.UserFieldTarget;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.gui.viewpane.graph.GraphViewPaneFrameComponent;
import de.imise.tool3lgm.gui.viewpane.matrix.MatrixViewPaneFrameComponent;
import de.imise.util.Sys;

public interface LGMChangeListener {

    public enum LGMChangeType {

        DATA_CHANGED {
            @Override
            protected void deliverEvent(final LGMChangeListener gdl, final GraphDocument source, final ElementContainer last_elem) {
                gdl.dataChanged(source);
            }
        },
        ELEMENT_NAME_CHANGED {
            @Override
            protected void deliverEvent(final LGMChangeListener gdl, final GraphDocument source, final ElementContainer last_elem) {
                gdl.elementNameChanged(last_elem);
            }
        },
        USER_FIELD_VALUE_CHANGED {
            @Override
            protected void deliverEvent(final LGMChangeListener gdl, final GraphDocument source, final ElementContainer last_elem) {
                UserFieldTarget userFieldTarget = last_elem == null ? null : last_elem.getElement();
                gdl.userFieldValueChanged(userFieldTarget);
            }
        },
        ELEMENT_GRAPHICS_CHANGED {
            @Override
            protected void deliverEvent(final LGMChangeListener gdl, final GraphDocument source, final ElementContainer last_elem) {
                gdl.elementGraphicsChanged(last_elem);
            }
        },
        LAYOUT_CHANGED {
            @Override
            protected void deliverEvent(final LGMChangeListener gdl, final GraphDocument source, final ElementContainer last_elem) {
                gdl.layoutChanged(source);
            }
        },
        GROUP_ORDER_CHANGED {
            @Override
            protected void deliverEvent(final LGMChangeListener gdl, final GraphDocument source, final ElementContainer last_elem) {
                gdl.groupOrderChanged(source);
            }
        },
        ACTIVE_LAYER_CHANGED {
            @Override
            protected void deliverEvent(final LGMChangeListener gdl, final GraphDocument source, final ElementContainer last_elem) {
                gdl.activeLayerChanged(source);
            }
        },
        SELECTED_SZENARIO_CHANGED {
            @Override
            protected void deliverEvent(final LGMChangeListener gdl, final GraphDocument source, final ElementContainer last_elem) {
                gdl.selectedSzenarioChanged(source);
            }
        },
        COLORS_CHANGED {
            @Override
            protected void deliverEvent(final LGMChangeListener gdl, final GraphDocument source, final ElementContainer last_elem) {
                gdl.colorsChanged(source);
            }
        },
        SELECTION_CHANGED {
            @Override
            protected void deliverEvent(final LGMChangeListener gdl, final GraphDocument source, final ElementContainer last_elem) {
                gdl.selectionChanged(source);
            }
        },
        MODEL_OR_SZENARIO_NAME_CHANGED {
            @Override
            protected void deliverEvent(final LGMChangeListener gdl, final GraphDocument source, final ElementContainer last_elem) {
                gdl.modelOrSzenarioNameChanged(source);
            }
        },
        MODEL_DESCRIPTION_CHANGED {
            @Override
            protected void deliverEvent(final LGMChangeListener gdl, final GraphDocument source, final ElementContainer last_elem) {
                gdl.modelDescriptionChanged(source);
            }
        },
        SZENARIO_ADDED {
            @Override
            protected void deliverEvent(final LGMChangeListener gdl, final GraphDocument source, final ElementContainer last_elem) {
                gdl.szenarioAdded(source);
            }
        },
        SZENARIO_REMOVED {
            @Override
            protected void deliverEvent(final LGMChangeListener gdl, final GraphDocument source, final ElementContainer last_elem) {
                gdl.szenarioRemoved(source);
            }
        },

        ;

        /**
         * Enthält alle {@link LGMChangeType}s, für die die Funktion
         * {@link #isSzenarioSpecific()} <code>false</code> liefern soll
         */
        private static final Collection<LGMChangeType> NOT_SZENARIO_SPECIFIC_CHANGE_TYPES = ImmutableSet.of(DATA_CHANGED, ELEMENT_NAME_CHANGED, USER_FIELD_VALUE_CHANGED, SELECTION_CHANGED, ACTIVE_LAYER_CHANGED); //ACTIVE_LAYER wird in der Collection gespeichert und gilt immer für alle docs

        /**
         * Liefert <code>true</code>, wenn dieses Ereignis nur für ein
         * bestimmtes Szenario ausgelöst werden soll. Soll es für alle ausgelöst
         * werden, dann muss das Ereignis <code>false</code> liefern. Bsp.:
         * DATA_CHANGED oder ELEMENT_NAME_CHANGED muss in allen Szenarios
         * durchschlagen, also muss diese Funktion <code>false</code> liefern.
         * SZENARIO_REMOVED aber darf nur für das tatsächlich gelöschte Szenario
         * aufgerufen werden, da sonst auch alle anderen melden, dass sie
         * gelöscht seien und die Fenster alle zugehen.
         *
         * @return
         */
        public boolean isSzenarioSpecific() {
            return !NOT_SZENARIO_SPECIFIC_CHANGE_TYPES.contains(this);
        }

        /**
         * @param l
         * @param source
         * @param last_elem
         */
        protected abstract void deliverEvent(LGMChangeListener l, final GraphDocument source, final ElementContainer last_elem);

        /**
         * @param listeners
         * @param source
         * @param last_elem
         * @param deliverStatic if <code>true</code>, the SZENARIO_ADDED and
         *            SZENARIO_REMOVED will be distributet as
         *            MODEL_CHANGE_SZENARIO_ADDED and
         *            MODEL_CHANGE_SZENARIO_REMOVED to the
         *            {@link Tool3lgmChangeListener}
         */
        protected void deliverEvent(final List<LGMChangeListener> listeners, final GraphDocument source, final ElementContainer last_elem, final boolean deliverStatic) {
            //das hier muss sein, weil es vorkommen kann, dass sich bei
            //deliverEvent(l, source, last_elem); der aktuelle Listener
            //aus der Listener-Liste löscht und dann wieder hinzufügt
            Collection<LGMChangeListener> listenersClone = new ArrayList<>(listeners);
            for (LGMChangeListener l : listenersClone) {
                deliverEvent(l, source, last_elem);
            }
            //            for (LGMChangeListener l : listeners) {
            //                Sys.err1(l);
            //            }
            //            System.err.println();

            //Das hier stellt die Verbindung zwischen dem globalen Listener des Tools und dem für ein
            //GraphDocument bzw. einer GDCollection her.
            //Die folgenden Ereignisse werden von beiden Listenern weiter geleitet.
            //Ser source null check ist notwendig, weil sonst beim Öffnen eines Modells diese Ereignisse
            //hier fliegen und damit die Frames doppelt angelegt werden.
            if (deliverStatic && source != null) {
                if (this == SZENARIO_ADDED) {
                    Static.distribute(MODEL_CHANGE_SZENARIO_ADDED, source);
                } else if (this == SZENARIO_REMOVED) {
                    Static.distribute(MODEL_CHANGE_SZENARIO_REMOVED, source);
                } else if (this == SELECTED_SZENARIO_CHANGED) {
                    Static.distribute(MODEL_CHANGE_SELECTED_SZENARIO_CHANGED, source);
                }
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

    /**
     * Wenn sich das Standard-GraphElementLayout geaendert hat (nur GraphArea)
     */
    public void layoutChanged(GraphDocument source);

    /** Hier "sollte" sich meiner Ansicht nach der ModelBrowser erneuern */
    public void groupOrderChanged(GraphDocument source);

    /** Wichtig fuer die Werkzeugleiste, GraphArea sollte sich auch erneueern */
    public void activeLayerChanged(GraphDocument source);

    /** Wichtig für den ModelBrowser und die Frames */
    public void selectedSzenarioChanged(GraphDocument source);

    /** Hier die GraphArea und evtl. Farb-Buttons neu machen */
    public void colorsChanged(GraphDocument source);

    /** Hier GraphArea und evtl. ModelBrowser */
    public void selectionChanged(GraphDocument source);

    /**
     * Aktualisiert je nach Parameter alle Components, die den Title eines
     * Modells oder Teilmodells anzeigen. Wenn
     * <code>!(source instanceof Szenario)</code>, dann nur die Fenster eines
     * bestimmtem Modells () oder wenn <code>source instanceof Szenario</code>,
     * dann nur die Fenster eines bestimmten Szenarios. Ein Szenario hat max.
     * eine {@link GraphViewPaneFrameComponent} und dann noch beliebig viele
     * {@link MatrixViewPaneFrameComponent}. ModelBrowser + FrameTitle +
     * Fenster-Actions im Menü + ...
     *
     * @param source <code>null</code> = alle Modelle, Szenario = nur dieses
     *            Teilmodell, GraphDocument = Hauptmodell des Modells, bei dem
     *            alle Teilmodelle betroffen sind
     */
    public void modelOrSzenarioNameChanged(GraphDocument source);

    /** Hier Dialog(e), der/die die Beschreibung anzeigen */
    public void modelDescriptionChanged(GraphDocument source);

    /**
     * alle, die die Teilmodelle dieses Modells darstellen oder repräsentieren.
     * ACHTUNG: {@link Tooll3gmChangeListener} hat auch so ein Ereignis, das
     * aber immer gefeuert wird, wenn bei irgendeinem Modell ein Teilmodell
     * hinzugefügt wurde.
     */
    public void szenarioAdded(GraphDocument source);

    /**
     * alle, die die Teilmodelle dieses Modells darstellen oder repräsentieren.
     * ACHTUNG: {@link Tooll3gmChangeListener} hat auch so ein Ereignis, das
     * aber immer gefeuert wird, wenn bei irgendeinem Modell ein Teilmodell
     * entfernt wurde.
     */
    public void szenarioRemoved(GraphDocument source);

    /**
     * @param listeners
     * @param source
     * @param last_elem
     * @param deliverStatic if <code>true</code>, the SZENARIO_ADDED and
     *            SZENARIO_REMOVED will be distributet as
     *            MODEL_CHANGE_SZENARIO_ADDED and MODEL_CHANGE_SZENARIO_REMOVED
     *            to the {@link Tool3lgmChangeListener}
     */
    public static void distributeEvent(final LGMChangeType changeType, final List<LGMChangeListener> listeners, final GraphDocument source, final ElementContainer last_elem, final boolean deliverStatic) {
        if (source != null) {
            if (source.isVerificationMode()) {
                Sys.out1("distributeEvent: " + changeType);
            }
        }
        changeType.deliverEvent(listeners, source, last_elem, deliverStatic);
    }

}
