package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.layout;

import java.util.Vector;

import de.imise.tool3lgm.graphtools.elements.Edge;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.UserFieldTable;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.cell.IUserFieldTableCell;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.cell.WeightReplaceTableCell;
import de.imise.util.NamedObjectContainer;

public class WeightReplaceTableLayout extends AbstractUserFieldTableLayout {

    private final Class<? extends Edge> edgeClass;

    public WeightReplaceTableLayout(final Class<? extends Edge> edgeClass) {
        this.edgeClass = edgeClass;
    }

    @Override
    public IUserFieldTableCell[][] getTableCells(final UserFieldTable table) {
        Vector<NamedObjectContainer<ModelElement>> rowIdentifiers = (Vector<NamedObjectContainer<ModelElement>>) table.getRowIdentifiers();
        IUserFieldTableCell[][] tableCells = null;
        // Falls das Model keine Daten enthält, werden headers nicht gesetzt
        if (table.hasUserFieldTableModel() && table.hasData()) {
            if (table.isFormattingActive() == true) {
                tableCells = new IUserFieldTableCell[table.getRowCount()][table.getColumnCount()];
                for (int row = 0; row < tableCells.length; row++) {
                    for (int col = 0; col < tableCells[0].length; col++) {
                        Object cellValue = table.getValueAt(row, col);
                        NamedObjectContainer<UserField> container = (NamedObjectContainer<UserField>) cellValue;
                        NamedObjectContainer<ModelElement> meContainer = rowIdentifiers.get(row);
                        ModelElement me = meContainer.getObject();
                        tableCells[row][col] = new WeightReplaceTableCell(container, table, me, edgeClass, col);
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
