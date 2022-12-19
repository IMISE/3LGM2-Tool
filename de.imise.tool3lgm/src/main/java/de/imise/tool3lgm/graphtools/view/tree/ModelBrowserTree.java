package de.imise.tool3lgm.graphtools.view.tree;

import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.MAX_LAYER_INDEX;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.MIN_LAYER_INDEX;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.isInterLayer;
import static de.imise.tool3lgm.graphtools.undoredo.TransactionManager.STANDARD_PID;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.FORMULA;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.GROUP;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.SEPARATOR;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.TAB;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_ENABLE_EXPERT_MODE;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_ENABLE_SUBMODEL_BROWSER;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SHOW_PART_OF_HIERARCHY;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SHOW_TEMPLATE_ELEMENTS_IN_MODEL_BROWSER;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SHOW_USER_DEFINED_PROPERTIES_IN_MODEL_BROWSER;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SUBORDINATE_COMPOSITION_ELEMENTS_IN_MODEL_BROWSER;

import java.awt.Image;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import de.imise.tool3lgm.KeyStrokes;
import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.event.ActionIdentifier;
import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.CoreMetaModel;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.CompositionEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Group;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.GraphDocumentOwner;
import de.imise.tool3lgm.graphtools.model.LGMChangeListener;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.userfield.definition.UserField;
import de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldDefinitions;
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
import de.imise.util.ReflectionUtils;

/**
 * A tree view to the model. Under the root node are the layer nodes, then for
 * every layer node the class nodes and then the instances with optional child
 * nodes for the user defined properties.
 *
 * @author N.N.
 */
public final class ModelBrowserTree extends DynamicTree implements UserFieldListener, GraphDocumentOwner {

    /**
     * A list for all visible layer nodes. This list contains the layer nodes
     * exact at the position of the layer index (defined in
     * {@link ModelConstants#LAYERS}.
     */
    private final List<StringTreeNode> layerNodes = new ArrayList<>();

    /**
     * For every layer node and every element type which is represented on all
     * layers this stores the element node on which instances of the element
     * type are added.
     */
    private final Table<StringTreeNode, Class<? extends ModelElement>, ElementClassTreeNode> everyLayerElementTypeNodes = HashBasedTable.create();

    /**
     * The {@link GraphDocument} this model browser is showing the elements
     * from.
     */
    private GraphDocument doc;

    /**
     * A class that encapsulates all functions to update the model browser on
     * change events. This is the {@link LGMChangeListener}.
     */
    private final ModelBrowserTreeLGMChangeListener transactionListener;

    /**
     * Transaction id for all changes of this tree (always default id).
     */
    public static final int PID = STANDARD_PID;

    /**
     * Rererence to the {@link TreeModel} of this tree as
     * {@link DefaultTreeModel}
     */
    private final DefaultTreeModel myModel;

    /** The {@link TreePath} with root as last path element. */
    private final TreePath rootPath;

    /**
     * Maps from a element class to the treenode which has to be only filled
     * with instance children and added to the model browser if the tool is in
     * enpert mode.
     */
    private final Map<Class<? extends ModelElement>, LGMTreeNode<Class<? extends ModelElement>>> elementClassToParentNode = new HashMap<>();

    /**
     * Contains the class nodes where the instances of model elements resp. the
     * element container are the children. In every rebuild of the tree all
     * children of these nodes are removed and new generated.
     */
    private final Collection<LGMTreeNode<Class<? extends ModelElement>>> nodesToClear;

    /**
     * Caches the value of {@link BooleanProperty#OPTION_SHOW_PART_OF_HIERARCHY}
     */
    private static boolean showPartOfHierarchy = false;

    /**
     * Caches the value of
     * {@link BooleanProperty#OPTION_SHOW_USER_DEFINED_PROPERTIES_IN_MODEL_BROWSER}
     */
    private static boolean showUserDefinedProperties = false;

    /**
     * Caches the value of
     * {@link BooleanProperty#OPTION_SUBORDINATE_COMPOSITION_ELEMENTS_IN_MODEL_BROWSER}
     */
    private static boolean subordinateSlaveElements = false;

    /** Caches the value of {@link BooleanProperty#OPTION_ENABLE_EXPERT_MODE} */
    private static boolean showExpertModeOnlyVisisbleElements = false;

    /**
     * Caches the value of
     * {@link BooleanProperty#OPTION_SHOW_TEMPLATE_ELEMENTS_IN_MODEL_BROWSER}
     */
    private static boolean showTemplateElements = false;

    /**
     * Caches the value of
     * {@link BooleanProperty#OPTION_ENABLE_SUBMODEL_BROWSER}
     */
    private static boolean showSubmodelInBrowser = false;

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
        for (int i = getRowCount() - 1; i >= 0; i--) {
            expandRow(i);
        }
    }

    /**
     * @param iconIdentifier
     * @return
     */
    private static ImageIcon getLayerIcon(final Enum<?> iconIdentifier) {
        ImageIcon icon = Tool3lgmConstants.getSmallIcon(iconIdentifier);
        icon = new ImageIcon(icon.getImage().getScaledInstance(14, 14, Image.SCALE_SMOOTH));
        return icon;
    }

    /**
     * @return
     */
    @Override
    public final GraphDocument getGraphDocument() {
        return doc;
    }

    @Override
    public GraphDocument getGraphDocument(final MouseEvent e) {
        return doc; //this Broser always shows only one GraphDocument so the mouse position is irrelevant
    }

    /**
     * @return
     */
    @Override
    public final GDCollection getCollection() {
        return getGraphDocument().getCollection();
    }

    /**
     *
     */
    void updateSelectedDoc() {
        LGMGraphDocument gdcollSelectedDoc = getSelectedDoc();
        if (doc != gdcollSelectedDoc) {
            setGraphDocument(gdcollSelectedDoc);
        }
    }

    /**
     * Setzt das übergebene {@link GraphDocument} für diesen Baum und fügt den
     * Baum als {@link LGMChangeListener} hinzu. Beim vorherigen
     * {@link GraphDocument} des Baumes wird der Baum als Listener entfernt.
     *
     * @param doc
     */
    private void setGraphDocument(final GraphDocument doc) {
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

    /**
     * @param classes
     */
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
     * @param metaModel
     * @param layerNode
     * @param treeLayerVisibleAbstractNodes
     * @param treeLayerVisibleInstancialeNodes
     */
    private final void initLayer(final MetaModel metaModel, final StringTreeNode layerNode, final Class<? extends ModelElement>[] treeLayerVisibleAbstractNodes, final Iterable<Class<? extends ModelElement>> treeLayerVisibleInstancialeNodes) {
        //die abstracten Klassen holen, die in der Hierarchie des Baumes unterhalb des Layer-Knotens angezeigt werden sollen
        List<Class<? extends ModelElement>> abstractClasses = new ArrayList<>(Arrays.asList(treeLayerVisibleAbstractNodes));
        //diese Liste so sortieren, dass sichergestellt ist, dass alle Klassen die in der Liste vorkommen, die Unterklasse einer anderen
        //Klasse in der Liste sind, immer nach dieser Oberklasse in der Liste stehen
        sortedByAssignable(abstractClasses);
        //Liste von Knoten für die abstrakten Klassen in derselben Reihenfolge wie die abstrakten Klassen
        List<ElementClassTreeNode> abstractClassNodes = new ArrayList<>();
        ElementsNameBuilder elementsNameBuilder = doc.getElementsNameBuilder();
        for (Class<? extends ModelElement> abstractClass : abstractClasses) {
            String label = elementsNameBuilder.getDisplayablePluralFullName(abstractClass);
            abstractClassNodes.add(new ElementClassTreeNode(abstractClass, label));
        }
        //jetzt die ElementKnoten unter die abstrakten Knoten hängen oder unter den LayerKnoten selbst, wenn es keinen abstakten Oberklassenkoten gibt
        for (Class<? extends ModelElement> elementClass : treeLayerVisibleInstancialeNodes) {
            String label = elementsNameBuilder.getDisplayablePluralFullName(elementClass);
            ElementClassTreeNode instanciableClassNode = new ElementClassTreeNode(elementClass, label, false); // muss nicht selbst sortieren, weil die Elemente bereits sortiert reinkommen
            elementClassToParentNode.put(elementClass, instanciableClassNode);
            boolean superClassFound = false;
            for (int i = abstractClasses.size() - 1; i >= 0; i--) {
                Class<? extends ModelElement> abstractClass = abstractClasses.get(i);
                if (abstractClass.isAssignableFrom(elementClass)) {
                    ElementClassTreeNode abstractClassNode = abstractClassNodes.get(i);
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
            ElementClassTreeNode potencialAbstractSubClassNode = abstractClassNodes.get(i);
            boolean superClassFound = false;
            //alle Klassen vor der aktuellen Klasse durchsuchen, ob es eine Oberklasse der aktuellen Klasse ist. Wenn ja -> hönge den Knoten der Klasse unter
            for (int j = i - 1; j >= 0; j--) {
                Class<? extends ModelElement> potencialAbstractSuperClass = abstractClasses.get(j);
                if (potencialAbstractSuperClass.isAssignableFrom(potencialAbstractSubClass)) {
                    ElementClassTreeNode potencialAbstractSuperClassNode = abstractClassNodes.get(j);
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

    /**
     * Inits the top level nodes for the layers and adds them to the root.
     */
    private void initTree() {
        MetaModel metaModel = doc.getMetaModel();
        for (int layer : ModelConstants.VISIBLE_LAYERS) {
            while (layerNodes.size() < layer + 1) { // every layer node should be present at the position of the layer index
                layerNodes.add(null);
            }
            String layerNodeDisplayText = ModelConstants.getVisibleLayerName(layer);
            ActionIdentifier activateLayerActionIdentifier = ActionIdentifier.getActivateLayerActionIdentifier(layer);
            ImageIcon layerIcon = getLayerIcon(activateLayerActionIdentifier);
            StringTreeNode layerNode = new StringTreeNode(layerNodeDisplayText, layerIcon);
            layerNodes.set(layer, layerNode);
            ((DefaultMutableTreeNode) treeModel.getRoot()).add(layerNode);
            Iterable<Class<? extends ModelElement>> treeVisibleNodesForLayer = metaModel.getTreeVisibleNodesForLayer(layer);
            initLayer(metaModel, layerNode, metaModel.getTreeDomainLayerVisibleAbstractNodes(), treeVisibleNodesForLayer);
        }
    }

    @Override
    public boolean isLayerNode(final Object o) {
        return layerNodes.contains(o);
    }

    @Override
    void setTransactionListenerActive(final boolean active) {
        transactionListener.setActive(active);
    }

    /**
     *
     */
    private final Map<DefaultMutableTreeNode, DefaultMutableTreeNode> visibilityRestrictedNodesToParent = new HashMap<>();

    /**
     * Der allgemeine Baum wird erzeugt oder zurueckgesetzt
     */
    private void createTree() {
        //alle Knoten entfernen/einblenden, die nicht/nur im ExpertMode zu sehen sein sollen
        MetaModel metaModel = doc.getMetaModel();
        Set<Class<? extends ModelElement>> onlyExpertModeVisibleNodes = metaModel.getOnlyExpertModeVisibleNodes();
        Set<Class<? extends ModelElement>> pureTemplateElementClasses = metaModel.getPureTemplateElementClasses();
        List<Class<? extends Node>> compositionSlaveNodes = metaModel.getCompositionSlaveNodes();
        Set<Class<? extends ModelElement>> alreadyVisibilityRestrictedElementClasses = new HashSet<>();
        addOrRemoveVisibilityRestrictedElementClassNodes(onlyExpertModeVisibleNodes, alreadyVisibilityRestrictedElementClasses, OPTION_ENABLE_EXPERT_MODE.is(), null);
        addOrRemoveVisibilityRestrictedElementClassNodes(pureTemplateElementClasses, alreadyVisibilityRestrictedElementClasses, OPTION_ENABLE_EXPERT_MODE.is(), OPTION_SHOW_TEMPLATE_ELEMENTS_IN_MODEL_BROWSER.is());
        addOrRemoveVisibilityRestrictedElementClassNodes(compositionSlaveNodes, alreadyVisibilityRestrictedElementClasses, !OPTION_SUBORDINATE_COMPOSITION_ELEMENTS_IN_MODEL_BROWSER.is(), null);

        for (LGMTreeNode<?> node : nodesToClear) {
            node.removeAllChildren();
        }
        for (ElementClassTreeNode elementClassTreeNode : everyLayerElementTypeNodes.values()) {
            DefaultMutableTreeNode layerNode = (DefaultMutableTreeNode) elementClassTreeNode.getParent(); // should be a layer node
            if (layerNode != null) {
                layerNode.remove(elementClassTreeNode);
            }
        }
    }

    /**
     * @param visibilityRestrictedElementClasses
     * @param alreadyVisibilityRestrictedElementClasses
     * @param property1
     * @param property2
     * @return all hidden elementClasses
     */
    private <T extends ModelElement> void addOrRemoveVisibilityRestrictedElementClassNodes(final Iterable<Class<? extends T>> visibilityRestrictedElementClasses, final Set<Class<? extends ModelElement>> alreadyVisibilityRestrictedElementClasses,
            final Boolean property1, final Boolean property2) {
        boolean showRestrictedNodes = property1 || property2 != null && property2;
        for (Class<? extends ModelElement> visibilityRestrictedElementClass : visibilityRestrictedElementClasses) {
            LGMTreeNode<?> node = elementClassToParentNode.get(visibilityRestrictedElementClass);
            if (node != null) {
                if (showRestrictedNodes && !alreadyVisibilityRestrictedElementClasses.contains(visibilityRestrictedElementClass)) {
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
                    alreadyVisibilityRestrictedElementClasses.add(visibilityRestrictedElementClass);
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
                elementNode = ElementContainerTreeNode.createModelBrowserTreeNode(kc);
            } else {
                elementNode.removeAllChildren();
            }
            LGMTreeNode<?> parent_node = getParentNodeOfType(kc, layer);

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
        if (subordinateSlaveElements) {
            addCompositionSlaves(elementNode, selDoc);
        }
        if (showUserDefinedProperties) {
            addUserDefinedProperties(elementNode, selDoc);
        }
        ElementContainer ec = elementNode.getUserObject();
        ModelElement me = ec.getElement();
        if (!showPartOfHierarchy && !(me instanceof Group)) {
            return;
        }
        LGMTreeNode<?> parent = (LGMTreeNode<?>) elementNode.getParent();
        GraphDocument maindoc = doc.getCollection().getMainDoc();
        List<ElementContainer> allPartContainers = me.getDirectPartContainers(showSubmodelInBrowser ? selDoc : maindoc);
        loop1: for (ElementContainer partContainer : allPartContainers) {
            ModelElement part = partContainer.getElement();
            ElementContainer selDocPartContainer = part.getContainer(selDoc);
            if (selDocPartContainer != null) {
                partContainer = selDocPartContainer;
            }
            if (showSubmodelInBrowser) {
                if (!part.isUnique() && selDocPartContainer == null) {
                    continue;
                }
            }
            LGMTreeNode<?> p = parent;
            while (p != null) {
                if (p.getUserObject() == partContainer) {
                    continue loop1;
                }
                p = (LGMTreeNode<?>) p.getParent();
            }
            ElementContainerTreeNode childNode = null;
            //with NodeContainers the possibly already existing TreeNodes are reused
            if (partContainer instanceof NodeContainer) {
                List<ElementContainer> directParentContainers = part.getDirectParentContainers(showSubmodelInBrowser ? selDoc : maindoc);
                // If there is more than one parent, then simply recreate all nodes. The case is rare, but
                // then possibly previously expanded nodes will no longer be expanded. The alternative would
                // be to remember all nodes in the ElementContaier instead of just one. I think this is not
                // necessary, because this would save the expansionState of all nodes that have more than one
                // parent only in this rare case.
                int directParentCount = directParentContainers.size();
                if (directParentCount == 0 || directParentCount == 1 && !part.hasParent(Group.class)) { // the only parent ca be not a group but some parent of parent can be
                    NodeContainer nc = (NodeContainer) partContainer;
                    childNode = nc.getTreeNode();
                }
            }
            if (childNode == null) {
                childNode = ElementContainerTreeNode.createModelBrowserTreeNode(partContainer, false);
            } else {
                childNode.removeAllChildren();
            }
            elementNode.add(childNode);
            addChildren(childNode, selDoc);
        }
    }

    /**
     * @param elementTreeNode
     * @param selDoc
     */
    private void addCompositionSlaves(final ElementContainerTreeNode elementTreeNode, final GraphDocument selDoc) {
        ElementContainer ec = elementTreeNode.getUserObject();
        ModelElement me = ec.getElement();
        Class<? extends ModelElement> elementClass = me.getClass();
        MetaModel metaModel = selDoc.getMetaModel();
        Class<? extends CompositionEdge>[] compositionEdgeTypesForMaster = metaModel.getCompositionEdgeTypesForMaster(elementClass);
        GraphDocument doc = OPTION_ENABLE_SUBMODEL_BROWSER.is() ? selDoc : selDoc.getMainDoc();
        //        hier dürfen onlyExpertModeVsisibleNodes nicht hinzugefügt werden, wenn der ExpertMode aus ist
        //        und hier dürfen TemplateElemente nicht hinzugefügt werden, wenn diese gerade nicth angezeigt werden sollen
        for (Class<? extends CompositionEdge> composition : compositionEdgeTypesForMaster) {
            List<ElementContainer> slaveContainers = me.getConnectedContainers(doc, composition);
            for (ElementContainer slaveContainer : slaveContainers) {
                ModelElement slave = slaveContainer.getElement();
                ElementContainerTreeNode slaveTreeNode = null;
                //Don't subordinate element classes to hide (expert mode only visible
                //nodes and template elements).
                //this must be checked for every single element because the composition
                //slave class can be abstract and one instanciable, assignable class of
                //of this slave class can be an expert mode only visible element class
                //and another not. Same with template element classes.
                if (!showExpertModeOnlyVisisbleElements) {
                    Class<? extends ModelElement> slaveClass = slave.getClass();
                    if (metaModel.isOnlyExpertModeVisibleElementClass(slaveClass)) {
                        continue;
                    }
                    if (!showTemplateElements) {
                        if (metaModel.isPureTemplateElementClass(slaveClass)) {
                            continue;
                        }
                    }
                }
                if (slaveContainer instanceof NodeContainer) {
                    List<ElementContainer> directParentContainers = slave.getDirectCompositionMasterContainer(selDoc);
                    // If there is more than one parent, then simply recreate all nodes. The case is rare, but
                    // then possibly previously expanded nodes will no longer be expanded. The alternative would
                    // be to remember all nodes in the ElementContaier instead of just one. I think this is not
                    // necessary, because this would save the expansionState of all nodes that have more than one
                    // parent only in this rare case.
                    int directParentCount = directParentContainers.size();
                    // very ugly code but in most cases we don't need the second OR part and so we should not
                    // execute the expensive hasPart(...) function before the ID
                    if (directParentCount == 0 || directParentCount == 1 && !directParentContainers.get(0).getElement().hasParent(Group.class)) {
                        NodeContainer slaveNodeContainer = (NodeContainer) slaveContainer;
                        slaveTreeNode = slaveNodeContainer.getTreeNode();
                    }
                }
                if (slaveTreeNode == null) {
                    slaveTreeNode = ElementContainerTreeNode.createModelBrowserTreeNode(slaveContainer);
                } else {
                    slaveTreeNode.removeAllChildren();
                }
                elementTreeNode.add(slaveTreeNode);
                addChildren(slaveTreeNode, selDoc);
            }

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
        GDCollection gdcoll = doc.getCollection();
        UserFieldDefinitions ufDefs = gdcoll.getUserFieldDefinitions();
        UserFieldTreeNode currentTabNode = null;
        UserFieldTreeNode currentGroupNode = null;
        UserFieldTreeNode valueOrSeparatorNode = null;
        for (UserField userField : ufDefs.getUserFields(me)) {
            if (userField.hasStyle(TAB)) {
                removeNodeIfEmpty(currentGroupNode); //remove empty group
                removeNodeIfEmpty(currentTabNode); //remove empty tab
                if (userField.isTreeVisibility()) {
                    currentTabNode = new UserFieldTreeNode(userField, me);
                } else {
                    currentTabNode = null;
                }
                currentGroupNode = null;
                valueOrSeparatorNode = null;
            } else if (userField.hasStyle(GROUP)) {
                removeNodeIfEmpty(currentGroupNode); //remove empty group
                currentGroupNode = userField.isTreeVisibility() ? new UserFieldTreeNode(userField, me) : null;
                valueOrSeparatorNode = null;
            } else {
                boolean add = userField.hasStyle(SEPARATOR) || allOfThisElement.contains(userField);
                add = add || userField.hasStyle(FORMULA) && !me.hasEmptyValueOrError(userField);
                if (add) {
                    valueOrSeparatorNode = userField.isTreeVisibility() ? new UserFieldTreeNode(userField, me) : null;
                }
            }
            LGMTreeNode<?> parent = null;
            LGMTreeNode<?> child = null;
            if (valueOrSeparatorNode != null) {
                parent = currentGroupNode != null ? currentGroupNode : currentTabNode != null ? currentTabNode : elementNode;
                child = valueOrSeparatorNode;
            } else if (currentGroupNode != null) {
                parent = currentTabNode != null ? currentTabNode : elementNode;
                child = currentGroupNode;
            } else if (currentTabNode != null) {
                parent = elementNode;
                child = currentTabNode;
            } else {
                continue; // should never happen
            }
            parent.add(child);
        }
        removeNodeIfEmpty(currentGroupNode); //remove empty group
        removeNodeIfEmpty(currentTabNode); //remove empty tab

    }

    /**
     * @param node
     * @return <code>true</code> if the node can be removed. This is the case if
     *         the node is a group node or tab node and has no
     */
    private void removeNodeIfEmpty(final UserFieldTreeNode node) {
        if (node == null) {
            return;
        }
        //remove empty groups or tabs. they are also empty if they contain only seperators
        int childCount = node.getChildCount();
        for (int i = 0; i < childCount; i++) {
            UserFieldTreeNode child = (UserFieldTreeNode) node.getChildAt(i);
            if (!child.hasStyle(Style.SEPARATOR)) {
                return;
            }
        }
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
        parent.remove(node);
    }

    /**
     * @param objekt
     */
    public void removeObject(final ElementContainer ec) {
        if (ec instanceof NodeContainer) {
            DefaultTreeModel model = (DefaultTreeModel) treeModel;
            NodeContainer nc = (NodeContainer) ec;
            LGMTreeNode<?> node = nc.getTreeNode();
            model.removeNodeFromParent(node);
            model.reload();
        }
    }

    /**
     * @param obj
     * @return
     */
    public static LGMTreeNode<?> getParentNodeOf(final NodeContainer obj) {
        if (obj == null) {
            return null;
        }
        ElementContainerTreeNode treeNode = obj.getTreeNode();
        if (treeNode == null) {
            return null;
        }
        return (LGMTreeNode<?>) treeNode.getParent();
    }

    /**
     * @param layerNode
     * @param elementClassNode
     * @param elementClass
     * @return
     */
    private ElementClassTreeNode getOrCreateMetamodelIndependentElementClassNode(final StringTreeNode layerNode, ElementClassTreeNode elementClassNode, Class<? extends ModelElement> elementClass) {
        if (elementClassNode == null) {
            ElementsNameBuilder elementsNameBuilder = doc.getElementsNameBuilder();
            String label = elementsNameBuilder.getDisplayablePluralName(elementClass);
            elementClassNode = new ElementClassTreeNode(elementClass, label, false);
        }
        if (elementClassNode.getParent() == null) {
            elementClassNode.removeAllChildren();
            layerNode.add(elementClassNode);
        }
        return elementClassNode;
    }

    /**
     * @param obj
     * @param layer
     * @return
     */
    private LGMTreeNode<?> getParentNodeOfType(final NodeContainer obj, final int layer) {
        Node me = (Node) obj.getElement();
        Class<? extends ModelElement> elementClass = me.getClass();
        if (CoreMetaModel.META_MODEL_INDEPENDENT_ELEMENT_TYPES.contains(elementClass)) {
            StringTreeNode layerNode = layerNodes.get(layer);
            ElementClassTreeNode elementClassTreeNode = everyLayerElementTypeNodes.get(layerNode, elementClass);
            elementClassTreeNode = getOrCreateMetamodelIndependentElementClassNode(layerNode, elementClassTreeNode, elementClass);
            everyLayerElementTypeNodes.put(layerNode, elementClass, elementClassTreeNode);
            return elementClassTreeNode;
        }
        return elementClassToParentNode.get(me.getClass());
    }

    /**
     * @param me
     * @param doc
     * @return <code>true</code> if the {@link NodeContainer} should be only
     *         added as child in part of hierarchies
     */
    private boolean addOnlyAsPart(ModelElement me, GraphDocument doc) {
        if (showPartOfHierarchy) {
            List<ElementContainer> parents = me.getDirectParentContainers(doc);
            // Groups with parents ( = Groups) should never added unter its
            // Class node, if it is a child.
            if (!parents.isEmpty() && me.is(Group.class)) {
                return true;
            }
            for (ElementContainer parent : parents) {
                ModelElement parentElement = parent.getElement();
                // If there is any other parent than a Group -> the element will
                // be only added as child. If not, it will be added as group child
                // and also under its class node.
                if (!ReflectionUtils.isAssignable(parentElement.getClass(), Group.class)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param me
     * @param doc
     * @return <code>true</code> if the element should be only added under its
     *         master
     */
    private boolean addOnlyAsSlave(ModelElement me, GraphDocument doc) {
        return subordinateSlaveElements && me.isCompositionSlave(doc);
    }

    /**
     * @param me
     * @param doc
     * @return <code>true</code> if the element should not added as indipendent
     *         element, but only under its parent or master
     */
    private boolean addOnlyAsSubordinatedElement(ModelElement me, GraphDocument doc) {
        return addOnlyAsPart(me, doc) || addOnlyAsSlave(me, doc);
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
        showUserDefinedProperties = OPTION_SHOW_USER_DEFINED_PROPERTIES_IN_MODEL_BROWSER.is();
        subordinateSlaveElements = OPTION_SUBORDINATE_COMPOSITION_ELEMENTS_IN_MODEL_BROWSER.is();
        showExpertModeOnlyVisisbleElements = OPTION_ENABLE_EXPERT_MODE.is();
        showTemplateElements = OPTION_SHOW_TEMPLATE_ELEMENTS_IN_MODEL_BROWSER.is();
        showSubmodelInBrowser = OPTION_ENABLE_SUBMODEL_BROWSER.is();

        for (int ebene = MAX_LAYER_INDEX; ebene >= MIN_LAYER_INDEX; ebene--) {
            if (isInterLayer(ebene)) {
                continue;
            }
            for (NodeContainer nc : maindoc.getLayer(ebene).getNodeContainersAlphabetical()) {
                ModelElement me = nc.getElement();
                if (showSubmodelInBrowser) {
                    if (me.isUnique()) {
                        if (addOnlyAsSubordinatedElement(me, maindoc)) {
                            continue;
                        }
                    } else if (me.getContainer(doc) == null || addOnlyAsSubordinatedElement(me, doc)) {
                        continue;
                    }
                } else if (addOnlyAsSubordinatedElement(me, maindoc)) {
                    continue;
                }
                addObject(nc, true, doc, ebene);
            }
        }
        ((DefaultTreeModel) treeModel).reload();
        restoreExpansionState();
        setSelectionListenerActive(true);
        selectObjects(null);
    }

    /**
     * Selects all elements in the tree which are selected in the corresponding
     * {@link GraphDocument}.
     */
    public void selectObjects(ElementContainer container2Select) {
        if (container2Select == null) {
            DefaultTreeModel treeModel = (DefaultTreeModel) super.treeModel;
            setSelectionListenerActive(false);
            List<TreePath> selectedPaths = new ArrayList<>();
            List<DefaultMutableTreeNode> nodes = new ArrayList<>();
            nodes.add((DefaultMutableTreeNode) treeModel.getRoot());
            for (int i = 0; i < nodes.size(); i++) {
                DefaultMutableTreeNode treeNode = nodes.get(i);
                for (int j = 0; j < treeNode.getChildCount(); j++) {
                    DefaultMutableTreeNode child = (DefaultMutableTreeNode) treeNode.getChildAt(j);
                    nodes.add(child);
                    Object userObject = child.getUserObject();
                    if (userObject instanceof ElementContainer) {
                        if (doc.isSelected((ElementContainer) userObject)) {
                            TreeNode[] pathToRoot = treeModel.getPathToRoot(child);
                            selectedPaths.add(new TreePath(pathToRoot));
                        }
                    }
                }
            }
            TreePath[] paths = selectedPaths.toArray(new TreePath[0]);
            setSelectionPaths(paths);
            scrollToPath(paths);
            setSelectionListenerActive(true);
        }
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
        for (LGMTreeNode<?> nodeToRefresh : nodesToClear) {
            refreshNode(nodeToRefresh);
        }
    }

    /**
     * Wenn die Layer-Nummer gültig ist, wird der zugehörige Ebenenknoten
     * selektiert und ggf. zu ihm hingescollt.
     *
     * @param layer
     */
    public void selectLayerNode(final int layer) {
        StringTreeNode layerNode = layerNodes.get(layer);
        TreePath path = new TreePath(((DefaultTreeModel) treeModel).getPathToRoot(layerNode));
        setSelectionPath(path);
        scrollPathToVisible(path);
    }

    /**
     * Über diese Funktion kann der {@link DynamicTreeSelectionListener} den
     * Layer wechseln, wenn ein Layerknoten im Baum selektiert wurde.
     *
     * @param node
     * @return
     */
    @Override
    public boolean setActiveLayer(final LGMTreeNode<?> node) {
        int index = layerNodes.indexOf(node);
        boolean layerChanged = false;
        if (index >= 0) {
            layerChanged = true;
            doc.getCollection().setActiveLayer(index);
        }
        return layerChanged;
    }

    //	----------------------------------------------------------------------------------------------------------------------------------

    /**
     *
     */
    Enumeration<TreePath> expandedPaths = null;

    /**
     *
     */
    private void saveExpansionState() {
        expandedPaths = getExpandedDescendants(rootPath);
    }

    /**
     *
     */
    public final void restoreExpansionState() {
        setExpandedPaths(expandedPaths);
    }

    ///////////////////////
    // UserFieldListener //
    ///////////////////////
    @Override
    public void userFieldAdded() {
        if (OPTION_SHOW_USER_DEFINED_PROPERTIES_IN_MODEL_BROWSER.is()) {
            buildTree();
        }
    }

    @Override
    public void userFieldRemoved() {
        if (OPTION_SHOW_USER_DEFINED_PROPERTIES_IN_MODEL_BROWSER.is()) {
            buildTree();
        }
    }

    @Override
    public void userFieldValueChanged() {
        if (OPTION_SHOW_USER_DEFINED_PROPERTIES_IN_MODEL_BROWSER.is()) {
            refreshTree();
        }
    }

}
