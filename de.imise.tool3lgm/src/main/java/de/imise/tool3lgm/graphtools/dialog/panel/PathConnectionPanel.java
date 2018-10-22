package de.imise.tool3lgm.graphtools.dialog.panel;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.getEndClass;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.getStartClass;

import java.awt.Component;
/**
 * @author AXS created on 20.05.2007
 */
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.HashSet;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.action.LGMActionLibrary;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.DragNDropInitializer;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.DragNDropInitializer.DragNDropActionChain;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.PathConnectionState;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.tools.LGMTree;
import de.imise.tool3lgm.tools.LGMTreeNode;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty;
import de.imise.util.StringUtils;

/**
 * Mit diesem Panel können für ein Element über einen Pfad von mehr als einer Edge verbundene Elemente
 * angezeigt, hinzugefügt und entfernt werden.
 */
public class PathConnectionPanel extends AbstractExpandablePanel {

    protected final LGMTree ltree;

    protected final LGMTree rtree;

    protected final DefaultTreeModel lmodel;

    private final DefaultTreeModel rmodel;

    protected final LGMTreeNode lroot;

    protected final LGMTreeNode rroot;

    private final JLabel rLabel;

    private final JScrollPane rScollPane;

    private final JPanel buttonpanel;

    private final boolean showRightTree;

    private final LGMAction addAction;

    private final LGMAction removeAction;

    private final LGMAction newElementAction;

    @SafeVarargs
    public PathConnectionPanel(final ElementPropertyDialog dialog, final boolean showRightTree, final Class<? extends Edge>... edgeClasses) {
        this(dialog, false, showRightTree, edgeClasses);
    }

    @SafeVarargs
    public PathConnectionPanel(final ElementPropertyDialog dialog, final boolean showRightTree, final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge>... edgeClasses) {
        this(dialog, false, showRightTree, searchElementClass, edgeClasses);
    }

    @SafeVarargs
    public PathConnectionPanel(final ElementPropertyDialog dialog, final boolean labelLastEdgeName, final boolean showRightTree, final Class<? extends Edge>... edgeClasses) {
        this(dialog, labelLastEdgeName, showRightTree, null, edgeClasses);
    }

    @SafeVarargs
    public PathConnectionPanel(final ElementPropertyDialog dialog, final boolean labelLastEdgeName, final boolean showRightTree, final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge>... edgeClasses) {
        super(dialog, labelLastEdgeName, searchElementClass, edgeClasses);
        this.showRightTree = showRightTree;
        setPreferredSize(new Dimension(550, 350));
        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
        GridBagConstraints constraints = new GridBagConstraints();

        //wenn der Pfad aus mehr als einer Edge besteht, dann soll über dem linken Baum einfach "verbunden" stehen
        String ltreeLabelString = lastEdgeIndex > 0 ? getResString("verb") : null;
        //wenn der Pfad aus nur einer Edge besteht
        if (ltreeLabelString == null) {
            //schreibe den Namen der Edge in der richtigen Richtung über den linken Baum
            Class<? extends Edge> lastEdge = edgeClasses[lastEdgeIndex];
            ltreeLabelString = directions[lastEdgeIndex] == Direction.FORWARDS ? ModelConstants.getForwardMetaAssociationName(lastEdge) : ModelConstants.getBackwardMetaAssociationName(lastEdge);
        }
        String rtreeLabelString = getResString("frei");
        ltreeLabelString = StringUtils.capitalizeFirstChar(ltreeLabelString);
        rtreeLabelString = StringUtils.capitalizeFirstChar(rtreeLabelString);

        westLabel.setText(ltreeLabelString);
        JLabel ltreeLabel = westLabel;
        lroot = new LGMTreeNode(getModelElement().getContainer(mainDoc), false, getSortLeftTreeRootChildrenAlphabetical());
        lmodel = new DefaultTreeModel(lroot);
        ltree = new LGMTree(lmodel, mainDoc);
        ltree.setRootVisible(false);
        ltree.setShowsRootHandles(true);
        ltree.setCellRenderer(treeRenderer);
        ltree.getSelectionModel().setSelectionMode(getTreesSelectionModel());
        JScrollPane sp = new JScrollPane(ltree);

        constraints.ipadx = 0;
        constraints.ipady = 0;
        constraints.anchor = GridBagConstraints.WEST;
        add(this, ltreeLabel, constraints, 0, 0, 2, 1);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1d;
        constraints.weighty = 1d;
        add(this, sp, constraints, 0, 1, 2, 4);

        if (showRightTree) {
            constraints.anchor = GridBagConstraints.EAST;
            constraints.weightx = 0d;
            constraints.weighty = 0d;
            constraints.fill = GridBagConstraints.NONE;
            //das hier braucht man wahrscheinlich nur unter Windows. Auf dem Mac sieht das komisch aus
            //            constraints.ipadx = -30;
            //            constraints.ipady = -10;
            add(this, viewButton, constraints, 1, 5, 1, 1);
            constraints.weightx = 1d;
            constraints.weighty = 1d;
            constraints.fill = GridBagConstraints.BOTH;
            rLabel = new JLabel(rtreeLabelString);
            rroot = new LGMTreeNode(rtreeLabelString, false);
            rmodel = new DefaultTreeModel(rroot);
            rtree = new LGMTree(rmodel, mainDoc);
            rtree.getSelectionModel().setSelectionMode(getTreesSelectionModel());
            rtree.setRootVisible(false);
            rtree.setShowsRootHandles(true);
            rtree.setCellRenderer(treeRenderer);
            rScollPane = new JScrollPane(rtree);

            //Buttons & Actions erstellen, Actions setzen
            addAction = getConnectAction();
            removeAction = getDisconnectAction();
            newElementAction = getNewConnectedElementAction();

            buttonpanel = new JPanel();
            buttonpanel.setSize(30, 250);
            buttonpanel.setLayout(new GridLayout(3, 1));
            buttonpanel.add(new JButton(addAction));
            buttonpanel.add(new JButton(removeAction));
            if (newElementAction != null) {
                buttonpanel.add(new JButton(newElementAction));
            }
            makeSameSize(westLabel, rLabel);
            // dieses setzen der Dimension muss sein, damit sich der rechte Baum nie
            // mehr Platz holt,
            // als ihm in den Constraints gegeben wurde (spLinks und spRechts haben
            // beide weigthx=0.5)
            // ->jetzt ist es egal, wenn im linken Baum nichts steht, beide Baeume
            // sind immer gleich breit!
            rScollPane.setPreferredSize(new Dimension(1, 1));

        } else {
            rLabel = null;
            rroot = null;
            rmodel = null;
            addAction = null;
            removeAction = null;
            newElementAction = null;
            buttonpanel = null;
            rScollPane = null;
            rtree = null;
        }
        initTreeListenerAndDragNDrop();
        showFullDialog(true);
    }

    private void makeSameSize(final JComponent c1, final JComponent c2) {
        JComponent larger = c1.getPreferredSize().width > c2.getPreferredSize().width ? c1 : c2;
        JComponent smaller = larger == c1 ? c2 : c1;
        smaller.setPreferredSize(larger.getPreferredSize());
        smaller.setMinimumSize(larger.getMinimumSize());
        smaller.setMaximumSize(larger.getMaximumSize());
    }

    public void addUnderLeftTree(final Component c, final GridBagConstraints gbc) {
        add(this, c, gbc, 0, 5, 1, 1);
    }

    public void addSouth(final Component c, final GridBagConstraints gbc, final int gridwidth) {
        add(this, c, gbc, 0, 6, gridwidth, 1);
    }

    /**
     * Wenn <code>true</code> werden die TreeNodes unter dem Root im linken Baum sortiert
     *
     * @return
     */
    protected boolean getSortLeftTreeRootChildrenAlphabetical() {
        return true;
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
            add(this, rScollPane, constraints, 3, 1, 1, 4);
        }
    }

    @Override
    protected final void showPartlyDialog() {
        if (showRightTree) {
            remove(buttonpanel);
            remove(rLabel);
            remove(rScollPane);
        }
    }

    /**
     * Set aller TreeNodes, die im linken Baum bereits verknüpft sind und im rechten nicht mehr auftauchen sollen
     */
    private final Collection<ElementContainer> childrenToExcludeFromRtree = new HashSet<>();

    /**
     * Sammelt für die letzte Edge alle Elemente ein, die im rechten Baum nicht mehr angezeigt werden sollen, weil
     * sie bereits verknüpft sind und kein weiteres Mal verknüpft werden können.
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
            if (edgeIndex == lastEdgeIndex) {
                childrenToExcludeFromRtree.addAll(potentialExcludeChildren);
            }
        }
    }

    private void updateLeftTree() {
        ltree.saveExpansionAndSelection();
        lroot.removeAllChildren();
        ltree.reset();
        buildLeftTree();
        lmodel.reload();
        ltree.restoreExpansionAndSelection();
    }

    /**
     * Baut im linken Baum den gesamten Pfad auf
     *
     * @return Collection aller Blätter im Baum
     */
    protected Collection<LGMTreeNode> buildLeftTree() {
        int edgeIndex = 0;
        Class<? extends ModelElement> pathStepEndClass = getPathStepEndElementClass(edgeIndex);
        ModelElement me = getModelElement();
        PathConnectionState connectionState = directions[edgeIndex] == Direction.FORWARDS ? PathConnectionState.FROM_ELEMENT : PathConnectionState.TO_ELEMENT;
        List<ElementContainer> all = me.getConnectedContainer(pathStepEndClass, mainDoc, edgeClasses[edgeIndex], connectionState);
        addChildrenToExcludeFromRtree(edgeIndex, all, true);
        // nur Node für Elemente in der all-Liste bis zur Größe der direkt verbundenen dürfen am Ende selektierbar sein
        int firstNonSelectableIndex = all.size();
        if (UserProperties.is(BooleanProperty.OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARTS)) {
            all.addAll(me.getPartConnectedContainer(pathStepEndClass, mainDoc, edgeClasses[edgeIndex], connectionState));
        }
        if (UserProperties.is(BooleanProperty.OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARENTS)) {
            all.addAll(me.getParentConnectedContainer(pathStepEndClass, mainDoc, edgeClasses[edgeIndex], connectionState));
        }
        ImmutableList.Builder<LGMTreeNode> leafs = ImmutableList.builder();
        List<LGMTreeNode> firstLevelNodes = new ArrayList<>(all.size());
        for (ElementContainer ec : all) {
            LGMTreeNode node = ltree.addObject(ec, lroot, null, true, false, false);
            firstLevelNodes.add(node);
        }
        List<LGMTreeNode> nextStepStartNodes = firstLevelNodes;
        for (edgeIndex = 1; edgeIndex < edgeClasses.length; edgeIndex++) {
            connectionState = directions[edgeIndex] == Direction.FORWARDS ? PathConnectionState.FROM_ELEMENT : PathConnectionState.TO_ELEMENT;
            pathStepEndClass = getPathStepEndElementClass(edgeIndex);
            List<LGMTreeNode> newNextStartNodes = new ArrayList<>();
            for (LGMTreeNode node : nextStepStartNodes) {
                ElementContainer nodeElementContainer = (ElementContainer) node.getUserObject();
                me = nodeElementContainer.getElement();
                List<ElementContainer> connected = me.getConnectedContainer(pathStepEndClass, mainDoc, edgeClasses[edgeIndex], connectionState);
                addChildrenToExcludeFromRtree(edgeIndex, connected, false);
                for (ElementContainer ec : connected) {
                    LGMTreeNode newNode = ltree.addObject(ec, node, null, true, false, false);
                    if (edgeIndex + 1 == edgeClasses.length) {
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
        rroot.removeAllChildren();
        rtree.reset();
        for (ElementContainer ec : getAvailableConnectables()) {
            rtree.addObject(ec, rroot, childrenToExcludeFromRtree, false, true);
        }
        rmodel.reload();
        rtree.restoreExpansion();
        rtree.restoreSelection();
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
    public final LGMTree[] getAllDragNDropTrees() {
        return new LGMTree[] {
                rtree, ltree
        };
    }

    /**
     * Liefert den TreePath, der im linken Baum als selektiert gilt und an den der neue Pfad(teil)
     * angehängt werden soll.
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

    protected LGMAction getConnectAction() {
        return new LGMAction("", Tool3lgmConstants.getIcon("arrow_left2.gif")) {

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
                return;
            }
        };
    }

    private static ModelElement getPathModelElement(final TreePath treePath) {
        LGMTreeNode node = (LGMTreeNode) treePath.getLastPathComponent();
        return getNodeModelElement(node);
    }

    protected static ModelElement getNodeModelElement(final LGMTreeNode node) {
        ElementContainer ec = (ElementContainer) node.getUserObject();
        ModelElement me = ec.getElement();
        return me;
    }

    /**
     * Methode liefert eine <code>LGMAction</code> zurück, die das Verschieben von Elementen aus dem
     * <code>srcTree</code> in den <code>targetTree</code> realisiert. Diese <code>LGMAction</code>
     * sollte an die "removeButtons" der Panels angefügt werden.
     *
     * @param srcTree linker Baum mit dem verknüpften Pfaden
     * @param targetTree rechter Baum mit den Elementen, die ausgewählt werden können
     */
    protected LGMAction getDisconnectAction() {
        final PathConnectionPanel panel = this;
        return new LGMAction("", Tool3lgmConstants.getIcon("arrow_right2.gif")) {

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

    protected final void disconnect(final ModelElement startInPath, final ModelElement endInPath, final int edgeIndexInPath) {
        GraphDocument selDoc = getSelectedGraphDocument();
        GDCollection gdcoll = selDoc.getCollection();
        int pid = getTransactionID();
        Class<? extends Edge> edgeClass = edgeClasses[edgeIndexInPath];
        gdcoll.unlink(startInPath, endInPath, edgeClass, pid);
        if (!startInPath.isConsistent()) {
            gdcoll.deleteElement(startInPath, selDoc, pid);
        }
        int nextEdgeIndexInPath = edgeIndexInPath + 1;
        if (nextEdgeIndexInPath < edgeClasses.length) {
            Class<? extends Edge> nextEdgeClass = edgeClasses[nextEdgeIndexInPath];
            Class<? extends ModelElement> nextElementClassInPath = directions[nextEdgeIndexInPath] == Direction.FORWARDS ? getEndClass(nextEdgeClass) : getStartClass(nextEdgeClass);
            List<ModelElement> connectedElements = endInPath.getConnectedElements(nextElementClassInPath, nextEdgeClass);
            for (ModelElement connectedElement : connectedElements) {
                disconnect(endInPath, connectedElement, nextEdgeIndexInPath);
            }
        }
        if (!endInPath.isConsistent()) {
            gdcoll.deleteElement(endInPath, selDoc, pid);
        }
    }

    /**
     * Hängt an den targetTreePath die lastPathComponent der sourceTreePaths an. Wenn als targetTreePath ein vollständiger Pfad
     * übergeben wird, dann werden die sourceTReePath-Elemente an den Parent der LastPathComponent gehängt. Wenn der Pfad gleich
     * nur bis zum Parent geht, dann werden sie da angehängt. Ist der Pfad kürzer, dann wird er bis zum Parent erzeugt und dann
     * die übergebenen sourceTreePath-Elemente angehängt.
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
        if (targetTreePathEdgeIndex == edgeClasses.length) {
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

    private LGMAction getNewConnectedElementAction() {
        if (!isPathCreatable()) {
            return null;
        }
        if (ModelConstants.isSlaveType(searchElementClass)) {
            return null;
        }
        return new LGMAction(getResString("new")) {

            @Override
            public void execute(final EventObject eo) {
                //wenn eindutig fest steht, an welchen Node ein neues Element gehängt werden sollte, dann wird
                //es auch gleich angehängt
                if (isConnectionPointUnique) {
                    connectToFirstPath(null);
                } else { //es ist nicht klar, wohin ein neues Element gehängt werden sollte -> nur neu erzeugen und nicht verknüpfen
                    createNodeWithContainerAndDependents(doc.getCollection().getSelectedDoc(), null, edgeClasses[lastEdgeIndex], directions[lastEdgeIndex], null, Direction.FORWARDS, getTransactionID());
                }
            }
        };
    }

}