package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.cell;

import static de.imise.tool3lgm.graphtools.userfield.UserField.Style.COMBO_BOX;

import java.awt.Component;
import java.awt.Point;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;

import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserField.Style;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.UserFieldTable;
import de.imise.util.NamedObjectContainer;
import de.imise.util.swing.component.AlphabeticalComboBox;
import de.imise.util.swing.component.text.ExtendedTextField;

/**
 * Repräsentiert genau eine Zelle eines UserFieldTables. Ist Editor- und die Renderer-Komponente für eine Zelle. Sorgt dafür, dass der
 * anzuzeigende Wert der Zelle formatiert dargestellt wird. Bei Selektion mehrerer Zellen werden Wertänderungen in allen diesen Zellen übernommen.
 * Dieses Verhalten wird durch die entsprechend überschriebene Methode
 * {@link #getTableCellRendererComponent(JTable, Object, boolean, boolean, int, int)} erzielt. Zur Abfrage der Editor- bzw. Rendererkomponente
 * dienen die Methoden {@link #getTableCellEditorComponent(JTable, Object, boolean, int, int)} bzw.
 * {@link #getTableCellRendererComponent(JTable, Object, boolean, boolean, int, int)}. Die zurückgegebenen Komponenten sollten vom Table als
 * Editor- bzw. Rendererkomponente benutzt werden, um eine formatierte Darstellung der Werte zu erreichen. Außerdem wird dem
 * <code>AbstractUserFieldTableModel</code> des <code>UserFielTable</code>s, der diese Zelle beinhaltet, mitgeteilt, ob sich der Wert der Zelle
 * möglicherweiße geändert hat. Diese Änderungsbenachrichtigung erfolgt beim Beginn des Editierens dieser Zelle, d.h., nach dem Aufruf der Methode
 * {@link #shouldSelectCell(EventObject)} bzw. {@link #getTableCellEditorComponent(JTable, Object, boolean, int, int)}.
 *
 * @author fstephan
 */
public class UserFieldActivatedTableCell implements IUserFieldTableCell {

    /**
     * <code>UserField</code>, dass das Format für die Wertdarstellung enthält
     */
    protected final UserField userField;

    /**
     * Tatsächlicher Wert der Zelle
     */
    protected NamedObjectContainer<UserField> value;

    /**
     * Angezeigter Wert in der Zelle
     */
    protected String text;

    /**
     * Editor der Zelle
     */
    protected DefaultCellEditor editor;

    /**
     * Renderer der Zelle
     */
    protected DefaultTableCellRenderer renderer;

    /**
     * Table, der diese Zelle beinhaltet
     */
    protected final UserFieldTable table;

    /**
     * Konstruktor
     *
     * @param noc <code>NamedObjectContainer</code>, der das zur Zelle gehörige {@link UserField} und den Wert der Zelle enthält.
     * @param table <code>UserFieldTable</code>, der diese Zelle enthält
     * @param column Spalte der Cell
     */
    public UserFieldActivatedTableCell(final NamedObjectContainer<UserField> noc, final UserFieldTable table, final int column) {
        this.table = table;
        value = noc;
        userField = noc.getObject();
        initEditor(column);
        initRenderer();
        update();
    }

    /**
     * Konstruktor, wenn der Spaltenindex egal ist
     *
     * @param noc <code>NamedObjectContainer</code>, der das zur Zelle gehörige {@link UserField} und den Wert der Zelle enthält.
     * @param table <code>UserFieldTable</code>, der diese Zelle enthält
     */
    public UserFieldActivatedTableCell(final NamedObjectContainer<UserField> noc, final UserFieldTable table) {
        this(noc, table, -1);
    }

    /**
     * Initialisiert den <code>editor</code>
     *
     * @param column
     */
    protected void initEditor(final int column) {
        UserField userField = value.getObject();
        UserField.Style style = userField.getStyle();
        if (style == COMBO_BOX) {
            AlphabeticalComboBox<String> component = new AlphabeticalComboBox<>(true);
            for (int i = 0; i < userField.getListValuesCount(); i++) {
                String listValue = userField.getListValueAt(i);
                component.addObject(listValue);
                if (listValue.equals(value == null ? "" : value.toString())) {
                    component.setSelectedObject(listValue);
                }
            }
            editor = new DefaultCellEditor(component);
        } else {
            JTextField editorComponent = new ExtendedTextField();
            editor = new DefaultCellEditor(editorComponent);
        }
    }

    /**
     * Initialisiert den <code>renderer</code>
     */
    protected void initRenderer() {
        renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(HORIZONTAL_ALIGNMENT_RIGHT);
    }

    /**
     * Erzeugt eine formatierte Darstellung des aktuell in {@link #value} enthalten Wertes und setzt {@link #xmlText} auf diesen String. <br>
     * Falls {@link #value} {@link UserField#EMPTY_STRING} entspricht, wird {@link #xmlText} auf {@link #RENDERER_EMPTY_STRING}, und {@link #value}
     * auf einen neuen {@link NamedObjectContainer} mit {@link #userField} und {@link EDITOR_EMPTY_STRING} gesetzt. Das heißt, dass
     * der Renderer und der Editor ein leeres Feld anzeigen. <br>
     * Die Formatierung erfolgt dabei durch die Methoden von {@link #userField}.
     */
    protected void update() {
        if (!(userField.getStyle() == Style.CLASSIFICATION_NUMBER_FORMULA) && value.toString().equals(UserField.EMPTY_STRING)) {
            text = RENDERER_EMPTY_STRING;
            value = new NamedObjectContainer<>(userField, EDITOR_EMPTY_STRING);
        } else {
            text = userField.getFormattedValue(value, true);
        }
    }

    /**
     * Wird beim Beginn des Editierens der Zelle durch die Tastatur aufgerufen. Der gesamte Text der Zelle wird markiert. Beim Aufruf dieser
     * Methode wird dem <code>AbstractUserFieldTableModel</code> mittegeteilt, dass sich Daten geändert haben.
     *
     * @see TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
     */
    @Override
    public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected, final int row, final int column) {
        ((UserFieldTable) table).fireTableDataChanged();
        Component editorComponent = editor.getTableCellEditorComponent(table, this.value, isSelected, row, column);
        if (editorComponent instanceof JTextComponent) {
            JTextComponent c = (JTextComponent) editorComponent;
            // Gesamten Text markieren
            c.selectAll();
            c.revalidate();
        }
        return editorComponent;
    }

    /**
     * Gibt den tatsächlichen Wert dieser Zelle wieder und speicher ihn unter {@link #value} ab. <br>
     * Falls der Wert im Editor <code>""</code> entspricht, wird ein neuer {@link NamedObjectContainer} mit {@link #userField} und
     * <code>"EMPTY_STRING"</code> zurückgegeben. Falls sich der Wert im Editor nicht auf BigDecimal parsen lässt, wird ein neuer
     * {@link NamedObjectContainer} mit {@link #userField} und <code>"NUMBER_FORMAT_ERROR"</code> zurückgegeben. Sonst wird ein neuer
     * {@link NamedObjectContainer} mit {@link #userField} und dem String im Editor zurückgegeben.
     *
     * @see javax.swing.CellEditor#getCellEditorValue()
     */
    @Override
    public Object getCellEditorValue() {
        Object newValue = editor.getCellEditorValue();
        String s = newValue == null ? "" : newValue.toString();
        //bei Kennzahlen die evtl. falschen Decimal-Separatoren ersetzen
        if (userField.hasStyle(Style.CLASSIFICATION_NUMBER)) {
            s = UserField.replaceWrongDecimalSeparator(s, EDITOR_DECIMAL_SEPARATOR);
        }
        value = new NamedObjectContainer<>(userField, s);
        update();
        return value;
    }

    @Override
    public String getCellRendererValue() {
        return text;
    }

    @Override
    public final boolean isCellEditable(final EventObject anEvent) {
        return editor.isCellEditable(anEvent);
    }

    /**
     * Wird beim Beginn des Editierens der Zelle durch die Maus aufgerufen. Der gesamte Text der Zelle wird markiert. Beim Aufruf dieser Methode
     * wird dem <code>AbstractUserFieldTableModel</code> mittegeteilt, dass sich Daten geändert haben.
     *
     * @see DefaultCellEditor#shouldSelectCell(java.util.EventObject)
     */
    @Override
    public final boolean shouldSelectCell(final EventObject anEvent) {
        table.fireTableDataChanged();
        // Gesamten Text markieren
        Component editorComponent = editor.getComponent();
        if (editorComponent instanceof JTextComponent) {
            JTextComponent textField = (JTextComponent) editorComponent;
            textField.selectAll();
            textField.revalidate();
        }
        return editor.shouldSelectCell(anEvent);
    }

    @Override
    public boolean stopCellEditing() {
        return editor.stopCellEditing();
    }

    @Override
    public void cancelCellEditing() {
        editor.cancelCellEditing();
    }

    @Override
    public void addCellEditorListener(final CellEditorListener l) {
        editor.addCellEditorListener(l);
    }

    /**
     * Entfernt den Listener nach Beendigung des Editierens und löst das Aktualisieren von {@link #value} und {@link #xmlText} anhand der
     * eingegebenen Werte aus.
     *
     * @see javax.swing.CellEditor#removeCellEditorListener(javax.swing.event.CellEditorListener)
     */
    @Override
    public void removeCellEditorListener(final CellEditorListener l) {
        update();
        editor.removeCellEditorListener(l);
    }

    /**
     * Gibt die Rendererkomponente dieser Zelle wieder. <br>
     * Hat eine Zelle keinen Focus, besteht trotzdem die Möglichkeit, dass sich durch Mehrfachauswahl ihr Wert geändert haben könnte. In diesem
     * Falle holt sich die Zelle aktuelle Werte aus dem zum {@link #table} gehörigen {@link TableModel} und setzt {@link #value} und {@link #xmlText}
     * entsprechend neu.
     *
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
     */
    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
        this.value = (NamedObjectContainer<UserField>) table.getValueAt(row, column);
        update();
        DefaultTableCellRenderer c = (DefaultTableCellRenderer) renderer.getTableCellRendererComponent(table, text, isSelected, hasFocus, row, column);
        // Style der Zelle setzen
        setTableCellRendererComponentStyle(c, isSelected, this.table.isAnchorPoint(new Point(row, column)));
        return c;
    }

    /**
     * Setzt den Look der Zelle
     *
     * @param isSelected ist die Zelle selektiert?
     * @param isAnchor ist die Zelle Ausgangspunkt der aktuellen Selektion?
     * @param c die anzupassende Komponente
     */
    private void setTableCellRendererComponentStyle(final DefaultTableCellRenderer c, final boolean isSelected, final boolean isAnchor) {
        // Farbe der Selektierten Zellen ändern
        if (isSelected) {
            c.setBackground(SELECTION_BACKROUND_COLOR);
        } else {
            c.setBackground(DEFAULT_BACKROUND_COLOR);
        }

        // Umrandung für Anchor-Zelle setzen
        if (isAnchor) {
            c.setBorder(ANCHOR_BORDER);
        }
    }

    @Override
    public DefaultTableCellRenderer getRenderer() {
        return renderer;
    }

    @Override
    public final DefaultCellEditor getEditor() {
        return editor;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()));
        sb.append("[editor-value: " + value + " , renderer-value: " + text + "]");
        return sb.toString();
    }
}
