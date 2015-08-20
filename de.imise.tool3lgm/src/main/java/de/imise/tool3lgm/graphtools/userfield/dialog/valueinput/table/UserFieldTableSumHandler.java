package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table;

import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.model.UserFieldTableModel;
import de.imise.util.NamedObjectContainer;

public class UserFieldTableSumHandler {

    private static boolean lastCallHadValidValues;

    /**
     * Liefert die Summe aller als Double parsbaren Elemente einer Zeile formatiert mit Einheit.
     * 
     * @param tableModel
     * @param row
     * @return
     */
    public static String getFormattedRowSum(final UserFieldTableModel tableModel, final int row) {
        return getFormattedSum(tableModel, row, true);
    }

    /**
     * Liefert die Summe aller als Double parsbaren Elemente einer Spalte formatiert mit Einheit.
     * 
     * @param tableModel
     * @param col
     * @return
     */
    public static String getFormattedColumnSum(final UserFieldTableModel tableModel, final int col) {
        return getFormattedSum(tableModel, col, false);
    }

    /**
     * Liefert die Summe aller als Double parsbaren Elemente einer Zeile oder Spalte formatiert mit Einheit.
     * 
     * @param tableModel
     * @param rowOrColumnIndex
     * @param useRow
     * @return
     */
    private static String getFormattedSum(final UserFieldTableModel tableModel, final int rowOrColumnIndex, final boolean useRow) {
        String returnValue = "";
        double sumValue = getSum(tableModel, rowOrColumnIndex, useRow);
        if (lastCallHadValidValues) {
            String sum = Double.toString(sumValue);
            UserField formatSource = getFormatSource(tableModel, rowOrColumnIndex, useRow);
            if (formatSource != null) {
                returnValue = UserField.getFormattedValue(sum, formatSource.getFormatUserField(), true);
            }
        }
        return returnValue;
    }

    /**
     * Liefert die Summe aller als Double parsbaren Elemente einer Zeile oder Spalte.
     * 
     * @param tableModel
     * @param rowOrColumnIndex
     * @param useRow
     * @return
     */
    private static double getSum(final UserFieldTableModel tableModel, final int rowOrColumnIndex, final boolean useRow) {
        double sum = 0d;
        lastCallHadValidValues = false;
        if (rowOrColumnIndex >= 0) {
            int count = useRow ? tableModel.getColumnCount() : tableModel.getRowCount();
            for (int i = 0; i < count; i++) {
                double d;
                int rowIndex = useRow ? rowOrColumnIndex : i;
                int colIndex = useRow ? i : rowOrColumnIndex;
                Object value = tableModel.getValueAt(rowIndex, colIndex);
                if (value == null) {
                    continue;
                }
                try {
                    d = Double.parseDouble(value.toString());
                } catch (Exception ex) {
                    continue;
                }
                sum += d;
                lastCallHadValidValues = true;
            }
        }
        return sum;
    }

    /**
     * Liefert das UserField, das für die gewünschte Zeile oder Spalte das Format vorgibt.
     * 
     * @param tableModel
     * @param rowOrColumnIndex
     * @param useRow
     * @return
     */
    public static UserField getFormatSource(final UserFieldTableModel tableModel, final int rowOrColumnIndex, final boolean useRow) {
        //Zeilen bzw. Spalten Header holen und das evtl. darin verpackte UserField holen
        UserField formatSource = useRow ? getRowHeaderUserField(tableModel, rowOrColumnIndex) : getColumnHeaderUserField(tableModel, rowOrColumnIndex);
        //wenn keins gefunden wurde -> dann mal in den jeweils anderen Header schauen, ob dort auch kein UserField steckt
        if (formatSource == null) {
            UserField otherFormatSource = useRow ? getColumnHeaderUserField(tableModel, 0) : getRowHeaderUserField(tableModel, 0);
            // nur wenn in beiden Headern kein UserField steckt, dann das UserField aus der Zelle holen 
            if (otherFormatSource == null) {
                return getCellUserField(tableModel);
            }
        }
        return formatSource;
    }

    /**
     * Liefert das UserField im RowHeader, wenn es ein solches gibt, sonst <code>null</code>.
     * 
     * @param tableModel
     * @param row
     * @return
     */
    private static UserField getRowHeaderUserField(final UserFieldTableModel tableModel, final int row) {
        NamedObjectContainer<?> rowIdentifierContainer = (NamedObjectContainer<?>) tableModel.getRowIdentifiers().get(row);
        Object rowIdentifierObject = rowIdentifierContainer.getObject();
        return rowIdentifierObject instanceof UserField ? (UserField) rowIdentifierObject : null;
    }

    /**
     * Liefert das UserField im ColumnHeader, wenn es ein solches gibt, sonst <code>null</code>.
     * 
     * @param tableModel
     * @param col
     * @return
     */
    private static UserField getColumnHeaderUserField(final UserFieldTableModel tableModel, final int col) {
        NamedObjectContainer<?> columnIdentifierContainer = (NamedObjectContainer<?>) tableModel.getColumnIdentifiers().get(col);
        Object columnIdentifierObject = columnIdentifierContainer.getObject();
        return columnIdentifierObject instanceof UserField ? (UserField) columnIdentifierObject : null;
    }

    /**
     * Liefert das erste gesetzte UserField in irgendeiner Zelle der Tabellendaten oder <code>null</code>, wenn
     * kein Feld gesetzt ist oder das erste gefundene Feld kein UserField enthält.
     * 
     * @param tableModel
     * @return
     */
    private static UserField getCellUserField(final UserFieldTableModel tableModel) {
        int rowCount = tableModel.getRowCount();
        int columnCount = tableModel.getColumnCount();
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < columnCount; col++) {
                Object cellValue = tableModel.getValueAt(row, col);
                if (cellValue != null) {
                    if (!(cellValue instanceof NamedObjectContainer)) {
                        return null;
                    }
                    NamedObjectContainer<?> valueContainer = (NamedObjectContainer<?>) cellValue;
                    Object value = valueContainer.getObject();
                    return value instanceof UserField ? (UserField) value : null;
                }
            }
        }
        return null;
    }

    public static final boolean hasTabelRowSum(final UserFieldTableModel tableModel) {
        return getFormatSource(tableModel, 0, true) != null;
    }

    public static final boolean hasTabelColumnSum(final UserFieldTableModel tableModel) {
        return getFormatSource(tableModel, 0, false) != null;
    }
}
