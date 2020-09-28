package de.imise.tool3lgm.graphtools.dialog.element.panel;

import java.awt.event.MouseEvent;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.action.LGMActionLibrary;
import de.imise.tool3lgm.graphtools.dialog.action.LGMTreeSelectionListener;
import de.imise.tool3lgm.graphtools.dialog.element.AbstractElementPropertyDialog;
import de.imise.tool3lgm.graphtools.path.metapaths.MetaPath;
import de.imise.tool3lgm.graphtools.view.tree.node.LGMTreeNode;

/**
 * Ein Panel das davon ausgeht, dass es mind einen Baum hat, der wenn er mit der Maus angeklickt wird eine Selektion zurück liefern kann.
 *
 * @author AXS
 * @created 22.06.2017
 */
public abstract class AbstractPathConnectionTreePanel extends AbstractPathConnectionPanel {

    public AbstractPathConnectionTreePanel(final AbstractElementPropertyDialog dialog, final MetaPath metaPath) {
        super(dialog, metaPath);
    }

    public AbstractPathConnectionTreePanel(final AbstractElementPropertyDialog dialog, final PanelLabelOption titleLabelOption, final PanelLabelOption westLabelOption, final MetaPath metaPath) {
        super(dialog, titleLabelOption, westLabelOption, metaPath);
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
        Object source = e.getSource();
        if (source instanceof JTree) { //can be an JLabel or JTree and a label has no selection
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
        }
        return selection;
    }

}
