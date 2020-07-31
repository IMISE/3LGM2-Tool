package de.imise.tool3lgm.graphtools.view.template;

import static de.imise.tool3lgm.graphtools.view.template.TemplateBrowserTree.PropertyChangeEventType.CONTENT_CHANGED;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.template.TemplateLibrariesManager;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.graphtools.view.pathtree.PathTreeDefinition;
import de.imise.tool3lgm.graphtools.view.pathtree.PathTreeModel;
import de.imise.tool3lgm.graphtools.view.tree.DynamicTree;
import de.imise.tool3lgm.graphtools.view.tree.TreeRenderer;
import de.imise.tool3lgm.graphtools.view.tree.node.ElementContainerTreeNode;
import de.imise.tool3lgm.graphtools.view.tree.node.LGMTreeNode;
import de.imise.tool3lgm.gui.menu.ContextGenerator;

/**
 * @author AXS (05.09.2019)
 */
public class TemplateBrowserTree extends DynamicTree implements PropertyChangeListener, AncestorListener, TemplateView {

    /**
     * The event type this tree fires to its
     * PropertyChangeListeners.
     *
     * @author Ich (31.07.2020)
     */
    public enum PropertyChangeEventType {
        CONTENT_CHANGED
    }

    /**
     *
     */
    private final PathTreeModel pathTreeModel;

    /**
     *
     */
    private TemplateLibrariesManager templateLibrariesManager;

    /**
     *
     */
    public TemplateBrowserTree() {
        super((TreeModel) null);
        setRootVisible(false);
        setShowsRootHandles(true);
        addAncestorListener(this);
        pathTreeModel = new PathTreeModel(Tool3lgmConstants.getResString("TEMPLATE_BROWSER_NO_TEMPLATES_AVAILABLE"), true);
        setModel(pathTreeModel);

        //analog ModelBrowser
        setCellRenderer(new TreeRenderer());
        ((TreeRenderer) getCellRenderer()).setBackgroundNonSelectionColor(getBackground());
        setBackground(getBackground());
        setEditable(false);
        putClientProperty("JTree.lineStyle", "Angled");
        setToggleClickCount(-1);
    }

    /**
     * @param propertyName
     * @param listener
     */
    public void addPropertyChangeListener(final PropertyChangeEventType propertyName, final PropertyChangeListener listener) {
        super.addPropertyChangeListener(propertyName.name(), listener);
    }

    @Override
    public GraphDocument getGraphDocument() {
        GraphDocument doc = null;
        TreePath leadSelectionPath = selectionModel.getLeadSelectionPath();
        if (leadSelectionPath != null) {
            Object lastPathComponent = leadSelectionPath.getLastPathComponent();
            if (lastPathComponent instanceof ElementContainerTreeNode) {
                ElementContainerTreeNode elementContainerNode = (ElementContainerTreeNode) lastPathComponent;
                doc = elementContainerNode.getGraphDocument();
            }
        }
        return doc;
    }

    @Override
    public ContextGenerator getContextGenerator() {
        return Static.templateContextGenerator;
    }

    /**
     * @return
     */
    public boolean hasContent() {
        Object root = pathTreeModel.getRoot();
        if (root == null) {
            return false;
        }
        if (!(root instanceof DefaultMutableTreeNode)) {
            return false;
        }
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) root;
        int rootChildCount = rootNode.getChildCount();
        if (rootChildCount == 0) {
            return false;
        }
        if (rootChildCount == 1) {
            TreeNode singleRootChild = rootNode.getChildAt(0);
            return singleRootChild.getChildCount() > 0;
        }
        return true;
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        boolean oldValueHasContent = hasContent();
        MetaModelContext currentMetaModelContext = pathTreeModel.getMetaModelContext();
        MetaModelContext newMetaModelContext = Static.getSelectedMetaModelContext();
        if (Objects.equals(currentMetaModelContext, newMetaModelContext)) {
            return;
        }
        TemplateLibrariesManager templateLibrariesManager = Static.getTemplateLibrariesManager();
        PathTreeDefinition templateTreeDefinition = templateLibrariesManager.getTemplateTreeDefintion(newMetaModelContext);
        PathTreeDefinition oldTreeDefinition = pathTreeModel.getPathTreeDefinition();
        saveExpansionState(oldTreeDefinition);
        setRootVisible(true);
        setSelectionListenerActive(false);
        pathTreeModel.setTreeDefinition(templateTreeDefinition);
        setSelectionListenerActive(true);
        setRootVisible(false);
        restoreExpansionState(templateTreeDefinition);
        boolean newValueHasContent = hasContent();
        firePropertyChange(CONTENT_CHANGED.name(), oldValueHasContent, newValueHasContent);
    }

    //    /**
    //     * Aus irgendeinem Grund expanded er den Baum nicht wieder - ARRRGGGHH!!
    //     */
    //    private final Map<PathTreeDefinition, Enumeration<TreePath>> treeDefintionToExpandedPaths = new HashMap<>();
    //
    //    private TreePath rootPath;
    //
    /**
     * @param treeDefinition
     */
    private void saveExpansionState(final PathTreeDefinition treeDefinition) {
        //        if (treeDefinition != null) {
        //            if (rootPath == null) {
        //                rootPath = getRootPath();
        //            }
        //            Enumeration<TreePath> expandedPaths = getExpandedDescendants(rootPath);
        //            treeDefintionToExpandedPaths.put(treeDefinition, expandedPaths);
        //        }
    }

    /**
     * @param treeDefinition
     */
    private void restoreExpansionState(final PathTreeDefinition treeDefinition) {
        //        if (treeDefinition != null) {
        //            Enumeration<TreePath> expandedPaths = treeDefintionToExpandedPaths.get(treeDefinition);
        //            if (expandedPaths != null) {
        //                setExpandedPaths(expandedPaths);
        //            } else {
        try {
            expandRow(0);
            expandRow(1);
        } catch (Exception e) {
        }
        //            }
        //        }
    }

    @Override
    public void ancestorAdded(final AncestorEvent event) {
        templateLibrariesManager = Static.getTemplateLibrariesManager();
        templateLibrariesManager.addPropertyChangeListener(this);
        propertyChange(null);
    }

    @Override
    public void ancestorRemoved(final AncestorEvent event) {
        templateLibrariesManager.removePropertyChangeListener(this);
    }

    @Override
    public void ancestorMoved(final AncestorEvent event) {
        //do nothing
    }

    @Override
    public Collection<GDCollection> getDisplayedTemplates() {
        return templateLibrariesManager == null ? new ArrayList<>() : templateLibrariesManager.getAllActiveTemplates();
    }

    @Override
    public void updateSelection() {
        Collection<GDCollection> displayedTemplates = getDisplayedTemplates();
        for (GDCollection templateModel : displayedTemplates) {
            GraphDocument template = templateModel.getSelectedDoc();
            selectObjects(template);
        }
    }

    /**
     * Selektiert im Baum alle Elemente, die im dazugehörigen Template selektiert sind.
     */
    public void selectObjects(final GraphDocument template) {
        TreePath[] path = new TreePath[template.getSelectedRealElementContainerCount()];
        int m = 0;
        GraphDocument mainDoc = template.getMainDoc();
        for (NodeContainer ec : template.getSelectedRealElementContainerIterable()) {
            ModelElement me = ec.getElement();
            ec = (NodeContainer) me.getContainer(template);
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
    }

}
