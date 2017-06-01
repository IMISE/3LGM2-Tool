package de.imise.tool3lgm.graphtools.dialog.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.EventObject;
import java.util.List;

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
import de.imise.tool3lgm.graphtools.dialog.action.ActionNotDefinedForClassException;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.action.LGMActionLibrary;
import de.imise.tool3lgm.graphtools.dialog.action.LGMMouseListener;
import de.imise.tool3lgm.graphtools.dialog.action.LGMTreeSelectionListener;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.edge.AwbKommssVerbindung;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.tools.LGMTreeNode;
import de.imise.tool3lgm.userproperties.UserProperties;

/**
 * Das Panel für die Bausteinschnittstellen
 */
public class MutipleCompositionPanel extends ElementDialogPanel {

    /**
     * COMMENTME
     */
    private final JTree tree;

    /**
     * COMMENTME
     */
    private final DefaultTreeModel lmodel;

    /**
     * COMMENTME
     */
    private final LGMTreeNode lroot;

    /**
     * COMMENTME
     */
    private JPanel workingpanel;

    private final JPanel buttonpanel;

    /**
     * COMMENTME
     */
    private final Class<? extends ModelElement> searchElementClass;

    public Class<? extends ModelElement> getSearchElementClass() {
        return searchElementClass;
    }

    /**
     * COMMENTME
     */
    private LGMAction addAction;

    /**
     * COMMENTME
     */
    private LGMAction removeAction;

    /**
     * @param searchElementClass
     * @param dl
     */
    public MutipleCompositionPanel(final Class<? extends ModelElement> searchElementClass, final ElementPropertyDialog dl) {
        super(dl);

        this.searchElementClass = searchElementClass;

        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
        GridBagConstraints constraints = new GridBagConstraints();

        JLabel label = new JLabel(Tool3lgmConstants.getResString("verb"));
        lroot = new LGMTreeNode(Tool3lgmConstants.getResString("verb"), false);
        lmodel = new DefaultTreeModel(lroot);
        tree = new JTree(lmodel);
        tree.setRootVisible(false);
        tree.setCellRenderer(treeRenderer);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        // MouseListener erstellen und an tree anhängen
        LGMAction treeMouseAction = LGMActionLibrary.getMouseAction(tree, this);
        tree.addMouseListener(new LGMMouseListener(null, null, null, treeMouseAction, null));

        // TreeSelectionListener erstellen und an tree anhängen
        LGMAction treeSelectionAction = LGMActionLibrary.getTreeSelectionAction(tree, this);
        tree.addTreeSelectionListener(new LGMTreeSelectionListener(treeSelectionAction));

        /*
         * Start: Buttons & Actions erstellen und registrieren ...
         */
        JButton addButton = new JButton();
        JButton removeButton = new JButton();

        try {
            addAction = getAddElementAction(null, null, this, false);
            removeAction = getDisconnectAction(tree, null, this, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        addButton.setAction(addAction);
        removeButton.setAction(removeAction);
        /*
         * ... end: Buttons & Actions erstellen und registrieren
         */

        JScrollPane sp = new JScrollPane(tree);

        constraints.anchor = GridBagConstraints.CENTER;
        buttonpanel = new JPanel();
        buttonpanel.setLayout(new GridLayout(1, 2));
        buttonpanel.add(removeButton);
        buttonpanel.add(addButton);
        add(this, buttonpanel, constraints, 0, 2, 3, 1);

        // add(this, viewButton, constraints, 2, 3, 1, 1);
        constraints.ipadx = 0;
        constraints.ipady = 0;
        constraints.anchor = GridBagConstraints.WEST;
        add(this, label, constraints, 0, 0, 1, 1);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 100;
        constraints.weighty = 100;
        add(this, sp, constraints, 0, 1, 3, 1);

        init();
    }

    @Override
    protected void init() {
        super.init();
        // remove(workingpanel);
        lroot.removeAllChildren();
        ModelElement modelElement = getModelElement();
        List<ElementContainer> all = modelElement.getConnectedContainer(searchElementClass, mainDoc);
        for (int m = 0; m < all.size(); m++) {
            LGMTreeNode node = new LGMTreeNode(all.get(m), false);
            lroot.add(node);
        }
        if (UserProperties.isSearchParts()) {
            all = ((Knoten) modelElement).getPartConnectedContainer(searchElementClass, mainDoc);
            for (int m = 0; m < all.size(); m++) {
                LGMTreeNode node = new LGMTreeNode(all.get(m), false);
                node.setSelectable(false);
                lroot.add(node);
            }
        }
        if (UserProperties.isSearchParents()) {
            all = ((Knoten) modelElement).getParentConnectedContainer(searchElementClass, mainDoc);
            for (int m = 0; m < all.size(); m++) {
                LGMTreeNode node = new LGMTreeNode(all.get(m), false);
                node.setSelectable(false);
                lroot.add(node);
            }
        }
        lmodel.reload();
        expandTree(tree);
        revalidate();
        repaint();
    }

    @Override
    protected void showFullDialog() {
        if (true) {
            return;
        }
        super.showFullDialog();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 100;
        constraints.weighty = 100;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        add(this, workingpanel, constraints, 1, 3, 1, 3);

        revalidate();
        repaint();
    }

    /**
     * Methode liefert eine <code>LGMAction</code> zurück, die das Verschieben von Elementen aus dem
     * <code>srcTree</code> in den <code>targetTree</code> realisiert. Diese <code>LGMAction</code>
     * sollte an die "addButtons" der Panels angefügt werden.
     *
     * @param srcTree
     * @param targetTree
     * @param edp
     * @param switchTree
     * @throws ActionNotDefinedForClassException
     */
    public static final LGMAction getAddElementAction(final JTree srcTree, final JTree targetTree, final ElementDialogPanel edp, final boolean switchTree) throws ActionNotDefinedForClassException {
        final GraphDocument doc = edp.getGraphDocument();
        final ElementPropertyDialog dialog = edp.getDialog();
        final ModelElement modelElement = edp.getModelElement();

        return new LGMAction(Tool3lgmConstants.getResString("addButtonText")) {
            @Override
            public void execute(final EventObject eo) {
                doc.select(modelElement.getContainer(doc.getCollection().getMainGraphDocument()), dialog.getTransactionID());
                if (edp instanceof MutipleCompositionPanel) {
                    GraphDocument.createAddicted(doc.getCollection().getSelectedDoc(), modelElement, AwbKommssVerbindung.class, ((MutipleCompositionPanel) edp).getSearchElementClass(), dialog.getTransactionID());
                }
                doc.select(modelElement.getContainer(doc.getCollection().getMainGraphDocument()), dialog.getTransactionID());
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
     * @param edp
     * @param switchTree
     * @throws ActionNotDefinedForClassException
     */
    public static final LGMAction getDisconnectAction(final JTree srcTree, final JTree targetTree, final ElementDialogPanel edp, final boolean switchTree) throws ActionNotDefinedForClassException {

        final boolean switchIt = switchTree;
        final JTree tree1 = srcTree;
        final JTree tree2 = targetTree;
        final GDCollection gdcoll = edp.getGraphDocument().getCollection();
        final ElementPropertyDialog dialog = edp.getDialog();

        LGMAction returnAction = new LGMAction("", Tool3lgmConstants.getIcon("arrow_right2.gif")) {

            @Override
            public void execute(final EventObject e) {
                TreePath[] selpaths = tree1.getSelectionPaths();
                if (selpaths != null) {
                    for (int n = 0; n < selpaths.length; n++) {
                        // if(lomodel.getChildCount(loroot)>0) return;
                        LGMTreeNode node = (LGMTreeNode) selpaths[n].getLastPathComponent();
                        ElementContainer knot = (ElementContainer) node.getUserObject();

                        ModelElement topLevelModelElement;
                        if (tree2 == null) {
                            topLevelModelElement = getTopLevelModelElement(tree1);
                        } else {
                            topLevelModelElement = getTopLevelModelElement(tree2);
                        }

                        if (switchIt == true) {
                            gdcoll.unlink(knot.getElement(), topLevelModelElement, dialog.getTransactionID());
                        } else {
                            gdcoll.unlink(topLevelModelElement, knot.getElement(), dialog.getTransactionID());
                        }
                    }
                }
            }
        };
        returnAction.putValue("Name", Tool3lgmConstants.getResString("delete"));
        returnAction.putValue("SmallIcon", null);

        return returnAction;
    }

    /**
     * Gibt das <code>ModelElement</code> des <code>ElementPropertyDialog</code> s wieder, in dem
     * sich der <code>tree</code> befindet.
     *
     * @param tree TODO: diese Funktion hat jetzt das {@link LGMDragNDropPanel}, so dass das hier
     *            irgendwann mal weg kann
     */
    private static ModelElement getTopLevelModelElement(final JTree tree) {
        ModelElement me = null;
        try {
            ElementPropertyDialog d = (ElementPropertyDialog) tree.getTopLevelAncestor();
            me = d.getModelElement();
        } catch (Exception ex) {
            Log.log(Log.ERROR, "LGMActionLibary: could'nt find TopLevelAncestor for tree", ex);
        }
        return me;
    }

}
