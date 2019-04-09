package de.imise.tool3lgm.graphtools.dialog.panel;

import java.awt.event.MouseEvent;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.action.LGMActionLibrary;
import de.imise.tool3lgm.graphtools.dialog.action.LGMTreeSelectionListener;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.view.tree.node.LGMTreeNode;

/**
 * Ein Panel das davon ausgeht, dass es mind einen Baum hat, der wenn er mit der Maus angeklickt wird eine Selektion zurück liefern kann.
 *
 * @author AXS
 * @created 22.06.2017
 */
public abstract class AbstractPathConnectionTreePanel extends AbstractPathConnectionPanel {

    public AbstractPathConnectionTreePanel(final ElementPropertyDialog dialog, final SimpleMetaPath simpleMetaPath) {
        super(dialog, simpleMetaPath);
    }

    public AbstractPathConnectionTreePanel(final ElementPropertyDialog dialog, final boolean labelEdgeName, final SimpleMetaPath simpleMetaPath) {
        super(dialog, labelEdgeName, simpleMetaPath);
    }

    public AbstractPathConnectionTreePanel(final ElementPropertyDialog dialog, final int searchEdgeIndex, final boolean labelEdgeName, final SimpleMetaPath simpleMetaPath) {
        super(dialog, searchEdgeIndex, labelEdgeName, simpleMetaPath);
    }

    protected void addListener(final JTree tree) {
        addMouseActions(tree);
        addTreeSelectionListener(tree);
    }

    private void addTreeSelectionListener(final JTree tree) {
        LGMAction treeSelectionAction = LGMActionLibrary.getTreeSelectionAction(tree, this);
        TreeSelectionListener treeSelectionListener = new LGMTreeSelectionListener(treeSelectionAction);
        tree.addTreeSelectionListener(treeSelectionListener);
    }

    @Override
    protected Object getSelection(final MouseEvent e) {
        //find selection
        Object selection = null;
        JTree tree = (JTree) e.getSource();
        TreePath path = tree.getPathForLocation(e.getX(), e.getY());
        if (path == null) {
            return null;
        }
        //set selection in tree
        tree.setSelectionPath(path);
        LGMTreeNode node = (LGMTreeNode) path.getLastPathComponent();
        if (node == null) {
            return null;
        }
        //return selected object
        selection = node.getUserObject();
        return selection;
    }

}
