/*
 * Created on 25.10.2007
 */
package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.model;

import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.util.NamedObjectContainer;

/**
 * Model für alle Tables des Attributeditors
 * <p>
 * Über die statischen Methoden können vorgefertigte
 * <code>AbstractUserFieldTableModel</code>s abgerufen werden, die auf Tabels
 * für Kennzahlen, Verteilungsgewicht oder Modelvariablen zugeschnitten sind.
 * <p>
 * !!! Die Änderung der Daten erfolgt nicht mehr über
 * javax.swing.table.DefaultTableModel.setDataVector(java.lang.Object[][],
 * java.lang.Object[]) sondern über setDataVector(java.lang.Object[][],
 * java.lang.Object[],java.lang.Object[]). Die Anwendung der alten Methode kann
 * dann dazu führen, dass die im Table dargstellten Daten nicht mehr den
 * Modeldaten entsprechen !!!
 *
 * @author fstephan
 */
public abstract class AbstractUserFieldTableModel extends AbstractTableModel {

    /**
     * Konstruktor
     *
     * @param doc
     */
    protected AbstractUserFieldTableModel(final GraphDocument doc) {
        super(doc);
    }

    @Override
    public final NamedObjectContainer<UserField> getContainerForNewValue(final Object value, final int row, final int col) {
        // Das UserField des NamedObjectContainers aus dataField[row][col]
        Object oldContainer = getValueAt(row, col);
        @SuppressWarnings("unchecked")
        UserField field = ((NamedObjectContainer<UserField>) oldContainer).getObject();
        // neuer Container beinhaltet altes UserField "field" aber neuen Wert "value"
        NamedObjectContainer<UserField> newValue = NamedObjectContainer.of(field, value.toString());
        return newValue;
    }

    /**
     * Setzt den Wert der Zelle in (row, col) auf {@link UserField#EMPTY_STRING}
     *
     * @param row
     * @param col
     */
    @Override
    public final void clearValueAt(final int row, final int col) {
        setValueAt(UserField.EMPTY_STRING, row, col);
    }
}
