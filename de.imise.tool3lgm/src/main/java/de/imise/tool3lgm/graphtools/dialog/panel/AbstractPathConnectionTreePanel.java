package de.imise.tool3lgm.graphtools.dialog.panel;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.action.LGMActionLibrary;
import de.imise.tool3lgm.graphtools.dialog.action.LGMMouseListener;
import de.imise.tool3lgm.graphtools.dialog.action.LGMTreeSelectionListener;
import de.imise.tool3lgm.graphtools.metamodel.Edge;
import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.tools.LGMTreeNode;

/**
 * Ein Panel das davon ausgeht, dass es mind einen Baum hat, der wenn er mit der Maus angeklickt wird eine Selektion zurück liefern kann.
 *
 * @author astruebi
 * @created 22.06.2017
 */
public abstract class AbstractPathConnectionTreePanel extends AbstractPathConnectionPanel {

    public AbstractPathConnectionTreePanel(final ElementPropertyDialog dialog, final Class<? extends Edge>... edgeClasses) {
        super(dialog, edgeClasses);
    }

    public AbstractPathConnectionTreePanel(final ElementPropertyDialog dialog, final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge>... edgeClasses) {
        super(dialog, searchElementClass, edgeClasses);
    }

    public AbstractPathConnectionTreePanel(final ElementPropertyDialog dialog, final boolean labelEdgeName, final Class<? extends Edge>... edgeClasses) {
        super(dialog, labelEdgeName, edgeClasses);
    }

    public AbstractPathConnectionTreePanel(final ElementPropertyDialog dialog, final boolean labelEdgeName, final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge>... edgeClasses) {
        super(dialog, labelEdgeName, searchElementClass, edgeClasses);
    }

    public AbstractPathConnectionTreePanel(final ElementPropertyDialog dialog, final int searchEdgeIndex, final boolean labelEdgeName, final Class<? extends Edge>... edgeClasses) {
        super(dialog, searchEdgeIndex, labelEdgeName, edgeClasses);
    }

    public AbstractPathConnectionTreePanel(final ElementPropertyDialog dialog, final int labelEdgeIndex, final boolean labelEdgeName, final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge>... edgeClasses) {
        super(dialog, labelEdgeIndex, labelEdgeName, searchElementClass, edgeClasses);
    }

    protected void addListener(final JTree tree) {
        addMouseListener(tree);
        addTreeSelectionListener(tree);
    }

    private void addMouseListener(final JTree tree) {
        LGMAction mousePressedAction = getMouseClickedAction();
        MouseListener mousePressedListener = new LGMMouseListener(null, null, null, mousePressedAction, null);
        tree.addMouseListener(mousePressedListener);
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
