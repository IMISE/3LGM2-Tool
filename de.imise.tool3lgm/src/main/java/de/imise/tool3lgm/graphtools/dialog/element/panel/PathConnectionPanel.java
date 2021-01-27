package de.imise.tool3lgm.graphtools.dialog.element.panel;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.dialog.element.panel.AbstractPathConnectionPanel.PanelLabelOption.LABEL_END_ELEMENT_TYPE;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARENTS;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARTS;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import de.imise.tool3lgm.graphtools.consistency.error.type.AbstractConsistencyError;
import de.imise.tool3lgm.graphtools.consistency.error.type.MissingPathError;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.action.LGMActionLibrary;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.DragNDropInitializer;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.DragNDropInitializer.DragNDropActionChain;
import de.imise.tool3lgm.graphtools.dialog.element.AbstractElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.element.DialogActionCommands;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.path.metapaths.ElementaryMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.MetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.MetaPathFunctions;
import de.imise.tool3lgm.graphtools.path.metapaths.PathFunctions;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.tree.ElementDialogPanelTree;
import de.imise.tool3lgm.graphtools.view.tree.PanelTreeRenderer;
import de.imise.tool3lgm.graphtools.view.tree.TreeRenderer;
import de.imise.tool3lgm.graphtools.view.tree.node.ElementContainerTreeNode;
import de.imise.tool3lgm.graphtools.view.tree.node.LGMTreeNode;
import de.imise.util.StringUtils;
import de.imise.util.swing.SwingUtils;
import de.imise.util.swing.component.LimitedHeightScrollTreePane;

/**
 * Mit diesem Panel können für ein Element über einen Pfad von mehr als einer
 * Edge verbundene Elemente angezeigt, hinzugefügt und entfernt werden.
 *
 * @author AXS created on 20.05.2007
 */
public class PathConnectionPanel extends AbstractExpandablePanel {

    /**  */
    protected final ElementDialogPanelTree ltree;

    /**  */
    protected final ElementDialogPanelTree rtree;

    /**  */
    private final JLabel rLabel;

    /**  */
    private final JPanel buttonpanel;

    /**  */
    private final boolean showRightTree;

    /**  */
    private final LGMAction addAction;

    /**  */
    private final LGMAction removeAction;

    /**  */
    private final LGMAction newElementAction;

    /**
     * @param dialog
     * @param metaPath
     */
    public PathConnectionPanel(final AbstractElementPropertyDialog dialog, final MetaPath metaPath) {
        this(dialog, -1, metaPath);
    }

    /**
     * @param dialog
     * @param titleLabelOption
     * @param westLabelOption
     * @param metaPath
     */
    public PathConnectionPanel(final AbstractElementPropertyDialog dialog, final PanelLabelOption titleLabelOption, final PanelLabelOption westLabelOption, final MetaPath metaPath) {
        this(dialog, titleLabelOption, westLabelOption, false, metaPath);
    }

    /**
     * @param dialog
     * @param maxLines
     * @param metaPath
     */
    public PathConnectionPanel(final AbstractElementPropertyDialog dialog, final int maxLines, final MetaPath metaPath) {
        this(dialog, LABEL_END_ELEMENT_TYPE, LABEL_END_ELEMENT_TYPE, maxLines, false, metaPath);
    }

    /**
     * @param dialog
     * @param maxLines
     * @param renderLeftTreeAsList
     * @param metaPath
     */
    public PathConnectionPanel(final AbstractElementPropertyDialog dialog, final int maxLines, final boolean renderLeftTreeAsList, final MetaPath metaPath) {
        this(dialog, LABEL_END_ELEMENT_TYPE, LABEL_END_ELEMENT_TYPE, maxLines, renderLeftTreeAsList, metaPath);
    }

    /**
     * @param dialog
     * @param titleLabelOption
     * @param westLabelOption
     * @param renderLeftTreeAsList
     * @param metaPath
     */
    public PathConnectionPanel(final AbstractElementPropertyDialog dialog, final PanelLabelOption titleLabelOption, final PanelLabelOption westLabelOption, final boolean renderLeftTreeAsList, final MetaPath metaPath) {
        this(dialog, titleLabelOption, westLabelOption, -1, renderLeftTreeAsList, metaPath);
    }

    /**
     * @param dialog
     * @param titleLabelOption
     * @param westLabelOption
     * @param maxLines
     * @param renderLeftTreeAsList
     * @param metaPath
     */
    public PathConnectionPanel(final AbstractElementPropertyDialog dialog, final PanelLabelOption titleLabelOption, final PanelLabelOption westLabelOption, final int maxLines, final boolean renderLeftTreeAsList, final MetaPath metaPath) {
        super(dialog, titleLabelOption, westLabelOption, metaPath);
        showRightTree = isEditable();
        if (!showRightTree) {
            setUnexpandable();
        }
        setPreferredSize(new Dimension(550, 350));
        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
        GridBagConstraints constraints = new GridBagConstraints();

        JLabel ltreeLabel = westLabel;
        ModelElement me = getModelElement();
        GraphDocument mainDoc = getMainDoc();
        ElementContainer ec = me.getContainer(mainDoc);
        boolean sortLeftTreeAlphabetical = sortLeftTreeRootChildrenAlphabetical();
        ltree = new ElementDialogPanelTree(ec, sortLeftTreeAlphabetical, maxLines, renderLeftTreeAsList);
        ltree.setRootVisible(false);
        ltree.setShowsRootHandles(true);
        ltree.setCellRenderer(treeRenderer);
        ltree.getSelectionModel().setSelectionMode(getTreesSelectionModel());

        constraints.anchor = GridBagConstraints.WEST;
        add(this, ltreeLabel, constraints, 0, 0, 2, 1);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1d;
        constraints.weighty = 1d;
        LimitedHeightScrollTreePane ltreeScrollPane = ltree.getScrollPane();
        add(this, ltreeScrollPane, constraints, 0, 1, 2, 4);

        if (showRightTree) {
            constraints.anchor = GridBagConstraints.EAST;
            constraints.weightx = 0d;
            constraints.weighty = 0d;
            constraints.fill = GridBagConstraints.NONE;
            add(this, viewButton, constraints, 1, 5, 1, 1);
            constraints.weightx = 1d;
            constraints.weighty = 1d;
            constraints.fill = GridBagConstraints.BOTH;
            String rtreeLabelString = getResString("frei");
            rtreeLabelString = StringUtils.capitalizeFirstChar(rtreeLabelString);
            rLabel = new JLabel(rtreeLabelString);
            rtree = new ElementDialogPanelTree(rtreeLabelString, mainDoc, maxLines);
            rtree.getSelectionModel().setSelectionMode(getTreesSelectionModel());
            rtree.setRootVisible(false);
            rtree.setShowsRootHandles(true);
            TreeRenderer highlightErrorElementsTreeRenderer = new PanelTreeRenderer(this);
            rtree.setCellRenderer(highlightErrorElementsTreeRenderer);

            //Buttons & Actions erstellen, Actions setzen
            addAction = getConnectAction();
            removeAction = getDisconnectAction();
            boolean supportNewElementAction = metaPath.isCreatable(true);
            newElementAction = supportNewElementAction ? getNewConnectedElementAction() : null;

            buttonpanel = createBetweenTreesButtonPanel(addAction, removeAction, newElementAction);

            //alles dafür tun, dass beide Dialogseiten gleich breit sind. Das wird über die PreferredSize der breitesten Komponente gesteuert.
            SwingUtils.fillToSameLength(westLabel, rLabel);
            SwingUtils.setSamePreferredSize(westLabel, rLabel);
            SwingUtils.setSamePreferredSize(ltreeScrollPane, rtree.getScrollPane());

        } else {
            rLabel = null;
            addAction = null;
            removeAction = null;
            newElementAction = null;
            buttonpanel = null;
            rtree = null;
        }
        initTreeListenerAndDragNDrop();
        showFullDialog(true);
    }

    /**
     * @return <code>true</code> if the left tree must be sorted alphabetical
     *         and <code>false</code> if all connected elements should be
     *         displayed in the same order in wich they are connected.
     */
    private boolean sortLeftTreeRootChildrenAlphabetical() {
        List<ElementaryMetaPath> elementaryMetaPaths = metaPath.getElementaryMetaPaths();
        if (elementaryMetaPaths.size() == 1) {
            ElementaryMetaPath elementaryMetaPath = elementaryMetaPaths.get(0);
            Class<? extends Edge> edgeClass = elementaryMetaPath.getEdgeClass();
            MetaModel metaModel = getMetaModel();
            if (metaModel.isOrderedEdgeClass(edgeClass)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param c
     * @param gbc
     */
    public void addUnderLeftTree(final Component c, final GridBagConstraints gbc) {
        add(this, c, gbc, 0, 5, 1, 1);
    }

    /**
     * @param c
     * @param gbc
     * @param gridwidth
     */
    public void addSouth(final Component c, final GridBagConstraints gbc, final int gridwidth) {
        add(this, c, gbc, 0, 6, gridwidth, 1);
    }

    /**
     * Rückgabewert legt fest, wie die Selektion in den Bäumen ist
     *
     * @return
     */
    protected int getTreesSelectionModel() {
        return TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION;
    }

    @Override
    public void update() {
        updateLeftTree();
        if (showRightTree && isRightSideVisible()) {
            buildRightTree();
        }
        revalidate();
        repaint();
    }

    /**
     * @return <code>true</code> if the panel's path
     */
    protected boolean isEditable() {
        return metaPath.isCreatable(false);// && metaPath.isRemoveable(true);//isRemoveable(...) prüft, ob sich das Element des Dialoges in Luft auflöst, wenn man die
        //MinCardinality unterschreitet. Das soll hier aber explizit zugelassen werden!

    }

    @Override
    protected final void showFullDialog() {
        if (showRightTree) {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.fill = GridBagConstraints.NONE;
            add(this, buttonpanel, constraints, 2, 3, 1, 2);
            constraints.anchor = GridBagConstraints.WEST;
            add(this, rLabel, constraints, 3, 0, 1, 1);
            constraints.anchor = GridBagConstraints.WEST;
            constraints.fill = GridBagConstraints.BOTH;
            constraints.weightx = 1d;
            constraints.weighty = 1d;
            add(this, rtree.getScrollPane(), constraints, 3, 1, 1, 4);
        }
    }

    @Override
    protected final void showPartlyDialog() {
        if (showRightTree) {
            remove(buttonpanel);
            remove(rLabel);
            remove(rtree.getScrollPane());
        }
    }

    /**
     * Set aller TreeNodes, die im linken Baum bereits verknüpft sind und im
     * rechten nicht mehr auftauchen sollen
     */
    private final Collection<ElementContainer> childrenToExcludeFromRtree = new HashSet<>();

    /**
     * Sammelt für die letzte Edge alle Elemente ein, die im rechten Baum nicht
     * mehr angezeigt werden sollen, weil sie bereits verknüpft sind und kein
     * weiteres Mal verknüpft werden können.
     *
     * @param edgeIndex
     * @param potentialExcludeChildren
     * @param clear
     */
    private void addChildrenToExcludeFromRtree(final int edgeIndex, final Collection<ElementContainer> potentialExcludeChildren, final boolean clear) {
        //nur bei Panels, bei denen der Pfad eindeutig verknüpfbar ist, kann man Elemente im rechten Baum ausschließen
        if (isConnectionPointUnique) {
            //beim ersten Durchlauf sollte die alte Collection geleert werden
            if (clear) {
                childrenToExcludeFromRtree.clear();
            }
            //wenn es der Index der letzten Edge ist -> zu den Ausschlusselementen hinzufügen
            if (edgeIndex == metaPath.getElementaryMetaPaths().size() - 1) {
                childrenToExcludeFromRtree.addAll(potentialExcludeChildren);
            }
        }
    }

    /**
     *
     */
    private void updateLeftTree() {
        ltree.saveExpansionAndSelection();
        ltree.reset();
        buildLeftTree();
        ltree.reloadModel();
        ltree.restoreExpansionAndSelection();
    }

    /**
     * Baut im linken Baum den gesamten Pfad auf
     *
     * @return Collection aller Blätter im Baum
     */
    protected Collection<ElementContainerTreeNode> buildLeftTree() {
        int edgeIndex = 0;
        //Durch diesen Aufruf hier geht das erstmal nicht für parallele Pfade, zumindes nicht für Vereinigungspfade. Aber im Moment gibt es dafür keinen Anwendungsfall
        List<ElementaryMetaPath> elementaryMetaPaths = metaPath.getElementaryMetaPaths();
        int elemetaryMetaPathCount = elementaryMetaPaths.size();
        ElementaryMetaPath elementaryMetaPath = elementaryMetaPaths.get(edgeIndex);
        Class<? extends ModelElement> pathStepEndClass = elementaryMetaPath.getEndClass();
        Class<? extends Edge> edgeClass = elementaryMetaPath.getEdgeClass();
        Direction direction = elementaryMetaPath.getDirection();
        ModelElement me = getModelElement();
        GraphDocument mainDoc = getMainDoc();
        boolean alphabetical = sortLeftTreeRootChildrenAlphabetical();
        List<ElementContainer> all = me.getConnectedContainers(pathStepEndClass, mainDoc, edgeClass, direction, null, alphabetical);
        addChildrenToExcludeFromRtree(edgeIndex, all, true);
        // nur Node für Elemente in der all-Liste bis zur Größe der direkt verbundenen dürfen am Ende selektierbar sein
        int firstNonSelectableIndex = all.size();
        if (OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARTS.is()) {
            all.addAll(me.getPartConnectedContainers(pathStepEndClass, mainDoc, edgeClass, direction, null, alphabetical));
        }
        if (OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARENTS.is()) {
            all.addAll(me.getParentConnectedContainers(pathStepEndClass, mainDoc, edgeClass, direction, null, alphabetical));
        }
        ImmutableList.Builder<ElementContainerTreeNode> leafs = ImmutableList.builder();
        List<ElementContainerTreeNode> firstLevelNodes = new ArrayList<>(all.size());
        for (ElementContainer ec : all) {
            ElementContainerTreeNode node = ltree.addObject(ec, true, false, false);
            firstLevelNodes.add(node);
        }
        List<ElementContainerTreeNode> nextStepStartNodes = firstLevelNodes;
        for (edgeIndex = 1; edgeIndex < elemetaryMetaPathCount; edgeIndex++) {
            elementaryMetaPath = elementaryMetaPaths.get(edgeIndex);
            pathStepEndClass = elementaryMetaPath.getEndClass();
            edgeClass = elementaryMetaPath.getEdgeClass();
            direction = elementaryMetaPath.getDirection();
            List<ElementContainerTreeNode> newNextStartNodes = new ArrayList<>();
            for (ElementContainerTreeNode node : nextStepStartNodes) {
                ElementContainer nodeElementContainer = node.getUserObject();
                me = nodeElementContainer.getElement();
                List<ElementContainer> connected = me.getConnectedContainers(pathStepEndClass, mainDoc, edgeClass, direction, null, false);
                addChildrenToExcludeFromRtree(edgeIndex, connected, false);
                for (ElementContainer ec : connected) {
                    ElementContainerTreeNode newNode = ltree.addObject(ec, node, null, true, false, false);
                    if (edgeIndex + 1 == elemetaryMetaPathCount) {
                        leafs.add(newNode);
                    }
                    newNextStartNodes.add(newNode);
                }
            }
            nextStepStartNodes = newNextStartNodes;
        }
        // alle Elemente die von den Parts oder Parents kamen, nichtselektierbar setzen
        for (int i = firstLevelNodes.size() - 1; i >= firstNonSelectableIndex; i--) {
            firstLevelNodes.get(i).setSelectable(false);
        }
        return leafs.build();
    }

    /**
     *
     */
    private void buildRightTree() {
        rtree.saveExpansion();
        rtree.saveSelection();
        rtree.reset();
        //disable sorting -> we have 2 lists, which must both be sorted
        //independently of each other and must not be sorted into each other
        LGMTreeNode<?> root = rtree.getRoot();
        root.setSort(false);

        //find all elements which should be connected to solve a missing path error
        List<ModelElement> errorSolutionElements = getErrorSolutionElements();

        //add all connectable element containers to the tree but not
        //these with an element to solve an error
        Set<ElementContainer> errorSolutionElementContainers = new HashSet<>();
        //the available connectables are sorted
        List<ElementContainer> availableConnectables = getAvailableConnectables();
        for (ElementContainer ec : availableConnectables) {
            ModelElement me = ec.getElement();
            if (errorSolutionElements.contains(me)) {
                //store the connectable element containers with an element
                //to solve an error and not connect them now
                errorSolutionElementContainers.add(ec);
            } else {
                rtree.addObject(ec, childrenToExcludeFromRtree, false, true);
            }
        }
        //disable sorting during insert -> the connectable element stay at the beginning
        int errorSolutionElementInsertIndex = 0;
        for (ElementContainer ec : errorSolutionElementContainers) {
            ElementContainerTreeNode errorSulutionTreeNode = rtree.insertObject(errorSolutionElementInsertIndex++, ec, childrenToExcludeFromRtree, false, true);
            ModelElement me = ec.getElement();
            AbstractConsistencyError errorElementIsSolutionFor = getErrorElementIsSolutionFor(me);
            errorSulutionTreeNode.setConsistencyError(errorElementIsSolutionFor);
        }
        rtree.reloadModel();
        rtree.restoreExpansion();
        rtree.restoreSelection();
    }

    /**
     * @return all elements which should be connected to solve a missing path
     *         error
     */
    private List<ModelElement> getErrorSolutionElements() {
        List<ModelElement> errorSolutionElements = new ArrayList<>();
        Collection<AbstractConsistencyError> consistencyErrors = getConsistencyErrors();
        for (AbstractConsistencyError consistencyError : consistencyErrors) {
            if (consistencyError instanceof MissingPathError) {
                MissingPathError missingPathError = (MissingPathError) consistencyError;
                Collection<ModelElement> missingElements = missingPathError.getMissingElements();
                errorSolutionElements.addAll(missingElements);
            }
        }
        return errorSolutionElements;
    }

    /**
     * @param me
     * @return an ConsistencyError, if the given ModelElement is an element to
     *         resolve the error, otherwise <code>null</code>
     */
    private AbstractConsistencyError getErrorElementIsSolutionFor(final ModelElement me) {
        Collection<AbstractConsistencyError> consistencyErrors = getConsistencyErrors();
        for (AbstractConsistencyError consistencyError : consistencyErrors) {
            if (consistencyError instanceof MissingPathError) {
                MissingPathError missingPathError = (MissingPathError) consistencyError;
                Collection<ModelElement> missingElements = missingPathError.getMissingElements();
                if (missingElements.contains(me)) {
                    return missingPathError;
                }
            }
        }
        return null;
    }

    @Override
    protected final DragNDropActionChain[] collectDragNDropActionChains() {
        DragNDropActionChain dndAC1 = DragNDropInitializer.createNewDragNDropActionChain(rtree, ltree, addAction);
        DragNDropActionChain dndAC2 = DragNDropInitializer.createNewDragNDropActionChain(ltree, rtree, removeAction);

        return new DragNDropActionChain[] {
                dndAC1, dndAC2
        };

    }

    @Override
    public final ElementDialogPanelTree[] getAllDragNDropTrees() {
        return new ElementDialogPanelTree[] {
                rtree, ltree
        };
    }

    /**
     * Liefert den TreePath, der im linken Baum als selektiert gilt und an den
     * der neue Pfad(teil) angehängt werden soll.
     *
     * @return
     */
    protected TreePath getConnectActionTargetTreeSelectionPath() {
        // Anzahl der selektierten Zeilen im linken Ziel-Baum ermitteln
        int targetTreeSelRowsCount = ltree.getSelectionCount();

        // wenn im linken Ziel-Baum nur eine Zeile enthalten ist und bisher nichts
        // selektiert ist, selektiere diese eine Zeile (falls die Zeile sich nicht
        // selektieren lässt, weil sie dialbels ist, wenn si über Teil-Von-Beziehungen
        // ererbt wurde, dann its danach immer noch nichts selektiert
        if (targetTreeSelRowsCount == 0 && ltree.getRowCount() == 1) {
            ltree.setSelectionRow(0);
        }

        // Anzahl der selektierten Zeilen im linken Ziel-Baum erneut ermitteln
        targetTreeSelRowsCount = ltree.getSelectionCount();

        //ausgewählter Path im TargetTree -> wenn sich vorher was selktieren ließ, dann das sonst der root
        TreePath targetTreeSelectionPath = targetTreeSelRowsCount > 0 ? ltree.getSelectionPath() : new TreePath(ltree.getModel().getRoot());
        return targetTreeSelectionPath;
    }

    /**
     * @return
     */
    protected LGMAction getConnectAction() {
        return new LGMAction(DialogActionCommands.ACTION_DIALOG_CONNECT_ELEMENT) {

            @Override
            public void execute(final EventObject eo) {
                //falls in den TargetTree gedroppt wurde -> selektiere den zur DropPosition nächstegelegenen TreePath
                LGMActionLibrary.getDragNDropLocateElementAsTargetAction(ltree).execute(eo);
                // Anzahl der selektierten Elemente im rechten Baum, die verbunden werden sollen, ermitteln
                int srcTreeSelRowsCount = rtree.getSelectionCount();
                if (srcTreeSelRowsCount < 1) {
                    return;
                }
                TreePath targetTreeSelectionPath = getConnectActionTargetTreeSelectionPath();
                TreePath[] sourceTreePaths = rtree.getSelectionPaths();
                connect(targetTreeSelectionPath, sourceTreePaths);

                //TODO: das hier expandiert das neue überhaupt nicht, sondern nur bis zum vorher schon geöffneten Node. Das ist doof!
                ltree.expandPath(targetTreeSelectionPath);
                ltree.clearSelection();
            }
        };
    }

    /**
     * @param treePath
     * @return
     */
    protected static ModelElement getPathModelElement(final TreePath treePath) {
        ElementContainerTreeNode node = (ElementContainerTreeNode) treePath.getLastPathComponent();
        return node.getModelElement();
    }

    /**
     * Methode liefert eine <code>LGMAction</code> zurück, die das Verschieben
     * von Elementen aus dem <code>srcTree</code> in den <code>targetTree</code>
     * realisiert. Diese <code>LGMAction</code> sollte an die "removeButtons"
     * der Panels angefügt werden.
     *
     * @param srcTree linker Baum mit dem verknüpften Pfaden
     * @param targetTree rechter Baum mit den Elementen, die ausgewählt werden
     *            können
     */
    protected LGMAction getDisconnectAction() {
        final PathConnectionPanel panel = this;
        return new LGMAction(DialogActionCommands.ACTION_DIALOG_DISCONNECT_ELEMENT) {

            @Override
            public void execute(final EventObject eo) {
                int selrows = ltree.getSelectionCount();
                if (selrows < 1) {
                    return;
                }

                TreePath[] path2disconnect = ltree.getSelectionPaths();
                for (int i = 0; i < path2disconnect.length; i++) {
                    //das ist der Index der Edge im Pfad, ab der entfernt werden soll
                    int treePathEdgeIndex = path2disconnect[i].getPathCount() - 2;
                    ModelElement element2Unlink = PathConnectionPanel.getPathModelElement(path2disconnect[i]);
                    ModelElement parentOfElement2Unlink = PathConnectionPanel.getPathModelElement(path2disconnect[i].getParentPath());
                    panel.disconnect(parentOfElement2Unlink, element2Unlink, treePathEdgeIndex);
                }
            }
        };
    }

    /**
     * @param startInPath
     * @param endInPath
     * @param edgeIndexInPath
     */
    protected final void disconnect(final ModelElement startInPath, final ModelElement endInPath, final int edgeIndexInPath) {
        disconnect(startInPath, endInPath, edgeIndexInPath, false);
    }

    /**
     * @param startInPath
     * @param endInPath
     * @param edgeIndexInPath
     * @param ensureConsistency
     */
    private final void disconnect(final ModelElement startInPath, final ModelElement endInPath, final int edgeIndexInPath, final boolean ensureConsistency) {
        GDCollection gdcoll = getCollection();
        //das disconnect sollte nur angeboten werden, wenn der Path ceratable ist und dann kommt bei metaPath.getElementaryMetaPaths() auch was sinnvolles zurück
        List<ElementaryMetaPath> elementaryMetaPaths = metaPath.getElementaryMetaPaths();
        ElementaryMetaPath elementaryMetaPath = elementaryMetaPaths.get(edgeIndexInPath);
        int pid = getTransactionID();
        Class<? extends Edge> edgeClass = elementaryMetaPath.getEdgeClass();
        Direction direction = elementaryMetaPath.getDirection();
        ModelElement dialogElement = getModelElement();
        gdcoll.unlink(startInPath, endInPath, edgeClass, direction, dialogElement, pid);
        //das hier stand mal drin, aber gdcoll.unlink() löscht startInPath auch schon, wenn das es durch das unlink inkonsistent wird
        //        if (!startInPath.isConsistent()) {
        //            gdcoll.deleteElement(startInPath, selDoc, pid);
        //        }
        int nextEdgeIndexInPath = edgeIndexInPath + 1;
        if (nextEdgeIndexInPath < elementaryMetaPaths.size()) {
            ElementaryMetaPath nextElementaryMetaPath = elementaryMetaPaths.get(nextEdgeIndexInPath);
            Class<? extends Edge> nextEdgeClass = nextElementaryMetaPath.getEdgeClass();
            Class<? extends ModelElement> nextElementClassInPath = MetaPathFunctions.getMetaPathsConnectingClass(elementaryMetaPath, nextElementaryMetaPath);
            List<ModelElement> connectedElements = endInPath.getConnectedElements(nextElementClassInPath, nextEdgeClass);
            for (ModelElement connectedElement : connectedElements) {
                disconnect(endInPath, connectedElement, nextEdgeIndexInPath);
            }
        }
        if (!endInPath.isConsistent()) {
            GraphDocument selectedDoc = getSelectedDoc();
            gdcoll.deleteElement(endInPath, selectedDoc, pid);
        }
    }

    /**
     * Hängt an den targetTreePath die lastPathComponent der sourceTreePaths an.
     * Wenn als targetTreePath ein vollständiger Pfad übergeben wird, dann
     * werden die sourceTReePath-Elemente an den Parent der LastPathComponent
     * gehängt. Wenn der Pfad gleich nur bis zum Parent geht, dann werden sie da
     * angehängt. Ist der Pfad kürzer, dann wird er bis zum Parent erzeugt und
     * dann die übergebenen sourceTreePath-Elemente angehängt.
     *
     * @param targetTreePath
     * @param sourceTreePaths
     */
    protected void connect(final TreePath targetTreePath, final TreePath... sourceTreePaths) {

        //das ist der Index der Edge im Pfad, ab der hinzugefügt werden soll
        int targetTreePathEdgeIndex = targetTreePath.getPathCount() - 1;

        TreePath realTargetTreePath = targetTreePath;
        //falls der TargetPath bis zum letzten Element angegeben wurde, dann soll eigenlich an den Parent angehängt werden, weil
        //die letzte Elemente im Pfad immer die anzuhängenden selbst sind, die auch auf der rechten Seite ausgewählt werden können
        if (targetTreePathEdgeIndex == getEdgesInPathCount()) {
            //nimm vom aktuell auf der linken Seite ausgewählten Pfad das vorletzte Pfadelement
            realTargetTreePath = realTargetTreePath.getParentPath();
            targetTreePathEdgeIndex--;
        }

        //Element holen, an das der Pfad angehängt werden soll
        ModelElement targetElement = getPathModelElement(realTargetTreePath);

        ImmutableList.Builder<ModelElement> elements2Connect = ImmutableList.builder();
        for (TreePath sourceTreePath : sourceTreePaths) {
            ModelElement element2Connect = getPathModelElement(sourceTreePath);
            elements2Connect.add(element2Connect);
        }
        connect(targetElement, elements2Connect.build(), targetTreePathEdgeIndex);
    }

    /**
     * @return
     */
    private LGMAction getNewConnectedElementAction() {
        if (!metaPath.isCreatable(true)) {
            return null;
        }
        final GDCollection gdcoll = getCollection();
        MetaModel metaModel = gdcoll.getMetaModel();
        if (metaModel.isSlaveType(searchElementClass)) {
            return null;
        }
        return new LGMAction(DialogActionCommands.ACTION_DIALOG_NEW_ELEMENT) {
            @Override
            public void execute(final EventObject eo) {
                //wenn eindutig fest steht, an welchen Node ein neues Element gehängt werden sollte, dann wird
                //es auch gleich angehängt
                if (isConnectionPointUnique) {
                    connectToFirstPath(null);
                } else { //es ist nicht klar, wohin ein neues Element gehängt werden sollte -> nur neu erzeugen und nicht verknüpfen
                    ElementaryMetaPath lastElementaryMetaPath = metaPath.getLastElementaryMetaPath();
                    LGMGraphDocument selectedDoc = gdcoll.getSelectedDoc();
                    int pid = getTransactionID();
                    PathFunctions.createNodeWithContainerAndDependents(selectedDoc, null, lastElementaryMetaPath, null, pid);
                }
            }
        };
    }

    /**
     * @param endElement
     */
    public final void createPath(final ModelElement endElement) {
        if (!metaPath.isCreatable(endElement != null)) {
            return;
        }
        ModelElement me = getModelElement();
        LGMGraphDocument selectedDoc = getSelectedDoc();
        int pid = getTransactionID();
        selectedDoc.createPath(me, endElement, metaPath, true, pid);
    }

    @Override
    public Collection<JComponent> getToolTipTargets() {
        return Lists.newArrayList(ltree, rtree);
    }

}