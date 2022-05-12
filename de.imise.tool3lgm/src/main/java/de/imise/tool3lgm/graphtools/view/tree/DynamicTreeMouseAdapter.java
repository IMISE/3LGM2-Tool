package de.imise.tool3lgm.graphtools.view.tree;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.DummyGDCollection;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GDCommands;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.userfield.definition.SubType;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.tree.node.ElementClassTreeNode;
import de.imise.tool3lgm.graphtools.view.tree.node.ElementContainerTreeNode;
import de.imise.tool3lgm.graphtools.view.tree.node.LGMTreeNode;
import de.imise.tool3lgm.graphtools.view.tree.node.UserFieldTreeNode;
import de.imise.tool3lgm.gui.menu.ContextGenerator;
import de.imise.util.swing.event.ExtendedAction;

/**
 * @author N.N. (??.??.200?), AXS
 */
public class DynamicTreeMouseAdapter implements MouseListener {

    /** The target tree for this mouse adapter */
    private final DynamicTree tree;

    /**
     * für die Kommunikation zwischen mousePressed und mouseClicked
     */
    private LGMTreeNode<?> selectedNode = null;

    /**
     * @param tree
     */
    private DynamicTreeMouseAdapter(final DynamicTree tree) {
        this.tree = tree;
    }

    /**
     * @param tree
     */
    public static void addAdapter(final DynamicTree tree) {
        for (MouseListener listener : tree.getMouseListeners()) {
            if (listener instanceof DynamicTreeMouseAdapter) {
                return;
            }
        }
        DynamicTreeMouseAdapter dynamicTreeMouseAdapter = new DynamicTreeMouseAdapter(tree);
        tree.addMouseListener(dynamicTreeMouseAdapter);
    }

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
        tree.dispatchEvent(new KeyEvent(tree, KeyEvent.KEY_RELEASED, 0l, 0, KeyEvent.VK_ALT, KeyEvent.CHAR_UNDEFINED, KeyEvent.KEY_LOCATION_STANDARD));
        if (left_button && selectedNode != null && selectedNode instanceof UserFieldTreeNode) {
            ((UserFieldTreeNode) selectedNode).openUserFieldEditorOrTarget();
            return;
        }
        if ((e.getModifiersEx() & InputEvent.ALT_DOWN_MASK) != 0) {
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
        GraphDocument doc = tree.getGraphDocument(e);
        GDCollection gdcoll = doc == null ? null : doc.getCollection();
        if (gdcoll instanceof DummyGDCollection) {
            return;
        }

        int xin = e.getX();
        int yin = e.getY();
        final JTree sourceTree = (JTree) e.getComponent();
        TreePath path = sourceTree.getPathForLocation(xin, yin);
        selectedNode = path == null ? null : (LGMTreeNode<?>) path.getLastPathComponent();
        if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0) {
            ContextGenerator contextGenerator = tree.getContextGenerator();
            contextGenerator.setControlled(true);
        }
        if (Tool3lgmConstants.isPopupTrigger(e)) {
            JPopupMenu menu = null;
            //Wenn die rechte Maustaste gedrückt wurde, wird <code>right_button</code> true;
            if (path == null) { //right click to an empty space in the tree (and not to a tree path)
                menu = getExpandCollapseTreeContectMenu(sourceTree);
            } else {
                // we must explicitely select the tree node if it was only right clicked
                if (!(selectedNode instanceof ElementContainerTreeNode)) {
                    tree.getSelectionModel().setSelectionPath(tree.getPathForLocation(xin, yin));
                }
                Object lastPathComponent = path.getLastPathComponent();
                if (tree.isLayerNode(lastPathComponent)) {
                    ContextGenerator contextGenerator = tree.getContextGenerator();
                    menu = contextGenerator.getLayerContextMenu();
                } else {
                    //wenn der selektierte Knoten kein ElementContainer-Knoten ist, bleibt die Variable null
                    ElementContainerTreeNode selectedElementContainerTreeNode = selectedNode instanceof ElementContainerTreeNode ? (ElementContainerTreeNode) selectedNode : null;
                    if (selectedNode instanceof ElementClassTreeNode) {
                        Class<? extends ModelElement> elementClass = ((ElementClassTreeNode) selectedNode).getUserObject();
                        if (doc != null) {
                            MetaModel metaModel = doc.getMetaModel();
                            if (metaModel.isCreatable(elementClass)) {
                                menu = getNewInstanceContextMenu(elementClass);
                            }
                        }
                    } else {
                        if (selectedElementContainerTreeNode != null) {
                            ElementContainer ec = selectedElementContainerTreeNode.getUserObject();
                            //wenn das Element schon in der Selektion war, wird es nur an die hinterste Position in der Selektiion verschoben
                            //und ist somit das Element, bezüglich dessen für andere selektierte Elemente das Kontextmenü angeboten wird
                            ec.getGraphDocument().addToSelection(ec, ModelBrowserTree.PID);
                        }
                        menu = callContextGenerator(xin, yin);
                    }
                }
            }
            showMenu(menu, xin, yin);
        }
    }

    /**
     * @param xin
     * @param yin
     * @return
     */
    private JPopupMenu callContextGenerator(int xin, int yin) {
        tree.setClickedTreePathForLocation(xin, yin);
        ContextGenerator contextGenerator = tree.getContextGenerator();
        return contextGenerator.getNodeContextMenu(tree);
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        ContextGenerator contextGenerator = tree.getContextGenerator();
        contextGenerator.setControlled(false);
    }

    /**
     * @param menu
     * @param xin
     * @param yin
     * @return
     */
    private final void showMenu(JPopupMenu menu, int xin, int yin) {
        if (menu != null) {
            menu.show(tree, xin + 3, yin + 3);
        }
    }

    /**
     * @param sourceTree
     * @return
     */
    protected JPopupMenu getExpandCollapseTreeContectMenu(final JTree sourceTree) {
        JPopupMenu menu = new JPopupMenu();
        // Menu item for expanding all trees
        JMenuItem itemOpenAll = new JMenuItem(new AbstractAction(getResString("MODEL_BROWSER_EXPAND_ALL")) {
            @Override
            public void actionPerformed(final ActionEvent arg0) {
                for (int i = 0; i < sourceTree.getRowCount(); i++) {
                    sourceTree.expandRow(i);
                }
            }
        });
        // Menu item for closing all trees
        JMenuItem itemCloseAll = new JMenuItem(new AbstractAction(getResString("MODEL_BROWSER_COLLAPSE_ALL")) {
            @Override
            public void actionPerformed(final ActionEvent arg0) {
                for (int i = sourceTree.getRowCount(); i >= 0; i--) {
                    sourceTree.collapseRow(i);
                }
            }
        });
        menu.add(itemOpenAll);
        menu.add(itemCloseAll);
        return menu;
    }

    /**
     * @param elementClass
     * @return
     */
    private final JPopupMenu getNewInstanceContextMenu(final Class<? extends ModelElement> elementClass) {
        JPopupMenu menu = new JPopupMenu();
        GraphDocument doc = tree.getGraphDocument();
        UserFieldDefinitions userFieldDefinitions = doc.getUserFieldDefinitions();
        List<SubType> subTypes = userFieldDefinitions.getSubTypes(elementClass);
        if (subTypes.isEmpty()) {
            JMenuItem createNodeItem = getCreateTypeItem(elementClass, null, null);
            menu.add(createNodeItem);
        } else {
            String newString = getResString("new");
            JLabel newLabel = new JLabel(newString);
            menu.add(newLabel);
            List<JMenuItem> menuItemsWithSubTypes = getMenuItemsWithSubTypes(elementClass, subTypes);
            for (JMenuItem item : menuItemsWithSubTypes) {
                menu.add(item);
            }
        }
        return menu;
    }

    /**
     * @param elementClass
     * @return
     */
    private JMenuItem getCreateTypeItem(final Class<? extends ModelElement> elementClass, final SubType subType, final String itemText) {
        ExtendedAction action = GDCommands.MODEL_ACTION_CREATE_NODE.createAction();
        String actionCommand = action.getActionCommand();
        actionCommand += " " + elementClass.getSimpleName();
        if (subType != null) {
            actionCommand += " " + subType.getID();
        }
        action.setActionCommand(actionCommand);
        JMenuItem createNodeItem = new JMenuItem(action);
        if (itemText != null) {
            createNodeItem.setText(itemText);
        }
        return createNodeItem;
    }

    /**
     * @param elementClass
     * @param subTypes
     * @return
     */
    private List<JMenuItem> getMenuItemsWithSubTypes(final Class<? extends ModelElement> elementClass, final List<SubType> subTypes) {
        List<JMenuItem> items = new ArrayList<>();
        ElementsNameBuilder elementsNameBuilder = tree.getElementsNameBuilder();
        String superTypeName = elementsNameBuilder.getDisplayableName(elementClass);
        JMenuItem createSuperTypeItem = getCreateTypeItem(elementClass, null, superTypeName);
        items.add(createSuperTypeItem);
        for (SubType subType : subTypes) {
            JMenuItem createSubTypeItem = getCreateTypeItem(elementClass, subType, ContextGenerator.STANDARD_SUBITEMS_INDENTATION + subType.getName());
            items.add(createSubTypeItem);
        }
        return items;
    }

}
