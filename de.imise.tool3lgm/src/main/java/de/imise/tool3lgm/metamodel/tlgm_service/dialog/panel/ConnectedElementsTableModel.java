package de.imise.tool3lgm.metamodel.tlgm_service.dialog.panel;

import java.util.List;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Optional;
import de.imise.tool3lgm.graphtools.path.meta.AbstractMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.ElementaryMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.path.pathmodel.PathResultTreeModel;
import de.imise.tool3lgm.graphtools.path.pathmodel.PathResultTreeNode;
import de.imise.tool3lgm.metamodel.tlgm_service.dialog.panel.ConnectedElementsTableColumnsDefinition.ColumnType;
import de.imise.tool3lgm.metamodel.tlgm_service.dialog.panel.ConnectedElementsTableColumnsDefinition.SingleColumnDefinition;

/**
 * @author AXS (11 Mar 2019)
 */
public class ConnectedElementsTableModel extends DefaultTableModel {

    private final SimpleMetaPath simpleMetaPath;

    private final ConnectedElementsTableColumnsDefinition columnsDefinition;

    /**
     *
     */
    public ConnectedElementsTableModel(final SimpleMetaPath simpleMetaPath, final ConnectedElementsTableColumnsDefinition columnsDefinition) {
        this.simpleMetaPath = simpleMetaPath;
        this.columnsDefinition = columnsDefinition;
        setColumnIdentifiers();
    }

    private void setColumnIdentifiers() {
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
                    //} else if (columnType == ColumnType.PATH_NAME) { // das hier geht gar nicht, weil man bei den Leafs nicht mehr weiß, von welchem Pfad sie gekommen sind
                    //    colName = ;
                } else {
                    List<ElementaryMetaPath> elementaryMetaPaths = simpleMetaPath.getElementaryMetaPaths();
                    int pathStepIndex = columnDefinition.getPathStepIndex();
                    ElementaryMetaPath elementaryMetaPath = elementaryMetaPaths.get(pathStepIndex);
                    if (columnType == ColumnType.OPTIONAL) {
                        colName = Optional.getOptionalityName();
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
        List<PathResultTreeNode> completePathLeafs = pathResultModel.getCompletePathLeafs();
        setRowCount(completePathLeafs.size());

        int row = 0;
        for (PathResultTreeNode resultNode : completePathLeafs) {
            int col = 0;
            for (SingleColumnDefinition singleColumnDefinition : columnsDefinition) {
                ColumnType columnType = singleColumnDefinition.getColumnType();
                if (columnType == ColumnType.END_ELEMENT) {
                    setValueAt(resultNode.getEndElement(), row, col);
                } else {
                    PathResultTreeNode currentPathNode = resultNode;
                    int level = currentPathNode.getLevel();
                    int optionalEdgeInPathIndex = singleColumnDefinition.getPathStepIndex();
                    while (level > 0) {
                        if (level - optionalEdgeInPathIndex == PathResultTreeModel.FIRST_PATH_STEP_NODE_LEVEL) {
                            break;
                        }
                        currentPathNode = (PathResultTreeNode) currentPathNode.getParent();
                        level = currentPathNode.getLevel();
                    }
                    String value = null;
                    if (columnType == ColumnType.OPTIONAL) {
                        Edge edge = currentPathNode.getEdge();
                        value = "  " + Optional.getOptionDisplayName(edge);
                    } else if (columnType == ColumnType.PATH_STEP_NAME) {
                        AbstractMetaPath metaPath = currentPathNode.getMetaPath();
                        value = metaPath.getName();
                    }
                    setValueAt(value, row, col);
                }
                col++;
            }
            row++;
        }
    }

}
