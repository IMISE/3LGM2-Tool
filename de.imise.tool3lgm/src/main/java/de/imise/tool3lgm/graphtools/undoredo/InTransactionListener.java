package de.imise.tool3lgm.graphtools.undoredo;

import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;

/** momentan wird dieser Listener ausschliesslich von den Dialogen genutzt */
public interface InTransactionListener {
    /** Hier die generelle Nachricht - alle sollten sich komplett erneuern */
    public void dataChanged(GraphDocument source, int pid);

    /** Hier reagiert bisher niemand, da Dialogen sowas egal ist */
    public void elementGraphicsChanged(GraphDocument source, ElementContainer element);

    /** Hier reagieren alle -> erneuern */
    public void elementAdded(GraphDocument source, ElementContainer element);

    /** Hier reagieren alle -> erneuern */
    public void elementDeleted(GraphDocument source, ElementContainer element);
}