/*
 * Created on 14.02.2008
 */
package de.imise.util.swing.component;

import java.awt.event.FocusEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import javax.swing.JFormattedTextField;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;

/**
 * @author AXS
 */
public class NumberTextField extends JFormattedTextField {

	/**
	 * Wenn <code>true</code> werden nur positive Eingaben akzeptiert.
	 */
	private boolean positiveOnly = false;

	/**
	 * Diese AbstractFormatterFactory wird nur belegt, wenn ein String übergeben
	 * wurde, der sich nicht nach Double parsen lässet. Diese Factory wird mit
	 * der NumberFormatFactory belegt, die das <code>NumberTextField</code>
	 * eigentlich besitzt Wenn dann in dem ElementPropertyDialog ein korrekter
	 * Wert eingegeben wurde, wird genau diese zwishcengespeicherte Factory
	 * zurückgespielt, damit nur noch gültige Wert möglich sind.
	 */
	private AbstractFormatterFactory tmpNumberFormaterFactory;

	/**
	 * @param format
	 * @param positiveOnly
	 */
	protected NumberTextField(NumberFormat numberFormat, boolean positiveOnly) {
		super(numberFormat);
		this.positiveOnly = positiveOnly;

	}

	/**
	 * @param numberFormat
	 * @param positiveOnly
	 * @return
	 */
	public static NumberTextField getNumberTextField(NumberFormat numberFormat, boolean positiveOnly) {
		if (numberFormat == null)
			return getNumberTextField((String) null, positiveOnly);
		return new NumberTextField(numberFormat, positiveOnly);
	}

	/**
	 * @param decimalPlaces
	 * @param positiveOnly
	 * @return
	 */
	public static NumberTextField getNumberTextField(int decimalPlaces, boolean positiveOnly) {
		StringBuilder patternBuilder;
		if (decimalPlaces <= 0)
			patternBuilder = new StringBuilder("0");
		else {
			patternBuilder = new StringBuilder("#0.0");
			for (int i = 1; i < decimalPlaces; i++)
				patternBuilder.append("0");
		}
		return getNumberTextField(patternBuilder.toString(), positiveOnly);
	}

	/**
	 * @param formatPattern
	 * @param positiveOnly
	 * @return
	 */
	public static NumberTextField getNumberTextField(String formatPattern, boolean positiveOnly) {
		NumberFormat f = NumberFormat.getInstance(Locale.getDefault());
		if (f instanceof DecimalFormat) {
			DecimalFormat df = (DecimalFormat) f;
			if (formatPattern != null)
				df.applyPattern(formatPattern);
			return new NumberTextField(df, positiveOnly);
		}
		if (formatPattern == null)
			return new NumberTextField(f, positiveOnly);
		return new NumberTextField(new DecimalFormat(formatPattern), positiveOnly);
	}

	/**
	 * Liefert ein <code>NumberTextField</code> mit dem
	 * Standard-Zahlenformatter, das auch negative Zahlen als Eingabe
	 * akzeptiert.
	 * 
	 * @return
	 */
	public static NumberTextField getNumberTextField() {
		return getNumberTextField((String) null, false);
	}

	/* (non-Javadoc)
	 * @see javax.swing.JFormattedTextField#commitEdit()
	 */
	@Override
	public void commitEdit() throws ParseException {
		setText(replaceWrongDecimalSeparator(getText()));
		// wenn nur positive Zahlen akzeptiert werden sollen
		if (positiveOnly) {
			char minusSign = (new java.text.DecimalFormatSymbols()).getMinusSign();
			// wenn mind. ein Minuszeichenin der Eingabe vorkommt
			if (getText().indexOf(minusSign) >= 0)
				// einfach die Eingabe ungültig machen
				setText("x");
		}
		super.commitEdit();
	}

	/* (non-Javadoc)
	 * @see javax.swing.JFormattedTextField#processFocusEvent(java.awt.event.FocusEvent)
	 */
	@Override
	protected void processFocusEvent(FocusEvent e) {
		if (e.getID() == FocusEvent.FOCUS_GAINED) {
			Object value = getValue();
			if (value != null) {
				// String s = FULL_SIZED_DECIMAL_FORMAT.format(value);
				String s = value.toString();
				char decimalSeparator = (new java.text.DecimalFormatSymbols()).getDecimalSeparator();
				if (s.indexOf(decimalSeparator) < 0) {
					if (decimalSeparator == ',')
						s = s.replace('.', ',');
					else
						/* if (decimalSeparator=='.') */
						s = s.replace(',', '.');
				}
				setText(s);
			}
		} else
		// Beim Verlassen des Textfields muss geprüft werden, ob das Feld
		// geleert wurde. In diesem Fall soll nicht der alte value zurückgesetzt
		// werden sondern
		if (e.getID() == FocusEvent.FOCUS_LOST) {

			Object value = getText();
			if (value.toString().trim().equals("")) {
				setText(value.toString());
				setValue(null);

			}
			// Es wird veruscht den eingegeben String in einen Double-Wert zu
			// parsen. Wenn das funktioniert,
			// wird die ursprünglich übergebene NumberFormatFactory, die in der
			// überschriebenen Methode setValue() extrahiert wurde,
			// dem NumberTextField übergeben.
			// Danach wird der Wert als Double korrekt formatiert und
			// dargestellt.
			try {
				String s = value.toString();
				char decimalSeparator = (new java.text.DecimalFormatSymbols()).getDecimalSeparator();
				if (decimalSeparator == ',')
					s = s.replace(',', '.');
				else
					/* if (decimalSeparator=='.') */
					s = s.replace('.', ',');
				double newDoubleValue = Double.parseDouble(s);
				setFormatterFactory(tmpNumberFormaterFactory);
				setValue(Double.valueOf(newDoubleValue));
			} catch (Exception ex) {

			}

		}
		super.processFocusEvent(e);
	}

	/**
	 * Führt, wenn das übergebene <code>Object</code> ungleich <code>null</code>
	 * ist die <code>toString()</code>-Methode aus und ersetzt in diesem String
	 * alle Punkte gegen Kommas, wenn der DecimalSeparator ein Komma ist und
	 * umgekehrt.
	 * 
	 * @param string
	 */
	private static final String replaceWrongDecimalSeparator(Object objectToString) {
		if (objectToString == null)
			return null;
		char decimalSeparator = (new java.text.DecimalFormatSymbols()).getDecimalSeparator();
		StringBuilder sb = new StringBuilder(objectToString.toString());
		// Komma oder Punkte im String? wenn ja, wo steht das/der letzte?
		int lastIndexOfSeparator = Math.max(sb.lastIndexOf("."), sb.lastIndexOf(","));
		// gar kein Separator -> einfach raus
		if (lastIndexOfSeparator < 0)
			return sb.toString();
		// das letzte Vorkommen einfach schon mal auf den Anzeigeseparator
		// setzen
		sb.setCharAt(lastIndexOfSeparator, decimalSeparator);
		// alle Punkte vor diesem letzten Separator löschen
		int index = 0;
		while (true) {
			index = sb.indexOf(".", index);
			if (index >= 0 && index < lastIndexOfSeparator) {
				sb.deleteCharAt(index);
				lastIndexOfSeparator--;
			} else
				break;
		}
		// alle Kommas vor diesem letzten Separator löschen
		index = 0;
		while (true) {
			index = sb.indexOf(",", index);
			if (index >= 0 && index < lastIndexOfSeparator) {
				sb.deleteCharAt(index);
				lastIndexOfSeparator--;
			} else
				break;
		}
		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.JFormattedTextField#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Object value) {

		// Es wird versucht den übergebenen Wert dem NumberTextField zu
		// übergeben.
		try {
			tmpNumberFormaterFactory = getFormatterFactory();
			super.setValue(value);
		} catch (Exception ex) {
			// Wenn ein ungültiger String übergeben wurde, wird von dem
			// NumberTextField die FormatterFactory geholt und gemerkt.
			// Ersatzweise wird dem numbertextfield nun eine
			// DefaultFormatterFactory übergeben, damit sich belibige Strings
			// darstellen lassen.
			// Dann klappt auch das setValue() mit jedem String.
			tmpNumberFormaterFactory = getFormatterFactory();
			setFormatterFactory(new DefaultFormatterFactory(new DefaultFormatter()));
			super.setValue(value);
		}
//		System.err.println(getValue());
	}
}
