package de.imise.tool3lgm.graphtools.model;

import de.imise.tool3lgm.graphtools.userfield.UserFieldTarget;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;

/**
 * Vereinfachung des Interfaces {@link LGMChangeListener}. In jeder der Methoden wird die abstrakte Methode {@link #changed()} und
 * {@link #changed(GraphDocument)} oder {@link #changed(ElementContainer)} aufgerufen (je nach Context).
 *
 * @author AXS (Created on 21.02.2008)
 */
public interface GDCollectionChangeListenerSimple extends LGMChangeListener {

    @Override
    public default void dataChanged(final GraphDocument source) {
        changed();
        changed(source);
    }

    @Override
    public default void elementNameChanged(final ElementContainer source) {
        changed();
        changed(source);
    }

    @Override
    default void userFieldValueChanged(final UserFieldTarget source) {
        changed();
        changed(source);
    }

    @Override
    public default void elementGraphicsChanged(final ElementContainer source) {
        changed();
        changed(source);
    }

    @Override
    public default void layoutChanged(final GraphDocument source) {
        changed();
        changed(source);
    }

    @Override
    public default void groupOrderChanged(final GraphDocument source) {
        changed();
        changed(source);
    }

    @Override
    public default void activeLayerChanged(final GraphDocument source) {
        changed();
        changed(source);
    }

    @Override
    public default void colorsChanged(final GraphDocument source) {
        changed();
        changed(source);
    }

    @Override
    public default void selectionChanged(final GraphDocument source) {
        changed();
        changed(source);
    }

    @Override
    public default void modelOrSzenarioNameChanged(final GraphDocument source) {
        changed();
        changed(source);
    }

    public default void changed() {
    }

    public default void changed(final GraphDocument source) {
    }

    public default void changed(final ElementContainer elementContainer) {
    }

    public default void changed(final UserFieldTarget userFieldTarget) {
    }

}