package de.imise.tool3lgm.graphtools.dialog.panel;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.elements.Doppelkante.FORWARD;

/**
 * @author AXS created on 20.05.2007
 */
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.Collection;
import java.util.EventObject;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.beust.jcommander.internal.Sets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.action.ActionNotDefinedForClassException;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.action.LGMActionLibrary;
import de.imise.tool3lgm.graphtools.dialog.action.LGMMouseListener;
import de.imise.tool3lgm.graphtools.dialog.action.LGMTreeSelectionListener;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.DragNDropInitializer;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.DragNDropInitializer.DragNDropActionChain;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.LGMDragNDropTree;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.tools.LGMTreeNode;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.util.Pair;
import de.imise.util.StringUtils;

/**
 * Mit diesem Panel können für ein Element über einen Pfad von mehr als einer Kante verbundene Elemente
 * angezeigt, hinzugefügt und entfernt werden.
 */
public class PathConnectionPanel extends AbstractPathConnectionPanel {

    protected final LGMDragNDropTree ltree;
    protected final LGMDragNDropTree rtree;
    private final DefaultTreeModel model, abmodel;
    protected final LGMTreeNode root, abroot;
    private final JLabel rtreeLabel;
    private final JScrollPane sp2;
    private final JPanel buttonpanel;
    private final boolean showRightTree;

    private LGMAction addAction;
    private LGMAction removeAction;
    private LGMAction newElementAction;

    public PathConnectionPanel(final ElementPropertyDialog dialog, final boolean showRightTree, final Class<? extends Kante>... edgeClasses) {
        this(dialog, false, showRightTree, edgeClasses);
    }

    public PathConnectionPanel(final ElementPropertyDialog dialog, final boolean showRightTree, final Class<? extends ModelElement> searchElementClass, final Class<? extends Kante>... edgeClasses) {
        this(dialog, false, showRightTree, searchElementClass, edgeClasses);
    }

    public PathConnectionPanel(final ElementPropertyDialog dialog, final boolean labelLastEdgeName, final boolean showRightTree, final Class<? extends Kante>... edgeClasses) {
        this(dialog, labelLastEdgeName, showRightTree, null, edgeClasses);
    }

    private PathConnectionPanel(final ElementPropertyDialog dialog, final boolean labelLastEdgeName, final boolean showRightTree, final Class<? extends ModelElement> searchElementClass, final Class<? extends Kante>... edgeClasses) {
        super(dialog, labelLastEdgeName, searchElementClass, edgeClasses);
        this.showRightTree = showRightTree;
        setPreferredSize(new Dimension(550, 350));
        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
        GridBagConstraints constraints = new GridBagConstraints();

        //wenn der Pfad aus mehr als einer Kante besteht, dann soll über dem linken Baum einfach "verbunden" stehen
        String ltreeLabelString = lastEdgeIndex > 0 ? getResString("verb") : null;
        //wenn der Pfad aus nur einer Kante besteht
        if (ltreeLabelString == null) {
            //schreibe den Namen der Kante in der richtigen Richtung über den linken Baum
            Class<? extends Kante> lastEdge = edgeClasses[lastEdgeIndex];
            ltreeLabelString = directions[lastEdgeIndex] == FORWARD ? ModelConstants.getForwardMetaAssociationName(lastEdge) : ModelConstants.getBackwardMetaAssociationName(lastEdge);
        }
        String rtreeLabelString = getResString("frei");
        Pair<String, String> treeLabels = StringUtils.makeSameLength(ltreeLabelString, rtreeLabelString);
        ltreeLabelString = treeLabels.getFirstItem();
        rtreeLabelString = treeLabels.getSecondItem();

        westLabel.setText(ltreeLabelString);
        JLabel ltreeLabel = westLabel;
        root = new LGMTreeNode(getModelElement().getContainer(mainDoc), false);
        model = new DefaultTreeModel(root);
        ltree = new LGMDragNDropTree(model, mainDoc);
        ltree.setRootVisible(false);
        ltree.setShowsRootHandles(true);
        ltree.setCellRenderer(treeRenderer);
        ltree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        JScrollPane sp = new JScrollPane(ltree);

        if (showRightTree) {
            constraints.anchor = GridBagConstraints.EAST;
            constraints.ipadx = -30;
            constraints.ipady = -10;
            add(this, viewButton, constraints, 0, 5, 1, 1);
        }
        constraints.ipadx = 0;
        constraints.ipady = 0;
        constraints.anchor = GridBagConstraints.WEST;
        add(this, ltreeLabel, constraints, 0, 0, 1, 1);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 100;
        constraints.weighty = 100;
        add(this, sp, constraints, 0, 1, 1, 4);

        rtreeLabel = new JLabel(rtreeLabelString);
        abroot = new LGMTreeNode(rtreeLabelString, false);
        abmodel = new DefaultTreeModel(abroot);
        rtree = new LGMDragNDropTree(abmodel, mainDoc);
        rtree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        rtree.setRootVisible(false);
        rtree.setShowsRootHandles(true);
        rtree.setCellRenderer(treeRenderer);
        sp2 = new JScrollPane(rtree);

        /*
         * Start: MouseListener erstellen und an Trees anhängen ...
         */
        LGMAction ltreeMouseAction = LGMActionLibrary.getMouseAction(ltree, this);
        LGMAction rtreeMouseAction = LGMActionLibrary.getMouseAction(rtree, this);

        ltree.addMouseListener(new LGMMouseListener(null, null, null, ltreeMouseAction, null));
        rtree.addMouseListener(new LGMMouseListener(null, null, null, rtreeMouseAction, null));
        /*
         * ... End: MouseListener erstellen und an Trees anhängen
         */

        /*
         * Start: TreeSelectionListener erstellen und an Trees anhängen ...
         */
        LGMAction ltreeSelectionAction = LGMActionLibrary.getTreeSelectionAction(ltree, this);
        LGMAction rtreeSelectionAction = LGMActionLibrary.getTreeSelectionAction(rtree, this);

        ltree.addTreeSelectionListener(new LGMTreeSelectionListener(ltreeSelectionAction));
        rtree.addTreeSelectionListener(new LGMTreeSelectionListener(rtreeSelectionAction));
        /*
         * ... End: TreeSelectionListener erstellen und an Trees anhängen
         */

        /*
         * Start: Buttons & Actions erstellen, Actions setzen ...
         */

        try {
            addAction = getConnectAction();
            removeAction = getDisconnectAction();
            newElementAction = getNewConnectedElementAction();
        } catch (ActionNotDefinedForClassException e) {
            Log.log(Log.DEBUG, e.getMessage());
        }

        /*
         * ... end: Buttons & Actions erstellen, Actions setzen
         */

        buttonpanel = new JPanel();
        buttonpanel.setSize(30, 250);
        buttonpanel.setLayout(new GridLayout(3, 1));
        buttonpanel.add(new JButton(addAction));
        buttonpanel.add(new JButton(removeAction));
        if (newElementAction != null) {
            buttonpanel.add(new JButton(newElementAction));
        }

        init();
    }

    @Override
    public void init() {
        super.init();
        remove(buttonpanel);
        remove(rtreeLabel);
        remove(sp2);
        buildLeftTree();
        revalidate();
        repaint();
    }

    @Override
    public void showFullDialog() {
        if (showRightTree) {
            super.showFullDialog();
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.fill = GridBagConstraints.NONE;
            add(this, buttonpanel, constraints, 1, 3, 1, 2);
            constraints.anchor = GridBagConstraints.WEST;
            add(this, rtreeLabel, constraints, 2, 0, 1, 1);
            constraints.anchor = GridBagConstraints.WEST;
            constraints.fill = GridBagConstraints.BOTH;
            constraints.weightx = 100;
            constraints.weighty = 100;
            add(this, sp2, constraints, 2, 1, 1, 4);

            buildRightTree();

            revalidate();
            repaint();
        }
    }

    /**
     * Set aller TreeNodes, die im linken Baum bereits verknüpft sind und im rechten nicht mehr auftauchen sollen
     */
    private final Collection<ElementContainer> childrenToExcludeFromRtree = Sets.newHashSet();

    /**
     * Sammelt für die letzte Kante alle Elemente ein, die im rechten Baum nicht mehr angezeigt werden sollen, weil
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
            //wenn es der Index der letzten Kante ist -> zu den Ausschlusselementen hinzufügen
            if (edgeIndex == lastEdgeIndex) {
                childrenToExcludeFromRtree.addAll(potentialExcludeChildren);
            }
        }
    }

    private void buildLeftTree() {
        ltree.saveExpansion();
        ltree.saveSelection();
        root.removeAllChildren();
        ltree.reset();
        buildTree();
        model.reload();
        ltree.restoreExpansion();
        ltree.restoreSelection();
    }

    /**
     * Baut im linken Baum den gesamten Pfad auf
     */
    protected Collection<LGMTreeNode> buildTree() {
        int edgeIndex = 0;
        Class<? extends ModelElement> pathStepEndClass = getPathStepEndElementClass(edgeIndex);
        ModelElement me = getModelElement();
        List<ElementContainer> all = me.getConnectedContainer(pathStepEndClass, mainDoc);
        addChildrenToExcludeFromRtree(edgeIndex, all, true);
        // nur Knoten für Elemente in der all-Liste bis zur Größe der direkt verbundenen dürfen am Ende selektierbar sein
        int firstNonSelectableIndex = all.size();
        if (UserProperties.isSearchParts()) {
            all.addAll(me.getPartConnectedContainer(pathStepEndClass, mainDoc));
        }
        if (UserProperties.isSearchParents()) {
            all.addAll(me.getParentConnectedContainer(pathStepEndClass, mainDoc));
        }
        ImmutableList.Builder<LGMTreeNode> leafs = ImmutableList.builder();
        List<LGMTreeNode> firstLevelNodes = Lists.newArrayListWithCapacity(all.size());
        for (ElementContainer ec : all) {
            LGMTreeNode node = ltree.addObject(ec, root, null, true, false, false);
            firstLevelNodes.add(node);
        }
        List<LGMTreeNode> nextStepStartNodes = firstLevelNodes;
        for (edgeIndex = 1; edgeIndex < edgeClasses.length; edgeIndex++) {
            pathStepEndClass = getPathStepEndElementClass(edgeIndex);
            List<LGMTreeNode> newNextStartNodes = Lists.newArrayList();
            for (LGMTreeNode node : nextStepStartNodes) {
                ElementContainer nodeElementContainer = (ElementContainer) node.getUserObject();
                me = nodeElementContainer.getElement();
                List<ElementContainer> connected = me.getConnectedContainer(pathStepEndClass, mainDoc);
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
        abroot.removeAllChildren();
        rtree.reset();
        for (ElementContainer ec : mainDoc.getElementContainer(searchElementClass, true, true)) {
            rtree.addObject(ec, abroot, childrenToExcludeFromRtree, false, true);
        }
        abmodel.reload();
        rtree.restoreExpansion();
        rtree.restoreSelection();
    }

    @Override
    protected final DragNDropActionChain[] collectDragNDropActionChains() {
        DragNDropActionChain dndAC1 = DragNDropInitializer.createNewDragNDropActionChain(rtree, ltree, addAction);
        DragNDropActionChain dndAC2 = DragNDropInitializer.createNewDragNDropActionChain(ltree, rtree, removeAction);

        return new DragNDropActionChain[] {
                dndAC1,
                dndAC2
        };

    }

    @Override
    public final LGMDragNDropTree[] getAllDragNDropTrees() {
        return new LGMDragNDropTree[] {
                rtree,
                ltree
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

    protected LGMAction getConnectAction() throws ActionNotDefinedForClassException {
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

                //TODO: das hier expandiert das neue überhaupt nicht, sondern nur bis zum vorher schon geöffneten Knoten. Das ist doof!
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
                    //das ist der Index der Kante im Pfad, ab der entfernt werden soll
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
        Class<? extends Kante> edgeClass = edgeClasses[edgeIndexInPath];
        gdcoll.unlink(startInPath, endInPath, edgeClass, pid);
        if (!startInPath.isConsistent()) {
            gdcoll.deleteElement(startInPath, selDoc, pid);
        }
        int nextEdgeIndexInPath = edgeIndexInPath + 1;
        if (nextEdgeIndexInPath < edgeClasses.length) {
            Class<? extends Kante> nextEdgeClass = edgeClasses[nextEdgeIndexInPath];
            Class<? extends ModelElement> nextElementClassInPath = directions[nextEdgeIndexInPath] == FORWARD ? Kante.getEndClass(nextEdgeClass) : Kante.getStartClass(nextEdgeClass);
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

        //das ist der Index der Kante im Pfad, ab der hinzugefügt werden soll
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
        return new LGMAction(Tool3lgmConstants.getResString("new")) {
            @Override
            public void execute(final EventObject eo) {
                //wenn eindutig fest steht, an welchen Knoten ein neues Element gehängt werden sollte, dann wird
                //es auch gleich angehängt
                if (isConnectionPointUnique) {
                    connectToFirstPath(null);
                } else { //es ist nicht klar, wohin ein neues Element gehängt werden sollte -> nur neu erzeugen und nicht verknüpfen
                    createNodeWithContainerAndDependents(doc.getCollection().getSelectedDoc(), null, edgeClasses[lastEdgeIndex], directions[lastEdgeIndex], null, FORWARD, getTransactionID());
                }
            }
        };
    }

}