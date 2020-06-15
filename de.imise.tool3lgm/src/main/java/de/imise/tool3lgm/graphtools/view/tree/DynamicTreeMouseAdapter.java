package de.imise.tool3lgm.graphtools.view.tree;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCommands;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.tree.node.ElementClassTreeNode;
import de.imise.tool3lgm.graphtools.view.tree.node.ElementContainerTreeNode;
import de.imise.tool3lgm.graphtools.view.tree.node.LGMTreeNode;
import de.imise.tool3lgm.graphtools.view.tree.node.UserFieldTreeNode;
import de.imise.tool3lgm.gui.menu.ContextGenerator;
import de.imise.util.swing.event.ExtendedAction;

public class DynamicTreeMouseAdapter implements MouseListener {

    private final DynamicTree tree;

    private DynamicTreeMouseAdapter(final DynamicTree tree) {
        this.tree = tree;
    }

    public static void addAdapter(final DynamicTree tree) {
        for (MouseListener listener : tree.getMouseListeners()) {
            if (listener instanceof DynamicTreeMouseAdapter) {
                return;
            }
        }
        DynamicTreeMouseAdapter dynamicTreeMouseAdapter = new DynamicTreeMouseAdapter(tree);
        tree.addMouseListener(dynamicTreeMouseAdapter);
    }

    /**
     * für die Kommunikation zwischen mousePressed und mouseClicked
     */
    private LGMTreeNode selectedNode = null;

    @Override
    public void mouseClicked(final MouseEvent e) {
        if (e.getClickCount() <= 1) {
            return;
        }
        boolean left_button = false;

        if (!Tool3lgmConstants.isPopupTrigger(e)) {
            left_button = true;
        }
        GraphDocument doc = tree.getGraphDocument();
        if (doc == null) {
            return;
        }
        // Hyprlink öffnen
        if ((e.getModifiersEx() & InputEvent.ALT_DOWN_MASK) != 0) {
            //Component source, int id, long when, int modifiers,
            //int keyCode, char keyChar, int keyLocation
            tree.dispatchEvent(new KeyEvent(tree, KeyEvent.KEY_RELEASED, 0l, 0, KeyEvent.VK_ALT, KeyEvent.CHAR_UNDEFINED, KeyEvent.KEY_LOCATION_STANDARD));
            if (left_button && selectedNode != null && selectedNode instanceof UserFieldTreeNode) {
                ((UserFieldTreeNode) selectedNode).openHyperlink();
                return;
            }
            Static.getTool().changeToLinked(doc);
            return;
        }

        // Teilobjkete zeigen oder verstecken
        if ((e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0) {
            doc.switchExpansionState(ModelBrowserTree.PID);
            return;
        }

        if (left_button && selectedNode != null && selectedNode instanceof ElementContainerTreeNode) {
            ElementContainer ec = ((ElementContainerTreeNode) selectedNode).getUserObject();
            Static.showPropertyDialog(ec);
        }
    }

    @Override
    public void mouseEntered(final MouseEvent e) {
    }

    @Override
    public void mouseExited(final MouseEvent e) {
    }

    @Override
    public void mousePressed(final MouseEvent e) {
        if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0) {
            ContextGenerator contextGenerator = tree.getContextGenerator();
            contextGenerator.setControlled(true);
        }
        boolean right_button = false;
        int xin = e.getX();
        int yin = e.getY();
        final JTree sourceTree = (JTree) e.getComponent();
        TreePath path = sourceTree.getPathForLocation(xin, yin);
        //Wenn die rechte Maustaste gedrückt wurde, wird <code>right_button</code> true;
        if (Tool3lgmConstants.isPopupTrigger(e)) {
            right_button = true;
            if (path != null) {
                LGMTreeNode lastNode = (LGMTreeNode) path.getLastPathComponent();
                // Wenn eine ElementClass rechtsgeklickt wurde, wird schon ein anderes Kontextmenü geladen,
                // so dass hier keine weiter Selektion erstellt werden muss.
                if (!(lastNode.getUserObject() instanceof ElementContainer)) {
                    tree.getSelectionModel().setSelectionPath(tree.getPathForLocation(xin, yin));
                }
            } else {
                JPopupMenu menu = new JPopupMenu();
                JMenuItem item = new JMenuItem(new AbstractAction(getResString("MODEL_BRWOSER_EXPAND_ALL")) {
                    @Override
                    public void actionPerformed(final ActionEvent arg0) {
                        for (int i = 0; i < sourceTree.getRowCount(); i++) {
                            sourceTree.expandRow(i);
                        }
                    }
                });
                menu.add(item);
                menu.show(tree, xin + 3, yin + 3);
            }
        }
        if (path != null) {
            selectedNode = (LGMTreeNode) path.getLastPathComponent();
            Object lastPathComponent = path.getLastPathComponent();
            if (tree.isLayerNode(lastPathComponent)) {
                if (right_button) {
                    ContextGenerator contextGenerator = tree.getContextGenerator();
                    JPopupMenu pm = contextGenerator.getLayerContextMenu();
                    if (pm != null) {
                        pm.show(tree, xin + 3, yin + 3);
                    }
                }
                return;
            }

            //wenn der selektierte Knoten kein ElementContainer-Knoten ist, bleibt die Variable null
            ElementContainerTreeNode selectedElementContainerTreeNode = selectedNode instanceof ElementContainerTreeNode ? (ElementContainerTreeNode) selectedNode : null;
            if (selectedElementContainerTreeNode == null) { //kein ElementContainer selektiert, sondern etwas anderes
                if (right_button) {
                    if (selectedNode instanceof ElementClassTreeNode) { //Klassenknoten?
                        Class<? extends ModelElement> elementClass = ((ElementClassTreeNode) selectedNode).getUserObject();
                        GraphDocument doc = tree.getGraphDocument();
                        if (doc != null) {
                            MetaModel metaModel = doc.getMetaModel();
                            if (metaModel.isCreatable(elementClass)) {
                                showNewInstanceContextMenu(elementClass.getSimpleName(), xin + 3, yin + 3);
                            }
                        }
                    }
                }
            } else { //ElementContainer ist selektiert
                if (right_button) {
                    ElementContainer ec = selectedElementContainerTreeNode.getUserObject();
                    //wenn das Element schon in der Selektion war, wird es nur an die hinterste Position in der Selektiion verschoben
                    //und ist somit das Element, bezüglich dessen für andere selektierte Elemente das Kontextmenü angeboten wird
                    ec.getGraphDocument().addToSelection(ec, ModelBrowserTree.PID);
                    ContextGenerator contextGenerator = tree.getContextGenerator();
                    TreePath clickedTreePath = tree.getPathForLocation(xin, yin);
                    if (clickedTreePath != null) {
                        tree.addSelectionPath(clickedTreePath); // das muss sein, damit der ContextGenerator der Templates das Doc ermitteln kann
                        JPopupMenu pm = contextGenerator.getNodeContextMenu(tree);
                        if (pm != null) {
                            pm.show(tree, xin + 3, yin + 3);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        ContextGenerator contextGenerator = tree.getContextGenerator();
        contextGenerator.setControlled(false);
    }

    /**
     * @param str
     * @return
     */
    private final JPopupMenu showNewInstanceContextMenu(final String str, final int x, final int y) {
        JPopupMenu menu = new JPopupMenu();

        ExtendedAction action = GDCommands.MODEL_ACTION_CREATE_NODE.createAction();
        String actionCommand = action.getActionCommand();
        actionCommand += " " + str;
        action.setActionCommand(actionCommand);
        JMenuItem createNodeItem = new JMenuItem(action);
        menu.add(createNodeItem);

        menu.show(tree, x, y);
        return menu;
    }

}
