package de.imise.tool3lgm.graphtools.view.tree;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.gui.menu.ContextGenerator;
import de.imise.tool3lgm.tools.BrowseUtils;
import de.imise.tool3lgm.tools.LGMTreeNode;

public class DynamicTreeMouseAdapter implements MouseListener, ActionListener {

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
     * COMMENTME
     */
    private Object tmpUserObject = null;

    // für die Kommunikation zwischen mousePressed und mouseClicked

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
        // Hyprlink öffnen
        if ((e.getModifiers() & InputEvent.ALT_MASK) != 0) {
            //Component source, int id, long when, int modifiers,
            //int keyCode, char keyChar, int keyLocation
            tree.dispatchEvent(new KeyEvent(tree, KeyEvent.KEY_RELEASED, 0l, 0, KeyEvent.VK_ALT, KeyEvent.CHAR_UNDEFINED, KeyEvent.KEY_LOCATION_STANDARD));
            if (left_button && tmpUserObject != null && tmpUserObject instanceof HyperlinkString) {
                String value = ((HyperlinkString) tmpUserObject).getValue();
                BrowseUtils.browse(value);
                return;
            }
            Static.getTool().changeToLinked(doc);
            return;
        }

        // Teilobjkete zeigen oder verstecken
        if ((e.getModifiers() & InputEvent.SHIFT_MASK) != 0) {
            doc.auf_zuklappen(DynamicTree.PID);
            return;
        }

        if (left_button && tmpUserObject != null && tmpUserObject instanceof NodeContainer) {
            doc.showPropertyDialog(((NodeContainer) tmpUserObject).getElement());
        }
    }

    @Override
    public void mouseEntered(final MouseEvent e) {
    }

    @Override
    public void mouseExited(final MouseEvent e) {
    }

    @SuppressWarnings("unchecked")
    @Override
    public void mousePressed(final MouseEvent e) {
        if ((e.getModifiers() & InputEvent.CTRL_MASK) != 0) {
            Tool3lgm.getContextGenerator().setControlled(true);
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
            Object knot = ((LGMTreeNode) path.getLastPathComponent()).getUserObject();
            tmpUserObject = knot;
            Object lastPathComponent = path.getLastPathComponent();
            if (tree.isLayerNode(lastPathComponent)) {
                if (right_button) {
                    JPopupMenu pm = ContextGenerator.getLayerContextMenu();
                    if (pm != null) {
                        pm.show(tree, xin + 3, yin + 3);
                    }
                }
                return;
            }

            // TODO:FST: Actions für Item aus GlobalActionLibrary holen und setzen
            TreePath parent = path.getParentPath();
            if (parent != null) {
                if (tree.isLayerNode(parent.getLastPathComponent()) || tree.isAbstractElementNode(parent.getLastPathComponent())) {
                    if (right_button) {
                        String label = path.getLastPathComponent().toString();
                        Class<? extends ModelElement> elementClass = null;
                        for (int c = 0; c < ModelConstants.TREE_CREATABLE_DOMAIN_LAYER_NODES.length; c++) {
                            String displayName = ModelConstants.getDisplayableName(ModelConstants.TREE_CREATABLE_DOMAIN_LAYER_NODES[c]);
                            if (displayName.equals(label)) {
                                elementClass = ((Class<?>) ModelConstants.TREE_CREATABLE_DOMAIN_LAYER_NODES[c]).asSubclass(ModelElement.class);
                            }
                        }
                        for (int c = 0; c < ModelConstants.TREE_CREATABLE_LOGICAL_LAYER_NODES.length; c++) {
                            String displayName = ModelConstants.getDisplayableName(ModelConstants.TREE_CREATABLE_LOGICAL_LAYER_NODES[c]);
                            if (displayName.equals(label)) {
                                elementClass = ((Class<?>) ModelConstants.TREE_CREATABLE_LOGICAL_LAYER_NODES[c]).asSubclass(ModelElement.class);
                            }
                        }
                        for (int c = 0; c < ModelConstants.TREE_CREATABLE_PHYSICAL_LAYER_NODES.length; c++) {
                            String displayName = ModelConstants.getDisplayableName(ModelConstants.TREE_CREATABLE_PHYSICAL_LAYER_NODES[c]);
                            if (displayName.equals(label)) {
                                elementClass = ((Class<?>) ModelConstants.TREE_CREATABLE_PHYSICAL_LAYER_NODES[c]).asSubclass(ModelElement.class);
                            }
                        }
                        if (elementClass == null) {
                            return;
                        }

                        showNewInstanceContextMenu(elementClass.getSimpleName(), xin + 3, yin + 3);
                    }
                }
            }
            if (knot instanceof ElementContainer) {
                if (right_button) {
                    ElementContainer elem = (ElementContainer) knot;
                    //wenn das Element schon in der Selektion war, wird es nur an die hinterste Position in der Selektiion verschoben
                    //und ist somit das Element, bezüglich dessen für andere selektierte Elemente das Kontextmenü angeboten wird
                    elem.getGraphDocument().addToSelection(elem, DynamicTree.PID);
                    JPopupMenu pm = Tool3lgm.getContextGenerator().getKnotContextMenu(tree);
                    if (pm != null) {
                        pm.show(tree, xin + 3, yin + 3);
                    }
                    return;
                }
            }
        }
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        Tool3lgm.getContextGenerator().setControlled(false);
    }

    /**
     * @param str
     * @return
     */
    private final JPopupMenu showNewInstanceContextMenu(final String str, final int x, final int y) {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem item;

        item = new JMenuItem(getResString("neue_instanz"));
        item.addActionListener(this);
        item.setActionCommand("newInstanze " + str);
        menu.add(item);
        Tool3lgm.setLastActionPosition(x + tree.getX(), y + tree.getY());
        menu.show(tree, x, y);

        return menu;
    }

    @Override
    public final void actionPerformed(final ActionEvent e) {
        GraphDocument doc = tree.getGraphDocument();
        if (doc == null) {
            return;
        }
        String str = e.getActionCommand();
        if (str.startsWith("newInstanze ")) {
            StringTokenizer s = new StringTokenizer(str, " ");
            if (s.countTokens() < 2) {
                return;
            }
            s.nextToken();
            String klassenname = s.nextToken();
            doc.createKnotenWithContainer(ModelConstants.NODE_PACKAGE_NAME + klassenname, DynamicTree.PID);
            return;
        }
    }

}
