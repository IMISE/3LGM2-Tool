/*
 * Created on 25.10.2007
 */
package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table;

import java.util.ArrayList;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.elements.Doppelkante;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.PartOfBeziehung;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.util.NamedObjectContainer;

/**
 * Model für alle Tables des Attributeditors
 * <p>
 * Über die statischen Methoden können vorgefertigte <code>UserFieldTableModel</code>s abgerufen werden, die auf Tabels für Kennzahlen,
 * Verteilungsgewicht oder Modelvariablen zugeschnitten sind.
 * <p>
 * !!! Die Änderung der Daten erfolgt nicht mehr über javax.swing.table.DefaultTableModel.setDataVector(java.lang.Object[][], java.lang.Object[])
 * sondern über setDataVector(java.lang.Object[][], java.lang.Object[],java.lang.Object[]). Die Anwendung der alten Methode kann dann dazu führen,
 * dass die im Table dargstellten Daten nicht mehr den Modeldaten entsprechen !!!
 * 
 * @author fstephan
 */
public class UserFieldTableModel extends DefaultTableModel {

    /* ************************ Start: Konstanten ************************************* */

    ///////////////////////////////////////////////
    /// Konstanten für die Auswahl 	            ///
    /// der im ClassificationNumberPanel        ///
    /// anzuzeigenden Elemente.                 ///
    /// Ist in createClassificationNumberModel( ///
    /// Class elementClass, GraphDocument doc,  ///
    /// int elementTypesToShow) für             /// 
    /// "elementTypesToShow" anzuwenden         ///
    ///////////////////////////////////////////////

    /**
     * Löst das Anzeigen aller ModelElemente im Table des <code>ClassificationNumberEditorPanel</code>s aus
     */
    public static final int SHOW_ALL_ELEMENTS = 1;

    /**
     * Löst das Anzeigen der Blatt-ModelElemente im Table des <code>ClassificationNumberEditorPanel</code>s aus
     */
    public static final int SHOW_LEAVES_ONLY = 2;

    /**
     * Löst das Anzeigen der Toplevel-ModelElemente im Table des <code>ClassificationNumberEditorPanel</code>s aus
     */
    public static final int SHOW_TOPLEVEL_ELEMENTS_ONLY = 3;

    /* ************************ Ende: Konstanten ************************************** */

    /* *********************** Start: Deklaration ************************************* */

    /**
     * Beinhaltet alle UserFields in einer HashMap, die für Knoten, Kanten und das Modell deklariert und definiert wurden.
     */
    private final UserFieldDefinitions definitions;

    /**
     * Teilmodell in dessen Kontext die Modeldaten stehen
     */
    private final GraphDocument doc;

    /**
     * Daten für die Zeilenköpfe des Tables
     */
    protected Vector<Object> rowIdentifiers;

    /**
     * Daten des tables Beinhaltet im Gegensatz zum {@link DefaultTableModel#dataVector} die aus <code>UserField</code> und anzuzeigendem Wert
     * bestehenden <code>NamedObjectContainer</code>. Mittels der <code>UserField</code>s ist es dann für den <code>TableCellRenderer</code> möglich,
     * formatierte Werte darstellen zu können.
     */
    private Object[][] dataField;

    /**
     * Gibt zurück, ob sich Daten geändert haben
     */
    private boolean dataChanged;

    /* *********************** Ende: Deklaration ************************************* */

    /* *********************** Start: Initialisierung ************************************* */

    /**
     * Konstruktor
     * 
     * @param doc
     */
    protected UserFieldTableModel(final GraphDocument doc) {
        super();
        this.doc = doc;
        definitions = doc.getCollection().getUserFieldDefinitions();
        rowIdentifiers = new Vector<Object>();
    }

    /**
     * Erstellt und setzt Kennzahlen-Modeldaten
     * 
     * @param elementClass
     * @param elementTypesToShow
     * @param userFieldStyle
     */
    private void setDataForCNTable(final Class<? extends ModelElement> elementClass, final boolean showTopLevel, final boolean showInner, final boolean showLeafs, final UserField.Style userFieldStyle) {
        // Ermitteln der UserFields zu elementClass
        ArrayList<UserField> userFieldList = new ArrayList<UserField>();
        for (UserField uf : definitions.getUserFields(elementClass)) {
            if (uf.hasStyle(userFieldStyle)) {
                userFieldList.add(uf);
            }
        }

        //Ermitteln der ModelElemente zu elementClass
        ArrayList<ModelElement> allModelElements = doc.getModelItems(elementClass, true, true);
        ArrayList<ModelElement> modelElements = new ArrayList<ModelElement>(allModelElements.size());
        //TODO:FST,XHB. Wenn die Kante PDVBKAWBVerb übergeben wurde, bleibt allModelElements leer. Ist auch richtig,solange es keine Soclhe Verbindung gibt. 
        // Dann sollte aber auch keine Exception mehr fliegen. prüf mal bitte, warum das so ist?!

        for (int i = 0; i < allModelElements.size(); i++) {
            ModelElement me = allModelElements.get(i);
            if (showTopLevel && !me.hasDirectParentContainer(doc)) { // Top-Level-E. anfügen
                modelElements.add(me);
            } else if (showInner && me.hasDirectParentContainer(doc) && me.hasDirectPartContainer(doc)) { // Innere E. anfügen
                modelElements.add(me);
            } else if (showLeafs && !me.hasDirectPartContainer(doc)) { // Blatt-E. anfügen
                modelElements.add(me);
            }
        }

        if (modelElements.size() == 0 || userFieldList.size() == 0) {
            modelElements.clear();
            userFieldList.clear();
        }

        // RowHeader aufbauen
        Object[] rowIdentifiers = new Object[modelElements.size()];
        for (int i = 0; i < rowIdentifiers.length; i++) {
            ModelElement me = modelElements.get(i);
            rowIdentifiers[i] = new NamedObjectContainer<ModelElement>(me, me.getName());
        }

        // ColumnHeader aufbauen
        Object[] columnIdentifiers = new Object[userFieldList.size()];
        for (int j = 0; j < columnIdentifiers.length; j++) {
            UserField f = userFieldList.get(j);
            columnIdentifiers[j] = new NamedObjectContainer<UserField>(f, f.getName());
        }

        //DataVector aufbauen
        Object[][] data = new Object[modelElements.size()][userFieldList.size()];
        for (int i = 0; i < data.length; i++) {
            ModelElement me = modelElements.get(i);
            for (int j = 0; j < data[0].length; j++) {
                UserField uf = userFieldList.get(j);
                String value = uf.getValue(me);
                data[i][j] = new NamedObjectContainer<UserField>(uf, value);
            }
        }

        //Daten setzen
        setDataVector(data, columnIdentifiers, rowIdentifiers);
    }

    /**
     * Erstellt und setzt Verteilungsgewicht-Modeldaten
     * 
     * @param edgeClass
     * @param rowElementClasses
     * @param colElementClasses
     * @param field
     * @param direction Richtung in der die ausgwählte Kante zu lesen ist. In der Tabelle sthen die Startklassen der Kante in den Zeilen, wenn
     *            <code>DoubleTrace.FORWARD</code> übergeben wurde. Bei <code>DoubleTrace.BACKWARD</code> stehen die Endklassenelemente in den Zeilen.
     */
    private void setDataForDWTable(final Class<? extends Kante> edgeClass, final int direction, final UserField field) {
        ArrayList<ModelElement> allRowElements = doc.getModelItems(Kante.getStartClass(edgeClass), false, true);
        ArrayList<ModelElement> allColumnElements = doc.getModelItems(Kante.getEndClass(edgeClass), false, true);
        ArrayList<ModelElement> rowElements = new ArrayList<ModelElement>(allRowElements.size());
        ArrayList<ModelElement> columnElements = new ArrayList<ModelElement>(allColumnElements.size());
        Object[][] temp_data = new Object[allRowElements.size()][allColumnElements.size()];

        // temp_data, rowElements, columnElements erstellen
        for (ModelElement re : allRowElements) {
            for (ModelElement ce : allColumnElements) {
                Kante edge = null;

                if (PartOfBeziehung.class.isAssignableFrom(edgeClass)) {
                    if (direction == Doppelkante.FORWARD) {
                        edge = ce.getEdgeTo(re, edgeClass);
                    } else {
                        edge = re.getEdgeTo(ce, edgeClass);
                    }
                } else {
                    ArrayList<Kante> edges = ce.getEdgesWith(re, edgeClass);
                    if (edges != null && edges.size() > 0) {
                        edge = edges.get(0);
                    }
                }

                if (edge == null) {
                    continue;
                }
                int columnIndex = columnElements.indexOf(ce);
                if (columnIndex == -1) {
                    columnIndex = columnElements.size();
                    columnElements.add(ce);
                }

                int rowIndex = rowElements.indexOf(re);
                if (rowIndex == -1) {
                    rowIndex = rowElements.size();
                    rowElements.add(re);
                }

                String value = field.getValue(edge);
                temp_data[rowIndex][columnIndex] = new NamedObjectContainer<UserField>(field, value);

            }
        }

        // RowHeader aufbauen
        Object[] rowIdentifiers = new Object[rowElements.size()];
        for (int i = 0; i < rowIdentifiers.length; i++) {
            ModelElement me = rowElements.get(i);
            rowIdentifiers[i] = new NamedObjectContainer<ModelElement>(me, me.getName());
        }

        // ColumnHeader aufbauen
        Object[] columnIdentifiers = new Object[columnElements.size()];
        for (int j = 0; j < columnIdentifiers.length; j++) {
            ModelElement me = columnElements.get(j);
            columnIdentifiers[j] = new NamedObjectContainer<ModelElement>(me, me.getName());
        }

        // DataVector aufbauen
        Object[][] data = new Object[rowIdentifiers.length][columnIdentifiers.length];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                if (temp_data[i][j] != null) {
                    data[i][j] = temp_data[i][j];
                }
            }
        }

        // Daten setzen
        this.setDataVector(data, columnIdentifiers, rowIdentifiers);
    }

    /**
     * Erstellt und setzt Modeldaten für Modelvariablen
     */
    private void setDataForMVTable() {

        GDCollection gdcoll = definitions.getCollection();

        // columIdentifiers erzeugen
        Object[] columnIdentifiers = new Object[] {
            "Wert"
        };

        // Liste aller globalen UserFields erstellen
        ArrayList<UserField> userFieldList = new ArrayList<UserField>();
        for (UserField uf : definitions.getGlobalUserFields()) {
            if (!uf.hasStyle(UserField.Style.FORMAT)) {
                userFieldList.add(uf);
            }
        }

        // Wenn keine Modelldaten existieren, wird auch keine Table angezeigt
        if (userFieldList.size() == 0) {
            return;
        }

        // rowIdentifiers und dataVector erzeugen
        Object[] rowIdentifiers = new Object[userFieldList.size()];
        Object[][] data = new Object[userFieldList.size()][1];
        for (int i = 0; i < rowIdentifiers.length; i++) {
            UserField uf = userFieldList.get(i);
            NamedObjectContainer<UserField> noc = new NamedObjectContainer<UserField>(uf, uf.getName());
            rowIdentifiers[i] = noc;
            String value = uf.getValue(gdcoll);
            data[i][0] = new NamedObjectContainer<UserField>(uf, value);
        }

        // Daten setzen
        setDataVector(data, columnIdentifiers, rowIdentifiers);
    }

    /* *********************** Ende: Initialisierung ************************************* */

    /* *********************** Start: statische Methoden ************************************* */

    /**
     * Erzeugt ein neues Model für den Table des <code>ClassificationNumberEditorPanel</code>s
     * 
     * @param elementClass in der <code>nodeBox</code> des <code>ClassificationNumberEditorPanel</code>s ausgewählte Klasse
     * @param doc
     * @param elementTypesToShow Typ der anzuzeigenden Elemente (siehe Konstanten)
     * @return
     */
    public static final UserFieldTableModel createClassificationNumberModel(final Class<? extends ModelElement> elementClass, final GraphDocument doc, final boolean showTopLevel, final boolean showInner, final boolean showLeafs) {
        UserFieldTableModel model = new UserFieldTableModel(doc);
        model.setDataForCNTable(elementClass, showTopLevel, showInner, showLeafs, UserField.Style.CLASSIFICATION_NUMBER);
        return model;
    }

    /**
     * Erzeugt ein neues Model für den Table des <code>ModelVariableEditorPanel</code>s
     * 
     * @param doc
     * @return
     */
    public static final UserFieldTableModel createClassificationModelVariableModel(final GraphDocument doc) {
        UserFieldTableModel model = new UserFieldTableModel(doc);
        model.setDataForMVTable();
        return model;
    }

    /**
     * Erzeugt ein neues Model für den Table des <code>ClassificationWeightingEditorPanel</code>s
     * 
     * @param elementClass
     * @param direction Richtung in die die übergebene Kantenklasse gelesen werden soll. (<code>Doppelkante.FORWARD</code> oder
     *            <code>Doppelkante.BACKWARD</code>)
     * @param field
     * @param doc
     * @return
     */
    public static final UserFieldTableModel createClassificationWeightingModel(final Class<? extends Kante> edgeClass, final int direction, final UserField field, final GraphDocument doc) {
        UserFieldTableModel model = new UserFieldTableModel(doc);
        model.setDataForDWTable(edgeClass, direction, field);
        return model;
    }

    /**
     * Erzeugt ein Model für einen Table, der für alle ModelElemente die berechneten Kennzahlen anzeigt. Ein Editieren dieses Tables ist nicht
     * möglich.
     * 
     * @param elementClass
     * @param doc
     * @param elementTypesToShow
     * @return
     */
    public static final UserFieldTableModel createClassificationNumberFormulaModel(final Class<? extends ModelElement> elementClass, final GraphDocument doc, final boolean showTopLevel, final boolean showInner, final boolean showLeafs) {
        UserFieldTableModel model = new UserFieldTableModel(doc) {
            /*
             * (non-Javadoc)
             * @see tool3lgm.graphtools.userfield.dialog.valueinput.table.UserFieldTableModel#isCellEditable(int, int)
             */
            @Override
            public boolean isCellEditable(final int row, final int col) {
                return false;
            }
        };
        model.setDataForCNTable(elementClass, showTopLevel, showInner, showLeafs, UserField.Style.CLASSIFICATION_NUMBER_FORMULA);
        return model;
    }

    /* *********************** Ende: statische Methoden ************************************* */

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

        // Zelle an der Stelle (row,count) hat keinen Wert
        if (column >= 0 && getValueAt(row, column) == null) {
            return false;
        }

        // sonst
        return true;
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
    public Object getValueAt(final int row, final int col) {
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

        // Das UserField des NamedObjectContainers aus dataField[row][col]
        @SuppressWarnings("unchecked")
        UserField field = ((NamedObjectContainer<UserField>) dataField[row][col]).getObject();

        // neuer Container beinhaltet altes UserField "field" aber neuen Wert "value"
        NamedObjectContainer<UserField> newValue = new NamedObjectContainer<UserField>(field, value.toString());

        // dataField update
        dataField[row][col] = newValue;

        // dataVector update
        super.setValueAt(newValue, row, col);
    }

    /**
     * Löscht den Wert der Zelle an der Position (<code>row</code>,<code>col</code>).
     * 
     * @param row Zeilenindex dieser Zelle
     * @param col Spaltenindex dieser Zelle
     */
    public void clearValueAt(final int row, final int col) {
        setValueAt(UserField.EMPTY_STRING, row, col);
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
     * @see UserFieldTableModel#setValuesAt(Object[][], int, int)
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