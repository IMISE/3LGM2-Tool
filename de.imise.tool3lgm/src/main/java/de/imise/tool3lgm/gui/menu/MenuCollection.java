package de.imise.tool3lgm.gui.menu;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import de.imise.tool3lgm.Help;
import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.event.ActionLibrary;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.dialog.ElementAlignmentDialog;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.plugin.B1ExportPlugin;
import de.imise.tool3lgm.plugin.ExportPdvb4AwbPlugin;
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

    /** Das Datei-Menu */
    public static final JMenu FILE_MENU = new FileMenu();

    /** Das Bearbeiten-Menu */
    public static final JMenu EDIT_MENU = createMenu(Tool3lgmConstants.getResString("edit"),

    ActionLibrary.EditActions.UNDO, ActionLibrary.EditActions.REDO, new JSeparator(), ActionLibrary.EditActions.SEARCH, new JSeparator(), ActionLibrary.EditActions.SELECT_ALL, new JSeparator(), ActionLibrary.EditActions.COPY,
            ActionLibrary.EditActions.CUT, ActionLibrary.EditActions.PASTE, ActionLibrary.EditActions.CLEAR_CLIPBOARD, new JSeparator(), ActionLibrary.EditActions.REMOVE_FROM_SUBMODEL, ActionLibrary.EditActions.REMOVE_FROM_MODEL);

    /** Das Ansicht-Menu */
    public static final JMenu VIEW_MENU = new ViewMenu();

    /** Das Einfügen-Menu */
    public static final JMenu INSERT_MENU = new InsertMenu();

    /** Das Format-Menu */
    public static final JMenu LAYOUT_MENU = new LayoutMenu();

    /** Das Teilmodell-Menu */
    public static final JMenu SUBMODEL_MENU = createMenu(

    Tool3lgmConstants.getResString("submodels"), ActionLibrary.SubmodelActions.NEW, ActionLibrary.SubmodelActions.REMOVE, new JSeparator(), ActionLibrary.SubmodelActions.RENAME);

    /** Das XMLAnalyse-Menu */
    public static final JMenu ANALYSIS_MENU = createMenu(Tool3lgmConstants.getResString("analysis"),

    ActionLibrary.AnalysisActions.OPEN_REPOSITORY, ActionLibrary.AnalysisActions.OPEN_EDITOR, ActionLibrary.AnalysisActions.RESET_RESULT, createCheckBoxItem(ActionLibrary.OptionsActions.Analysis.NEW_SUBMODEL), new JSeparator(),
            ActionLibrary.AnalysisActions.ACTIVATE_REDUNDANCY_ANALYSIS, ActionLibrary.AnalysisActions.ACTIVATE_DATA_AVAILABILITY, createCheckBoxItem(ActionLibrary.OptionsActions.Analysis.CONFIGURATIONAL_REDUNDANCY),
            createCheckBoxItem(ActionLibrary.OptionsActions.Analysis.DATA_REDUNDANCY)

    );

    /** Das Optionen-Menu */
    public static final JMenu OPTIONS_MENU = createMenu(Tool3lgmConstants.getResString("options"),

    OptionsSubMenus.GENERAL_OPTIONS_MENU, OptionsSubMenus.BROWSER_OPTIONS_MENU, OptionsSubMenus.GRAPHICS_OPTIONS_MENU, ActionLibrary.OptionsActions.OPEN_RMI_SETTINGS, OptionsSubMenus.LOCALE_MENU);

    /** Das Extras-Menu */
    public static final JMenu EXTRAS_MENU = createMenu(Tool3lgmConstants.getResString("extras"),

    ActionLibrary.ExtrasActions.USERFIELD_DEFINITION_DIALOG, ActionLibrary.ExtrasActions.ATTRIBUTE_EDITOR, createCheckBoxItem(ActionLibrary.OptionsActions.Analysis.ACTIVATE_CALCULATION), new JSeparator(),
            ActionLibrary.ExtrasActions.AUTOMATIC_ETMT_ASSIGNMENT, new JSeparator(), createCheckBoxItem(ActionLibrary.AnalysisActions.ACTIVATE_CONSISTENCY_CHECK), ExtrasSubMenus.PLUGIN_MENU);

    /** Das Fenster-Menu */
    public static final JMenu WINDOW_MENU = new WindowMenu();

    /** Das Hilfe-Menu */
    public static final JMenu HELP_MENU = new HelpMenu();

    /** Erzeugt {@link JMenu} mit spezifizierten Titel und Einträgen */
    private static JMenu createMenu(final String title, final Object... entries) {
        return MenuCreator.createMenu(title, entries, true);
    }

    /** Erzeugt ein {@link JCheckBoxMenuItem} mit der übergebenen {@link Action} */
    private static JCheckBoxMenuItem createCheckBoxItem(final Action a) {
        return MenuCreator.createCheckBoxMenuItem(a);
    }

    /** Sammlung der Unter-Menus des Datei-Menus */
    static class FileSubMenus {

        /** Das Import-Menu */
        public static final JMenu IMPORT_MENU = createMenu(Tool3lgmConstants.getResString("import"),

        ActionLibrary.FileActions.ImportActions.IMPORT_SUBMODEL, ActionLibrary.FileActions.ImportActions.IMPORT_MODEL, new JSeparator(), ActionLibrary.FileActions.ImportActions.IMPORT_DATA);

        /** Das Export-Menu */
        public static final JMenu EXPORT_MENU = createMenu(Tool3lgmConstants.getResString("export"),

        ActionLibrary.FileActions.ExportActions.EXPORT_GRAPHIC, new JSeparator(), ActionLibrary.FileActions.ExportActions.EXPORT_XSLT, ActionLibrary.FileActions.ExportActions.EXPORT_SUBMODEL, new JSeparator(),
                ActionLibrary.FileActions.ExportActions.EXPORT_WEB, new JSeparator(), ActionLibrary.FileActions.ExportActions.EXPORT_DATA);
    }

    /** Sammlung der Unter-Menus des Ansicht-Menus */
    static class ViewSubMenus {

        /** Das Symbolleisten-Menu */
        public static final JMenu TOOLBAR_MENU = createMenu(Tool3lgmConstants.getResString("toolbarMenu"),

        createCheckBoxItem(ActionLibrary.ViewActions.ToolbarActions.SWITCH_SHOW_PAINTING_BAR), createCheckBoxItem(ActionLibrary.ViewActions.ToolbarActions.SWITCH_SHOW_STANDARD_BAR));
    }

    /** Sammlung der Unter-Menus des Format-Menus */
    public static class LayoutSubMenus {

        /** Das Element-Layout-Menu */
        public static final JMenu ELEMENT_LAYOUT_MENU = createMenu(
                Tool3lgmConstants.getResString("elementLayoutMenu"),

                ActionLibrary.LayoutActions.ElementLayout.CHOOSE_FONT,
                ActionLibrary.LayoutActions.ElementLayout.CHANGE_COLOR,
                createMenu(
                        // Transparenz-Menu
                        Tool3lgmConstants.getResString("elementTransparencyMenu"), ActionLibrary.LayoutActions.ElementLayout.Transparency.NO, ActionLibrary.LayoutActions.ElementLayout.Transparency.SEMI,
                        ActionLibrary.LayoutActions.ElementLayout.Transparency.FULL),
                createMenu(
                        // Auf Standard setzen Menu
                        Tool3lgmConstants.getResString("elementSetToStandardMenu"), ActionLibrary.LayoutActions.ElementLayout.RESET_FONT, ActionLibrary.LayoutActions.ElementLayout.RESET_COLOR, ActionLibrary.LayoutActions.ElementLayout.RESET_TRANSPARENCY,
                        ActionLibrary.LayoutActions.ElementLayout.RESET_ALL),
                new JSeparator(),
                createMenu( // Icon-Menu
                        Tool3lgmConstants.getResString("icon"),

                        ActionLibrary.LayoutActions.ElementLayout.Icon.NO_ICON, ActionLibrary.LayoutActions.ElementLayout.Icon.CHOOSE_ICON),
                new JSeparator(),
                createMenu(
                        // Textausrichtungs-Menu (horizontal)
                        Tool3lgmConstants.getResString("textAlignmentMenu"),

                        ActionLibrary.LayoutActions.ElementLayout.TextAlignment.Horizontal.LEFT, ActionLibrary.LayoutActions.ElementLayout.TextAlignment.Horizontal.CENTER, ActionLibrary.LayoutActions.ElementLayout.TextAlignment.Horizontal.RIGHT,
                        new JSeparator(), ActionLibrary.LayoutActions.ElementLayout.TextAlignment.Vertical.TOP, ActionLibrary.LayoutActions.ElementLayout.TextAlignment.Vertical.CENTER,
                        ActionLibrary.LayoutActions.ElementLayout.TextAlignment.Vertical.BOTTOM));

        /** Das Ebenen-Layout-Menu */
        public static final JMenu LAYER_LAYOUT_MENU = createMenu(Tool3lgmConstants.getResString("layerLayoutMenu"),

        ActionLibrary.LayoutActions.LayerLayout.RESET, ActionLibrary.LayoutActions.LayerLayout.CHANGE_COLOR, createMenu( // Transparenz-Menu
                Tool3lgmConstants.getResString("layerTransparencyMenu"),

                ActionLibrary.LayoutActions.LayerLayout.Transparency.NO, ActionLibrary.LayoutActions.LayerLayout.Transparency.SEMI, ActionLibrary.LayoutActions.LayerLayout.Transparency.FULL));

        /** Das Level-Menu */
        public static final JMenu ELEMENT_LEVEL_MENU = createMenu(Tool3lgmConstants.getResString("levelMenu"),

        ActionLibrary.LayoutActions.Level.HIGHEST, ActionLibrary.LayoutActions.Level.INCREASE, ActionLibrary.LayoutActions.Level.DECREASE, ActionLibrary.LayoutActions.Level.LOWEST);

        /** Das Elemente-Ausrichtung-Menu */
        public static final JMenu ELEMENT_ALIGNMENT_MENU = createMenu(Tool3lgmConstants.getResString("elementAlignmentMenu"), ActionLibrary.LayoutActions.ElementAlignment.Horizontal.LEFT, ActionLibrary.LayoutActions.ElementAlignment.Horizontal.CENTER,
                ActionLibrary.LayoutActions.ElementAlignment.Horizontal.RIGHT, new JSeparator(), ActionLibrary.LayoutActions.ElementAlignment.Vertical.TOP, ActionLibrary.LayoutActions.ElementAlignment.Vertical.CENTER,
                ActionLibrary.LayoutActions.ElementAlignment.Vertical.BOTTOM, new JSeparator(), ActionLibrary.LayoutActions.ElementAlignment.Size.WIDTH, ActionLibrary.LayoutActions.ElementAlignment.Size.HEIGTH,
                ActionLibrary.LayoutActions.ElementAlignment.Size.WIDTH_AND_HEIGTH, new JSeparator(), ElementAlignmentDialog.getElementAlignmentAction());
    }

    /** Sammlung der Unter-Menus des Optionen-Menus */
    static class OptionsSubMenus {

        /** Menu für allgemeine Optionen */
        public static final JMenu GENERAL_OPTIONS_MENU = createMenu(Tool3lgmConstants.getResString("general"), createCheckBoxItem(ActionLibrary.OptionsActions.Gerneral.SHOW_REMOVE_WARNING),
                createCheckBoxItem(ActionLibrary.OptionsActions.PartOf.CONSIDER_PARENTS), createCheckBoxItem(ActionLibrary.OptionsActions.PartOf.HIERARCHICAL));

        /** Das Browser-Optionen-Menu */
        public static final JMenu BROWSER_OPTIONS_MENU = createMenu(Tool3lgmConstants.getResString("browserOptionsMenu"),

        createCheckBoxItem(ActionLibrary.OptionsActions.ModelBrowser.SUBMODEL_SPECIFIC), createCheckBoxItem(ActionLibrary.OptionsActions.ModelBrowser.SHOW_MULTIPLE_BROSERS),
        //				createCheckBoxItem(ActionLibrary.ViewActions.SHOW_SUBMODELS_IN_BROWSER_SIDE_BY_SIDE),
                new JSeparator(), createCheckBoxItem(ActionLibrary.OptionsActions.ModelBrowser.USERDEFINED_PROPERTIES));

        /** Das Grafik-Optionen-Menu */
        public static final JMenu GRAPHICS_OPTIONS_MENU = createMenu(Tool3lgmConstants.getResString("graphicOptionsMenu"),

        createCheckBoxItem(ActionLibrary.OptionsActions.Graphics.USE_RASTER), createCheckBoxItem(ActionLibrary.OptionsActions.Graphics.SHOW_RASTER),
                createCheckBoxItem(ActionLibrary.OptionsActions.Graphics.PAINT_EDGES_ONLY_FOR_SELECTED_ELEMENTS),
                createCheckBoxItem(ActionLibrary.OptionsActions.Graphics.SIGNIFY_LINKED_ELEMENTS),
                //				createCheckBoxItem(ActionLibrary.OptionsActions.Graphics.UNUSED_INTERFACES),
                ActionLibrary.OptionsActions.Graphics.HIDE_UNASSOCIATED_INTERFACES, ActionLibrary.OptionsActions.Graphics.UNHIDE_ALL_INTERFACES, createCheckBoxItem(ActionLibrary.OptionsActions.Graphics.AUTOMATIC_COLORING),
                createCheckBoxItem(ActionLibrary.OptionsActions.PartOf.AUTO_MOVE_CHILDREN), createCheckBoxItem(ActionLibrary.OptionsActions.PartOf.SIGNIFY_COARSEMENT), ActionLibrary.OptionsActions.Graphics.ANALYSIS_COLOR,
                ActionLibrary.OptionsActions.Graphics.RENDERING_OPTIONS
        //createCheckBoxItem(ActionLibrary.OptionsActions.Graphics.TOOLTIPS)
        );

        /** Das Sprach-Menu */
        public static final JMenu LOCALE_MENU = createLocaleMenu();

        /** Methode erzeugt das Sprachen-Menu */
        private static final DynamicMenu createLocaleMenu() {
            Action[] actions = ActionLibrary.OptionsActions.Locale.CHANGE_LOCALE_ACTIONS;
            DynamicMenu localeMenu = new DynamicMenu(Tool3lgmConstants.getResString("localeOptionsMenu")) {
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

        public static final JMenu PLUGIN_MENU = createMenu(Tool3lgmConstants.getResString("plugin"), new B1ExportPlugin().getAction(), new ExportPdvb4AwbPlugin().getAction());

    }

    /* *************************************************** Start: Unterklassen ******************************** */

    /** Das Datei-Menu */
    private static class FileMenu extends DynamicMenu {

        /** Name dieses Menus */
        public static final String title = Tool3lgmConstants.getResString("file");

        /** Index, an dem die erste Datei im Menu steht */
        private static final int firstFileIndex = 11;

        /** Index, an dem die letzte Datei im Menu steht */
        private int lastFileIndex = -1;

        public FileMenu() {
            super(title);
            MenuCreator.addAll(this, MenuCreator.createMenuEntries(true, ActionLibrary.FileActions.ACTION_NEW_MODEL, ActionLibrary.FileActions.OPEN, ActionLibrary.FileActions.SAVE, ActionLibrary.FileActions.SAVEAS, ActionLibrary.FileActions.CLOSE,
                    new JSeparator(), ActionLibrary.FileActions.DESCRIPTION, new JSeparator(), FileSubMenus.IMPORT_MENU, FileSubMenus.EXPORT_MENU, new JSeparator(),
                    //hier werden später die zuletzt geladenen Modelle angezeigt 
                    new JSeparator(), ActionLibrary.FileActions.EXIT));
        }

        @Override
        protected void updateItems() {

            Action[] a = ActionLibrary.DynamicActions.getLastUsedFilesOpenActions();

            if (a == null || a.length == 0) {
                return;
            }

            removeItems(firstFileIndex, lastFileIndex);

            MenuCreator.addAll(this, MenuCreator.createMenuEntries(a, false), firstFileIndex);
            lastFileIndex = firstFileIndex + a.length - 1;
        }
    }

    /** Das Datei-Menu */
    private static class ViewMenu extends DynamicMenu {

        /** Name dieses Menus */
        public static final String title = Tool3lgmConstants.getResString("viewMenu");

        /** Item für das Aktivieren der Ein-Ebenen-Ansicht */
        private static final Component oneLayerPerspective = MenuCreator.createMenuEntry(ActionLibrary.ViewActions.ONE_LAYER_PERSPECTIVE);

        /** Item für das Aktivieren der Drei-Ebenen-Ansicht */
        private static final Component threeLayerPerspective = MenuCreator.createMenuEntry(ActionLibrary.ViewActions.THREE_LAYER_PERSPECTIVE);

        /** Index der beiden Items im Menu */
        private static final int switchPerspectiveIndex = 4;

        public ViewMenu() {
            super(title);
            MenuCreator.addAll(this, MenuCreator.createMenuEntries(true, ViewSubMenus.TOOLBAR_MENU, createCheckBoxItem(ActionLibrary.ViewActions.SWITCH_SHOW_BROWSER), new JSeparator(), oneLayerPerspective, ActionLibrary.ViewActions.SHOW_DOMAIN_LAYER,
                    ActionLibrary.ViewActions.SHOW_LOGICAL_TOOL_LAYER, ActionLibrary.ViewActions.SHOW_PHYSICAL_TOOL_LAYER, ActionLibrary.ViewActions.OPEN_LAYER_SETTINGS, new JSeparator(), ActionLibrary.ViewActions.OPEN_MATRIX));
        }

        @Override
        protected void updateItems() {
            remove(oneLayerPerspective);
            remove(threeLayerPerspective);

            if (ActionLibrary.ViewActions.ONE_LAYER_PERSPECTIVE.isEnabled()) {
                add(oneLayerPerspective, switchPerspectiveIndex);
            } else if (ActionLibrary.ViewActions.THREE_LAYER_PERSPECTIVE.isEnabled()) {
                add(threeLayerPerspective, switchPerspectiveIndex);
            }
        }
    }

    /** Das Einfügen-Menu */
    public static class InsertMenu extends DynamicMenu {

        /** Name dieses Menus */
        public static final String title = Tool3lgmConstants.getResString("insert");

        /** Einträge der fachlichen Ebene */
        public static final Component[] MENU_ENTRIES_DOMAIN = MenuCreator.createMenuEntries(ActionLibrary.InsertActions.DOMAIN_LAYER_ACTIONS, true);

        /** Einträge der logischen Ebene */
        public static final Component[] MENU_ENTRIES_LOGICAL = MenuCreator.createMenuEntries(ActionLibrary.InsertActions.LOGICAL_TOOLLAYER_ACTIONS, true);

        /** Einträge der physichen bene */
        public static final Component[] MENU_ENTRIES_PHYSICAL = MenuCreator.createMenuEntries(ActionLibrary.InsertActions.PHYSICAL_TOOLLAYER_ACTIONS, true);

        public InsertMenu() {
            super(title);
        }

        @Override
        protected void updateItems() {
            if (!ActionLibrary.InsertActions.isInsertAvailable()) {
                return;
            }

            removeAll();

            int layerID = Tool3lgm.tool.getSelectedGDCollection().getActiveLayer();
            switch (layerID) {
            case ModelConstants.DOMAIN_LAYER:
                MenuCreator.addAll(this, MENU_ENTRIES_DOMAIN);
                break;
            case ModelConstants.LOGICAL_LAYER:
                MenuCreator.addAll(this, MENU_ENTRIES_LOGICAL);
                break;
            case ModelConstants.PHYSICAL_LAYER:
                MenuCreator.addAll(this, MENU_ENTRIES_PHYSICAL);
                break;
            }
        }
    }

    /** Das Layout-Menu */
    private static class LayoutMenu extends DynamicMenu {

        /** Name dieses Menus */
        public static final String title = Tool3lgmConstants.getResString("layoutMenu");

        /** Item für das Teilmodell-Layout */
        private final JMenuItem globalLayout = new JMenuItem(ActionLibrary.LayoutActions.GLOBAL_LAYOUT);

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

            GraphDocument doc = Tool3lgm.tool.getSelectedDoc();
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
        public static final String title = Tool3lgmConstants.getResString("windowMenu");

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
                noc[i - 1] = new NamedObjectContainer<Action>(a[i], a[i].getValue(Action.NAME).toString());
            }
            Alphabetical.sort(noc);

            for (int i = 1; i < a.length; i++) {
                a[i] = noc[i - 1].getObject();
            }

            MenuCreator.addAll(this, MenuCreator.createMenuEntries(a, false), firstFrameIndex);
            add(new JSeparator(), firstFrameIndex + 1);// Trenner zwischen aktivem Frame und aktivierbaren Frames
        }
    }

    public static class HelpMenu extends JMenu {

        /** Name dieses Menus */
        public static final String title = Tool3lgmConstants.getResString("help_icon");

        public HelpMenu() {
            super(title);

            JMenuItem help = new JMenuItem(Tool3lgmConstants.getResString("help"));
            Help.getHelp().enableHelpOnButton(help, "willkommen");
            add(help);

            JMenuItem directHelp = new JMenuItem(Tool3lgmConstants.getResString("direct_help")) {
                @Override
                protected void fireActionPerformed(final ActionEvent e) {
                    super.fireActionPerformed(e);
                    //Tool3lgm.tool.getGlassPane().addMouseListener(this);
                    //Tool3lgm.tool.getGlassPane().setVisible(true);
                }
            };
            directHelp.addActionListener(Help.getHelp().getDisplayHelpAfterTracking());
            add(directHelp);

            MenuCreator.addAll(this, MenuCreator.createMenuEntries(false, new JSeparator(), ActionLibrary.HelpActions.EVALUATION, new JSeparator(), ActionLibrary.HelpActions.ONLINE_HELP, ActionLibrary.HelpActions.LGM_ONLINE, new JSeparator(),
                    ActionLibrary.HelpActions.EXAMPLE, ActionLibrary.HelpActions.MODEL_LIBRARY, new JSeparator(), ActionLibrary.HelpActions.INFO, new JSeparator(), ActionLibrary.HelpActions.IMPORT_LICENSE));
        }
    }

    /* *************************************************** Ende: Unterklassen ******************************** */

}
