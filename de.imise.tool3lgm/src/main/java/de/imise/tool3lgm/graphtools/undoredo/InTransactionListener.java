package de.imise.tool3lgm.graphtools.undoredo;

import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;

/** momentan wird dieser Listener ausschliesslich von den Dialogen genutzt */
public interface InTransactionListener {
    /** Hier die generelle Nachricht - alle sollten sich komplett erneuern */
    public void dataChanged(GraphDocument source, int pid);

    /** Hier reagiert bisher niemand, da Dialogen sowas egal ist */
    public void elementGraphicsChanged(GraphDocument source, ElementContainer ec);

    /** Hier reagieren alle -> erneuern */
    public void elementAdded(GraphDocument source, ElementContainer ec);

    /** Hier reagieren alle -> erneuern */
    public void elementDeleted(GraphDocument source, ElementContainer ec);

    /** Hier reagieren alle, die den Namen anzeigen */
    public void elementNameChanged(ElementContainer ec);

    /** Hier reagieren alle, die UserFields anzeigen */
    public void userFieldValueChanged(ElementContainer ec);

}