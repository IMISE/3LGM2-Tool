package de.imise.tool3lgm.graphtools.view.tree;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.DOMAIN_LAYER;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.LOGICAL_LAYER;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.MAX_LAYER_INDEX;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.MIN_LAYER_INDEX;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.PHYSICAL_LAYER;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.getDisplayableName;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.isInterLayer;
import static de.imise.tool3lgm.graphtools.undoredo.TransactionManager.STANDARD_PID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import de.imise.tool3lgm.KeyStrokes;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.Node;
import de.imise.tool3lgm.graphtools.metamodel.Textfeld;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.GraphDocumentListener;
import de.imise.tool3lgm.graphtools.model.GraphDocumentOwner;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.event.UserFieldListener;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.tools.LGMTreeNode;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty;

/**
 * @author N.N.
 */
public final class DynamicTree extends JTree implements UserFieldListener, GraphDocumentOwner {

    /**
     * Node für die Fachliche Ebene
     */
    private final LGMTreeNode fachebene = new LGMTreeNode(ModelConstants.getVisibleLayerName(ModelConstants.DOMAIN_LAYER), false, true);

    /**
     * Node für die Logische Werkzeugebene
     */
    private final LGMTreeNode logebene = new LGMTreeNode(ModelConstants.getVisibleLayerName(ModelConstants.LOGICAL_LAYER), false, true);

    /**
     * Node für die physische Werkzeugebene
     */
    private final LGMTreeNode phyebene = new LGMTreeNode(ModelConstants.getVisibleLayerName(ModelConstants.PHYSICAL_LAYER), false, true);

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
     * Transaktions-ID, mit der der Baum alle seine Änderungen vornimmt.
     */
    public static final int PID = STANDARD_PID;

    /**
     * COMMENTME
     */
    private final DefaultTreeModel myModel;

    /**
     * COMMENTME
     */
    private final TreePath rootPath;

    private final Map<Class<? extends ModelElement>, LGMTreeNode> elementClassToParentNode = new HashMap<>();

    private final Collection<LGMTreeNode> nodesToClear;

    /**
     * @param d
     */
    public DynamicTree(final GraphDocument d) {
        super(new DefaultTreeModel(new LGMTreeNode(getResString("MODEL_BRWOSER_TITLE"), false, false)));
        initTree();
        nodesToClear = elementClassToParentNode.values();
        rootPath = new TreePath(((DefaultTreeModel) getModel()).getPathToRoot((LGMTreeNode) getModel().getRoot()));
        doc = d;
        //alle KeyStrokes im Baum hinzufügen, die systemweit gelten sollen. Da der Baum schon eine eigene InputMap und ActionMap hat,
        //werden die ShortCuts aus dem RootPane des Tools hier nicht auch beachtet und müssen explizit hinzugefügt werden
        KeyStrokes.registerPublicKeyStrokes(this);
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

    private void sortedByAssignable(final List<Class<? extends ModelElement>> classes) {
        int classesCount = classes.size();
        for (int i = 0; i < classesCount - 1; i++) {
            for (int j = i + 1; j < classesCount; j++) {
                if (classes.get(j).isAssignableFrom(classes.get(i))) {
                    classes.add(i, classes.remove(j));
                    i--;
                    break;
                }
            }
        }
    }

    private final void initLayer(final LGMTreeNode layerNode, final Class<? extends ModelElement>[] treeLayerVisibleAbstractNodes, final Class<? extends ModelElement>[] treeLayerVisibleInstancialeNodes) {
        //die abstracten Klassen holen, die in der Hierarchie des Baumes unterhalb des Layer-Knotens angezeigt werden sollen
        List<Class<? extends ModelElement>> abstractClasses = new ArrayList<>(Arrays.asList(treeLayerVisibleAbstractNodes));
        //diese Liste so sortieren, dass sichergestellt ist, dass alle Klassen die in der Liste vorkommen, die Unterklasse einer anderen
        //Klasse in der Liste sind, immer nach dieser Oberklasse in der Liste stehen
        sortedByAssignable(abstractClasses);
        //Liste von Knoten für die abstrakten Klassen in derselben Reihenfolge wie die abstrakten Klassen
        List<LGMTreeNode> abstractClassNodes = new ArrayList<>();
        for (Class<? extends ModelElement> abstractClass : abstractClasses) {
            abstractClassNodes.add(new LGMTreeNode(ModelConstants.getDisplayablePluralName(abstractClass), false, true));
        }
        //jetzt die ElementKnoten unter die abstrakten Knoten hängen oder unter den LayerKnoten selbst, wenn es keinen abstakten Oberklassenkoten gibt
        for (Class<? extends ModelElement> elementClass : treeLayerVisibleInstancialeNodes) {
            LGMTreeNode instanciableClassNode = new LGMTreeNode(getDisplayableName(elementClass), false, false);
            elementClassToParentNode.put(elementClass, instanciableClassNode);
            boolean superClassFound = false;
            for (int i = abstractClasses.size() - 1; i >= 0; i--) {
                Class<? extends ModelElement> abstractClass = abstractClasses.get(i);
                if (abstractClass.isAssignableFrom(elementClass)) {
                    LGMTreeNode abstractClassNode = abstractClassNodes.get(i);
                    abstractClassNode.add(instanciableClassNode);
                    superClassFound = true;
                    break;
                }
            }
            if (!superClassFound) {
                layerNode.add(instanciableClassNode);
            }
        }
        //jetzt die abstrakten Knoten sich gegenseitig unterhängen (wenn der eine ein Oberklassenknoten des anderen ist) oder dem LayerKnoten unterhängen (wenn es keinen Oberklassenknoten gibt)
        //von hinten anfangen, weil die Klasse ja so sortiert wurden, dass die hinteren Unterklassen der vorderen sein können aber nicht mehr umgekehrt
        for (int i = abstractClasses.size() - 1; i >= 0; i--) {
            Class<? extends ModelElement> potencialAbstractSubClass = abstractClasses.get(i);
            LGMTreeNode potencialAbstractSubClassNode = abstractClassNodes.get(i);
            boolean superClassFound = false;
            //alle Klassen vor der aktuellen Klasse durchsuchen, ob es eine Oberklasse der aktuellen Klasse ist. Wenn ja -> hönge den Knoten der Klasse unter
            for (int j = i - 1; j >= 0; j--) {
                Class<? extends ModelElement> potencialAbstractSuperClass = abstractClasses.get(j);
                if (potencialAbstractSuperClass.isAssignableFrom(potencialAbstractSubClass)) {
                    LGMTreeNode potencialAbstractSuperClassNode = abstractClassNodes.get(j);
                    potencialAbstractSuperClassNode.add(potencialAbstractSubClassNode);
                    superClassFound = true;
                    break;
                }
            }
            //keinen Oberklassenknoten gefunden -> an den Layerknoten hängen
            if (!superClassFound) {
                layerNode.add(potencialAbstractSubClassNode);
            }
        }
    }

    private void initTree() {
        LGMTreeNode top = (LGMTreeNode) treeModel.getRoot();
        top.add(fachebene);
        top.add(logebene);
        top.add(phyebene);
        initLayer(fachebene, ModelConstants.getTreeDomainLayerVisibleAbstractNodes(), ModelConstants.TREE_DOMAIN_LAYER_NODES);
        initLayer(logebene, ModelConstants.getTreeLogicalLayerVisibleAbstractNodes(), ModelConstants.TREE_LOGICAL_LAYER_NODES);
        initLayer(phyebene, ModelConstants.getTreePhysicalLayerVisibleAbstractNodes(), ModelConstants.TREE_PHYSICAL_LAYER_NODES);
    }

    public boolean isLayerNode(final Object o) {
        return o == fachebene || o == logebene || o == phyebene;
    }

    public void setTransactionListenerActive(final boolean active) {
        transactionListener.setActive(active);
    }

    /**
     * Der allgemeine Baum wird erzeugt oder zurueckgesetzt
     */
    private void createTree() {
        for (LGMTreeNode node : nodesToClear) {
            node.removeAllChildren();
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

    /**
     * @param ec
     * @param performingRebuild
     * @param selDoc
     * @param layer
     */
    private void addObject(final ElementContainer ec, final boolean performingRebuild, final GraphDocument selDoc, final int layer) {
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
        boolean isEnableSubmodelBrowser = UserProperties.is(BooleanProperty.OPTION_ENABLE_SUBMODEL_BROWSER);
        List<ElementContainer> all = kc.getElement().getDirectPartContainer(isEnableSubmodelBrowser ? selDoc : maindoc);
        loop1: for (ElementContainer pc : all) {
            ModelElement me = pc.getElement();
            ElementContainer ecSelDoc = me.getContainer(selDoc);
            if (ecSelDoc != null) {
                pc = ecSelDoc;
            }
            if (isEnableSubmodelBrowser) {
                if (!me.isUnique() && ecSelDoc == null) {
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
                List<ElementContainer> directParentElements = me.getDirectParentContainer(isEnableSubmodelBrowser ? selDoc : maindoc);
                // wenn es mehr als einen parent gibt, dann einfach alle Nodes neu erzeugen. Der Fall ist selten
                //aber dann werden evtl. vorher ausgeklappte nodes nicht mehr aufgeklappt sein. Die Alternative wäre,
                //sich statt nur eines Nodes im ElementContaier alle zu merken. Ich finde das muss nicht sein, da das
                //nur in diesem seltenen Fals den expansionState von allen Node retten würde, die mehr als einen Parent haben.
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
        Class<? extends ModelElement> elementClass = me.getClass();
        for (UserField uf : ufDefs.getUserFields(elementClass)) {
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

    private LGMTreeNode getOrCreateTextFieldNode(final LGMTreeNode layerNode, LGMTreeNode textFieldNode) {
        if (textFieldNode == null) {
            textFieldNode = new LGMTreeNode(getDisplayableName(Textfeld.class), false, false);
        }
        if (textFieldNode.getParent() == null) {
            textFieldNode.removeAllChildren();
            layerNode.add(textFieldNode);
        }
        return textFieldNode;
    }

    /**
     * @param obj
     * @param layer
     * @return
     */
    private LGMTreeNode getParentNodeOfType(final NodeContainer obj, final int layer) {
        Node me = (Node) obj.getElement();
        if (me instanceof Textfeld) {
            if (layer == DOMAIN_LAYER) {
                textFieldDomainLayer = getOrCreateTextFieldNode(fachebene, textFieldDomainLayer);
                return textFieldDomainLayer;
            } else if (layer == LOGICAL_LAYER) {
                textFieldLogicalLayer = getOrCreateTextFieldNode(logebene, textFieldLogicalLayer);
                return textFieldLogicalLayer;
            } else if (layer == PHYSICAL_LAYER) {
                textFieldPhysicalLayer = getOrCreateTextFieldNode(phyebene, textFieldPhysicalLayer);
                return textFieldPhysicalLayer;
            }
        }
        return elementClassToParentNode.get(me.getClass());
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
        showPartOfHierarchy = UserProperties.is(BooleanProperty.OPTION_SHOW_PART_OF_HIERARCHY);
        showUserDefinedProperties = isShowUserDefinedPropertiesInModelBrowser();
        boolean isEnableSubmodelBrowser = UserProperties.is(BooleanProperty.OPTION_ENABLE_SUBMODEL_BROWSER);
        for (int ebene = MAX_LAYER_INDEX; ebene >= MIN_LAYER_INDEX; ebene--) {
            if (isInterLayer(ebene)) {
                continue;
            }
            for (NodeContainer nc : maindoc.getLayer(ebene).getKnotenAlphabetical()) {
                ModelElement me = nc.getElement();
                if (isEnableSubmodelBrowser) {
                    if (me.isUnique()) {
                        if (showPartOfHierarchy && nc.hasParent(maindoc)) {
                            continue;
                        }
                    } else {
                        if (me.getContainer(doc) == null || showPartOfHierarchy && nc.hasParent(doc)) {
                            continue;
                        }
                    }
                } else {
                    if (showPartOfHierarchy && nc.hasParent(maindoc)) {
                        continue;
                    }
                }
                addObject(nc, true, doc, ebene);
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
    public static final int getLayerOf(final Node knot) {
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
        for (LGMTreeNode nodeToRefresh : nodesToClear) {
            refreshNode(nodeToRefresh);
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
        case DOMAIN_LAYER:
            path = new TreePath(((DefaultTreeModel) treeModel).getPathToRoot(fachebene));
            break;
        case LOGICAL_LAYER:
            path = new TreePath(((DefaultTreeModel) treeModel).getPathToRoot(logebene));
            break;
        case PHYSICAL_LAYER:
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
            doc.getCollection().setActiveLayer(DOMAIN_LAYER);
        } else if (node == logebene) {
            layerChanged = true;
            doc.getCollection().setActiveLayer(LOGICAL_LAYER);
        } else if (node == phyebene) {
            layerChanged = true;
            doc.getCollection().setActiveLayer(PHYSICAL_LAYER);
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

    private boolean isShowUserDefinedPropertiesInModelBrowser() {
        return UserProperties.is(BooleanProperty.OPTION_SHOW_USER_DEFINED_PROPERTIES_IN_MODEL_BROWSER);
    }

    ///////////////////////
    // UserFieldListener //
    ///////////////////////
    @Override
    public void userFieldAdded() {
        if (isShowUserDefinedPropertiesInModelBrowser()) {
            buildTree();
        }
    }

    @Override
    public void userFieldRemoved() {
        if (isShowUserDefinedPropertiesInModelBrowser()) {
            buildTree();
        }
    }

    @Override
    public void userFieldValueChanged() {
        if (isShowUserDefinedPropertiesInModelBrowser()) {
            refreshTree();
        }
    }

}
