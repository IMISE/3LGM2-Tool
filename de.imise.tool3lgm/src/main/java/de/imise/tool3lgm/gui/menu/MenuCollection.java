package de.imise.tool3lgm.gui.menu;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.DOMAIN_LAYER;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.LOGICAL_LAYER;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.PHYSICAL_LAYER;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.event.ActionLibrary;
import de.imise.tool3lgm.event.ActionLibrary.AnalysisActions;
import de.imise.tool3lgm.event.ActionLibrary.EditActions;
import de.imise.tool3lgm.event.ActionLibrary.ExtrasActions;
import de.imise.tool3lgm.event.ActionLibrary.FileActions;
import de.imise.tool3lgm.event.ActionLibrary.FileActions.ExportActions;
import de.imise.tool3lgm.event.ActionLibrary.FileActions.ImportActions;
import de.imise.tool3lgm.event.ActionLibrary.OptionsActions;
import de.imise.tool3lgm.event.ActionLibrary.OptionsActions.Analysis;
import de.imise.tool3lgm.event.ActionLibrary.OptionsActions.Graphics;
import de.imise.tool3lgm.event.ActionLibrary.ViewActions;
import de.imise.tool3lgm.event.action.ChangeLocaleAction;
import de.imise.tool3lgm.event.action.StaticAction;
import de.imise.tool3lgm.graphtools.dialog.ElementAlignmentDialog;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCommands;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty;
import de.imise.util.Alphabetical;
import de.imise.util.NamedObjectContainer;
import de.imise.util.swing.menu.DynamicMenu;
import de.imise.util.swing.menu.MenuCreator;
import de.imise.util.swing.menu.MenuScroller;

/**
 * Sammlung aller Menus für die {@link MenuBar}
 *
 * @author fstephan
 */
public class MenuCollection {

    private static JMenu createMenu(final String titleResKey, final Object... menuEntries) {
        return MenuCreator.createMenu(getResString(titleResKey), menuEntries);
    }

    /** Das Datei-Menu */
    public static final JMenu FILE_MENU = new FileMenu();

    /** Das Bearbeiten-Menu */
    public static final JMenu EDIT_MENU = createMenu("edit", EditActions.ACTION_UNDO, EditActions.ACTION_REDO, new JSeparator(), EditActions.ACTION_SEARCH, new JSeparator(), EditActions.SELECT_ALL, new JSeparator(), GDCommands.MODEL_ACTION_COPY,
            GDCommands.MODEL_ACTION_CUT, GDCommands.MODEL_ACTION_PASTE, new JSeparator(), GDCommands.MODEL_ACTION_DELETE_FROM_SUBMODEL, GDCommands.MODEL_ACTION_DELETE_FROM_MODEL);

    /** Das Ansicht-Menu */
    public static final JMenu VIEW_MENU = createMenu("viewMenu", ViewSubMenus.TOOLBAR_MENU, BooleanProperty.OPTION_MODEL_BROWSER_SHOW, new JSeparator(), ViewActions.ACTION_GRAPH_SWITCH_ONE_LAYER_AND_THREE_LAYER_PERSPECTIVE,
            ViewActions.ACTION_ACTIVATE_DOMAIN_LAYER, ViewActions.ACTION_ACTIVATE_LOGICAL_TOOL_LAYER, ViewActions.ACTION_ACTIVATE_PHYSICAL_TOOL_LAYER, ViewActions.ACTION_OPEN_GRAPH_VIEW_SETTINGS_DIALOG, new JSeparator(),
            ViewActions.ACTION_OPEN_MATRIX_VIEW);

    /** Das Einfügen-Menu */
    public static final JMenu INSERT_MENU = new InsertMenu();

    /** Das Format-Menu */
    public static final JMenu LAYOUT_MENU = new LayoutMenu();

    /** Das Teilmodell-Menu */
    public static final JMenu SUBMODEL_MENU = createMenu("submodels", GDCommands.MODEL_ACTION_CREATE_SUBMODEL, GDCommands.MODEL_ACTION_DELETE_SUBMODEL, new JSeparator(), GDCommands.MODEL_ACTION_RENAME_SUBMODEL);

    /** Das XMLAnalyse-Menu */
    public static final JMenu ANALYSIS_MENU = createMenu("analysis", AnalysisActions.ACTION_ANALYSIS_OPEN_REPOSITORY, AnalysisActions.ACTION_ANALYSIS_OPEN_EDITOR, AnalysisActions.ACTION_ANALYSIS_RESET_RESULT,
            BooleanProperty.OPTION_CREATE_NEW_SUBMODEL_FOR_ANALYSIS_RESULT, AnalysisActions.ACTION_ANALYSIS_CHOOSE_GRAPH_ANALYSIS_RESULT_COLOR, new JSeparator(), AnalysisActions.ACTION_ANALYSIS_REDUNDANCY,
            ModelConstants.getAnalysisDefinition().getAnalysisActions(), Analysis.OPTIONS_SIMPLE_REDUNDANCY_ANALYSIS);

    /** Das Optionen-Menu */
    public static final JMenu OPTIONS_MENU = createMenu("options", OptionsSubMenus.GENERAL_OPTIONS_MENU, OptionsSubMenus.BROWSER_OPTIONS_MENU, OptionsSubMenus.GRAPHICS_OPTIONS_MENU, OptionsActions.ACTION_PROPERTY_INT_RMI_PORT,
            OptionsActions.ACTIONS_CHOOSE_META_MODEL, OptionsSubMenus.LOCALE_MENU);

    /** Das Extras-Menu */
    public static final JMenu EXTRAS_MENU = createMenu("extras", ExtrasActions.ACTION_OPEN_USERFIELD_DEFINITION_DIALOG, ExtrasActions.ACTION_OPEN_USERFIELD_VALUE_EDITOR_DIALOG, BooleanProperty.OPTION_ENABLE_CLASSIFICATION_NUMBER_CALCULATION,
            new JSeparator(), ModelConstants.getExtrasActions(false), new JSeparator(), BooleanProperty.OPTION_CHECK_CONSISTENCY, ExtrasSubMenus.PLUGIN_MENU);

    /** Das Fenster-Menu */
    public static final JMenu WINDOW_MENU = new WindowMenu();

    /** Das Hilfe-Menu */
    public static final JMenu HELP_MENU = new HelpMenu();

    /** Sammlung der Unter-Menus des Datei-Menus */
    static class FileSubMenus {

        /** Das Import-Menu */
        public static final JMenu IMPORT_MENU = createMenu("import", ImportActions.ACTION_IMPORT_SUBMODEL, ImportActions.ACTION_IMPORT_MODEL, new JSeparator(), ImportActions.ACTION_IMPORT_DATA);

        /** Das Export-Menu */
        public static final JMenu EXPORT_MENU = createMenu("export", ExportActions.ACTION_EXPORT_GRAPHIC, ExportActions.ACTION_EXPORT_GRAPHML_YED, ExportActions.ACTION_EXPORT_GRAPHML_YFILES, new JSeparator(), ExportActions.ACTION_EXPORT_XSLT,
                ExportActions.ACTION_EXPORT_SUBMODEL, new JSeparator(), ExportActions.ACTION_EXPORT_HTML, new JSeparator(), ExportActions.ACTION_EXPORT_DATA);
    }

    /** Sammlung der Unter-Menus des Ansicht-Menus */
    static class ViewSubMenus {

        /** Das Symbolleisten-Menu */
        public static final JMenu TOOLBAR_MENU = createMenu("toolbarMenu", BooleanProperty.OPTION_SHOW_PAINTING_TOOLBAR, BooleanProperty.OPTION_SHOW_STANDARD_TOOLBAR);
    }

    /** Sammlung der Unter-Menus des Format-Menus */
    public static class LayoutSubMenus {

        /** Das Element-Layout-Menu */
        public static final JMenu ELEMENT_LAYOUT_MENU = createMenu("elementLayoutMenu", GDCommands.MODEL_ACTION_SET_ELEMENT_FONT, GDCommands.MODEL_ACTION_SET_ELEMENT_COLOR, createMenu( // Transparenz-Menu
                "elementTransparencyMenu", GDCommands.MODEL_ACTION_SET_ELEMENT_TRANSPARENCY_NONE, GDCommands.MODEL_ACTION_SET_ELEMENT_TRANSPARENCY_HALF, GDCommands.MODEL_ACTION_SET_ELEMENT_TRANSPARENCY_FULL),
                createMenu( // Auf Standard setzen Menu
                        "elementSetToStandardMenu", GDCommands.MODEL_ACTION_SET_ELEMENT_DEFAULT_FONT, GDCommands.MODEL_ACTION_SET_ELEMENT_DEFAULT_COLOR, GDCommands.MODEL_ACTION_SET_ELEMENT_DEFAULT_TRANSPARENCY,
                        GDCommands.MODEL_ACTION_SET_ELEMENT_DEFAULT_FULL_LAYOUT),
                new JSeparator(), createMenu( // Icon-Menu
                        "icon", GDCommands.MODEL_ACTION_SET_ELEMENT_ICON_NONE, GDCommands.MODEL_ACTION_SET_ELEMENT_ICON),
                new JSeparator(), createMenu( // Textausrichtungs-Menu (horizontal)
                        "textAlignmentMenu", GDCommands.MODEL_ACTION_SET_ELEMENT_LABEL_HALIGN_LEFT, GDCommands.MODEL_ACTION_SET_ELEMENT_LABEL_HALIGN_CENTER, GDCommands.MODEL_ACTION_SET_ELEMENT_LABEL_HALIGN_RIGHT, new JSeparator(),
                        GDCommands.MODEL_ACTION_SET_ELEMENT_LABEL_VALIGN_TOP, GDCommands.MODEL_ACTION_SET_ELEMENT_LABEL_VALIGN_CENTER, GDCommands.MODEL_ACTION_SET_ELEMENT_LABEL_VALIGN_BOTTOM));

        /** Das Ebenen-Layout-Menu */
        public static final JMenu LAYER_LAYOUT_MENU = createMenu("layerLayoutMenu", GDCommands.MODEL_ACTION_SET_LAYER_DEFAULT_COLOR_AND_TRANSPARENCY, GDCommands.MODEL_ACTION_SET_LAYER_COLOR, createMenu( // Transparenz-Menu
                "layerTransparencyMenu", GDCommands.MODEL_ACTION_SET_LAYER_TRANSPARENCY_NONE, GDCommands.MODEL_ACTION_SET_LAYER_TRANSPARENCY_HALF, GDCommands.MODEL_ACTION_SET_LAYER_TRANSPARENCY_FULL));

        /** Das Level-Menu */
        public static final JMenu ELEMENT_LEVEL_MENU = createMenu(// Elementreihenfolge
                "levelMenu", GDCommands.MODEL_ACTION_MOVE_ORDER_TO_FIRST_POSITION, GDCommands.MODEL_ACTION_MOVE_ORDER_ONE_POSITION_UP, GDCommands.MODEL_ACTION_MOVE_ORDER_ONE_POSITION_DOWN, GDCommands.MODEL_ACTION_MOVE_ORDER_TO_LAST_POSITION);

        /** Das Elemente-Ausrichtung-Menu */
        public static final JMenu ELEMENT_ALIGNMENT_MENU = createMenu( // Elemente ausrichten
                "elementAlignmentMenu", GDCommands.MODEL_ACTION_SET_ELEMENT_ALIGNMENT_HORIZONTAL_LEFT, GDCommands.MODEL_ACTION_SET_ELEMENT_ALIGNMENT_HORIZONTAL_CENTER, GDCommands.MODEL_ACTION_SET_ELEMENT_ALIGNMENT_HORIZONTAL_RIGHT, new JSeparator(),
                GDCommands.MODEL_ACTION_SET_ELEMENT_ALIGNMENT_VERTICAL_TOP, GDCommands.MODEL_ACTION_SET_ELEMENT_ALIGNMENT_VERTICAL_CENTER, GDCommands.MODEL_ACTION_SET_ELEMENT_ALIGNMENT_VERTICAL_BOTTOM, new JSeparator(),
                GDCommands.MODEL_ACTION_SET_ELEMENT_ALIGNMENT_SIZE_WIDTH, GDCommands.MODEL_ACTION_SET_ELEMENT_ALIGNMENT_SIZE_HEIGTH, GDCommands.MODEL_ACTION_SET_ELEMENT_ALIGNMENT_SIZE_WIDTH_AND_HEIGTH, new JSeparator(),
                ElementAlignmentDialog.getElementAlignmentAction());
    }

    /** Sammlung der Unter-Menus des Optionen-Menus */
    static class OptionsSubMenus {

        /** Menu für allgemeine Optionen */
        public static final JMenu GENERAL_OPTIONS_MENU = createMenu("general", BooleanProperty.OPTION_SHOW_REMOVE_WARNING, BooleanProperty.OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARENTS, BooleanProperty.OPTION_SHOW_PART_OF_HIERARCHY);

        /** Das Browser-Optionen-Menu */
        public static final JMenu BROWSER_OPTIONS_MENU = createMenu("browserOptionsMenu", BooleanProperty.OPTION_ENABLE_SUBMODEL_BROWSER, BooleanProperty.OPTION_SHOW_MODELS_IN_SEPARATE_BROWSER,
                //createCheckBoxItem(ActionLibrary.ViewActions.SHOW_SUBMODELS_IN_BROWSER_SIDE_BY_SIDE),
                new JSeparator(), BooleanProperty.OPTION_SHOW_USER_DEFINED_PROPERTIES_IN_MODEL_BROWSER);

        /** Das Grafik-Optionen-Menu */
        public static final JMenu GRAPHICS_OPTIONS_MENU = createMenu("graphicOptionsMenu", BooleanProperty.OPTION_USE_RASTER, BooleanProperty.OPTION_SHOW_RASTER, BooleanProperty.OPTION_PAINT_EDGES_ONLY_FOR_SELECTED_ELEMENTS,
                BooleanProperty.OPTION_SHOW_LINKED_WITH_SUBMODEL_SYMBOLS, Graphics.MODEL_ACTIONS_HIDE_UNHIDE_UNASSOCIATED, BooleanProperty.OPTION_ASSIGN_CONFIGURATION_COLORS, BooleanProperty.OPTION_GRAPH_MOVE_SUBELEMENTS,
                BooleanProperty.TRANSIENT_OPTION_SHOW_EXPANSION_SIGN, Graphics.ACTION_PROPERTY_INT_RENDER_SETTINGS
        //createCheckBoxItem(Graphics.TOOLTIPS)
        );

        /** Das Sprach-Menu */
        public static final JMenu LOCALE_MENU = createLocaleMenu();

        /** Methode erzeugt das Sprachen-Menu */
        private static final DynamicMenu createLocaleMenu() {
            ChangeLocaleAction[] actions = ChangeLocaleAction.getAllActions();
            DynamicMenu localeMenu = new DynamicMenu(getResString("localeOptionsMenu"));
            ButtonGroup bg = new ButtonGroup();
            for (ChangeLocaleAction action : actions) {
                JRadioButtonMenuItem languageButton = MenuCreator.createRadioButtonMenuItem(action);
                bg.add(languageButton);
                localeMenu.add(languageButton);
                languageButton.setSelected(action.isSelected());
            }
            return localeMenu;
        }
    }

    /** Sammlung der Unter-Menus des Extras-Menus */
    static class ExtrasSubMenus {

        public static final JMenu PLUGIN_MENU = createMenu("plugin", ModelConstants.getExtrasActions(true));

    }

    /* *************************************************** Start: Unterklassen ******************************** */

    /** Das Datei-Menu */
    private static class FileMenu extends DynamicMenu {

        /** Name dieses Menus */
        public static final String title = getResString("file");

        /** Index, an dem die erste Datei im Menu steht */
        private static final int firstFileIndex = 11;

        /** Index, an dem die letzte Datei im Menu steht */
        private int lastFileIndex = -1;

        public FileMenu() {
            super(title);
            MenuCreator.addAll(this,
                    MenuCreator.createMenuEntries(true, FileActions.ACTION_NEW_MODEL, FileActions.ACTION_OPEN_MODEL, FileActions.ACTION_SAVE_MODEL, FileActions.ACTION_SAVE_MODEL_AS, FileActions.ACTION_CLOSE_MODEL, new JSeparator(),
                            FileActions.ACTION_SHOW_MODEL_DESCRIPTION_FRAME, new JSeparator(), FileSubMenus.IMPORT_MENU, FileSubMenus.EXPORT_MENU, new JSeparator(),
                            //hier werden später die zuletzt geladenen Modelle angezeigt
                            new JSeparator(), FileActions.ACTION_EXIT));
        }

        @Override
        protected void updateItems() {
            Action[] a = ActionLibrary.DynamicActions.getLastUsedFilesOpenActions();
            if (a == null || a.length == 0) {
                return;
            }
            removeItems(firstFileIndex, lastFileIndex);
            MenuCreator.addAll(this, firstFileIndex, MenuCreator.createMenuEntries(a, false));
            lastFileIndex = firstFileIndex + a.length - 1;
        }
    }

    /** Das Einfügen-Menu */
    public static class InsertMenu extends DynamicMenu {

        /** Name dieses Menus */
        public static final String title = getResString("insert");

        public InsertMenu() {
            super(title);
        }

        @Override
        protected void updateItems() {
            if (Static.getSelectedDoc() == null) {
                return;
            }

            removeAll();
            int layerID = Static.getSelectedGDCollection().getActiveLayer();
            List<Action> menuActions = new ArrayList<>();
            Iterable<StaticAction> createElementActions = null;
            switch (layerID) {
            case DOMAIN_LAYER:
                createElementActions = ActionLibrary.CreateElementActions.DOMAIN_LAYER_CREATABLE_NODES_ACTIONS;
                break;
            case LOGICAL_LAYER:
                createElementActions = ActionLibrary.CreateElementActions.LOGICAL_TOOL_LAYER_CREATABLE_NODES_ACTIONS;
                break;
            case PHYSICAL_LAYER:
                createElementActions = ActionLibrary.CreateElementActions.PHYSICAL_TOOL_LAYER_CREATABLE_NODES_ACTIONS;
                break;
            }
            for (StaticAction action : createElementActions) {
                String arguments = action.getArguments();
                Class<? extends ModelElement> elementClass = ModelConstants.getClassForName(arguments);
                if (ModelConstants.isEditable(elementClass)) {
                    menuActions.add(action);
                }
            }
            Component[] menuEntries = MenuCreator.createMenuEntries(menuActions, true);
            MenuCreator.addAll(this, menuEntries);
        }
    }

    /** Das Layout-Menu */
    private static class LayoutMenu extends DynamicMenu {

        /** Name dieses Menus */
        public static final String title = getResString("layoutMenu");

        /** Item für das Teilmodell-Layout */
        private final JMenuItem globalLayout = new JMenuItem(ActionLibrary.LayoutActions.ACTION_OPEN_GLOBAL_LAYOUT_EDITOR);

        /** Menu für das Ebenen Layout */
        private final JMenu layerLayoutMenu = LayoutSubMenus.LAYER_LAYOUT_MENU;

        /** Menu für das Element Layout */
        private final JMenu elementLayoutMenu = LayoutSubMenus.ELEMENT_LAYOUT_MENU;

        /** Das Level-Menu */
        private final JMenu elementLevelMenu = LayoutSubMenus.ELEMENT_LEVEL_MENU;

        /** Das Elemente Ausrichtungs Menu */
        private final JMenu elementAlignmentMenu = LayoutSubMenus.ELEMENT_ALIGNMENT_MENU;

        public LayoutMenu() {
            super(title);
        }

        @Override
        protected void updateItems() {
            removeAll();
            GraphDocument doc = Static.getSelectedDoc();
            if (doc == null) {
                return;
            }
            add(globalLayout);
            if (doc.getActiveLayer() != null) {
                add(new JSeparator());
            }
            add(layerLayoutMenu);
            if (doc.isSelectedOnlyRealNodes()) {
                add(new JSeparator());
                add(elementLayoutMenu);
                add(new JSeparator());
                add(elementLevelMenu);
                if (doc.isSelectedOnlyNodes()) {
                    add(elementAlignmentMenu);
                }
            }
        }
    }

    static int tt = 0;

    /** Das Fenster-Menu */
    private static class WindowMenu extends DynamicMenu {

        /** Name dieses Menus */
        public static final String title = getResString("windowMenu");

        /** Index, des ersten Fensters im Menu */
        private final int firstFrameIndex;

        private final int scrollItemCount = 30;

        public WindowMenu() {
            super(title);
            MenuCreator.addAll(this, MenuCreator.createMenuEntries(true, ActionLibrary.WindowActions.ACTION_GRAPH_FRAMES_PARALLEL_ARRAGEMENT, ActionLibrary.WindowActions.ACTION_GRAPH_FRAMES_OVERLAPPING_ARRAGEMENT, new JSeparator()));
            firstFrameIndex = getItemCount();
            //firstFrameIndex + 2 weil der jeweils aktive Frame und der darauffolgende JSeparator nicht mitgescrollt werden sollen
            MenuScroller.setScrollerFor(this, scrollItemCount, 125, firstFrameIndex + 2, 0);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void updateItems() {
            //alle Items von Fenstern entfernen
            removeItems(firstFrameIndex, getItemCount() - 1);
            Action[] a = ActionLibrary.DynamicActions.getSelectInternalFrameActions();
            if (a == null || a.length == 0) {
                return;
            }
            //Die Fenster alphabetisch sortieren (toString liefert nicht den Anzeigenamen)
            //an Index 0 steht der aktive Frame, der nicht mit einsortiert werden darf -> Start ab Index 1
            NamedObjectContainer<Action>[] noc = new NamedObjectContainer[a.length - 1];
            for (int i = 1; i < a.length; i++) {
                noc[i - 1] = new NamedObjectContainer<>(a[i], a[i].getValue(Action.NAME).toString());
            }
            Alphabetical.sort(noc);
            for (int i = 1; i < a.length; i++) {
                a[i] = noc[i - 1].getObject();
            }
            MenuCreator.addAll(this, firstFrameIndex, MenuCreator.createMenuEntries(a, false));
            add(new JSeparator(), firstFrameIndex + 1);// Trenner zwischen aktivem Frame und aktivierbaren Frames
        }
    }

    public static class HelpMenu extends JMenu {

        /** Name dieses Menus */
        public static final String title = Tool3lgmConstants.getResString("help");

        public HelpMenu() {
            super(title);

            MenuCreator.addAll(this,
                    MenuCreator.createMenuEntries(false, ActionLibrary.HelpActions.ACTION_OPEN_HELP_DIALOG, ActionLibrary.HelpActions.ACTION_ACTIVATE_DIRECT_HELP, new JSeparator(),
                            ActionLibrary.HelpActions.ACTION_SHOW_INFORMATION_SYSTEM_EVALUATION_TUTORIAL, new JSeparator(), ActionLibrary.HelpActions.ACTION_SHOW_ONLINE_HELP, ActionLibrary.HelpActions.ACTION_SHOW_3LGM_WEB_SITE, new JSeparator(),
                            ActionLibrary.HelpActions.ACTION_OPEN_EXAMPLE_MODEL_FILE, ActionLibrary.HelpActions.ACTION_OPEN_FILE_CHOSSER_IN_MODEL_LIBRARY, new JSeparator(), ActionLibrary.HelpActions.ACTION_OPEN_ABOUT_DIALOG,
                            new JSeparator()/* , ActionLibrary.HelpActions.ACTION_IMPORT_LICENSE_FILE */));
        }
    }

    /* *************************************************** Ende: Unterklassen ******************************** */

}
