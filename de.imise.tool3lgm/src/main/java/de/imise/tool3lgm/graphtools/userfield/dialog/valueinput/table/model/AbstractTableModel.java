/*
 * Created on 25.10.2007
 */
package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.model;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.util.NamedObjectContainer;

/**
 * Model für alle Tables des Attributeditors
 * <p>
 * Über die statischen Methoden können vorgefertigte <code>AbstractUserFieldTableModel</code>s abgerufen werden, die auf Tabels für Kennzahlen,
 * Verteilungsgewicht oder Modelvariablen zugeschnitten sind.
 * <p>
 * !!! Die Änderung der Daten erfolgt nicht mehr über javax.swing.table.DefaultTableModel.setDataVector(java.lang.Object[][], java.lang.Object[])
 * sondern über setDataVector(java.lang.Object[][], java.lang.Object[],java.lang.Object[]). Die Anwendung der alten Methode kann dann dazu führen,
 * dass die im Table dargstellten Daten nicht mehr den Modeldaten entsprechen !!!
 * 
 * @author fstephan
 */
public abstract class AbstractTableModel extends DefaultTableModel {

    /* ************************ Start: Konstanten ************************************* */

    ///////////////////////////////////////////////
    /// Konstanten für die Auswahl              ///
    /// der im ClassificationNumberPanel        ///
    /// anzuzeigenden Elemente.                 ///
    /// Ist in createClassificationNumberModel( ///
    /// Class elementClass, GraphDocument doc,  ///
    /// int elementTypesToShow) für             /// 
    /// "elementTypesToShow" anzuwenden         ///
    ///////////////////////////////////////////////

    /**
     * Löst das Anzeigen aller ModelElemente im Table des <code>GeneralUserFieldEditorPanel</code>s aus
     */
    public static final int SHOW_ALL_ELEMENTS = 1;

    /**
     * Löst das Anzeigen der Blatt-ModelElemente im Table des <code>GeneralUserFieldEditorPanel</code>s aus
     */
    public static final int SHOW_LEAVES_ONLY = 2;

    /**
     * Löst das Anzeigen der Toplevel-ModelElemente im Table des <code>GeneralUserFieldEditorPanel</code>s aus
     */
    public static final int SHOW_TOPLEVEL_ELEMENTS_ONLY = 3;

    /* ************************ Ende: Konstanten ************************************** */

    /* *********************** Start: Deklaration ************************************* */

    /**
     * Das {@link GraphDocument} dieses Tables
     */
    protected GraphDocument doc;

    /**
     * Beinhaltet alle UserFields in einer HashMap, die für Knoten, Kanten und das Modell deklariert und definiert wurden.
     */
    protected final UserFieldDefinitions definitions;

    /**
     * Daten für die Zeilenköpfe des Tables
     */
    private Vector<Object> rowIdentifiers;

    /**
     * Daten des tables Beinhaltet im Gegensatz zum {@link DefaultTableModel#dataVector} die aus <code>UserField</code> und anzuzeigendem Wert
     * bestehenden <code>NamedObjectContainer</code>. Mittels der <code>UserField</code>s ist es dann für den <code>TableCellRenderer</code> möglich,
     * formatierte Werte darstellen zu können.
     */
    private Object[][] dataField;

    /**
     * Gibt zurück, ob sich Daten geändert haben
     */
    protected boolean dataChanged;

    /* *********************** Ende: Deklaration ************************************* */

    /* *********************** Start: Initialisierung ************************************* */

    /**
     * Konstruktor
     * 
     * @param doc
     */
    protected AbstractTableModel(final GraphDocument doc) {
        super();
        this.doc = doc;
        definitions = doc.getUserFieldDefinitions();
        rowIdentifiers = new Vector<Object>();
    }

    /* *********************** Ende: Initialisierung ************************************* */

    /* *********************** Start: get/set-Methoden ************************************* */

    /**
     * Liefert den ToolTipText für den Kopf der Spalte <code>columnIndex</code>.
     * 
     * @param columnIndex
     * @return
     */
    public String getColumnToolTip(final int columnIndex) {
        return columnIdentifiers.get(columnIndex).toString();
    }

    /**
     * Liefert den ToolTipText für den Kopf der Zeile <code>rowIndex</code>.
     * 
     * @param rowIndex
     * @return
     */
    public String getRowToolTip(final int rowIndex) {
        return rowIdentifiers.get(rowIndex).toString();
    }

    /**
     * Überschreibt die Methode der Superklasse, sodass in Abhängigkeit der Daten über die Editierbarkeit der Zelle an der Stelle (<code>row</code>,
     * <code>column</code>) entschieden wird.
     * 
     * @return <code>true</code>, falls ein Wert an der Stelle (<code>row</code>,<code>column</code>) exisitiert, sonst <code>false</code>
     * @see javax.swing.table.TableModel#isCellEditable(int, int)
     */
    @Override
    public boolean isCellEditable(final int row, final int column) {
        // Zelle an der Stelle (row,count) hat keinen Wert -> false, sonst true
        return column >= 0 && getValueAt(row, column) != null;
    }

    /**
     * Gibt die Spaltenköpfe wieder
     * 
     * @return <code>columnIdentifiers</code>
     */
    public Vector<Object> getColumnIdentifiers() {
        return columnIdentifiers;
    }

    /**
     * Methode setzt den <code>dataVector</code>, die <code>columnIdentifiers</code> und die <code>rowIdentifiers</code>. Speichert <code>data</code>
     * in <code>dataField</code> ab.
     * 
     * @param data
     * @param columnIdentifiers
     * @param rowIdentifiers
     */
    public void setDataVector(final Object[][] data, final Object[] columnIdentifiers, final Object[] rowIdentifiers) {

        if (data == null || data.length == 0) {
            super.setDataVector(data, columnIdentifiers);
            return;
        }

        if (this.rowIdentifiers == null) {
            this.rowIdentifiers = new Vector<Object>();
        }

        for (int i = 0; i < rowIdentifiers.length; i++) {
            this.rowIdentifiers.add(rowIdentifiers[i]);
        }

        dataField = new Object[data.length][data[0].length];
        dataField = new Object[rowIdentifiers.length][columnIdentifiers.length];
        for (int i = 0; i < dataField.length; i++) {
            for (int j = 0; j < dataField[0].length; j++) {
                dataField[i][j] = data[i][j];
            }
        }
        super.setDataVector(data, columnIdentifiers);

        Vector<Object> cIdentifiers = new Vector<Object>();
        for (int j = 0; j < columnIdentifiers.length; j++) {
            cIdentifiers.add(columnIdentifiers[j]);
        }
        this.columnIdentifiers = cIdentifiers;
    }

    /**
     * Methode gibt den <code>NamedObjectContainer</code> zurück, der das <code>UserField</code> und den Wert an der Stelle (row,col) enthält.
     * Überschreibt die Methode der Superklasse, damit der CellRenderer das Format feststellen kann.
     * 
     * @see javax.swing.table.DefaultTableModel#getValueAt(int, int)
     */
    @Override
    public final Object getValueAt(final int row, final int col) {
        return dataField[row][col];
    }

    /**
     * Gibt alle Werte aus dem angegebenen Bereich in einem <code>Object[][]</code> wieder.
     * 
     * @param firstRow erste Zeile, aber der die Werte zurückgegeben werden sollen
     * @param lastRow letzte Zeile, bis zu der die Werte zurückgegeben werden sollen
     * @param firstColumn erste Spalte, aber der die Werte zurückgegeben werden sollen
     * @param lastColumn letzte Spalte, bis zu der die Werte zurückgegeben werden sollen
     * @return
     */
    public Object[][] getValues(final int firstRow, final int lastRow, final int firstColumn, final int lastColumn) {
        int m = lastRow - firstRow + 1;
        int n = lastColumn - firstColumn + 1;

        Object[][] values = new Object[m][n];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                values[i][j] = getValueAt(i + firstRow, j + firstColumn);
            }
        }
        return values;
    }

    /**
     * Gibt alle Werte des Models in einem <code>Object[][]</code> wieder.
     * 
     * @see #getValues(int, int, int, int)
     * @return
     */
    public Object[][] getValues() {
        return dataField;
    }

    /**
     * Liefert einen {@link NamedObjectContainer} für der an die Zelle mit den gegebenen Indizes gesetzt wird. Unterklassen
     * müssen diese Container geeignet initialisieren.
     * 
     * @param value
     * @param row
     * @param col
     */
    public abstract NamedObjectContainer<?> getCreatedValueAt(final Object value, final int row, final int col);

    /**
     * Übernimmt einen im Table eingegebenen Wert in dieses Model. Dabei wird ein neuer <code>NamedObjectContainer</code> aus <code>value</code> und
     * dem bereits in {@link #dataField} enthaltenen <code>UserField</code> erstellt und als neuer Wert in {@link DefaultTableModel#dataVector} und
     * {@link #dataField} gesetzt.
     * 
     * @param value
     * @param row
     * @param col
     * @see javax.swing.table.DefaultTableModel#setValueAt(java.lang.Object, int, int)
     */
    @Override
    public void setValueAt(final Object value, final int row, final int col) {
        // neuer Container beinhaltet altes UserField "field" aber neuen Wert "value"
        NamedObjectContainer<?> newValue = getCreatedValueAt(value, row, col);
        // dataField update
        dataField[row][col] = newValue;
        // dataVector update
        super.setValueAt(newValue, row, col);
    }

    /**
     * Setzt die Modeldaten beginnend bei (<code>firstRow</code>,<code>firstColumn</code>) auf die Werte in <code>values</code>.<br>
     * Wird dabei die Größe des Models überschritten, gibt diese Methode <code>false</code> zurück.<br>
     * War das Übernehmen der Werte erfolgreich, wird <code>true</code> zurückgegeben.
     * 
     * @param values neue Werte
     * @param firstRow Zeilenindex, ab dem die Werte eingefügt werden sollen
     * @param firstColumn Spaltenindex, ab dem die Werte eingefügt werden sollen
     * @return
     */
    public boolean setValuesAt(final Object[][] values, final int firstRow, final int firstColumn) {

        if (values == null) {
            return false;
        }

        int m = values.length;
        int n = values[0].length;

        if (m + firstRow > getRowCount() || n + firstColumn > getColumnCount()) {
            return false;
        }

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                setValueAt(values[i][j], i + firstRow, j + firstColumn);
            }
        }
        return true;
    }

    /**
     * Setzt die Modeldaten auf die Werte in <code>values</code>.
     * 
     * @see AbstractUserFieldTableModel#setValuesAt(Object[][], int, int)
     * @param values neue Werte
     * @return
     */
    public boolean setValues(final Object[][] values) {
        return setValuesAt(values, 0, 0);
    }

    /**
     * Gibt die Zeilenköpfe wieder
     * 
     * @return rowIdentifiers
     */
    public Vector<Object> getRowIdentifiers() {
        return rowIdentifiers;
    }

    /**
     * Gibt wieder,
     * 
     * @return <code>this.columnIdentifiers != null</code>
     */
    public boolean hasColumnIdentifiers() {
        return columnIdentifiers != null;
    }

    /**
     * @return <code>this.rowIdentifiers != null</code>
     */
    public boolean hasRowIdentifiers() {
        return rowIdentifiers != null;
    }

    /**
     * Gibt wieder, ob sich Daten in diesem Model befinden oder nicht
     * 
     * @return
     */
    public boolean hasData() {
        return dataField != null && dataField.length > 0;
    }

    /**
     * Gibt zurück, ob sich Daten geändert haben
     */
    public boolean dataChanged() {
        return dataChanged;
    }

    /**
     * Methode setzt das Attribut {@link #dataChanged} und bestimmt damit, ob sich Daten geändert haben oder nicht. <br>
     * Löst {@link #fireTableDataChanged()} aus, falls <code>b</code> den Wert <code>true</code> hat.
     * 
     * @param b
     */
    public void dataChanged(final boolean b) {
        dataChanged = b;
        if (b == true) {
            fireTableDataChanged();
        }
    }

    /* *********************** Ende: get/set-Methoden ************************************* */

}
