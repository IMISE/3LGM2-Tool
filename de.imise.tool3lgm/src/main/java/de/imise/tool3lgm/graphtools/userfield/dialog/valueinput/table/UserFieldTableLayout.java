/*
 * Created on 14.02.2008
 */
package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table;

import static de.imise.tool3lgm.graphtools.userfield.UserField.Style.CLASSIFICATION_NUMBER;
import static de.imise.tool3lgm.graphtools.userfield.UserField.Style.COMBO_BOX;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.ScrollPane;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.NumberFormat;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.UIManager;
import javax.swing.event.CellEditorListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;

import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.util.NamedObjectContainer;
import de.imise.util.swing.component.AlphabeticalComboBox;
import de.imise.util.swing.component.text.ExtendedTextField;
import de.imise.util.swing.component.text.NumberTextField;

/**
 * Klasse repräsentiert ein konkretes Layout für einen <code>UserFieldTable</code>.
 * <p>
 * Es werden Methoden bereitgestellt, die einen <code>UserFieldTable</code> in einen geeigneten Container einbetten und für diesen das gewählte Layout
 * setzen.
 * <p>
 * Über statische Methoden können vorgefertigte <code>UserFieldTableLayout</code>s abgerufen werden, die auf Tabels für Kennzahlen, Verteilungsgewicht
 * oder Modelvariablen zugeschnitten sind.
 * <p>
 * Es wird ein RowHeader(optional) und ColumnHeader mit Tooltips gesetzt. Der RowHeader und alle Spalten lassen sich in ihrer Größe ändern.
 * <p>
 * Werte im Table werden in formatierter Form dargestellt.
 * <p>
 * Komponenten, die einen solchen <code>UserFieldTable</code> darstellen, sollten nicht den Table selbst, sondern den Container verwenden.<br>
 * Beispiel:
 * 
 * <pre>
 * public void add(Component comp, Object constraints) {
 * 
 *     if (comp instanceof UserFieldTable) {
 *         this.add(((UserFieldTable) comp).getLayoutContainer(), constraints);
 * 
 *     } else {
 *         super.add(comp, constraints);
 *     }
 * }
 * 
 * public void remove(Component comp) {
 *     if (comp instanceof UserFieldTable) {
 *         super.remove(((UserFieldTable) comp).getLayoutContainer());
 *     } else
 *         super.remove(comp);
 * }
 * </pre>
 * 
 * @author fstephan
 */
public class UserFieldTableLayout {

    /* ****************************** Beginn: Konstanten ********************************* */

    /** Maximale initiale Breite des rowHeaders */
    public static final int MAX_INITIAL_ROW_HEADER_WIDTH = 200;

    /** Minimale Breite des rowHeaders */
    public static final int MIN_ROW_HEADER_WIDTH = 50;

    /* ****************************** Ende: Konstanten *********************************** */

    /* ************************************ Start: Deklaration ************************************ */

    /**
     * Gibt wieder, ob der Table Zeilenköpfe enthält
     */
    private final boolean hasRowHeader;

    /**
     * Gibt wieder, ob nicht editierbare Zellen grau dargstellt werden sollen
     */
    private final boolean changeDeactivatedCellColor;

    /* ************************************ Ende: Deklaration ************************************ */

    /* ************************************ Start: Initialisierung ************************************ */

    /**
     * Erzeugt ein neues Layout Falls <code>hasRowHeader</code> = <code>true</code> wird ein grafisch abgewandelter <code>RowHeader</code> gewählt.
     * Falls <code>changeDeactivatedCellColor = true</code>, werden nicht editierbare Zellen grau gefärbt.
     * 
     * @param hasRowHeader
     * @param changeDeactivatedCellColor
     */
    protected UserFieldTableLayout(final boolean hasRowHeader, final boolean changeDeactivatedCellColor) {
        this.hasRowHeader = hasRowHeader;
        this.changeDeactivatedCellColor = changeDeactivatedCellColor;
    }

    /**
     * Erzeugt einen TableHeader, der ToolTips anzeigt und gibt diesen zurück
     * 
     * @param table
     */
    private JTableHeader createColumnHeader(final UserFieldTable table) {

        // Erzeuge toolTips
        Vector<?> identifiers = table.getColumnIdentifiers();
        int n = identifiers.size();
        final String[] toolTips = new String[n];
        for (int i = 0; i < n; i++) {
            toolTips[i] = identifiers.get(i).toString();
        }

        // Setzte Header mit Tooltips
        final JTableHeader columnHeader = new JTableHeader(table.getColumnModel()) {
            @Override
            public String getToolTipText(final MouseEvent e) {
                java.awt.Point p = e.getPoint();
                int index = columnModel.getColumnIndexAtX(p.x);
                int realIndex = columnModel.getColumn(index).getModelIndex();
                return toolTips[realIndex];
            }
        };

        // keine Spaltenvertauschungen zulassen
        columnHeader.setReorderingAllowed(false);

        return columnHeader;
    }

    /**
     * Setzt für jede Zelle des Tables ein <code>UserFieldTableCell</code> als Editor und Renderer und legt die Zeilenhöhe fest.
     * 
     * @param table
     */
    private void setTableCells(final UserFieldTable table) {
        updateTableCells(table);
        table.setRowHeight(UserFieldTableCell.PREFERRED_CELL_HEIGHT);
    }

    /**
     * <code>RowHeaderRenderer</code> wird als Renderer für die Zeilenköpfe gesetzt
     * 
     * @param table
     */
    private void setRowHeader(final UserFieldTable table, final JScrollPane layoutContainer) {

        JList rowHeader = new JList(table.getRowIdentifiers());

        // Minimale und initiale Größe des rowHeaders festlegen
        rowHeader.setFixedCellHeight(table.getRowHeight());
        rowHeader.setFixedCellWidth(calculateRowHeaderWidth(rowHeader));
        Dimension minDim = new Dimension(MIN_ROW_HEADER_WIDTH, UserFieldTableCell.MIN_CELL_HEIGHT);
        rowHeader.setMinimumSize(minDim);
        rowHeader.setCellRenderer(new RowHeaderRenderer(table));
        rowHeader.setBackground(table.getTableHeader().getBackground());
        rowHeader.setFocusable(false);

        layoutContainer.setRowHeaderView(rowHeader);
    }

    /**
     * Berechnet die durchschnittliche Breite der Zellen des rowHeaders. Liegt der errechnete Wert oberhalb der maximalen Breite, wird
     * {@link #MAX_INITIAL_ROW_HEADER_WIDTH} genommen, bzw. {@link #MIN_ROW_HEADER_WIDTH}, falls die minimale Breite unterschritten wird.
     * 
     * @return
     */
    private static final int calculateRowHeaderWidth(final JList header) {
        ListModel m = header.getModel();
        int commonTextLength = 0;
        int cellCount = m.getSize();
        for (int i = 0; i < cellCount; i++) {
            commonTextLength += header.getFontMetrics(header.getFont()).stringWidth(m.getElementAt(i).toString());
        }
        if (cellCount == 0) {
            return MIN_ROW_HEADER_WIDTH;
        }
        int avarageTextLength = commonTextLength / cellCount + 15;
        if (avarageTextLength > MAX_INITIAL_ROW_HEADER_WIDTH) {
            return MAX_INITIAL_ROW_HEADER_WIDTH;
        }
        if (avarageTextLength < MIN_ROW_HEADER_WIDTH) {
            return MIN_ROW_HEADER_WIDTH;
        }
        return avarageTextLength;
    }

    /**
     * Aktiviert die Änderbarkeit der RowHeader-Breite
     * 
     * @param layoutContainer
     */
    private void initResizableRowHeader(final JScrollPane layoutContainer) {
        JList rowHeader = (JList) layoutContainer.getRowHeader().getView();
        JTable table = (JTable) layoutContainer.getViewport().getView();

        // Alten Listener entfernen
        removeRowHeaderResizeListener(layoutContainer);

        RowHeaderResizeListener rl = new RowHeaderResizeListener(layoutContainer);

        // Listener an das ScrollPane, den Table und den RowHeader anfügen
        rowHeader.addMouseListener(rl);
        rowHeader.addMouseMotionListener(rl);
        table.addMouseListener(rl);
        table.addMouseMotionListener(rl);
        layoutContainer.addMouseListener(rl);
        layoutContainer.addMouseMotionListener(rl);
    }

    /**
     * Entfernt den alten <code>RowHeaderResizeListener</code>
     * 
     * @param layoutContainer
     */
    private static final void removeRowHeaderResizeListener(final JScrollPane layoutContainer) {
        JList rowHeader = (JList) layoutContainer.getRowHeader().getView();
        JTable table = (JTable) layoutContainer.getViewport().getView();
        MouseListener[] mls = table.getMouseListeners();
        for (int i = 0; i < mls.length; i++) {
            if (mls[i] instanceof RowHeaderResizeListener) {
                RowHeaderResizeListener rl = (RowHeaderResizeListener) mls[i];
                rowHeader.removeMouseListener(rl);
                rowHeader.removeMouseMotionListener(rl);
                table.removeMouseListener(rl);
                table.removeMouseMotionListener(rl);
                layoutContainer.removeMouseListener(rl);
                layoutContainer.removeMouseMotionListener(rl);
            }
        }
    }

    /* ************************************ Ende: Initialisierung ************************************ */

    /* ************************************ Start: funktionale Methoden ************************************ */

    /**
     * Aktualisiert die Zellen, sowie Row- und ColumnHeader des Tables.
     * <p>
     * Diese Methode ist bei der Initialisierung des <code>table</code>s und nach jedem Neusetzen des dazugehörigen TableModels auszuführen.
     * <p>
     * Im Falle, dass der <code>table</code> noch nicht in seinen <code>layoutContainer</code> eingebettet wurde, wird dies hier noch erledigt.
     * <p>
     * Im Falle, dass der <code>table</code> noch keinen <code>layoutContainer</code> besitzt, wird eine {@link IllegalArgumentException} geworfen.
     * 
     * @param table
     * @throws IllegalArgumentException
     */
    public void update(final UserFieldTable table) throws IllegalArgumentException {

        JScrollPane layoutContainer;

        // Prüfen, ob table schon einen LayoutContainer besitzt
        if (table.getLayoutContainer() != null) {// table besitzt einen layoutContainer
            layoutContainer = table.getLayoutContainer();
        } else { // table besitzt keinen layoutContainer
            throw new IllegalArgumentException("Der " + table.getClass().getSimpleName() + " " + table + " besitzt noch keinen layoutContainer!\n" + "siehe Methode: UserFieldTableLayout.createLayoutContainer(UserfieldTable)");
        }

        if (table.getViewport().getView() != table) {
            embed(table, layoutContainer);
        }

        // Falls das Model keine Daten enthält, werden headers und tableCells nicht gesetzt
        if (!table.hasUserFieldTableModel() || !table.hasData()) {
            return;
        }

        // Setzen der Renderer- und Editorkomponenten für die Zellen des Tables
        setTableCells(table);

        // Erzeugen und Setzen des ColumnHeaders
        JTableHeader h = createColumnHeader(table);
        table.setTableHeader(h);
        layoutContainer.setColumnHeaderView(h);

        // Erzeugen und Setzen des RowHeaders
        if (hasRowHeader == true) {
            setRowHeader(table, layoutContainer);
            initResizableRowHeader(layoutContainer);
        }
    }

    /**
     * Erzeugt ein {@link JScrollPane} und gibt dieses wieder.
     * <p>
     * Zu Darstellung des Tables, muss dieses ScrollPane verwendet werden, nachdem der Table durch {@link #embed(UserFieldTable, JScrollPane)}
     * eingebettet wurde. Andernfalls erfolgt keine Darstellung des RowHeaders.
     * 
     * @return
     */
    public JScrollPane createLayoutContainer() {
        return new JScrollPane();
    }

    /**
     * Bettet den <code>table</code> in <code>layoutContainer</code> ein.<br>
     * Um die grafische Darstellung zu aktualisieren, ist möglicherweise noch ein Aufruf von {@link #update(UserFieldTable)} für den
     * <code>table</code> notwendig.
     * <p>
     * Falls das Einbetten gelingt, wird <code>true</code> zurückgegeben, sonst <code>false</code>.
     * 
     * @param table Table, der in den <code>layoutContainer</code> einzubetten ist
     * @param layoutContainer Scrollpane, in das der <code>table</code> eingebettet werden soll
     */
    public boolean embed(final UserFieldTable table, final JScrollPane layoutContainer) {
        if (table == null || layoutContainer == null) {
            return false;
        }
        layoutContainer.setViewportView(table);
        return true;
    }

    /**
     * Aktualisiert alle Zellen des <code>table</code>s
     * 
     * @param table
     */
    public void updateTableCells(final UserFieldTable table) {
        // Falls das Model keine Daten enthält, werden headers nicht gesetzt
        if (!table.hasUserFieldTableModel() || !table.hasData()) {
            return;
        }
        UserFieldTableCell[][] tableCells;
        if (table.isFormattingActive() == true) {
            tableCells = new UserFieldTableCell[table.getRowCount()][table.getColumnCount()];
            for (int i = 0; i < tableCells.length; i++) {
                for (int j = 0; j < tableCells[0].length; j++) {
                    NamedObjectContainer<UserField> container = (NamedObjectContainer<UserField>) table.getValueAt(i, j);
                    if (changeDeactivatedCellColor) { // nicht editierbare
                                                      // Zellen grau
                        if (table.isCellEditable(i, j) == true) {
                            tableCells[i][j] = new UserFieldActivatedTableCell(container, table);
                        } else {
                            tableCells[i][j] = new UserFieldDeactivatedTableCell();
                        }
                    } else {
                        tableCells[i][j] = new UserFieldActivatedTableCell(container, table);
                    }
                }
            }
        } else {
            tableCells = null;
        }
        table.setTableCells(tableCells);
    }

    /**
     * Deaktiviert die formatierte Darstellung der Werte, das Resizing des RowHeaders, die Tooltips des RowHeaders und die Farbänderung nicht
     * editierbarer Zellen
     * 
     * @param table
     */
    public void removeFrom(final UserFieldTable table) {
        JScrollPane layoutContainer = table.getLayoutContainer();
        if (layoutContainer != null) {
            removeRowHeaderResizeListener(layoutContainer);
        }
        table.setTableCells(null);
        if (layoutContainer.getRowHeader().getView() != null) {
            ((JList) table.getLayoutContainer().getRowHeader().getView()).setToolTipText(null);
        }
    }

    /* ************************************ Ende: funktionale Methoden ************************************ */

    /* ************************************ Start: Unterklassen ************************************ */

    /**
     * Der Renderer für die JList, die als RowHeader angezeigt wird. Sie wird im Großen und Ganzen so dargestellt, wie der ColumnHeader. Außerdem wird
     * der Titel jeder Zeile als ToolTipText angezeigt.
     * 
     * @author AXS
     */
    private class RowHeaderRenderer extends JLabel implements ListCellRenderer {

        /**
         * ToolTipText für die Reihenköpfe
         */
        private final String[] toolTips;

        /**
         * Konstruktor
         * 
         * @param table Table, der diesen RowHeader besitzt
         */
        public RowHeaderRenderer(final UserFieldTable table) {

            JTableHeader header = table.getTableHeader();

            setOpaque(true);
            setBorder(UIManager.getBorder("TableHeader.cellBorder"));

            // Erzeuge toolTips
            Vector<?> identifiers = table.getRowIdentifiers();
            int n = identifiers.size();
            toolTips = new String[n];
            for (int i = 0; i < n; i++) {
                toolTips[i] = identifiers.get(i).toString();
            }
            setHorizontalAlignment(LEFT);
            setForeground(header.getForeground());
            setBackground(header.getBackground());
            setFont(header.getFont());
        }

        @Override
        public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
            setText(value == null ? "" : value.toString());
            setToolTipText(toolTips[index]);
            return this;
        }

    }

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
    private class UserFieldActivatedTableCell implements UserFieldTableCell {

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
         * Falls {@link #value} {@link UserField#EMPTY_STRING} entspricht, wird {@link #xmlText} auf {@link #RENDERER_EMPTY_STRING}, und
         * {@link #value} auf einen neuen {@link NamedObjectContainer} mit {@link #userField} und {@link EDITOR_EMPTY_STRING} gesetzt. Das heißt, dass
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
            this.update();
            editor.removeCellEditorListener(l);
        }

        /**
         * Gibt die Rendererkomponente dieser Zelle wieder. <br>
         * Hat eine Zelle keinen Focus, besteht trotzdem die Möglichkeit, dass sich durch Mehrfachauswahl ihr Wert geändert haben könnte. In diesem
         * Falle holt sich die Zelle aktuelle Werte aus dem zum {@link #table} gehörigen {@link TableModel} und setzt {@link #value} und
         * {@link #xmlText} entsprechend neu.
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

    /**
     * Repräsentiert eine nicht editierbare Zelle eines UserFieldTables. Ist Editor- und die Renderer-Komponente für eine solche Zelle. Sorgt dafür,
     * dass die Zelle grau dargstellt wird und nicht editierbar ist.
     * 
     * @author fstephan
     */
    private class UserFieldDeactivatedTableCell implements UserFieldTableCell {

        /**
         * StandardRenderer
         */
        private final DefaultTableCellRenderer renderer;

        /**
         * Konstruktor
         */
        public UserFieldDeactivatedTableCell() {
            renderer = new DefaultTableCellRenderer();
        }

        @Override
        public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected, final int row, final int column) {
            return null;
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }

        @Override
        public boolean isCellEditable(final EventObject anEvent) {
            return false;
        }

        @Override
        public boolean shouldSelectCell(final EventObject anEvent) {
            return false;
        }

        @Override
        public boolean stopCellEditing() {
            return false;
        }

        @Override
        public void cancelCellEditing() {
        }

        @Override
        public void addCellEditorListener(final CellEditorListener l) {
        }

        @Override
        public void removeCellEditorListener(final CellEditorListener l) {
        }

        /**
         * Gibt die Renderer-Komponente diese Zelle zurück. Setzt die BackgroundColor auf Grau
         * 
         * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
         */
        @Override
        public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
            Component c = renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            //nur die Stellen richtig ausgrauen, die gar keinen Wert haben -> nicht editierbare Zellen sehen auf den ersten Blick wie editierbare aus
            if (value == null) {
                c.setBackground(DEACTIVATED_CELL_BACKROUND_COLOR);
            }
            return c;
        }

        @Override
        public DefaultTableCellRenderer getRenderer() {
            return renderer;
        }

        @Override
        public DefaultCellEditor getEditor() {
            return null;
        }

        @Override
        public String getCellRendererValue() {
            return null;
        }
    }

    /**
     * Listener, der das Resizing des rowHeaders ermöglicht und überwacht
     * 
     * @author fstephan
     */
    private class RowHeaderResizeListener extends MouseAdapter {

        /**
         * MousePointer - Verschiebung <br>
         * Sorgt dafür, dass beim Eintreten eines Resize-Ereignisses der rowHeader das source-Objekt der MouseEvents ist.
         */
        private static final int MOUSE_POINT_X_PITCH = 1;

        /** der RowHeader des Tables */
        private final JList header;

        /** das ScrollPane, das den Table und den Header enthält */
        private final JScrollPane pane;

        /** gibt, wieder ob die Größe des RowHeaders gerade geändert wird */
        private boolean isResizing = false;

        /**
         * der MouseCursor <br>
         * ändert sich, wenn er sich auf dem Rand zwischen RowHeader und Table befindet
         */
        private Cursor cursor;

        /**
         * Gibt wieder, ob sich der {@link #cursor} auf dem Rand zwischen Table und RowHeader befindet.
         */
        private boolean canResize = false;

        /**
         * Konstruktor
         * 
         * @param sp {@link ScrollPane}, das den {@link #header} als <code>RowHeaderViewportView</code> beinhaltet
         */
        public RowHeaderResizeListener(final JScrollPane sp) {
            pane = sp;
            header = (JList) sp.getRowHeader().getView();
        }

        /**
         * Verändert den Cursor-Typ in Abhängigkeit seiner Position. Ist er über dem Rand zwischen rowHeader und table, wechselt er in die
         * resize-Dartsellung, sonst wir der Standard-Cursor angezeigt.
         * 
         * @param e
         */
        @Override
        public void mouseMoved(final MouseEvent e) {

            if (isResizing == true) {
                return;
            }

            int mouseX = e.getX() + MOUSE_POINT_X_PITCH;
            int headerX = header.getX() + header.getWidth();
            Rectangle visibleCells = header.getCellBounds(header.getFirstVisibleIndex(), header.getLastVisibleIndex());

            // Cursor über dem Rand von rowHeader und table
            if ((mouseX == headerX || mouseX + 1 == headerX) && e.getSource() == header && e.getY() <= visibleCells.y + visibleCells.height) {

                canResize = true;
                cursor = new Cursor(Cursor.E_RESIZE_CURSOR);
            } else { // sonst
                canResize = false;
                cursor = new Cursor(Cursor.DEFAULT_CURSOR);
            }

            if (cursor.getType() != pane.getCursor().getType()) {
                pane.setCursor(cursor);
            }
        }

        /**
         * Führt den resize aus. Unterbindet das Verkleinern, falls die minimale Breite unterschritten wird. Unterbindet das Vergrößern, falls die
         * Breite des rowHeaders fast die Fensterbreite erreicht.
         * 
         * @param e
         */
        @Override
        public void mouseDragged(final MouseEvent e) {

            if (canResize == false) {
                return;
            }

            isResizing = true;

            int mouseX = e.getX() + MOUSE_POINT_X_PITCH;

            /*
             * Verhindert, dass der rowHeader seine minimale Größe unterschreiten bzw. die Fenstergröße überschreiten kann
             */
            JViewport p = pane.getViewport();
            if (mouseX <= header.getMinimumSize().width || mouseX >= p.getWidth() + p.getX() - 10) {
                return;
            }

            header.setFixedCellWidth(mouseX);
        }

        /**
         * Zeigt das Ende eines Resize-Vorganges an
         */
        @Override
        public void mouseReleased(final MouseEvent e) {
            isResizing = false;
        }

    }

    /* ************************************ Ende: Unterklassen ************************************ */

    /* ************************************ Start: statische Methoden ************************************ */

    /**
     * Gibt ein Layout für einen CN-Table (Kennzahleingabe) wieder
     * 
     * @return
     */
    public final static UserFieldTableLayout getLayoutForClassificationNumberEditorTable() {
        UserFieldTableLayout uf = new UserFieldTableLayout(true, false);
        return uf;
    }

    /**
     * Gibt ein Layout für einen DW-Table wieder
     * 
     * @return
     */
    public final static UserFieldTableLayout getLayoutForDistributionWeightEditorTable() {
        return new UserFieldTableLayout(true, true);
    }

    /**
     * Gibt ein Layout für einen MV-Table wieder
     * 
     * @return
     */
    public final static UserFieldTableLayout getLayoutForModelVariableEditorTable() {
        return new UserFieldTableLayout(true, false);
    }

    /**
     * Gibt ein Layout für einen CNFTable wieder
     * 
     * @return
     */
    public final static UserFieldTableLayout getLayoutForClassificationNumberFormulaTable() {
        return new UserFieldTableLayout(true, false);
    }

    /**
     * Gibt ein Layout für einen Table zur Eingabe aller UserFields außer Kennzahlen und Kantengewichten wieder
     * 
     * @return
     */
    public final static UserFieldTableLayout getLayoutForGeneralUserFieldEditorTable() {
        UserFieldTableLayout uf = new UserFieldTableLayout(true, false);
        return uf;
    }

    /* ************************************ Ende: statische Methoden ************************************ */

}
