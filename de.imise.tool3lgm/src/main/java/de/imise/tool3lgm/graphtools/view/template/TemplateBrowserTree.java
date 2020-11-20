package de.imise.tool3lgm.graphtools.view.template;

import static de.imise.tool3lgm.graphtools.view.template.TemplateBrowserTree.PropertyChangeEventType.CONTENT_CHANGED;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.swing.JScrollPane;
import javax.swing.JViewport;
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
import de.imise.tool3lgm.graphtools.dialog.search.SearchFunctions;
import de.imise.tool3lgm.graphtools.dialog.search.SearchOptions;
import de.imise.tool3lgm.graphtools.dialog.search.SearchResultView;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.template.TemplateLibrariesManager;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.graphtools.view.pathtree.PathTreeBranchDefinition;
import de.imise.tool3lgm.graphtools.view.pathtree.PathTreeDefinition;
import de.imise.tool3lgm.graphtools.view.pathtree.PathTreeModel;
import de.imise.tool3lgm.graphtools.view.tree.DynamicTree;
import de.imise.tool3lgm.graphtools.view.tree.TreeRenderer;
import de.imise.tool3lgm.graphtools.view.tree.node.ElementContainerTreeNode;
import de.imise.tool3lgm.graphtools.view.tree.node.LGMTreeNode;
import de.imise.tool3lgm.gui.menu.ContextGenerator;
import de.imise.util.BooleanOption;
import de.imise.util.swing.component.ParentComponentFinder;

/**
 * Baum, in dem die Templates dargestellt werden.
 *
 * @author AXS (05.09.2019)
 */
public class TemplateBrowserTree extends DynamicTree implements SearchResultView, PropertyChangeListener, AncestorListener, TemplateView {

    /**
     * The event type this tree fires to its PropertyChangeListeners.
     *
     * @author Ich (31.07.2020)
     */
    public enum PropertyChangeEventType {
        CONTENT_CHANGED
    }

    /**
     * The model of the tree
     */
    private final PathTreeModel pathTreeModel;

    /**
     * The {@link TemplateLibrariesManager}
     */
    private TemplateLibrariesManager templateLibrariesManager;

    /**
     * Saves the expanded paths of the tree for the respective model type. There
     * is a separate PathTreeDefinition for each model type.
     */
    private final Map<PathTreeDefinition, TreeViewData> lastTemplateTreeViewDataOfModelType = new HashMap<>();

    /**
     * Data structure for storing the properties of the tree
     *
     * @author AXS (01.11.2020)
     */
    private class TreeViewData {

        /** Expanded paths in the template tree */
        public Enumeration<TreePath> expandedPaths;

        /** Position of the viewport view = scroll position of the scrollpane */
        public Point viewPosition;

    }

    /**
     * @param showAllElements if <code>true</code> all elements in the
     *            definition tree paths are displayed. If <code>false</code>
     *            only the start- and end-classes of the contained outer
     *            metapaths in the {@link PathTreeBranchDefinition} will be
     *            displayed
     */
    public TemplateBrowserTree(final BooleanOption showAllElements) {
        super((TreeModel) null);
        setRootVisible(false);
        setShowsRootHandles(true);
        addAncestorListener(this);
        pathTreeModel = new PathTreeModel(Tool3lgmConstants.getResString("TEMPLATE_BROWSER_NO_TEMPLATES_AVAILABLE"), true, showAllElements);
        setModel(pathTreeModel);

        //analog ModelBrowser
        TreeRenderer cellRenderer = new TreeRenderer();
        setCellRenderer(cellRenderer);
        Color backgroundColor = getBackground();
        cellRenderer.setBackgroundNonSelectionColor(backgroundColor);
        setBackground(backgroundColor);

        setEditable(false);
        putClientProperty("JTree.lineStyle", "Angled");
        setToggleClickCount(-1);
        addViewPositionRestoreHandler();
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

    @Override
    public PathTreeModel getModel() {
        return pathTreeModel;
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
        //Sys.err("propertyChange " + evt);
        reload(false);
    }

    /**
     * @param forceReload The tree must always be reloaded if the
     *            MetaModelContext has changed or the "Expert Mode" option where
     *            all elements should be displayed.
     */
    public void reload(final boolean forceReload) {
        boolean oldValueHasContent = hasContent();
        MetaModelContext currentMetaModelContext = pathTreeModel.getMetaModelContext();
        MetaModelContext newMetaModelContext = Static.getSelectedMetaModelContext();
        if (!forceReload && Objects.equals(currentMetaModelContext, newMetaModelContext)) {
            return;
        }
        TemplateLibrariesManager templateLibrariesManager = Static.getTemplateLibrariesManager();
        PathTreeDefinition templateTreeDefinition = templateLibrariesManager.getTemplateTreeDefintion(newMetaModelContext);
        PathTreeDefinition oldTreeDefinition = pathTreeModel.getPathTreeDefinition();
        saveViewState(oldTreeDefinition);
        setRootVisible(true);
        setSelectionListenerActive(false);
        pathTreeModel.setTreeDefinition(templateTreeDefinition);
        setSelectionListenerActive(true);
        setRootVisible(false);
        boolean newValueHasContent = hasContent();
        firePropertyChange(CONTENT_CHANGED.name(), oldValueHasContent, newValueHasContent);
        restoreExpansionState(templateTreeDefinition); //necessarily after firePropertyChange(...)!
    }

    /**
     * Saves the expanded paths and the position of the view in the map that
     * contains this information for the respective model type.
     *
     * @param treeDefinition
     */
    private void saveViewState(final PathTreeDefinition treeDefinition) {
        if (treeDefinition != null) {
            TreePath rootPath = getRootPath();
            TreeViewData viewData = new TreeViewData();
            viewData.expandedPaths = getExpandedDescendants(rootPath);
            JScrollPane scrollPane = ParentComponentFinder.getParent(this, JScrollPane.class);
            if (scrollPane != null) {
                JViewport viewport = scrollPane.getViewport();
                viewData.viewPosition = viewport.getViewPosition();
            }
            lastTemplateTreeViewDataOfModelType.put(treeDefinition, viewData);
        }
    }

    /**
     * Restores the expanded paths in the tree.
     *
     * @param treeDefinition
     */
    private void restoreExpansionState(final PathTreeDefinition treeDefinition) {
        if (treeDefinition != null) {
            TreeViewData viewData = lastTemplateTreeViewDataOfModelType.get(treeDefinition);
            if (viewData != null) {
                setExpandedPaths(viewData.expandedPaths);
            } else {
                try {
                    expandRow(0);
                    expandRow(1);
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * The tree is not immediately resized when the expanded paths are set (in
     * {@link #restoreExpansionState(PathTreeDefinition)}). Therefore you cannot
     * simply set the viewPosition after setting the expansionPaths, but have to
     * wait until the tree component is really resized.<br>
     * The ComponentResized event always comes after the PropertyChange event in
     * which the expanded paths are restored.
     */
    private void addViewPositionRestoreHandler() {
        final TemplateBrowserTree templateBrowserTree = this;
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(final ComponentEvent e) {
                MetaModelContext metaModelContext = pathTreeModel.getMetaModelContext();
                TemplateLibrariesManager templateLibrariesManager = Static.getTemplateLibrariesManager();
                PathTreeDefinition templateTreeDefinition = templateLibrariesManager.getTemplateTreeDefintion(metaModelContext);
                if (templateTreeDefinition == null) {
                    return;
                }
                TreeViewData viewData = lastTemplateTreeViewDataOfModelType.get(templateTreeDefinition);
                if (viewData != null) {
                    JScrollPane scrollPane = ParentComponentFinder.getParent(templateBrowserTree, JScrollPane.class);
                    if (scrollPane != null) {
                        JViewport viewport = scrollPane.getViewport();
                        viewport.revalidate();
                        viewport.setViewPosition(viewData.viewPosition);
                        lastTemplateTreeViewDataOfModelType.remove(templateTreeDefinition);
                    }
                }
                removeViewDataForRemovedTemplates(templateLibrariesManager);
            }
        });
    }

    /**
     * Checks if the view data of a template type is still needed or if all
     * models for this template type have been closed and the saved data can be
     * deleted to restore the view.
     *
     * @param templateLibrariesManager
     */
    private void removeViewDataForRemovedTemplates(final TemplateLibrariesManager templateLibrariesManager) {
        List<PathTreeDefinition> pathTreeDefinitions = new ArrayList<>(lastTemplateTreeViewDataOfModelType.keySet()); //Copy!
        for (PathTreeDefinition pathTreeDefinition : pathTreeDefinitions) {
            MetaModelContext metaModelContext = pathTreeDefinition.getMetaModelContext();
            Collection<GDCollection> templates = templateLibrariesManager.getTemplates(metaModelContext);
            if (templates.isEmpty()) {
                lastTemplateTreeViewDataOfModelType.remove(pathTreeDefinition);
            }
        }
    }

    @Override
    public void ancestorAdded(final AncestorEvent event) {
        //Sys.err("ancestorAdded " + event);
        templateLibrariesManager = Static.getTemplateLibrariesManager();
        templateLibrariesManager.addPropertyChangeListener(this);
        propertyChange(null);
    }

    @Override
    public void ancestorRemoved(final AncestorEvent event) {
        //Sys.err("ancestorRemoved " + event);
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
    public void setSelection() {
        clearSelection();
        Collection<GDCollection> displayedTemplates = getDisplayedTemplates();
        for (GDCollection templateModel : displayedTemplates) {
            GraphDocument template = templateModel.getSelectedDoc();
            selectObjects(template);
        }
    }

    /**
     * Selects all elements in the tree that are selected in the corresponding
     * template.
     */
    private void selectObjects(final GraphDocument template) {
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
        addSelectionPaths(path);
        scrollToPath(path);
    }

    @Override
    public void showResult(final GraphDocument doc, final SearchOptions options) {
        resetView();
        for (GDCollection template : getDisplayedTemplates()) {
            template.deselectAll();
            GraphDocument selectedTemplateDoc = template.getSelectedDoc();
            List<ElementContainer> result = SearchFunctions.getResult(selectedTemplateDoc, options);
            template.addToSelection(result);
        }
        setSelection();
    }

    /**
     * Collapse all excect root
     */
    private void resetView() {
        for (int i = getRowCount() - 1; i > 0; i--) {
            collapseRow(i);
        }
    }

}
