package de.imise.util.swing.menu;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * Erweiterungsklasse zu {@link JMenu}
 * <p>
 * Besonders ist hier, dass bei jedem Öffnen die Einträge aktualisiert werden können.
 * Außerdem werden dabei immer <code>isSelected</code> und <code>isEnabled</code> auf die
 * entsprechenden Werte der {@link Action} dieser Items gesetzt.
 *
 * @author fstephan
 */
public class DynamicMenu extends JMenu {

    /**
     * Konstruktor
     * <p>
     * Erzeugt eine neue Instanz mit entsprechender Beschriftung
     *
     * @param s
     */
    public DynamicMenu(final String s) {
        super(s);
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
    protected void updateItems() {
        MenuCreator.checkEnabledAndSelected(this);
    }

    /** Entfernt alle Items innerhalb und inklusive der spezifizierten Indices */
    public void removeItems(final int firstIndex, final int lastIndex) {
        for (int i = firstIndex; i <= lastIndex; i++) {
            remove(firstIndex);
        }
    }

    /** Fügt die spezifiezierten Items an der spezifizierten Position ein */
    public void insertItems(final JMenuItem[] items, int pos) {
        for (JMenuItem item : items) {
            insert(item, pos++);
        }
    }

}
