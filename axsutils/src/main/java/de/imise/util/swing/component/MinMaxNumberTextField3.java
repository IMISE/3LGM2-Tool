package de.imise.util.swing.component;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.DecimalFormat;

import javax.swing.JFormattedTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

/**
 * Ein JTextField, das nur Zahlenwerte in einem bestimmten Bereich mit einer vorher festgeleten Genauigkeit als Eingabe akzeptiert.
 * Im Unterschied zu dem eigentlichen Verhalten eines {@link JFormattedTextField} wird hier schon die Eingabe ungültiger Werte
 * verhindert und nicht erst beim Beenden des Editierens (= Enter gedrückt oder Focus verloren).
 *
 * @author AXS
 */
public class MinMaxNumberTextField3 extends JFormattedTextField implements CaretListener, FocusListener, DocumentListener {

    /** stores the last valid input value */
    private String oldText;

    /** Dezimaltrennzeichen der aktuellen Locale dieses Textfeldes */
    private final char decimalSeparator;

    /** Anzahl der zulässigen Nachkommastellen bei der Eingabe */
    private final int decimalPlaces;

    /**
     * @param min
     * @param max
     * @param decimalPlaces
     */
    public MinMaxNumberTextField3(final double min, final double max, final int decimalPlaces) {
        StringBuilder sb = new StringBuilder("#");
        if (decimalPlaces > 0) {
            sb.append(".");
        }
        for (int i = 0; i < decimalPlaces; i++) {
            sb.append("#");
        }
        this.decimalPlaces = decimalPlaces;
        DecimalFormat format = new DecimalFormat(sb.toString());
        decimalSeparator = format.getDecimalFormatSymbols().getDecimalSeparator();
        NumberFormatter formatter = new NumberFormatter(format);
        setFormatterFactory(new DefaultFormatterFactory(formatter));
        formatter.setMinimum(new Double(min));
        formatter.setMaximum(new Double(max));
        addCaretListener(this);
        addFocusListener(this);
        getDocument().addDocumentListener(this);
        oldText = getText();
    }

    @Override
    public void caretUpdate(final CaretEvent e) {
        validateText();
    }

    @Override
    public void insertUpdate(final DocumentEvent e) {
        validateText();
    }

    @Override
    public void removeUpdate(final DocumentEvent e) {
        validateText();
    }

    @Override
    public void changedUpdate(final DocumentEvent e) {
        validateText();
    }

    @SuppressWarnings("unchecked")
    private void validateText() {
        String text = getText();
        if (text.equals(oldText)) {
            return;
        }
        String parseableText = text.replace(decimalSeparator, '.');
        String newText = text.replace('.', decimalSeparator);
        try {
            //testen, ob
            Double value = new Double(Double.parseDouble(parseableText));
            NumberFormatter formatter = (NumberFormatter) getFormatter();
            //Unterschreiten des Minimums und Unterschreiten des Maximums bei der Eingabe verhindern
            Comparable<Double> minimum = formatter.getMinimum();
            Comparable<Double> maximum = formatter.getMaximum();
            if (minimum.compareTo(value) > 0 || maximum.compareTo(value) < 0) {
                throw new Exception();
            }
            //Genauigkeit wurde überschritten
            int decimalSepIndex = parseableText.indexOf('.');
            if (decimalSepIndex >= 0 && parseableText.length() - decimalSepIndex - 1 > decimalPlaces) {
                throw new Exception();
            }
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
            } catch (Exception ex2) {
            }
        }
    }

    @Override
    public void focusGained(final FocusEvent e) {
        //Cursoposition ans Ende setzen
        setText(getText());
        //alles selektieren
        selectAll();
    }

    @Override
    public void focusLost(final FocusEvent e) {
    }

}
