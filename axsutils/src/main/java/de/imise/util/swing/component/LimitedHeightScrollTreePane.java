/*
 * Created on 02.09.2003
 * To change this generated comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.imise.util.swing.component;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;

/**
 * Wird maxLines >0 angegeben, dann wird das TextPane nie größer als diese Zeilenanzahl
 * sondern es erscheint ein ScrollPane. Bei maxLines kleiner 1 vergrößert sich das Pane
 * beliebig, ohne dass ein ScrollPane erscheint.
 */
public class LimitedHeightScrollTreePane extends JScrollPane {

    private static int BORDER = 3;

    /**
     * Maximale Zeilenanzahl, auf die sich die Komponente vergrößert, bevor das ScrollPane angezeigt wird.
     */
    private final int maxLines;

    /**
     * Der eigentliche Tree
     */
    private final JTree tree;

    /**
     * Höhe einer Zeile im gerenderten Baum
     */
    private final int singleLineHeight;

    /**
     * Maximale Höhe des Baumes
     */
    private final int maxHeight;

    /**
     *
     */
    public LimitedHeightScrollTreePane() {
        this(-1);
    }

    /**
     * @param editable
     */
    public LimitedHeightScrollTreePane(final boolean editable) {
        this(-1, editable);
    }

    /**
     * @param maxLines
     */
    public LimitedHeightScrollTreePane(final int maxLines) {
        this(maxLines, true);
    }

    /**
     * @param maxLines
     * @param editable
     */
    public LimitedHeightScrollTreePane(final int maxLines, final boolean editable) {
        this(new JTree(), maxLines, editable, false);
    }

    /**
     * @param tree
     * @param maxLines
     * @param renderTreeAsList
     *            wenn <code>true</code>, zeigt der Baum alle Einträge ohne Einrückung wie in einer Liste an. Bei <code>false</code> bleibt die
     *            originale Einrückung und Darstellung der Linien erhalten.
     */
    public LimitedHeightScrollTreePane(final JTree tree, final int maxLines, final boolean renderTreeAsList) {
        this(tree, maxLines, tree.isEditable(), renderTreeAsList);
    }

    /**
     * @param tree
     * @param maxLines
     * @param editable
     * @param renderTreeAsList
     *            wenn <code>true</code>, zeigt der Baum alle Einträge ohne Einrückung wie in einer Liste an. Bei <code>false</code> bleibt die
     *            originale Einrückung und Darstellung der Linien erhalten.
     */
    public LimitedHeightScrollTreePane(final JTree tree, final int maxLines, final boolean editable, final boolean renderTreeAsList) {
        this.tree = tree;
        if (renderTreeAsList) {
            BasicTreeUI basicTreeUI = (BasicTreeUI) tree.getUI();
            basicTreeUI.setRightChildIndent(0);
            basicTreeUI.setLeftChildIndent(0); //kleinen Abstand zwischen Rand und erstem Buchstaben lassen
            tree.setBorder(BorderFactory.createEmptyBorder(BORDER, BORDER, BORDER, BORDER));
            tree.putClientProperty("JTree.lineStyle", "None");
        }
        tree.setEditable(editable);
        this.maxLines = maxLines;
        singleLineHeight = maxLines > 0 ? getSingleLineHeigth() : -1; //braucht nicht berechnet werden, wenn die Komponente gar nicht in der Höhe eingeschränkt werden soll
        maxHeight = maxLines > 0 ? getHeight(maxLines) : Integer.MAX_VALUE;
        setViewportView(tree);
    }

    /**
     * @return
     */
    private int getSingleLineHeigth() {
        TreeCellRenderer cellRenderer = tree.getCellRenderer();
        Component treeCellRendererComponent = cellRenderer.getTreeCellRendererComponent(tree, "Any String", false, false, true, 0, false);
        Dimension preferredSize = treeCellRendererComponent.getPreferredSize();
        return preferredSize.height + 2; //Standardhöhe der Knoten braucht 2 Pixel mehr, damit das ScrollPane nicht erscheint. Wahrscheinlich wird zwischen die Baumknoten noch irgendein Gap oben und unten von jeweils 1 gerendert. Ich habe diesen Wert aber nicht in der Compoente gefunden
    }

    /**
     * @param lineCount
     * @return
     */
    private int getHeight(final int lineCount) {
        return singleLineHeight * lineCount + 2 * BORDER;
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getPreferredSize() {
        TreeModel treeModel = tree.getModel();
        Object root = treeModel.getRoot();
        if (maxLines > 0) {
            if (root instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) root;
                int rootChildCount = Math.max(rootNode.getChildCount(), 1); //mind. eine Zeile soll dargestellt werden
                Dimension maximumSize = getMaximumSize();
                int currentHeight = getHeight(rootChildCount);
                maximumSize.height = Math.min(maximumSize.height, currentHeight);
                return maximumSize;
            }
        }
        return super.getPreferredSize();
    }

    @Override
    public Dimension getMaximumSize() {
        if (maxLines > 0) {
            Dimension treeMaximumSize = tree.getMaximumSize();
            return new Dimension(treeMaximumSize.width, maxHeight);
        }
        return super.getMaximumSize();
    }

    /**
     * @return
     */
    public JTree getTree() {
        return (JTree) getViewport().getView();
    }

    /**
     * @param b
     */
    public void setEditable(final boolean b) {
        tree.setEditable(b);
    }

    @Override
    public synchronized void addMouseListener(final MouseListener l) {
        super.addMouseListener(l);
        tree.addMouseListener(l);
    }

}