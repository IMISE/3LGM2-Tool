package de.imise.util.swing.menu;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;

import de.imise.util.NamedObjectContainer;
import de.imise.util.swing.event.ActionSource;
import de.imise.util.swing.event.ExtendedAction;
import de.imise.util.swing.event.OptionAction;

/**
 * Util-Klasse zur Erzeugung von {@link JMenu}s.
 * <p>
 * Die häufig vorkommenden entry-<code>Objects</code> werden in allen Methoden wie folgt abgebildet:
 * <table border="1">
 * <tr>
 * <th>entry-Object</th>
 * <th>Component</th>
 * <th>Erklärung</th>
 * </tr>
 * <tr>
 * <td>{@link Action}</td>
 * <td>{@link JMenuItem}</td>
 * <td>Erzeugt ein Item mit der spezifizierten Action</td>
 * </tr>
 * <tr>
 * <td>{@link Component}</td>
 * <td>{@link Component}</td>
 * <td>Gibt die spezifizierte Komponente selbst zurück</td>
 * </tr>
 * <tr>
 * <td>{@link Class}</td>
 * <td>{@link Object}</td>
 * <td>Erzeugt neue Instanz der Klasse, wenn möglich</td>
 * </tr>
 * <tr>
 * <td>{@link String}</td>
 * <td>{@link JLabel}</td>
 * <td>Erzeugt ein Label mit der spezifizierten Beschriftung</td>
 * </tr>
 * <tr>
 * <td>{@link NamedObjectContainer}</td>
 * <td>{@link JMenu}</td>
 * <td>Erzeugt ein Untermenu mit dem spezifizierten Namen({@link NamedObjectContainer#toString()}) und den
 * spezifizierten Einträgen ({@link NamedObjectContainer#getObject()})</td>
 * </tr>
 * </table>
 *
 * @author fstephan, AXS
 */
public class MenuCreator {

    /**
     * Erzeugt eine Array von Menu-Einträgen entsprechend der spezifizierten Werte.
     * <p>
     * Diese Methode macht das gleiche wie {@link #createMenuEntries(Object[], boolean)}. Der Unterschied ist nur, dass
     * hier eine einfachere Eingabe der entry-<code>Object</code>s möglich ist.
     *
     * @param setMnemonics
     *            <code>true</code>: setze Mnemonics für die Einträge wenn möglich <br>
     *            <code>false</code>: es werden keine Mnemonics gesetzt.
     * @param menuEntries
     *            siehe Klassen-Doku
     */
    public static final Component[] createMenuEntries(final boolean setMnemonics, final Object... menuEntries) {
        return createMenuEntries(menuEntries, setMnemonics);
    }

    /**
     * Erzeugt eine Array von Menu-Einträgen entsprechend der spezifizierten Werte.
     * <p>
     * Diese Methode tut das gleiche wie {@link #createMenuEntries(boolean, Object...)}
     *
     * @param menuEntries
     *            siehe Klassen-Doku
     * @param setMnemonics
     *            <code>true</code>: setze Mnemonics für die Einträge wenn möglich <br>
     *            <code>false</code>: es werden keine Mnemonics gesetzt.
     */
    public static final Component[] createMenuEntries(final Object[] menuEntries, final boolean setMnemonics) {
        return createMenuEntries(Arrays.asList(menuEntries), setMnemonics);
    }

    /**
     * Erzeugt eine Array von Menu-Einträgen entsprechend der spezifizierten Werte.
     * <p>
     * Diese Methode tut das gleiche wie {@link #createMenuEntries(boolean, Object...)}
     *
     * @param menuEntries
     *            siehe Klassen-Doku
     * @param setMnemonics
     *            <code>true</code>: setze Mnemonics für die Einträge wenn möglich <br>
     *            <code>false</code>: es werden keine Mnemonics gesetzt.
     */
    public static final Component[] createMenuEntries(final Iterable<?> menuEntries, final boolean setMnemonics) {
        List<Object> allMenuEntries = new ArrayList<>();
        for (Object entry : menuEntries) {
            if (entry == null) {
                continue;
            }
            if (entry instanceof Object[]) {
                for (Object subEntry : (Object[]) entry) {
                    allMenuEntries.add(subEntry);
                }
            } else {
                allMenuEntries.add(entry instanceof ActionSource ? ((ActionSource) entry).createAction() : entry);
            }
        }

        Component[] menuItems = new Component[allMenuEntries.size()];
        for (int i = 0; i < allMenuEntries.size(); i++) {
            Component item = createMenuEntry(allMenuEntries.get(i));
            if (item != null) {
                menuItems[i] = item;
            }
        }
        if (setMnemonics) {
            setMnemonics(menuItems);
        }
        return menuItems;
    }

    /**
     * Erzeugt einen einzelnen Eintrag für ein {@link JMenu} entsprechend des spezifizierten entry-<code>Object</code>s.
     *
     * @param entry
     *            siehe Klassen-Doku
     */
    public static final Component createMenuEntry(final Object entry) {
        Component item = null;
        if (entry == null) {
            throw new IllegalArgumentException("null ist kein gültiges Argument");
        } else if (entry instanceof Action) {
            if (entry instanceof ExtendedAction) {
                ExtendedAction action = (ExtendedAction) entry;
                item = action.createMenuItem();
            }
            if (item == null) {
                item = new JMenuItem((Action) entry);
            }
        } else if (entry instanceof Class<?>) {
            try {
                Class<?> menuItemClass = (Class<?>) entry;
                item = (Component) menuItemClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (entry instanceof String) {
            item = new JLabel((String) entry);
        } else if (entry instanceof Component) {
            item = (Component) entry;
        } else if (entry instanceof NamedObjectContainer<?>) {
            if (((NamedObjectContainer<?>) entry).getObject() instanceof Object[]) {
                @SuppressWarnings("unchecked")
                NamedObjectContainer<Object[]> subMenu = (NamedObjectContainer<Object[]>) entry;
                item = createMenu(subMenu.toString(), subMenu.getObject(), false);
            }
        }
        return item;
    }

    /**
     * Erzeugt ein {@link JCheckBoxMenuItem} mit der spezifizierten {@link Action}.
     *
     * @param a
     */
    public static final JCheckBoxMenuItem createCheckBoxItem(final Action a) {
        return new JCheckBoxMenuItem(a);
    }

    /** Erzeugt ein Array von {@link JCheckBoxMenuItem} mit der übergebenen {@link Action} */
    public static JCheckBoxMenuItem[] createCheckBoxItems(final Action... actions) {
        JCheckBoxMenuItem[] items = new JCheckBoxMenuItem[actions.length];
        for (int i = 0; i < items.length; i++) {
            items[i] = createCheckBoxItem(actions[i]);
        }
        return items;
    }

    /**
     * Erzeugt ein {@link JRadioButtonMenuItem} mit der spezifizierten {@link Action}.
     *
     * @param a
     */
    public static final JRadioButtonMenuItem createRadioButtonMenuItem(final Action a) {
        return new JRadioButtonMenuItem(a);
    }

    /**
     * Erzeugt ein {@link JMenu} mit dem entsprechenden Titel und den entsprechendnen Einträgen.
     * Für alle Einträge, bei denen das möglich ist, werden Mnemonics gesetzt.
     *
     * @param title
     *            Titel des Menus
     * @param menuEntries
     *            siehe Klassen-Doku
     */
    public static final JMenu createMenu(final String title, final Object... menuEntries) {
        return createMenu(title, menuEntries, true);
    }

    /**
     * Erzeugt ein {@link JMenu} mit dem entsprechenden Titel und den entsprechendnen Einträgen.
     *
     * @param title
     *            Titel des Menus
     * @param menuEntries
     *            siehe Klassen-Doku
     * @param setMnemonics
     *            <code>true</code>: setze Mnemonics für die Einträge wenn möglich <br>
     *            <code>false</code>: es werden keine Mnemonics gesetzt.
     */
    public static final JMenu createMenu(final String title, final Object[] menuEntries, final boolean setMnemonics) {
        JMenu menu = new JMenu(title);
        addAll(menu, createMenuEntries(menuEntries, setMnemonics));
        return menu;
    }

    /**
     * Erzeugt ein {@link JMenu} mit dem entsprechenden Titel und den entsprechendnen Einträgen.
     *
     * @param title
     *            Titel des Menus
     * @param menuEntries
     *            siehe Klassen-Doku
     * @param setMnemonics
     *            <code>true</code>: setze Mnemonics für die Einträge wenn möglich <br>
     *            <code>false</code>: es werden keine Mnemonics gesetzt.
     */
    public static final JMenu createMenu(final String title, final boolean setMnemonics, final Object... menuEntries) {
        return createMenu(title, menuEntries, setMnemonics);
    }

    /**
     * Erzeugt ein {@link JPopupMenu} mit dem entsprechenden Titel und den entsprechendnen Einträgen.
     *
     * @param title
     *            Titel des Menus
     * @param menuEntries
     *            siehe Klassen-Doku
     * @param setMnemonics
     *            <code>true</code>: setze Mnemonics für die Einträge wenn möglich <br>
     *            <code>false</code>: es werden keine Mnemonics gesetzt.
     */
    public static final JPopupMenu createPopupMenu(final String title, final boolean setMnemonics, final Object... menuEntries) {
        JPopupMenu menu = new DynamicPopupMenu(title);
        addAll(menu, createMenuEntries(setMnemonics, menuEntries));
        return menu;
    }

    /**
     * Erzeugt ein {@link JMenu} mit dem entsprechenden Titel und den entsprechendnen Einträgen.
     *
     * @param title
     *            Titel des Menus
     * @param menuEntries
     *            siehe Klassen-Doku
     * @param setMnemonics
     *            <code>true</code>: setze Mnemonics für die Einträge wenn möglich <br>
     *            <code>false</code>: es werden keine Mnemonics gesetzt.
     */
    public static final JPopupMenu createPopupMenu(final String title, final Object[] menuEntries, final boolean setMnemonics) {
        return createPopupMenu(title, setMnemonics, menuEntries);
    }

    /**
     * Setzt für jeden der übergebenen Menü-Einträge, die Instanzen von <code>AbstractButton</code> sind, ein Mnemonic,
     * wenn noch keins gesetzt wurde. Es wird immer der erste noch freie Buchstabe des Textes des Buttons als Mnemonic
     * gesetzt.
     *
     * @param menuEntries
     *            siehe Klassen-Doku
     */
    public final static void setMnemonics(final Component... menuEntries) {
        for (int i = 0; i < menuEntries.length; i++) {
            if (menuEntries[i] == null || !(menuEntries[i] instanceof AbstractButton)) {
                continue;
            }
            AbstractButton ab = (AbstractButton) menuEntries[i];
            if (ab.getMnemonic() != 0) {
                continue;
            }
            String name = ab.getText();
            if (name == null) {
                continue;
            }
            JButton testButton = new JButton();
            boolean alreadyAssigned = false;
            for (int n = 0; n < name.length(); n++) {
                // hole den n-ten Buchstaben des Namens
                char c = name.charAt(n);
                // wenn es sich um ein WhiteSpace-Zeichen handelt -> nächstes Zeichen
                if (Character.isWhitespace(c)) {
                    continue;
                }
                // das muss sein, weil die setMnemonic das char nicht direkt setzt
                testButton.setMnemonic(c);
                alreadyAssigned = false;
                for (int j = 0; j < menuEntries.length; j++) {
                    if (i == j || !(menuEntries[j] instanceof AbstractButton)) {
                        continue;
                    }
                    AbstractButton me = (AbstractButton) menuEntries[j];
                    if (testButton.getMnemonic() == me.getMnemonic()) {
                        alreadyAssigned = true;
                        break;
                    }
                }
                if (!alreadyAssigned) {
                    ab.setMnemonic(testButton.getMnemonic());
                    break;
                }
            }
        }
    }

    /**
     * Fügt alle Einträge ab der spezifizierten Position in das Menu ein.
     *
     * @param parent
     *            Menu, in das die Einträge eingefügt werden sollen
     * @param pos
     *            Position, ab der eingefügt werden soll
     * @param children
     *            Einträge, die in das Menu eingefügt werden sollen
     */
    public static void addAll(final JMenu parent, int pos, final Component... children) {
        for (Component child : children) {
            parent.add(child, pos++);
        }
    }

    /**
     * Fügt alle Einträge an das Menu an.
     *
     * @param parent
     *            Menu, in das die Einträge eingefügt werden sollen
     * @param children
     *            Einträge, die in das Menu eingefügt werden sollen
     */
    public static void addAll(final JMenu parent, final Component... children) {
        for (Component child : children) {
            parent.add(child);
        }
    }

    /**
     * Fügt alle Einträge ab der spezifizierten Position in das Menu ein.
     *
     * @param parent
     *            Menu, in das die Einträge eingefügt werden sollen
     * @param children
     *            Einträge, die in das Menu eingefügt werden sollen
     * @param pos
     *            Position, ab der eingefügt werden soll
     */
    public static void addAll(final JPopupMenu parent, final Component[] children, int pos) {
        for (Component child : children) {
            parent.add(child, pos++);
        }
    }

    /**
     * Fügt alle Einträge an das Menu an.
     *
     * @param parent
     *            Menu, in das die Einträge eingefügt werden sollen
     * @param children
     *            Einträge, die in das Menu eingefügt werden sollen
     */
    public static void addAll(final JPopupMenu parent, final Component[] children) {
        for (Component child : children) {
            parent.add(child);
        }
    }

    /**
     * Methode gibt alle Menueinträge wieder.
     *
     * @param menu
     */
    public static JMenuItem[] getAllItems(final JMenu menu) {
        int n = menu.getItemCount();
        JMenuItem[] items = new JMenuItem[n];
        for (int i = 0; i < n; i++) {
            items[i] = menu.getItem(i);
        }
        return items;
    }

    /**
     * Methode gibt alle Menueinträge wieder.
     *
     * @param menu
     */
    public static JMenuItem[] getAllItems(final JPopupMenu menu) {
        List<JMenuItem> itemList = new ArrayList<>(menu.getComponentCount());
        for (Component c : menu.getComponents()) {
            if (c instanceof JMenuItem) {
                itemList.add((JMenuItem) c);
            }
        }
        return itemList.toArray(new JMenuItem[itemList.size()]);
    }

    /**
     * Setzt die Attribute <code>isSelected</code> und <code>isEnabled</code> aller Einträge entsprechend der jeweiligen
     * {@link Action}.
     */
    public static void checkEnabledAndSelected(final JPopupMenu menu) {
        checkEnabledAndSelected(getAllItems(menu));
    }

    /**
     * Setzt die Attribute <code>isSelected</code> und <code>isEnabled</code> aller Einträge entsprechend der jeweiligen
     * {@link Action}.
     */
    public static void checkEnabledAndSelected(final JMenu menu) {
        checkEnabledAndSelected(getAllItems(menu));
    }

    /**
     * Setzt die Attribute <code>isSelected</code> und <code>isEnabled</code> aller Einträge entsprechend der jeweiligen
     * {@link Action}.
     */
    private static void checkEnabledAndSelected(final JMenuItem[] items) {
        for (JMenuItem item : items) {
            if (item == null) {
                continue;
            }
            Action a = item.getAction();
            if (a != null) {
                item.setEnabled(a.isEnabled());
                if (a instanceof OptionAction) {
                    OptionAction optionAction = (OptionAction) a;
                    item.setSelected(optionAction.isSelected());
                }
            }
        }
    }

}
