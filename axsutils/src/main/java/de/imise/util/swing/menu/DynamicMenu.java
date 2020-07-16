package de.imise.util.swing.menu;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JSeparator;

/**
 * Erweiterungsklasse zu {@link JMenu}
 * <p>
 * Besonders ist hier, dass bei jedem Öffnen die Einträge aktualisiert werden können.
 * Außerdem werden dabei immer <code>isSelected</code> und <code>isEnabled</code> auf die
 * entsprechenden Werte der {@link Action} dieser Items gesetzt.
 *
 * @author fstephan
 */
public abstract class DynamicMenu extends JMenu {

    private final List<DynamicMenuPlaceholder> placeholders = new ArrayList<>();

    /**
     * Konstruktor
     * <p>
     * Erzeugt eine neue Instanz mit entsprechender Beschriftung
     *
     * @param s
     * @param menuEntries
     */
    public DynamicMenu(final String s, final Object... menuEntries) {
        super(s);
        Object[] allMenuEntries = menuEntries.length > 0 ? menuEntries : new Object[] {
                new DynamicMenuPlaceholder() // wenn gar keine Items angegeben wurden, dann wird hier ein Placeholder eingefügt. Ohne diesen Placeholder braucht man kein DynamicMenu zu nehmen
        };
        for (int i = 0; i < allMenuEntries.length; i++) {
            if (allMenuEntries[i] instanceof DynamicMenuPlaceholder) {
                DynamicMenuPlaceholder placeholder = (DynamicMenuPlaceholder) allMenuEntries[i];
                placeholder.startIndex = i;
                placeholder.parent = this;
                placeholders.add(placeholder);
            }
        }
        MenuCreator.addAll(this, MenuCreator.createMenuEntries(true, allMenuEntries));
    }

    /**
     * Liefert den Placeholder mit am übergebenen Index
     *
     * @param index
     * @return
     */
    public DynamicMenuPlaceholder getPlaceholder(final int index) {
        return placeholders.get(index);
    }

    /**
     * Wird ausgelöst, wenn das Menu geöffnet wird.<br>
     * Löst das Aktualisieren der Items und das Setzen von <code>isSelected</code> und
     * <code>isEnabled</code> aus.
     * {@inheritDoc}
     */
    @Override
    protected void fireMenuSelected() {
        //unbedings erst die Items aktualisieren und dann den Listenern bescheid sagen
        updateItems();
        MenuCreator.checkEnabledAndSelected(this);
        super.fireMenuSelected();
    }

    /**
     * Aktualisiert die Menu Einträge
     * <p>
     * Diese Methode wird automatisch bei jedem Öffnen des Menus aufgerufen und sollte so benutzt
     * werden, dass die Einträge neu gesetz bzw. reduziert oder erweitert werden. <br>
     * Das Setzen der Attribute <code>isSelected</code> und <code>isEnabled</code> muss hier nicht
     * implementiert werden, da dies bereits über {@link #fireMenuSelected()} erfolgt.
     */
    protected final void updateItems() {
        //von hinten löschen, weil sonst die Indizes nicht stimmen
        for (int i = placeholders.size() - 1; i >= 0; i--) {
            removeItems(placeholders.get(i));
        }
        for (DynamicMenuPlaceholder placeholder : placeholders) {
            remove(placeholder);
            updateItems(placeholder);
        }
        removeUselessSeparators(this);
    }

    protected abstract void updateItems(final DynamicMenuPlaceholder placeholder);

    /** Entfernt alle Items innerhalb und inklusive der spezifizierten Indices */
    private final void removeItems(final DynamicMenuPlaceholder placeholder) {
        for (int i = placeholder.startIndex; i < placeholder.startIndex + placeholder.itemCount && getItemCount() > 0; i++) {
            remove(placeholder.startIndex);
        }
        placeholder.itemCount = -1;
        add(placeholder, placeholder.startIndex);
    }

    public static final class DynamicMenuPlaceholder extends Component {

        private int startIndex;

        private int itemCount = -1;

        private DynamicMenu parent;

        public final void addAll(final Action[] actions) {
            if (actions == null || actions.length == 0) {
                return;
            }
            addAll(Arrays.asList(actions));
        }

        public final void addSeparator(final int indexFromStartIndex) {
            MenuCreator.addAll(parent, startIndex + indexFromStartIndex, new JSeparator());
            itemCount++;
        }

        public final void addAll(final List<Action> actions) {
            Component[] menuEntries = MenuCreator.createMenuEntries(actions, true);
            itemCount = (itemCount == -1 ? 0 : itemCount) + actions.size();
            MenuCreator.addAll(parent, startIndex, menuEntries);
        }

        public int getStartIndex() {
            return startIndex;
        }

    }

    /**
     * Removes all {@link JSeparator} at the end of the menu and every doubled
     * {@link JSeparator}.
     *
     * @param menu the menu to clean from useless separators
     */
    public static final void removeUselessSeparators(final JMenu menu) {
        boolean delete = true;
        for (int i = menu.getItemCount() - 1; i >= 0; i--) {
            Component component = menu.getItem(i);
            if (component == null || component instanceof JSeparator) {
                if (delete) {
                    menu.remove(i);
                } else {
                    delete = true;
                }
            } else {
                delete = false;
            }
        }
    }

}
