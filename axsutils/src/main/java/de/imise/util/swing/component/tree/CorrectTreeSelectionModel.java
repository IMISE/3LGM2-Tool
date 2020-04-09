package de.imise.util.swing.component.tree;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;

public class CorrectTreeSelectionModel extends DefaultTreeSelectionModel {

    private final JTree myTree;

    public CorrectTreeSelectionModel(final JTree myTree) {
        this.myTree = myTree;
    }

    @Override
    public void setSelectionPaths(final TreePath[] paths) {
        if (isPathInThisTree(paths)) {
            super.setSelectionPaths(paths);
        }
    }

    @Override
    public void addSelectionPaths(final TreePath[] paths) {
        if (isPathInThisTree(paths)) {
            super.addSelectionPaths(paths);
        }
    }

    private boolean isPathInThisTree(final TreePath[] paths) {
        if (paths == null || paths.length == 0) {
            return true;
        }
        Object root = myTree.getModel().getRoot();
        TreePath selectionPath = paths[0];
        Object[] path = selectionPath.getPath();
        return path.length > 0 && path[0] == root;
    }

}
