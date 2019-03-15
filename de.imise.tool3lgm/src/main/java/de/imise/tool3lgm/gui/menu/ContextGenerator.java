package de.imise.tool3lgm.gui.menu;

import static de.imise.tool3lgm.Static.getCollections;
import static de.imise.tool3lgm.Static.getPreSelectedGDCollection;
import static de.imise.tool3lgm.Tool3lgmConstants.getIcon;
import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.ElementsNameBuilder.getBackwardMetaAssociationName;
import static de.imise.tool3lgm.graphtools.ElementsNameBuilder.getForwardMetaAssociationName;
import static de.imise.tool3lgm.graphtools.ElementsNameBuilder.getFullBackwardMetaAssociationName;
import static de.imise.tool3lgm.graphtools.ElementsNameBuilder.getFullForwardMetaAssociationName;
import static de.imise.tool3lgm.graphtools.ElementsNameBuilder.getFullMetaAssociationName;
import static de.imise.tool3lgm.graphtools.ElementsNameBuilder.getMetaAssociationName;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.CREATABLE_DOMAIN_LAYER_NODES;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.CREATABLE_LOGICAL_LAYER_NODES;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.CREATABLE_PHYSICAL_LAYER_NODES;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.getClassForName;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.getEdgeTypes;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.getEndClass;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.getStartClass;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.isConnectingForward;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.isStartClass;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction.BACKWARD;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction.FORWARD;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_ADD_SELECTED_TO_ALL_SUBMODELS;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_ADD_SELECTED_TO_NEW_SUBMODEL;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_ADD_SELECTED_TO_SUBMODEL;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_COMMAND_LINE;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_CREATE_ADDICTED;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_CREATE_INSTANCIATION;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_CREATE_NODE;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_DELETE_FROM_MODEL;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_DELETE_FROM_SUBMODEL;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_INTERNAL_CHECK_CONSISTENCY;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_JOIN_SELECTED;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_LINK;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_LINK_SELECTED_TO_NEW_SUBMODEL;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_LINK_SELECTED_TO_SUBMODEL;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_PRINT_QUEUE;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_SELECT_LINKED_SUBMODEL;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_SET_ELEMENT_EXPANSION_OFF;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_SET_ELEMENT_EXPANSION_ON;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_SET_ELEMENT_VISIBILITY_OFF;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_SET_ELEMENT_VISIBILITY_ON;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_SET_LAYER_COLOR;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_SET_LAYER_DEFAULT_COLOR_AND_TRANSPARENCY;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_SET_LAYER_TRANSPARENCY_FULL;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_SET_LAYER_TRANSPARENCY_HALF;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_SET_LAYER_TRANSPARENCY_NONE;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_UNLINK;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_UNLINK_SELECTED_TO_SUBMODEL;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_OPTION_GDCOLL_INTERACTIVE_MODE;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_OPTION_GDOC_VERIFICATION_MODE;
import static de.imise.tool3lgm.graphtools.undoredo.TransactionManager.STANDARD_PID;
import static javax.swing.BoxLayout.Y_AXIS;
import static javax.swing.JOptionPane.DEFAULT_OPTION;
import static javax.swing.JOptionPane.PLAIN_MESSAGE;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.event.ActionLibrary;
import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.analyse.context.AbstractAnalyse;
import de.imise.tool3lgm.graphtools.analyse.context.AnalyseRepository;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.CompositionEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge.ConnectionState;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.HasPartEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.InstanciationEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Knickpunkt;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.metamodel.elements.Textfield;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GDCommands;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.graphtools.view.container.BendpointContainer;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.InterLayerConnectedNodeContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.graphtools.view.graph.InputGraphArea;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty;
import de.imise.util.Alphabetical;
import de.imise.util.NamedObjectContainer;
import de.imise.util.Pair;
import de.imise.util.swing.menu.DynamicMenu;
import de.imise.util.swing.menu.MenuScroller;

/**
 * @author N.N., Thomas, AXS
 */
public class ContextGenerator implements PopupMenuListener, ActionListener {

    /**
     * COMMENTME
     */
    static JMenu new_logical_tree, new_domain_tree, new_physical_tree;

    /**
     * COMMENTME
     */
    private JMenu internals;

    /**
     * COMMENTME
     */
    private JMenuItem delete_selected, delete_selected_from_szenario, join_selected;

    /**
     * COMMENTME
     */
    private JMenuItem properties;

    /**
     * COMMENTME
     */
    private JMenuItem new_text;

    /**
     * COMMENTME
     */
    private JMenuItem unlinkToSzenario, selectLinkedSzenario;

    /**
     * COMMENTME
     */
    private JMenuItem command_line;

    /**
     * COMMENTME
     */
    private JMenuItem nicht_trans_layer, halb_trans_layer, voll_trans_layer;

    /**
     * COMMENTME
     */
    private JMenuItem color_layer, normalize_layer, layout_layer;

    /**
     * COMMENTME
     */
    private JMenuItem show_configs, hide_configs, set_visible, set_invisible;

    /**
     * COMMENTME
     */
    private JMenuItem layer_show_configs, layer_hide_configs;

    /**
     * COMMENTME
     */
    private JMenuItem expand, collapse;

    /**
     * COMMENTME
     */
    private JMenuItem cb_copy, cb_cut, cb_paste, cb_clear;

    /**
     * COMMENTME
     */
    private JMenuItem undo, redo, queue, consistency;

    /**
     * COMMENTME
     */
    private LGMGraphDocument doc;

    /**
     * COMMENTME
     */
    private boolean controlled = false;

    /**
     * COMMENTME
     */
    private JPopupMenu menu = null;

    /**
     * COMMENTME
     */
    private boolean resizing = false;

    /** Icon für das Herstellen einer Verbindung */
    static ImageIcon verbindung_anlegen = getIcon("verbindung_anlegen.gif");

    /** Icon für das Trennen einer Verbindung */
    static ImageIcon verbindung_trennen = getIcon("verbindung_trennen.gif");

    /** Element, das den Kontekt vorgibt, also das Element auf das sich die Aktionen beziehen. */
    private ElementContainer mc = null;

    /**
     * Konstruktor, den Tool3lgm am Anfang aufruft. Der ContextListener und das
     * GrapDocument sind erstmal egal, da sie beim ersten aktivieren über
     * changeContext(GraphDocument) eines InternalFrames auf korrekte Werte
     * gesetzt werden.
     */
    public ContextGenerator() {
        setControlled(false);
        init();
    }

    /**
     * @param b
     */
    public final void setControlled(final boolean b) {
        controlled = b;
    }

    /**
     * @return
     */
    public GraphDocument getDoc() {
        return doc;
    }

    /**
     * Setzt das GraphDocument auf das übergebene und tauscht bei allen
     * MenuItems den ContextListener aus
     *
     * @param GraphDocument
     */
    public void changeContext(final LGMGraphDocument document) {
        doc = document;
    }

    // --- Methoden zur Statusveraenderung --- Ende ---

    private JMenu createLayerMenu(final Iterable<Class<? extends ModelElement>> creatableLayerNodes) {
        JMenu layerMenu = new JMenu(getResString("el_neu"));
        for (Class<? extends ModelElement> elementClass : creatableLayerNodes) {
            JMenuItem item = new JMenuItem(ElementsNameBuilder.getDisplayableName(elementClass));
            item.addActionListener(this);
            item.setActionCommand(MODEL_ACTION_CREATE_NODE + " " + elementClass.getName());
            layerMenu.add(item);
        }
        return layerMenu;
    }

    /**
     *
     */
    private void init() {
        new_domain_tree = createLayerMenu(CREATABLE_DOMAIN_LAYER_NODES);
        new_logical_tree = createLayerMenu(CREATABLE_LOGICAL_LAYER_NODES);
        new_physical_tree = createLayerMenu(CREATABLE_PHYSICAL_LAYER_NODES);
        new_text = getItem("text_neu", MODEL_ACTION_CREATE_NODE, Textfield.class.getName());

        properties = getItem(ActionLibrary.ContextActions.ACTION_SHOW_ELEMENT_PROPERTY_DIALOG);
        unlinkToSzenario = getItem(MODEL_ACTION_UNLINK_SELECTED_TO_SUBMODEL);
        selectLinkedSzenario = getItem("selectLinkedSzenario", MODEL_ACTION_SELECT_LINKED_SUBMODEL);
        delete_selected = getItem(MODEL_ACTION_DELETE_FROM_MODEL);
        // der leere Argumentstring bewirkt, dass am Ende ein Leerzeichen angehängt wird, hinter das dann die Hashes der zulöschenden Elemnte kommen
        delete_selected_from_szenario = getItem(MODEL_ACTION_DELETE_FROM_SUBMODEL);

        join_selected = getItem(MODEL_ACTION_JOIN_SELECTED);

        JMenuItem verify = getItem(MODEL_OPTION_GDOC_VERIFICATION_MODE);
        JMenuItem interactive = getItem(MODEL_OPTION_GDCOLL_INTERACTIVE_MODE);
        JMenuItem expertMode = UserProperties.BooleanProperty.OPTION_ENABLE_EXPERT_MODE.createAction().createMenuItem();

        command_line = getItem(MODEL_ACTION_COMMAND_LINE);
        queue = getItem(MODEL_ACTION_PRINT_QUEUE);
        consistency = getItem(MODEL_ACTION_INTERNAL_CHECK_CONSISTENCY);

        internals = new DynamicMenu(getResString("intern"));
        internals.add(verify);
        internals.add(interactive);
        internals.add(expertMode);
        internals.addSeparator();
        internals.add(command_line);
        internals.addSeparator();
        internals.add(queue);
        internals.add(consistency);

        // weiter mit Grafik-Sachen
        normalize_layer = getItem(MODEL_ACTION_SET_LAYER_DEFAULT_COLOR_AND_TRANSPARENCY);
        voll_trans_layer = getItem(MODEL_ACTION_SET_LAYER_TRANSPARENCY_FULL);
        halb_trans_layer = getItem(MODEL_ACTION_SET_LAYER_TRANSPARENCY_HALF);
        nicht_trans_layer = getItem(MODEL_ACTION_SET_LAYER_TRANSPARENCY_NONE);
        color_layer = getItem(MODEL_ACTION_SET_LAYER_COLOR);

        JMenu trans_layer = new JMenu(getResString("layerTransparencyMenu"));
        trans_layer.add(nicht_trans_layer);
        trans_layer.add(halb_trans_layer);
        trans_layer.add(voll_trans_layer);

        layout_layer = new JMenu(getResString("layerLayoutMenu"));
        layout_layer.add(normalize_layer);
        layout_layer.add(color_layer);
        layout_layer.add(trans_layer);

        show_configs = getItem(ActionLibrary.ContextActions.MODEL_ACTION_SET_ELEMENT_INTERLAYER_CONNECTIONS_VISIBILITY_ON);
        hide_configs = getItem(ActionLibrary.ContextActions.MODEL_ACTION_SET_ELEMENT_INTERLAYER_CONNECTIONS_VISIBILITY_OFF);
        set_visible = getItem(MODEL_ACTION_SET_ELEMENT_VISIBILITY_ON);
        set_invisible = getItem(MODEL_ACTION_SET_ELEMENT_VISIBILITY_OFF);

        layer_show_configs = getItem(ActionLibrary.ContextActions.MODEL_ACTION_SET_LAYER_INTERLAYER_CONNECTIONS_VISIBILITY_ON);
        layer_hide_configs = getItem(ActionLibrary.ContextActions.MODEL_ACTION_SET_LAYER_INTERLAYER_CONNECTIONS_VISIBILITY_OFF);
        expand = getItem(MODEL_ACTION_SET_ELEMENT_EXPANSION_ON);
        collapse = getItem(MODEL_ACTION_SET_ELEMENT_EXPANSION_OFF);

    }

    /**
     * @param resKeyOrString
     * @param command
     * @param arguments
     * @param icon
     * @param enabled
     * @param toolTip
     * @return
     */
    private JMenuItem getItem(final String resKeyOrString, final GDCommands command, final String arguments, final ImageIcon icon, final boolean enabled, final String toolTip) {
        String label = null;
        label = Tool3lgmConstants.getResStringWithoutError(resKeyOrString);
        JMenuItem item = new JMenuItem(label, icon);
        item.addActionListener(this);
        if (arguments == null) {
            item.setActionCommand(command.toString());
        } else {
            item.setActionCommand(command + " " + arguments);
        }
        item.setEnabled(enabled);
        item.setToolTipText(toolTip);
        return item;
    }

    /**
     * @param resKeyOrString
     * @param command
     * @param arguments
     * @param icon
     * @return
     */
    private JMenuItem getItem(final String resKeyOrString, final GDCommands command, final String arguments, final ImageIcon icon) {
        return getItem(resKeyOrString, command, arguments, icon, true, null);
    }

    /**
     * @param resKey
     * @param command
     * @param arguments
     * @return
     */
    private JMenuItem getItem(final String resKey, final GDCommands command, final String arguments) {
        return getItem(resKey, command, arguments, null);
    }

    /**
     * @param resKey
     * @param command
     * @return
     */
    private JMenuItem getItem(final String resKey, final GDCommands command) {
        return getItem(resKey, command, null);
    }

    /**
     * @param command
     * @return
     */
    private JMenuItem getItem(final GDCommands command) {
        if (command.isModelOption()) {
            return new JCheckBoxMenuItem(command.createAction());
        }
        return getItem(command.name(), command);
    }

    /**
     * @param command
     * @return
     */
    private JMenuItem getItem(final Action action) {
        return new JMenuItem(action);
    }

    /**
     * Liefert das Menü für die untergeordneten Elemente.
     *
     * @return
     */
    private JMenu getSubElemMenu() {
        ModelElement selected = doc.getLastSelected().getElement();
        JMenu sub_elem = new JMenu(getResString("unterg_el"));
        HashSet<Pair<Class<? extends CompositionEdge>, Class<? extends ModelElement>>> slavePairs = new HashSet<>();
        for (Class<? extends CompositionEdge> compositionClass : ModelConstants.getCompositionEdgeTypesForMaster(selected.getClass())) {
            Class<? extends ModelElement> abstractSlaves = CompositionEdge.getSlaveType(compositionClass);
            for (Class<? extends ModelElement> instanciableSlaves : ModelConstants.getInstanciableAssignableClasses(abstractSlaves)) {
                slavePairs.add(new Pair<Class<? extends CompositionEdge>, Class<? extends ModelElement>>(compositionClass, instanciableSlaves));
            }
        }

        if (slavePairs.size() == 0) {
            return sub_elem;
        }

        ArrayList<JMenuItem> items = new ArrayList<>(slavePairs.size());

        for (Pair<Class<? extends CompositionEdge>, Class<? extends ModelElement>> slavePair : slavePairs) {
            Class<? extends CompositionEdge> compositionClass = slavePair.getFirstItem();
            JMenuItem item = getItem(slavePair.getSecondItem().getSimpleName(), MODEL_ACTION_CREATE_ADDICTED, doc.getHashString() + " " + selected.getHashString() + " " + compositionClass.getSimpleName() + " " + slavePair.getSecondItem().getSimpleName());
            item.setEnabled(selected.countConnections(compositionClass) < CompositionEdge.getMaxMasterToSlaveCardinality(compositionClass));
            items.add(item);
        }
        Alphabetical.sort(items);
        for (JMenuItem item : items) {
            sub_elem.add(item);
        }
        return sub_elem;
    }

    /**
     * Liefert das Menü, mit dem selektierte Elemente in andere Teilmodelle
     * übernommen werden können.
     *
     * @return
     */
    private JMenu getAddToSzenarioMenu() {
        JMenu szenario_menu = new JMenu(getResString("inszenario"));
        JMenuItem item = getItem(MODEL_ACTION_ADD_SELECTED_TO_NEW_SUBMODEL);
        szenario_menu.add(item);
        szenario_menu.add(new JSeparator());

        GDCollection gdcoll = doc.getCollection();

        for (Szenario szen : gdcoll.getSzenarios()) {
            item = new JMenuItem(szen.getTitle());

            szenario_menu.add(item);

            if (UserProperties.is(BooleanProperty.OPTION_ENABLE_SUBMODEL_BROWSER) && szen == Static.getSelectedDoc()) {
                item.setEnabled(false);
                continue;
            }

            item.addActionListener(this);
            item.setActionCommand(MODEL_ACTION_ADD_SELECTED_TO_SUBMODEL + " " + szen.getHashString());
        }

        item = getItem(MODEL_ACTION_ADD_SELECTED_TO_ALL_SUBMODELS);
        szenario_menu.add(new JSeparator());
        szenario_menu.add(item);

        if (gdcoll.getSzenarioCount() > 20) {
            MenuScroller.setScrollerFor(szenario_menu, 20, 125, 2, 2);
        }

        return szenario_menu;
    }

    /**
     * Menü für das verknüpfen mit einem Teilmodell
     *
     * @return
     */
    private JMenu getLinkToSzenarioMenu() {
        JMenu link_to_szenario_menu = new JMenu(getResString("verkn_mit_szen"));
        JMenuItem item = getItem(MODEL_ACTION_LINK_SELECTED_TO_NEW_SUBMODEL);
        link_to_szenario_menu.add(item);
        link_to_szenario_menu.add(new JSeparator());

        GDCollection gdcoll = doc.getCollection();
        for (Szenario szen : gdcoll.getSzenarios()) {
            item = new JMenuItem(szen.getTitle());

            /* ist Node schon mit diesem Szenario verknüpft */
            if (mc != null && mc.getElement() instanceof Node && szen.getHashString().equals(((Node) mc.getElement()).getAssociatedDoc())) {
                item.setEnabled(false);
            }

            item.addActionListener(this);
            item.setActionCommand(MODEL_ACTION_LINK_SELECTED_TO_SUBMODEL + " " + szen.getHashString());
            link_to_szenario_menu.add(item);
        }
        return link_to_szenario_menu;
    }

    private static void addMenuItem(final JPopupMenu menu, final JMenuItem item) {
        menu.add(item);
        Action action = item.getAction();
        item.setEnabled(action == null || action.isEnabled());
    }

    /**
     * Kontextmenü eines Einzelknotens
     *
     * @param contextSource
     * @param ec
     * @return
     */
    private JPopupMenu getSingleKnotContextMenu(final Component contextSource, final ElementContainer ec) {
        //		System.err.println("ContextGenerator.getSingleKnotContextMenu()");
        JPopupMenu menu = new JPopupMenu();
        mc = ec;
        ModelElement me = ec.getElement();
        if (!(ec instanceof BendpointContainer)) {
            addMenuItem(menu, properties);
            menu.addSeparator();

            Class<? extends ModelElement> meClass = me.getClass();

            //Anlegbare Pfade zu anderen Elementen anbieten
            JLabel connectLabel = null;
            for (SimpleMetaPath metaPath : ModelConstants.getCreatableMetaPaths(meClass)) {
                if (connectLabel == null) {
                    connectLabel = new JLabel(getResString("LABEL_CONNECT"));
                    menu.add(connectLabel);
                }
                Class<? extends ModelElement> endClass = metaPath.getEndClass();
                JMenu pathConnectableElements = new JMenu(metaPath.getName(false, true));
                pathConnectableElements.setIcon(verbindung_anlegen);
                List<ModelElement> endElements = doc.getModelItems(endClass, true, true);
                pathConnectableElements.setEnabled(!endElements.isEmpty());
                menu.add(pathConnectableElements);
                for (ModelElement endMe : endElements) {
                    Action createPathAction = createPathAction(metaPath, endMe);
                    JMenuItem createPathItem = getItem(createPathAction);
                    pathConnectableElements.add(createPathItem);
                }
            }

            //InstaciationEdges -> "Neue Instanz" der verbundenen Klasse erzeugen anbieten
            JLabel newInstanceLabel = null;
            if (!ModelConstants.isSlaveType(meClass)) {
                for (Class<? extends Edge> edgeClass : getEdgeTypes(meClass)) {
                    if (InstanciationEdge.class.isAssignableFrom(edgeClass) && Edge.isStartClass(edgeClass, meClass)) {
                        if (newInstanceLabel == null) {
                            newInstanceLabel = new JLabel(getResString(MODEL_ACTION_CREATE_INSTANCIATION.name()));
                            menu.add(newInstanceLabel);
                        }
                        String toolTip = ElementsNameBuilder.getFullForwardMetaAssociationName(edgeClass);
                        Class<? extends ModelElement> endClass = Edge.getEndClass(edgeClass);
                        String label = ElementsNameBuilder.getDisplayableName(endClass);
                        JMenuItem item = getItem(label, MODEL_ACTION_CREATE_INSTANCIATION, edgeClass.getSimpleName(), verbindung_anlegen, true, toolTip);
                        menu.add(item);
                    }
                }
            }

            if (newInstanceLabel != null || connectLabel != null) {
                menu.addSeparator();
            }

            JMenu subElems = getSubElemMenu();
            if (subElems.getItemCount() > 0) {
                menu.add(subElems);
                menu.addSeparator();
            }

            if (!me.isUnique()) {
                menu.add(getAddToSzenarioMenu());
            }
            JMenuItem addToModelMenu = getAddToModelMenu();
            if (addToModelMenu != null) {
                menu.add(addToModelMenu);
            }

            if (me.getAssociatedDoc() != null) {
                menu.add(selectLinkedSzenario);
            }
            menu.add(getLinkToSzenarioMenu());
            if (me.getAssociatedDoc() != null) {
                menu.add(unlinkToSzenario);
            }
            if (doc instanceof Szenario) {
                if (ec instanceof InterLayerConnectedNodeContainer && contextSource instanceof InputGraphArea) {
                    menu.addSeparator();
                    addMenuItem(menu, show_configs);
                    addMenuItem(menu, hide_configs);
                }
                if (ModelConstants.hasLayout(me.getClass())) {
                    menu.addSeparator();
                    if (!ec.isVisible()) {
                        menu.add(set_visible);
                    } else {
                        menu.add(set_invisible);
                    }
                }
                if (contextSource instanceof InputGraphArea) {
                    if (me.canHaveParts()) {
                        // menu.addSeparator();
                        if (me.hasDirectPartContainer(doc)) {
                            expand.setEnabled(true);
                            collapse.setEnabled(true);
                        } else {
                            expand.setEnabled(false);
                            collapse.setEnabled(false);
                        }
                        if (!ec.isExpanded()) {
                            menu.add(expand);
                        } else {
                            menu.add(collapse);
                        }
                    }
                    menu.addSeparator();
                    menu.add(MenuCollection.LayoutSubMenus.ELEMENT_LAYOUT_MENU);
                    menu.add(MenuCollection.LayoutSubMenus.ELEMENT_LEVEL_MENU);
                    menu.add(getLayerMenu());
                }
            }

            menu.addSeparator();

            // Analysemenü anfügen
            menu.add(getAnalyseMenu());

            JMenuItem joinMenu = getJoinMenu();
            if (joinMenu != null) {
                menu.addSeparator();
                menu.add(joinMenu);
            }

            menu.addSeparator();
        }

        //        if (ActionLibrary.EditActions.MODEL_ACTION_REMOVE_CHILDS.isEnabled()) {
        //            menu.add(ActionLibrary.EditActions.MODEL_ACTION_REMOVE_CHILDS);
        //        }

        //bewirkt, dass "Aus Teilmodell löschen" nur angezeigt wird,
        //wenn das selektierte Element in mehr als einem Teilmodell vorkommt
        //und nicht der <Alle-Elemte>-Browser aktiviert ist.
        if (!ec.getElement().isUnique() && !ec.getElement().isSlave()) {
            if (doc instanceof Szenario) {
                menu.add(delete_selected_from_szenario);
            }
        }

        if (!(ec instanceof BendpointContainer)) {
            menu.add(delete_selected);
        }

        return menu;
    }

    /**
     * @param contextSource
     * @return
     */
    private JPopupMenu getMultiKnotContextMenu(final Component contextSource) {

        JPopupMenu menu = new JPopupMenu();

        boolean knickpunkte = doc.isSelectedOnlyBendpoints();

        if (!knickpunkte) {
            ModelElement lastSelected = doc.getLastSelected().getElement();
            Class<? extends ModelElement> lastSelectedClass = lastSelected.getClass();

            List<ModelElement> selectedElements = doc.getSelectedElements();

            List<NamedObjectContainer<JMenuItem>> connectableItems = new ArrayList<>();
            List<NamedObjectContainer<JMenuItem>> disconnectableItems = new ArrayList<>();

            for (Class<? extends ModelElement> me2Class : doc.getSelectedRealElementClasses()) {
                List<Object> edgesAndPaths = new ArrayList<>();
                edgesAndPaths.addAll(Arrays.asList(getEdgeTypes(lastSelectedClass, me2Class)));
                edgesAndPaths.addAll(ModelConstants.getCreatableMetaPaths(lastSelectedClass, me2Class));
                for (Object edgeClassOrMetaPath : edgesAndPaths) {
                    if (edgeClassOrMetaPath instanceof Class) {
                        Class<? extends Edge> edgeClass = ((Class<?>) edgeClassOrMetaPath).asSubclass(Edge.class);
                        //Hat-Teil-Kante
                        if (HasPartEdge.class.isAssignableFrom(edgeClass)) {
                            if (isConnectingForward(edgeClass, lastSelectedClass, me2Class)) {
                                String label = getForwardMetaAssociationName(edgeClass, false, true);
                                String toolTip = getFullForwardMetaAssociationName(edgeClass);
                                boolean connectable = false;
                                boolean disconnectable = false;
                                for (ModelElement me2 : selectedElements) {
                                    if (lastSelected == me2) {
                                        continue;
                                    }
                                    if (!lastSelected.isPartOf(me2) && !lastSelected.isDirectParentOf(me2)) {
                                        connectable = true;
                                    }
                                    if (lastSelected.isDirectParentOf(me2)) {
                                        disconnectable = true;
                                    }
                                    if (connectable && disconnectable) {
                                        break;
                                    }
                                }
                                connectableItems.add(new NamedObjectContainer<>(getItem(label, MODEL_ACTION_LINK, edgeClass.getSimpleName() + " " + FORWARD, verbindung_anlegen, connectable, toolTip), label));
                                disconnectableItems.add(new NamedObjectContainer<>(getItem(label, MODEL_ACTION_UNLINK, edgeClass.getSimpleName() + " " + FORWARD, verbindung_trennen, disconnectable, toolTip), label));
                            }
                            if (isConnectingForward(edgeClass, me2Class, lastSelectedClass)) {
                                String label = getBackwardMetaAssociationName(edgeClass, false, true);
                                String toolTip = getFullBackwardMetaAssociationName(edgeClass);
                                boolean connectable = false;
                                boolean disconnectable = false;
                                for (ModelElement me2 : selectedElements) {
                                    if (lastSelected == me2) {
                                        continue;
                                    }
                                    if (!me2.isPartOf(lastSelected) && !me2.isDirectParentOf(lastSelected)) {
                                        connectable = true;
                                    }
                                    if (me2.isDirectParentOf(lastSelected)) {
                                        disconnectable = true;
                                    }
                                    if (connectable && disconnectable) {
                                        break;
                                    }
                                }
                                connectableItems.add(new NamedObjectContainer<>(getItem(label, MODEL_ACTION_LINK, edgeClass.getSimpleName() + " " + BACKWARD, verbindung_anlegen, connectable, toolTip), label));
                                disconnectableItems.add(new NamedObjectContainer<>(getItem(label, MODEL_ACTION_UNLINK, edgeClass.getSimpleName() + " " + BACKWARD, verbindung_trennen, disconnectable, toolTip), label));
                            }
                            //Kante mit Doppelter Bedeutung
                        } else if (ModelConstants.isDoubleMeaningEdge(edgeClass)) {
                            if (isConnectingForward(edgeClass, lastSelectedClass, me2Class)) {
                                Direction direction = Direction.FORWARD;
                                ConnectionState connectionState = ConnectionState.FORWARD;
                                String label = getMetaAssociationName(edgeClass, direction, connectionState, false, true);
                                String toolTip = getFullMetaAssociationName(edgeClass, direction, connectionState);
                                boolean connectable = false;
                                boolean disconnectable = false;
                                for (ModelElement me2 : selectedElements) {
                                    if (lastSelected == me2) {
                                        continue;
                                    }
                                    if (!lastSelected.isConnectedTo(me2, edgeClass)) {
                                        connectable = true;
                                    } else {
                                        disconnectable = true;
                                    }
                                    if (connectable && disconnectable) {
                                        break;
                                    }
                                }
                                connectableItems.add(new NamedObjectContainer<>(getItem(label, MODEL_ACTION_LINK, edgeClass.getSimpleName() + " " + FORWARD, verbindung_anlegen, connectable, toolTip), label));
                                disconnectableItems.add(new NamedObjectContainer<>(getItem(label, MODEL_ACTION_UNLINK, edgeClass.getSimpleName() + " " + FORWARD, verbindung_trennen, disconnectable, toolTip), label));

                                connectionState = ConnectionState.BACKWARD;
                                label = getMetaAssociationName(edgeClass, direction, connectionState, false, true);
                                toolTip = getFullMetaAssociationName(edgeClass, direction, connectionState);
                                connectable = false;
                                disconnectable = false;
                                for (ModelElement me2 : selectedElements) {
                                    if (lastSelected == me2) {
                                        continue;
                                    }
                                    if (!lastSelected.isConnectedFrom(me2, edgeClass)) {
                                        connectable = true;
                                    } else {
                                        disconnectable = true;
                                    }
                                    if (connectable && disconnectable) {
                                        break;
                                    }
                                }
                                connectableItems.add(new NamedObjectContainer<>(getItem(label, MODEL_ACTION_LINK, edgeClass.getSimpleName() + " " + BACKWARD, verbindung_anlegen, connectable, toolTip), label));
                                disconnectableItems.add(new NamedObjectContainer<>(getItem(label, MODEL_ACTION_UNLINK, edgeClass.getSimpleName() + " " + BACKWARD, verbindung_trennen, disconnectable, toolTip), label));
                            }
                            // Doppeldeutige Kanten mit identischer Start- und
                            // Endklasse brauchen nur 1x angeboten werden
                            if (isConnectingForward(edgeClass, me2Class, lastSelectedClass) && getStartClass(edgeClass) != getEndClass(edgeClass)) {
                                Direction direction = Direction.BACKWARD;
                                ConnectionState connectionState = ConnectionState.FORWARD;
                                String label = getMetaAssociationName(edgeClass, direction, connectionState, false, true);
                                String toolTip = getFullMetaAssociationName(edgeClass, direction, connectionState);
                                boolean connectable = false;
                                boolean disconnectable = false;
                                for (ModelElement me2 : selectedElements) {
                                    if (lastSelected == me2) {
                                        continue;
                                    }
                                    if (!lastSelected.isConnectedTo(me2, edgeClass)) {
                                        connectable = true;
                                    } else {
                                        disconnectable = true;
                                    }
                                    if (connectable && disconnectable) {
                                        break;
                                    }
                                }
                                connectableItems.add(new NamedObjectContainer<>(getItem(label, MODEL_ACTION_LINK, edgeClass.getSimpleName() + " " + FORWARD, verbindung_anlegen, connectable, toolTip), label));
                                disconnectableItems.add(new NamedObjectContainer<>(getItem(label, MODEL_ACTION_UNLINK, edgeClass.getSimpleName() + " " + FORWARD, verbindung_trennen, disconnectable, toolTip), label));

                                connectionState = ConnectionState.BACKWARD;
                                label = getMetaAssociationName(edgeClass, direction, connectionState, false, true);
                                toolTip = getFullMetaAssociationName(edgeClass, direction, connectionState);
                                connectable = false;
                                disconnectable = false;
                                for (ModelElement me2 : selectedElements) {
                                    if (lastSelected == me2) {
                                        continue;
                                    }
                                    if (!lastSelected.isConnectedFrom(me2, edgeClass)) {
                                        connectable = true;
                                    } else {
                                        disconnectable = true;
                                    }
                                    if (connectable && disconnectable) {
                                        break;
                                    }
                                }
                                connectableItems.add(new NamedObjectContainer<>(getItem(label, MODEL_ACTION_LINK, edgeClass.getSimpleName() + " " + BACKWARD, verbindung_anlegen, connectable, toolTip), label));
                                disconnectableItems.add(new NamedObjectContainer<>(getItem(label, MODEL_ACTION_UNLINK, edgeClass.getSimpleName() + " " + BACKWARD, verbindung_trennen, disconnectable, toolTip), label));

                            }
                            //Kanten die nicht doppeltdeutig sind, aber dieselben Elementarten verbinden und in beide Richtungen unterschiedlich heißen, müssen auch in beiden Richtungen angeboten werden
                        } else if (Edge.isConnectingForward(edgeClass, lastSelectedClass, me2Class) && Edge.isConnectingForward(edgeClass, me2Class, lastSelectedClass) && ModelConstants.isDirectedEdge(edgeClass)) {
                            String labelForward = getForwardMetaAssociationName(edgeClass, false, true);
                            String toolTipForward = getFullForwardMetaAssociationName(edgeClass);
                            String labelBackward = getBackwardMetaAssociationName(edgeClass, false, true);
                            String toolTipBackward = getFullBackwardMetaAssociationName(edgeClass);
                            boolean connectableForward = false;
                            boolean disconnectableForward = false;
                            boolean connectableBackward = false;
                            boolean disconnectableBackward = false;
                            for (ModelElement me2 : selectedElements) {
                                if (lastSelected == me2) {
                                    continue;
                                }
                                if (!lastSelected.isConnectedTo(me2, edgeClass)) {
                                    connectableForward = true;
                                } else {
                                    disconnectableForward = true;
                                }
                                if (!lastSelected.isConnectedFrom(me2, edgeClass)) {
                                    connectableBackward = true;
                                } else {
                                    disconnectableBackward = true;
                                }
                                if (connectableForward && disconnectableForward && connectableBackward && disconnectableBackward) {
                                    break;
                                }
                            }
                            connectableItems.add(new NamedObjectContainer<>(getItem(labelForward, MODEL_ACTION_LINK, edgeClass.getSimpleName() + " " + FORWARD, verbindung_anlegen, connectableForward, toolTipForward), labelForward));
                            disconnectableItems.add(new NamedObjectContainer<>(getItem(labelForward, MODEL_ACTION_UNLINK, edgeClass.getSimpleName() + " " + FORWARD, verbindung_trennen, disconnectableForward, toolTipForward), labelForward));
                            connectableItems.add(new NamedObjectContainer<>(getItem(labelBackward, MODEL_ACTION_LINK, edgeClass.getSimpleName() + " " + BACKWARD, verbindung_anlegen, connectableBackward, toolTipBackward), labelBackward));
                            disconnectableItems.add(new NamedObjectContainer<>(getItem(labelBackward, MODEL_ACTION_UNLINK, edgeClass.getSimpleName() + " " + BACKWARD, verbindung_trennen, disconnectableBackward, toolTipBackward), labelBackward));

                        } else if (InstanciationEdge.class.isAssignableFrom(edgeClass)) {
                            //diese Kanten sind bei Mehrfachauswahl zu ignorieren!
                        } else /* if (Edge.isConnecting(edgeClass, lastSelectedClass, me2Class)) */ {
                            Direction direction;
                            String label;
                            String toolTip;
                            if (isStartClass(edgeClass, lastSelectedClass)) {
                                direction = FORWARD;
                                label = getForwardMetaAssociationName(edgeClass, false, true);
                                toolTip = getFullForwardMetaAssociationName(edgeClass);
                            } else {
                                direction = BACKWARD;
                                label = getBackwardMetaAssociationName(edgeClass, false, true);
                                toolTip = getFullBackwardMetaAssociationName(edgeClass);
                            }
                            boolean connectable = false;
                            boolean disconnectable = false;
                            for (ModelElement selected : selectedElements) {
                                if (lastSelected == selected) {
                                    continue;
                                }
                                Class<? extends ModelElement> selectedClass = selected.getClass();
                                if (direction == FORWARD && !Edge.isConnectingForward(edgeClass, lastSelectedClass, selectedClass)) {
                                    continue;
                                }
                                if (direction == BACKWARD && !Edge.isConnectingForward(edgeClass, selectedClass, lastSelectedClass)) {
                                    continue;
                                }
                                if (!lastSelected.isConnectedWith(selected, edgeClass)) {
                                    connectable = true;
                                } else {
                                    disconnectable = true;
                                }
                                if (connectable && disconnectable) {
                                    break;
                                }
                            }
                            connectableItems.add(new NamedObjectContainer<>(getItem(label, MODEL_ACTION_LINK, edgeClass.getSimpleName() + " " + direction, verbindung_anlegen, connectable, toolTip), label));
                            disconnectableItems.add(new NamedObjectContainer<>(getItem(label, MODEL_ACTION_UNLINK, edgeClass.getSimpleName() + " " + direction, verbindung_trennen, disconnectable, toolTip), label));
                        }
                    } else {
                        SimpleMetaPath metaPath = (SimpleMetaPath) edgeClassOrMetaPath;
                        Action createPathAction = createPathAction(metaPath);
                        JMenuItem createPathItem = getItem(createPathAction);
                        connectableItems.add(new NamedObjectContainer<>(createPathItem, createPathAction.toString()));
                    }
                }
            }

            if (connectableItems.size() > 0) {
                Alphabetical.sort(connectableItems);
                menu.add(new JLabel(getResString("LABEL_CONNECT")));
                for (NamedObjectContainer<JMenuItem> itemContainer : connectableItems) {
                    menu.add(itemContainer.getObject());
                }

            }
            if (disconnectableItems.size() > 0) {
                Alphabetical.sort(disconnectableItems);
                menu.add(new JLabel(getResString("LABEL_DISCONNECT")));
                for (NamedObjectContainer<JMenuItem> itemContainer : disconnectableItems) {
                    menu.add(itemContainer.getObject());
                }
            }

            if (menu.getComponentCount() > 0) {
                menu.addSeparator();
            }

            menu.add(getAddToSzenarioMenu());
            JMenuItem addToModelMenu = getAddToModelMenu();
            if (addToModelMenu != null) {
                menu.add(addToModelMenu);
            }

            if (contextSource instanceof InputGraphArea) {
                if (menu.getComponentCount() > 0) {
                    menu.addSeparator();
                }
                addMenuItem(menu, show_configs);
                addMenuItem(menu, hide_configs);
            }
            if (menu.getComponentCount() > 0) {
                menu.addSeparator();
            }
            menu.add(set_visible);
            menu.add(set_invisible);

            if (contextSource instanceof InputGraphArea) {
                if (menu.getComponentCount() > 0) {
                    menu.addSeparator();
                }
                if (lastSelected.hasDirectPartContainer(doc)) {
                    expand.setEnabled(true);
                    collapse.setEnabled(true);
                } else {
                    expand.setEnabled(false);
                    collapse.setEnabled(false);
                }
                if (!doc.getLastSelected().isExpanded()) {
                    menu.add(expand);
                } else {
                    menu.add(collapse);
                }
                menu.addSeparator();
                menu.add(MenuCollection.LayoutSubMenus.ELEMENT_LAYOUT_MENU);
                menu.add(MenuCollection.LayoutSubMenus.ELEMENT_LEVEL_MENU);
                menu.add(MenuCollection.LayoutSubMenus.ELEMENT_ALIGNMENT_MENU);
            }

            if (doc.isJoinableElementsSelected()) {
                if (menu.getComponentCount() > 0) {
                    menu.addSeparator();
                }
                menu.add(join_selected);
            }
        }

        if (doc instanceof Szenario && !doc.isSelectedOnlyUnique() && !doc.isSelectedOnlySlaveRealNodes() && !doc.isSelectedOnlySubmodelElements()) {
            if (menu.getComponentCount() > 0) {
                menu.addSeparator();
            }
            menu.add(delete_selected_from_szenario);
        }

        if (!knickpunkte) {
            menu.add(delete_selected);
        }

        return menu;
    }

    /**
     * Kontextmenü im Searchdialog
     */
    public JPopupMenu getSearchDialogContextMenu() {
        JPopupMenu menu = new JPopupMenu();
        if (doc.isSingleSelection()) {
            addMenuItem(menu, properties);
            menu.addSeparator();
        }
        if (!doc.isSelectedOnlyUnique() && !doc.isSelectedOnlySubmodelElements() && doc instanceof Szenario) {
            menu.add(delete_selected_from_szenario);
        }
        menu.add(delete_selected);
        return menu;
    }

    /**
     * @param source
     * @return
     */
    public final JPopupMenu getKnotContextMenu(final Component source) {
        JPopupMenu menu = new JPopupMenu();
        if (doc.isSelectedOnlyBendpoints()) {
            if (doc instanceof Szenario) {
                menu.add(delete_selected_from_szenario);
            }
        } else {
            if (doc.isSingleSelection()) {
                menu = getSingleKnotContextMenu(source, doc.getLastSelected());
            } else if (doc.isMultipleSelection()) {
                menu = getMultiKnotContextMenu(source);
            }
        }
        return menu;
    }

    /**
     * @return
     */
    private final JPopupMenu getSingleTraceContextMenu() {
        //		System.err.println("ContextGenerator.getSingleEdgeContextMenu()");
        JPopupMenu menu = new JPopupMenu();
        if (doc.isSingleSelection() && doc.getLastSelected() instanceof EdgeContainer) {
            addMenuItem(menu, properties);
        }

        JMenuItem addToModelMenu = getAddToModelMenu();
        if (addToModelMenu != null) {
            menu.addSeparator();
            menu.add(addToModelMenu);
        }
        if (doc instanceof Szenario) {
            menu.addSeparator();
            menu.add(delete_selected);
        }
        return menu;
    }

    /**
     * @return
     */
    private final JPopupMenu getMultiTraceContextMenu() {
        JPopupMenu menu = new JPopupMenu();
        if (menu.getComponentCount() > 0) {
            menu.addSeparator();
        }
        JMenuItem addToModelMenu = getAddToModelMenu();
        if (addToModelMenu != null) {
            menu.add(addToModelMenu);
        }

        if (menu.getComponentCount() > 0) {
            menu.addSeparator();
        }
        menu.add(delete_selected);

        return menu;
    }

    /**
     * @return
     */
    private final JPopupMenu getTraceContextMenu() {
        if (doc.isSingleSelection()) {
            return getSingleTraceContextMenu();
        }
        return getMultiTraceContextMenu();
    }

    /**
     * @return
     */
    private final JPopupMenu getMultiContextMenu() {
        JPopupMenu menu = new JPopupMenu();
        if (menu.getComponentCount() > 0) {
            menu.addSeparator();
        }
        menu.add(getAddToSzenarioMenu());
        JMenuItem addToModelMenu = getAddToModelMenu();
        if (addToModelMenu != null) {
            menu.add(addToModelMenu);
        }

        menu.addSeparator();
        if (doc instanceof Szenario) {
            menu.add(delete_selected_from_szenario);
        }
        menu.add(delete_selected);

        return menu;
    }

    /**
     * @param menu
     * @param enabled
     * @return null, wenn enabled == false, sonst menu
     */
    private JMenu setItemsEnabled(final JMenu menu, final boolean enabled) {
        for (int i = 0; i < menu.getItemCount(); i++) {
            menu.getItem(i).setEnabled(enabled);
        }
        return enabled ? menu : null;
    }

    /**
     * @return
     */
    private JMenu getNewKnotMenu() {
        int activeLayer = doc.getCollection().getActiveLayer();
        JMenu menu = setItemsEnabled(new_domain_tree, activeLayer == ModelConstants.DOMAIN_LAYER);
        menu = menu != null ? menu : setItemsEnabled(new_logical_tree, activeLayer == ModelConstants.LOGICAL_LAYER);
        menu = menu != null ? menu : setItemsEnabled(new_physical_tree, activeLayer == ModelConstants.PHYSICAL_LAYER);
        return menu != null ? menu : new_domain_tree;
    }

    /**
     * @return
     */
    private JMenu getLayerMenu() {
        JMenu menu = new JMenu(getResString("layer"));
        JPopupMenu popup = getLayerContextMenu();
        for (Component c : popup.getComponents()) {
            if (c instanceof JMenu) {
                menu.add((JMenu) c);
            } else if (c instanceof JMenuItem) {
                menu.add((JMenuItem) c);
            } else if (c instanceof JPopupMenu.Separator) {
                menu.addSeparator();
            }
        }
        return menu;
    }

    /**
     * @return
     */
    public final JPopupMenu getLayerContextMenu() {
        JPopupMenu menu = new JPopupMenu();
        menu.add(getNewKnotMenu());
        menu.add(new_text);
        menu.addSeparator();

        addMenuItem(menu, layer_show_configs);
        addMenuItem(menu, layer_hide_configs);

        menu.addSeparator();
        menu.add(layout_layer);
        menu.add(ActionLibrary.LayoutActions.ACTION_OPEN_GLOBAL_LAYOUT_EDITOR);
        menu.addSeparator();
        menu.add(internals);
        return menu;
    }

    /**
     *
     */
    public void check_cb_menu() {
        check_undo_redo();
        if (doc == null) {
            cb_copy.setEnabled(false);
            cb_cut.setEnabled(false);
            delete_selected_from_szenario.setEnabled(false);
            delete_selected.setEnabled(false);
            cb_paste.setEnabled(false);
            cb_clear.setEnabled(false);
            return;
        }
        if (!doc.isSelection()) {
            cb_copy.setEnabled(false);
            cb_cut.setEnabled(false);
            delete_selected_from_szenario.setEnabled(false);
            delete_selected.setEnabled(false);
        } else {
            cb_copy.setEnabled(true);
            cb_cut.setEnabled(true);
            if (doc instanceof Szenario) {
                delete_selected_from_szenario.setEnabled(true);
            }
            delete_selected.setEnabled(true);
        }
        if (!LGMGraphDocument.isClipboardAvailable()) {
            cb_paste.setEnabled(false);
            cb_clear.setEnabled(false);
        } else {
            cb_paste.setEnabled(true);
            cb_clear.setEnabled(true);
        }
    }

    /**
     *
     */
    private void check_undo_redo() {
        if (doc == null) {
            undo.setEnabled(false);
            redo.setEnabled(false);
            return;
        }
        if (doc.getCollection().getTman().isUndoAvailable()) {
            undo.setEnabled(true);
        } else {
            undo.setEnabled(false);
        }
        if (doc.getCollection().getTman().isRedoAvailable()) {
            redo.setEnabled(true);
        } else {
            redo.setEnabled(false);
        }
    }

    /**
     * @return
     */
    public boolean getResizing() {
        return resizing;
    }

    /**
     * @param b
     */
    public void setResizing(final boolean b) {
        resizing = b;
    }

    /**
     * COMMENTME
     */
    protected boolean elementGetroffen = false;

    /**
     * @return
     */
    public boolean getElementGetroffen() {
        return elementGetroffen;
    }

    /**
     * @param b
     */
    public void setElementGetroffen(final boolean b) {
        elementGetroffen = b;
    }

    /**
     * COMMENTME
     */
    protected boolean ebeneGetroffen = false;

    /**
     * @return
     */
    public boolean getEbeneGetroffen() {
        return ebeneGetroffen;
    }

    /**
     * @param b
     */
    public void setEbeneGetroffen(final boolean b) {
        ebeneGetroffen = b;
    }

    /**
     * @param modelElementCont
     */
    public void setModelElement(final ElementContainer modelElementCont) {
        mc = modelElementCont;
    }

    /**
     * @param left_button
     * @param right_button
     */
    public void processMouseEvent(final boolean left_button, final boolean right_button) {
        processMouseEventInternal(left_button, right_button, null, 0, 0);
    }

    /**
     * @param left_button
     * @param right_button
     * @param gdl
     */
    public void processMouseEvent(final boolean left_button, final boolean right_button, final Component gdl) {
        processMouseEventInternal(left_button, right_button, gdl, 0, 0);
    }

    /**
     * @param left_button
     * @param right_button
     * @param gdl
     * @param xin
     * @param yin
     */
    public void processMouseEvent(final boolean left_button, final boolean right_button, final Component gdl, final int xin, final int yin) {
        // System.err.println("ContextGenerator.processMouseEvent " + xin + " " + yin + " " + gdl.getX() + " " + gdl.getY());
        Tool3lgm.setLastActionPosition(xin + gdl.getX(), yin + gdl.getY());
        processMouseEventInternal(left_button, right_button, gdl, xin, yin);
    }

    /**
     * @param left_button
     * @param right_button
     * @param gdl
     * @param xin
     * @param yin
     */
    private void processMouseEventInternal(final boolean left_button, final boolean right_button, final Component gdl, final int xin, final int yin) {
        if (resizing) {
            if (right_button) {
                return;
            }

            // nichts selektiert
            if (!doc.isSelection()) {
                return;
            }
            // nur Kanten selektiert
            if (doc.isSelectedOnlyEdges()) {
                if (left_button && !controlled) {
                    left_knothand_noshift_traces();
                    return;
                }
                if (left_button && controlled) {
                    left_knothand_shift_traces();
                    return;
                }
                return;
            }
            // nur Node selektiert
            if (doc.isSelectedOnlyNodes()) {
                if (left_button && !controlled) {
                    left_knothand_noshift_knots();
                    return;
                }
                if (left_button && controlled) {
                    left_knothand_shift_knots();
                    return;
                }
                return;
            }
            // Node und Kanten selektiert
            if (left_button && !controlled) {
                left_knothand_noshift_multi();
                return;
            }
            if (left_button && controlled) {
                left_knothand_shift_multi();
                return;
            }
            return;
        }

        if (elementGetroffen) {
            //wenn man auf einer selektierten Edge das Kontexmenü auf einem Knickpunkt öffnet,
            //dann soll das Kontextmenü aufgehen, als wäre die Edge angeklickt worden und nicht
            //der BendpointContainer, der ja ein Knotenkontainer ist und ein sinnloses Kontextmenü
            //anzeigen würde
            if (mc instanceof BendpointContainer) {
                EdgeContainer kc = ((Knickpunkt) mc.getElement()).getOwner();
                if (doc.isSelected(kc)) {
                    if (left_button) {
                        doc.select(mc, TransactionManager.STANDARD_PID);
                    } else {
                        mc = kc;
                    }
                }
            }

            if (mc instanceof NodeContainer) {
                // nichts selektiert
                if (!doc.isSelection()) {
                    if (left_button && !controlled) {
                        left_knot_noshift_none();
                        return;
                    }
                    if (left_button && controlled) {
                        left_knot_shift_none();
                        return;
                    }
                    if (right_button) {
                        right_knot_none(gdl, xin, yin);
                        return;
                    }
                    return;
                }
                // nur Kanten selektiert
                if (doc.isSelectedOnlyEdges()) {
                    if (left_button && !controlled) {
                        left_knot_noshift_traces();
                        return;
                    }
                    if (left_button && controlled) {
                        left_knot_shift_traces();
                        return;
                    }
                    if (right_button) {
                        right_knot_traces(gdl, xin, yin);
                        return;
                    }
                    return;
                }
                // nur Node selektiert
                if (doc.isSelectedOnlyNodes()) {
                    if (left_button && !controlled) {
                        left_knot_noshift_knots();
                        return;
                    }
                    if (left_button && controlled) {
                        left_knot_shift_knots();
                        return;
                    }
                    if (right_button) {
                        right_knot_knots(gdl, xin, yin);
                        return;
                    }
                    return;
                }
                // Node und Kanten selektiert
                if (left_button && !controlled) {
                    left_knot_noshift_multi();
                    return;
                }
                if (left_button && controlled) {
                    left_knot_shift_multi();
                    return;
                }
                if (right_button) {
                    right_knot_multi(gdl, xin, yin);
                    return;
                }
                return;
            } else if (mc instanceof EdgeContainer) {
                // nichts selektiert
                if (!doc.isSelection()) {
                    if (left_button && !controlled) {
                        left_trace_noshift_none();
                        return;
                    }
                    if (left_button && controlled) {
                        left_trace_shift_none();
                        return;
                    }
                    if (right_button) {
                        right_trace_none(gdl, xin, yin);
                        return;
                    }
                    return;
                }
                // nur Kanten selektiert
                if (doc.isSelectedOnlyEdges()) {
                    if (left_button && !controlled) {
                        left_trace_noshift_traces();
                        return;
                    }
                    if (left_button && controlled) {
                        left_trace_shift_traces();
                        return;
                    }
                    if (right_button) {
                        right_trace_traces(gdl, xin, yin);
                        return;
                    }
                    return;
                }
                // nur Node selektiert
                if (doc.isSelectedOnlyNodes()) {
                    if (left_button && !controlled) {
                        left_trace_noshift_knots();
                        return;
                    }
                    if (left_button && controlled) {
                        left_trace_shift_knots();
                        return;
                    }
                    if (right_button) {
                        right_trace_knots(gdl, xin, yin);
                        return;
                    }
                    return;
                }
                // Node und Kanten selektiert
                if (left_button && !controlled) {
                    left_trace_noshift_multi();
                    return;
                }
                if (left_button && controlled) {
                    left_trace_shift_multi();
                    return;
                }
                if (right_button) {
                    right_trace_multi(gdl, xin, yin);
                    return;
                }
                return;
            }
        }

        if (ebeneGetroffen) {
            // nichts selektiert
            if (!doc.isSelection()) {
                if (left_button && !controlled) {
                    left_layer_noshift_none();
                    return;
                }
                if (left_button && controlled) {
                    left_layer_shift_none();
                    return;
                }
                if (right_button) {
                    right_layer_none(gdl, xin, yin);
                    return;
                }
                return;
            }
            // nur Kanten selektiert
            if (doc.isSelectedOnlyEdges()) {
                if (left_button && !controlled) {
                    left_layer_noshift_traces();
                    return;
                }
                if (left_button && controlled) {
                    left_layer_shift_traces();
                    return;
                }
                if (right_button) {
                    right_layer_traces(gdl, xin, yin);
                    return;
                }
                return;
            }
            // nur Node selektiert
            if (doc.isSelectedOnlyNodes()) {
                if (left_button && !controlled) {
                    left_layer_noshift_knots();
                    return;
                }
                if (left_button && controlled) {
                    left_layer_shift_knots();
                    return;
                }
                if (right_button) {
                    right_layer_knots(gdl, xin, yin);
                    return;
                }
                return;
            }
            // Node und Kanten selektiert
            if (left_button && !controlled) {
                left_layer_noshift_multi();
                return;
            }
            if (left_button && controlled) {
                left_layer_shift_multi();
                return;
            }
            if (right_button) {
                right_layer_multi(gdl, xin, yin);
                return;
            }
            return;
        }
        //_general
        if (left_button && !controlled) {
            left_noshift_outside();
            return;
        }
        if (left_button && controlled) {
            left_shift_outside();
            return;
        }
    }

    // Methoden, die aus dem Zustand und der Aktion mit der Maus eine semantische machen

    // Es sind 56 Stueck. Viel Spass beim Hacken. Wahrscheinlich werden nur wenige
    // richtig programmiert werden, der Grossteil wird nix machen oder auf andere
    // Methoden zeigen, die dasselbe machen

    // Namensgebung: Button_ObjektTyp_Shift_Selection

    /**
     *
     */
    private void left_shift_outside() {
    }

    /**
     *
     */
    private void left_noshift_outside() {
        doc.deselectAll(false);
    }

    /**
     *
     */
    private void left_layer_noshift_none() {
        doc.deselectAll(false);
    }

    /**
     *
     */
    private void left_layer_noshift_knots() {
        doc.deselectAll(false);
    }

    /**
     *
     */
    private void left_layer_noshift_traces() {
        doc.deselectAll(false);
    }

    /**
     *
     */
    private void left_layer_noshift_multi() {
        doc.deselectAll(false);
    }

    /**
     *
     */
    private void left_layer_shift_none() {
    }

    /**
     *
     */
    private void left_layer_shift_knots() {
        doc.deselectAll(false);
    }

    /**
     *
     */
    private void left_layer_shift_traces() {
        doc.deselectAll(false);
    }

    /**
     *
     */
    private void left_layer_shift_multi() {
    }

    /**
     * @param gdl
     * @param xin
     * @param yin
     */
    private void right_layer_none(final Component gdl, final int xin, final int yin) {
        //TODO: FST: showMenu
        //FSTContextMenu.showMenu(gdl, xin, yin);
        menu = getLayerContextMenu();
        menu.show(gdl, xin, yin);
    }

    /**
     * @param gdl
     * @param xin
     * @param yin
     */
    private void right_layer_knots(final Component gdl, final int xin, final int yin) {
        menu = getLayerContextMenu();
        menu.show(gdl, xin, yin);
    }

    /**
     * @param gdl
     * @param xin
     * @param yin
     */
    private void right_layer_traces(final Component gdl, final int xin, final int yin) {
        menu = getLayerContextMenu();
        menu.show(gdl, xin, yin);
    }

    /**
     * @param gdl
     * @param xin
     * @param yin
     */
    private void right_layer_multi(final Component gdl, final int xin, final int yin) {
        menu = getLayerContextMenu();
        menu.show(gdl, xin, yin);
    }

    // Klicks in Node

    // Rechte Maustaste auf Node

    /**
     * @param gdl
     * @param xin
     * @param yin
     */
    private void right_knot_none(final Component gdl, final int xin, final int yin) {
        doc.addToSelection(mc, 0);
        menu = getKnotContextMenu(gdl);
        menu.show(gdl, xin, yin);
    }

    /**
     * @param gdl
     * @param xin
     * @param yin
     */
    private void right_knot_knots(final Component gdl, final int xin, final int yin) {
        doc.addToSelection(mc, 0);
        menu = getKnotContextMenu(gdl);
        //TODO: FST: showMenu
        //FSTContextMenu.showMenu(gdl, xin, yin);
        menu.show(gdl, xin, yin);
    }

    /**
     * @param gdl
     * @param xin
     * @param yin
     */
    private void right_knot_traces(final Component gdl, final int xin, final int yin) {
        doc.addToSelection(mc, 0);
        menu = getMultiContextMenu();
        menu.show(gdl, xin, yin);
    }

    /**
     * @param gdl
     * @param xin
     * @param yin
     */
    private void right_knot_multi(final Component gdl, final int xin, final int yin) {
        doc.addToSelection(mc, 0);
        //Wenn mind. 2 Node selektiert sind und das Kontextmenü auf einem Node aufgerufen wurde,
        //kann man auch das Knotenkontextmenü anbieten
        //		menu = getMultiContextMenu();
        menu = getMultiKnotContextMenu(gdl);
        menu.show(gdl, xin, yin);
    }

    // Linke Maustaste auf Node

    /**
     *
     */
    private void left_knot_noshift_none() {
        doc.select(mc, 0);
    }

    /**
     *
     */
    private void left_knot_noshift_knots() {
        doc.select(mc, 0);
    }

    /**
     *
     */
    private void left_knot_noshift_traces() {
        doc.select(mc, 0);
    }

    /**
     *
     */
    private void left_knot_noshift_multi() {
        doc.select(mc, 0);
    }

    /**
     *
     */
    private void left_knot_shift_none() {
        doc.addToSelection(mc, 0);
    }

    /**
     *
     */
    private void left_knot_shift_knots() {
        if (mc.isSelected()) {
            doc.deselect(mc, 0);
        } else {
            doc.addToSelection(mc, 0);
        }
    }

    /**
     *
     */
    private void left_knot_shift_traces() {
        left_knot_shift_knots();
    }

    /**
     *
     */
    private void left_knot_shift_multi() {
        left_knot_shift_knots();
    }

    // Klicks auf die Kanten

    /**
     *
     */
    private void left_trace_noshift_none() {
        doc.select(mc, 0);
    }

    /**
     *
     */
    private void left_trace_noshift_knots() {
        doc.select(mc, 0);
    }

    /**
     *
     */
    private void left_trace_noshift_traces() {
        doc.select(mc, 0);
    }

    /**
     *
     */
    private void left_trace_noshift_multi() {
        doc.select(mc, 0);
    }

    /**
     *
     */
    private void left_trace_shift_none() {
        doc.addToSelection(mc, 0);
    }

    /**
     *
     */
    private void left_trace_shift_knots() {
        if (mc.isSelected()) {
            doc.deselect(mc, 0);
        } else {
            doc.addToSelection(mc, 0);
        }
    }

    /**
     *
     */
    private void left_trace_shift_traces() {
        left_trace_shift_knots();
    }

    /**
     *
     */
    private void left_trace_shift_multi() {
        left_trace_shift_knots();
    }

    /**
     * @param gdl
     * @param xin
     * @param yin
     */
    private void right_trace_none(final Component gdl, final int xin, final int yin) {
        doc.addToSelection(mc, 0);
        menu = getTraceContextMenu();
        menu.show(gdl, xin, yin);
    }

    /**
     * @param gdl
     * @param xin
     * @param yin
     */
    private void right_trace_knots(final Component gdl, final int xin, final int yin) {
        doc.addToSelection(mc, 0);
        menu = getTraceContextMenu();
        menu.show(gdl, xin, yin);
    }

    /**
     * @param gdl
     * @param xin
     * @param yin
     */
    private void right_trace_traces(final Component gdl, final int xin, final int yin) {
        doc.addToSelection(mc, 0);
        menu = getTraceContextMenu();
        menu.show(gdl, xin, yin);
    }

    /**
     * @param gdl
     * @param xin
     * @param yin
     */
    private void right_trace_multi(final Component gdl, final int xin, final int yin) {
        doc.addToSelection(mc, 0);
        menu = getTraceContextMenu();
        menu.show(gdl, xin, yin);
    }

    // Klicks in die Node-Haende

    // Klicks mit rechts
    /*
     * private void right_knothand_knots() {
     * }
     * private void right_knothand_traces() {
     * }
     * private void right_knothand_multi() {
     * }
     */
    // Klicks mit links

    /**
     *
     */
    private void left_knothand_noshift_knots() {
        doc.select(mc, 0);
    }

    /**
     *
     */
    private void left_knothand_noshift_traces() {
        doc.select(mc, 0);
    }

    /**
     *
     */
    private void left_knothand_noshift_multi() {
        doc.select(mc, 0);
    }

    /**
     *
     */
    private void left_knothand_shift_knots() {
    }

    /**
     *
     */
    private void left_knothand_shift_traces() {
    }

    /**
     *
     */
    private void left_knothand_shift_multi() {
    }

    /**
     * @return
     */
    public JPopupMenu getTreeKnotContextMenu() {
        JPopupMenu menu = new JPopupMenu();
        addMenuItem(menu, properties);
        menu.addSeparator();
        boolean do_join = doc.isJoinableElementsSelected();
        if (do_join) {
            menu.add(join_selected);
            menu.addSeparator();
        }
        if (doc instanceof Szenario) {
            menu.add(delete_selected_from_szenario);
        }
        menu.add(delete_selected);
        delete_selected.setEnabled(true);
        //		System.out.println("getTreeKnotContextMenu - addPopupMenuListener ausgeführt");
        menu.addPopupMenuListener(this);
        return menu;
    }

    /**
     * @param ec
     * @return
     */
    public JPopupMenu getTreeKnotContextMenu(final ElementContainer ec) {
        JPopupMenu menu = new JPopupMenu();
        doc.addSimpleToSelection(ec);
        addMenuItem(menu, properties);
        menu.addPopupMenuListener(this);
        return menu;
    }

    /**
     * Gibt ein Menü zurück, welches die Analysen für das aktuell ausgewählten
     * Element enthält.
     *
     * @return Analysemenü
     */
    private JMenu getAnalyseMenu() {
        JMenu menu = new JMenu(getResString("analysis"));
        ElementContainer ec = doc.getLastSelected();
        if (ec != null && ec.getElement() instanceof Node) {
            // Alle Analysen für die ausgewählte Klasse holen
            String klasse = ec.getElement().getClass().getName();
            klasse = klasse.substring(klasse.lastIndexOf('.') + 1);
            List<AbstractAnalyse> analysen = AnalyseRepository.getAnalysenFuerKnoten(klasse);
            // Analysen ins Menü eintragen
            if (analysen != null && analysen.size() > 0) {
                for (final AbstractAnalyse ana : analysen) {
                    JMenuItem item = new JMenuItem(ana.getName());
                    menu.add(item);
                    item.addActionListener(e -> ana.setAnalysisResult(doc));
                }
            } else {
                menu.setEnabled(false);
            }
        } else {
            menu.setEnabled(false);
        }
        return menu;
    }

    /**
     * Liefert eine Action zu einem SimpleMetaPath der zwischen dem zuletzt selektierten Element und allen zum Endelement des Pfades passenden anderen
     * selektierten Elementen angelegt wird.
     *
     * @param path2create
     * @return
     */
    private Action createPathAction(final SimpleMetaPath path2create) {
        return createPathAction(path2create, doc.getSelectedElements(), path2create.getName(false, true), verbindung_anlegen);
    }

    /**
     * Liefert eine Action zu einem SimpleMetaPath der zwischen dem zuletzt selektierten Element und allen zum Endelement des Pfades passenden anderen
     * selektierten Elementen angelegt wird.
     *
     * @param path2create
     * @return
     */
    private Action createPathAction(final SimpleMetaPath path2create, final ModelElement endElement) {
        return createPathAction(path2create, ImmutableList.of(endElement), endElement.getName(), null);
    }

    /**
     * Liefert eine Action zu einem SimpleMetaPath der zwischen dem zuletzt selektierten Element und allen zum Endelement des Pfades passenden anderen
     * übergebenen Elementen angelegt wird.
     *
     * @param path2create Pfad der angelegt werden soll
     * @param endElements Elemente, zu denen der Pfad vom zuletzt selektierten Element aus angelegt werden soll
     * @param name Name der Action
     * @param icon Icon der Sction
     * @return
     */
    private Action createPathAction(final SimpleMetaPath path2create, final Collection<ModelElement> endElements, final String name, final Icon icon) {
        return new AbstractAction(name, icon) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Class<? extends ModelElement> startClass = path2create.getStartClass();
                Class<? extends ModelElement> endClass = path2create.getEndClass();
                ModelElement lastSelected = doc.getLastSelected().getElement();
                if (!startClass.isAssignableFrom(lastSelected.getClass())) {
                    return;
                }
                for (ModelElement me : endElements) {
                    if (lastSelected == me || !endClass.isAssignableFrom(me.getClass())) {
                        continue;
                    }
                    GDCollection gdcoll = doc.getCollection();
                    boolean lastInteractiveMode = gdcoll.setInteractiveMode(false);
                    doc.createPath(lastSelected, me, path2create, STANDARD_PID);
                    gdcoll.setInteractiveMode(lastInteractiveMode);
                }
            }
        };
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        doc.exec(e.getActionCommand(), STANDARD_PID);
    }

    //--------------------------------------------------------------------------------------------------------------------------------
    // PopupMenuListener-Funktionen
    @Override
    public void popupMenuWillBecomeVisible(final PopupMenuEvent e) {
    }

    @Override
    public void popupMenuWillBecomeInvisible(final PopupMenuEvent e) {
        // das Leerzeichen am Ende muss sein, da dahinter dann die Hashes der zulöschenden Elemnte kommen
        delete_selected_from_szenario.setActionCommand(GDCommands.MODEL_ACTION_DELETE_FROM_SUBMODEL + " ");
        delete_selected.setActionCommand(GDCommands.MODEL_ACTION_DELETE_FROM_MODEL + " ");
        ((JPopupMenu) e.getSource()).removePopupMenuListener(this);
    }

    @Override
    public void popupMenuCanceled(final PopupMenuEvent e) {
    }

    //--------------------------------------------------------------------------------------------------------------------------------

    /**
     * @return
     */
    private JMenuItem getAddToModelMenu() {
        if (Static.getCollectionCount() < 2) {
            return null;
        }
        JMenu menu = new JMenu(getResString("inmodel"));
        for (GDCollection gdcoll : getCollections()) {
            if (gdcoll != doc.getCollection()) {
                menu.add(getSubModelMenu(gdcoll));
            }
        }
        return menu;
    }

    /**
     * @param gdcoll
     * @return
     */
    private final JMenuItem getSubModelMenu(final GDCollection gdcoll) {
        JMenu menu = new JMenu(gdcoll.getName());
        JMenuItem item = new JMenuItem(getResString("main_model"));
        item.addActionListener(e -> doc.copySelectedToModel(gdcoll.getMainGraphDocument()));
        menu.add(item);
        for (final Szenario szen : gdcoll.getSzenarios()) {
            item = new JMenuItem(szen.getTitle());
            item.addActionListener(e -> doc.copySelectedToModel(szen));
            menu.add(item);
        }
        return menu;
    }

    /**
     * @return
     */
    private JMenuItem getJoinMenu() {
        if (mc == null) {
            return null;
        }
        if (Static.getCollectionCount() < 2) {
            return null;
        }
        if (!doc.isSingleSelection()) {
            return null;
        }
        JMenu menu = new JMenu(getResString("join_elements"));
        JMenuItem item;
        ModelElement me1 = doc.getLastSelected().getElement();
        for (GDCollection gdcoll : Static.getCollections()) {
            if (gdcoll == doc.getCollection()) {
                continue;
            }
            final GraphDocument doc2 = getPreSelectedGDCollection().getSelectedDoc();
            if (!doc2.isSingleSelection()) {
                continue;
            }
            ModelElement me2 = doc2.getLastSelected().getElement();
            if (me2.getClass() != me1.getClass()) {
                continue;
            }
            if (menu.getItemCount() > 0) {
                menu.addSeparator();
            }
            item = new JMenuItem(getResString("join_elements_result") + " " + doc.getCollection().getName());
            item.addActionListener(e -> doc.joinElements(doc2, false));
            menu.add(item);
            item = new JMenuItem(getResString("join_elements_result") + " " + doc2.getCollection().getName());
            item.addActionListener(e -> ((LGMGraphDocument) doc2).joinElements(doc, false));
            menu.add(item);
            //			item = new JMenuItem(Tool3lgmConstants.getResourceString("join_elements_result_both"));
            //			item.addActionListener(new ActionListener() {
            //				public void actionPerformed(ActionEvent e) {
            //					doc.joinElements(doc2, true);
            //				}
            //			});
            //			menu.add(item);
        }
        return menu.getItemCount() > 0 ? menu : null;
    }

    /**
     * Liefert eine Kanteart zwischen den beiden übergebenen Elementarte zurück, wenn es mind. eine gibt. Gibt es mehrere, wird der Benutzer mit einem
     * Dialog vor die Auswahl gestellt.
     *
     * @param elementClass1
     * @param elementClass2
     * @return
     */
    public static Class<? extends Edge> requestCurrentEdgeType(final Class<? extends ModelElement> elementClass1, final Class<? extends ModelElement> elementClass2) {
        Class<? extends Edge> edgeClass = null;
        Class<? extends Edge>[] edgeClasses = getEdgeTypes(elementClass1, elementClass2);
        if (edgeClasses == null || edgeClasses.length == 0) {
            return null;
        }
        edgeClass = edgeClasses[0];
        if (edgeClasses.length > 1) {
            JPanel messagePanel = new JPanel();
            messagePanel.setLayout(new BoxLayout(messagePanel, Y_AXIS));
            ButtonGroup buttonGroup = new ButtonGroup();
            for (int i = 0; i < edgeClasses.length; i++) {
                JRadioButton b = new JRadioButton(ElementsNameBuilder.getForwardMetaAssociationName(edgeClasses[i]));
                b.setActionCommand(edgeClasses[i].getName());
                messagePanel.add(b);
                buttonGroup.add(b);
                if (i == 0) {
                    b.setSelected(true);
                }
            }
            JOptionPane optionPane = new JOptionPane(messagePanel, PLAIN_MESSAGE, DEFAULT_OPTION);
            JDialog dialog = optionPane.createDialog(Static.getMainFrame(), getResString("choose_trace"));
            dialog.setVisible(true);
            String edgeClassName = buttonGroup.getSelection().getActionCommand();
            edgeClass = getClassForName(edgeClassName).asSubclass(Edge.class);
        }
        return edgeClass;
    }

}