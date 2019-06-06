package de.imise.tool3lgm.graphtools.metamodel.elements;

import javax.annotation.Nonnull;

import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.log.Log;

/**
 * Diese Klasse dient nur dazu Elemente anzulegen und die package-sichtbare Funktion {@link ModelElement#setMetaModel()} aufrufen zu können, was von
 * außerhalt nicht gehen soll.
 * Dieser ganze Umweg ist nur nötig, damit nicht jede Untrklasse von ModelElement einen Konstruktor braucht, in dem das MetaModel übergeben wird.
 *
 * @author AXS (23 May 2019)
 */
public class ModelElementInstanceCreator {

    /** das Metamodel, das an die ModelElements durchgereicht wird */
    private final MetaModel metaModel;

    /**
     * @param metaModel
     */
    public ModelElementInstanceCreator(@Nonnull final MetaModel metaModel) {
        this.metaModel = metaModel;
    }

    /**
     * Erzeugt eine neue Instanz eines Modellelementes.<br>
     * Loggt eine Fehlermedung, wenn Objekt nicht erzeugt werden konnte und <code>log</code> mit <code>true</code> übergeben wurde.
     *
     * @param elementClass Unterklasse von <code>ModelElement</code>
     * @param log wenn <code>true</code> wird ein eventuell auftretender Fehler geloggt
     * @return neues ModelElement der übergebenen Klasse oder <code>null</code>
     */
    public final <T extends ModelElement> T createElement(final Class<? extends T> elementClass, final boolean log) {
        try {
            T me = elementClass.newInstance();
            me.setMetaModel(metaModel);
            return me;
        } catch (Exception e) {
            if (log) {
                Log.show(Log.ERROR, "Konnte Klasse " + elementClass.getName() + " nicht erstellen.", e);
            }
            return null;
        }
    }

}
