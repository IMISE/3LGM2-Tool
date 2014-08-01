package de.imise.util.swing.component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.DecimalFormat;

import javax.swing.JFormattedTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

/**
 * Ein JTetxField, das nur Zahlenwerte in einem bestimmten Bereich mit einer vorher festgeleten Genauigkeit als Eingabe akzeptiert.
 * Im Unterschied zu dem eigentlichen Verhalten eines {@link JFormattedTextField} wird hier schon die Eingabe ungültiger Werte
 * verhindert und nicht erst beim Beenden des Editierens (= Enter gedrückt oder Focus verloren).
 * 
 * @author AXS
 */
public class MinMaxNumberTextField2 extends JFormattedTextField implements CaretListener, FocusListener {

	/** stores the last valid input value*/
	private String oldText;
	
	/** Dezimaltrennzeichen der aktuellen Locale dieses Textfeldes */
	private char decimalSeparator;
	
	/** Anzahl der zulässigen Nachkommastellen bei der Eingabe */
	private int decimalPlaces;
	
	/**
	 * @param min
	 * @param max
	 * @param decimalPlaces
	 */
	public MinMaxNumberTextField2(double min, double max, int decimalPlaces) {
	    StringBuilder sb = new StringBuilder("#");
	    if (decimalPlaces > 0)
	    	sb.append(".");
	    for (int i = 0; i < decimalPlaces; i++)
	    	sb.append("#");
	    this.decimalPlaces = decimalPlaces;
	    DecimalFormat format = new DecimalFormat(sb.toString());
		decimalSeparator = format.getDecimalFormatSymbols().getDecimalSeparator();
	    NumberFormatter formatter = new NumberFormatter(format);
	    setFormatterFactory(new DefaultFormatterFactory(formatter));
	    formatter.setMinimum(new Double(min));
	    formatter.setMaximum(new Double(max));
	    addCaretListener(this);
		addFocusListener(this);
	    oldText = getText();
    }

	/* (non-Javadoc)
     * @see javax.swing.event.CaretListener#caretUpdate(javax.swing.event.CaretEvent)
     */
    @Override
    public void caretUpdate(CaretEvent e) {
    	String text = getText();
    	if (text.equals(oldText))
    		return;
		String parseableText = text.replace(decimalSeparator, '.');
		String newText = text.replace('.', decimalSeparator);
    	try {
    		//testen, ob 
    		Double value = new Double(Double.parseDouble(parseableText));
    		NumberFormatter formatter = (NumberFormatter)getFormatter();
    		//Unterschreiten des Minimums und Unterschreiten des Maximums bei der Eingabe verhindern
    		if (formatter.getMinimum().compareTo(value) > 0 || formatter.getMaximum().compareTo(value) < 0)
    			throw new Exception();
    		//Genauigkeit wurde überschritten
    		int decimalSepIndex = parseableText.indexOf('.');
    		if (decimalSepIndex >= 0 && parseableText.length() - decimalSepIndex - 1 > decimalPlaces)
    			throw new Exception();
    		oldText = newText;
    		if (!newText.equals(text)) {
        		setText(oldText);
    		}
        } catch (Exception ex) {
        	//caretUpdate wird immer 2x gefeuert, aber beim ersten Mal wird bei 
    		//setText() eine Exception ausgelöst und erst beim 2ten Mal kann der
    		//Text tatsächlich wieder zurück gesetzt werden
        	try {
        		setText(oldText);
        	} catch (Exception ex2) {}
        }
    }

	/* (non-Javadoc)
     * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
     */
    @Override
    public void focusGained(FocusEvent e) {
    	//Cursoposition ans Ende setzen
    	setText(getText());
    	//alles selektieren
    	selectAll();
    }

	/* (non-Javadoc)
     * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
     */
    @Override
    public void focusLost(FocusEvent e) {
    }

}
