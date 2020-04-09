package de.imise.util.swing.component.tree;

import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

public class CorrectSelectionTree extends JTree {

    public CorrectSelectionTree() {
        init();
    }

    public CorrectSelectionTree(final Object[] value) {
        super(value);
        init();
    }

    public CorrectSelectionTree(final Vector<?> value) {
        super(value);
        init();
    }

    public CorrectSelectionTree(final Hashtable<?, ?> value) {
        super(value);
        init();
    }

    public CorrectSelectionTree(final TreeNode root) {
        super(root);
        init();
    }

    public CorrectSelectionTree(final TreeModel newModel) {
        super(newModel);
        init();
    }

    public CorrectSelectionTree(final TreeNode root, final boolean asksAllowsChildren) {
        super(root, asksAllowsChildren);
        init();
    }

    private void init() {
        setSelectionModel(new CorrectTreeSelectionModel(this));
    }

}
