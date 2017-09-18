package de.imise.tool3lgm.gui.menu;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.Component;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import de.imise.tool3lgm.event.ActionLibrary;
import de.imise.tool3lgm.graphtools.model.ElementSelectionContext;
import de.imise.util.swing.menu.DynamicMenu;
import de.imise.util.swing.menu.DynamicPopupMenu;
import de.imise.util.swing.menu.MenuCreator;

/**
 * AXS:04.02.2013: Diese Klasse wurde mal von Frank Stepahn angefangen, konnte aber nicht beendet werden.
 * Sie sollte den ContextGenerator ersetzen.
 * Dynamisches Kontextmenu.<br>
 * Noch nicht voll funktionsfähig
 * <p>
 * Beinhaltet alle Einträge bzw. Untermenüs, die das Kontextmenu überhaupt anzeigen kann. Der Kontext wird aus {@link ElementSelectionContext}
 * ermittelt und das Menu entsprechend gebaut.
 *
 * @author fstephan
 */
@SuppressWarnings("unused")
public class FSTContextMenu extends JPopupMenu {

    /**
     * Das "Internes" - Menu * /
     * private static final JMenu INTERNAL_MENU = MenuCreator.createMenu(ResString("localeOptionsMenu"), false,
     * ActionLibrary.ContextActions.VERIFICATION,
     * ActionLibrary.ContextActions.INTERACTIVE,
     * ActionLibrary.ContextActions.COMMANDLINE,
     * ActionLibrary.ContextActions.SHOW_QUEUE,
     * ActionLibrary.ContextActions.TEST
     * );
     */
    // Ebenenmenu muss in dieser Klasse bleiben, weil es einmal normales menu
    // und einmal
    // popmenu ist
    /** Einträge für das Layermenu */
    private static Component[] LAYER_MENU_ENTRIES = MenuCreator.createMenuEntries(false, new MenuCollection.InsertMenu(), //man muss hier das InsertMenu neu insatziieren, da es sonst aus dem Menü oben verschwindet!
            // ActionLibrary.ContextActions.CREATE_TEXTFIELD,
            new JSeparator(),
            // ActionLibrary.ContextActions.CONFIGURATIONS_VISIBILITY,
            // ActionLibrary.ContextActions.CONFIGURATIONS_INVISIBLE,
            new JSeparator(), MenuCollection.LayoutSubMenus.LAYER_LAYOUT_MENU, new JMenuItem(ActionLibrary.LayoutActions.GLOBAL_LAYOUT), new JSeparator()/*
                                                                                                                                                          * ,
                                                                                                                                                          * INTERNAL_MENU
                                                                                                                                                          */);

    /** Das Kontextmenu für Elemente */
    private static JPopupMenu elementMenu = new ElementMenu();

    /** Das Kontextmenu für Ebenen */
    private static JPopupMenu layerMenu = MenuCreator.createPopupMenu(getResString("localeOptionsMenu"), LAYER_MENU_ENTRIES, false);

    //	/*
    //	 * private static Component[] ELEMENT_MENU_ENTRIES =
    //	 * MenuCreator.createMenuEntries(false,
    //	 * ActionLibrary.ContextActions.OPEN_PROPERTY_DIALOG,
    //	 * MenuCreator.createMenu(getResString("hallo"),
    //	 * LAYER_MENU_ENTRIES, false),
    //	 * MenuCollection.LayoutSubMenus.ELEMENT_LAYOUT_MENU );
    //	 */

    protected FSTContextMenu() {
        super();
    }

    protected FSTContextMenu(final String s) {
        super(s);
    }

    public static void showMenu(final Component invoker, final MouseEvent cause) {
        showMenu(invoker, cause.getX(), cause.getY());
    }

    public static void showMenu(final Component invoker, final int x, final int y) {
        JPopupMenu menu = new ElementMenu();
        // MenuCreator.addAll(menu, LAYER_MENU_ENTRIES);
        // menu.add(INTERNAL_MENU);
        menu.show(invoker, x, y);
    }

    private static Component create(final Object entry) {
        return MenuCreator.createMenuEntry(entry);
    }

    /** Das Kontextmenu für Elemente */
    private static class ElementMenu extends DynamicPopupMenu {
        //		/*
        //		 * private Component properties =
        //		 * create(ActionLibrary.ContextActions.OPEN_PROPERTY_DIALOG); private
        //		 * Component hasPart = create private Component isPartOf private
        //		 * Component ownsInterface private Component ownsDBS private Component
        //		 * sendsTo private Component receivesFrom private Component
        //		 * isConnectedWith
        //		 */

        private final Component subordinatedElementsMenu = new SubordinatedElementsMenu();
        private final Component takeOverInSubmodelMenu = new TakeOverInSubmodelMenu();
        private final Component connectToSubmodelMenu = new ConnectToSubmodelMenu();
        private final Component analyseMenu = new AnalyseMenu();
        private final Component configsVisibility = new JCheckBoxMenuItem(ActionLibrary.ContextActions.CONFIGURATIONS_VISIBILITY);

        //		/*
        //		 * private JMenu configsMenu = new
        //		 * JMenu(getResString("windowMenu")); private
        //		 * Component show =
        //		 * create(ActionLibrary.ContextActions.SET_ELEMENT_VISIBLE); private
        //		 * Component hide =
        //		 * create(ActionLibrary.ContextActions.SET_ELEMENT_INVISIBLE); private
        //		 * Component expand =
        //		 * create(ActionLibrary.ContextActions.EXPAND_ELEMENT); private
        //		 * Component collapse =
        //		 * create(ActionLibrary.ContextActions.COLLAPSE_ELEMENT); private
        //		 * Component join = create(ActionLibrary.ContextActions.JOIN_ELEMENTS);
        //		 * private Component removeSubmodel =
        //		 * create(ActionLibrary.EditActions.REMOVE_FROM_SUBMODEL); private
        //		 * Component removeModel =
        //		 * create(ActionLibrary.EditActions.REMOVE_FROM_MODEL);
        //		 */

        private ElementMenu() {
            super();

        }

        @Override
        protected void updateItems() {
            removeAll();

            // Verbinden- und Trennen-Items hinzufügen
            Object[] itemSources;
            boolean hasConnectionItems = false;
            Action[] actions = ActionLibrary.DynamicActions.getLinkActions();
            int n = actions.length;
            if (n > 0) { // Verbinden-Items dem Menu hinzufügen
                itemSources = new Object[n + 1];
                System.arraycopy(actions, 0, itemSources, 1, n);
                itemSources[0] = getResString("verbinden");
                MenuCreator.addAll(this, MenuCreator.createMenuEntries(itemSources, false));
                hasConnectionItems = true;
            }
            actions = ActionLibrary.DynamicActions.getUnlinkActions();
            n = actions.length;
            if (n > 0) { // Trennen-Items dem Menu hinzufügen
                itemSources = new Object[n + 1];
                System.arraycopy(actions, 0, itemSources, 1, n);
                itemSources[0] = getResString("trennen");
                MenuCreator.addAll(this, MenuCreator.createMenuEntries(itemSources, false));
                hasConnectionItems = true;
            }
            if (hasConnectionItems) {
                addSeparator();
            }

            add(configsVisibility);
            // Konfigurationen ein-/ausblenden

        }

        /** Das "Untergeordnete Elemente"- Menü */
        private static class SubordinatedElementsMenu extends DynamicMenu {

            public static final String title = getResString("windowMenu");

            protected SubordinatedElementsMenu() {
                super(title);
                // TODO Auto-generated constructor stub
            }

            @Override
            protected void updateItems() {
                // TODO Auto-generated method stub
            }
        }

        /** Das "Übernehmen in Teilmodell"- Menü */
        private static class TakeOverInSubmodelMenu extends DynamicMenu {

            public static final String title = getResString("windowMenu");

            protected TakeOverInSubmodelMenu() {
                super(title);
                // TODO Auto-generated constructor stub
            }

            @Override
            protected void updateItems() {
                // TODO Auto-generated method stub

            }
        }

        /** Das "Verknüpfen mit Teilmodell"- Menü */
        private static class ConnectToSubmodelMenu extends DynamicMenu {

            public static final String title = getResString("windowMenu");

            protected ConnectToSubmodelMenu() {
                super(title);
                // TODO Auto-generated constructor stub
            }

            @Override
            protected void updateItems() {
                // TODO Auto-generated method stub

            }
        }

        /** Das "XMLAnalyse"- Menü */
        private static class AnalyseMenu extends DynamicMenu {

            public static final String title = getResString("windowMenu");

            protected AnalyseMenu() {
                super(title);
                // TODO Auto-generated constructor stub
            }

            @Override
            protected void updateItems() {
                // TODO Auto-generated method stub
            }
        }

    }

}
