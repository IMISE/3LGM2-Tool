package de.imise.tool3lgm.graphtools.dialog.panel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import com.google.common.base.Strings;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.dialog.panel.ConnectedElementsTableDefinition.ColumnType;
import de.imise.tool3lgm.graphtools.dialog.panel.ConnectedElementsTableDefinition.SingleColumnDefinition;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.OptionalEdge;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.path.metapaths.AbstractMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.ElementaryMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.ElementaryMetaPathHandler;
import de.imise.tool3lgm.graphtools.path.metapaths.PathFunctions;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPathCreator;
import de.imise.tool3lgm.graphtools.path.metapaths.UnionMetaPath;
import de.imise.tool3lgm.graphtools.path.paths.PathResultTreeModel;
import de.imise.tool3lgm.graphtools.path.paths.PathResultTreeNode;
import de.imise.util.NamedObjectContainer;
import de.imise.util.StringUtils;

/**
 * Model eines {@link ConnectedElementsTable}
 *
 * @author AXS (11 Mar 2019)
 */
public class ConnectedElementsTableModel extends DefaultTableModel {

    /**
     * ModelElement, für das die verbundenen Elemente über den MetaPfad dargestellt werden sollen
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
    private final ConnectedElementsTableDefinition tableDefinition;

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
     *            ModelElement, für das die verbundenen Elemente über den MetaPfad dargestellt werden sollen
     * @param simpleMetaPath
     *            MetaPfad, der in der Tabelle dargestellt werden soll
     * @param tableDefinition
     *            Definition der Spalten der Tabelle. Das bezieht sich im Header auf den im Konstruktor übergebenen {@link SimpleMetaPath} und in den
     *            Zellen auf die Positionen im {@link PathResultTreeModel}.
     */
    public ConnectedElementsTableModel(final ModelElement modelElement, final SimpleMetaPath simpleMetaPath, final ConnectedElementsTableDefinition tableDefinition) {
        this.modelElement = modelElement;
        Set<SimpleMetaPath> allDifferentSimpleMetaPaths = new HashSet<>();
        Collection<SimpleMetaPath> simpleMetaPathsNonAbstract = SimpleMetaPathCreator.getSimpleMetaPathsNonAbstract(simpleMetaPath);
        allDifferentSimpleMetaPaths.addAll(simpleMetaPathsNonAbstract);
        metaPath = new UnionMetaPath(allDifferentSimpleMetaPaths);
        this.tableDefinition = tableDefinition;
        setColumnIdentifiers(metaPath);
    }

    /**
     * @param columnHeaderReferencePath
     */
    private void setColumnIdentifiers(final UnionMetaPath columnHeaderReferencePath) {
        //letzte Spalte ist hidden und enthält den PathResultTreeNode, aus dem die Zeile entstanden ist. Den braucht man, um zu wissen, wo der Pfad herkam und ihn löschen zu können
        Vector<Object> colNames = new Vector<>(tableDefinition.columnCount() + 1);
        for (SingleColumnDefinition columnDefinition : tableDefinition) {
            String colName = columnDefinition.getHeaderResKeyOrName();
            if (!Strings.isNullOrEmpty(colName)) {
                colName = columnHeaderReferencePath.getResStringWithoutError(colName); //wenn irgendwas als Spaltennanem von außen vorgegeben ist -> das setzten
            } else {
                ColumnType columnType = columnDefinition.getColumnType();
                if (columnType == ColumnType.END_ELEMENT) {
                    Class<? extends ModelElement> pathEndClass = metaPath.getEndClass();
                    MetaModel metaModel = metaPath.getMetaModel();
                    ElementsNameBuilder elementsNameBuilder = metaModel.getElementsNameBuilder();
                    colName = elementsNameBuilder.getDisplayablePluralName(pathEndClass);
                    //} else if (columnType == ColumnType.PATH_NAME) { // das hier geht gar nicht, weil man bei den Leafs nicht mehr weiß, von welchem Pfad sie gekommen sind
                    //    colName = ;
                } else {
                    if (columnType == ColumnType.OPTIONAL) {
                        colName = OptionalEdge.getOptionalityName();
                    } else {
                        Collection<String> colNameParts = new ArrayList<>();
                        List<AbstractMetaPath> metaPaths = columnHeaderReferencePath.getSubMetaPaths();
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
                                MetaModel metaModel = metaPath.getMetaModel();
                                ElementsNameBuilder elementsNameBuilder = metaModel.getElementsNameBuilder();
                                colName = elementsNameBuilder.getDisplayablePluralName(elementClass);
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
        List<PathResultTreeNode> incompletePathLeafs = currentPathResultTreeModel.getIncompletePathLeafs();
        setRowCount(completePathLeafs.size() + incompletePathLeafs.size());
        int row = 0;
        for (int i = 0; i < 2; i++) {
            List<PathResultTreeNode> currentPathLeafs = i == 0 ? completePathLeafs : incompletePathLeafs;
            for (PathResultTreeNode resultNode : currentPathLeafs) {
                int col = 0;
                PathResultTreeNode[] pathToRoot = resultNode.getPathToRoot();
                for (SingleColumnDefinition singleColumnDefinition : tableDefinition) {
                    ColumnType columnType = singleColumnDefinition.getColumnType();
                    Object value = null;
                    int pathStepIndex = singleColumnDefinition.getPathStepIndex() + 1; // + 1 weil im pathToRoot der Rootknoten mit enthalten ist, der in der ColumnDefnition nicht mitgezählt wird
                    if (pathToRoot.length > pathStepIndex) {
                        PathResultTreeNode currentPathNode = pathToRoot[pathStepIndex];
                        if (columnType == ColumnType.END_ELEMENT && i == 0) { // nur wenn wir in der Liste mit den vollständigen Pfaden sind, ist das das richtige Endelement
                            value = resultNode.getEndElement();
                        } else {
                            if (columnType == ColumnType.OPTIONAL) {
                                Edge edge = currentPathNode.getEdge();
                                if (edge instanceof OptionalEdge) {
                                    value = new NamedObjectContainer<>(edge, ((OptionalEdge) edge).getOptionDisplayName(), true); //true, damit der Editor der Tabllenzelle den richtigen String auswählt, wenn er gestartet wird
                                }
                            } else if (columnType == ColumnType.PATH_STEP_START) {
                                value = currentPathNode.getStartElement();
                            } else if (columnType == ColumnType.PATH_STEP_END) {
                                value = currentPathNode.getEndElement();
                            } else if (columnType == ColumnType.PATH_STEP_EDGE) {
                                value = currentPathNode.getEdge();
                            } else if (columnType == ColumnType.PATH_STEP_NAME || columnType == ColumnType.PATH_STEP_BACKWARD_NAME) {
                                Edge edge = currentPathNode.getEdge();
                                if (edge != null) {
                                    ElementaryMetaPath metaPath = currentPathNode.getMetaPath();
                                    Class<? extends Edge> edgeClass = edge.getClass();
                                    //der Metapfad kann auf einer abstrakten Oberklasse der konkreten Kante definiert sein. Ist das der Fall, muss der Name aber von der konkreten Kantenklasse abgeleitet werden!
                                    if (edgeClass != metaPath.getEdgeClass()) {
                                        MetaModel metaModel = metaPath.getMetaModel();
                                        ElementaryMetaPathHandler elementaryMetaPathHandler = metaModel.getElementaryMetaPathHandler();
                                        metaPath = elementaryMetaPathHandler.getMetaPath(metaPath.getStartClass(), edgeClass, metaPath.getDirection(), metaPath.getEndClass());
                                    }
                                    String name = columnType == ColumnType.PATH_STEP_NAME ? metaPath.getName() : metaPath.getOtherDirection().getName();
                                    value = new NamedObjectContainer<>(edge, name);
                                }
                            }
                        }
                    }
                    setValueAt(value, row, col++);
                }
                //letzte Spalte ist hidden und enthält den resultNode, damit man die Quelle der Zeile kennt (braucht man zum Löschen)
                setValueAt(resultNode, row++, col);
            }
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
                    GraphDocument doc = edge.getCollection().getMainDoc();
                    doc.setOptional(edge, isOptinal, pid);
                }
            }
        }
    }

    public void update() {
        //get resultTree with incomplete paths
        currentPathResultTreeModel = PathFunctions.getResultTree(modelElement, metaPath, true);
        setData();
    }

    public int getHiddenPathResultTreeNodeColumn() {
        return findColumn(HIDDEN_RESULT_NODE_COLUMN_IDENTIFIER.toString());
    }

    public PathResultTreeNode getPathResultTreeNode(final int rowIndex) {
        return (PathResultTreeNode) getValueAt(rowIndex, getHiddenPathResultTreeNodeColumn());
    }

}
