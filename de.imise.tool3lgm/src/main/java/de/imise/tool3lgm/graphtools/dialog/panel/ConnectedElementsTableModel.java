package de.imise.tool3lgm.graphtools.dialog.panel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import com.google.common.base.Strings;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.dialog.panel.ConnectedElementsTableColumnsDefinition.ColumnType;
import de.imise.tool3lgm.graphtools.dialog.panel.ConnectedElementsTableColumnsDefinition.SingleColumnDefinition;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.OptionalEdge;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.path.MetaPathFunctions;
import de.imise.tool3lgm.graphtools.path.meta.AbstractMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.ElementaryMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.UnionMetaPath;
import de.imise.tool3lgm.graphtools.path.pathmodel.PathResultTreeModel;
import de.imise.tool3lgm.graphtools.path.pathmodel.PathResultTreeNode;
import de.imise.util.NamedObjectContainer;
import de.imise.util.StringUtils;

/**
 * Model eines {@link ConnectedElementsTable}
 *
 * @author AXS (11 Mar 2019)
 */
public class ConnectedElementsTableModel extends DefaultTableModel {

    /**
     * ModelElement, für das die verbundenen Elemente über den MetaPafd dargestellt werden sollen
     */
    private final ModelElement modelElement;

    /**
     * MetaPfad, der in der Tabelle dargestellt werden soll
     */
    private final UnionMetaPath metaPath;

    /**
     * Definition der Spalten der Tabelle. Das bezieht sich im Header auf den im Konstruktor übergebenen {@link SimpleMetaPath} und in den
     * Zellen auf die Positionen im {@link PathResultTreeModel}.
     */
    private final ConnectedElementsTableColumnsDefinition columnsDefinition;

    /**
     * Der Ergebnisbaum der Pfadsuche der aktuellen Pfade.
     */
    private PathResultTreeModel currentPathResultTreeModel;

    /**
     * Der Identifier für die versteckte Spalte mit dem PathResultTreeNode. Muss nur eindeutig sein und wird nirgends angezeigt, daher einfach ein
     * statisches Object.
     */
    public static String HIDDEN_RESULT_NODE_COLUMN_IDENTIFIER = new Object().toString();

    /**
     * @param modelElement
     *            ModelElement, für das die verbundenen Elemente über den MetaPafd dargestellt werden sollen
     * @param metaPath
     *            MetaPfad, der in der Tabelle dargestellt werden soll
     * @param columnsDefinition
     *            Definition der Spalten der Tabelle. Das bezieht sich im Header auf den im Konstruktor übergebenen {@link SimpleMetaPath} und in den
     *            Zellen auf die Positionen im {@link PathResultTreeModel}.
     */
    public ConnectedElementsTableModel(final ModelElement modelElement, final UnionMetaPath metaPath, final ConnectedElementsTableColumnsDefinition columnsDefinition) {
        this.modelElement = modelElement;
        this.metaPath = metaPath;
        this.columnsDefinition = columnsDefinition;
        setColumnIdentifiers(metaPath);
    }

    private void setColumnIdentifiers(final UnionMetaPath columnHeaderReferencePath) {
        //letzte Spalte ist hidden und enthält den PathResultTreeNode, aus dem die Zeile entstanden ist. Den braucht man, um zu wissen, wo der Pfad herkam und ihn löschen zu können
        Vector<Object> colNames = new Vector<>(columnsDefinition.columnCount() + 1);
        for (SingleColumnDefinition columnDefinition : columnsDefinition) {
            String colName = columnDefinition.getHeaderResKeyOrName();
            if (!Strings.isNullOrEmpty(colName)) {
                colName = Tool3lgmConstants.getResStringWithoutError(colName); //wenn irgendwas als Spaltennanem von außen vorgegeben ist -> das setzten
            } else {
                ColumnType columnType = columnDefinition.getColumnType();
                if (columnType == ColumnType.END_ELEMENT) {
                    Class<? extends ModelElement> pathEndClass = metaPath.getEndClass();
                    colName = ElementsNameBuilder.getDisplayablePluralName(pathEndClass);
                    //} else if (columnType == ColumnType.PATH_NAME) { // das hier geht gar nicht, weil man bei den Leafs nicht mehr weiß, von welchem Pfad sie gekommen sind
                    //    colName = ;
                } else {
                    if (columnType == ColumnType.OPTIONAL) {
                        colName = OptionalEdge.getOptionalityName();
                    } else {
                        Collection<String> colNameParts = new ArrayList<>();
                        List<AbstractMetaPath> metaPaths = columnHeaderReferencePath.getMetaPaths();
                        for (AbstractMetaPath metaPath : metaPaths) {
                            List<ElementaryMetaPath> elementaryMetaPaths = metaPath.getElementaryMetaPaths();
                            int pathStepIndex = columnDefinition.getPathStepIndex();
                            ElementaryMetaPath elementaryMetaPath = elementaryMetaPaths.get(pathStepIndex);
                            if (columnType == ColumnType.PATH_STEP_NAME) {
                                colName = elementaryMetaPath.getName();
                            } else if (columnType == ColumnType.PATH_STEP_FULL_NAME) {
                                colName = elementaryMetaPath.getFullName();
                            } else if (columnType == ColumnType.PATH_STEP_BACKWARD_NAME) {
                                colName = elementaryMetaPath.getOtherDirection().getName();
                            } else if (columnType == ColumnType.PATH_STEP_FULL_BACKAWARD_NAME) {
                                colName = elementaryMetaPath.getOtherDirection().getFullName();
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
                            if (!colNameParts.contains(colName)) {
                                colNameParts.add(colName);
                            }
                        }
                        colName = StringUtils.createCollectionString(colNameParts, " / ");
                    }
                }
            }
            colNames.add(colName);
        }
        colNames.addElement(HIDDEN_RESULT_NODE_COLUMN_IDENTIFIER);
        setColumnIdentifiers(colNames);
    }

    private void setData() {
        List<PathResultTreeNode> completePathLeafs = currentPathResultTreeModel.getCompletePathLeafs();
        setRowCount(completePathLeafs.size());

        int row = 0;
        for (PathResultTreeNode resultNode : completePathLeafs) {
            int col = 0;
            for (SingleColumnDefinition singleColumnDefinition : columnsDefinition) {
                ColumnType columnType = singleColumnDefinition.getColumnType();
                Object value = null;
                if (columnType == ColumnType.END_ELEMENT) {
                    value = resultNode.getEndElement();
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
                    if (columnType == ColumnType.OPTIONAL) {
                        Edge edge = currentPathNode.getEdge();
                        if (edge instanceof OptionalEdge) {
                            value = new NamedObjectContainer<>(edge, ((OptionalEdge) edge).getOptionDisplayName(), true); //true, damit der Editor der Tabllenzelle den richtigen String auswählt, wenn er gestartet wird
                        }
                    } else if (columnType == ColumnType.PATH_STEP_NAME || columnType == ColumnType.PATH_STEP_BACKWARD_NAME) {
                        Edge edge = currentPathNode.getEdge();
                        AbstractMetaPath metaPath = currentPathNode.getMetaPath();
                        String name = columnType == ColumnType.PATH_STEP_NAME ? metaPath.getName() : metaPath.getOtherDirection().getName();
                        value = new NamedObjectContainer<>(edge, name);
                    }
                }
                setValueAt(value, row, col++);
            }
            //letzte Spalte ist hidden und enthält den resultNode, damit man die Quelle der Zeile kennt (braucht man zum Löschen)
            setValueAt(resultNode, row++, col);
        }
    }

    void setOptionalValue(final Object oldValue, final Object newValue, final int pid) {
        if (!oldValue.equals(newValue)) {
            if (oldValue instanceof NamedObjectContainer) {
                NamedObjectContainer<?> oldValueContainer = (NamedObjectContainer<?>) oldValue;
                Object oldObject = oldValueContainer.getObject();
                if (oldObject instanceof Edge) {
                    String newValueString = String.valueOf(newValue);
                    boolean isOptinal = OptionalEdge.getOptionOptionalDisplayName().equals(newValueString);
                    Edge edge = (Edge) oldObject;
                    GraphDocument doc = edge.getCollection().getMainGraphDocument();
                    doc.setOptional(edge, isOptinal, pid);
                }
            }
        }
    }

    public void update() {
        currentPathResultTreeModel = MetaPathFunctions.getResultTree(modelElement, metaPath);
        setData();
    }

    public int getHiddenPathResultTreeNodeColumn() {
        return findColumn(HIDDEN_RESULT_NODE_COLUMN_IDENTIFIER.toString());
    }

    public PathResultTreeNode getPathResultTreeNode(final int rowIndex) {
        return (PathResultTreeNode) getValueAt(rowIndex, getHiddenPathResultTreeNodeColumn());
    }

}
