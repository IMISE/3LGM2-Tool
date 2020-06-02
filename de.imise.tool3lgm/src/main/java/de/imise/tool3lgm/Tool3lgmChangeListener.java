package de.imise.tool3lgm;

import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMChangeListener;
import de.imise.util.event.ListenerSupport;

/**
 * Hier sind alle Ereignisse enthalten, die im gesamten Tool selbst relevant sind. SZENARIO_ADDED und SZENARIO_REMOVED sind eigentlich
 * Modell-spezifische Ereignisse und daher auch im {@link LGMChangeListener}, werden aber auch als Tool-spezifisches Ereignis behandelt, damit nicht
 * alle Klassen, die auf diese Ereignisse reagieren müssen immer an allen Teilmodellen als Listener hinzugefügt werden müssen. Wer sich bei diesen
 * Ereignissen nur für ein Modell interessiert, wird Listener des Modell. Wer sich bei den Ereignissen für alle Modelle gleichzeitig interessiert,
 * wird Listener dieser Klasse hier.
 *
 * @author AXS (15 Aug 2019)
 */
public interface Tool3lgmChangeListener {

    public static enum Tool3lgmChangeType {

        MODEL_CHANGE_MODEL_OPENED {
            @Override
            public void deliverEvent(final Tool3lgmChangeListener gdl, final GraphDocument source) {
                gdl.model_change_model_opened(source);
            }
        },
        MODEL_CHANGE_MODEL_CLOSED {
            @Override
            public void deliverEvent(final Tool3lgmChangeListener gdl, final GraphDocument source) {
                gdl.model_change_model_closed(source);
            }
        },
        MODEL_CHANGE_MODEL_SAVED {
            @Override
            public void deliverEvent(final Tool3lgmChangeListener gdl, final GraphDocument source) {
                gdl.model_change_model_saved(source);
            }
        },
        MODEL_CHANGE_SELECTED_SZENARIO_CHANGED {
            @Override
            public void deliverEvent(final Tool3lgmChangeListener gdl, final GraphDocument source) {
                gdl.model_change_selected_szenario_changed(source);
            }
        },
        MODEL_CHANGE_SZENARIO_ADDED {
            @Override
            public void deliverEvent(final Tool3lgmChangeListener gdl, final GraphDocument source) {
                gdl.model_change_szenario_added(source);
            }
        },
        MODEL_CHANGE_SZENARIO_REMOVED {
            @Override
            public void deliverEvent(final Tool3lgmChangeListener gdl, final GraphDocument source) {
                gdl.model_change_szenario_removed(source);
            }
        },

        ;

        /**
         * Stores the last fired change type to prevent circles in event delivering
         * (in combination with the {@link #lastDoc}
         */
        private static Tool3lgmChangeType lastChangeType;

        /**
         * Stores the doc which was the source of the last fired change type to prevent
         * circles in event delivering (in combination with the {@link #lastChangeType}
         */
        private static GraphDocument lastDoc;

        /**
         * @param l
         * @param source
         * @param last_elem
         */
        protected abstract void deliverEvent(Tool3lgmChangeListener l, final GraphDocument source);

        /**
         * @param listeners
         * @param source
         * @param last_elem
         */
        public void deliverEvent(final ListenerSupport<Tool3lgmChangeListener> listeners, final GraphDocument source) {
            //esp. selected doc changed events can be called in circle
            if (lastChangeType == this && lastDoc == source) {
                return;
            }
            lastChangeType = this;
            lastDoc = source;

            //das hier muss sein, weil es vorkommen kann, dass sich bei deliverEvent(l, source, last_elem); der aktuelle Listener aus der Listener-Liste löscht
            //eine andere Variante wäre, die Liste vorher zu clonen und auf dem Clone zu iterieren
            Tool3lgmChangeListener lastListener = null;
            for (int i = 0; i < listeners.size();) {
                Tool3lgmChangeListener l = listeners.get(i);
                if (l == lastListener) {
                    i++;
                    continue;
                }
                lastListener = l;
                //Sys.err(l.getClass().getSimpleName() + " " + source + " " + name());
                deliverEvent(l, source);
            }
        }

    }

    /** Fügt dem Tool <code>this</code> als Listener hinzu. */
    public default void addAsToolChangeListener() {
        Static.getTool().addChangeListener(this);
    }

    /** Entfernt <code>this</code> als Listener vom Tool. */
    public default void removeAsToolChangeListener() {
        Static.getTool().removeChangeListener(this);
    }

    /** alle die Modelle darstellen */
    public default void model_change_model_opened(final GraphDocument source) {
        model_change_changed(source);
    }

    /** alle die Modelle darstellen */
    public default void model_change_model_closed(final GraphDocument source) {
        model_change_changed(source);
    }

    /** alle die Modelle darstellen */
    public default void model_change_model_saved(final GraphDocument source) {
        model_change_changed(source);
    }

    /** alle, die (Teil-)Modelle darstellen oder repräsentieren */
    public default void model_change_selected_szenario_changed(final GraphDocument source) {
        model_change_changed(source);
    }

    /** alle, die (Teil-)Modelle darstellen oder repräsentieren */
    public default void model_change_szenario_added(final GraphDocument source) {
        model_change_changed(source);
    }

    /** alle, die (Teil-)Modelle darstellen oder repräsentieren */
    public default void model_change_szenario_removed(final GraphDocument source) {
        model_change_changed(source);
    }

    /**
     * In der default-Implementierung ruft jede andere change-Funktion dieses Listeners einfach diese Funktion hier auf. Überschreibt man sie, fängt
     * man automatisch jedes Ereignis als Listener ab.
     *
     * @param source das Teilmodell, für das das Change-Ereignis ausgelöst wurde
     */
    public default void model_change_changed(final GraphDocument source) {
    }

}
