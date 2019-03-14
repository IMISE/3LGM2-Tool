package de.imise.tool3lgm.metamodel.tlgm_service.dialog.panel;

import javax.swing.JTable;
import javax.swing.table.TableColumn;

import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.path.pathmodel.PathResultTreeModel;
import de.imise.tool3lgm.metamodel.tlgm_service.dialog.panel.ConnectedElementsTableColumnsDefinition.SingleColumnDefinition;

/**
 * @author AXS (11 Mar 2019)
 */
public class ConnectedElementsTable extends JTable {

    private final ConnectedElementsTableModel model;

    /**
     *
     */
    public ConnectedElementsTable(final SimpleMetaPath simpleMetaPath, final ConnectedElementsTableColumnsDefinition columnsDefinition) {
        super(new ConnectedElementsTableModel(simpleMetaPath, columnsDefinition));
        model = (ConnectedElementsTableModel) getModel();
        initColumnWidth(columnsDefinition);
    }

    public void setData(final PathResultTreeModel pathResultModel) {
        model.setData(pathResultModel);
    }

    private void initColumnWidth(final ConnectedElementsTableColumnsDefinition columnsDefinition) {
        for (int i = 0; i < columnsDefinition.columnCount(); i++) {
            TableColumn column = columnModel.getColumn(i);
            SingleColumnDefinition singleColumnDefinition = columnsDefinition.get(i);
            int width = singleColumnDefinition.getWidth();
            column.setPreferredWidth(width);
        }
    }
}
