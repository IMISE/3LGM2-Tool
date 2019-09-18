package de.imise.tool3lgm.graphtools.view.pathtree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.tree.DefaultTreeModel;

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelSpecific;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.model.template.TemplateLibrariesManager;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.tree.node.ElementClassTreeNode;
import de.imise.tool3lgm.graphtools.view.tree.node.ElementContainerTreeNode;
import de.imise.tool3lgm.graphtools.view.tree.node.LGMTreeNode;
import de.imise.tool3lgm.graphtools.view.tree.node.StringTreeNode;

/**
 * @author AXS (01.09.2019)
 */
public class PathTreeModel extends DefaultTreeModel implements MetaModelSpecific {

    /** Definiton of the branches of the tree */
    private PathTreeDefinition treeDefinition;

    /** root node as {@link LGMTreeNode} */
    private final LGMTreeNode root;

    /**
     *
     */
    public PathTreeModel() {
        super(new StringTreeNode("Root", true));
        root = (LGMTreeNode) super.root;
    }

    /**
     * @param treeDefinition
     */
    public void setTreeDefinition(final PathTreeDefinition treeDefinition) {
        this.treeDefinition = treeDefinition;
        reload();
    }

    /**
     * @param parent
     * @param hierarchyDefinitionObject
     * @return
     */
    private LGMTreeNode getOrCreateHierarchyNode(final LGMTreeNode parent, final Object hierarchyDefinitionObject) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            LGMTreeNode childNode = (LGMTreeNode) parent.getChildAt(i);
            Object childNodeUserObject = childNode.getUserObject();
            //check the UserObject is is equals to the current object in the hierarchy of the definition
            if (hierarchyDefinitionObject.equals(childNodeUserObject)) {
                return childNode;
            }
        }
        if (hierarchyDefinitionObject instanceof Class) {
            try {
                Class<? extends ModelElement> elementClass = ((Class) hierarchyDefinitionObject).asSubclass(ModelElement.class);
                ElementsNameBuilder elementsNameBuilder = treeDefinition.getElementsNameBuilder();
                String elementClassName = elementsNameBuilder.getDisplayableName(elementClass);
                return new ElementClassTreeNode(elementClass, elementClassName);
            } catch (Exception e) {
            }
        }
        String hierarchyNodeTextResourceKey = hierarchyDefinitionObject.toString();
        String hierarchyNodeText = treeDefinition.getResStringWithoutError(hierarchyNodeTextResourceKey);
        LGMTreeNode hierarchyNode = new LGMTreeNode(hierarchyDefinitionObject, hierarchyNodeText, true);
        parent.add(hierarchyNode);
        return hierarchyNode;
    }

    /**
     * Creates the full hierarchy of the given branch defintion and returns the last node in this hierarchy.
     *
     * @param branchDefinition
     * @return the last node of the hierarchy node defined by the given branch definition
     */
    private LGMTreeNode getOrCreateBranchLastHierarchyNode(final PathTreeBranchDefinition branchDefinition) {
        LGMTreeNode lastHierarchyNode = root;
        for (Object hiearchyObject : branchDefinition.iterableHierarchyObjects()) {
            lastHierarchyNode = getOrCreateHierarchyNode(lastHierarchyNode, hiearchyObject);
        }
        return lastHierarchyNode;
    }

    /**
     * @param elementsPath
     * @param lastHierarchyNode
     */
    private void addBranchModelElementsPath(final SimpleMetaPath elementsPath, final LGMTreeNode lastHierarchyNode) {
        MetaModelContext metaModelContext = elementsPath.getMetaModelContext();
        TemplateLibrariesManager templateLibrariesManager = Static.getTemplateLibrariesManager();
        Collection<GDCollection> templates = templateLibrariesManager.getTemplates(metaModelContext);
        Class<? extends ModelElement> pathStepConnectionClass = elementsPath.getStartClass();
        Collection<ElementContainerTreeNode> pathStepNodes = new ArrayList<>();
        for (GDCollection template : templates) {
            LGMGraphDocument mainGraphDocument = template.getMainGraphDocument();
            List<ElementContainer> elementContainers = mainGraphDocument.getElementContainers(pathStepConnectionClass);
            createNodes(pathStepNodes, elementContainers, lastHierarchyNode);
        }
        int pathLength = elementsPath.length();
        for (int i = 0; i < pathLength; i++) {
            Class<? extends ModelElement> pathStepElementClass = elementsPath.getPathStepElementClass(i);
            pathStepNodes = addPathStepNodes(pathStepNodes, pathStepElementClass);
        }
    }

    /**
     * @param parentNodes
     * @param nextPathStepElementClass
     * @return
     */
    private Collection<ElementContainerTreeNode> addPathStepNodes(final Iterable<ElementContainerTreeNode> parentNodes, final Class<? extends ModelElement> nextPathStepElementClass) {
        Collection<ElementContainerTreeNode> nextPathStepNodes = new ArrayList<>();
        for (ElementContainerTreeNode parentNode : parentNodes) {
            ElementContainer parentEc = parentNode.getUserObject();
            GraphDocument doc = parentEc.getGraphDocument();
            ModelElement me = parentEc.getElement();
            List<ElementContainer> connectedContainers = me.getConnectedContainers(nextPathStepElementClass, doc);
            createNodes(nextPathStepNodes, connectedContainers, parentNode);
        }
        return nextPathStepNodes;
    }

    /**
     * @param createdNodes
     * @param elementContainers
     * @param parent
     */
    private void createNodes(final Collection<ElementContainerTreeNode> createdNodes, final Iterable<ElementContainer> elementContainers, final LGMTreeNode parent) {
        for (ElementContainer ec : elementContainers) {
            ElementContainerTreeNode pathStepNode = new ElementContainerTreeNode(ec, true, true);
            parent.add(pathStepNode);
            createdNodes.add(pathStepNode);
        }
    }

    /**
     * @param branchDefinition
     */
    private void addBranch(final PathTreeBranchDefinition branchDefinition) {
        LGMTreeNode lastHierarchyNode = getOrCreateBranchLastHierarchyNode(branchDefinition);
        SimpleMetaPath elementsPath = branchDefinition.getElementsPath();
        addBranchModelElementsPath(elementsPath, lastHierarchyNode);
    }

    @Override
    public void reload() {
        root.removeAllChildren();
        for (PathTreeBranchDefinition branchDefinition : treeDefinition) {
            addBranch(branchDefinition);
        }
        super.reload();
    }

    @Override
    public MetaModelContext getMetaModelContext() {
        return treeDefinition == null ? null : treeDefinition.getMetaModelContext();
    }

}
