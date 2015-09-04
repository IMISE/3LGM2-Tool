package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table;

import static de.imise.tool3lgm.graphtools.userfield.UserField.Style.CLASSIFICATION_NUMBER;
import static de.imise.tool3lgm.graphtools.userfield.UserField.Style.COMBO_BOX;

import java.awt.Component;
import java.awt.Point;
import java.text.NumberFormat;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;

import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.util.NamedObjectContainer;
import de.imise.util.swing.component.AlphabeticalComboBox;
import de.imise.util.swing.component.text.ExtendedTextField;
import de.imise.util.swing.component.text.NumberTextField;

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
class UserFieldActivatedTableCell implements UserFieldTableCell {

    /**
     * <code>UserField</code>, dass das Format für die Wertdarstellung enthält
     */
    private final UserField userField;

    /**
     * Tatsächlicher Wert der Zelle
     */
    private NamedObjectContainer<UserField> value;

    /**
     * Angezeigter Wert in der Zelle
     */
    private String text;

    /**
     * Editor der Zelle
     */
    private DefaultCellEditor editor;

    /**
     * Renderer der Zelle
     */
    private DefaultTableCellRenderer renderer;

    /**
     * Table, der diese Zelle beinhaltet
     */
    private final UserFieldTable table;

    /**
     * TextField, dass die Editor-Komponente repräsentiert
     */
    private JComponent editorComponent;

    /**
     * Konstruktor
     * 
     * @param noc <code>NamedObjectContainer</code>, der das zur Zelle gehörige {@link UserField} und den Wert der Zelle enthält.
     * @param table <code>UserFieldTable</code>, der diese Zelle enthält
     */
    public UserFieldActivatedTableCell(final NamedObjectContainer<UserField> noc, final UserFieldTable table) {
        this.table = table;
        value = noc;
        userField = noc.getObject();
        initEditor();
        initRenderer();
        update();
    }

    /**
     * Initialisiert den <code>editor</code>
     */
    private void initEditor() {
        UserField userField = value.getObject();
        UserField.Style style = userField.getStyle();
        if (style == CLASSIFICATION_NUMBER) {
            NumberFormat numberFormat = userField.getNumberFormat();
            boolean isPositiveOnly = userField.isPositiveOnly();
            editorComponent = NumberTextField.getNumberTextField(numberFormat, isPositiveOnly);
            editor = new DefaultCellEditor((JTextField) editorComponent);
        } else if (style == COMBO_BOX) {
            AlphabeticalComboBox component = new AlphabeticalComboBox(true);
            for (int i = 0; i < userField.getListValuesCount(); i++) {
                String listValue = userField.getListValueAt(i);
                component.addItem(listValue);
                if (listValue.equals(value == null ? "" : value.toString())) {
                    component.setSelectedItem(listValue);
                }
            }
            editorComponent = component;
            editor = new DefaultCellEditor(component);
        } else {
            editorComponent = new ExtendedTextField();
            editor = new DefaultCellEditor((JTextField) editorComponent);
        }
    }

    /**
     * Initialisiert den <code>renderer</code>
     */
    private void initRenderer() {
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
    private void update() {
        if (value.toString().equals(UserField.EMPTY_STRING)) {
            text = RENDERER_EMPTY_STRING;
            value = new NamedObjectContainer<UserField>(userField, EDITOR_EMPTY_STRING);
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
     * <code>"EMPTY_STRING"</code> zurückgegeben. Falls sich der Wert im Editor nicht auf double parsen lässt, wird ein neuer
     * {@link NamedObjectContainer} mit {@link #userField} und <code>"NUMBER_FORMAT_ERROR"</code> zurückgegeben. Sonst wird ein neuer
     * {@link NamedObjectContainer} mit {@link #userField} und dem String im Editor zurückgegeben.
     * 
     * @see javax.swing.CellEditor#getCellEditorValue()
     */
    @Override
    public Object getCellEditorValue() {
        Object newValue = editor.getCellEditorValue();
        String s = newValue == null ? "" : newValue.toString();
        s = UserField.replaceWrongDecimalSeparator(s, EDITOR_DECIMAL_SEPARATOR);

        if (editor.getComponent() instanceof NumberTextField) {
            try {
                Double.parseDouble(s);
            } catch (NumberFormatException e) {
                if (s.equals(EDITOR_EMPTY_STRING)) {
                    s = UserField.EMPTY_STRING;
                } else {
                    s = UserField.NUMBER_FORMAT_ERROR;
                }
            }
        }

        value = new NamedObjectContainer<UserField>(userField, s);
        update();
        return value;
    }

    @Override
    public String getCellRendererValue() {
        return text;
    }

    @Override
    public boolean isCellEditable(final EventObject anEvent) {
        return editor.isCellEditable(anEvent);
    }

    /**
     * Wird beim Beginn des Editierens der Zelle durch die Maus aufgerufen. Der gesamte Text der Zelle wird markiert. Beim Aufruf dieser Methode
     * wird dem <code>AbstractUserFieldTableModel</code> mittegeteilt, dass sich Daten geändert haben.
     * 
     * @see DefaultCellEditor#shouldSelectCell(java.util.EventObject)
     */
    @Override
    public boolean shouldSelectCell(final EventObject anEvent) {
        table.fireTableDataChanged();
        // Gesamten Text markieren
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
    public DefaultCellEditor getEditor() {
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
