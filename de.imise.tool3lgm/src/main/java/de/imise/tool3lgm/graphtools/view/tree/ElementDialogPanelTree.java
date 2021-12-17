package de.imise.tool3lgm.graphtools.view.tree;

import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SHOW_PART_OF_HIERARCHY;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.google.common.collect.ImmutableSet;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.dialog.element.ElementPropertyDialogsContext;
import de.imise.tool3lgm.graphtools.dialog.search.SearchFunctions;
import de.imise.tool3lgm.graphtools.dialog.search.SearchOptions;
import de.imise.tool3lgm.graphtools.dialog.search.SearchResultView;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.graphtools.view.tree.node.ElementContainerTreeNode;
import de.imise.tool3lgm.graphtools.view.tree.node.LGMTreeNode;
import de.imise.tool3lgm.graphtools.view.tree.node.StringTreeNode;
import de.imise.util.ReflectionUtils;
import de.imise.util.ToolTipProvider;
import de.imise.util.swing.component.LimitedHeightScrollTreePane;
import de.imise.util.swing.component.tree.CorrectSelectionTree;

/**
 * @author AXS < (??.??.2010)
 */
public class ElementDialogPanelTree extends CorrectSelectionTree implements SearchResultView {

    /**
     *
     */
    private final GraphDocument doc;

    /**
     *
     */
    private final LimitedHeightScrollTreePane scrollPane;

    /**
     *
     */
    private final Collection<ElementContainer> elementsAdded = new HashSet<>();

    /**
     * @param rootObject
     * @param doc
     */
    public ElementDialogPanelTree(final String rootObject, final GraphDocument doc) {
        this(rootObject, doc, -1, false);
    }

    /**
     * @param rootObject
     * @param doc
     * @param maxLines
     */
    public ElementDialogPanelTree(final String rootObject, final GraphDocument doc, final int maxLines) {
        this(rootObject, doc, maxLines, false);
    }

    /**
     * @param rootObject
     * @param doc
     * @param maxLines
     * @param renderTreeAsList
     */
    public ElementDialogPanelTree(final String rootObject, final GraphDocument doc, final int maxLines, final boolean renderTreeAsList) {
        super(new DefaultTreeModel(new StringTreeNode(rootObject)));
        this.doc = doc;
        scrollPane = new LimitedHeightScrollTreePane(this, maxLines, renderTreeAsList);
    }

    /**
     * @param ec
     * @param sortRootChildren
     * @param maxLines
     * @param renderTreeAsList
     */
    public ElementDialogPanelTree(final ElementContainer ec, final boolean sortRootChildren, final int maxLines, final boolean renderTreeAsList) {
        super(new DefaultTreeModel(new ElementContainerTreeNode(ec, true, false, sortRootChildren)));
        doc = ec.getGraphDocument();
        setShowsRootHandles(true);
        scrollPane = new LimitedHeightScrollTreePane(this, maxLines, renderTreeAsList);
    }

    /**
     * @return the scrollPane
     */
    public final LimitedHeightScrollTreePane getScrollPane() {
        return scrollPane;
    }

    /**
     *
     */
    public final void reset() {
        LGMTreeNode<?> root = getRoot();
        root.removeAllChildren();
        elementsAdded.clear();
    }

    /**
     * @param ec
     * @param force
     * @param childrenAreSelectable
     * @return
     */
    public ElementContainerTreeNode addObject(final ElementContainer ec, final boolean force, final boolean childrenAreSelectable) {
        return addObject(ec, null, force, childrenAreSelectable);
    }

    /**
     * @param ec
     * @param excludeChildren
     * @param force
     * @param childrenAreSelectable
     * @return
     */
    public ElementContainerTreeNode addObject(final ElementContainer ec, final Collection<ElementContainer> excludeChildren, final boolean force, final boolean childrenAreSelectable) {
        return addObject(ec, null, excludeChildren, force, childrenAreSelectable);
    }

    /**
     * @param index
     * @param ec
     * @param excludeChildren
     * @param force
     * @param childrenAreSelectable
     * @return
     */
    public ElementContainerTreeNode insertObject(final int index, final ElementContainer ec, final Collection<ElementContainer> excludeChildren, final boolean force, final boolean childrenAreSelectable) {
        return insertObject(index, ec, null, excludeChildren, force, true, childrenAreSelectable);
    }

    /**
     * @param ec
     * @param parent
     * @param excludeChildren
     * @param force
     * @param childrenAreSelectable
     * @return
     */
    private ElementContainerTreeNode addObject(final ElementContainer ec, final LGMTreeNode<?> parent, final Collection<ElementContainer> excludeChildren, final boolean force, final boolean childrenAreSelectable) {
        return addObject(ec, parent, excludeChildren, force, true, childrenAreSelectable);
    }

    /**
     * @param ec
     * @param force
     * @param checkAlreadyAdded
     * @param childrenAreSelectable
     * @return
     */
    public ElementContainerTreeNode addObject(final ElementContainer ec, final boolean force, final boolean checkAlreadyAdded, final boolean childrenAreSelectable) {
        return addObject(ec, null, force, checkAlreadyAdded, childrenAreSelectable);
    }

    /**
     * @param ec
     * @param excludeChildren
     * @param force
     * @param checkAlreadyAdded
     * @param childrenAreSelectable
     * @return
     */
    public ElementContainerTreeNode addObject(final ElementContainer ec, final Collection<ElementContainer> excludeChildren, final boolean force, final boolean checkAlreadyAdded, final boolean childrenAreSelectable) {
        return addObject(ec, null, excludeChildren, force, checkAlreadyAdded, childrenAreSelectable);
    }

    /**
     * @param ec
     * @param parent
     * @param excludeChildren
     * @param force
     * @param checkAlreadyAdded
     * @param childrenAreSelectable
     * @return
     */
    public ElementContainerTreeNode addObject(final ElementContainer ec, final LGMTreeNode<?> parent, final Collection<ElementContainer> excludeChildren, final boolean force, final boolean checkAlreadyAdded, final boolean childrenAreSelectable) {
        return insertObject(-1, ec, parent, excludeChildren, force, checkAlreadyAdded, childrenAreSelectable);
    }

    /**
     * @param index
     * @param ec
     * @param parent
     * @param excludeChildren
     * @param force
     * @param checkAlreadyAdded
     * @param childrenAreSelectable
     * @return
     */
    private ElementContainerTreeNode insertObject(final int index, final ElementContainer ec, LGMTreeNode<?> parent, final Collection<ElementContainer> excludeChildren, final boolean force, final boolean checkAlreadyAdded,
            final boolean childrenAreSelectable) {
        if (checkAlreadyAdded && elementsAdded.contains(ec)) {
            return null;
        }
        boolean showPartOfHierarchy = OPTION_SHOW_PART_OF_HIERARCHY.is();
        if (showPartOfHierarchy && !force && !ec.getElement().getParentElements().isEmpty()) {
            return null;
        }
        ElementContainerTreeNode elementNode = ElementContainerTreeNode.createDialogTreeNode(ec);
        if (excludeChildren != null && excludeChildren.contains(ec)) {
            elementNode.setSelectable(false);
        }
        if (parent == null) {
            parent = getRoot();
        }
        if (index < 0 || index > parent.getChildCount()) {
            parent.add(elementNode);
        } else {
            parent.insert(elementNode, index);
        }
        elementsAdded.add(ec);
        if (showPartOfHierarchy) {
            addChildren(elementNode, excludeChildren, checkAlreadyAdded, childrenAreSelectable);
        }

        //this.setExpandedState(new TreePath(elementNode.getPath()), false);
        return elementNode;
    }

    /**
     * @return
     */
    public LGMTreeNode<?> getRoot() {
        TreeModel model = getModel();
        LGMTreeNode<?> root = (LGMTreeNode<?>) model.getRoot();
        return root;
    }

    /**
     * @param elementNode
     * @param excludeChildren
     * @param checkAlreadyAdded
     * @param childrenAreSelectable
     */
    private void addChildren(final ElementContainerTreeNode elementNode, final Collection<ElementContainer> excludeChildren, final boolean checkAlreadyAdded, final boolean childrenAreSelectable) {
        Object elementNodeUserObject = elementNode.getUserObject();
        if (elementNodeUserObject instanceof NodeContainer) { //only Nodes can have parts
            NodeContainer kc = (NodeContainer) elementNodeUserObject;

            List<ElementContainer> all = kc.getNode().getDirectPartContainers(doc);
            for (int i = 0; i < all.size(); i++) {
                NodeContainer pc = (NodeContainer) all.get(i);

                if (pc == null || checkAlreadyAdded && elementsAdded.contains(pc)) {
                    continue;
                }

                ElementContainerTreeNode childNode = ElementContainerTreeNode.createDialogTreeNode(pc);
                childNode.setSelectable(childrenAreSelectable);
                if (excludeChildren != null && excludeChildren.contains(pc)) {
                    childNode.setSelectable(false);
                }
                elementNode.add(childNode);
                elementsAdded.add(pc);
                addChildren(childNode, excludeChildren, checkAlreadyAdded, childrenAreSelectable);
            }
        }
    }

    /**
     *
     */
    public void reloadModel() {
        DefaultTreeModel model = (DefaultTreeModel) getModel();
        model.reload();
    }

    ////////////////////////////////////////////////////////////////
    // Model neu laden und Expansion + Selektion wiederherstellen //
    ////////////////////////////////////////////////////////////////

    /**
     *
     */
    public final void saveExpansionAndSelection() {
        saveExpansion();
        saveSelection();
    }

    /**
     *
     */
    public final void restoreExpansionAndSelection() {
        restoreExpansion();
        restoreSelection();
    }

    /**
     * <code>ArrayList</code> mit den <code>String</code>s der expandierten
     * Pfade
     */
    private final List<String> expandedPathStrings = new ArrayList<>();

    /**
     * Aktuelle Expansion merken
     */
    public final void saveExpansion() {
        //umständlich, aber alles andere zieht viel zu große Änderungen nach sich
        Enumeration<TreePath> e = getExpandedDescendants(new TreePath(treeModel.getRoot()));
        expandedPathStrings.clear();
        if (e != null) {
            while (e.hasMoreElements()) {
                expandedPathStrings.add(e.nextElement().toString());
            }
        }
    }

    /**
     * Expansion wieder herstellen. Da sich wenn man die Selektion wieder
     * herstellen möchte eigentlich immer das <code>TreeModel</code> geändert
     * hat, wird es auch neu geladen.
     */
    public final void restoreExpansion() {
        expandFull();
        if (expandedPathStrings.size() > 0) {
            //von hinten alle Pfade wieder zusammenklappen, die vorher auch nicht expandiert waren
            for (int i = getRowCount() - 1; i >= 0; i--) {
                TreePath p = getPathForRow(i);
                DefaultMutableTreeNode lastPathNode = (DefaultMutableTreeNode) p.getLastPathComponent();
                if (lastPathNode.isLeaf()) {
                    continue;
                }
                String actPathString = p.toString();
                boolean expanded = false;
                for (String nextExpandedPathString : expandedPathStrings) {
                    if (actPathString.equals(nextExpandedPathString)) {
                        expanded = true;
                        break;
                    }
                }
                if (!expanded) {
                    collapseRow(i);
                }
            }
        }

    }

    /**
     * Expandiert den gesamten Baum
     */
    public final void expandFull() {
        for (int n = 0; n < getRowCount(); n++) {
            expandRow(n);
        }
    }

    /**
     * Liste der selektierten Pfade
     */
    protected TreePath[] selectedPaths = null;

    /**
     * Aktuelle Selektion merken
     */
    public final void saveSelection() {
        selectedPaths = getSelectionPaths();
        if (selectedPaths == null) {
            selectedPaths = new TreePath[0];
        }

        //		//umständlich, aber alles andere zieht viel zu große Änderungen nach sich
        //		TreePath[] selectedPathes = getSelectionPaths();
        //		if (selectedPathes==null || selectedPathes.length==0) {
        //			selectedPathStrings = new String[0];
        //			return;
        //		}
        //		selectedPathStrings = new String[selectedPathes.length];
        //		for (int i=0; i<selectedPathes.length; i++)
        //			selectedPathStrings[i]=selectedPathes[i].toString();

    }

    /**
     * Selektion wieder herstellen. Da sich wenn man die Selektion wieder
     * herstellen möchte eigentlich immer das <code>TreeModel</code> geändert
     * hat, wird es auch neu geladen.
     */
    public final void restoreSelection() {
        if (selectedPaths.length == 0) {
            return;
        }
        String[] selectedPathStrings = new String[selectedPaths.length];
        for (int i = 0; i < selectedPaths.length; i++) {
            selectedPathStrings[i] = selectedPaths[i].toString();
        }

        for (int i = getRowCount() - 1; i >= 0; i--) {
            boolean selected = false;
            String pathString = getPathForRow(i).toString();
            for (int j = 0; j < selectedPaths.length; j++) {
                if (pathString.equals(selectedPaths[j].toString())) {
                    selected = true;
                }
            }
            if (selected) {
                addSelectionRow(i);
            }
        }
    }

    /**
     * In diesem Baum werden alle Elemente selektiert, die sichtbar sind und
     * deren letztes Pfadelement denen des übergebenen Baumes entspricht. Diese
     * Funktion ist hilfreich, falls in Dialogen "von einem Baum in einen
     * anderen" etwas übernommen wurde. Im Quellbaum ist das Element dann häufig
     * nicht mehr selektierbar, im Zielbaum erkennt man nicht, dass es
     * selektiert sein soll, außer man sicht nach den Node mit dem gleichen
     * UserObject.
     *
     * @param selectionSource Der Baum dessen Selektion nachgebildet werden soll
     */
    public final void restoreSelection(final ElementDialogPanelTree selectionSource) {
        TreePath[] sourceSelectedPaths = selectionSource.selectedPaths;
        for (int i = 0; i < sourceSelectedPaths.length; i++) {
            LGMTreeNode<?> sourceNode = (LGMTreeNode<?>) sourceSelectedPaths[i].getLastPathComponent();
            Object sourceElementContainer = sourceNode.getUserObject();
            for (int j = 0; j < getRowCount(); j++) {
                LGMTreeNode<?> node = (LGMTreeNode<?>) getPathForRow(j).getLastPathComponent();
                Object elementContainer = node.getUserObject();
                if (sourceElementContainer == elementContainer) {
                    addSelectionRow(j);
                }
            }
        }

    }

    /**
     * Macht dasselbe wie
     * <code>restoreSelectionAndScroll(LGMTree selectionSource)</code> und
     * scrollt die erste selektierte Zeile in den sichtbaren Bereich.
     *
     * @param selectionSource
     */
    public final void restoreSelectionAndScroll(final ElementDialogPanelTree selectionSource) {
        restoreSelection(selectionSource);
        scrollPathToVisible(getLeadSelectionPath());
    }

    @Override
    public final String getToolTipText(final MouseEvent event) {
        ToolTipProvider toolTipProvider = ElementPropertyDialogsContext.getToolTipProvider();
        return toolTipProvider.getToolTip(event);
    }

    /**
     * @param index
     * @return
     */
    private ModelElement getTreeNodeElement(final int index) {
        TreePath path = getPathForRow(index);
        Object lastPathComponent = path.getLastPathComponent();
        if (lastPathComponent instanceof ElementContainerTreeNode) {
            ElementContainerTreeNode node = (ElementContainerTreeNode) path.getLastPathComponent();
            ModelElement me = node.getModelElement();
            return me;
        }
        return null;
    }

    /**
     * @return
     */
    private Class<? extends ModelElement> getContainedElementsSuperClass() {
        Set<Class<? extends ModelElement>> result = new HashSet<>();
        int rowCount = getRowCount();
        for (int i = 0; i < rowCount; i++) {
            ModelElement me = getTreeNodeElement(i);
            if (me != null) {
                result.add(me.getClass());
            }
        }
        return ReflectionUtils.getCommonSuperClassOfClasses(result);
    }

    @Override
    public void showResult(final GraphDocument doc, final SearchOptions options) {
        int rowCount = getRowCount();
        if (rowCount < 0) {
            return;
        }
        options.searchedElementType = getContainedElementsSuperClass();

        TreePath[] selectedSearchPaths = null;
        List<ElementContainer> searchResults = doc == null ? new ArrayList<>() : SearchFunctions.getResult(doc, options);
        List<TreePath> tempSearchPaths = new ArrayList<>();
        int m = 0;

        for (ElementContainer result : searchResults) {
            ModelElement resultMe = result.getElement();
            for (int i = rowCount - 1; i >= 0; i--) {
                boolean selected = false;
                ModelElement me = getTreeNodeElement(i);
                if (Objects.equals(resultMe, me)) {
                    selected = true;
                    m++;
                }
                if (selected) {
                    tempSearchPaths.add(getPathForRow(i));
                }
            }
            if (m > 0) {
                TreePath[] searchPaths = new TreePath[m];
                for (int i = 0; i < m; i++) {
                    searchPaths[i] = tempSearchPaths.get(i);
                }
                selectedSearchPaths = searchPaths;
            } else {
                selectedSearchPaths = new TreePath[0];
            }
        }
        setSelectionPaths(selectedSearchPaths);
    }

    @Override
    public Set<Class<? extends ModelElement>> getSearchableElementClasses() {
        return ImmutableSet.of(ModelElement.class);
    }

    @Override
    public ElementsNameBuilder getElementsNameBuilder() {
        return doc.getElementsNameBuilder();
    }
}
