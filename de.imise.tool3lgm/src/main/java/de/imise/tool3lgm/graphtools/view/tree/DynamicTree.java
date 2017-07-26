package de.imise.tool3lgm.graphtools.view.tree;

import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.GraphDocumentListener;
import de.imise.tool3lgm.graphtools.GraphDocumentOwner;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.Textfeld;
import de.imise.tool3lgm.graphtools.elements.node.Anwendungsbaustein;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.event.UserFieldListener;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.tools.LGMTreeNode;
import de.imise.tool3lgm.userproperties.UserProperties;

/**
 * @author N.N.
 */
public final class DynamicTree extends JTree implements UserFieldListener, GraphDocumentOwner {

    /**
     * Knoten für die Fachliche Ebene
     */
    private final LGMTreeNode fachebene = new LGMTreeNode(Tool3lgmConstants.getResString("domain_layer"), false, true);
    /**
     * Knoten für die Logische Werkzeugebene
     */
    private final LGMTreeNode logebene = new LGMTreeNode(Tool3lgmConstants.getResString("logical_tool_layer"), false, true);
    /**
     * Knoten für die physische Werkzeugebene
     */
    private final LGMTreeNode phyebene = new LGMTreeNode(Tool3lgmConstants.getResString("physical_tool_layer"), false, true);
    /**
     * COMMENTME
     */
    private final LGMTreeNode awb = new LGMTreeNode(Tool3lgmConstants.getResString("Anwendungsbaustein_p"), false, true);;
    /**
     * COMMENTME
     */
    private LGMTreeNode textFieldDomainLayer = null;
    /**
     * COMMENTME
     */
    private LGMTreeNode textFieldLogicalLayer = null;
    /**
     * COMMENTME
     */
    private LGMTreeNode textFieldPhysicalLayer = null;

    /**
     * COMMENTME
     */
    private GraphDocument doc;

    private final DynamicTreeSelectionListener selectionListener;

    private final DynamicTreeGraphDocumentAndInTransactionListener transactionListener;

    /**
     * Alle Knoten deren Kinder immer wieder removed und neu angelegt werden.
     */
    private final LGMTreeNode[] nodesToClear = new LGMTreeNode[ModelConstants.TREE_DOMAIN_LAYER_NODES.length + ModelConstants.TREE_LOGICAL_LAYER_NODES.length + ModelConstants.TREE_PHYSICAL_LAYER_NODES.length];

    /**
     * Transaktions-ID, mit der der Baum alle seine Änderungen vornimmt.
     */
    public static final int PID = TransactionManager.STANDARD_PID;

    /**
     * COMMENTME
     */
    private final DefaultTreeModel myModel;
    /**
     * COMMENTME
     */
    private final TreePath rootPath;

    /**
     * @param d
     */
    public DynamicTree(final GraphDocument d) {
        super(new DefaultTreeModel(new LGMTreeNode(Tool3lgmConstants.getResString("browser"), false, false)));

        rootPath = new TreePath(((DefaultTreeModel) getModel()).getPathToRoot((LGMTreeNode) getModel().getRoot()));
        doc = d;
        DynamicTreeMouseAdapter.addAdapter(this);
        selectionListener = new DynamicTreeSelectionListener(this);
        transactionListener = new DynamicTreeGraphDocumentAndInTransactionListener(this);

        setCellRenderer(new TreeRenderer(doc));
        ((TreeRenderer) getCellRenderer()).setBackgroundNonSelectionColor(getBackground());
        setBackground(getBackground());
        setEditable(false);
        putClientProperty("JTree.lineStyle", "Angled");
        setToggleClickCount(-1);

        setRootVisible(false);
        setShowsRootHandles(true);
        buildTree();
        myModel = (DefaultTreeModel) getModel();
    }

    /**
     * @return
     */
    @Override
    public final GraphDocument getGraphDocument() {
        return doc;
    }

    /**
     * @return
     */
    @Override
    public final GDCollection getCollection() {
        return getGraphDocument().getCollection();
    }

    /**
     * Setzt das übergebene {@link GraphDocument} für diesen Baum und fügt den Baum als {@link GraphDocumentListener} hinzu. Beim vorherigen
     * {@link GraphDocument} des Baumes wird der Baum als Listener entfernt.
     *
     * @param doc
     */
    public void setGraphDocument(final GraphDocument doc) {
        if (this.doc != null) {
            transactionListener.remove();
            this.doc = doc;
        }
        if (doc != null) {
            transactionListener.add();
        }
        buildTree();
    }

    public boolean isLayerNode(final Object o) {
        return o == fachebene || o == logebene || o == phyebene;
    }

    public boolean isAbstractElementNode(final Object o) {
        return o == awb;
    }

    public void setTransactionListenerActive(final boolean active) {
        transactionListener.setActive(active);
    }

    /**
     * Der allgemeine Baum wird erzeugt oder zurueckgesetzt
     */
    private void createTree() {
        LGMTreeNode top = (LGMTreeNode) treeModel.getRoot();
        if (top.getChildCount() == 0) {
            fachebene.removeAllChildren();
            top.add(fachebene);

            int nodesToClearIndex = 0;

            for (int c = 0; c < ModelConstants.TREE_DOMAIN_LAYER_NODES.length; c++) {
                @SuppressWarnings("unchecked")
                LGMTreeNode node = new LGMTreeNode(ModelConstants.getDisplayableName(ModelConstants.TREE_DOMAIN_LAYER_NODES[c]), false, false);
                fachebene.add(node);
                nodesToClear[nodesToClearIndex++] = node;
            }
            logebene.removeAllChildren();
            top.add(logebene);
            awb.removeAllChildren();
            logebene.add(awb);
            for (int c = 0; c < ModelConstants.TREE_LOGICAL_LAYER_NODES.length; c++) {
                Class<? extends ModelElement> clazz = ((Class<?>) ModelConstants.TREE_LOGICAL_LAYER_NODES[c]).asSubclass(ModelElement.class);
                LGMTreeNode node = new LGMTreeNode(ModelConstants.getDisplayableName(clazz), false, false);
                if (Anwendungsbaustein.class.isAssignableFrom(clazz)) {
                    awb.add(node);
                } else {
                    logebene.add(node);
                }
                nodesToClear[nodesToClearIndex++] = node;
            }
            phyebene.removeAllChildren();
            top.add(phyebene);
            for (int c = 0; c < ModelConstants.TREE_PHYSICAL_LAYER_NODES.length; c++) {
                @SuppressWarnings("unchecked")
                LGMTreeNode node = new LGMTreeNode(ModelConstants.getDisplayableName(ModelConstants.TREE_PHYSICAL_LAYER_NODES[c]), false, false);
                phyebene.add(node);
                nodesToClear[nodesToClearIndex++] = node;
            }
        } else {
            for (int i = 0; i < nodesToClear.length; i++) {
                nodesToClear[i].removeAllChildren();
            }
            if (textFieldDomainLayer != null && textFieldDomainLayer.getParent() == fachebene) {
                fachebene.remove(textFieldDomainLayer);
            }
            if (textFieldLogicalLayer != null && textFieldLogicalLayer.getParent() == logebene) {
                logebene.remove(textFieldLogicalLayer);
            }
            if (textFieldPhysicalLayer != null && textFieldPhysicalLayer.getParent() == phyebene) {
                phyebene.remove(textFieldPhysicalLayer);
            }
        }
    }

    /**
     * @param ec
     * @param performingRebuild
     * @param selDoc
     * @param layer
     */
    public void addObject(final ElementContainer ec, final boolean performingRebuild, final GraphDocument selDoc, final int layer) {
        if (ec instanceof NodeContainer) {
            NodeContainer kc = (NodeContainer) ec;
            NodeContainer kc2 = (NodeContainer) kc.getElement().getContainer(selDoc);
            if (kc2 != null) {
                kc = kc2;
            }
            if (!performingRebuild && getParentNodeOf(kc) != null) {
                return;
            }

            LGMTreeNode elementNode = kc.getTreeNode();
            //			if (kc.getElement() instanceof Prozess) {
            //				kc.checkIcon();
            //				elementNode = new LGMTreeNode(kc, true, false);
            //			}
            if (elementNode == null) {
                elementNode = new LGMTreeNode(kc, true, false);
            } else {
                elementNode.removeAllChildren();
            }
            LGMTreeNode parent_node = getParentNodeOfType(kc, layer);
            if (parent_node != null) {
                parent_node.add(elementNode);
                addChildren(elementNode, performingRebuild, selDoc);
            }
        }
    }

    /**
     * @param elementNode
     * @param performingRebuild
     * @param selDoc
     */
    private void addChildren(final LGMTreeNode elementNode, final boolean performingRebuild, final GraphDocument selDoc) {
        if (showUserDefinedProperties) {
            addUserDefinedProperties(elementNode, performingRebuild, selDoc);
        }

        if (!showPartOfHierarchy) {
            return;
        }

        ElementContainer kc = (ElementContainer) elementNode.getUserObject();

        LGMTreeNode parent = (LGMTreeNode) elementNode.getParent();

        GraphDocument maindoc = doc.getCollection().getMainGraphDocument();
        List<ElementContainer> all = kc.getElement().getDirectPartContainer(UserProperties.isEnableSubmodelBrowser() ? selDoc : maindoc);
        loop1: for (int i = 0; i < all.size(); i++) {
            ElementContainer pc = all.get(i);
            ElementContainer pc2 = pc.getElement().getContainer(selDoc);
            if (pc2 != null) {
                pc = pc2;
            }

            if (UserProperties.isEnableSubmodelBrowser()) {
                if (!pc.getElement().isUnique() && pc.getElement().getContainer(selDoc) == null) {
                    continue;
                }
            }

            LGMTreeNode p = parent;
            while (p != null) {
                if (p.getUserObject() == pc) {
                    continue loop1;
                }
                p = (LGMTreeNode) p.getParent();
            }
            LGMTreeNode childNode = null;
            //bei NodeContainern werden die evtl. bereits vorhandenen TreeNodes wiederverwendet
            if (pc instanceof NodeContainer) {
                List<ElementContainer> directParentElements = pc.getElement().getDirectParentContainer(UserProperties.isEnableSubmodelBrowser() ? selDoc : maindoc);
                // wenn es mehr als einen parent gibt, dann einfach alle Nodes neu erzeugen. Der Fall ist selten
                //aber dann werden evtl. vorher ausgeklappte nodes nicht mehr aufgeklappt sein. Die Alternative wäre,
                //sich statt nur eines Nodes im ElementContaier alle zu merken. Ich finde das muss nicht sein, da das
                //nur in diesem seltenen Fals den expansionState von allen Knoten retten würde, die mehr als einen Parent haben.
                if (directParentElements.size() < 2) {
                    NodeContainer nc = (NodeContainer) pc;
                    childNode = nc.getTreeNode();
                }
            }
            if (childNode == null) {
                childNode = new LGMTreeNode(pc, true, false);
            } else {
                childNode.removeAllChildren();
            }
            elementNode.add(childNode);
            addChildren(childNode, performingRebuild, selDoc);
        }
    }

    /**
     * @param elementNode
     * @param performingRebuild
     * @param selDoc
     */
    private void addUserDefinedProperties(final LGMTreeNode elementNode, final boolean performingRebuild, final GraphDocument selDoc) {
        ElementContainer ec = (ElementContainer) elementNode.getUserObject();
        ModelElement me = ec.getElement();

        Set<UserField> allOfThisElement = me.getUserFieldInputValueKeys();

        GDCollection gdcol = doc.getCollection();
        UserFieldDefinitions ufDefs = gdcol.getUserFieldDefinitions();

        for (UserField uf : ufDefs.getUserFields(me.getClass())) {
            if (uf.hasStyle(UserField.Style.HYPERLINK)) {
                if (uf.isTreeVisibility()) {
                    String value = me.getUserFieldInputValue(uf);
                    HyperlinkString label = new HyperlinkString(uf.getName() + ": " + value, value);
                    LGMTreeNode childNode = new LGMTreeNode(label, true, false);
                    elementNode.add(childNode);
                }
            } else if (uf.hasStyle(UserField.Style.SEPARATOR)) {
                if (uf.isTreeVisibility()) {
                    String label = "--- " + uf.getName() + " ---------";
                    LGMTreeNode childNode = new LGMTreeNode(label, true, false);
                    elementNode.add(childNode);
                }
            } else if (uf.isClassificationUserField()) {
                if (uf.isTreeVisibility()) {

                    //					System.err.println("Name:\t\t"+uf.getName());
                    //					System.err.println("mename:\t\t"+me.getName());
                    //					System.err.println("value:\t\t"+uf.getValue(me));
                    //					System.err.println("formatetvalue:\t"+uf.getFormatedValue(me,true)+"\n");

                    String label = uf.getName() + ": " + uf.getFormattedValue(me, true);
                    LGMTreeNode childNode = new LGMTreeNode(label, true, false);
                    elementNode.add(childNode);
                }
            } else {
                if (uf.isTreeVisibility() && allOfThisElement.contains(uf)) {
                    String value = me.getUserFieldInputValue(uf);
                    String label = uf.getName() + ": " + value;
                    elementNode.add(new LGMTreeNode(label, true, false));
                }
            }
        }

    }

    /**
     * @param objekt
     */
    public void removeObject(final ElementContainer objekt) {
        if (objekt instanceof NodeContainer) {
            LGMTreeNode node = ((NodeContainer) objekt).getTreeNode();
            ((DefaultTreeModel) treeModel).removeNodeFromParent(node);
        }
        ((DefaultTreeModel) treeModel).reload();
    }

    /**
     * @param obj
     * @return
     */
    public static LGMTreeNode getParentNodeOf(final NodeContainer obj) {
        if (obj == null) {
            return null;
        }
        if (obj.getTreeNode() == null) {
            return null;
        }
        return (LGMTreeNode) obj.getTreeNode().getParent();
    }

    /**
     * @param obj
     * @param layer
     * @return
     */
    protected LGMTreeNode getParentNodeOfType(final NodeContainer obj, final int layer) {
        Knoten me = (Knoten) obj.getElement();
        String name = ModelConstants.getDisplayableName(me.getClass());
        switch (layer) {
        case 4:
            for (int n = 0; n < fachebene.getChildCount(); n++) {
                if (name.equals(fachebene.getChildAt(n).toString())) {
                    return (LGMTreeNode) fachebene.getChildAt(n);
                }
            }
            if (me instanceof Textfeld) {
                if (textFieldDomainLayer == null) {
                    textFieldDomainLayer = new LGMTreeNode(ModelConstants.getDisplayableName(Textfeld.class), false, false);
                }
                if (textFieldDomainLayer.getParent() == null) {
                    textFieldDomainLayer.removeAllChildren();
                    fachebene.add(textFieldDomainLayer);
                }
                return textFieldDomainLayer;
            }
            break;
        case 2:
            if (me instanceof Anwendungsbaustein) {
                for (int n = 0; n < awb.getChildCount(); n++) {
                    if (name.equals(awb.getChildAt(n).toString())) {
                        return (LGMTreeNode) awb.getChildAt(n);
                    }
                }
            } else if (me instanceof Textfeld) {
                if (textFieldLogicalLayer == null) {
                    textFieldLogicalLayer = new LGMTreeNode(ModelConstants.getDisplayableName(Textfeld.class), false, false);
                }
                if (textFieldLogicalLayer.getParent() == null) {
                    textFieldLogicalLayer.removeAllChildren();
                    logebene.add(textFieldLogicalLayer);
                }
                return textFieldLogicalLayer;
            } else {
                for (int n = 0; n < logebene.getChildCount(); n++) {
                    if (name.equals(logebene.getChildAt(n).toString())) {
                        return (LGMTreeNode) logebene.getChildAt(n);
                    }
                }
            }
            break;
        case 0:
            for (int n = 0; n < phyebene.getChildCount(); n++) {
                if (name.equals(phyebene.getChildAt(n).toString())) {
                    return (LGMTreeNode) phyebene.getChildAt(n);
                }
            }
            if (me instanceof Textfeld) {
                if (textFieldPhysicalLayer == null) {
                    textFieldPhysicalLayer = new LGMTreeNode(ModelConstants.getDisplayableName(Textfeld.class), false, false);
                }
                if (textFieldPhysicalLayer.getParent() == null) {
                    textFieldPhysicalLayer.removeAllChildren();
                    phyebene.add(textFieldPhysicalLayer);
                }
                return textFieldPhysicalLayer;
            }
            break;
        }

        return null;
    }

    /**
     * @param obj
     * @return
     */
    protected LGMTreeNode createAndGetUserDefParentNodeOfType(final NodeContainer obj) {

        Knoten me = (Knoten) obj.getElement();
        String name = ModelConstants.getDisplayableName(me.getClass());

        switch (me.layerFor()) {
        case 4:
            for (int n = 0; n < fachebene.getChildCount(); n++) {
                if (name.equals(fachebene.getChildAt(n).toString())) {
                    return (LGMTreeNode) fachebene.getChildAt(n);
                }
            }
            break;
        case 2:
            if (me instanceof Anwendungsbaustein) {
                for (int n = 0; n < awb.getChildCount(); n++) {
                    if (name.equals(awb.getChildAt(n).toString())) {
                        return (LGMTreeNode) awb.getChildAt(n);
                    }
                }
            } else {
                for (int n = 0; n < logebene.getChildCount(); n++) {
                    if (name.equals(logebene.getChildAt(n).toString())) {
                        return (LGMTreeNode) logebene.getChildAt(n);
                    }
                }
            }
            break;
        case 0:
            for (int n = 0; n < phyebene.getChildCount(); n++) {
                if (name.equals(phyebene.getChildAt(n).toString())) {
                    return (LGMTreeNode) phyebene.getChildAt(n);
                }
            }
            break;
        }

        return null;
    }

    static boolean showPartOfHierarchy = false;
    static boolean showUserDefinedProperties = false;
    static int count = 0;

    /**
     * 
     */
    void buildTree() {
        if (doc == null) {
            return;
        }
        GraphDocument maindoc = doc.getCollection().getMainGraphDocument();
        selectionListener.setActive(false);
        createTree();
        saveExpansionState();
        showPartOfHierarchy = UserProperties.isShowPartOfHierarchy();
        showUserDefinedProperties = UserProperties.isShowUserDefinedPropertiesInModelBrowser();

        int ebene, n;
        for (ebene = 4; ebene >= 0; ebene -= 2) {
            List<NodeContainer> knoten = maindoc.getLayer(ebene).getKnotenAlphabetical();
            for (n = 0; n < knoten.size(); n++) {
                NodeContainer ec = knoten.get(n);
                ModelElement me = ec.getElement();

                if (UserProperties.isEnableSubmodelBrowser()) {
                    if (me.isUnique()) {
                        if (showPartOfHierarchy && ec.hasParent(maindoc)) {
                            continue;
                        }
                    } else {
                        if (me.getContainer(doc) == null || showPartOfHierarchy && ec.hasParent(doc)) {
                            continue;
                        }
                    }
                } else {
                    if (showPartOfHierarchy && ec.hasParent(maindoc)) {
                        continue;
                    }
                }
                addObject(ec, true, doc, ebene);
            }
        }
        ((DefaultTreeModel) treeModel).reload();
        restoreExpansionState();
        selectionListener.setActive(true);
        selectObjects();
    }

    /**
     * Selektiert im Baum alle Elemente, die im dazugehörigen {@link GraphDocument} selektiert sind.
     */
    public void selectObjects() {
        selectionListener.setActive(false);
        TreePath[] path = new TreePath[doc.getSelectedRealElementContainerCount()];
        int m = 0;
        GraphDocument mainDoc = doc.getCollection().getMainGraphDocument();

        for (NodeContainer ec : doc.getSelectedRealElementContainerIterable()) {
            ModelElement me = ec.getElement();
            ec = (NodeContainer) me.getContainer(doc);
            if (ec == null) {
                ec = (NodeContainer) me.getContainer(mainDoc);
            }
            LGMTreeNode node = ec.getTreeNode();
            if (node != null) {
                path[m++] = new TreePath(((DefaultTreeModel) treeModel).getPathToRoot(node));
            }
        }
        setSelectionPaths(path);
        if (path.length > 0) {
            scrollPathToVisible(path[path.length - 1]);
        }
        selectionListener.setActive(true);
    }

    /**
     * @param knot
     * @return
     */
    public static final int getLayerOf(final Knoten knot) {
        return knot.layerFor();
    }

    /**
     * @param node
     */
    private void refreshNode(final TreeNode node) {
        for (int i = 0; i < node.getChildCount(); i++) {
            TreeNode n = node.getChildAt(i);
            myModel.nodeChanged(n);
            refreshNode(n);
        }
    }

    /**
     * 
     */
    public void refreshTree() {
        for (int i = 0; i < nodesToClear.length; i++) {
            refreshNode(nodesToClear[i]);
        }
    }

    /**
     * Wenn die Layer-Nummer gültig ist, wird der zugehörige Ebenenknoten selektiert und ggf. zu ihm hingescollt.
     *
     * @param layer
     */
    public void selectLayerNode(final int layer) {
        TreePath path = null;
        switch (layer) {
        case 4:
            path = new TreePath(((DefaultTreeModel) treeModel).getPathToRoot(fachebene));
            break;
        case 2:
            path = new TreePath(((DefaultTreeModel) treeModel).getPathToRoot(logebene));
            break;
        case 0:
            path = new TreePath(((DefaultTreeModel) treeModel).getPathToRoot(phyebene));
            break;
        }
        setSelectionPath(path);
        scrollPathToVisible(path);

    }

    /**
     * Über diese Funktion kann der {@link DynamicTreeSelectionListener} den Layer wechseln, wenn
     * ein Layerknoten im Baum selektiert wurde.
     *
     * @param node
     * @return
     */
    public boolean setActiveLayer(final LGMTreeNode node) {
        boolean layerChanged = false;
        if (node == fachebene) {
            layerChanged = true;
            doc.getCollection().setActiveLayer(4);
        } else if (node == logebene) {
            layerChanged = true;
            doc.getCollection().setActiveLayer(2);
        } else if (node == phyebene) {
            layerChanged = true;
            doc.getCollection().setActiveLayer(0);
        }
        return layerChanged;
    }

    //	----------------------------------------------------------------------------------------------------------------------------------

    Enumeration<TreePath> expansionEnum = null;

    private void saveExpansionState() {
        expansionEnum = getExpandedDescendants(rootPath);
    }

    private void restoreExpansionState() {
        if (expansionEnum != null) {
            while (expansionEnum.hasMoreElements()) {
                TreePath path = expansionEnum.nextElement();
                expandPath(path);
            }
        }
    }

    ///////////////////////
    // UserFieldListener //
    ///////////////////////

    @Override
    public void userFieldAdded() {
        if (UserProperties.isShowUserDefinedPropertiesInModelBrowser()) {
            buildTree();
        }
    }

    @Override
    public void userFieldRemoved() {
        if (UserProperties.isShowUserDefinedPropertiesInModelBrowser()) {
            buildTree();
        }
    }

    @Override
    public void userFieldValueChanged() {
        if (UserProperties.isShowUserDefinedPropertiesInModelBrowser()) {
            refreshTree();
        }
    }

}
