package de.imise.util.swing.dnd.tree.demo;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.Serializable;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

public class DnDJTreeApp {
    public static void main(final String[] args) {
        JFrame frame = new JFrame("Drag and drop JTrees");
        frame.getContentPane().add(new TestPane());
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * @author AXS (27.11.2020)
     */
    public static class TestPane extends JPanel {

        private final JTree ltree;
        private final JTree rtree;

        public TestPane() {

            ltree = createTree("Tree 1");
            rtree = createTree("Tree 2");

            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.BOTH;
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weighty = 1;
            gbc.weightx = 1;
            add(new JScrollPane(ltree), gbc);

            gbc.gridx++;
            add(new JScrollPane(rtree), gbc);
        }
    }

    /**
     * @param addTransferHandler
     */
    private static JTree createTree(final String rootName) {
        DefaultTreeModel treeModel = getDefaultTreeModel(rootName);
        DnDJTree tree = new DnDJTree(treeModel);
        tree.setModel(treeModel);
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        return tree;
    }

    /**
     * @author AXS (27.11.2020)
     */
    public static class TreeItem implements Serializable {

        private final String text;

        public TreeItem(final String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        @Override
        public String toString() {
            return getText();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (text == null ? 0 : text.hashCode());
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            TreeItem other = (TreeItem) obj;
            if (text == null) {
                if (other.text != null) {
                    return false;
                }
            } else if (!text.equals(other.text)) {
                return false;
            }
            return true;
        }

    }

    /**
     * @param rootName
     * @return
     */
    private static DefaultTreeModel getDefaultTreeModel(final String rootName) {
        TreeItem rootItem = new TreeItem(rootName);
        DnDNode root = new DnDNode(rootItem);
        DnDNode parent;
        DnDNode nparent;

        if (rootName.endsWith("1")) {
            parent = add(root, "names");
            nparent = add(parent, "men");
            add(nparent, "jack");
            add(nparent, "kieran");
            add(nparent, "william");
            add(nparent, "jose");
            nparent = add(parent, "women");
            add(nparent, "jennifer");
            add(nparent, "holly");
            add(nparent, "danielle");
            add(nparent, "tara");
        } else {

            parent = add(root, "colors");
            add(parent, "red");
            add(parent, "red");
            add(parent, "yellow");
            add(parent, "green");
            add(parent, "blue");
            add(parent, "purple");

            parent = add(root, "sports");
            add(parent, "basketball");
            add(parent, "soccer");
            add(parent, "football");
            add(parent, "billard");

            nparent = add(parent, "hockey");
            add(nparent, "ice hockey");
            add(nparent, "roller hockey");
            add(nparent, "floor hockey");
            add(nparent, "road hockey");

            parent = add(root, "food");
            add(parent, "pizza");
            add(parent, "wings");
            add(parent, "pasta");
            nparent = add(parent, "fruit");
            add(nparent, "bananas");
            add(nparent, "apples");
            add(nparent, "grapes");
            add(nparent, "pears");
        }
        return new DefaultTreeModel(root);
    }

    /**
     * @param parent
     * @param itemText
     * @return
     */
    private static DnDNode add(final DnDNode parent, final String itemText) {
        TreeItem treeItem = new TreeItem(itemText);
        DnDNode childNode = new DnDNode(treeItem);
        parent.add(childNode);
        return childNode;
    }

}
