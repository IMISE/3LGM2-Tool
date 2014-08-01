package de.imise.tool3lgm.gui.menu;

import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import de.imise.util.swing.menu.MenuCreator;

/**
 * Die Menu-Leiste des Tools.
 *
 * @author fstephan
 */
public class MenuBar extends JMenuBar {
	
	/** Liste aller toplevel Menus */
	private ArrayList<JMenu> allMenus;
	
	/**
	 * Konstruktor 
	 * <p>
	 * Erzeugt die Menu-Leiste des Tools.
	 */
	public MenuBar() {
	    super();
	    allMenus = new ArrayList<JMenu>(11);

	    add(MenuCollection.FILE_MENU);
	    add(MenuCollection.EDIT_MENU);
	    add(MenuCollection.VIEW_MENU);
	    add(MenuCollection.INSERT_MENU);
	    add(MenuCollection.LAYOUT_MENU);
	    add(MenuCollection.SUBMODEL_MENU);
	    add(MenuCollection.ANALYSIS_MENU);
	    add(MenuCollection.OPTIONS_MENU);
	    add(MenuCollection.EXTRAS_MENU);
	    add(MenuCollection.WINDOW_MENU);
	    add(MenuCollection.HELP_MENU);
		
		MenuCreator.setMnemonics(getAllMenus());
    }

	/** Gibt alle toplevel Menus in einer Liste wieder (nicht geclont)*/
	public JMenu[] getAllMenus() {
		JMenu[] menuArray = new JMenu[allMenus.size()];
		return allMenus.toArray(menuArray);
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.JMenuBar#add(javax.swing.JMenu)
	 */
	@Override
	public JMenu add(JMenu menu) {
		super.add(menu);
		allMenus.add(menu);
		return menu;
	}

}
