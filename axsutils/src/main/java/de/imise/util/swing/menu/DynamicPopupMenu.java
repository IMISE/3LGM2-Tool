package de.imise.util.swing.menu;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;


/**
 * @author AXS?
 * @create ???
 */
public abstract class DynamicPopupMenu extends JPopupMenu {
	
	/**
	 * 
	 */
	public DynamicPopupMenu() {
		super();
	}
	
	/**
	 * @param label
	 */
	public DynamicPopupMenu(String label) {
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
	protected abstract void updateItems();
	
	/**
	 * Fügte alle Items des spezifizierten Menus in dieses PopupMenu ein
	 * @param menu
	 */
	public void addItemsFrom(JMenu menu) {
		MenuCreator.addAll(this, MenuCreator.getAllItems(menu));
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JPopupMenu#firePopupMenuWillBecomeVisible()
	 */
	@Override
	protected void firePopupMenuWillBecomeVisible() {
		super.firePopupMenuWillBecomeVisible();
		updateItems();
		MenuCreator.checkEnabledAndSelected(this);
	}
	
	/** 
	 * Entfernt alle Items innerhalb und inklusive der spezifizierten Indices
	 * @param firstIndex
	 * @param lastIndex
	 */
	public void removeItems(int firstIndex, int lastIndex) {
		for (int i = firstIndex; i <= lastIndex; i++) {
			remove(firstIndex);
		}
	}

	/** 
	 * Fügt die spezifiezierten Items an der spezifizierten Position ein
	 * @param items
	 * @param pos
	 */
	public void insertItems(JMenuItem[] items, int pos) {
		for (JMenuItem item : items)
			insert(item, pos++);
	}
}
