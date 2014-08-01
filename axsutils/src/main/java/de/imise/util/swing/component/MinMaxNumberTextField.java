package de.imise.util.swing.component;

import java.text.DecimalFormat;

import javax.swing.JFormattedTextField;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

/**
 * Ein JTextField, das nur Zahlenwerte in einem bestimmten Bereich mit einer vorher festgeleten Genauigkeit als Eingabe akzeptiert.
 * Im Unterschied zu dem eigentlichen Verhalten eines {@link JFormattedTextField} wird hier schon die Eingabe ungültiger Werte
 * verhindert und nicht erst beim Beenden des Editierens (= Enter gedrückt oder Focus verloren).
 * 
 * @author AXS
 */
public class MinMaxNumberTextField extends JFormattedTextField {

	/**
	 * @param min
	 * @param max
	 * @param decimalPlaces
	 */
	public MinMaxNumberTextField(double min, double max, int decimalPlaces) {
	    StringBuilder sb = new StringBuilder("0");
	    if (decimalPlaces > 0)
	    	sb.append(".");
	    for (int i = 0; i < decimalPlaces; i++)
	    	sb.append("0");
	    DecimalFormat format = new DecimalFormat(sb.toString());
	    NumberFormatter formatter = new NumberFormatter(format);
	    setFormatterFactory(new DefaultFormatterFactory(formatter));
	    formatter.setMinimum(new Double(min));
	    formatter.setMaximum(new Double(max));
    }

}
