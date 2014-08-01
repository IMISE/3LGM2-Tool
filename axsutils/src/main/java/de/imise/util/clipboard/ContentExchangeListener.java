package de.imise.util.clipboard;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * {@link KeyListener}, der cut, copy und paste für einen {@link ContentExchanger} steuert.
 * @author fstephan
 */
public class ContentExchangeListener extends KeyAdapter {
	
	/** zu steuernder {@link ContentExchanger} */
	private ContentExchanger contentExchanger;
	
	/**
	 * Konstruktor
	 * 
	 * @param contentExchanger
	 * 			zu steuernder {@link ContentExchanger}
	 */
	public ContentExchangeListener(ContentExchanger contentExchanger) {
		super();
		this.contentExchanger = contentExchanger;
	}
	
	/**
	 * Löst cut, copy oder paste in {@link #contentExchanger} aus.
	 * @see java.awt.event.KeyAdapter#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if(e.isControlDown()) {
			int keyCode = e.getKeyCode();
			switch(keyCode) {
			case KeyEvent.VK_C: contentExchanger.copy();break; // kopieren
			case KeyEvent.VK_X: contentExchanger.cut();break; // ausschneiden
			case KeyEvent.VK_V: contentExchanger.paste();break; // einfügen
			}
			
			
		}
	}

}
