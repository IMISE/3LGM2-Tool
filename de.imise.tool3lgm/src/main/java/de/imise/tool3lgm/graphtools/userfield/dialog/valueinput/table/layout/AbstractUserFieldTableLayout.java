/*
 * Created on 14.02.2008
 */
package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.layout;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.ScrollPane;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.UIManager;
import javax.swing.table.JTableHeader;

import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.UserFieldTable;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.cell.IUserFieldTableCell;

/**
 * Klasse repräsentiert ein konkretes Layout für einen
 * <code>UserFieldTable</code>.
 * <p>
 * Es werden Methoden bereitgestellt, die einen <code>UserFieldTable</code> in
 * einen geeigneten Container einbetten und für diesen das gewählte Layout
 * setzen.
 * <p>
 * Über statische Methoden können vorgefertigte
 * <code>UserFieldTableLayout</code>s abgerufen werden, die auf Tabels für
 * Kennzahlen, Verteilungsgewicht oder Modelvariablen zugeschnitten sind.
 * <p>
 * Es wird ein RowHeader(optional) und ColumnHeader mit Tooltips gesetzt. Der
 * RowHeader und alle Spalten lassen sich in ihrer Größe ändern.
 * <p>
 * Werte im Table werden in formatierter Form dargestellt.
 * <p>
 * Komponenten, die einen solchen <code>UserFieldTable</code> darstellen,
 * sollten nicht den Table selbst, sondern den Container verwenden.<br>
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
public abstract class AbstractUserFieldTableLayout {

    /*
     * ****************************** Beginn: Konstanten
     * *********************************
     */

    /** Maximale initiale Breite des rowHeaders */
    public static final int MAX_INITIAL_ROW_HEADER_WIDTH = 200;

    /** Minimale Breite des rowHeaders */
    public static final int MIN_ROW_HEADER_WIDTH = 50;

    /*
     * ****************************** Ende: Konstanten
     * ***********************************
     */

    /*
     * ************************************ Start: Deklaration
     * ************************************
     */

    /**
     * Gibt wieder, ob der Table Zeilenköpfe enthält
     */
    protected boolean hasRowHeader = true;

    /*
     * ************************************ Ende: Deklaration
     * ************************************
     */

    /*
     * ************************************ Start: Initialisierung
     * ************************************
     */

    /**
     * Erzeugt ein neues Layout. Falls
     * <code>changeDeactivatedCellColor = true</code>, werden nicht editierbare
     * Zellen grau gefärbt.
     *
     * @param changeDeactivatedCellColor
     */
    protected AbstractUserFieldTableLayout() {
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
     * Setzt für jede Zelle des Tables ein <code>IUserFieldTableCell</code> als
     * Editor und Renderer und legt die Zeilenhöhe fest.
     *
     * @param table
     */
    private void setTableCells(final UserFieldTable table) {
        updateTableCells(table);
        table.setRowHeight(IUserFieldTableCell.PREFERRED_CELL_HEIGHT);
    }

    /**
     * <code>RowHeaderRenderer</code> wird als Renderer für die Zeilenköpfe
     * gesetzt
     *
     * @param table
     */
    private void setRowHeader(final UserFieldTable table, final JScrollPane layoutContainer) {

        JList rowHeader = new JList(table.getRowIdentifiers());

        // Minimale und initiale Größe des rowHeaders festlegen
        rowHeader.setFixedCellHeight(table.getRowHeight());
        rowHeader.setFixedCellWidth(calculateRowHeaderWidth(rowHeader));
        Dimension minDim = new Dimension(MIN_ROW_HEADER_WIDTH, IUserFieldTableCell.MIN_CELL_HEIGHT);
        rowHeader.setMinimumSize(minDim);
        rowHeader.setCellRenderer(new RowHeaderRenderer(table));
        rowHeader.setBackground(table.getTableHeader().getBackground());
        rowHeader.setFocusable(false);

        layoutContainer.setRowHeaderView(rowHeader);
    }

    /**
     * Berechnet die durchschnittliche Breite der Zellen des rowHeaders. Liegt
     * der errechnete Wert oberhalb der maximalen Breite, wird
     * {@link #MAX_INITIAL_ROW_HEADER_WIDTH} genommen, bzw.
     * {@link #MIN_ROW_HEADER_WIDTH}, falls die minimale Breite unterschritten
     * wird.
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

    /*
     * ************************************ Ende: Initialisierung
     * ************************************
     */

    /*
     * ************************************ Start: funktionale Methoden
     * ************************************
     */

    /**
     * Aktualisiert die Zellen, sowie Row- und ColumnHeader des Tables.
     * <p>
     * Diese Methode ist bei der Initialisierung des <code>table</code>s und
     * nach jedem Neusetzen des dazugehörigen TableModels auszuführen.
     * <p>
     * Im Falle, dass der <code>table</code> noch nicht in seinen
     * <code>layoutContainer</code> eingebettet wurde, wird dies hier noch
     * erledigt.
     * <p>
     * Im Falle, dass der <code>table</code> noch keinen
     * <code>layoutContainer</code> besitzt, wird eine
     * {@link IllegalArgumentException} geworfen.
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
     * Zu Darstellung des Tables, muss dieses ScrollPane verwendet werden,
     * nachdem der Table durch {@link #embed(UserFieldTable, JScrollPane)}
     * eingebettet wurde. Andernfalls erfolgt keine Darstellung des RowHeaders.
     *
     * @return
     */
    public JScrollPane createLayoutContainer() {
        return new JScrollPane();
    }

    /**
     * Bettet den <code>table</code> in <code>layoutContainer</code> ein.<br>
     * Um die grafische Darstellung zu aktualisieren, ist möglicherweise noch
     * ein Aufruf von {@link #update(UserFieldTable)} für den <code>table</code>
     * notwendig.
     * <p>
     * Falls das Einbetten gelingt, wird <code>true</code> zurückgegeben, sonst
     * <code>false</code>.
     *
     * @param table Table, der in den <code>layoutContainer</code> einzubetten
     *            ist
     * @param layoutContainer Scrollpane, in das der <code>table</code>
     *            eingebettet werden soll
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
        IUserFieldTableCell[][] tableCells = getTableCells(table);
        table.setTableCells(tableCells);
    }

    public abstract IUserFieldTableCell[][] getTableCells(final UserFieldTable table);

    /**
     * Deaktiviert die formatierte Darstellung der Werte, das Resizing des
     * RowHeaders, die Tooltips des RowHeaders und die Farbänderung nicht
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

    /*
     * ************************************ Ende: funktionale Methoden
     * ************************************
     */

    /*
     * ************************************ Start: Unterklassen
     * ************************************
     */

    /**
     * Der Renderer für die JList, die als RowHeader angezeigt wird. Sie wird im
     * Großen und Ganzen so dargestellt, wie der ColumnHeader. Außerdem wird der
     * Titel jeder Zeile als ToolTipText angezeigt.
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
     * Listener, der das Resizing des rowHeaders ermöglicht und überwacht
     *
     * @author fstephan
     */
    private class RowHeaderResizeListener extends MouseAdapter {

        /**
         * MousePointer - Verschiebung <br>
         * Sorgt dafür, dass beim Eintreten eines Resize-Ereignisses der
         * rowHeader das source-Objekt der MouseEvents ist.
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
         * ändert sich, wenn er sich auf dem Rand zwischen RowHeader und Table
         * befindet
         */
        private Cursor cursor;

        /**
         * Gibt wieder, ob sich der {@link #cursor} auf dem Rand zwischen Table
         * und RowHeader befindet.
         */
        private boolean canResize = false;

        /**
         * Konstruktor
         *
         * @param sp {@link ScrollPane}, das den {@link #header} als
         *            <code>RowHeaderViewportView</code> beinhaltet
         */
        public RowHeaderResizeListener(final JScrollPane sp) {
            pane = sp;
            header = (JList) sp.getRowHeader().getView();
        }

        /**
         * Verändert den Cursor-Typ in Abhängigkeit seiner Position. Ist er über
         * dem Rand zwischen rowHeader und table, wechselt er in die
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
         * Führt den resize aus. Unterbindet das Verkleinern, falls die minimale
         * Breite unterschritten wird. Unterbindet das Vergrößern, falls die
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
             * Verhindert, dass der rowHeader seine minimale Größe
             * unterschreiten bzw. die Fenstergröße überschreiten kann
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

    /*
     * ************************************ Ende: Unterklassen
     * ************************************
     */

}
