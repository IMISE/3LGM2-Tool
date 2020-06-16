package de.imise.tool3lgm.graphtools.view.tree;

import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.DOMAIN_LAYER;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.LOGICAL_LAYER;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.MAX_LAYER_INDEX;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.MIN_LAYER_INDEX;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.PHYSICAL_LAYER;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.isInterLayer;
import static de.imise.tool3lgm.graphtools.undoredo.TransactionManager.STANDARD_PID;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_ENABLE_EXPERT_MODE;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_ENABLE_SUBMODEL_BROWSER;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SHOW_PART_OF_HIERARCHY;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SHOW_TEMPLATE_ELEMENTS_IN_MODEL_BROWSER;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SHOW_USER_DEFINED_PROPERTIES_IN_MODEL_BROWSER;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import de.imise.tool3lgm.KeyStrokes;
import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.event.ActionIdentifier;
import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.metamodel.elements.Textfield;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMChangeListener;
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
import de.imise.tool3lgm.gui.menu.ContextGenerator;
import de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty;

/**
 * A tree view to the model. Under the root node are the layer nodes, then for every
 * layer node the class nodes and then the instances with optional child nodes for
 * the user defined properties.
 *
 * @author N.N.
 */
public final class ModelBrowserTree extends DynamicTree implements UserFieldListener {

    /** Node for the Domain Layer */
    private final LGMTreeNode domainLayer = new StringTreeNode(ModelConstants.getVisibleLayerName(ModelConstants.DOMAIN_LAYER), getLayerIcon(ActionIdentifier.ACTION_ACTIVATE_DOMAIN_LAYER));

    /** Node for the Logical Tool Layer */
    private final LGMTreeNode logicalLayer = new StringTreeNode(ModelConstants.getVisibleLayerName(ModelConstants.LOGICAL_LAYER), getLayerIcon(ActionIdentifier.ACTION_ACTIVATE_LOGICAL_LAYER));

    /** Node for the Physical Tool Layer */
    private final LGMTreeNode physicalLayer = new StringTreeNode(ModelConstants.getVisibleLayerName(ModelConstants.PHYSICAL_LAYER), getLayerIcon(ActionIdentifier.ACTION_ACTIVATE_PHYSICAL_LAYER));

    /**
     * Node where all {@link Textfield} instances are displayed as
     * children which exists at the Domain Layer in the corresponding
     * {@link GraphDocument}.
     */
    private LGMTreeNode textFieldDomainLayer = null;

    /**
     * Node where all {@link Textfield} instances are displayed as
     * children which exists at the Logical Tool Layer in the
     * corresponding {@link GraphDocument}.
     */
    private LGMTreeNode textFieldLogicalLayer = null;

    /**
     * Node where all {@link Textfield} instances are displayed as
     * children which exists at the Physical Tool Layer in the
     * corresponding {@link GraphDocument}.
     */
    private LGMTreeNode textFieldPhysicalLayer = null;

    /**
     * The {@link GraphDocument} this model browser is showing
     * the elements from.
     */
    private GraphDocument doc;

    /**
     * A class that encapsulates all functions to update
     * the model browser on change events. This is the
     * {@link LGMChangeListener}.
     */
    private final ModelBrowserTreeLGMChangeListener transactionListener;

    /**
     * Transaction id for all changes of this tree (always default id).
     */
    public static final int PID = STANDARD_PID;

    /** Rererence to the {@link TreeModel} of this tree as {@link DefaultTreeModel} */
    private final DefaultTreeModel myModel;

    /** The {@link TreePath} with root as last path element. */
    private final TreePath rootPath;

    /**
     * Maps from a element class to the treenode which has to be only filled with
     * instance children and added to the model browser if the tool is in enpert mode.
     */
    private final Map<Class<? extends ModelElement>, ElementClassTreeNode> elementClassToParentNode = new HashMap<>();

    /**
     * Contains the class nodes where the instances of model elements resp. the
     * element container are the children. In every rebuild of the tree all
     * children of these nodes are removed and new generated.
     */
    private final Collection<ElementClassTreeNode> nodesToClear;

    /** Caches the value of {@link BooleanProperty#OPTION_SHOW_PART_OF_HIERARCHY} */
    private static boolean showPartOfHierarchy = false;

    /** Caches the value of {@link BooleanProperty#OPTION_SHOW_USER_DEFINED_PROPERTIES_IN_MODEL_BROWSER} */
    private static boolean showUserDefinedProperties = false;

    /**
     * @param doc the {@link GraphDocument} this model sbrowser should display
     */
    public ModelBrowserTree(final GraphDocument doc) {
        super(new DefaultTreeModel(new StringTreeNode(Tool3lgmConstants.getResString("MODEL_BRWOSER_TITLE"), false)));
        this.doc = doc;
        initTree();
        nodesToClear = elementClassToParentNode.values();
        rootPath = getRootPath();
        //alle KeyStrokes im Baum hinzufügen, die systemweit gelten sollen. Da der Baum schon eine eigene InputMap und ActionMap hat,
        //werden die ShortCuts aus dem RootPane des Tools hier nicht auch beachtet und müssen explizit hinzugefügt werden
        KeyStrokes.registerPublicKeyStrokes(this);
        transactionListener = new ModelBrowserTreeLGMChangeListener(this);
        setCellRenderer(new TreeRenderer());
        ((TreeRenderer) getCellRenderer()).setBackgroundNonSelectionColor(getBackground());
        setBackground(getBackground());
        setEditable(false);
        putClientProperty("JTree.lineStyle", "Angled");
        setToggleClickCount(-1);
        setRootVisible(false);
        setShowsRootHandles(true);
        buildTree();
        myModel = (DefaultTreeModel) getModel();
        //Expand the 3 layer nodes
        //        expandRow(2);
        //        expandRow(1);
        //        expandRow(0);
    }

    /**
     * @param iconIdentifier
     * @return
     */
    private static ImageIcon getLayerIcon(final Enum<?> iconIdentifier) {
        ImageIcon icon = Tool3lgmConstants.getLargeIcon(iconIdentifier);
        //icon = new ImageIcon(icon.getImage().getScaledInstance(14, 14, Image.SCALE_SMOOTH));
        return icon;
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
     * Setzt das übergebene {@link GraphDocument} für diesen Baum und fügt den Baum als {@link LGMChangeListener} hinzu. Beim vorherigen
     * {@link GraphDocument} des Baumes wird der Baum als Listener entfernt.
     *
     * @param doc
     */
    void setGraphDocument(final GraphDocument doc) {
        if (this.doc != null) {
            transactionListener.remove();
            this.doc = doc;
        }
        if (doc != null) {
            transactionListener.add();
        }
        buildTree();
    }

    @Override
    public ContextGenerator getContextGenerator() {
        return Static.contextGenerator;
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

    /**
     * @param layerNode
     * @param treeLayerVisibleAbstractNodes
     * @param treeLayerVisibleInstancialeNodes
     */
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
            ElementClassTreeNode instanciableClassNode = new ElementClassTreeNode(elementClass, label, false); // muss nicht selbst sortieren, weil die Elemente bereits sortiert reinkommen
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

    @Override
    public boolean isLayerNode(final Object o) {
        return o == domainLayer || o == logicalLayer || o == physicalLayer;
    }

    @Override
    void setTransactionListenerActive(final boolean active) {
        transactionListener.setActive(active);
    }

    private final Map<DefaultMutableTreeNode, DefaultMutableTreeNode> visibilityRestrictedNodesToParent = new HashMap<>();

    /**
     * Der allgemeine Baum wird erzeugt oder zurueckgesetzt
     */
    private void createTree() {
        //alle Knoten entfernen/einblenden, die nicht/nur im ExpertMode zu sehen sein sollen
        MetaModel metaModel = doc.getMetaModel();
        Set<Class<? extends ModelElement>> onlyExpertModeVisibleNodes = metaModel.getOnlyExpertModeVisibleNodes();
        Set<Class<? extends ModelElement>> pureTemplateElementClasses = metaModel.getPureTemplateElementClasses();
        addOrRemoveVisibilityRestrictedElementClassNodes(onlyExpertModeVisibleNodes, OPTION_ENABLE_EXPERT_MODE, null);
        addOrRemoveVisibilityRestrictedElementClassNodes(pureTemplateElementClasses, OPTION_ENABLE_EXPERT_MODE, OPTION_SHOW_TEMPLATE_ELEMENTS_IN_MODEL_BROWSER);

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
     * @param visibilityRestrictedElementClasses
     * @param property1
     * @param property2
     */
    private void addOrRemoveVisibilityRestrictedElementClassNodes(final Iterable<Class<? extends ModelElement>> visibilityRestrictedElementClasses, final BooleanProperty property1, final BooleanProperty property2) {
        boolean showRestrictedNodes = property1.is() || property2 != null && property2.is();
        for (Class<? extends ModelElement> visibilityRestrictedElementClass : visibilityRestrictedElementClasses) {
            LGMTreeNode node = elementClassToParentNode.get(visibilityRestrictedElementClass);
            if (node != null) {
                if (showRestrictedNodes) {
                    DefaultMutableTreeNode parent = visibilityRestrictedNodesToParent.remove(node);
                    if (parent != null) {
                        parent.add(node);
                    }
                } else {
                    DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
                    if (parent != null) {
                        visibilityRestrictedNodesToParent.put(node, parent);
                        parent.remove(node);
                    }
                }
            }
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
        GraphDocument maindoc = doc.getCollection().getMainDoc();
        boolean isEnableSubmodelBrowser = OPTION_ENABLE_SUBMODEL_BROWSER.is();
        List<ElementContainer> all = kc.getElement().getDirectPartContainers(isEnableSubmodelBrowser ? selDoc : maindoc);
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
                List<ElementContainer> directParentElements = me.getDirectParentContainers(isEnableSubmodelBrowser ? selDoc : maindoc);
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

    /**
     *
     */
    void buildTree() {
        if (doc == null) {
            return;
        }
        GraphDocument maindoc = doc.getCollection().getMainDoc();
        setSelectionListenerActive(false);
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
        setSelectionListenerActive(true);
        selectObjects();
    }

    /**
     * Selektiert im Baum alle Elemente, die im dazugehörigen {@link GraphDocument} selektiert sind.
     */
    public void selectObjects() {
        setSelectionListenerActive(false);
        TreePath[] path = new TreePath[doc.getSelectedRealElementContainerCount()];
        int m = 0;
        GraphDocument mainDoc = doc.getCollection().getMainDoc();
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
        setSelectionListenerActive(true);
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
    @Override
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
    Enumeration<TreePath> expandedPaths = null;

    private void saveExpansionState() {
        expandedPaths = getExpandedDescendants(rootPath);
    }

    public final void restoreExpansionState() {
        setExpandedPaths(expandedPaths);
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
