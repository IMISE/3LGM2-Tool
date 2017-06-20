package de.imise.tool3lgm.graphtools.dialog.panel;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.EventObject;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.action.LGMActionLibrary;
import de.imise.tool3lgm.graphtools.dialog.action.LGMMouseListener;
import de.imise.tool3lgm.graphtools.dialog.action.LGMTreeSelectionListener;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.DragNDropInitializer;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.DragNDropInitializer.DragNDropActionChain;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.edge.AwbKommssVerbindung;
import de.imise.tool3lgm.graphtools.elements.node.Anwendungsbaustein;
import de.imise.tool3lgm.graphtools.elements.node.Schnittstelle;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.tools.LGMTree;
import de.imise.tool3lgm.tools.LGMTreeNode;
import de.imise.tool3lgm.userproperties.UserProperties;

public class NConnectionPanel extends AbstractExpandablePanel {

    private final LGMTree ltree;

    private final LGMTree rtree;

    private final DefaultTreeModel lmodel, rmodel;
    private final LGMTreeNode lroot, rroot;
    private final boolean mw;
    private final JLabel rtreeLabel;
    private final JScrollPane rtreeScollPane;
    private final JPanel buttonpanel;
    private final Class<? extends ModelElement> searchElementClass;

    private LGMAction addAction;
    private LGMAction removeAction;
    private LGMAction newElementAction;

    /**
     * @param searchElementClass
     * @param dl
     * @param mitnew
     * @param editable
     */
    public NConnectionPanel(final Class<? extends ModelElement> searchElementClass, final ElementPropertyDialog dl, final boolean mitnew, final boolean editable) {
        this(searchElementClass, null, dl, mitnew, editable);
    }

    /**
     * @param searchElementClass
     * @param name
     * @param dl
     * @param mitnew
     * @param editable
     */
    public NConnectionPanel(final Class<? extends ModelElement> searchElementClass, final String name, final ElementPropertyDialog dl, final boolean mitnew, final boolean editable) {
        super(dl, false, searchElementClass, ModelConstants.getEdgeTypes(dl.getModelElement().getClass(), searchElementClass)[0]);
        this.searchElementClass = searchElementClass;
        // bei abstracten Klassen darf grundsätzlich kein Neu-Knopf angeboten werden
        mw = Modifier.isAbstract(searchElementClass.getModifiers()) ? false : mitnew;

        // setPreferredSize(new Dimension(550,350));
        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
        GridBagConstraints constraints = new GridBagConstraints();

        JLabel ltreeLabel = new JLabel(getResString("verb"));
        lroot = new LGMTreeNode(getResString("verb"), false);
        lmodel = new DefaultTreeModel(lroot);

        // FST: geändert!
        ltree = new LGMTree(lmodel, mainDoc);
        ltree.setName("ltree");

        ltree.setRootVisible(false);
        ltree.setShowsRootHandles(true);
        ltree.setCellRenderer(treeRenderer);
        ltree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        JScrollPane ltreeScollPane = new JScrollPane(ltree);

        //View-Button zum Auf- und Zuklappen des rechten Baumes
        constraints.anchor = GridBagConstraints.EAST;
        constraints.weightx = 0d;
        constraints.weighty = 0d;
        constraints.ipadx = -30;
        constraints.ipady = -10;
        if (editable) {
            add(this, viewButton, constraints, 0, 2, 1, 1);
        }

        //linker Baum direkt über dem ViewButton
        constraints.ipadx = 0;
        constraints.ipady = 0;
        constraints.anchor = GridBagConstraints.WEST;
        add(this, ltreeLabel, constraints, 0, 0, 1, 1);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 100;
        constraints.weighty = 100;
        add(this, ltreeScollPane, constraints, 0, 1, 1, 1);

        rtreeLabel = new JLabel(getResString("frei"));
        rroot = new LGMTreeNode(getResString("frei"), false);
        rmodel = new DefaultTreeModel(rroot);

        // FST: geändert!
        rtree = new LGMTree(rmodel, mainDoc);
        rtree.setName("rtree");
        rtree.setRootVisible(false);
        rtree.setShowsRootHandles(true);
        rtree.setCellRenderer(treeRenderer);
        rtree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        rtreeScollPane = new JScrollPane(rtree);

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
         * Start: Buttons & Actions erstellen und registrieren ...
         */
        JButton addButton = new JButton();
        JButton removeButton = new JButton();
        JButton newElementButton = new JButton();

        try {
            addAction = getConnectAction(rtree, ltree);
            removeAction = getDisconnectAction(ltree, rtree);
            newElementAction = getNewElementAction();
        } catch (Exception e) {
            e.printStackTrace();
        }
        addButton.setAction(addAction);
        removeButton.setAction(removeAction);
        newElementButton.setAction(newElementAction);
        /*
         * ... end: Buttons & Actions erstellen und registrieren
         */

        buttonpanel = new JPanel();
        buttonpanel.setSize(30, 250);
        buttonpanel.setLayout(new GridLayout(3, 1));

        buttonpanel.add(addButton);
        buttonpanel.add(removeButton);

        if (mw) {
            buttonpanel.add(newElementButton);
        }
        update();
    }

    ArrayList<ElementContainer> childrenToExcludeFromRtree = new ArrayList<ElementContainer>(5000);

    @Override
    public void update() {
        childrenToExcludeFromRtree.clear();
        lroot.removeAllChildren();
        ltree.reset();

        ModelElement modelElement = getModelElement();
        ArrayList<ElementContainer> all = modelElement.getConnectedContainer(searchElementClass, mainDoc);
        for (ElementContainer ec : all) {
            childrenToExcludeFromRtree.add(ec);
            ltree.addObject(ec, lroot, null, true, false);
        }
        if (UserProperties.isSearchParts()) {
            all = modelElement.getPartConnectedContainer(searchElementClass, mainDoc);
            for (ElementContainer ec : all) {
                childrenToExcludeFromRtree.add(ec);
                LGMTreeNode node = ltree.addObject(ec, lroot, null, true, false);
                if (node != null) {
                    node.setSelectable(false);
                }
            }
        }
        if (UserProperties.isSearchParents()) {
            all = modelElement.getParentConnectedContainer(searchElementClass, mainDoc);
            for (ElementContainer ec : all) {
                childrenToExcludeFromRtree.add(ec);
                LGMTreeNode node = ltree.addObject(ec, lroot, null, true, false);
                if (node != null) {
                    node.setSelectable(false);
                }
            }
        }
        lmodel.reload();
        // expandTree(ltree);

        if (isRightSideVisible()) {
            buildRightTree();
        }
        revalidate();
        repaint();
    }

    //
    // public static class ConnectionAction extends AbstractAction{
    //
    // private ElementDialogPanel dialogPanel;
    //
    // private JTree selectionSource;
    //
    // public ConnectionAction(ElementDialogPanel dialogPanel, JTree selectionSource) {
    // super("", Tool3lgmConstants.getIcon("arrow_left2.gif"));
    // this.dialogPanel = dialogPanel;
    // this.selectionSource = selectionSource;
    // }
    // public void actionPerformed(ActionEvent e) {
    // GraphDocument doc = dialogPanel.doc;
    // int id = dialogPanel.dialog.getID();
    //
    //
    // doc.start_transaction(id);
    // TreePath[] selpaths = selectionSource.getSelectionPaths();
    // if (selpaths != null) {
    // for (int n = 0; n < selpaths.length; n++) {
    // LGMTreeNode node = (LGMTreeNode) selpaths[n].getLastPathComponent();
    // NodeContainer knot = ((NodeContainer) node.getUserObject());
    // doc.link(dialogPanel.object, knot.getElement(), id);
    // }
    // }
    // doc.finish_transaction(id);
    // doc.distributeEvent(GraphDocument.DATA_CHANGED, null, null, id);
    //
    // }
    //
    //
    // private ActionEvent createActionEvent() {
    //
    // ActionEvent ae = new ActionEvent(this,
    // ActionEvent.ACTION_PERFORMED,
    // ACTION_COMMAND_KEY);
    // return ae;
    // }
    //
    //
    // }

    @Override
    protected DragNDropActionChain[] collectDragNDropActionChains() {
        DragNDropActionChain tac1 = DragNDropInitializer.createNewDragNDropActionChain(rtree, ltree, addAction);
        DragNDropActionChain tac2 = DragNDropInitializer.createNewDragNDropActionChain(ltree, rtree, removeAction);

        return new DragNDropActionChain[] {
                tac1,
                tac2
        };
    }

    @Override
    public LGMTree[] getAllDragNDropTrees() {
        return new LGMTree[] {
                rtree,
                ltree
        };
    }

    /**
     * @return
     */
    public Class<? extends ModelElement> getElementClass() {
        return searchElementClass;
    }

    private void buildRightTree() {
        rroot.removeAllChildren();
        rtree.reset();

        ArrayList<ElementContainer> all = mainDoc.getElementContainer(searchElementClass);
        for (ElementContainer ec : all) {
            // if (((Knoten) modelElement).isConnectedWith(ec.getKnoten())) {
            // childrenToExcludeFromRtree.add(ec);
            // }
            rtree.addObject(ec, rroot, childrenToExcludeFromRtree, false, true);
        }
        rmodel.reload();
    }

    @Override
    protected void showFullDialog() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        add(this, buttonpanel, constraints, 1, 1, 1, 1);
        constraints.anchor = GridBagConstraints.WEST;
        add(this, rtreeLabel, constraints, 2, 0, 1, 1);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 100;
        add(this, rtreeScollPane, constraints, 2, 1, 1, 1);
    }

    @Override
    protected void showPartlyDialog() {
        remove(buttonpanel);
        remove(rtreeLabel);
        remove(rtreeScollPane);
    }

    /**
     * Methode liefert eine <code>LGMAction</code> zurück, die das Verschieben von Elementen aus dem
     * <code>srcTree</code> in den <code>targetTree</code> realisiert. Diese <code>LGMAction</code>
     * sollte an die "addButtons" der Panels angefügt werden.
     *
     * @param srcTree
     * @param targetTree
     */
    protected final LGMAction getConnectAction(final JTree srcTree, final JTree targetTree) {
        final GraphDocument doc = getGraphDocument();
        final GDCollection gdcoll = doc.getCollection();
        final int pid = getTransactionID();
        return new LGMAction("", Tool3lgmConstants.getIcon("arrow_left2.gif")) {
            @Override
            public void execute(final EventObject e) {
                TreePath[] selpaths = srcTree.getSelectionPaths();
                if (selpaths != null) {
                    for (int n = 0; n < selpaths.length; n++) {
                        LGMTreeNode node = (LGMTreeNode) selpaths[n].getLastPathComponent();
                        ElementContainer ec = (ElementContainer) node.getUserObject();
                        ModelElement me = ec.getElement();
                        ModelElement topLevelMe = getTopLevelModelElement(targetTree);
                        gdcoll.link(topLevelMe, me, pid);
                    }
                }
            }
        };
    }

    /**
     * Methode liefert eine <code>LGMAction</code> zurück, die das Verschieben von Elementen aus dem
     * <code>srcTree</code> in den <code>targetTree</code> realisiert. Diese <code>LGMAction</code>
     * sollte an die "removeButtons" der Panels angefügt werden.
     *
     * @param srcTree
     * @param targetTree
     */
    protected final LGMAction getDisconnectAction(final JTree srcTree, final JTree targetTree) {
        final GraphDocument doc = getGraphDocument();
        final GDCollection gdcoll = doc.getCollection();
        final int pid = getTransactionID();
        return new LGMAction("", Tool3lgmConstants.getIcon("arrow_right2.gif")) {
            @Override
            public void execute(final EventObject e) {
                TreePath[] selpaths = srcTree.getSelectionPaths();
                if (selpaths != null) {
                    for (int n = 0; n < selpaths.length; n++) {
                        LGMTreeNode node = (LGMTreeNode) selpaths[n].getLastPathComponent();
                        ElementContainer ec = (ElementContainer) node.getUserObject();
                        ModelElement me = ec.getElement();
                        ModelElement topLevelModelElement = getTopLevelModelElement(targetTree == null ? srcTree : targetTree);
                        gdcoll.unlink(topLevelModelElement, me, pid);
                    }
                }
            }
        };
    }

    /**
     * Methode liefert eine <code>LGMAction</code> zurück, die das Erzeugen eines neuen Elements
     * realisiert.
     */
    public final LGMAction getNewElementAction() {

        final GraphDocument doc = getGraphDocument();
        final GDCollection gdcoll = doc.getCollection();
        final int pid = getTransactionID();
        final ModelElement me = getModelElement();

        return new LGMAction(Tool3lgmConstants.getResString("new")) {
            @Override
            public void execute(final EventObject eo) {
                // doc.start_transaction(dialog.getTransactionID());
                Knoten k = null;
                if (me instanceof Anwendungsbaustein && Schnittstelle.class.isAssignableFrom(searchElementClass)) {
                    doc.select(me.getContainer(doc), pid);
                    GraphDocument.createAddicted(doc.getCollection().getSelectedDoc(), me, AwbKommssVerbindung.class, searchElementClass, pid);
                    if (doc.getLastCreated() != null) {
                        if (searchElementClass.isAssignableFrom(doc.getLastCreated().getElement().getClass())) {
                            k = (Knoten) doc.getLastCreated().getElement();
                        } else {
                            System.out.println("Was ist mit der Selektion los????");
                        }
                    }
                } else {
                    doc.createKnotenWithContainer(searchElementClass, pid);
                    if (doc.getLastCreated() != null) {
                        k = (Knoten) doc.getLastCreated().getElement();
                        gdcoll.link(getEdgeType(me, k), me, k, pid);
                    }
                }
                // doc.finish_transaction(dialog.getTransactionID());
                // doc.distributeEvent(GraphDocument.DATA_CHANGED, null,
                // null, dialog.getTransactionID());
                return;
            }
        };
    }

}
