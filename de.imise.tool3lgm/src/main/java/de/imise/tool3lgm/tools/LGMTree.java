package de.imise.tool3lgm.tools;

import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.userproperties.UserProperties;

public class LGMTree extends JTree {

    private GraphDocument doc;

    public LGMTree(final DefaultTreeModel treeModel, final GraphDocument doc) {
        super(treeModel);
        this.doc = doc;
        setShowsRootHandles(true);
        //super(new DefaultTreeModel(new LGMTreeNode(Tool3lgmConstants.getResourceBundle().getString("browser"), false, false)));
    }

    public LGMTree(final DefaultTreeModel treeModel) {
        super(treeModel);
    }

    ArrayList<ElementContainer> elementsAdded = new ArrayList<ElementContainer>(500);

    public void reset() {
        elementsAdded.clear();
    }

    public LGMTreeNode addObject(final ElementContainer objekt, final LGMTreeNode parent, final ArrayList<ElementContainer> excludeChildren, final boolean force, final boolean childrenAreSelectable) {
        return addObject(objekt, parent, excludeChildren, force, true, childrenAreSelectable);
    }

    public LGMTreeNode addObject(final ElementContainer objekt, final LGMTreeNode parent, final ArrayList<ElementContainer> excludeChildren, final boolean force, final boolean checkAlreadyAdded, final boolean childrenAreSelectable) {
        if (objekt instanceof NodeContainer) {
            NodeContainer kc = (NodeContainer) objekt;
            if (checkAlreadyAdded && elementsAdded.contains(kc)) {
                return null;
            }

            if (UserProperties.isShowPartOfHierarchy() && !force && !kc.getElement().getParentElements().isEmpty()) {
                return null;
            }
            LGMTreeNode elementNode = new LGMTreeNode(kc, false, true);
            if (excludeChildren != null && excludeChildren.contains(kc)) {
                elementNode.setSelectable(false);
            }

            parent.add(elementNode);
            elementsAdded.add(kc);
            if (UserProperties.isShowPartOfHierarchy()) {
                addChildren(elementNode, excludeChildren, checkAlreadyAdded, childrenAreSelectable);
            }

            //this.setExpandedState(new TreePath(elementNode.getPath()), false);
            return elementNode;
        }
        return null;
    }

    private void addChildren(final LGMTreeNode elementNode, final ArrayList<ElementContainer> excludeChildren, final boolean checkAlreadyAdded, final boolean childrenAreSelectable) {
        NodeContainer kc = (NodeContainer) elementNode.getUserObject();

        ArrayList<ElementContainer> all = kc.getKnoten().getDirectPartContainer(doc);
        for (int i = 0; i < all.size(); i++) {
            NodeContainer pc = (NodeContainer) all.get(i);

            if (pc == null || checkAlreadyAdded && elementsAdded.contains(pc)) {
                continue;
            }

            LGMTreeNode childNode = new LGMTreeNode(pc, false, true);
            childNode.setSelectable(childrenAreSelectable);
            if (excludeChildren != null && excludeChildren.contains(pc)) {
                childNode.setSelectable(false);
            }
            elementNode.add(childNode);
            elementsAdded.add(pc);
            addChildren(childNode, excludeChildren, checkAlreadyAdded, childrenAreSelectable);
        }
    }

    ////////////////////////////////////////////////////////////////
    // Model neu laden und Expansion + Selektion wiederherstellen //
    ////////////////////////////////////////////////////////////////

    /**
     * <code>ArrayList</code> mit den <code>String</code>s der expandierten Pfade
     */
    private final ArrayList<String> expandedPathStrings = new ArrayList<String>();

    /**
     * Aktuelle Expansion merken
     */
    public void saveExpansion() {
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
     * Expansion wieder herstellen. Da sich wenn man die Selektion wieder herstellen möchte eigentlich
     * immer das <code>TreeModel</code> geändert hat, wird es auch neu geladen.
     */
    public void restoreExpansion() {
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
                for (int j = expandedPathStrings.size() - 1; j >= 0; j--) {
                    String nextExpandedPathString = expandedPathStrings.get(j).toString();
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
    public void expandFull() {
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
    public void saveSelection() {
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
     * Selektion wieder herstellen. Da sich wenn man die Selektion wieder herstellen möchte eigentlich
     * immer das <code>TreeModel</code> geändert hat, wird es auch neu geladen.
     */
    public void restoreSelection() {
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
                    break;
                }
            }
            if (selected) {
                addSelectionRow(i);
            }
        }
    }

    /**
     * In diesem Baum werden alle Elemente selektiert, die sichtbar sind und deren letztes Pfadelement
     * denen des übergebenen Baumes entspricht.
     * Diese Funktion ist hilfreich, falls in Dialogen "von einem Baum in einen anderen" etwas übernommen
     * wurde. Im Quellbaum ist das Element dann häufig nicht mehr selektierbar, im Zielbaum erkennt man
     * nicht, dass es selektiert sein soll, außer man sicht nach den Knoten mit dem gleichen UserObject.
     * 
     * @param selectionSource Der Baum dessen Selektion nachgebildet werden soll
     */
    public void restoreSelection(final LGMTree selectionSource) {
        TreePath[] sourceSelectedPaths = selectionSource.selectedPaths;
        for (int i = 0; i < sourceSelectedPaths.length; i++) {
            LGMTreeNode sourceNode = (LGMTreeNode) sourceSelectedPaths[i].getLastPathComponent();
            Object sourceElementContainer = sourceNode.getUserObject();
            for (int j = 0; j < getRowCount(); j++) {
                LGMTreeNode node = (LGMTreeNode) getPathForRow(j).getLastPathComponent();
                Object elementContainer = node.getUserObject();
                if (sourceElementContainer == elementContainer) {
                    addSelectionRow(j);
                }
            }
        }

    }

    /**
     * Macht dasselbe wie <code>restoreSelectionAndScroll(LGMTree selectionSource)</code> und
     * scrollt die erste selektierte Zeile in den sichtbaren Bereich.
     * 
     * @param selectionSource
     */
    public void restoreSelectionAndScroll(final LGMTree selectionSource) {
        restoreSelection(selectionSource);
        scrollPathToVisible(getLeadSelectionPath());
    }

}
