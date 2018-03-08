package de.imise.tool3lgm.gui.menu;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.DOMAIN_LAYER;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.LOGICAL_LAYER;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.PHYSICAL_LAYER;
import static de.imise.util.swing.menu.MenuCreator.createCheckBoxItem;

import java.awt.Component;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
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
import de.imise.tool3lgm.event.ActionLibrary.LayoutActions.ElementAlignment;
import de.imise.tool3lgm.event.ActionLibrary.LayoutActions.ElementLayout;
import de.imise.tool3lgm.event.ActionLibrary.LayoutActions.ElementLayout.TextAlignment;
import de.imise.tool3lgm.event.ActionLibrary.LayoutActions.LayerLayout;
import de.imise.tool3lgm.event.ActionLibrary.LayoutActions.Level;
import de.imise.tool3lgm.event.ActionLibrary.OptionsActions;
import de.imise.tool3lgm.event.ActionLibrary.OptionsActions.Analysis;
import de.imise.tool3lgm.event.ActionLibrary.OptionsActions.Gerneral;
import de.imise.tool3lgm.event.ActionLibrary.OptionsActions.Graphics;
import de.imise.tool3lgm.event.ActionLibrary.OptionsActions.Locale;
import de.imise.tool3lgm.event.ActionLibrary.OptionsActions.ModelBrowser;
import de.imise.tool3lgm.event.ActionLibrary.OptionsActions.PartOf;
import de.imise.tool3lgm.event.ActionLibrary.SubmodelActions;
import de.imise.tool3lgm.event.ActionLibrary.ViewActions;
import de.imise.tool3lgm.event.ActionLibrary.ViewActions.ToolbarActions;
import de.imise.tool3lgm.graphtools.dialog.ElementAlignmentDialog;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
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
    public static final JMenu EDIT_MENU = createMenu("edit", EditActions.ACTION_UNDO, EditActions.ACTION_REDO, new JSeparator(), EditActions.ACTION_SEARCH, new JSeparator(), EditActions.SELECT_ALL, new JSeparator(), EditActions.MODEL_ACTION_COPY,
            EditActions.MODEL_ACTION_CUT, EditActions.MODEL_ACTION_PASTE, new JSeparator(), EditActions.MODEL_ACTION_DELETE_FROM_SUBMODEL, EditActions.MODEL_ACTION_REMOVE_FROM_MODEL);

    /** Das Ansicht-Menu */
    public static final JMenu VIEW_MENU = createMenu("viewMenu", ViewSubMenus.TOOLBAR_MENU, ViewActions.OPTION_MODEL_BROWSER_SHOW, new JSeparator(), ViewActions.ACTION_GRAPH_SWITCH_ONE_LAYER_AND_THREE_LAYER_PERSPECTIVE,
            ViewActions.ACTION_ACTIVATE_DOMAIN_LAYER, ViewActions.ACTION_ACTIVATE_LOGICAL_TOOL_LAYER, ViewActions.ACTION_ACTIVATE_PHYSICAL_TOOL_LAYER, ViewActions.ACTION_OPEN_GRAPH_VIEW_SETTINGS_DIALOG, new JSeparator(),
            ViewActions.ACTION_OPEN_MATRIX_VIEW);

    /** Das Einfügen-Menu */
    public static final JMenu INSERT_MENU = new InsertMenu();

    /** Das Format-Menu */
    public static final JMenu LAYOUT_MENU = new LayoutMenu();

    /** Das Teilmodell-Menu */
    public static final JMenu SUBMODEL_MENU = createMenu("submodels", SubmodelActions.NEW, SubmodelActions.REMOVE, new JSeparator(), SubmodelActions.RENAME);

    /** Das XMLAnalyse-Menu */
    public static final JMenu ANALYSIS_MENU = createMenu("analysis", AnalysisActions.ACTION_ANALYSIS_OPEN_REPOSITORY, AnalysisActions.ACTION_ANALYSIS_OPEN_EDITOR, AnalysisActions.ACTION_ANALYSIS_RESET_RESULT,
            Analysis.OPTION_CREATE_NEW_SUBMODEL_FOR_ANALYSIS_RESULT, new JSeparator(), AnalysisActions.ACTION_ANALYSIS_REDUNDANCY, ModelConstants.getAnalysisDefinition().getAnalysisActions(), Analysis.OPTIONS_SIMPLE_REDUNDANCY_ANALYSIS);

    /** Das Optionen-Menu */
    public static final JMenu OPTIONS_MENU = createMenu("options", OptionsSubMenus.GENERAL_OPTIONS_MENU, OptionsSubMenus.BROWSER_OPTIONS_MENU, OptionsSubMenus.GRAPHICS_OPTIONS_MENU, OptionsActions.OPEN_RMI_SETTINGS, OptionsSubMenus.LOCALE_MENU);

    /** Das Extras-Menu */
    public static final JMenu EXTRAS_MENU = createMenu("extras", ExtrasActions.ACTION_OPEN_USERFIELD_DEFINITION_DIALOG, ExtrasActions.ACTION_OPEN_USERFIELD_VALUE_EDITOR_DIALOG, createCheckBoxItem(Analysis.ACTIVATE_CALCULATION), new JSeparator(),
            ModelConstants.getExtrasActions(false), new JSeparator(), AnalysisActions.OPTION_CHECK_CONSISTENCY, ExtrasSubMenus.PLUGIN_MENU);

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
        public static final JMenu TOOLBAR_MENU = createMenu("toolbarMenu", ToolbarActions.OPTION_SHOW_PAINTING_TOOLBAR, ToolbarActions.OPTION_SHOW_STANDARD_TOOLBAR);
    }

    /** Sammlung der Unter-Menus des Format-Menus */
    public static class LayoutSubMenus {

        /** Das Element-Layout-Menu */
        public static final JMenu ELEMENT_LAYOUT_MENU = createMenu("elementLayoutMenu", ElementLayout.MODEL_ACTION_SET_ELEMENT_FONT, ElementLayout.MODEL_ACTION_SET_ELEMENT_COLOR, createMenu( // Transparenz-Menu
                "elementTransparencyMenu", ElementLayout.Transparency.MODEL_ACTION_SET_ELEMENT_TRANSPARENCY_NONE, ElementLayout.Transparency.MODEL_ACTION_SET_ELEMENT_TRANSPARENCY_HALF, ElementLayout.Transparency.MODEL_ACTION_SET_ELEMENT_TRANSPARENCY_FULL),
                createMenu( // Auf Standard setzen Menu
                        "elementSetToStandardMenu", ElementLayout.MODEL_ACTION_SET_ELEMENT_DEFAULT_FONT, ElementLayout.MODEL_ACTION_SET_ELEMENT_DEFAULT_COLOR, ElementLayout.MODEL_ACTION_SET_ELEMENT_DEFAULT_TRANSPARENCY,
                        ElementLayout.MODEL_ACTION_SET_ELEMENT_DEFAULT_FULL_LAYOUT),
                new JSeparator(),
                createMenu( // Icon-Menu
                        "icon", ElementLayout.Icon.MODEL_ACTION_SET_ELEMENT_ICON_NONE, ElementLayout.Icon.ACTION_CHOOSE_ELEMENT_ICON),
                new JSeparator(),
                createMenu( // Textausrichtungs-Menu (horizontal)
                        "textAlignmentMenu", TextAlignment.Horizontal.MODEL_ACTION_SET_ELEMENT_LABEL_HALIGN_LEFT, TextAlignment.Horizontal.MODEL_ACTION_SET_ELEMENT_LABEL_HALIGN_CENTER, TextAlignment.Horizontal.MODEL_ACTION_SET_ELEMENT_LABEL_HALIGN_RIGHT,
                        new JSeparator(), TextAlignment.Vertical.MODEL_ACTION_SET_ELEMENT_LABEL_VALIGN_TOP, TextAlignment.Vertical.MODEL_ACTION_SET_ELEMENT_LABEL_VALIGN_CENTER, TextAlignment.Vertical.MODEL_ACTION_SET_ELEMENT_LABEL_VALIGN_BOTTOM));

        /** Das Ebenen-Layout-Menu */
        public static final JMenu LAYER_LAYOUT_MENU = createMenu("layerLayoutMenu", LayerLayout.MODEL_ACTION_SET_LAYER_DEFAULT_COLOR_AND_TRANSPARENCY, LayerLayout.MODEL_ACTION_SET_LAYER_COLOR, createMenu( // Transparenz-Menu
                "layerTransparencyMenu", LayerLayout.Transparency.MODEL_ACTION_SET_LAYER_TRANSPARENCY_NONE, LayerLayout.Transparency.MODEL_ACTION_SET_LAYER_TRANSPARENCY_HALF, LayerLayout.Transparency.MODEL_ACTION_SET_LAYER_TRANSPARENCY_FULL));

        /** Das Level-Menu */
        public static final JMenu ELEMENT_LEVEL_MENU = createMenu(// Elementreihenfolge
                "levelMenu", Level.MODEL_ACTION_MOVE_ORDER_TO_FIRST_POSITION, Level.MODEL_ACTION_MOVE_ORDER_ONE_POSITION_UP, Level.MODEL_ACTION_MOVE_ORDER_ONE_POSITION_DOWN, Level.MODEL_ACTION_MOVE_ORDER_TO_LAST_POSITION);

        /** Das Elemente-Ausrichtung-Menu */
        public static final JMenu ELEMENT_ALIGNMENT_MENU = createMenu( // Elemente ausrichten
                "elementAlignmentMenu", ElementAlignment.Horizontal.MODEL_ACTION_SET_ELEMENT_ALIGNMENT_HORIZONTAL_LEFT, ElementAlignment.Horizontal.MODEL_ACTION_SET_ELEMENT_ALIGNMENT_HORIZONTAL_CENTER,
                ElementAlignment.Horizontal.MODEL_ACTION_SET_ELEMENT_ALIGNMENT_HORIZONTAL_RIGHT, new JSeparator(), ElementAlignment.Vertical.MODEL_ACTION_SET_ELEMENT_ALIGNMENT_VERTICAL_TOP,
                ElementAlignment.Vertical.MODEL_ACTION_SET_ELEMENT_ALIGNMENT_VERTICAL_CENTER, ElementAlignment.Vertical.MODEL_ACTION_SET_ELEMENT_ALIGNMENT_VERTICAL_BOTTOM, new JSeparator(),
                ElementAlignment.Size.MODEL_ACTION_SET_ELEMENT_ALIGNMENT_SIZE_WIDTH, ElementAlignment.Size.MODEL_ACTION_SET_ELEMENT_ALIGNMENT_SIZE_HEIGTH, ElementAlignment.Size.MODEL_ACTION_SET_ELEMENT_ALIGNMENT_SIZE_WIDTH_AND_HEIGTH, new JSeparator(),
                ElementAlignmentDialog.getElementAlignmentAction());
    }

    /** Sammlung der Unter-Menus des Optionen-Menus */
    static class OptionsSubMenus {

        /** Menu für allgemeine Optionen */
        public static final JMenu GENERAL_OPTIONS_MENU = createMenu("general", createCheckBoxItem(Gerneral.SHOW_REMOVE_WARNING), createCheckBoxItem(PartOf.OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARENTS),
                createCheckBoxItem(PartOf.OPTION_SHOW_PART_OF_HIERARCHY));

        /** Das Browser-Optionen-Menu */
        public static final JMenu BROWSER_OPTIONS_MENU = createMenu("browserOptionsMenu", createCheckBoxItem(ModelBrowser.OPTION_ENABLE_SUBMODEL_BROWSER), createCheckBoxItem(ModelBrowser.OPTION_SHOW_MODELS_IN_SEPARATE_BROWSER),
                //createCheckBoxItem(ActionLibrary.ViewActions.SHOW_SUBMODELS_IN_BROWSER_SIDE_BY_SIDE),
                new JSeparator(), createCheckBoxItem(ModelBrowser.OPTION_SHOW_USER_DEFINED_PROPERTIES_IN_MODEL_BROWSER));

        /** Das Grafik-Optionen-Menu */
        public static final JMenu GRAPHICS_OPTIONS_MENU = createMenu("graphicOptionsMenu", Graphics.OPTION_USE_RASTER, Graphics.OPTION_SHOW_RASTER, Graphics.OPTION_PAINT_EDGES_ONLY_FOR_SELECTED_ELEMENTS,
                createCheckBoxItem(Graphics.OPTION_SHOW_LINKED_WITH_SUBMODEL_SYMBOLS), Graphics.HIDE_UNHIDE_UNASSOCIATED, Graphics.OPTION_ASSIGN_CONFIGURATION_COLORS, PartOf.OPTION_GRAPH_MOVE_SUBELEMENTS, PartOf.SIGNIFY_COARSEMENT, Graphics.ANALYSIS_COLOR,
                Graphics.RENDERING_OPTIONS
        //createCheckBoxItem(Graphics.TOOLTIPS)
        );

        /** Das Sprach-Menu */
        public static final JMenu LOCALE_MENU = createLocaleMenu();

        /** Methode erzeugt das Sprachen-Menu */
        private static final DynamicMenu createLocaleMenu() {
            Action[] actions = Locale.CHANGE_LOCALE_ACTIONS;
            DynamicMenu localeMenu = new DynamicMenu(getResString("localeOptionsMenu")) {
                @Override
                protected void updateItems() {
                }
            };
            for (Action action : actions) {
                localeMenu.add(MenuCreator.createRadioButtonMenuItem(action));
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

        /** Einträge der fachlichen Ebene */
        public static final Component[] MENU_ENTRIES_DOMAIN = MenuCreator.createMenuEntries(ActionLibrary.CreateElementActions.DOMAIN_LAYER_CREATEABLE_NODES_ACTIONS, true);

        /** Einträge der logischen Ebene */
        public static final Component[] MENU_ENTRIES_LOGICAL = MenuCreator.createMenuEntries(ActionLibrary.CreateElementActions.LOGICAL_TOOL_LAYER_CREATEABLE_NODES_ACTIONS, true);

        /** Einträge der physichen bene */
        public static final Component[] MENU_ENTRIES_PHYSICAL = MenuCreator.createMenuEntries(ActionLibrary.CreateElementActions.PHYSICAL_TOOL_LAYER_CREATEABLE_NODES_ACTIONS, true);

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
            switch (layerID) {
            case DOMAIN_LAYER:
                MenuCreator.addAll(this, MENU_ENTRIES_DOMAIN);
                break;
            case LOGICAL_LAYER:
                MenuCreator.addAll(this, MENU_ENTRIES_LOGICAL);
                break;
            case PHYSICAL_LAYER:
                MenuCreator.addAll(this, MENU_ENTRIES_PHYSICAL);
                break;
            }
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
            MenuCreator.addAll(this, MenuCreator.createMenuEntries(true, ActionLibrary.WindowActions.MODELS_PARALLEL_ARRANGEMENT, ActionLibrary.WindowActions.MODELS_OVERLAPPING_ARRANGEMENT, new JSeparator()));
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
