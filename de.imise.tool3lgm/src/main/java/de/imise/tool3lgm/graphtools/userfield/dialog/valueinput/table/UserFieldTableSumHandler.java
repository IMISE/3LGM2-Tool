package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table;

import java.math.BigDecimal;

import com.google.common.base.Strings;

import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldNumberFormat;
import de.imise.tool3lgm.graphtools.userfield.definition.type.UserField;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.model.AbstractTableModel;
import de.imise.util.NamedObjectContainer;

public class UserFieldTableSumHandler {

    private static boolean lastCallHadValidValues;

    /**
     * Liefert die Summe aller als BigDecimal parsbaren Elemente einer Zeile
     * formatiert mit Einheit.
     *
     * @param tableModel
     * @param row
     * @return
     */
    public static String getFormattedRowSum(final AbstractTableModel tableModel, final int row) {
        return getFormattedSum(tableModel, row, true);
    }

    /**
     * Liefert die Summe aller als BigDecimal parsbaren Elemente einer Spalte
     * formatiert mit Einheit.
     *
     * @param tableModel
     * @param col
     * @return
     */
    public static String getFormattedColumnSum(final AbstractTableModel tableModel, final int col) {
        return getFormattedSum(tableModel, col, false);
    }

    /**
     * Liefert die Summe aller als BigDecimal parsbaren Elemente einer Zeile
     * oder Spalte formatiert mit Einheit.
     *
     * @param tableModel
     * @param rowOrColumnIndex
     * @param useRow
     * @return
     */
    private static String getFormattedSum(final AbstractTableModel tableModel, final int rowOrColumnIndex, final boolean useRow) {
        String returnValue = "";
        UserField formatSource = getFormatSource(tableModel, rowOrColumnIndex, useRow);
        String formatUnit = formatSource != null ? formatSource.getFormatUnit() : null;
        BigDecimal sumValue = getSum(tableModel, rowOrColumnIndex, useRow, formatUnit);
        if (lastCallHadValidValues) {
            String sum = sumValue.toString();
            if (formatSource != null) {
                UserFieldNumberFormat numberFormat = formatSource.getNumberFormat();
                returnValue = UserField.getFormattedValue(sum, numberFormat, true);
            }
        }
        return returnValue;
    }

    /**
     * Liefert die Summe aller als BigDecimal parsbaren Elemente einer Zeile
     * oder Spalte.
     *
     * @param tableModel
     * @param rowOrColumnIndex
     * @param useRow
     * @return
     */
    private static BigDecimal getSum(final AbstractTableModel tableModel, final int rowOrColumnIndex, final boolean useRow, final String formatUnit) {
        BigDecimal sum = BigDecimal.ZERO;
        lastCallHadValidValues = false;
        if (rowOrColumnIndex >= 0) {
            int count = useRow ? tableModel.getColumnCount() : tableModel.getRowCount();
            for (int i = 0; i < count; i++) {
                BigDecimal d;
                int rowIndex = useRow ? rowOrColumnIndex : i;
                int colIndex = useRow ? i : rowOrColumnIndex;
                Object value = tableModel.getValueAt(rowIndex, colIndex);
                if (value == null) {
                    continue;
                }
                String valueString = value.toString();
                if (!Strings.isNullOrEmpty(formatUnit)) {
                    if (valueString.endsWith(formatUnit)) {
                        valueString = valueString.substring(0, valueString.length() - formatUnit.length());
                    }
                }
                try {
                    d = new BigDecimal(valueString.trim());
                } catch (Exception ex) {
                    continue;
                }
                sum = sum.add(d);
                lastCallHadValidValues = true;
            }
        }
        return sum;
    }

    /**
     * Liefert das UserField, das für die gewünschte Zeile oder Spalte das
     * Format vorgibt.
     *
     * @param tableModel
     * @param rowOrColumnIndex
     * @param useRow
     * @return
     */
    private static UserField getFormatSource(final AbstractTableModel tableModel, final int rowOrColumnIndex, final boolean useRow) {
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
     * Liefert das UserField im RowHeader, wenn es ein solches gibt, sonst
     * <code>null</code>.
     *
     * @param tableModel
     * @param row
     * @return
     */
    private static UserField getRowHeaderUserField(final AbstractTableModel tableModel, final int row) {
        NamedObjectContainer<?> rowIdentifierContainer = tableModel.getRowIdentifiers().get(row);
        Object rowIdentifierObject = rowIdentifierContainer.getObject();
        return rowIdentifierObject instanceof UserField ? (UserField) rowIdentifierObject : null;
    }

    /**
     * Liefert das UserField im ColumnHeader, wenn es ein solches gibt, sonst
     * <code>null</code>.
     *
     * @param tableModel
     * @param col
     * @return
     */
    private static UserField getColumnHeaderUserField(final AbstractTableModel tableModel, final int col) {
        NamedObjectContainer<?> columnIdentifierContainer = tableModel.getColumnIdentifiers().get(col);
        Object columnIdentifierObject = columnIdentifierContainer.getObject();
        return columnIdentifierObject instanceof UserField ? (UserField) columnIdentifierObject : null;
    }

    /**
     * Liefert das erste gesetzte UserField in irgendeiner Zelle der
     * Tabellendaten oder <code>null</code>, wenn kein Feld gesetzt ist oder das
     * erste gefundene Feld kein UserField enthält.
     *
     * @param tableModel
     * @return
     */
    private static UserField getCellUserField(final AbstractTableModel tableModel) {
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

}
