/*
 * Created on 21.02.2008
 */
package de.imise.tool3lgm.graphtools.userfield.event;

import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GDCollectionChangeAdapter;
import de.imise.tool3lgm.graphtools.model.GDCollectionChangeListener;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel.PropertyDialogUserFieldPanel;

/**
 * @author AXS
 */
public abstract class UserFieldDefinitionChangeHandler extends GDCollectionChangeAdapter {

    /**
     * Wenn diese Variable <code>true</code> ist, wird bei der Abfrage irgendeines Kennzahlformelwertes alles neu berechnet. Sie wird bei allen
     * Änderungen am Modell <code>true</code>.
     */
    private boolean reset = true;

    /** Das Modell für welches die UserField definiert sind */
    protected GDCollection gdcoll = null;

    /**
     * @param gdcoll
     */
    public UserFieldDefinitionChangeHandler(final GDCollection gdcoll) {
        super();
        this.gdcoll = gdcoll;
    }

    /**
     * @return Returns the gdcoll.
     */
    public GDCollection getCollection() {
        return gdcoll;
    }

    /** Liefert das MetaModel der zugehörigen {@link GDCollection} */
    public MetaModel getMetaModel() {
        return gdcoll.getMetaModel();
    }

    /**
     * Löscht alle Berechneten Werte, d.h. alle berechneten Werte werden inkonsitstent (null) gesetzt.
     */
    protected abstract void clearCalculatedUserFieldValues();

    /**
     * Über diese Funktion kann dem Calculator mitgeteilt werden, dass sich mind. eine Kennzahl geändert hat. Nachdem alle Kennzahlen geändert wurden,
     * kann dann die Funktion reset() aufgerufen werden, in der alle Kennzahlen neu berechnet werden. Der Calculator ist selbst kein
     * {@link GDCollectionChangeListener}, der auf <code>DATA_CHANGED</code> hört, weil er sonst bei jeder Änderung immer alles neu berechen würde.
     * Das <code>GraphDocument</code> setzt den <code>boolen reset</code> auf <code>true</code>, wenn das Kommando
     * <code>MODEL_ACTION_SET_USER_FIELD_VALUE</code>
     * ausgeführt wurde. Ein Aufruf der Funktion <code>reset()</code> nach dem <code>true</code>-setzen, führt dann tatsächlich zu der Neuberechnung.
     * Das macht aber nicht das <code>GraphDocument</code>, sondern das muss man selber machen, nachdem man alle Kennzahlen geändert hat.
     *
     * @see PropertyDialogUserFieldPanel#commit()
     */
    public final void initReset() {
        reset = true;
    }

    /**
     * Setzt alle berechneten Werte zurück
     */
    public final void reset() {
        //wenn sich irgendwas geändert hatte, ist reset auf true
        if (reset) {
            //alle berechneten Werte auf null zurücksetzen
            clearCalculatedUserFieldValues();
            reset = false;
        }
    }

    @Override
    public void dataChanged(final GraphDocument source) {
        initReset();
    }

}
