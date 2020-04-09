package de.imise.util.swing.component;

import java.awt.event.FocusListener;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Ein Panel, das 2 Spinner anzeigt, wobei das eine immer einen kleineren Wert hat, als das andere.
 * 
 * @author AFranz
 */
public class MinMaxSpinnerPanel extends JPanel implements ChangeListener{

	/**
	 * Spinner für die Einstellung des Minimum und Maximumwertes
	 */
	private RaiseStepSpinner minSpinner, maxSpinner;
	
	/**
	 * 	Name der Variablen, die zwischen den Kleiner-Gleich-Zeichen angezeigt wird.
	 */
	private String varName;
	
	/**
	 * 	Label, das {@link #varName} zwischen Kleiner-Gleich-Zeichen angezeigt.
	 */
	private JLabel varLabel;
	
	/**
	 * Liefert ein {@link MinMaxSpinnerPanel} für Integer-Werte, bei dem der Minimumspinner mit dem
	 * Minimalwert und der Maximumspinner mit dem Maximalwert initialisiert wird.
	 * 
	 * @param varName
	 * @param min
	 * @param max
	 */
	public MinMaxSpinnerPanel(String varName, int min, int max) {
		this(varName, min, max, min, max);
	}

	/**
	 * Liefert ein {@link MinMaxSpinnerPanel} für Integer-Werte, bei dem der Minimumspinner mit dem
	 * übergebenen Minimalwert und der Maximumspinner mit dem übergebenen Maximalwert initialisiert wird.
	 * 
	 * @param varName
	 * @param min
	 * @param max
	 * @param minInitialValue
	 * @param maxInitialValue
	 */
	public MinMaxSpinnerPanel(String varName, int min, int max, int minInitialValue, int maxInitialValue) {
		this(varName, min, max, minInitialValue, maxInitialValue, 0);
	}

	/**
	 * Liefert ein {@link MinMaxSpinnerPanel} für Double-Werte, bei dem der Minimumspinner mit dem
	 * Minimalwert und der Maximumspinner mit dem Maximalwert initialisiert wird.
	 * 
	 * @param varName
	 * @param min
	 * @param max
	 * @param decimalPlace
	 */
	public MinMaxSpinnerPanel(String varName, double min, double max, int decimalPlace) {
		this(varName, min, max, min, max, decimalPlace);
	}
	
	/**
	 * Liefert ein {@link MinMaxSpinnerPanel} für Double oder Integer-Werte, bei dem der Minimumspinner mit dem
	 * übergebenen Minimalwert und der Maximumspinner mit dem übergebenen Maximalwert initialisiert wird.
	 * 
	 * @param varName
	 * @param min
	 * @param max
	 * @param minInitialValue
	 * @param maxInitialValue
	 * @param decimalPlace
	 * @param converToIntSpinner
	 */
	public MinMaxSpinnerPanel(String varName, double min, double max, double minInitialValue, double maxInitialValue, int decimalPlace) {
		super();
		minSpinner = new RaiseStepSpinner(min, max, minInitialValue, decimalPlace);
		maxSpinner = new RaiseStepSpinner(min, max, maxInitialValue, decimalPlace);
		minSpinner.addChangeListener(this);
		maxSpinner.addChangeListener(this);
		setVarName(varName);
		varLabel = new JLabel("<= " + varName + " <=");
		add(minSpinner);
		add(varLabel);
		add(maxSpinner);
	}

	/**
	 * @return
	 * 		Name der Variablen, die zwischen den Kleiner-Gleich-Zeichen angezeigt wird.
	 */
	public String getVarXName() {
		return varName;
	}

	/**
	 * Setzt den Namen der Variablen, die zwischen den Kleiner-Gleich-Zeichen angezeigt wird.
	 * @param varName
	 */
	public void setVarName(String varName) {
		this.varName = varName;
		varLabel = new JLabel("<= " + varName + " <=");
	}

	/**
	 * @return
	 * 		Aktuellen Wert des Spinners mit dem Minimumwert.
	 */
	public Number getMinValue() {
		return minSpinner.getValue();
	}
	
	/**
	 * @return
	 * 		Aktuellen Wert des Spinners mit dem Maximumwert.
	 */
	public Number getMaxValue() {
		return maxSpinner.getValue();
	}

	/**
	 * Liefert den Spinner für den Minimalwert.
	 * 
	 * @return
	 */
	public RaiseStepSpinner getMinSpinner() {
		return minSpinner;
	}
	
	/**
	 * Liefert den Spinner für den Maximalwert.
	 * 
	 * @return
	 */
	public RaiseStepSpinner getMaxSpinner() {
		return maxSpinner;
	}

	/* (non-Javadoc)
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    @Override
    public void stateChanged(ChangeEvent e) {
    	//verhindert, dass das Minimum das Maximum überschreitet und umgekehrt
    	SpinnerNumberModel minSpinnerModel = (SpinnerNumberModel)minSpinner.getModel();
    	SpinnerNumberModel maxSpinnerModel = (SpinnerNumberModel)maxSpinner.getModel();
	    if (e.getSource() == minSpinner && minSpinnerModel.getNumber().doubleValue() > maxSpinnerModel.getNumber().doubleValue())
	    	maxSpinnerModel.setValue(minSpinnerModel.getValue());
	    else if (e.getSource() == maxSpinner && maxSpinnerModel.getNumber().doubleValue() < minSpinnerModel.getNumber().doubleValue())
	    	minSpinnerModel.setValue(maxSpinnerModel.getValue());
    }

	@Override
	public synchronized void addFocusListener(FocusListener l) {
		super.addFocusListener(l);
		maxSpinner.addFocusListener(l);
		minSpinner.addFocusListener(l);
		varLabel.addFocusListener(l);
	}

	@Override
	public synchronized void addMouseListener(MouseListener l) {
		super.addMouseListener(l);
		maxSpinner.addMouseListener(l);
		minSpinner.addMouseListener(l);
		varLabel.addMouseListener(l);
	}

	@Override
	public synchronized void removeFocusListener(FocusListener l) {
		super.removeFocusListener(l);
		maxSpinner.removeFocusListener(l);
		minSpinner.removeFocusListener(l);
		varLabel.removeFocusListener(l);
	}

	@Override
	public synchronized void removeMouseListener(MouseListener l) {
		super.removeMouseListener(l);
		maxSpinner.removeMouseListener(l);
		minSpinner.removeMouseListener(l);
		varLabel.removeMouseListener(l);
	}

	
	/**
	 * Hängt an beide Spinner den übergebenen ChangeListener
	 * 
	 * @param listener
	 */
	public void addChangeListener(ChangeListener listener) {
		maxSpinner.addChangeListener(listener);
		minSpinner.addChangeListener(listener);
	}

	/**
	 * Entfernt von beiden Spinnern den übergebenen ChangeListener
	 * 
	 * @param listener
	 */
	public void removeChangeListener(ChangeListener listener) {
		maxSpinner.removeChangeListener(listener);
		minSpinner.removeChangeListener(listener);
	}
	
	
}
