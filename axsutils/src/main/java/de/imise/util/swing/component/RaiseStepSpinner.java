package de.imise.util.swing.component;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicSpinnerUI;

import de.imise.util.robot.ScreenRobot;

/**
 * Ein {@link JSpinner} für Double-Werte, der beim Drücken der Pfeil-Knöpfe mit
 * der Maus über längere Zeit oder bei langem Drücken der Pfeiltasten auf der
 * Tastatur nicht jeden Wert einzeln hoch oder runter zählt, sondern die
 * Schrittweite der Änderung nach einigen Durchläufen automatisch erhöht.
 *
 * @author AFranz
 * @create 30.06.2010
 */
public class RaiseStepSpinner extends JSpinner implements ChangeListener, MouseListener, KeyListener, FocusListener {

    /** Das Model dieses Spinners */
    private final SpinnerNumberModel model;

    /**
     * Die Ausgangsschrittweite, mit der der Spinner initialisiert wurde. Für
     * diesen Wert könnte man bei Bedarf auch noch eine Set-Funktion
     * bereitstellen.
     */
    private final double initialStepSize;

    /**
     * Zeit in Millisekunden in der 2 aufeinanderfolgende Änderungen als ein
     * Änderungszyklus angesehen werden
     */
    private static final long SAME_CHANGE_LOOP_MAX_MILLIS = 300;

    /** Zeit der letzten erfolgreichen Änderung des Eingabewertes */
    private long lastChangeTime = System.currentTimeMillis();

    /**
     * Anzahl der Änderungen, die jeweils nie mehr als
     * {@value #SAME_CHANGE_LOOP_MAX_MILLIS} Millisekunden auseinander liegen
     */
    private int counter = 0;

    /**
     * Anzahl der Änderungen, die jeweils nie mehr als
     * {@value #SAME_CHANGE_LOOP_MAX_MILLIS} Millisekunden auseinander liegen,
     * nach denen die Änderungsschrittweite um den Faktor
     * {@link #nextLevelStepFactor} erhöht wird. Diesen Wert könnte man bei
     * Bedarf auch noch im Konstruktor übergeben oder eine Set-Funktion
     * bereitstellen.
     */
    private final int nextLevelStepCount = 50;

    /**
     * Faktor, um den die Schrittweite nach {@link #nextLevelStepCount}
     * Änderungsschritten , die jeweils nie mehr als
     * {@value #SAME_CHANGE_LOOP_MAX_MILLIS} Millisekunden auseinander liegen,
     * erhöht wird. Diesen Wert könnte man bei Bedarf auch noch im Konstruktor
     * übergeben oder eine Set-Funktion bereitstellen.
     */
    private final double nextLevelStepFactor = 10;

    /** Letzter gültiger Eingabewert */
    private double lastValue;

    /**
     * @param min
     * @param max
     * @param initialValue
     */
    public RaiseStepSpinner(final int min, final int max, final int initialValue) {
        this(min, max, initialValue, 0);
    }

    /**
     * @param min
     * @param max
     * @param initialValue
     * @param decimalPlace
     */
    public RaiseStepSpinner(final double min, final double max, final double initialValue, final int decimalPlace) {
        super();
        initialStepSize = Math.pow(10d, -decimalPlace);
        if (initialValue > max) {
            System.err.println(min + " " + max + " " + initialValue);
        }
        model = new SpinnerNumberModel(initialValue, min, max, initialStepSize);
        lastValue = initialValue;
        setModel(model);
        StringBuilder sb = new StringBuilder("0");
        if (decimalPlace > 0) {
            sb.append(".0");
            for (int i = 1; i < decimalPlace; i++) {
                sb.append("0");
            }
        }
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(this, sb.toString());
        //dem Editortextfield diese Komponente als KeyListener hinzufügen, damit beim Loslassen von Tasten
        //die Schrittweite der Änderungen wieder aus den Ausgangswert gesetzt werden kann
        editor.getTextField().addKeyListener(this);
        editor.getTextField().addFocusListener(this);
        setEditor(editor);
        addChangeListener(this);
        setUI(new MySpinnerUI(this));
    }

    /*
     * (non-Javadoc)
     * @see javax.swing.JSpinner#getValue()
     */
    @Override
    public Number getValue() {
        return model == null ? null : model.getNumber();
    }

    // ChangeListener Anfang ////////////////////////////////////////////////////

    /*
     * (non-Javadoc)
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.
     * ChangeEvent)
     */
    @Override
    public void stateChanged(final ChangeEvent e) {
        long changeTime = System.currentTimeMillis();
        if (changeTime - lastChangeTime < SAME_CHANGE_LOOP_MAX_MILLIS) {
            counter++;
            model.setStepSize(initialStepSize * Math.pow(nextLevelStepFactor, counter / nextLevelStepCount));
        }
        lastChangeTime = changeTime;

        double value = model.getNumber().doubleValue();
        double min = ((Double) model.getMinimum()).doubleValue();
        double max = ((Double) model.getMaximum()).doubleValue();
        //wenn die die StepSize größer als der Restwert bis zum Minimum ist, dann muss man den Wert explizit auf min setzen
        if (lastValue > value && model.getStepSize().doubleValue() > value - min) {
            model.setValue(min);
            //wenn die die StepSize größer als der Restwert bis zum Maximum ist, dann muss man den Wert explizit auf max setzen
        } else if (lastValue < value && model.getStepSize().doubleValue() > max - value) {
            model.setValue(max);
        }
        lastValue = value;
    }

    // ChangeListener Ende ////////////////////////////////////////////////////

    // MouseListener Anfang ////////////////////////////////////////////////////

    /*
     * (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseClicked(final MouseEvent e) {
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseEntered(final MouseEvent e) {
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseExited(final MouseEvent e) {
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    @Override
    public void mousePressed(final MouseEvent e) {
    }

    /*
     * (non-Javadoc)
     * @see
     * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseReleased(final MouseEvent e) {
        //Die beiden Hoch- und Runter-Buttons des UI können nur über die Maus bedient werden
        //und wenn die Maus losgelassen wird, wird die StepSize wieder auf den Anfangswert gestellt.
        counter = 0;
        model.setStepSize(initialStepSize);
    }

    // MouseListener Ende ////////////////////////////////////////////////////

    // FocusListener Anfang ////////////////////////////////////////////////////

    @Override
    public void focusGained(final FocusEvent e) {
        if (e.getSource() instanceof JFormattedTextField) {
            ScreenRobot.pressKey(KeyEvent.VK_CONTROL, KeyEvent.VK_A);
        }
    }

    @Override
    public void focusLost(final FocusEvent e) {
    }

    // FocusListener Ende ////////////////////////////////////////////////////

    // KeyListener Anfang ////////////////////////////////////////////////////

    /*
     * (non-Javadoc)
     * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
     */
    @Override
    public void keyPressed(final KeyEvent e) {
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
     */
    @Override
    public void keyReleased(final KeyEvent e) {
        //Wenn der Benutzer die Werte im Editor über die Pfeil-hoch und -Runter Tasten ändert, muss nach dem 
        //Loslassen der Tasten die Schrittweite der Änderung wieder auf den Anfangswert gestellt werden.
        counter = 0;
        model.setStepSize(initialStepSize);
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
     */
    @Override
    public void keyTyped(final KeyEvent e) {
    }

    // KeyListener Ende ////////////////////////////////////////////////////

    /**
     * {@link BasicSpinnerUI}, bei dem an die Hoch- und Runter-Buttons ein
     * MouseListener gehängt wird.
     *
     * @author AFranz
     * @create 30.06.2010
     */
    private class MySpinnerUI extends BasicSpinnerUI {

        /**
         * Der MouseListener der an die beiden Buttons gehängt wird
         */
        private final MouseListener mouseListener;

        /**
         * Die beiden Buttons des Spinner
         */
        private Component nextButton, previousButton;

        /**
         * @param mouseListener
         */
        public MySpinnerUI(final MouseListener mouseListener) {
            super();
            this.mouseListener = mouseListener;
        }

        /*
         * (non-Javadoc)
         * @see javax.swing.plaf.basic.BasicSpinnerUI#createNextButton()
         */
        @Override
        protected Component createNextButton() {
            Component nextButton = super.createNextButton();
            //den Spinner als MouseListener zu dem Runter-Knopf hinzufügen, damit beim Loslassen des Knopfes
            //mit der Maus die Schrittweite der Änderungen wieder auf den Anfangswert gestellt wird
            if (nextButton instanceof JButton) {
                ((JButton) nextButton).addMouseListener(mouseListener);
            }
            this.nextButton = nextButton;
            return nextButton;
        }

        /*
         * (non-Javadoc)
         * @see javax.swing.plaf.basic.BasicSpinnerUI#createPreviousButton()
         */
        @Override
        protected Component createPreviousButton() {
            Component previousButton = super.createPreviousButton();
            //den Spinner als MouseListener zu dem Hoch-Knopf hinzufügen, damit beim Loslassen des Knopfes
            //mit der Maus die Schrittweite der Änderungen wieder auf den Anfangswert gestellt wird
            if (previousButton instanceof JButton) {
                ((JButton) previousButton).addMouseListener(mouseListener);
            }
            this.previousButton = previousButton;
            return previousButton;
        }

    }

    @Override
    public synchronized void addFocusListener(final FocusListener l) {
        super.addFocusListener(l);
        MySpinnerUI ui = (MySpinnerUI) getUI();
        ui.nextButton.addFocusListener(l);
        ui.previousButton.addFocusListener(l);
        JSpinner.NumberEditor editor = (JSpinner.NumberEditor) getEditor();
        editor.addFocusListener(l);
        editor.getTextField().addFocusListener(l);
    }

    @Override
    public synchronized void addMouseListener(final MouseListener l) {
        super.addMouseListener(l);
        MySpinnerUI ui = (MySpinnerUI) getUI();
        ui.nextButton.addMouseListener(l);
        ui.previousButton.addMouseListener(l);
        JSpinner.NumberEditor editor = (JSpinner.NumberEditor) getEditor();
        editor.addMouseListener(l);
        editor.getTextField().addMouseListener(l);
    }

    @Override
    public synchronized void removeFocusListener(final FocusListener l) {
        super.removeFocusListener(l);
        MySpinnerUI ui = (MySpinnerUI) getUI();
        ui.nextButton.removeFocusListener(l);
        ui.previousButton.removeFocusListener(l);
        JSpinner.NumberEditor editor = (JSpinner.NumberEditor) getEditor();
        editor.removeFocusListener(l);
        editor.getTextField().removeFocusListener(l);
    }

    @Override
    public synchronized void removeMouseListener(final MouseListener l) {
        super.removeMouseListener(l);
        MySpinnerUI ui = (MySpinnerUI) getUI();
        ui.nextButton.removeMouseListener(l);
        ui.previousButton.removeMouseListener(l);
        JSpinner.NumberEditor editor = (JSpinner.NumberEditor) getEditor();
        editor.removeMouseListener(l);
        editor.getTextField().removeMouseListener(l);
    }

}
