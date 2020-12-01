package de.imise.util.swing.dnd.demo.transferabletest;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

public class DnDTransferableTreesTest {

    public static void main(final String[] args) {
        new DnDTransferableTreesTest();
    }

    public DnDTransferableTreesTest() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                }

                JFrame frame = new JFrame("Testing");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLayout(new BorderLayout());
                frame.add(new TestPane());
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    private static ListTransferHandler listTransferHandler = new ListTransferHandler();

    /**
     * @param addTransferHandler
     */
    private JTree createTree(final boolean addTransferHandler) {
        DefaultTreeModel treeModel = getDefaultTreeModel();
        JTree tree = new JTree(treeModel);
        tree.setModel(treeModel);
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        tree.setDragEnabled(true);
        if (addTransferHandler) {
            tree.setTransferHandler(listTransferHandler);
        }
        return tree;
    }

    /**
     * @author AXS (27.11.2020)
     */
    public class TestPane extends JPanel {

        private final JTree ltree;
        private final JTree rtree;

        public TestPane() {

            ltree = createTree(true);
            rtree = createTree(false);

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
     * @author AXS (27.11.2020)
     */
    public static class ListTransferHandler extends TransferHandler {

        @Override
        public boolean canImport(final TransferSupport support) {
            return support.getComponent() instanceof JLabel && support.isDataFlavorSupported(ListItemTransferable.LIST_ITEM_DATA_FLAVOR);
        }

        @Override
        public boolean importData(final TransferSupport support) {
            boolean accept = false;
            if (canImport(support)) {
                try {
                    Transferable t = support.getTransferable();
                    Object value = t.getTransferData(ListItemTransferable.LIST_ITEM_DATA_FLAVOR);
                    if (value instanceof TreeItem) {
                        Component component = support.getComponent();
                        if (component instanceof JLabel) {
                            ((JLabel) component).setText(((TreeItem) value).getText());
                            accept = true;
                        }
                    }
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
            }
            return accept;
        }

        @Override
        public int getSourceActions(final JComponent c) {
            return DnDConstants.ACTION_COPY_OR_MOVE;
        }

        @Override
        protected Transferable createTransferable(final JComponent c) {
            Transferable t = null;
            if (c instanceof JList) {
                @SuppressWarnings("unchecked")
                JList<TreeItem> list = (JList<TreeItem>) c;
                Object value = list.getSelectedValue();
                if (value instanceof TreeItem) {
                    TreeItem li = (TreeItem) value;
                    t = new ListItemTransferable(li);
                }
            }
            return t;
        }

        @Override
        protected void exportDone(final JComponent source, final Transferable data, final int action) {
            System.out.println("ExportDone");
            // Here you need to decide how to handle the completion of the transfer,
            // should you remove the item from the list or not...
        }
    }

    /**
     * @author AXS (27.11.2020)
     */
    public static class ListItemTransferable implements Transferable {

        public static final DataFlavor LIST_ITEM_DATA_FLAVOR = new DataFlavor(TreeItem.class, "java/TreeItem");
        private final TreeItem listItem;

        public ListItemTransferable(final TreeItem listItem) {
            this.listItem = listItem;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[] {
                    LIST_ITEM_DATA_FLAVOR
            };
        }

        @Override
        public boolean isDataFlavorSupported(final DataFlavor flavor) {
            return flavor.equals(LIST_ITEM_DATA_FLAVOR);
        }

        @Override
        public Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            return listItem;
        }
    }

    /**
     * @author AXS (27.11.2020)
     */
    public static class TreeItem {

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
    }

    /**
     * @return
     */
    private static DefaultTreeModel getDefaultTreeModel() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("things");
        DefaultMutableTreeNode parent;
        DefaultMutableTreeNode nparent;

        parent = add(root, "colors");
        add(parent, "red");
        add(parent, "red");
        add(parent, "yellow");
        add(parent, "green");
        add(parent, "blue");
        add(parent, "purple");

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

        parent = add(root, "sports");
        add(parent, "basketball");
        add(parent, "soccer");
        add(parent, "football");

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
        return new DefaultTreeModel(root);
    }

    /**
     * @param parent
     * @param itemText
     * @return
     */
    private static DefaultMutableTreeNode add(final DefaultMutableTreeNode parent, final String itemText) {
        TreeItem treeItem = new TreeItem(itemText);
        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(treeItem);
        parent.add(childNode);
        return childNode;
    }

}
