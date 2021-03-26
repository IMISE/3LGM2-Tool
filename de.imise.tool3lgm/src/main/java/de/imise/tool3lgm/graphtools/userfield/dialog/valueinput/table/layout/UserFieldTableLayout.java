/*
 * Created on 14.02.2008
 */
package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.layout;

import de.imise.tool3lgm.graphtools.userfield.definition.UserField;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.UserFieldTable;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.cell.IUserFieldTableCell;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.cell.UserFieldActivatedTableCell;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.cell.UserFieldDeactivatedTableCell;
import de.imise.util.NamedObjectContainer;

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
 * Es wird ein RowHeader(optional) und ColumnHeader mit ToolTips gesetzt. Der
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
public final class UserFieldTableLayout extends AbstractUserFieldTableLayout {

    /**
     * Gibt wieder, ob nicht editierbare Zellen grau dargstellt werden sollen
     */
    private final boolean changeDeactivatedCellColor;

    /**
     * Erzeugt ein neues Layout. Nicht editierbare Zellen werden nicht grau
     * gefärbt, d.h. sie bleiben weiß
     */
    public UserFieldTableLayout() {
        this(false);
    }

    /**
     * Erzeugt ein neues Layout. Falls
     * <code>changeDeactivatedCellColor = true</code>, werden nicht editierbare
     * Zellen grau gefärbt.
     *
     * @param changeDeactivatedCellColor
     */
    public UserFieldTableLayout(final boolean changeDeactivatedCellColor) {
        this.changeDeactivatedCellColor = changeDeactivatedCellColor;
    }

    /**
     * Aktualisiert alle Zellen des <code>table</code>s
     *
     * @param table
     */
    @Override
    public IUserFieldTableCell[][] getTableCells(final UserFieldTable table) {
        IUserFieldTableCell[][] tableCells = null;
        // Falls das Model keine Daten enthält, werden headers nicht gesetzt
        if (table.hasUserFieldTableModel() && table.hasData()) {
            if (table.isFormattingActive() == true) {
                tableCells = new IUserFieldTableCell[table.getRowCount()][table.getColumnCount()];
                for (int i = 0; i < tableCells.length; i++) {
                    for (int j = 0; j < tableCells[0].length; j++) {
                        NamedObjectContainer<UserField> container = (NamedObjectContainer<UserField>) table.getValueAt(i, j);
                        if (changeDeactivatedCellColor && container == null) { // nicht editierbare Zellen grau
                            tableCells[i][j] = new UserFieldDeactivatedTableCell();
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
        return tableCells;
    }
}
