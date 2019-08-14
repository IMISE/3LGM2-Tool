package de.imise.tool3lgm.graphtools.view.tree;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.DOMAIN_LAYER;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.LOGICAL_LAYER;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.MAX_LAYER_INDEX;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.MIN_LAYER_INDEX;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.PHYSICAL_LAYER;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.isInterLayer;
import static de.imise.tool3lgm.graphtools.undoredo.TransactionManager.STANDARD_PID;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_ENABLE_SUBMODEL_BROWSER;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SHOW_PART_OF_HIERARCHY;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SHOW_USER_DEFINED_PROPERTIES_IN_MODEL_BROWSER;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import de.imise.tool3lgm.KeyStrokes;
import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.metamodel.elements.Textfield;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.GDCollectionChangeListener;
import de.imise.tool3lgm.graphtools.model.GraphDocumentOwner;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.event.UserFieldListener;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.graphtools.view.tree.node.ElementClassTreeNode;
import de.imise.tool3lgm.graphtools.view.tree.node.ElementContainerTreeNode;
import de.imise.tool3lgm.graphtools.view.tree.node.LGMTreeNode;
import de.imise.tool3lgm.graphtools.view.tree.node.StringTreeNode;
import de.imise.tool3lgm.graphtools.view.tree.node.UserFieldTreeNode;

/**
 * @author N.N.
 */
public final class DynamicTree extends JTree implements UserFieldListener, GraphDocumentOwner {

    /**
     * Node für die Fachliche Ebene
     */
    private final LGMTreeNode domainLayer = new StringTreeNode(ModelConstants.getVisibleLayerName(ModelConstants.DOMAIN_LAYER));

    /**
     * Node für die Logische Werkzeugebene
     */
    private final LGMTreeNode logicalLayer = new StringTreeNode(ModelConstants.getVisibleLayerName(ModelConstants.LOGICAL_LAYER));

    /**
     * Node für die physische Werkzeugebene
     */
    private final LGMTreeNode physicalLayer = new StringTreeNode(ModelConstants.getVisibleLayerName(ModelConstants.PHYSICAL_LAYER));

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
     * @param doc
     */
    public DynamicTree(final GraphDocument doc) {
        super(new DefaultTreeModel(new StringTreeNode(getResString("MODEL_BRWOSER_TITLE"), false)));
        this.doc = doc;
        initTree();
        nodesToClear = elementClassToParentNode.values();
        rootPath = new TreePath(((DefaultTreeModel) getModel()).getPathToRoot((LGMTreeNode) getModel().getRoot()));
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
     * Setzt das übergebene {@link GraphDocument} für diesen Baum und fügt den Baum als {@link GDCollectionChangeListener} hinzu. Beim vorherigen
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

    private final void initLayer(final LGMTreeNode layerNode, final Class<? extends ModelElement>[] treeLayerVisibleAbstractNodes, final Iterable<Class<? extends ModelElement>> treeLayerVisibleInstancialeNodes) {
        //die abstracten Klassen holen, die in der Hierarchie des Baumes unterhalb des Layer-Knotens angezeigt werden sollen
        List<Class<? extends ModelElement>> abstractClasses = new ArrayList<>(Arrays.asList(treeLayerVisibleAbstractNodes));
        //diese Liste so sortieren, dass sichergestellt ist, dass alle Klassen die in der Liste vorkommen, die Unterklasse einer anderen
        //Klasse in der Liste sind, immer nach dieser Oberklasse in der Liste stehen
        sortedByAssignable(abstractClasses);
        //Liste von Knoten für die abstrakten Klassen in derselben Reihenfolge wie die abstrakten Klassen
        List<LGMTreeNode> abstractClassNodes = new ArrayList<>();
        ElementsNameBuilder elementsNameBuilder = doc.getElementsNameBuilder();
        for (Class<? extends ModelElement> abstractClass : abstractClasses) {
            String label = elementsNameBuilder.getDisplayableName(abstractClass);
            abstractClassNodes.add(new ElementClassTreeNode(abstractClass, label));
        }
        //jetzt die ElementKnoten unter die abstrakten Knoten hängen oder unter den LayerKnoten selbst, wenn es keinen abstakten Oberklassenkoten gibt
        for (Class<? extends ModelElement> elementClass : treeLayerVisibleInstancialeNodes) {
            String label = elementsNameBuilder.getDisplayableName(elementClass);
            LGMTreeNode instanciableClassNode = new ElementClassTreeNode(elementClass, label, false); // muss nicht selbst sortieren, weil die Elemente bereits sortiert reinkommen
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
        top.add(domainLayer);
        top.add(logicalLayer);
        top.add(physicalLayer);
        MetaModel metaModel = doc.getMetaModel();
        initLayer(domainLayer, metaModel.getTreeDomainLayerVisibleAbstractNodes(), metaModel.treeDomainLayerNodes);
        initLayer(logicalLayer, metaModel.getTreeLogicalLayerVisibleAbstractNodes(), metaModel.treeLogicalLayerNodes);
        initLayer(physicalLayer, metaModel.getTreePhysicalLayerVisibleAbstractNodes(), metaModel.treePhysicalLayerNodes);
    }

    public boolean isLayerNode(final Object o) {
        return o == domainLayer || o == logicalLayer || o == physicalLayer;
    }

    public void setTransactionListenerActive(final boolean active) {
        transactionListener.setActive(active);
    }

    private final Map<DefaultMutableTreeNode, DefaultMutableTreeNode> onlyExperModeVisibleNodesToParent = new HashMap<>();

    /**
     * Der allgemeine Baum wird erzeugt oder zurueckgesetzt
     */
    private void createTree() {
        //alle Knoten entfernen/einblenden, die nicht/nur im ExpertMode zu sehen sein sollen
        for (Class<? extends ModelElement> onlyExperModeVisibleNodeClass : doc.getMetaModel().getOnlyExpertModeVisibleNodes()) {
            LGMTreeNode node = elementClassToParentNode.get(onlyExperModeVisibleNodeClass);
            if (node != null) {
                if (Static.isExpertMode()) {
                    DefaultMutableTreeNode parent = onlyExperModeVisibleNodesToParent.remove(node);
                    if (parent != null) {
                        parent.add(node);
                    }
                } else {
                    DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
                    if (parent != null) {
                        onlyExperModeVisibleNodesToParent.put(node, parent);
                        parent.remove(node);
                    }
                }
            }
        }

        for (LGMTreeNode node : nodesToClear) {
            node.removeAllChildren();
        }
        if (textFieldDomainLayer != null && textFieldDomainLayer.getParent() == domainLayer) {
            domainLayer.remove(textFieldDomainLayer);
        }
        if (textFieldLogicalLayer != null && textFieldLogicalLayer.getParent() == logicalLayer) {
            logicalLayer.remove(textFieldLogicalLayer);
        }
        if (textFieldPhysicalLayer != null && textFieldPhysicalLayer.getParent() == physicalLayer) {
            physicalLayer.remove(textFieldPhysicalLayer);
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
            ElementContainerTreeNode elementNode = kc.getTreeNode();
            //			if (kc.getElement() instanceof Prozess) {
            //				kc.checkIcon();
            //				elementNode = new LGMTreeNode(kc, true, false);
            //			}
            if (elementNode == null) {
                elementNode = new ElementContainerTreeNode(kc, true, false);
            } else {
                elementNode.removeAllChildren();
            }
            LGMTreeNode parent_node = getParentNodeOfType(kc, layer);
            if (parent_node != null) {
                parent_node.add(elementNode);
                addChildren(elementNode, selDoc);
            }
        }
    }

    /**
     * @param elementNode
     * @param performingRebuild
     * @param selDoc
     */
    private void addChildren(final ElementContainerTreeNode elementNode, final GraphDocument selDoc) {
        if (showUserDefinedProperties) {
            addUserDefinedProperties(elementNode, selDoc);
        }
        if (!showPartOfHierarchy) {
            return;
        }
        ElementContainer kc = elementNode.getUserObject();
        LGMTreeNode parent = (LGMTreeNode) elementNode.getParent();
        GraphDocument maindoc = doc.getCollection().getMainGraphDocument();
        boolean isEnableSubmodelBrowser = OPTION_ENABLE_SUBMODEL_BROWSER.is();
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
            ElementContainerTreeNode childNode = null;
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
                childNode = new ElementContainerTreeNode(pc, true, false);
            } else {
                childNode.removeAllChildren();
            }
            elementNode.add(childNode);
            addChildren(childNode, selDoc);
        }
    }

    /**
     * @param elementNode
     * @param selDoc
     */
    private void addUserDefinedProperties(final ElementContainerTreeNode elementNode, final GraphDocument selDoc) {
        ElementContainer ec = elementNode.getUserObject();
        ModelElement me = ec.getElement();
        Set<UserField> allOfThisElement = me.getUserFieldInputValueKeys();
        GDCollection gdcol = doc.getCollection();
        UserFieldDefinitions ufDefs = gdcol.getUserFieldDefinitions();
        Class<? extends ModelElement> elementClass = me.getClass();
        for (UserField uf : ufDefs.getUserFields(elementClass)) {
            if (uf.isTreeVisibility()) {
                if (uf.hasStyle(UserField.Style.HYPERLINK) || uf.hasStyle(UserField.Style.SEPARATOR) || uf.isClassificationUserField() || allOfThisElement.contains(uf)) {
                    UserFieldTreeNode userFieldTreeNode = new UserFieldTreeNode(uf, me);
                    elementNode.add(userFieldTreeNode);
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
            ElementsNameBuilder elementsNameBuilder = doc.getElementsNameBuilder();
            String label = elementsNameBuilder.getDisplayableName(Textfield.class);
            textFieldNode = new ElementClassTreeNode(Textfield.class, label, false);
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
        if (me instanceof Textfield) {
            if (layer == DOMAIN_LAYER) {
                textFieldDomainLayer = getOrCreateTextFieldNode(domainLayer, textFieldDomainLayer);
                return textFieldDomainLayer;
            } else if (layer == LOGICAL_LAYER) {
                textFieldLogicalLayer = getOrCreateTextFieldNode(logicalLayer, textFieldLogicalLayer);
                return textFieldLogicalLayer;
            } else if (layer == PHYSICAL_LAYER) {
                textFieldPhysicalLayer = getOrCreateTextFieldNode(physicalLayer, textFieldPhysicalLayer);
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
        showPartOfHierarchy = OPTION_SHOW_PART_OF_HIERARCHY.is();
        showUserDefinedProperties = isShowUserDefinedPropertiesInModelBrowser();
        boolean isEnableSubmodelBrowser = OPTION_ENABLE_SUBMODEL_BROWSER.is();
        for (int ebene = MAX_LAYER_INDEX; ebene >= MIN_LAYER_INDEX; ebene--) {
            if (isInterLayer(ebene)) {
                continue;
            }
            for (NodeContainer nc : maindoc.getLayer(ebene).getNodeContainersAlphabetical()) {
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
     * @param node
     * @return
     */
    public static final int getLayerOf(final Node node) {
        return node.layerFor();
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
            path = new TreePath(((DefaultTreeModel) treeModel).getPathToRoot(domainLayer));
            break;
        case LOGICAL_LAYER:
            path = new TreePath(((DefaultTreeModel) treeModel).getPathToRoot(logicalLayer));
            break;
        case PHYSICAL_LAYER:
            path = new TreePath(((DefaultTreeModel) treeModel).getPathToRoot(physicalLayer));
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
        if (node == domainLayer) {
            layerChanged = true;
            doc.getCollection().setActiveLayer(DOMAIN_LAYER);
        } else if (node == logicalLayer) {
            layerChanged = true;
            doc.getCollection().setActiveLayer(LOGICAL_LAYER);
        } else if (node == physicalLayer) {
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
        return OPTION_SHOW_USER_DEFINED_PROPERTIES_IN_MODEL_BROWSER.is();
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
