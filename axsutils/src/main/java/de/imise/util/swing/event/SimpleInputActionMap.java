package de.imise.util.swing.event;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ComponentInputMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 * Erzeugt eine {@link InputMap} und eine {@link ActionMap}, die beide
 * durch diese Klasse einfach gehandhabt werden können.<br>
 * Insbesondere entfällt hier das Erzeugen eines Identifiers zur Verbindung 
 * beider Maps.
 * 
 * @author fstephan
 */
public class SimpleInputActionMap {
	
	private final InputMap inputMap;
	private final ActionMap actionMap;
	private transient int elementCount = 0;
	
	/**
	 * 
	 * @param component
	 * 			Wird nur benötigt, wenn die {@link InputMap} mit der Condition {@link JComponent#WHEN_IN_FOCUSED_WINDOW}
	 * 			für die Komponente gesetzt wird
	 */
	public SimpleInputActionMap(JComponent component) {
		if (component == null)
			inputMap = new InputMap();
		else
			inputMap = new ComponentInputMap(component);
		actionMap = new ActionMap();
	}
	
	private Integer newID() {
		return new Integer(elementCount++);
	}
	
	/**
	 * @see InputMap#put(KeyStroke, Object)
	 * @see ActionMap#put(Object, Action)
	 * 
	 * @param k
	 * 		KeyStroke für den die Action in der ActionMap registriert werden soll
	 * @param a
	 * 		Action, die beim Auslösen des KeyStrokes gefeuert werden soll
	 */
	public void put(KeyStroke k, Action a) {
		Integer id = newID();
		inputMap.put(k, id);
		actionMap.put(id, a);
		elementCount++;
	}
	
	/**
	 * @see InputMap#put(KeyStroke, Object)
	 * @see ActionMap#put(Object, Action)
	 * 
	 * @param k
	 * 		KeyStroke für den die Action in der ActionMap registriert werden soll
	 * @param a
	 * 		ActionListener, dessen {@link ActionListener#actionPerformed(ActionEvent)} 
	 * 		beim Auslösen KeyStrokes aufgerufen werden soll
	 */
	public void put(KeyStroke k, final ActionListener a) {
		Integer id = newID();
		inputMap.put(k, id);
		actionMap.put(id, new AbstractAction() {
			@Override
            public void actionPerformed(ActionEvent e) {
	            a.actionPerformed(e);
            }
		});
		elementCount++;
	}
	
	/**
	 * Nimmt den in der Action spezifizierten KeyStroke in {@link #put(KeyStroke, Action)}
	 * 
	 * @param k
	 * 		KeyStroke für den die Action in der ActionMap registriert werden soll
	 * @param a
	 * 		Action, die beim Auslösen des KeyStrokes gefeuert werden soll
	 */
	public void put(Action a) {
		Integer id = newID();
		inputMap.put((KeyStroke) a.getValue(Action.ACCELERATOR_KEY), id);
		actionMap.put(id, a);
	}
	
	/**
	 * @see InputMap#remove(KeyStroke)
	 * @see ActionMap#remove(Object)
	 *
	 * @param k
	 * 		Entfernt den KeyStroke und die für ihn registierte Action
	 */
	public void remove(KeyStroke k) {
		actionMap.remove(inputMap.get(k));
		inputMap.remove(k);
	}
	
	/**
	 * @return {@link #inputMap}
	 */
	public InputMap getInputMap() {
		return inputMap;
	}
	
	/**
	 * @return {@link #actionMap}
	 */
	public ActionMap getActionMap() {
		return actionMap;
	}
	

}
