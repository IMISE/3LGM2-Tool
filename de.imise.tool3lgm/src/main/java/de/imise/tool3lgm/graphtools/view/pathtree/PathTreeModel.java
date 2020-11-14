package de.imise.tool3lgm.graphtools.view.pathtree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.tree.DefaultTreeModel;

import org.apache.jena.ext.com.google.common.base.Strings;

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelDefinition;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelSpecific;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.model.template.TemplateLibrariesManager;
import de.imise.tool3lgm.graphtools.path.metapaths.SequenceMetaPath;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.tree.node.ElementClassTreeNode;
import de.imise.tool3lgm.graphtools.view.tree.node.ElementContainerTreeNode;
import de.imise.tool3lgm.graphtools.view.tree.node.IconifiedTreeNode;
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
     *
     */
    public PathTreeModel() {
        this(null, false);
    }

    /**
     * @param emptyModelInfo Info that is displayed if there is nothing else to
     *            display
     */
    public PathTreeModel(final String emptyModelInfo) {
        this(emptyModelInfo, false);
    }

    /**
     * @param showElementNamesWithSubmodels If <code>false</code> only the name
     *            of the model elements will be displayed in
     *            ElementContainerTreeNodes. If <code>true</code> the name with
     *            all submodel information will be displayed for
     *            ElementContainerTreeNodes.
     */
    public PathTreeModel(final boolean showElementNamesWithSubmodels) {
        this(null, showElementNamesWithSubmodels);
    }

    /**
     * @param emptyModelInfo Info that is displayed if there is nothing else to
     *            display
     * @param showElementNamesWithSubmodels If <code>false</code> only the name
     *            of the model elements will be displayed in
     *            ElementContainerTreeNodes. If <code>true</code> the name with
     *            all submodel information will be displayed for
     *            ElementContainerTreeNodes.
     */
    public PathTreeModel(final String emptyModelInfo, final boolean showElementNamesWithSubmodels) {
        super(new StringTreeNode("Root", true));
        root = (LGMTreeNode) super.root;
        this.emptyModelInfo = emptyModelInfo;
        this.showElementNamesWithSubmodels = showElementNamesWithSubmodels;
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
    private LGMTreeNode getOrCreateHierarchyNode(final LGMTreeNode parent, final Object hierarchyDefinitionObject, final ImageIcon icon) {
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
                Class<? extends ModelElement> elementClass = ((Class<?>) hierarchyDefinitionObject).asSubclass(ModelElement.class);
                ElementsNameBuilder elementsNameBuilder = treeDefinition.getElementsNameBuilder();
                String elementClassName = elementsNameBuilder.getDisplayableName(elementClass);
                return new ElementClassTreeNode(elementClass, elementClassName, icon);
            } catch (Exception e) {
            }
        }
        String hierarchyNodeTextResourceKey = hierarchyDefinitionObject.toString();
        String hierarchyNodeText = treeDefinition.getResStringWithoutError(hierarchyNodeTextResourceKey);
        LGMTreeNode hierarchyNode = new IconifiedTreeNode(hierarchyDefinitionObject, hierarchyNodeText, true, icon);
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
    private LGMTreeNode getOrCreateBranchLastHierarchyNode(final PathTreeBranchDefinition branchDefinition) {
        LGMTreeNode lastHierarchyNode = root;
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
    private void addBranch(final PathTreeBranchDefinition branchDefinition) {
        LGMTreeNode lastHierarchyNode = getOrCreateBranchLastHierarchyNode(branchDefinition);
        SequenceMetaPath elementsPath = branchDefinition.getElementsPath();
        MetaModelContext metaModelContext = getMetaModelContext();
        TemplateLibrariesManager templateLibrariesManager = Static.getTemplateLibrariesManager();
        Collection<GDCollection> templates = templateLibrariesManager.getTemplates(metaModelContext);
        Class<? extends ModelElement> pathStepConnectionClass = elementsPath.getStartClass();
        Collection<ElementContainerTreeNode> pathStepNodes = new ArrayList<>();
        for (GDCollection template : templates) {
            LGMGraphDocument mainGraphDocument = template.getMainDoc();
            List<ElementContainer> elementContainers = mainGraphDocument.getElementContainers(pathStepConnectionClass);
            createNodes(pathStepNodes, elementContainers, lastHierarchyNode, branchDefinition);
        }
        int pathLength = elementsPath.getElementaryMetaPathCount();
        for (int i = 0; i < pathLength; i++) {
            pathStepConnectionClass = elementsPath.getElementaryPathStepConnectingClass(i);
            pathStepNodes = addPathStepNodes(pathStepNodes, pathStepConnectionClass, branchDefinition);
        }
    }

    /**
     * @param parentNodes
     * @param nextPathStepElementClass
     * @param branchDefinition
     * @return
     */
    private Collection<ElementContainerTreeNode> addPathStepNodes(final Iterable<ElementContainerTreeNode> parentNodes, final Class<? extends ModelElement> nextPathStepElementClass, final PathTreeBranchDefinition branchDefinition) {
        Collection<ElementContainerTreeNode> nextPathStepNodes = new ArrayList<>();
        for (ElementContainerTreeNode parentNode : parentNodes) {
            ElementContainer parentEc = parentNode.getUserObject();
            GraphDocument doc = parentEc.getGraphDocument();
            ModelElement me = parentEc.getElement();
            List<ElementContainer> connectedContainers = me.getConnectedContainers(nextPathStepElementClass, doc);
            createNodes(nextPathStepNodes, connectedContainers, parentNode, branchDefinition);
        }
        return nextPathStepNodes;
    }

    /**
     * @param createdNodes
     * @param elementContainers
     * @param parent
     * @param branchDefinition
     */
    private void createNodes(final Collection<ElementContainerTreeNode> createdNodes, final Iterable<ElementContainer> elementContainers, final LGMTreeNode parent, final PathTreeBranchDefinition branchDefinition) {
        for (ElementContainer ec : elementContainers) {
            ModelElement me = ec.getElement();
            Class<? extends ModelElement> meClass = me.getClass();
            ImageIcon icon = branchDefinition.getIcon(meClass);
            ElementContainerTreeNode pathStepNode = new ElementContainerTreeNode(ec, true, false, icon);
            if (!showElementNamesWithSubmodels) {
                String simpleName = me.toString();
                String currentToStringName = pathStepNode.toString();
                if (!simpleName.equals(currentToStringName)) { // nur setzen, wenn anders
                    pathStepNode.setText(simpleName);
                }
            }
            parent.add(pathStepNode);
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

}
