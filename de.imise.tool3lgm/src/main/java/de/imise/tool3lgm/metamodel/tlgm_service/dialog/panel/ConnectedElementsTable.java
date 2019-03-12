package de.imise.tool3lgm.metamodel.tlgm_service.dialog.panel;

import javax.swing.JTable;

import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.path.pathmodel.PathResultTreeModel;

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
    }

    public void setData(final PathResultTreeModel pathResultModel) {
        model.setData(pathResultModel);
    }

}
