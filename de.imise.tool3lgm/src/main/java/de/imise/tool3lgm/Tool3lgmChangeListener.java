package de.imise.tool3lgm;

import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMChangeListener;

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
        public void deliverEvent(final Iterable<Tool3lgmChangeListener> listeners, final GraphDocument source) {
            for (Tool3lgmChangeListener l : listeners) {
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
    }

    /** alle die Modelle darstellen */
    public default void model_change_model_closed(final GraphDocument source) {
    }

    /** alle die Modelle darstellen */
    public default void model_change_model_saved(final GraphDocument source) {
    }

    /** alle, die (Teil-)Modelle darstellen oder repräsentieren */
    public default void model_change_selected_szenario_changed(final GraphDocument source) {
    }

    /** alle, die (Teil-)Modelle darstellen oder repräsentieren */
    public default void model_change_szenario_added(final GraphDocument source) {
    }

    /** alle, die (Teil-)Modelle darstellen oder repräsentieren */
    public default void model_change_szenario_removed(final GraphDocument source) {
    }

}
