package de.imise.util.swing.event;

import java.awt.AWTEventMulticaster;
import java.util.EventListener;

import javax.swing.event.MouseInputListener;

/**
 * Erweiterungsklasse zum {@link AWTEventMulticaster}. 
 * <p>
 * Beinhaltet die Verallgemeinerung zu den add()- und remove()-Methoden, sodass nun
 * alle von {@link EventListener} abgeleiteten Klassen durch die selbe Methode abgedeckt sind.
 * 
 * @author fstephan
 *
 */
public class EventMulticaster extends AWTEventMulticaster implements MouseInputListener {
	
	protected EventMulticaster(EventListener a, EventListener b) {
	    super(a, b);
    }
	
	/**
	 * Verbindet l1 und l2 zu einem neuen Listener der entsprechenden Klasse. 
	 * <p>
	 * <i>Hinweis:</i> Events werden immer zuerst an l1 und danach an l2 weitergeleitet
	 * 
	 * @param <T>
	 * 		Typ der Listener; Entspricht gleichzeitig dem Typ des Rückgabewertes; 
	 * 		Muss eine Ableitung {@link EventListener} sein.
	 * @param l1
	 * 		Erster Listener
	 * @param l2
	 * 		Zweiter Listener
	 * @param flag
	 * 		Hat keinen Einfluss auf diese Methode; Wird hier nur verwendet, um die Abgrenzung zu den
	 * 		add()-Methoden des {@link AWTEventMulticaster} zu gewährleisten.
	 * 
	 * @return {@link AWTEventMulticaster} aus l1 und l2
	 */
	public static final <T extends EventListener> T add(T l1, T l2, boolean flag) {
		return (T) addInternal(l1, l2);
	}
	
	/**
	 * Entfernt oldl von l.
	 * 
	 * @param <T>
	 * 		Typ der Listener; Entspricht gleichzeitig dem Typ des Rückgabewertes; 
	 * 		Muss eine Ableitung {@link EventListener} sein.
	 * @param l
	 * 		{@link AWTEventMulticaster} von dem oldl entfernt werden soll
	 * @param oldl
	 * 		Listener der von l entfernt werden soll
	 * @param flag
	 * 		Hat keinen Einfluss auf diese Methode; Wird hier nur verwendet, um die Abgrenzung zu den
	 * 		remove()-Methoden des {@link AWTEventMulticaster} zu gewährleisten.
	 * 
	 * @return den nach dem Entfernen resultierenden {@link AWTEventMulticaster}
	 */
	public static final <T extends EventListener> T remove(T l, T oldl, boolean flag) {
		return (T) removeInternal(l, oldl);
	}
	
	

}
