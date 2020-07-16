package de.imise.util.swing.menu;

import java.awt.Component;
import java.awt.Container;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

/**
 * @author AXS?
 * @create ???
 */
public class DynamicPopupMenu extends JPopupMenu {

    /**
     *
     */
    public DynamicPopupMenu() {
        super();
    }

    /**
     * @param label
     */
    public DynamicPopupMenu(final String label) {
        super(label);
    }

    /**
     * Aktualisiert die Menu Einträge
     * <p>
     * Diese Methode wird automatisch bei jedem Öffnen des Menus aufgerufen und sollte so benutzt
     * werden, dass die Einträge neu gesetz bzw. reduziert oder erweitert werden. <br>
     * Das Setzen der Attribute <code>isSelected</code> und <code>isEnabled</code> muss hier nicht
     * implementiert werden, da dies bereits über {@link #fireMenuSelected()} erfolgt.
     */
    protected void updateItems() {
    }

    /**
     * Fügte alle Items des spezifizierten Menus in dieses PopupMenu ein
     *
     * @param menu
     */
    public void addItemsFrom(final JMenu menu) {
        MenuCreator.addAll(this, MenuCreator.getAllItems(menu));
    }

    /*
     * (non-Javadoc)
     * @see javax.swing.JPopupMenu#firePopupMenuWillBecomeVisible()
     */
    @Override
    protected void firePopupMenuWillBecomeVisible() {
        super.firePopupMenuWillBecomeVisible();
        updateItems();
        removeUselessSeparators(this);
        MenuCreator.checkEnabledAndSelected(this);
    }

    /**
     * Entfernt alle Items innerhalb und inklusive der spezifizierten Indices
     *
     * @param firstIndex
     * @param lastIndex
     */
    public void removeItems(final int firstIndex, final int lastIndex) {
        for (int i = firstIndex; i <= lastIndex; i++) {
            remove(firstIndex);
        }
    }

    /**
     * Fügt die spezifiezierten Items an der spezifizierten Position ein
     *
     * @param items
     * @param pos
     */
    public void insertItems(final JMenuItem[] items, int pos) {
        for (JMenuItem item : items) {
            insert(item, pos++);
        }
    }

    /**
     * Removes all {@link JSeparator} at the end of the menu and every doubled
     * {@link JSeparator}.
     *
     * @param menu the menu to clean from useless separators
     */
    public static final void removeUselessSeparators(final Container menu) {
        boolean delete = true;
        for (int i = menu.getComponentCount() - 1; i >= 0; i--) {
            Component component = menu.getComponent(i);
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
