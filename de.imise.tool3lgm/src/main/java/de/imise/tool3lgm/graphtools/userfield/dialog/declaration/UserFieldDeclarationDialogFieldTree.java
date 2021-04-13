package de.imise.tool3lgm.graphtools.userfield.dialog.declaration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import de.imise.tool3lgm.graphtools.userfield.definition.UserField;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldTarget;
import de.imise.tool3lgm.graphtools.userfield.definition.definitiontree.DefinitionGroupNode;
import de.imise.tool3lgm.graphtools.userfield.definition.definitiontree.DefinitionStructureNode;
import de.imise.tool3lgm.graphtools.userfield.definition.definitiontree.DefinitionTabNode;
import de.imise.tool3lgm.graphtools.userfield.definition.definitiontree.DefinitionUserFieldNode;
import de.imise.tool3lgm.graphtools.userfield.definition.definitiontree.DefinitionUserFieldTargetClassNode;
import de.imise.tool3lgm.graphtools.view.tree.node.LGMTreeNode;
import de.imise.util.Sys;
import de.imise.util.pair.Pair;

/**
 * {@link JList}, die UserFields anzeigen kann.
 *
 * @author astruebi
 */
public class UserFieldDeclarationDialogFieldTree extends JTree {

    /**
     *
     */
    private final UserFieldDefinitions definitions;

    /**
     *
     */
    private final DefaultTreeModel model;

    /**
     * @param definitions
     */
    public UserFieldDeclarationDialogFieldTree(final UserFieldDefinitions definitions) {
        this.definitions = definitions;
        model = new DefaultTreeModel(null);
        setModel(model);
        setRootVisible(false);
        getSelectionModel().setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
    }

    /**
     * Aktualisiert die Liste der {@link UserField}s für die selektierte Klasse
     */
    public void update(final Class<? extends UserFieldTarget> selectedClass) {
        DefinitionUserFieldTargetClassNode root = definitions.getUserFieldTargetClassNode(selectedClass);
        model.setRoot(root);
    }

    /**
     * Fügt zur Liste der <code>UserField</code>s das übergebene
     * <code>UserField</code> hinzu.
     *
     * @param userField
     */
    public void addUserField(final UserField userField) {
        definitions.addUserField(userField);
        model.reload();
    }

    /**
     * Fügt zur Liste der <code>UserField</code>s das übergebene
     * <code>UserField</code> hinzu.
     *
     * @param userField
     * @param index
     */
    public void addEntry(final DefinitionGroupNode parent, final UserField userField) {
        definitions.addUserField(parent, userField);
        model.reload();
    }

    /**
     * Fügt zur Liste der <code>UserField</code>s das übergebene
     * <code>UserField</code> hinzu.
     *
     * @param userField
     * @param index
     */
    public void addEntry(final DefinitionUserFieldNode sibling, final UserField userField) {
        definitions.addUserFieldAfter(sibling, userField);
        model.reload();
    }

    /**
     *
     */
    public void refresh() {
        model.reload();
        revalidate();
        repaint();

        //        //aus der Liste entfernen und wieder hinzufügen, damit der Anzeigename korrekt aktualisert wird
        //        int selectedIndex = getSelectedIndex();
        //        //Das Element aus der Liste entfernen und an alter Stelle wieder neu hinzufügen,
        //        //damit der evtl. geänderte korrekt Name angezeigt wird
        //        NamedObjectContainer<UserField> removed = model.remove(selectedIndex);
        //        addEntry(removed.getObject(), selectedIndex);
        //        setSelectedIndex(selectedIndex);
    }

    /**
     * @return
     */
    public UserField getSelectedUserField() {
        DefinitionUserFieldNode selectedUserFieldNode = getSelectedUserFieldNode();
        return selectedUserFieldNode == null ? null : selectedUserFieldNode.getUserObject();
    }

    /**
     * @return
     */
    public Set<UserField> getSelectedUserFields() {
        Set<UserField> result = new HashSet<>();
        TreePath[] selectionPaths = getSelectionPaths();
        for (TreePath path : selectionPaths) {
            Object lastPathComponent = path.getLastPathComponent();
            if (lastPathComponent instanceof DefinitionUserFieldNode) {
                DefinitionUserFieldNode userFieldNode = (DefinitionUserFieldNode) lastPathComponent;
                UserField userField = userFieldNode.getUserObject();
                result.add(userField);
            }
        }
        return result;
    }

    /**
     * @returnthe selected {@link DefinitionUserFieldNode} if only one node is
     *            selected and this is a {@link DefinitionUserFieldNode}
     */
    private DefinitionUserFieldNode getSelectedUserFieldNode() {
        return getSelectedNode(DefinitionUserFieldNode.class);
    }

    /**
     * @returnthe selected {@link DefinitionTabNode} if only one node is
     *            selected and this is a {@link DefinitionTabNode}
     */
    private DefinitionTabNode getSelectedTabNode() {
        return getSelectedNode(DefinitionTabNode.class);
    }

    /**
     * @returnthe selected {@link DefinitionGroupNode} if only one node is
     *            selected and this is a {@link DefinitionGroupNode}
     */
    private DefinitionGroupNode getSelectedGroupNode() {
        return getSelectedNode(DefinitionGroupNode.class);
    }

    /**
     * @returnthe selected {@link DefinitionUserFieldNode} if only one node is
     *            selected and this is a {@link DefinitionUserFieldNode}
     */
    private <T extends DefaultMutableTreeNode> T getSelectedNode(final Class<T> treeNodeClass) {
        TreePath[] selectionPaths = getSelectionPaths();
        if (selectionPaths.length == 1) {
            Object lastPathComponent = selectionPaths[0].getLastPathComponent();
            Class<? extends Object> lastPathComponentClass = lastPathComponent.getClass();
            if (treeNodeClass.isAssignableFrom(lastPathComponentClass)) {
                @SuppressWarnings("unchecked")
                T node = (T) lastPathComponent;
                return node;
            }
        }
        return null;
    }

    /**
     * @return List of all selected Nodes with the specified type
     */
    public <T extends DefaultMutableTreeNode> List<T> getSelectedNodes(final Class<T> treeNodeClass) {
        List<T> result = new ArrayList<>();
        TreePath[] selectionPaths = getSelectionPaths();
        for (TreePath path : selectionPaths) {
            Object lastPathComponent = path.getLastPathComponent();
            Class<? extends Object> lastPathComponentClass = lastPathComponent.getClass();
            if (treeNodeClass.isAssignableFrom(lastPathComponentClass)) {
                @SuppressWarnings("unchecked")
                T node = (T) lastPathComponent;
                result.add(node);
            }
        }
        return result;
    }

    //////////
    // Move //
    //////////

    /**
     *
     */
    public void moveUp() {
        move(true);
    }

    /**
     *
     */
    public void moveDown() {
        move(false);
    }

    /**
     * @param userFieldNode
     * @return
     */
    private Pair<DefinitionGroupNode, Integer> getMoveUpInsertPosition(final DefinitionUserFieldNode userFieldNode) {
        DefinitionGroupNode parentGroupNode = userFieldNode.getParent();
        int userFieldNodeIndex = parentGroupNode.getIndex(userFieldNode);
        DefinitionGroupNode newParentGroupNode = null;
        int newParentGroupNodeChildIndex = -1;
        if (userFieldNodeIndex > 0) {
            newParentGroupNode = parentGroupNode;
            newParentGroupNodeChildIndex = userFieldNodeIndex - 1;
        } else {
            DefinitionTabNode parentTabNode = parentGroupNode.getParent();
            int groupNodeIndex = parentTabNode.getIndex(parentGroupNode);
            if (groupNodeIndex > 0) {
                newParentGroupNode = parentTabNode.getChildAt(groupNodeIndex - 1);
            } else {
                LGMTreeNode<?> classOrTypeNode = parentTabNode.getParent();
                int parentTabNodeIndex = classOrTypeNode.getIndex(parentTabNode);
                if (parentTabNodeIndex > 0) {
                    parentTabNode = (DefinitionTabNode) classOrTypeNode.getChildAt(parentTabNodeIndex - 1);
                    int parentTabNodeChildCount = parentTabNode.getChildCount();
                    if (parentTabNodeChildCount > 0) {
                        newParentGroupNode = parentTabNode.getChildAt(parentTabNodeChildCount - 1);
                    } else {
                        newParentGroupNode = definitions.addGroup(parentTabNode);
                    }
                }
            }
        }
        return new Pair<>(newParentGroupNode, newParentGroupNodeChildIndex);
    }

    /**
     * @param userFieldNode
     * @return
     */
    private Pair<DefinitionGroupNode, Integer> getMoveDownInsertPosition(final DefinitionUserFieldNode userFieldNode) {
        DefinitionGroupNode parentGroupNode = userFieldNode.getParent();
        int userFieldNodeIndex = parentGroupNode.getIndex(userFieldNode);
        DefinitionGroupNode newParentGroupNode = null;
        int newParentGroupNodeChildIndex = 0;
        if (userFieldNodeIndex < parentGroupNode.getChildCount() - 1) {
            newParentGroupNode = parentGroupNode;
            newParentGroupNodeChildIndex = userFieldNodeIndex + 1;
        } else {
            DefinitionTabNode parentTabNode = parentGroupNode.getParent();
            int groupNodeIndex = parentTabNode.getIndex(parentGroupNode);
            if (groupNodeIndex < parentTabNode.getChildCount() - 1) {
                newParentGroupNode = parentTabNode.getChildAt(groupNodeIndex + 1);
            } else {
                LGMTreeNode<?> classOrTypeNode = parentTabNode.getParent();
                int parentTabNodeIndex = classOrTypeNode.getIndex(parentTabNode);
                if (parentTabNodeIndex < parentTabNodeIndex - 1) {
                    parentTabNode = (DefinitionTabNode) classOrTypeNode.getChildAt(parentTabNodeIndex + 1);
                    int parentTabNodeChildCount = parentTabNode.getChildCount();
                    if (parentTabNodeChildCount > 0) {
                        newParentGroupNode = parentTabNode.getChildAt(0);
                    } else {
                        newParentGroupNode = definitions.addGroup(parentTabNode);
                    }
                }
            }
        }
        return new Pair<>(newParentGroupNode, newParentGroupNodeChildIndex);
    }

    /**
     * Verschiebt das selektierte {@link UserField} um i Schritte (wenn die
     * Liste das zulässt). In den {@link UserFieldDefinitions} wird das
     * UserField ebenfalls verschoben.
     *
     * @param up
     */
    private void move(final boolean up) {
        DefinitionUserFieldNode selectedUserFieldNode = getSelectedUserFieldNode();
        if (selectedUserFieldNode != null) {
            Pair<DefinitionGroupNode, Integer> moveInsertPosition = up ? getMoveUpInsertPosition(selectedUserFieldNode) : getMoveDownInsertPosition(selectedUserFieldNode);
            DefinitionGroupNode groupNode = moveInsertPosition.getFirstItem();
            if (groupNode != null) {
                Integer insertIndex = moveInsertPosition.getSecondItem();
                definitions.moveToPosition(groupNode, selectedUserFieldNode, insertIndex);
                TreeNode[] selectedUserFieldNodePath = selectedUserFieldNode.getPath();
                TreePath newSelectionPath = new TreePath(selectedUserFieldNodePath);
                setSelectionPath(newSelectionPath);
            }
        } else {
            DefinitionGroupNode selectedGroupNode = getSelectedGroupNode();
            if (selectedGroupNode != null) {
                //TODO: #382: Move für GroupNodes implementieren
                Sys.err1("TODO: #382: Move für GroupNodes implementieren");
            } else {
                DefinitionTabNode selectedTabNode = getSelectedTabNode();
                if (selectedTabNode != null) {
                    //TODO: #382: Move für TabNodes implementieren
                    Sys.err1("TODO: #382: Move für TabNodes implementieren");
                }
            }
        }
        refresh();
    }

    ////////////
    // Delete //
    ////////////

    /**
     * @return all deleted UserFields
     */
    public void deleteSelected() {
        TreePath[] selectionPaths = getSelectionPaths();
        for (TreePath path : selectionPaths) {
            Object lastPathComponent = path.getLastPathComponent();
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) lastPathComponent;
            TreeNode[] pathToRoot = node.getPath();
            if (!(pathToRoot[0] instanceof DefinitionUserFieldTargetClassNode)) { //already deleted from parent?
                continue;
            }
            if (lastPathComponent instanceof DefinitionStructureNode) {
                DefinitionStructureNode structureNode = (DefinitionStructureNode) lastPathComponent;
                definitions.remove(structureNode);
            } else if (lastPathComponent instanceof DefinitionUserFieldNode) {
                DefinitionUserFieldNode userFieldNode = (DefinitionUserFieldNode) lastPathComponent;
                definitions.remove(userFieldNode);
            }
        }
        refresh();
    }

}
