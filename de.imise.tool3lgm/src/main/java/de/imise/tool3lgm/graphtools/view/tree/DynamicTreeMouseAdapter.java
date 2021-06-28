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
    private LGMTreeNode<?> selectedNode = null;

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
        GDCollection gdcoll = doc.getCollection();
        if (gdcoll instanceof DummyGDCollection) {
            return;
        }
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
                LGMTreeNode<?> lastNode = (LGMTreeNode<?>) path.getLastPathComponent();
                // Wenn eine ElementClass rechtsgeklickt wurde, wird schon ein anderes Kontextmenü geladen,
                // so dass hier keine weiter Selektion erstellt werden muss.
                if (!(lastNode.getUserObject() instanceof ElementContainer)) {
                    tree.getSelectionModel().setSelectionPath(tree.getPathForLocation(xin, yin));
                }
            } else {
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
                JMenuItem itemCloseAll = new JMenuItem(new AbstractAction(getResString("MODEL_BROWSER_CLOSE_ALL")) {
                    @Override
                    public void actionPerformed(final ActionEvent arg0) {
                        for (int i = sourceTree.getRowCount(); i >= 0; i--) {
                            sourceTree.collapseRow(i);
                        }
                    }
                });
                menu.add(itemOpenAll);
                menu.add(itemCloseAll);
                menu.show(tree, xin + 3, yin + 3);
            }
        }
        if (path != null) {
            selectedNode = (LGMTreeNode<?>) path.getLastPathComponent();
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
                        if (doc != null) {
                            MetaModel metaModel = doc.getMetaModel();
                            if (metaModel.isCreatable(elementClass)) {
                                showNewInstanceContextMenu(elementClass, xin + 3, yin + 3);
                            }
                        }
                    }
                }
                //ElementContainer ist selektiert && Rechtsklick
            } else if (right_button) {
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

    @Override
    public void mouseReleased(final MouseEvent e) {
        ContextGenerator contextGenerator = tree.getContextGenerator();
        contextGenerator.setControlled(false);
    }

    /**
     * @param elementClass
     * @param x
     * @param y
     * @return
     */
    private final JPopupMenu showNewInstanceContextMenu(final Class<? extends ModelElement> elementClass, final int x, final int y) {
        JPopupMenu menu = getNewInstanceContextMenu(elementClass);
        menu.show(tree, x, y);
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
