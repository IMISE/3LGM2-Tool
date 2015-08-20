/*
 * Created on 13.01.2008
 */
package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.model.UserFieldTableModel;
import de.imise.util.NamedObjectContainer;
import de.imise.util.Pair;
import de.imise.util.clipboard.ContentExchangeListener;
import de.imise.util.clipboard.ContentExchanger;
import de.imise.util.clipboard.ContentManagerImpl;
import de.imise.util.clipboard.IllegalContentException;
import de.imise.util.clipboard.SimpleContentParser;
import de.imise.util.collections.CollectionUtils;

/**
 * Klasse repräsentiert einen speziellen <code>JTable</code>, der zur Eingabe und Darstellung von Kennzahlen, Verteilungsgewichten und Modelvaribalen
 * verwendet werden kann.
 * <p>
 * Es besteht die Möglichkeit, diese Tabelle in eine Komponente mit speziellem Layout einzubetten(im Allgem. ein <code>JScrollPane</code>). Die
 * grafische Darstellung des Tables ist dann über <code>getLayoutContainer()</code> zu erreichen.
 * <p>
 * Über eine <code>TableEditCondition</code> kann die Editierbarkeit einer jeden Zelle speziell festgelegt werden. Über die Konstanten können
 * spezielle AutoResizeModes aktiviert werden.
 * <p>
 * Code für die Verwendung des <code>container</code>s
 * 
 * <pre>
 * 	JPanel panel = new JPanel() {
 * 
 * 		public void add(Component comp, Object constraints) {
 * 
 * 			if (comp instanceof UserFieldTable) {
 * 				this.add(((UserFieldTable)comp).getLayoutContainer(),constraints);
 * 			}
 * 			else {
 * 				super.add(comp,constraints);
 * 				}
 * 		}	
 * 
 * 		public void remove(Component comp) {
 * 			if (comp instanceof UserFieldTable) {
 * 				super.remove(((UserFieldTable)comp).getLayoutContainer());
 * 			}
 * 		    else super.remove(comp);
 * 		}
 * 	}
 * </pre>
 * <p>
 * Spezielle Selektierungseigenschaften: <br>
 * Es ist möglich mittels Gedrückthalten der "Shift" oder "Strg" - Taste, mehrere Zellen auszuwählen und ebenso auch die Selektierung wieder
 * aufzuheben. Änderungen durch Werteeingabe werden auf alle ausgewählten Zellen übertragen.
 * <p>
 * Undo/Redo: <br>
 * Mit den Tastenkombinationen <b>Ctrl+Z</b> bzw. <b>Ctrl+Y</b> ist und Undo bzw. Redo möglich.
 * <p>
 * Cut/Copy/Paste: <br>
 * Mit den Tastenkombinationen <b>Ctrl+X</b>, <b>Ctrl+C</b>, <b>Ctrl+V</b> ist das Ausschneiden und das Kopieren von Werten aus dem Table in die
 * Systemzwischenablage möglich, sowie das Einfügen von Werten aus der Zwischenablage in den Table.
 * 
 * @author fstephan
 */
public class UserFieldTable extends JTable implements ContentExchanger {

    /**
     * Beschreibt die Editierbarkeit und den Selektionszustand der Zellen des Tables
     */
    private UserFieldTableController tableController;

    /**
     * Komponente, die diesen Table und das dazugehörige Layout enthält
     */
    private final JScrollPane layoutContainer;

    /**
     * Spezielles Layout für Cellrendering/-editing und RowHeaders
     */
    private UserFieldTableLayout tableLayout;

    /**
     * Drückt aus, ob Standard-AutoResizeModes von <code>JTable</code> verwendet werden sollen, oder nicht.
     * 
     * @value = true, falls <code>JTable</code> AutoResizeModes verwendet werden soll
     * @value = false, falls eigene AutoResizeModes verwendet werden sollen
     */
    private boolean superResize = false;

    /**
     * Bestimmt das Resize-Verhalten des Tables bei großem Fenster(Table kann vollständig in seiner<code>preferredSize</code> dargestellt werden).
     */
    private int autoResizeModeLarge;

    /**
     * Bestimmt das Resize-Verhalten des Tables bei kleinem Fenster (Table kann nicht vollständig in seiner<code>preferredSize</code> dargestellt
     * werden).
     */
    private int autoResizeModeSmall;

    /**
     * Spezielle Renderer- und Editorkomponenten für die Zellen des Tables. Werden zur formatierten Darstellung der Werte der Zellen verwendet.
     */
    private UserFieldTableCell[][] tableCells;

    /**
     * Gibt an, ob die Werte in den {@link #tableCells} formatiert dargestellt werden sollen
     */
    private boolean doFormatting;

    /** Speichert alle gemachten Wertänderung */
    private ArrayList<String[][]> undoStack;

    /** Speichert alle rückgängig gemachten Änderunge */
    private ArrayList<String[][]> redoStack;

    /* ************************ Beginn: Initialisierungsteil ***************************************** */

    /**
     * Konstruktor Setzt <code>uftm</code> als <code>dataModel</code>, <code>uftc</code> als <code>tableController</code> und bettet diesen Table in
     * eine durch <code>uftl</code> erzeugte Layout-Komponente ein.
     * 
     * @param uftm
     * @param uftl
     * @param tec
     * @return
     */
    public UserFieldTable(final UserFieldTableModel uftm, final UserFieldTableLayout uftl, final UserFieldTableController uftc) {
        super(uftm);
        tableController = uftc;
        tableLayout = uftl;
        layoutContainer = tableLayout.createLayoutContainer();
        init();
    }

    /**
     * Konstruktor Erzeugt einen leeren Table, der in eine durch <code>uftl</code> erzeugte Layout-Komponente besitzt.
     * 
     * @param uftl
     */
    public UserFieldTable(final UserFieldTableLayout uftl) {
        this(null, uftl, null);
    }

    /**
     * Initialisiert diesen Table. <br>
     * Es wird das AutoResize-Verhalten und das Focus-Verhalten festgelegt, ein {@link ContentExchangeListener} hinzugefügt sowie Undo bzw. Redo für
     * die Tastenkombinationen <b>Ctrl+Z</b> bzw. <b>Ctrl+Y</b> gesetzt.
     */
    private void init() {
        setRowSelectionAllowed(false);

        // Überwachung des resize-Modes
        getViewport().addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(final ComponentEvent e) {
                // small- u. largeMode-Resize-Verhalten gewählt
                if (!superResize) {
                    if (getViewport().getWidth() < getPreferredSize().width) {
                        // Table kann nicht in der preferredSize dargestellt werden -> aktiviere autoResizeModeSmall
                        UserFieldTable.super.setAutoResizeMode(autoResizeModeSmall);
                    } else {
                        // / Table kann vollständig in der preferredSize dargestellt werden -> aktiviere autoResizeModeLarge
                        UserFieldTable.super.setAutoResizeMode(autoResizeModeLarge);
                    }
                }
            }
        });

        this.setAutoResizeMode(AUTO_RESIZE_OFF, AUTO_RESIZE_SUBSEQUENT_COLUMNS);

        // undo & redo
        undoStack = new ArrayList<String[][]>(100);
        redoStack = new ArrayList<String[][]>(100);
        InputMap im = getInputMap();
        ActionMap am = getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK), new Integer(KeyEvent.VK_Z));
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK), new Integer(KeyEvent.VK_Y));
        am.put(new Integer(KeyEvent.VK_Z), new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                undo();
            }
        });
        am.put(new Integer(KeyEvent.VK_Y), new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                redo();
            }
        });

        // Selektion löschen, wenn Table den Focus verliert

        addFocusListener(new FocusListener() {

            @Override
            public void focusGained(final FocusEvent e) {
            }

            @Override
            public void focusLost(final FocusEvent e) {
                if (tableController != null) {
                    tableController.clearSelection();
                }
            }
        });
    }

    /* ************************ Ende: Initialisierungsteil ***************************************** */

    /* ************************ Beginn: Funktionale Methoden ***************************************** */

    /**
     * Aktiviert die Darstellung von Formaten in den Zellen.
     */
    public void activateFormatting() {
        doFormatting = true;
    }

    /**
     * Deaktiviert die Darstellung von Formaten in den Zellen.
     */
    public void deactivateFormatting() {
        doFormatting = false;
    }

    /**
     * Aktiviert die Mehrfach-Selektion von Zellen
     */
    public void activateMultipleSelection() {
        if (tableController != null && !tableController.isMultipleSelectionEnabled()) {
            tableController.enableMultipleSelection(this);
        }
    }

    /**
     * Deaktiviert die Mehrfach-Selektion von Zellen
     */
    public void deactivateMultipleSelection() {
        if (tableController != null) {
            tableController.disableMultipleSelection(this);
        }
    }

    /**
     * Löst das Neuzeichnen der Zelle an der Position (<code>row</code>,<code>column</code>) aus.
     * 
     * @param row Zeilennummer der Zelle
     * @param column Spaltennummer der Zelle
     * @param includeSpacing sagt aus, ob der Zellzwischenraum auch neu gezeichnet werden soll
     */
    public void repaintCellAt(final int row, final int column, final boolean includeSpacing) {
        repaint(getCellRect(row, column, includeSpacing));
    }

    /**
     * So überschrieben, dass Änderungen in der Selektion jetzt auch während gedrückter Shift-Taste an das {@link #selectionModel} übergeben werden.
     * 
     * @see javax.swing.JTable#changeSelection(int, int, boolean, boolean)
     * @param rowIndex
     * @param columnIndex
     * @param toggle
     * @param extend
     */
    @Override
    public void changeSelection(final int rowIndex, final int columnIndex, final boolean toggle, final boolean extend) {

        if (tableController != null) {
            tableController.changeSelection(rowIndex, columnIndex);
        }

        super.changeSelection(rowIndex, columnIndex, toggle, false);
    }

    /**
     * Beendet das Editieren der aktuell ausgewählten Zelle im Table.
     */
    public void stopEditing() {
        int row = getEditingRow();
        int column = getEditingColumn();

        if (row == -1 || column == -1) {
            return;
        }

        getCellEditor(row, column).stopCellEditing();
    }

    /**
     * Benachrichtigt das {@link #dataModel}, dass sich Daten geändert haben.
     */
    public void fireTableDataChanged() {
        if (hasUserFieldTableModel()) {
            ((UserFieldTableModel) dataModel).dataChanged(true);
        }
    }

    /**
     * Aktualisiert die gesamte grafische Darstellung des Tables und des umschließenden ScrollPanes.
     */
    public void updateLayout() {
        tableLayout.update(this);
        layoutContainer.revalidate();
        layoutContainer.repaint();
    }

    /**
     * Aktualisiert die grafische Darstellung der Zellen.
     */
    public void updateCells() {
        tableLayout.updateTableCells(this);
        layoutContainer.revalidate();
        layoutContainer.repaint();
    }

    @Override
    public void copy() {

        if (tableController == null) {
            return;
        }

        Pair<Point, Point> selectionRange = tableController.getCoherentlySelection();

        if (selectionRange == null) {
            showWarningMessage("userFieldTable_copy_warning1");
        } else {
            Point firstPoint = selectionRange.getFirstItem();
            Point lastPoint = selectionRange.getSecondItem();

            ContentManagerImpl cmi = ContentManagerImpl.getCurrentManager();
            SimpleContentParser scp = SimpleContentParser.getDefaultParser();

            Object[][] internalContent = getValues(firstPoint.x, lastPoint.x, firstPoint.y, lastPoint.y, true, true);
            try {
                cmi.setClipboardContent(scp.toExternalContent(internalContent));
            } catch (IllegalContentException e) {
                showWarningMessage("userFieldTable_copy_warning2");
            }
        }
    }

    @Override
    public void cut() {
        copy();
        clearValueAt(-1, -1);
    }

    @Override
    public void paste() {

        if (tableController == null) {
            return;
        }

        ContentManagerImpl cmi = ContentManagerImpl.getCurrentManager();
        SimpleContentParser scp = SimpleContentParser.getDefaultParser();
        Object[][] internalContent = null;
        try {
            internalContent = scp.toInternalContent(cmi.getClipboardContent());
        } catch (IllegalContentException e) {
            showWarningMessage("userFieldTable_paste_warning");
            return;
        }

        Point leadingPoint = tableController.getLeadSelectionPoint();

        if (leadingPoint.x == -1 || leadingPoint.y == -1) {
            return;
        }

        if (((UserFieldTableModel) dataModel).setValuesAt(internalContent, leadingPoint.x, leadingPoint.y) == false)
        // Einfügen nicht erfolgreich
        {
            showWarningMessage("userFieldTable_paste_warning");
        }
    }

    /**
     * Zeigt eine Warnung mit dem in den Resourcem durch <code>key</code> identifizierten Text an.
     * 
     * @param key
     */
    private void showWarningMessage(final String key) {
        JOptionPane.showMessageDialog(this, Tool3lgmConstants.getResString(key), Tool3lgmConstants.getResString("userFieldTable_warning"), JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Macht die letzte Eingabe rückgängig
     */
    public void undo() {

        int n = undoStack.size();

        if (n < 1) {
            return;
        }

        redoStack.add(CollectionUtils.toStringArray(((UserFieldTableModel) dataModel).getValues()));

        String[][] values = undoStack.remove(n - 1);
        ((UserFieldTableModel) dataModel).setValues(values);

    }

    /**
     * Macht das letzte Undo rückgängig
     */
    public void redo() {
        int n = redoStack.size();

        if (n < 1) {
            return;
        }

        undoStack.add(CollectionUtils.toStringArray(((UserFieldTableModel) dataModel).getValues()));

        String[][] values = redoStack.remove(n - 1);
        ((UserFieldTableModel) dataModel).setValues(values);

    }

    /**
     * Speichert die letzten Werte im {@link #undoStack} und löscht den {@link #redoStack}.
     */
    private boolean saveValues() {
        Object[][] data = ((UserFieldTableModel) dataModel).getValues();
        if (data == null) {
            return false;
        }

        redoStack.clear();
        undoStack.add(CollectionUtils.toStringArray(((UserFieldTableModel) dataModel).getValues()));
        return true;
    }

    /* ************************ Ende: Funktionale Methoden ***************************************** */

    /* ************************ Beginn: get/set - Methoden ***************************************** */

    /**
     * Methode setzt <code>dataModel</code> bzw. <code>tableController</code> auf <code>uftm</code> bzw. <code>uftc</code>.
     * 
     * @param uftm
     * @param uftc
     */
    public void setModel(final UserFieldTableModel uftm, final UserFieldTableController uftc) {
        super.setModel(uftm);
        if (uftc != null) {
            setUserFieldTableController(uftc);
        }
    }

    /**
     * Setzt das Attribut {@link #tableLayout} auf <code>uftl</code>.
     * 
     * @param uftl
     */
    public void setUserFieldTableLayout(final UserFieldTableLayout uftl) {
        if (tableLayout != null) {
            tableLayout.removeFrom(this);
        }
        tableLayout = uftl;
    }

    /**
     * Methode setzt <code>tableController</code> auf <code>uftc</code> und aktiviert Mehrfachauswahl von Zellen
     * 
     * @param uftc
     * @param enableMultipleSelection
     */
    public void setUserFieldTableController(final UserFieldTableController uftc) {
        if (tableController != null) {
            tableController.removeFrom(this);
        }
        tableController = uftc;
    }

    /**
     * Setzen des AutoResizeModes
     * 
     * @see JTable#setAutoResizeMode(int)
     * @param mode
     */
    @Override
    public void setAutoResizeMode(final int mode) {
        super.setAutoResizeMode(mode);
        superResize = true;
    }

    /**
     * Bestimmt jeweils das Resize-Verhalten für kleines und großes Fenster.
     * 
     * @see UserFieldTable#autoResizeModeSmall
     * @see UserFieldTable#autoResizeModeLarge
     * @param smallMode AutoResizeMode bei kleinem Fenster
     * @param largeMode AutoResizeMode bei großem Fenster
     */
    public void setAutoResizeMode(final int smallMode, final int largeMode) {

        if (smallMode == largeMode) {
            setAutoResizeMode(smallMode);
        } else {
            setAutoResizeMode(-1);
            superResize = false;
            autoResizeModeSmall = smallMode;
            autoResizeModeLarge = largeMode;
        }
    }

    /**
     * Übernimmt <code>value</code> für alle ausgewählten Zellen
     * 
     * @see JTable#setValueAt(java.lang.Object, int, int)
     */
    @Override
    public void setValueAt(final Object value, final int row, final int col) {

        if (!saveValues()) {
            return;
        }

        if (tableController == null) {
            super.setValueAt(value, row, col);
            return;
        }

        for (int i = 0; i < getRowCount(); i++) {
            for (int j = 0; j < getColumnCount(); j++) {
                if (isCellSelected(i, j)) {
                    super.setValueAt(value, i, j);
                }
            }
        }
    }

    /**
     * Löscht den Wert aller selektierten Zellen.
     * 
     * @see UserFieldTableModel#clearValueAt(int, int)
     * @param row
     * @param col
     */
    public void clearValueAt(final int row, final int col) {
        UserFieldTableModel model = (UserFieldTableModel) dataModel;
        if (!saveValues()) {
            return;
        }

        if (tableController == null) {
            model.clearValueAt(row, col);
            return;
        }

        for (int i = 0; i < getRowCount(); i++) {
            for (int j = 0; j < getColumnCount(); j++) {
                if (isCellSelected(i, j)) {
                    model.clearValueAt(i, j);
                }
            }
        }
    }

    /**
     * Setzt die Zellen des Tables
     * 
     * @param tableCells
     */
    void setTableCells(final UserFieldTableCell[][] tableCells) {
        this.tableCells = tableCells;
    }

    /**
     * Gibt die Zellen des Tables wieder.
     * 
     * @return {@link #tableCells}
     */
    public UserFieldTableCell[][] getTableCells() {
        return tableCells;
    }

    /**
     * Setzt die bevorzugte Größe für diesen Table.
     * 
     * @see JTable#setPreferredSize(Dimension)
     * @param width bevorzugte Breite des Tables
     * @param height bevorzugte Höhe des Tables
     */
    public void setPreferredSize(final int width, final int height) {
        super.setPreferredSize(new Dimension(width, height));
    }

    /**
     * Gibt den LayoutContainer dieses Tables zurück. Alle Klassen, die den Table darstellen wollen, sollten stattdessen das hier zurückgegebene
     * ScrollPane darstellen. Die Modifizierungen des RowHeaders sind sonst nicht sichtbar.
     * 
     * @return container
     */
    public JScrollPane getLayoutContainer() {
        return layoutContainer;
    }

    /**
     * Gibt das UserFieldTableLayout dieses Tables wieder
     * 
     * @return layout
     */
    public UserFieldTableLayout getUserFieldTableLayout() {
        return tableLayout;
    }

    /**
     * @return tableController
     */
    public UserFieldTableController getUserFieldTableController() {
        return tableController;
    }

    /**
     * Gibt den Renderer für die Zelle an Position (<code>row</code>,<code>column</code>) wieder. Der zurückgegebene Renderer sorgt für die
     * formatierte Darstellung der Werte in dieser Zelle.
     * 
     * @param row
     * @param column
     */
    @Override
    public TableCellRenderer getCellRenderer(final int row, final int column) {

        if (row == -1 || column == -1) {
            return null;
        }

        if (tableCells == null) {
            return super.getCellRenderer(row, column);
        }

        return tableCells[row][column];
    }

    /**
     * Gibt den Editor für die Zelle an Position (<code>row</code>,<code>column</code>) wieder. Der zurückgegebene Editor stellt den tatsächlichen
     * unformatierten Wert dar.
     * 
     * @param row
     * @param column
     */
    @Override
    public TableCellEditor getCellEditor(final int row, final int column) {

        if (row == -1 || column == -1) {
            return null;
        }

        if (tableCells == null) {
            return super.getCellEditor(row, column);
        }

        return tableCells[row][column];
    }

    /**
     * Gibt alle Werte aus dem angegebenen Bereich in einem <code>Object[][]</code> wieder.
     * 
     * @param firstRow erste Zeile, aber der die Werte zurückgegeben werden sollen
     * @param lastRow letzte Zeile, bis zu der die Werte zurückgegeben werden sollen
     * @param firstColumn erste Spalte, aber der die Werte zurückgegeben werden sollen
     * @param lastColumn letzte Spalte, bis zu der die Werte zurückgegeben werden sollen
     * @param replaceIgnorableErrors gibt an, ob Werte aus {@link UserField#IGNOREABLE_ERROR_SET} durch <code>""</code> ersetzt werden soll
     * @param replaceErrors gibt an, ob Werte aus {@link UserField#ERROR_SET} durch <code>""</code> ersetzt werden soll
     * @return
     */
    private Object[][] getValues(final int firstRow, final int lastRow, final int firstColumn, final int lastColumn, final boolean replaceIgnorableErrors, final boolean replaceErrors) {
        int m = lastRow - firstRow + 1;
        int n = lastColumn - firstColumn + 1;

        Object[][] values = new Object[m][n];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {

                NamedObjectContainer<UserField> value = (NamedObjectContainer<UserField>) getValueAt(i + firstRow, j + firstColumn);
                String toString = value.toString();

                if (replaceIgnorableErrors && UserField.isIgnoreableErrorString(toString)) {
                    toString = "";
                }
                if (replaceErrors && UserField.isErrorString(toString)) {
                    toString = "";
                }

                values[i][j] = new NamedObjectContainer<UserField>(value.getObject(), toString);
            }
        }
        return values;
    }

    /**
     * Gibt die Zeilenköpfe zurück.
     * 
     * @see UserFieldTableModel#getRowIdentifiers()
     * @return
     */
    public Vector<?> getRowIdentifiers() {
        return hasUserFieldTableModel() ? ((UserFieldTableModel) dataModel).getRowIdentifiers() : null;
    }

    /**
     * Gibt die Spaltenköpfe zurück.
     * 
     * @see UserFieldTableModel#getColumnIdentifiers()
     * @return
     */
    public Vector<?> getColumnIdentifiers() {
        return hasUserFieldTableModel() ? ((UserFieldTableModel) dataModel).getColumnIdentifiers() : null;
    }

    /**
     * Gibt den <code>Viewport</code> des {@link #layoutContainer}s wieder, das heißt den Bereich des {@link #layoutContainer}s, der die Zellen des
     * Tables darstellt.
     * 
     * @see JScrollPane#getViewport()
     * @return
     */
    public JViewport getViewport() {
        return layoutContainer.getViewport();
    }

    /**
     * Gibt den RowHeader wieder
     * 
     * @return
     */
    public JList getRowHeaderView() {
        return (JList) layoutContainer.getRowHeader().getView();
    }

    /**
     * Reguliert die Editierbarkeit von Zellen in Abhängigkeit von dem <code>tableController</code>.
     */
    @Override
    public boolean isCellEditable(final int row, final int col) {
        if (tableController != null) {
            return tableController.isEditable(row, col);
        }
        return super.isCellEditable(row, col);
    }

    /**
     * Überschreibt die Methode von {@link JTable}, sodass der Wert von {@link UserFieldTableController#isCellSelected(int, int)} an der Stelle (
     * <code>row</code>,<code>column</code>) zurückgegeben wird.
     */
    @Override
    public boolean isCellSelected(final int row, final int column) {
        if (tableController != null) {
            return tableController.isCellSelected(row, column) || getEditingRow() == row && getEditingColumn() == column;
        }
        return super.isCellSelected(row, column);
    }

    /**
     * Gibt wieder, ob <code>p</code> der Ausgangspunkt der aktuellen Selektion ist
     * 
     * @param p
     * @see UserFieldTableController#isAnchorPoint(Point)
     */
    public boolean isAnchorPoint(final Point p) {
        if (tableController != null) {
            return tableController.isAnchorPoint(p);
        }
        return getSelectionModel().getAnchorSelectionIndex() == p.x && getColumnModel().getSelectionModel().getAnchorSelectionIndex() == p.y;
    }

    /**
     * Gibt wieder, ob <code>p</code> der Punkt ist, bis zu dem die aktuelle Selektion reicht.
     * 
     * @param p
     * @see UserFieldTableController#isLeadingPoint(Point)
     */
    public boolean isLeadingPoint(final Point p) {
        if (tableController != null) {
            return tableController.isLeadingPoint(p);
        }
        return getSelectionModel().getLeadSelectionIndex() == p.x && getColumnModel().getSelectionModel().getLeadSelectionIndex() == p.y;
    }

    /**
     * Gibt wieder, ob die formatierte Darstellung der Werte in den Zellen aktiviert ist
     * 
     * @return {@link UserFieldTable#doFormatting}
     */
    public boolean isFormattingActive() {
        return doFormatting;
    }

    /**
     * Gibt zurück, ob das {@link #dataModel} eine Instanz der Klasse {@link UserFieldTableModel} ist.
     * 
     * @return
     */
    public boolean hasUserFieldTableModel() {
        return dataModel instanceof UserFieldTableModel;
    }

    /**
     * Gibt wieder, ob sich Daten im {@link #dataModel} befinden
     * 
     * @return
     */
    public boolean hasData() {
        if (hasUserFieldTableModel()) {
            return ((UserFieldTableModel) dataModel).hasData();
        }
        return dataModel.getRowCount() > 0 && dataModel.getColumnCount() > 0;
    }

    /* ************************ Ende: get/set - Methoden ***************************************** */

    /* ************************ Start: add/remove Methoden ************************************** */

    /**
     * Fügt den <code>ListSelectionListener l</code> an alle Spalten an.
     * 
     * @param l
     */
    public void addSelectionListener(final ListSelectionListener l) {
        // Horizontale Veränderungen
        getSelectionModel().addListSelectionListener(l);
        // Vertikale Veränderungen
        getColumnModel().getSelectionModel().addListSelectionListener(l);
    }

    /**
     * Fügt den {@link TableModelListener} <code>l</code> an {@link #dataModel} an.
     * 
     * @see TableModel#addTableModelListener(TableModelListener)
     * @param l
     */
    public void addTableModelListener(final TableModelListener l) {
        getModel().addTableModelListener(l);
    }

    /**
     * Löscht den <code>ListSelectionListener l</code>
     * 
     * @param l
     */
    public void removeSelectionListener(final ListSelectionListener l) {
        getSelectionModel().removeListSelectionListener(l);
        getColumnModel().getSelectionModel().removeListSelectionListener(l);
    }

    /**
     * Löscht den {@link TableModelListener} <code>l</code>
     * 
     * @see TableModel#removeTableModelListener(TableModelListener)
     * @param l
     */
    public void removeTableModelListener(final TableModelListener l) {
        getModel().removeTableModelListener(l);
    }

    /*
     * (non-Javadoc)
     * @see tool3lgm.util.clipboard.ContentExchanger#setContentExchangeListener()
     */
    @Override
    public void addContentExchangeListener(final ContentExchangeListener l) {
        removeContentExchangeListener(l);
        addKeyListener(l);
    }

    /*
     * (non-Javadoc)
     * @see tool3lgm.util.clipboard.ContentExchanger#removeContentExchangeListener()
     */
    @Override
    public void removeContentExchangeListener(final ContentExchangeListener l) {
        removeKeyListener(l);
    }

    /**
     * Löscht den gesamten Table inklusive Row- und ColumnHeader aus dem {@link #layoutContainer}
     */
    public void removeFromLayoutContainer() {
        clearSelection();
        layoutContainer.setViewportView(null);
        layoutContainer.setRowHeaderView(null);
        layoutContainer.repaint();
    }

    /* ************************ Ende: add/remove Methoden ************************************** */

    /* ************************ Start: Kontroll-Methoden *************************************** */

    /**
     * Typen von internen Werten. Dazu gehören: <li>Editor-Werte <li>Renderer-Werte <li>Selektionszustand
     * 
     * @see UserFieldTable#showInternalValueTable(UserFieldTable, Frame, INTERNAL_VALUE_TYPE)
     */
    private static enum INTERNAL_VALUE_TYPE {
        ModelValues, EditorValues, RendererValues, SelectionStateValues
    }

    /**
     * Zeigt einen Table mit den Model-Werten der Zellen von <code>originalTable</code> an.
     * 
     * @param originTable Table, dessen Model-Werte angezeigt werden sollen
     * @param owner Frame, in dem der Table angezeigt werden soll
     */
    public static void showModelValueTable(final UserFieldTable originTable, final Frame owner) {
        showInternalValueTable(originTable, owner, INTERNAL_VALUE_TYPE.ModelValues);
    }

    /**
     * Zeigt einen Table mit den Editor-Werten der Zellen von <code>originalTable</code> an.
     * 
     * @param originTable Table, dessen Editor-Werte angezeigt werden sollen
     * @param owner Frame, in dem der Table angezeigt werden soll
     */
    public static void showEditorValueTable(final UserFieldTable originTable, final Frame owner) {
        showInternalValueTable(originTable, owner, INTERNAL_VALUE_TYPE.EditorValues);
    }

    /**
     * Zeigt einen Table mit den Renderer-Werten der Zellen von <code>originalTable</code> an.
     * 
     * @param originTable Table, dessen Renderer-Werte angezeigt werden sollen
     * @param owner Frame, in dem der Table angezeigt werden soll
     */
    public static void showRendererValueTable(final UserFieldTable originTable, final Frame owner) {
        showInternalValueTable(originTable, owner, INTERNAL_VALUE_TYPE.RendererValues);
    }

    /**
     * Zeigt einen Table mit dem Selektionszustand der Zellen von <code>originalTable</code> an.
     * 
     * @param originTable Table, dessen Selektionszustand für die Zellen angezeigt werden sollen
     * @param owner Frame, in dem der Table angezeigt werden soll
     */
    public static void showSelectionStateTable(final UserFieldTable originTable, final Frame owner) {
        showInternalValueTable(originTable, owner, INTERNAL_VALUE_TYPE.SelectionStateValues);
    }

    /**
     * Generiert einen {@link JOptionPane} mit dem durch <code>type</code> spezifizierten Table
     * 
     * @param originTable
     * @param owner
     * @param type
     */
    private static void showInternalValueTable(final UserFieldTable originTable, final Frame owner, final INTERNAL_VALUE_TYPE type) {

        int m = originTable.getRowCount();
        int n = originTable.getColumnCount();

        Object[][] values = new Object[m][n];
        Object[] colNames = new Object[n];
        String title;

        switch (type) {

        case EditorValues:
            title = INTERNAL_VALUE_TYPE.EditorValues.name();
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    values[i][j] = originTable.getTableCells()[i][j].getCellEditorValue();
                }
            }
            break;

        case RendererValues:
            title = INTERNAL_VALUE_TYPE.RendererValues.name();
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    values[i][j] = originTable.getTableCells()[i][j].getCellRendererValue();
                }
            }
            break;

        case SelectionStateValues:
            title = INTERNAL_VALUE_TYPE.SelectionStateValues.name();
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    values[i][j] = originTable.isCellSelected(i, j);
                }
            }
            break;

        case ModelValues:
            title = INTERNAL_VALUE_TYPE.ModelValues.name();
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    values[i][j] = originTable.getValueAt(i, j);
                }
            }
            break;

        default:
            return;
        }

        for (int j = 0; j < n; j++) {
            colNames[j] = String.valueOf(j);
        }

        TableModel model = new DefaultTableModel(values, colNames) {
            @Override
            public boolean isCellEditable(final int row, final int col) {
                return false;
            }
        };
        JTable table = new JTable(model);

        JOptionPane.showMessageDialog(owner, new JScrollPane(table), title, JOptionPane.INFORMATION_MESSAGE);
    }

    /* ************************ Start: Kontroll-Methoden *************************************** */

}
