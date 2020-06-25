package de.imise.tool3lgm.gui.menu;

import static de.imise.tool3lgm.Static.getPreSelectedGDCollection;
import static de.imise.tool3lgm.Static.getSelectedGDCollection;
import static de.imise.tool3lgm.Tool3lgmConstants.getIcon;
import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
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
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_OPTION_GDCOLL_AUTOMATIC_MODE;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_OPTION_GDOC_VERIFICATION_MODE;
import static de.imise.tool3lgm.graphtools.undoredo.TransactionManager.STANDARD_PID;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_ENABLE_SUBMODEL_BROWSER;
import static javax.swing.BoxLayout.Y_AXIS;
import static javax.swing.JOptionPane.DEFAULT_OPTION;
import static javax.swing.JOptionPane.PLAIN_MESSAGE;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.event.ActionLibrary;
import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.analyse.context.AbstractAnalysis;
import de.imise.tool3lgm.graphtools.analyse.context.AnalysesRepository;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Bendpoint;
import de.imise.tool3lgm.graphtools.metamodel.elements.CompositionEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge.ConnectionState;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.HasPartEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.InstanciationEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.metamodel.elements.Textfield;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GDCommands;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.graphtools.view.container.BendpointContainer;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.InterLayerConnectedNodeContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.graphtools.view.graph.InputGraphArea;
import de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty;
import de.imise.util.Alphabetical;
import de.imise.util.NamedObjectContainer;
import de.imise.util.pair.Pair;

/**
 * @author N.N., Thomas, AXS
 */
public class RegularContextGenerator extends ContextGenerator implements PopupMenuListener {

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
    private JMenuItem layer_transparencey_none, layer_transparencey_semi, layer_transparencey_full;

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
    private JPopupMenu menu = null;

    /**
     * COMMENTME
     */
    private boolean resizing = false;

    /** Icon für das Herstellen einer Verbindung */
    static ImageIcon link_icon = getIcon("verbindung_anlegen.gif");

    /** Icon für das Trennen einer Verbindung */
    static ImageIcon unlink_icon = getIcon("verbindung_trennen.gif");

    /** Element, das den Kontext vorgibt, also das Element auf das sich die Aktionen beziehen. */
    private ElementContainer ec = null;

    /**
     * Konstruktor, den Tool3lgm am Anfang aufruft. Der ContextListener und das
     * GrapDocument sind erstmal egal, da sie beim ersten aktivieren über
     * changeContext(GraphDocument) eines Frames auf korrekte Werte
     * gesetzt werden.
     */
    public RegularContextGenerator() {
        init();
    }

    /**
     * @return
     */
    @Override
    public GraphDocument getDoc() {
        return Static.getSelectedDoc();
    }

    // --- Methoden zur Statusveraenderung --- Ende ---

    /**
     *
     */
    private void init() {
        new_text = getItem("text_neu", MODEL_ACTION_CREATE_NODE, Textfield.class.getName());

        properties = getItem(ActionLibrary.ContextActions.ACTION_SHOW_ELEMENTS_PROPERTY_DIALOG);
        unlinkToSzenario = getItem(MODEL_ACTION_UNLINK_SELECTED_TO_SUBMODEL);
        selectLinkedSzenario = getItem("selectLinkedSzenario", MODEL_ACTION_SELECT_LINKED_SUBMODEL);
        delete_selected = getItem(MODEL_ACTION_DELETE_FROM_MODEL);
        // der leere Argumentstring bewirkt, dass am Ende ein Leerzeichen angehängt wird, hinter das dann die Hashes der zulöschenden Elemnte kommen
        delete_selected_from_szenario = getItem(MODEL_ACTION_DELETE_FROM_SUBMODEL);

        join_selected = getItem(MODEL_ACTION_JOIN_SELECTED);

        JMenuItem verify = getItem(MODEL_OPTION_GDOC_VERIFICATION_MODE);
        JMenuItem interactive = getItem(MODEL_OPTION_GDCOLL_AUTOMATIC_MODE);
        JMenuItem debugGraph = getItem(BooleanProperty.TRANSIENT_OPTION_DEBUG_GRAPH);
        JMenuItem expertMode = getItem(BooleanProperty.OPTION_ENABLE_EXPERT_MODE);

        command_line = getItem(MODEL_ACTION_COMMAND_LINE);
        queue = getItem(MODEL_ACTION_PRINT_QUEUE);
        consistency = getItem(MODEL_ACTION_INTERNAL_CHECK_CONSISTENCY);

        internals = new JMenu(getResString("intern"));
        internals.add(verify);
        internals.add(debugGraph);
        internals.add(interactive);
        internals.add(expertMode);
        internals.addSeparator();
        internals.add(command_line);
        internals.addSeparator();
        internals.add(queue);
        internals.add(consistency);

        // weiter mit Grafik-Sachen
        normalize_layer = getItem(MODEL_ACTION_SET_LAYER_DEFAULT_COLOR_AND_TRANSPARENCY);
        layer_transparencey_full = getItem(MODEL_ACTION_SET_LAYER_TRANSPARENCY_FULL);
        layer_transparencey_semi = getItem(MODEL_ACTION_SET_LAYER_TRANSPARENCY_HALF);
        layer_transparencey_none = getItem(MODEL_ACTION_SET_LAYER_TRANSPARENCY_NONE);
        color_layer = getItem(MODEL_ACTION_SET_LAYER_COLOR);

        JMenu trans_layer = new JMenu(getResString("layerTransparencyMenu"));
        trans_layer.add(layer_transparencey_none);
        trans_layer.add(layer_transparencey_semi);
        trans_layer.add(layer_transparencey_full);

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
     * Liefert das Menü für die untergeordneten Elemente.
     *
     * @return
     */
    private JMenu getSubElemMenu() {
        GraphDocument doc = getDoc();
        ElementContainer ec = doc.getLastSelected();
        ModelElement me = ec.getElement();
        Class<? extends ModelElement> elementClass = me.getClass();
        JMenu sub_elem = new JMenu(getResString("unterg_el"));
        MetaModel metaModel = doc.getMetaModel();
        //die Elementklasse darf man nicht bearbeiten, also auch keine Unterelemente hinzufügen -> raus
        if (!metaModel.isEditable(elementClass)) {
            return sub_elem;
        }
        Set<Pair<Class<? extends CompositionEdge>, Class<? extends ModelElement>>> slavePairs = new HashSet<>();
        for (Class<? extends CompositionEdge> compositionClass : metaModel.getCompositionEdgeTypesForMaster(elementClass)) {
            Class<? extends ModelElement> abstractSlaves = CompositionEdge.getSlaveType(compositionClass);
            for (Class<? extends ModelElement> instanciableSlaves : metaModel.getInstanciableAssignableClasses(abstractSlaves)) {
                slavePairs.add(new Pair<Class<? extends CompositionEdge>, Class<? extends ModelElement>>(compositionClass, instanciableSlaves));
            }
        }
        if (slavePairs.size() == 0) {
            return sub_elem;
        }
        ArrayList<JMenuItem> items = new ArrayList<>(slavePairs.size());
        for (Pair<Class<? extends CompositionEdge>, Class<? extends ModelElement>> slavePair : slavePairs) {
            Class<? extends CompositionEdge> compositionClass = slavePair.getFirstItem();
            JMenuItem item = getItem(slavePair.getSecondItem().getSimpleName(), MODEL_ACTION_CREATE_ADDICTED, doc.getHashString() + " " + me.getHashString() + " " + compositionClass.getSimpleName() + " " + slavePair.getSecondItem().getSimpleName());
            item.setEnabled(me.countConnections(compositionClass) < MetaModel.getMaxMasterToSlaveCardinality(compositionClass));
            items.add(item);
        }
        Alphabetical.sort(items);
        for (JMenuItem item : items) {
            sub_elem.add(item);
        }
        setMenuScroller(sub_elem);
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

        GDCollection gdcoll = getSelectedGDCollection();

        for (Szenario szen : gdcoll.getSzenarios()) {
            item = new JMenuItem(szen.getTitle());

            szenario_menu.add(item);

            if (OPTION_ENABLE_SUBMODEL_BROWSER.is() && szen == Static.getSelectedDoc()) {
                item.setEnabled(false);
                continue;
            }

            item.addActionListener(this);
            item.setActionCommand(MODEL_ACTION_ADD_SELECTED_TO_SUBMODEL + " " + szen.getHashString());
        }

        item = getItem(MODEL_ACTION_ADD_SELECTED_TO_ALL_SUBMODELS);
        szenario_menu.add(item);

        setMenuScroller(szenario_menu, 1, 1);
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

        GDCollection gdcoll = getSelectedGDCollection();
        for (Szenario szen : gdcoll.getSzenarios()) {
            item = new JMenuItem(szen.getTitle());
            /* ist Node schon mit diesem Szenario verknüpft */
            if (ec != null) {
                ModelElement me = ec.getElement();
                String szenHash = szen.getHashString();
                String associatedSzenHash = me.getAssociatedDoc();
                if (szenHash.equals(associatedSzenHash)) {
                    item.setEnabled(false);
                }
            }
            item.addActionListener(this);
            item.setActionCommand(MODEL_ACTION_LINK_SELECTED_TO_SUBMODEL + " " + szen.getHashString());
            link_to_szenario_menu.add(item);
        }
        setMenuScroller(link_to_szenario_menu, 1, 0);
        return link_to_szenario_menu;
    }

    /**
     * @param menu
     * @param startElement
     * @param creatableMetaPath
     * @param endElements
     * @return <code>true</code> if something was added to the menu
     */
    public boolean addConnectMenuItems(final JPopupMenu menu, final ModelElement startElement, final SimpleMetaPath creatableMetaPath, final Collection<ModelElement> endElements) {
        return addConnectMenuItems(menu, startElement, ImmutableList.of(creatableMetaPath), endElements);

    }

    /**
     * @param menu
     * @param startElement
     * @param creatableMetaPaths
     * @param endElements
     * @return <code>true</code> if something was added to the menu
     */
    private boolean addConnectMenuItems(final JPopupMenu menu, final ModelElement startElement, final Collection<SimpleMetaPath> creatableMetaPaths, Collection<ModelElement> endElements) {
        JLabel connectLabel = null;
        boolean somethingAdded = false;
        for (SimpleMetaPath metaPath : creatableMetaPaths) {
            if (connectLabel == null) {
                connectLabel = new JLabel(getResString("LABEL_CONNECT"));
                menu.add(connectLabel);
                somethingAdded = true;
            }
            Class<? extends ModelElement> endClass = metaPath.getEndClass();
            String metaPathName = metaPath.getName(false, true);
            JMenu pathConnectableElements = new JMenu(metaPathName);
            pathConnectableElements.setIcon(link_icon);
            GraphDocument doc = getDoc();
            if (endElements == null) {
                endElements = doc.getModelItems(endClass, true, true);
            }
            pathConnectableElements.setEnabled(!endElements.isEmpty());
            menu.add(pathConnectableElements);
            for (ModelElement endMe : endElements) {
                endClass = endMe.getClass();
                if (metaPath.isEndClass(endClass)) {
                    Action createPathAction = createPathAction(startElement, metaPath, endMe);
                    JMenuItem createPathItem = getItem(createPathAction);
                    pathConnectableElements.add(createPathItem);
                }
            }
            setMenuScroller(pathConnectableElements);
        }
        return somethingAdded;
    }

    /**
     * Kontextmenü eines Einzelknotens
     *
     * @param contextSource
     * @param ec
     * @return
     */
    private JPopupMenu getSingleNodeContextMenu(final Component contextSource, final ElementContainer ec) {
        //		System.err.println("ContextGenerator.getSingleNodeContextMenu()");
        JPopupMenu menu = createUpdatingPopupMenu();
        this.ec = ec;
        ModelElement me = ec.getElement();
        if (!(ec instanceof BendpointContainer)) {
            addMenuItem(menu, properties);
            menu.addSeparator();

            Class<? extends ModelElement> meClass = me.getClass();

            //Anlegbare Pfade zu anderen Elementen anbieten
            MetaModel metaModel = me.getMetaModel();
            Collection<SimpleMetaPath> creatableMetaPaths = metaModel.getCreatableMetaPaths(meClass);
            boolean connectMenuAdded = addConnectMenuItems(menu, me, creatableMetaPaths, null);

            //InstaciationEdges -> "Neue Instanz" der verbundenen Klasse erzeugen anbieten
            JLabel newInstanceLabel = null;
            if (!metaModel.isSlaveType(meClass)) {
                ElementsNameBuilder elementsNameBuilder = metaModel.getElementsNameBuilder();
                for (Class<? extends Edge> edgeClass : metaModel.getEdgeTypes(meClass)) {
                    if (MetaModel.isInstanciationMaster(edgeClass, meClass)) {
                        if (newInstanceLabel == null) {
                            newInstanceLabel = new JLabel(getResString(MODEL_ACTION_CREATE_INSTANCIATION.name()));
                            menu.add(newInstanceLabel);
                        }
                        String toolTip = elementsNameBuilder.getFullForwardMetaAssociationName(edgeClass);
                        Class<? extends ModelElement> endClass = Edge.getEndClass(edgeClass);
                        String label = elementsNameBuilder.getDisplayableName(endClass);
                        JMenuItem item = getItem(label, MODEL_ACTION_CREATE_INSTANCIATION, edgeClass.getSimpleName(), link_icon, true, toolTip);
                        menu.add(item);
                    }
                }
            }

            if (newInstanceLabel != null || connectMenuAdded) {
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
            GraphDocument doc = getDoc();
            if (doc instanceof Szenario) {
                if (ec instanceof InterLayerConnectedNodeContainer && contextSource instanceof InputGraphArea) {
                    menu.addSeparator();
                    addMenuItem(menu, show_configs);
                    addMenuItem(menu, hide_configs);
                }
                if (metaModel.hasLayout(me.getClass())) {
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
                    menu.add(MenuCollection.LayoutSubMenus.ELEMENT_ORDER_MENU);
                    menu.add(getLayerMenu());
                }
            }

            menu.addSeparator();

            // Analysemenü anfügen
            menu.add(getAnalysisMenu());

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
            if (getDoc() instanceof Szenario) {
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
    private JPopupMenu getMultiNodeContextMenu(final Component contextSource) {

        JPopupMenu menu = createUpdatingPopupMenu();

        GraphDocument doc = getDoc();
        boolean knickpunkte = doc.isSelectedOnlyBendpoints();

        if (!knickpunkte) {
            ModelElement lastSelected = doc.getLastSelected().getElement();
            Class<? extends ModelElement> lastSelectedClass = lastSelected.getClass();

            List<ModelElement> selectedElements = doc.getSelectedElements();

            List<NamedObjectContainer<JMenuItem>> connectableItems = new ArrayList<>();
            List<NamedObjectContainer<JMenuItem>> disconnectableItems = new ArrayList<>();

            MetaModel metaModel = doc.getMetaModel();

            for (Class<? extends ModelElement> me2Class : doc.getSelectedRealElementClasses()) {
                List<Object> edgesAndPaths = new ArrayList<>();
                edgesAndPaths.addAll(Arrays.asList(metaModel.getEdgeTypes(lastSelectedClass, me2Class)));
                edgesAndPaths.addAll(metaModel.getCreatableMetaPaths(lastSelectedClass, me2Class));
                for (Object edgeClassOrMetaPath : edgesAndPaths) {
                    if (edgeClassOrMetaPath instanceof Class) {
                        Class<? extends Edge> edgeClass = ((Class<?>) edgeClassOrMetaPath).asSubclass(Edge.class);
                        /////////////////////////////////////////////
                        //   Edges to Ignore ( InstanciationEdge)  //
                        /////////////////////////////////////////////
                        if (InstanciationEdge.class.isAssignableFrom(edgeClass)) {
                            continue;
                        }
                        //edges between template elements are not creatable
                        if (!metaModel.isCreatable(edgeClass)) {
                            continue;
                        }
                        //////////////
                        //   Edges  //
                        //////////////
                        for (Direction edgeDirection : Direction.values()) { //beide Richtungen testen
                            boolean addLinkMenuEntry = false;

                            //prüfen, ob die Kante in der aktuellen Richtung hinzugefügt werden soll
                            if (MetaModel.isConnecting(edgeClass, lastSelectedClass, me2Class, edgeDirection)) {
                                if (edgeDirection == Direction.FORWARD) {
                                    addLinkMenuEntry = true;
                                } else {
                                    // Doppeldeutige Kanten mit identischer Start- und Endklasse brauchen nur 1x angeboten werden -> Rückrichtung nur, wenn die Klassen verschieden sind
                                    if (MetaModel.isDoubleMeaningEdge(edgeClass)) {
                                        if (Edge.getStartClass(edgeClass) != Edge.getEndClass(edgeClass)) {
                                            addLinkMenuEntry = true;
                                        }
                                        //bei alle anderen Kanten die Rückwärtsrichtung nur hinzufügen, wenn sie in beide Richtungen unterschiedliche Bedeutungen hat,
                                        //also verscheidene Elemente verbindet oder die gleichen verbindet aber beide Richtungen unterschiedlich heißen
                                    } else if (metaModel.isDirectedEdge(edgeClass)) {
                                        addLinkMenuEntry = true;
                                    }
                                }
                            }

                            //wenn die Kante in der aktuellen Richtung hinzugefügt werden soll
                            if (addLinkMenuEntry) {
                                ConnectionState[] connectionStates;
                                //bei Kanten mit doppelter Bedeutung auch jeden ConnectionState hinzufügen (also 2 EInträge pro Kante generieren)
                                if (MetaModel.isDoubleMeaningEdge(edgeClass)) {
                                    connectionStates = new ConnectionState[2];
                                    connectionStates[0] = ConnectionState.FORWARD;
                                    connectionStates[1] = ConnectionState.BACKWARD;
                                    //bei allen anderen Kanten nur den 'egal'-COnnectionState nehmen
                                } else {
                                    connectionStates = new ConnectionState[1];
                                    connectionStates[0] = ConnectionState.DOUBLE;
                                }
                                for (ConnectionState connectionState : connectionStates) { //die bzw. den jeweiligen ConnectionStates testen
                                    boolean connectable = false;
                                    boolean disconnectable = false;
                                    //jedes selektierte Element testen
                                    for (ModelElement me2 : selectedElements) {
                                        if (lastSelected == me2) {
                                            continue;
                                        }
                                        boolean setConnectableTrue = false;
                                        setConnectableTrue |= connectionState == ConnectionState.FORWARD && !lastSelected.isConnectedTo(me2, edgeClass);
                                        setConnectableTrue |= connectionState == ConnectionState.BACKWARD && !lastSelected.isConnectedFrom(me2, edgeClass);
                                        if (!setConnectableTrue && connectionState == ConnectionState.DOUBLE) {
                                            setConnectableTrue |= !lastSelected.isConnectedWith(me2, edgeClass);
                                        }
                                        if (setConnectableTrue) {
                                            connectable = true;
                                        } else {
                                            //bei PartOfEdges darf immer nur einer der beiden disconnect-Einträge aktiv sein
                                            if (MetaModel.isHasPartEdge(edgeClass)) {
                                                if (edgeDirection == HasPartEdge.PARENT_TO_PART_DIRECTION) {
                                                    disconnectable = lastSelected.isDirectParentOf(me2);
                                                } else {
                                                    disconnectable = lastSelected.isDirectPartOf(me2);
                                                }
                                            } else {
                                                disconnectable = true;
                                            }
                                        }
                                        if (connectable && disconnectable) {
                                            break;
                                        }
                                    }
                                    String edgeClassName = edgeClass.getSimpleName();
                                    ElementsNameBuilder elementsNameBuilder = doc.getElementsNameBuilder();
                                    String label = elementsNameBuilder.getMetaAssociationName(edgeClass, edgeDirection, connectionState, false, true);
                                    String toolTip = elementsNameBuilder.getMetaAssociationName(edgeClass, edgeDirection, connectionState, true, true);
                                    //Menuitems
                                    //das muss sein, weil man sich nicht darauf verlassen sollte, dass die ConnectionStates und Directions dieselben Strings haben.
                                    //In den UndoRedo-Kommandos werden aber Directions gebraucht, die sich in diesem Fall aber aus den ConnectionStates ergeben -> sauber überführen
                                    Direction linkDirection = connectionState == ConnectionState.FORWARD ? Direction.FORWARD : connectionState == ConnectionState.BACKWARD ? Direction.BACKWARD : edgeDirection;
                                    JMenuItem connectableItem = getItem(label, MODEL_ACTION_LINK, edgeClassName + " " + linkDirection, link_icon, connectable, toolTip);
                                    JMenuItem disconnectableItem = getItem(label, MODEL_ACTION_UNLINK, edgeClassName + " " + linkDirection, unlink_icon, disconnectable, toolTip);
                                    //NamedObjectContainer um die Items
                                    NamedObjectContainer<JMenuItem> connectableContainer = new NamedObjectContainer<>(connectableItem, label);
                                    NamedObjectContainer<JMenuItem> disconnectableContainer = new NamedObjectContainer<>(disconnectableItem, label);
                                    //zu den übergebenen Ergebnislisten hinzufügen
                                    connectableItems.add(connectableContainer);
                                    disconnectableItems.add(disconnectableContainer);
                                }
                            }
                        }
                        ///////////////////
                        //   MetaPaths   //
                        ///////////////////
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
                menu.add(MenuCollection.LayoutSubMenus.ELEMENT_ORDER_MENU);
                menu.add(MenuCollection.LayoutSubMenus.ELEMENT_ALIGNMENT_MENU);
            }

            if (doc.isJoinableElementsSelected()) {
                if (menu.getComponentCount() > 0) {
                    menu.addSeparator();
                }
                menu.add(join_selected);
            }
        }

        if (menu.getComponentCount() > 0) {
            menu.addSeparator();
        }
        if (doc instanceof Szenario && !doc.isSelectedOnlyUnique() && !doc.isSelectedOnlySlaveRealNodes() && !doc.isSelectedOnlySubmodelElements()) {
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
        JPopupMenu menu = createUpdatingPopupMenu();
        GraphDocument doc = getDoc();
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
    @Override
    public final JPopupMenu getNodeContextMenu(final Component source) {
        JPopupMenu menu = createUpdatingPopupMenu();
        GraphDocument doc = getDoc();
        if (doc.isSelectedOnlyBendpoints()) {
            if (doc instanceof Szenario) {
                menu.add(delete_selected_from_szenario);
            }
        } else {
            if (doc.isSingleSelection()) {
                menu = getSingleNodeContextMenu(source, doc.getLastSelected());
            } else if (doc.isMultipleSelection()) {
                menu = getMultiNodeContextMenu(source);
            }
        }
        return menu;
    }

    /**
     * @return
     */
    private final JPopupMenu getSingleEdgeContextMenu() {
        //		System.err.println("ContextGenerator.getSingleEdgeContextMenu()");
        JPopupMenu menu = createUpdatingPopupMenu();
        GraphDocument doc = getDoc();
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
    private final JPopupMenu getMultiEdgeContextMenu() {
        JPopupMenu menu = createUpdatingPopupMenu();
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
    private final JPopupMenu getEdgeContextMenu() {
        GraphDocument doc = getDoc();
        if (doc.isSingleSelection()) {
            return getSingleEdgeContextMenu();
        }
        return getMultiEdgeContextMenu();
    }

    /**
     * @return
     */
    private final JPopupMenu getMultiContextMenu() {
        JPopupMenu menu = createUpdatingPopupMenu();
        if (menu.getComponentCount() > 0) {
            menu.addSeparator();
        }
        menu.add(getAddToSzenarioMenu());
        JMenuItem addToModelMenu = getAddToModelMenu();
        if (addToModelMenu != null) {
            menu.add(addToModelMenu);
        }

        menu.addSeparator();
        if (getDoc() instanceof Szenario) {
            menu.add(delete_selected_from_szenario);
        }
        menu.add(delete_selected);

        return menu;
    }

    /**
     * @return
     */
    private JMenu getCreateNewNodesMenu() {
        GraphDocument doc = getDoc();
        int activeLayer = doc.getCollection().getActiveLayer();
        MetaModel metaModel = doc.getMetaModel();
        ElementsNameBuilder elementsNameBuilder = metaModel.getElementsNameBuilder();
        Iterable<Class<? extends ModelElement>> creatableLayerNodes = metaModel.getCreatableLayerNodes(activeLayer);
        JMenu layerMenu = new JMenu(getResString("el_neu"));
        for (Class<? extends ModelElement> elementClass : creatableLayerNodes) {
            if (metaModel.isEditable(elementClass)) {
                JMenuItem item = new JMenuItem(elementsNameBuilder.getDisplayableName(elementClass));
                item.addActionListener(this);
                item.setActionCommand(MODEL_ACTION_CREATE_NODE + " " + elementClass.getName());
                layerMenu.add(item);
            }
        }
        return layerMenu;
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
    @Override
    public final JPopupMenu getLayerContextMenu() {
        JPopupMenu menu = createUpdatingPopupMenu();
        menu.add(getCreateNewNodesMenu());
        menu.add(new_text);
        menu.addSeparator();

        addMenuItem(menu, layer_show_configs);
        addMenuItem(menu, layer_hide_configs);

        menu.addSeparator();
        menu.add(layout_layer);
        menu.add(ActionLibrary.LayoutActions.ACTION_OPEN_GLOBAL_LAYOUT_EDITOR);
        if (Static.isExpertMode()) {
            menu.addSeparator();
            menu.add(internals);
        }
        return menu;
    }

    /**
     *
     */
    public void check_cb_menu() {
        check_undo_redo();
        GraphDocument doc = getDoc();
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
        GraphDocument doc = getDoc();
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
    public boolean isResizing() {
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
    protected boolean elementClicked = false;

    /**
     * @return
     */
    public boolean isElementClicked() {
        return elementClicked;
    }

    /**
     * @param b
     */
    public void setElementClicked(final boolean b) {
        elementClicked = b;
    }

    /**
     * COMMENTME
     */
    protected boolean layerClicked = false;

    /**
     * @return
     */
    public boolean isLayerClicked() {
        return layerClicked;
    }

    /**
     * @param b
     */
    public void setLayerClicked(final boolean b) {
        layerClicked = b;
    }

    /**
     * @param ec
     */
    public void setElementContainer(final ElementContainer ec) {
        this.ec = ec;
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
        GraphDocument doc = getDoc();
        boolean controlled = isControlled();
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
                    left_nodehand_noctrl_edges();
                    return;
                }
                if (left_button && controlled) {
                    left_nodehand_ctrl_edges();
                    return;
                }
                return;
            }
            // nur Node selektiert
            if (doc.isSelectedOnlyNodes()) {
                if (left_button && !controlled) {
                    left_nodehand_noctrl_nodes();
                    return;
                }
                if (left_button && controlled) {
                    left_nodehand_ctrl_nodes();
                    return;
                }
                return;
            }
            // Node und Kanten selektiert
            if (left_button && !controlled) {
                left_nodehand_noctrl_multi();
                return;
            }
            if (left_button && controlled) {
                left_nodehand_ctrl_multi();
                return;
            }
            return;
        }

        if (elementClicked) {
            //wenn man auf einer selektierten Edge das Kontexmenü auf einem Knickpunkt öffnet,
            //dann soll das Kontextmenü aufgehen, als wäre die Edge angeklickt worden und nicht
            //der BendpointContainer, der ja ein Knotenkontainer ist und ein sinnloses Kontextmenü
            //anzeigen würde
            if (ec instanceof BendpointContainer) {
                EdgeContainer kc = ((Bendpoint) ec.getElement()).getOwner();
                if (doc.isSelected(kc)) {
                    if (left_button) {
                        doc.select(ec, TransactionManager.STANDARD_PID);
                    } else {
                        ec = kc;
                    }
                }
            }

            if (ec instanceof NodeContainer) {
                // nichts selektiert
                if (!doc.isSelection()) {
                    if (left_button && !controlled) {
                        left_node_noctrl_none();
                        return;
                    }
                    if (left_button && controlled) {
                        left_node_ctrl_none();
                        return;
                    }
                    if (right_button) {
                        right_node_none(gdl, xin, yin);
                        return;
                    }
                    return;
                }
                // nur Kanten selektiert
                if (doc.isSelectedOnlyEdges()) {
                    if (left_button && !controlled) {
                        left_node_noctrl_edges();
                        return;
                    }
                    if (left_button && controlled) {
                        left_node_ctrl_edges();
                        return;
                    }
                    if (right_button) {
                        right_node_edges(gdl, xin, yin);
                        return;
                    }
                    return;
                }
                // nur Node selektiert
                if (doc.isSelectedOnlyNodes()) {
                    if (left_button && !controlled) {
                        left_node_noctrl_nodes();
                        return;
                    }
                    if (left_button && controlled) {
                        left_node_ctrl_nodes();
                        return;
                    }
                    if (right_button) {
                        right_node_nodes(gdl, xin, yin);
                        return;
                    }
                    return;
                }
                // Node und Kanten selektiert
                if (left_button && !controlled) {
                    left_node_noctrl_multi();
                    return;
                }
                if (left_button && controlled) {
                    left_node_ctrl_multi();
                    return;
                }
                if (right_button) {
                    right_node_multi(gdl, xin, yin);
                    return;
                }
                return;
            } else if (ec instanceof EdgeContainer) {
                // nichts selektiert
                if (!doc.isSelection()) {
                    if (left_button && !controlled) {
                        left_edge_noctrl_none();
                        return;
                    }
                    if (left_button && controlled) {
                        left_edge_ctrl_none();
                        return;
                    }
                    if (right_button) {
                        right_edge_none(gdl, xin, yin);
                        return;
                    }
                    return;
                }
                // nur Kanten selektiert
                if (doc.isSelectedOnlyEdges()) {
                    if (left_button && !controlled) {
                        left_edge_noctrl_edges();
                        return;
                    }
                    if (left_button && controlled) {
                        left_edge_ctrl_edges();
                        return;
                    }
                    if (right_button) {
                        right_edge_edges(gdl, xin, yin);
                        return;
                    }
                    return;
                }
                // nur Node selektiert
                if (doc.isSelectedOnlyNodes()) {
                    if (left_button && !controlled) {
                        left_edge_noctrl_nodes();
                        return;
                    }
                    if (left_button && controlled) {
                        left_edge_ctrl_nodes();
                        return;
                    }
                    if (right_button) {
                        right_edge_nodes(gdl, xin, yin);
                        return;
                    }
                    return;
                }
                // Node und Kanten selektiert
                if (left_button && !controlled) {
                    left_edge_noctrl_multi();
                    return;
                }
                if (left_button && controlled) {
                    left_edge_ctrl_multi();
                    return;
                }
                if (right_button) {
                    right_edge_multi(gdl, xin, yin);
                    return;
                }
                return;
            }
        }

        if (layerClicked) {
            // nichts selektiert
            if (!doc.isSelection()) {
                if (left_button && !controlled) {
                    left_layer_noctrl_none();
                    return;
                }
                if (left_button && controlled) {
                    left_layer_ctrl_none();
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
                    left_layer_noctrl_edges();
                    return;
                }
                if (left_button && controlled) {
                    left_layer_ctrl_edges();
                    return;
                }
                if (right_button) {
                    right_layer_edges(gdl, xin, yin);
                    return;
                }
                return;
            }
            // nur Node selektiert
            if (doc.isSelectedOnlyNodes()) {
                if (left_button && !controlled) {
                    left_layer_noctrl_nodes();
                    return;
                }
                if (left_button && controlled) {
                    left_layer_ctrl_nodes();
                    return;
                }
                if (right_button) {
                    right_layer_nodes(gdl, xin, yin);
                    return;
                }
                return;
            }
            // Node und Kanten selektiert
            if (left_button && !controlled) {
                left_layer_noctrl_multi();
                return;
            }
            if (left_button && controlled) {
                left_layer_ctrl_multi();
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
            left_noctrl_outside();
            return;
        }
        if (left_button && controlled) {
            left_ctrl_outside();
            return;
        }
    }

    // Methoden, die aus dem Zustand und der Aktion mit der Maus eine semantische machen

    // Es sind 56 Stueck. Viel Spass beim Hacken. Wahrscheinlich werden nur wenige
    // richtig programmiert werden, der Grossteil wird nix machen oder auf andere
    // Methoden zeigen, die dasselbe machen

    // Namensgebung: Button_ObjektTyp_Ctrl_Selection

    private void deselectAll() {
        GraphDocument doc = getDoc();
        doc.deselectAll(false);
    }

    /**
     *
     */
    private void left_ctrl_outside() {
    }

    /**
     *
     */
    private void left_noctrl_outside() {
        deselectAll();
    }

    /**
     *
     */
    private void left_layer_noctrl_none() {
        deselectAll();
    }

    /**
     *
     */
    private void left_layer_noctrl_nodes() {
        deselectAll();
    }

    /**
     *
     */
    private void left_layer_noctrl_edges() {
        deselectAll();
    }

    /**
     *
     */
    private void left_layer_noctrl_multi() {
        deselectAll();
    }

    /**
     *
     */
    private void left_layer_ctrl_none() {
    }

    /**
     *
     */
    private void left_layer_ctrl_nodes() {
        deselectAll();
    }

    /**
     *
     */
    private void left_layer_ctrl_edges() {
        deselectAll();
    }

    /**
     *
     */
    private void left_layer_ctrl_multi() {
    }

    /**
     * @param gdl
     * @param xin
     * @param yin
     */
    private void right_layer_none(final Component gdl, final int xin, final int yin) {
        menu = getLayerContextMenu();
        menu.show(gdl, xin, yin);
    }

    /**
     * @param gdl
     * @param xin
     * @param yin
     */
    private void right_layer_nodes(final Component gdl, final int xin, final int yin) {
        menu = getLayerContextMenu();
        menu.show(gdl, xin, yin);
    }

    /**
     * @param gdl
     * @param xin
     * @param yin
     */
    private void right_layer_edges(final Component gdl, final int xin, final int yin) {
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
    private void right_node_none(final Component gdl, final int xin, final int yin) {
        //System.err.println("right_node_none");
        GraphDocument doc = getDoc();
        doc.addToSelection(ec, 0);
        menu = getNodeContextMenu(gdl);
        menu.show(gdl, xin, yin);
    }

    /**
     * @param gdl
     * @param xin
     * @param yin
     */
    private void right_node_nodes(final Component gdl, final int xin, final int yin) {
        //System.err.println("right_node_nodes");
        GraphDocument doc = getDoc();
        doc.addToSelection(ec, 0);
        menu = getNodeContextMenu(gdl);
        menu.show(gdl, xin, yin);
    }

    /**
     * @param gdl
     * @param xin
     * @param yin
     */
    private void right_node_edges(final Component gdl, final int xin, final int yin) {
        //System.err.println("right_node_edges");
        GraphDocument doc = getDoc();
        doc.addToSelection(ec, 0);
        menu = getMultiContextMenu();
        menu.show(gdl, xin, yin);
    }

    /**
     * @param gdl
     * @param xin
     * @param yin
     */
    private void right_node_multi(final Component gdl, final int xin, final int yin) {
        //System.err.println("right_node_multi");
        GraphDocument doc = getDoc();
        doc.addToSelection(ec, 0);
        menu = getMultiNodeContextMenu(gdl);
        menu.show(gdl, xin, yin);
    }

    // Linke Maustaste auf Node

    /**
     *
     */
    private void select() {
        GraphDocument doc = getDoc();
        doc.select(ec, STANDARD_PID);
    }

    /**
     *
     */
    private void addToSelection() {
        GraphDocument doc = getDoc();
        doc.addToSelection(ec, STANDARD_PID);
    }

    /**
     *
     */
    private void deselect() {
        GraphDocument doc = getDoc();
        doc.deselect(ec, STANDARD_PID);
    }

    /**
     *
     */
    private void left_node_noctrl_none() {
        select();
    }

    /**
     *
     */
    private void left_node_noctrl_nodes() {
        select();
    }

    /**
     *
     */
    private void left_node_noctrl_edges() {
        select();
    }

    /**
     *
     */
    private void left_node_noctrl_multi() {
        select();
    }

    /**
     *
     */
    private void left_node_ctrl_none() {
        addToSelection();
    }

    /**
     *
     */
    private void left_node_ctrl_nodes() {
        if (ec.isSelected()) {
            deselect();
        } else {
            addToSelection();
        }
    }

    /**
     *
     */
    private void left_node_ctrl_edges() {
        left_node_ctrl_nodes();
    }

    /**
     *
     */
    private void left_node_ctrl_multi() {
        left_node_ctrl_nodes();
    }

    // Klicks auf die Kanten

    /**
     *
     */
    private void left_edge_noctrl_none() {
        select();
    }

    /**
     *
     */
    private void left_edge_noctrl_nodes() {
        select();
    }

    /**
     *
     */
    private void left_edge_noctrl_edges() {
        select();
    }

    /**
     *
     */
    private void left_edge_noctrl_multi() {
        select();
    }

    /**
     *
     */
    private void left_edge_ctrl_none() {
        addToSelection();
    }

    /**
     *
     */
    private void left_edge_ctrl_nodes() {
        left_node_ctrl_nodes();
    }

    /**
     *
     */
    private void left_edge_ctrl_edges() {
        left_edge_ctrl_nodes();
    }

    /**
     *
     */
    private void left_edge_ctrl_multi() {
        left_edge_ctrl_nodes();
    }

    /**
     * @param gdl
     * @param xin
     * @param yin
     */
    private void right_edge_none(final Component gdl, final int xin, final int yin) {
        addToSelection();
        menu = getEdgeContextMenu();
        menu.show(gdl, xin, yin);
    }

    /**
     * @param gdl
     * @param xin
     * @param yin
     */
    private void right_edge_nodes(final Component gdl, final int xin, final int yin) {
        addToSelection();
        menu = getEdgeContextMenu();
        menu.show(gdl, xin, yin);
    }

    /**
     * @param gdl
     * @param xin
     * @param yin
     */
    private void right_edge_edges(final Component gdl, final int xin, final int yin) {
        addToSelection();
        menu = getEdgeContextMenu();
        menu.show(gdl, xin, yin);
    }

    /**
     * @param gdl
     * @param xin
     * @param yin
     */
    private void right_edge_multi(final Component gdl, final int xin, final int yin) {
        addToSelection();
        menu = getEdgeContextMenu();
        menu.show(gdl, xin, yin);
    }

    // Klicks in die Node-Haende

    // Klicks mit rechts
    /*
     * private void right_nodehand_nodes() {
     * }
     * private void right_nodehand_edges() {
     * }
     * private void right_nodehand_multi() {
     * }
     */
    // Klicks mit links

    /**
     *
     */
    private void left_nodehand_noctrl_nodes() {
        select();
    }

    /**
     *
     */
    private void left_nodehand_noctrl_edges() {
        select();
    }

    /**
     *
     */
    private void left_nodehand_noctrl_multi() {
        select();
    }

    /**
     *
     */
    private void left_nodehand_ctrl_nodes() {
    }

    /**
     *
     */
    private void left_nodehand_ctrl_edges() {
    }

    /**
     *
     */
    private void left_nodehand_ctrl_multi() {
    }

    /**
     *
     */
    public final void closeMenu() {
        if (menu != null) {
            menu.setVisible(false);
        }
    }

    /**
     * @return
     */
    public JPopupMenu getDialogSelectionContextMenu(final boolean propertiesOnly) {
        JPopupMenu menu = createUpdatingPopupMenu();
        addMenuItem(menu, properties);
        if (!propertiesOnly) {
            menu.addSeparator();
            GraphDocument doc = getDoc();
            boolean do_join = doc.isJoinableElementsSelected();
            if (do_join) {
                menu.add(join_selected);
                menu.addSeparator();
            }
            if (doc instanceof Szenario) {
                menu.add(delete_selected_from_szenario);
            }
            menu.add(delete_selected);
            delete_selected.setEnabled(doc.isSelection());
        }
        //		System.out.println("getDialogSelectionContextMenu - addPopupMenuListener ausgeführt");
        menu.addPopupMenuListener(this);
        return menu;
    }

    /**
     * @param ec
     * @return
     */
    public JPopupMenu getDialogSelectionContextMenu(final ElementContainer ec) {
        JPopupMenu menu = createUpdatingPopupMenu();
        GraphDocument doc = ec.getGraphDocument();
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
    private JMenu getAnalysisMenu() {
        JMenu menu = new JMenu(getResString("analysis"));
        GraphDocument doc = getDoc();
        ElementContainer ec = doc.getLastSelected();
        if (ec != null && ec.getElement() instanceof Node) {
            // Alle Analysen für die ausgewählte Klasse holen
            Class<? extends ModelElement> elementClass = ec.getElement().getClass();
            List<AbstractAnalysis> analysis = AnalysesRepository.getAnalyses(Static.getSelectedMetaModel(), elementClass);
            // Analysen ins Menü eintragen
            for (final AbstractAnalysis ana : analysis) {
                JMenuItem item = new JMenuItem(ana.getName());
                menu.add(item);
                item.addActionListener(e -> ana.setAnalysisResult(doc));
            }
        }
        menu.setEnabled(menu.getItemCount() != 0);
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
        GraphDocument doc = getDoc();
        List<ModelElement> selectedElements = doc.getSelectedElements();
        String pathName = path2create.getName(false, true);
        return createPathAction(null, path2create, selectedElements, pathName, link_icon);
    }

    /**
     * Liefert eine Action zu einem SimpleMetaPath der zwischen dem übergebenen StartElement hin zum übergebenen Endelement angelegt wird.
     * Ist das startElement null wird das zuletzt seletierte genommen.
     *
     * @param startElement
     * @param path2create
     * @param endElement
     * @return
     */
    private Action createPathAction(final ModelElement startElement, final SimpleMetaPath path2create, final ModelElement endElement) {
        return createPathAction(startElement, path2create, ImmutableList.of(endElement), endElement.getName(), null);
    }

    /**
     * Liefert eine Action zu einem SimpleMetaPath der zwischen dem zuletzt selektierten Element und allen zum Endelement des Pfades passenden anderen
     * übergebenen Elementen angelegt wird.
     *
     * @param startElement
     * @param path2create Pfad der angelegt werden soll
     * @param endElements Elemente, zu denen der Pfad vom zuletzt selektierten Element aus angelegt werden soll
     * @param name Name der Action
     * @param icon Icon der Sction
     * @return
     */
    private Action createPathAction(final ModelElement startElement, final SimpleMetaPath path2create, final Collection<ModelElement> endElements, final String name, final Icon icon) {
        return new AbstractAction(name, icon) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Class<? extends ModelElement> startClass = path2create.getStartClass();
                Class<? extends ModelElement> endClass = path2create.getEndClass();
                GraphDocument doc = getDoc();
                ModelElement realStartElement = startElement;
                if (realStartElement == null) {
                    ElementContainer lastSelected = doc.getLastSelected();
                    realStartElement = lastSelected.getElement();
                }
                Class<? extends ModelElement> realStartElementClass = realStartElement.getClass();
                if (!startClass.isAssignableFrom(realStartElementClass)) {
                    return;
                }
                for (ModelElement me : endElements) {
                    if (realStartElement == me) {
                        continue;
                    }
                    Class<? extends ModelElement> meClass = me.getClass();
                    if (!endClass.isAssignableFrom(meClass)) {
                        continue;
                    }
                    GDCollection gdcoll = doc.getCollection();
                    boolean lastAutomaticMode = gdcoll.setAutomaticMode(true);
                    doc.createPath(realStartElement, me, path2create, STANDARD_PID);
                    gdcoll.setAutomaticMode(lastAutomaticMode);
                }
            }
        };
    }

    //--------------------------------------------------------------------------------------------------------------------------------
    // PopupMenuListener-Funktionen

    @Override
    public void popupMenuWillBecomeVisible(final PopupMenuEvent e) {
        //do nothing
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
        //do nothing
    }

    //--------------------------------------------------------------------------------------------------------------------------------

    /**
     * @return
     */
    protected JMenuItem getAddToModelMenu() {
        MetaModelContext selectedMetaModelContext = Static.getSelectedMetaModelContext();
        List<GDCollection> collections = Static.getCollections(selectedMetaModelContext);
        if (collections.size() < 2) {
            return null;
        }
        JMenu menu = new JMenu(getResString("inmodel"));
        for (GDCollection gdcoll : collections) {
            GraphDocument doc = getDoc();
            if (gdcoll != doc.getCollection()) {
                menu.add(getCopyToModelMenu(gdcoll));
            }
        }
        return menu;
    }

    /**
     * @return
     */
    private JMenuItem getJoinMenu() {
        if (ec == null) {
            return null;
        }
        if (Static.getCollectionCount() < 2) {
            return null;
        }
        LGMGraphDocument doc = (LGMGraphDocument) getDoc();
        if (!doc.isSingleSelection()) {
            return null;
        }
        JMenu menu = new JMenu(getResString("join_elements"));
        JMenuItem item;
        ModelElement me1 = doc.getLastSelected().getElement();
        for (GDCollection gdcoll : Static.iterableCollections()) {
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
     * @param metaModel
     * @param elementClass1
     * @param elementClass2
     * @return
     */
    public static Class<? extends Edge> requestCurrentEdgeType(final MetaModel metaModel, final Class<? extends ModelElement> elementClass1, final Class<? extends ModelElement> elementClass2) {
        Class<? extends Edge> edgeClass = null;
        Class<? extends Edge>[] edgeClasses = metaModel.getEdgeTypes(elementClass1, elementClass2);
        if (edgeClasses == null || edgeClasses.length == 0) {
            return null;
        }
        edgeClass = edgeClasses[0];
        if (edgeClasses.length > 1) {
            JPanel messagePanel = new JPanel();
            messagePanel.setLayout(new BoxLayout(messagePanel, Y_AXIS));
            ButtonGroup buttonGroup = new ButtonGroup();
            ElementsNameBuilder elementsNameBuilder = metaModel.getElementsNameBuilder();
            for (int i = 0; i < edgeClasses.length; i++) {
                JRadioButton b = new JRadioButton(elementsNameBuilder.getForwardMetaAssociationName(edgeClasses[i]));
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
            edgeClass = metaModel.getClassForName(edgeClassName).asSubclass(Edge.class);
        }
        return edgeClass;
    }

}