package de.imise.tool3lgm.graphtools.view.pathtree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.tree.DefaultTreeModel;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelDefinition;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelSpecific;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.path.metapaths.ElementaryMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.MetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.MetaPathFunctions;
import de.imise.tool3lgm.graphtools.path.metapaths.SequenceMetaPath;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.tree.node.ElementClassTreeNode;
import de.imise.tool3lgm.graphtools.view.tree.node.ElementContainerTreeNode;
import de.imise.tool3lgm.graphtools.view.tree.node.IconifiedTreeNode;
import de.imise.tool3lgm.graphtools.view.tree.node.LGMTreeNode;
import de.imise.tool3lgm.graphtools.view.tree.node.PathStepTreeNode;
import de.imise.tool3lgm.graphtools.view.tree.node.StringTreeNode;
import de.imise.util.BooleanOption;

/**
 * A TreeModel that builds itself according to its underlying
 * PathTreeDefinition.
 *
 * @author AXS (01.09.2019)
 */
public abstract class PathTreeModel extends DefaultTreeModel implements MetaModelSpecific {

    /** Definiton of the branches of the tree */
    private PathTreeDefinition treeDefinition;

    /** root node as {@link LGMTreeNode} */
    private final IconifiedTreeNode<?> root;

    /** Info that is displayed if there is nothing else to display */
    private final String emptyModelInfo;

    /**
     * If <code>false</code> only the name of the model elements will be
     * displayed in ElementContainerTreeNodes. If <code>true</code> the name
     * with all submodel information will be displayed for
     * ElementContainerTreeNodes.
     */
    private final boolean showElementNamesWithSubmodels;

    /**
     * If <code>true</code> all elements in the definition tree paths are
     * displayed. If <code>false</code> only the start- and end-classes of the
     * contained outer metapaths in the {@link PathTreeBranchDefinition} will be
     * displayed
     */
    final BooleanOption showAllElements;

    /**
     * @param emptyModelInfo Info that is displayed if there is nothing else to
     *            display
     * @param showElementNamesWithSubmodels If <code>false</code> only the name
     *            of the model elements will be displayed in
     *            ElementContainerTreeNodes. If <code>true</code> the name with
     *            all submodel information will be displayed for
     *            ElementContainerTreeNodes.
     * @param showAllElements if <code>true</code> all elements in the
     *            definition tree paths are displayed. If <code>false</code>
     *            only the start- and end-classes of the contained outer
     *            metapaths in the {@link PathTreeBranchDefinition} will be
     *            displayed
     */
    public PathTreeModel(final String emptyModelInfo, final boolean showElementNamesWithSubmodels, final BooleanOption showAllElements) {
        super(new StringTreeNode("Root", true));
        root = (StringTreeNode) super.root;
        this.emptyModelInfo = emptyModelInfo;
        this.showElementNamesWithSubmodels = showElementNamesWithSubmodels;
        this.showAllElements = showAllElements;
    }

    /**
     * @param treeDefinition
     */
    public void setTreeDefinition(final PathTreeDefinition treeDefinition) {
        this.treeDefinition = treeDefinition;
        reload();
    }

    /**
     * @return
     */
    public PathTreeDefinition getPathTreeDefinition() {
        return treeDefinition;
    }

    /**
     * @param parent
     * @param hierarchyDefinitionObject
     * @param icon
     * @return
     */
    private IconifiedTreeNode<?> getOrCreateHierarchyNode(final LGMTreeNode<?> parent, final Object hierarchyDefinitionObject, final ImageIcon icon) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            IconifiedTreeNode<?> childNode = (IconifiedTreeNode<?>) parent.getChildAt(i);
            Object childNodeUserObject = childNode.getUserObject();
            //check the UserObject is is equals to the current object in the hierarchy of the definition
            if (hierarchyDefinitionObject.equals(childNodeUserObject)) {
                return childNode;
            }
        }
        if (hierarchyDefinitionObject instanceof Class) {
            try {
                Class<? extends ModelElement> elementClass = ((Class<?>) hierarchyDefinitionObject).asSubclass(ModelElement.class);
                ElementsNameBuilder elementsNameBuilder = treeDefinition.getElementsNameBuilder();
                String elementClassName = elementsNameBuilder.getDisplayableName(elementClass);
                return new ElementClassTreeNode(elementClass, elementClassName, icon);
            } catch (Exception e) {
            }
        }
        String hierarchyNodeTextResourceKey = hierarchyDefinitionObject.toString();
        String hierarchyNodeText = treeDefinition.getResStringWithoutError(hierarchyNodeTextResourceKey);
        IconifiedTreeNode<?> hierarchyNode = new IconifiedTreeNode<>(hierarchyDefinitionObject, hierarchyNodeText, true, icon);
        parent.add(hierarchyNode);
        return hierarchyNode;
    }

    /**
     * Creates the full hierarchy of the given branch defintion and returns the
     * last node in this hierarchy.
     *
     * @param branchDefinition
     * @return the last node of the hierarchy node defined by the given branch
     *         definition
     */
    protected IconifiedTreeNode<?> getOrCreateBranchLastHierarchyNode(final PathTreeBranchDefinition branchDefinition) {
        IconifiedTreeNode<?> lastHierarchyNode = root;
        for (Object hierarchyObject : branchDefinition.iterableHierarchyObjects()) {
            ImageIcon icon = branchDefinition.getIcon(hierarchyObject);
            //if String try to load a resource string for this string as key
            if (hierarchyObject instanceof String) {
                hierarchyObject = branchDefinition.getResStringWithoutError(hierarchyObject);
            }
            lastHierarchyNode = getOrCreateHierarchyNode(lastHierarchyNode, hierarchyObject, icon);
        }
        return lastHierarchyNode;
    }

    /**
     * @param branchDefinition
     */
    protected void addBranch(final PathTreeBranchDefinition branchDefinition) {
        IconifiedTreeNode<?> lastHierarchyNode = getOrCreateBranchLastHierarchyNode(branchDefinition);
        SequenceMetaPath metaPath = branchDefinition.getElementsPath();
        Class<? extends ModelElement> pathStepConnectionClass = metaPath.getStartClass();
        Collection<ElementContainerTreeNode> pathStepNodes = new ArrayList<>();
        Iterable<GraphDocument> allSourceModels = getAllSourceModels();
        for (GraphDocument sourceModel : allSourceModels) {
            List<ElementContainer> elementContainers = sourceModel.getElementContainers(pathStepConnectionClass);
            getOrCreateNodes(pathStepNodes, elementContainers, lastHierarchyNode, null, branchDefinition);
        }
        boolean showAllElements = this.showAllElements.is();
        List<MetaPath> subMetaPaths = metaPath.getSubMetaPaths(showAllElements);
        int pathLength = subMetaPaths.size();
        for (int i = 0; i < pathLength; i++) {
            pathStepConnectionClass = MetaPathFunctions.getMetaPathsConnectingClass(subMetaPaths, i);
            MetaPath subMetaPath = subMetaPaths.get(i);
            pathStepNodes = addPathStepNodes(pathStepNodes, subMetaPath, branchDefinition);
        }
    }

    /**
     * @param parentNodes
     * @param subMetaPath
     * @param branchDefinition
     * @return
     */
    protected Collection<ElementContainerTreeNode> addPathStepNodes(final Iterable<ElementContainerTreeNode> parentNodes, final MetaPath subMetaPath, final PathTreeBranchDefinition branchDefinition) {
        Collection<ElementContainerTreeNode> nextPathStepNodes = new ArrayList<>();
        for (ElementContainerTreeNode parentNode : parentNodes) {
            ElementContainer parentEc = parentNode.getUserObject();
            GraphDocument doc = parentEc.getGraphDocument();
            ModelElement me = parentEc.getElement();
            List<ElementContainer> connectedContainers = subMetaPath.getConnectedContainer(me, doc);
            getOrCreateNodes(nextPathStepNodes, connectedContainers, parentNode, subMetaPath, branchDefinition);
        }
        return nextPathStepNodes;
    }

    /**
     * @param createdNodes
     * @param elementContainers
     * @param parent
     * @param subMetaPath
     * @param branchDefinition
     */
    protected void getOrCreateNodes(final Collection<ElementContainerTreeNode> createdNodes, final Iterable<ElementContainer> elementContainers, final LGMTreeNode<?> parent, final MetaPath subMetaPath, final PathTreeBranchDefinition branchDefinition) {
        for (ElementContainer ec : elementContainers) {
            ModelElement me = ec.getElement();
            Class<? extends ModelElement> meClass = me.getClass();
            boolean createSimpleNode = subMetaPath == null || subMetaPath instanceof ElementaryMetaPath;
            ImageIcon icon = null;
            if (!createSimpleNode) {
                //try to load an icon with the metapath resoruce key
                String baseResKeyOrName = subMetaPath.getBaseResKeyOrName();
                icon = branchDefinition.getIcon(baseResKeyOrName);
            }
            if (icon == null) {
                icon = branchDefinition.getIcon(meClass);
            }
            //setTreeNode is false. Set the treeNode only if there is no equals existing node
            boolean setTreeNodeInNodeContainer = false;
            ElementContainerTreeNode pathStepNode = createSimpleNode ? new ElementContainerTreeNode(ec, setTreeNodeInNodeContainer, true, icon) : new PathStepTreeNode(ec, subMetaPath, setTreeNodeInNodeContainer, true, icon);
            if (!showElementNamesWithSubmodels) {
                String simpleName = me.toString();
                String currentToStringName = pathStepNode.toString();
                if (!simpleName.equals(currentToStringName)) { // nur setzen, wenn anders
                    pathStepNode.setText(simpleName);
                }
            }
            LGMTreeNode<ElementContainer> existingEqualsNode = parent.getEqualsChild(pathStepNode);
            if (existingEqualsNode == null) {
                parent.add(pathStepNode);
                pathStepNode.setTreeNode(ec); //now set the new TreeNode to the ElementContainer (NodeContainer)
            } else {
                pathStepNode = (ElementContainerTreeNode) existingEqualsNode;
            }
            createdNodes.add(pathStepNode);
        }
    }

    @Override
    public void reload() {
        root.removeAllChildren();
        if (treeDefinition != null) {
            if (treeDefinition.isEmpty()) {
                if (!Strings.isNullOrEmpty(emptyModelInfo)) {
                    StringTreeNode noTemplatesInfoNode = new StringTreeNode(emptyModelInfo);
                    root.add(noTemplatesInfoNode);
                }
            } else {
                for (PathTreeBranchDefinition branchDefinition : treeDefinition) {
                    addBranch(branchDefinition);
                }
            }
        }
        super.reload();
    }

    @Override
    public Class<? extends MetaModelDefinition> getMetaModelDefinitionClass() {
        return treeDefinition == null ? null : treeDefinition.getMetaModelDefinitionClass();
    }

    /**
     * @return all submodels to be queried to fill the model
     */
    private Iterable<GraphDocument> getAllSourceModels() {
        Iterable<GraphDocument> sourceModels = getSourceModels();
        if (sourceModels != null) {
            return sourceModels;
        }
        GraphDocument sourceModel = getSourceModel();
        if (sourceModel == null) {
            sourceModel = Static.getSelectedDoc();
        }
        return ImmutableList.of(sourceModel);
    }

    /**
     * Subclasses must override this function or {@link #getSourceModel()} so
     * that the model knows which submodel or submodels to populate from. If
     * neither function is overridden, the main model of the currently selected
     * model is taken.
     *
     * @return the submodels to be queried to fill the model
     * @see #getSourceModel()
     */
    protected Iterable<GraphDocument> getSourceModels() {
        return null;
    }

    /**
     * Subclasses must override this function or {@link #getSourceModels()} so
     * that the model knows which submodel or submodels to populate from. If
     * neither function is overridden, the main model of the currently selected
     * model is taken.
     *
     * @return the submodel to be queried to fill the model
     * @see #getSourceModels()
     */
    protected GraphDocument getSourceModel() {
        return null;
    }

}
