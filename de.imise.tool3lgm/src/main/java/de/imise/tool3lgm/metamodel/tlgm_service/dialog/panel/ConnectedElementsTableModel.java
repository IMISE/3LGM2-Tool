package de.imise.tool3lgm.metamodel.tlgm_service.dialog.panel;

import java.util.List;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Optional;
import de.imise.tool3lgm.graphtools.path.meta.ElementaryMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.path.pathmodel.PathResultTreeModel;
import de.imise.tool3lgm.metamodel.tlgm_service.dialog.panel.ConnectedElementsTableColumnsDefinition.ColumnType;
import de.imise.tool3lgm.metamodel.tlgm_service.dialog.panel.ConnectedElementsTableColumnsDefinition.SingleColumnDefinition;

/**
 * @author AXS (11 Mar 2019)
 */
public class ConnectedElementsTableModel extends DefaultTableModel {

    private final SimpleMetaPath simpleMetaPath;

    /**
     *
     */
    public ConnectedElementsTableModel(final SimpleMetaPath simpleMetaPath, final ConnectedElementsTableColumnsDefinition columnsDefinition) {
        this.simpleMetaPath = simpleMetaPath;
        setColumnIdentifiers(columnsDefinition);
    }

    private void setColumnIdentifiers(final ConnectedElementsTableColumnsDefinition columnsDefinition) {
        Vector<String> colNames = new Vector<>(columnsDefinition.columnCount());
        for (SingleColumnDefinition columnDefinition : columnsDefinition) {
            String colName = columnDefinition.getHeaderResKeyOrName();
            if (colName != null) {
                colName = Tool3lgmConstants.getResStringWithoutError(colName); //wenn irgendwas als Spaltennanem von außen vorgegeben ist -> das setzten
            } else {
                ColumnType columnType = columnDefinition.getColumnType();
                if (columnType == ColumnType.END_ELEMENT) {
                    Class<? extends ModelElement> pathEndClass = simpleMetaPath.getEndClass();
                    colName = ElementsNameBuilder.getDisplayablePluralName(pathEndClass);
                    //} else if (columnType == ColumnType.PATHNAME) {
                    //    colName = ;
                } else {
                    List<ElementaryMetaPath> elementaryMetaPaths = simpleMetaPath.getElementaryMetaPaths();
                    int pathStepIndex = columnDefinition.getIndex();
                    ElementaryMetaPath elementaryMetaPath = elementaryMetaPaths.get(pathStepIndex);
                    if (columnType == ColumnType.OPTIONAL) {
                        Class<? extends Edge> edgeClass = elementaryMetaPath.getEdgeClass();
                        colName = Optional.getOptionName(edgeClass);
                    } else if (columnType == ColumnType.PATH_STEP_NAME) {
                        colName = elementaryMetaPath.getName();
                    } else if (columnType == ColumnType.PATH_STEP_FULL_NAME) {
                        colName = elementaryMetaPath.getFullName();
                    } else {
                        Class<? extends ModelElement> elementClass = null;
                        if (columnType == ColumnType.PATH_STEP_START) {
                            elementClass = elementaryMetaPath.getStartClass();
                        } else if (columnType == ColumnType.PATH_STEP_END) {
                            elementClass = elementaryMetaPath.getEndClass();
                        } else if (columnType == ColumnType.PATH_STEP_EDGE) {
                            elementClass = elementaryMetaPath.getEdgeClass();
                        }
                        colName = ElementsNameBuilder.getDisplayablePluralName(elementClass);
                    }
                }
            }
            colNames.add(colName);
        }
        setColumnIdentifiers(colNames);
    }

    public void setData(final PathResultTreeModel pathResultModel) {
    }

}
