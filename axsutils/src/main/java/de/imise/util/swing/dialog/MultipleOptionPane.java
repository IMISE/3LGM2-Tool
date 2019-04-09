/*
 * Created on 09.11.2007
 */
package de.imise.util.swing.dialog;

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;

import com.google.common.base.Strings;

import de.imise.util.pair.Pair;

/**
 * Stellt einen Dialog bereit, der untereinander CheckBoxen für übergebene Optionen darstellt,
 * die alle einzeln selektiert werden können. Der Dialog gibt die Selektion der Optionen in
 * einem <code>boolean</code>-Array zurück, in der die Reihenfolge der einzelnen boolean-Werte
 * der Reihenfolge der übergebenen Optionen entspricht. Ein <code>true</code> an Stelle 0
 * im Rückgabe-Array bedeutet, dass die erste Option selektiert wurde.<br>
 * Der Dialog kann in seiner Anzeigebreite beschränkt werden, wenn man eine direkte Instanz von
 * dieser Klasse bildet und dann nicht über die statischen Funktionen von <code>JOptionPane</code>
 * sondern über die Instanzmethoden dieser Klasse Dialoge anzeigt.
 *
 * @author AXS
 */
public class MultipleOptionPane extends JOptionPane {

    /**
     * Maximale Anzahl von Zeichen in einer Zeile
     */
    private int maxCharactersPerLineCount = 90;

    /**
     * Ertsellt ein neues Pane, dessen Dialoge auf 90 Zeichen Breite beschränkt sind.
     */
    public MultipleOptionPane() {
        super();
    }

    /**
     * @param maxCharactersPerLineCount maximale Anzahl von Zeichen in einer Zeile
     */
    public MultipleOptionPane(final int maxCharactersPerLineCount) {
        super();
        this.maxCharactersPerLineCount = maxCharactersPerLineCount;
    }

    /**
     * Zeigt einen Dialog an, der oben eine Message und darunter die übergebene Komponente anzeigt.
     *
     * @param parentComponent
     *            Besitzerkomponente des Dialoges
     * @param title
     *            Titel des Dialoges
     * @param message
     *            Nachricht des Dialoges
     * @param component
     *            Anzuzeigende Komponente
     * @return
     *         {@link JOptionPane#OK_OPTION} wenn OK gedrückt wurde. {@link JOptionPane#CANCEL_OPTION}, wenn etwas anderes (Schließen-Kreuz
     *         oder Abbrechen) gedrückt wurde.
     */
    public final int showComponentDialog(final Component parentComponent, final String title, final String message, final Component component) {
        Object msg[] = {
                message, component
        };
        setMessage(msg);
        setMessageType(JOptionPane.QUESTION_MESSAGE);
        setOptionType(JOptionPane.OK_CANCEL_OPTION);
        JDialog dialog = createDialog(parentComponent, title);
        dialog.setVisible(true);
        // Schließen übers Kreuz oder irgendwas unvorhergesehenes
        if (value == null || !(value instanceof Integer)) {
            return CANCEL_OPTION;
        }
        return OK_OPTION;
    }

    /**
     * Zeigt einen Ok-Cancel-Dialog mit einer Checkbox an.
     *
     * @param parentComponent
     *            Besitzerkomponente des Dialoges
     * @param title
     *            Titel des Dialoges
     * @param message
     *            Nachricht des Dialoges
     * @param option
     *            Option die zur Auswahl gestellt wird
     * @param selected
     *            Selektionszustand der Option beim Öffnen des Dialoges
     * @return
     *         Boolean-Wert mit der Selektion der angezeigten Chekcbox oder <code>null</code> bei Abbrechen oder Schließen über das Kreuz
     */
    public static final Boolean showSingleCheckboxDialog(final Component parentComponent, final String title, final String message, final String option, final boolean selected) {
        Object[] answer = showCheckBoxOptionDialog(parentComponent, title, message, null, null, false, option, selected);
        if (answer == null) {
            return null;
        }
        return answer[0] == null;
    }

    /**
     * Das hier sollte man eigentlich über eine DropDown-Auswahl machen, statt über untereinander liegende Chekcboxen, aber das mit den Checkboxen war
     * schon da.
     * Zeigt einen Optionen-Dialog an, der für jede übergebene Option eine Checkbox darstellt.<br>
     * Es kann immer nur eine Chekcbox gleichzeitig selektiert sein. Zusätlich kann separat in einer
     * weiteren Checkbox eine Option wie "Diesen Dialog nicht mehr zeigen" angezeigt und der Eingabewert zurück geliefert werden.
     *
     * @param parentComponent
     * @param title
     * @param message
     * @param options
     * @param selected
     * @param additionalSeparatedOption
     * @param additionalSeparatedOptionSelected
     * @return <code>null</code> wenn nichts gewählt wurde sonst ein Paar, das als erstes Elenent die gewählte Option enthält und als zweites den
     *         Selektionszustand der Zusatzfrage. Dieser ist immer <code>null</code>, wenn die Zusatzfrage gar nicht angezeigt werden sollte.
     */
    public static final <T> Pair<T, Boolean> showSingleSelectionOptionDialog(final Component parentComponent, final String title, final String message, final T[] options, final Object selected, final String additionalSeparatedOption,
            final boolean additionalSeparatedOptionSelected) {
        boolean selectedOptions[] = new boolean[options.length];
        for (int i = 0; i < selectedOptions.length; i++) {
            selectedOptions[i] = options[i] == null ? selected == null : options[i].equals(selected);
            if (selectedOptions[i]) {
                break;
            }
        }
        Object[] answer = showCheckBoxOptionDialog(parentComponent, title, message, options, selectedOptions, true, additionalSeparatedOption, additionalSeparatedOptionSelected);
        if (answer != null) {
            for (int i = 0; i < options.length; i++) {
                if (answer[i] != null) {
                    return new Pair<>((T) answer[i], Strings.isNullOrEmpty(additionalSeparatedOption) ? null : answer[answer.length - 1] != null);
                }
            }
        }
        return null;
    }

    /**
     * Zeigt einen Optionen-Dialog an, der für jede übergebene Option eine Checkbox darstellt.<br>
     * Über das Array <code>selected</code> können bereits selektierte Checkboxen festgelegt werden.
     * Dieses Array muss die gleiche Länge wie <code>options</code> haben oder kann <code>null</code>
     * sein. Wenn es <code>null</code> ist, sind alle Checkboxen nicht selektiert.
     *
     * @param parentComponent
     *            Besitzerkomponente des Dialoges
     * @param title
     *            Titel des Dialoges
     * @param message
     *            Nachricht des Dialoges
     * @param options
     *            Optionen, die über Checkboxen zur Auswahl gestellt werden. Diese Optionen werden über
     *            ihre toString()-Methode im Dialog angezeigt.
     * @param selected
     *            legt fest, ob Checkboxen der Optionen bereits angewählt sind oder nicht
     * @param singleSelection
     *            legt fest, ob immer nur eine Checkbox oder mehrere gleichzeitig selektiert sein können
     * @return Array der übergebenen Options. War die Option ausgewählt, ist sie nicht <code>null</code>, sost ist sie <code>null</code>.
     */
    public static final Object[] showCheckBoxOptionDialog(final Component parentComponent, final String title, final String message, final Object[] options, final boolean[] selected, final boolean singleSelection) {
        return showCheckBoxOptionDialog(parentComponent, title, message, options, selected, singleSelection, null, false);
    }

    /**
     * Zeigt einen Optionen-Dialog an, der für jede übergebene Option eine Checkbox darstellt.<br>
     * Über das Array <code>selected</code> können bereits selektierte Checkboxen festgelegt werden.
     * Dieses Array muss die gleiche Länge wie <code>options</code> haben oder kann <code>null</code>
     * sein. Wenn es <code>null</code> ist, sind alle Checkboxen nicht selektiert.
     *
     * @param parentComponent
     *            Besitzerkomponente des Dialoges
     * @param title
     *            Titel des Dialoges
     * @param message
     *            Nachricht des Dialoges
     * @param options
     *            Optionen, die über Checkboxen zur Auswahl gestellt werden. Diese Optionen werden über
     *            ihre toString()-Methode im Dialog angezeigt.
     * @param selected
     *            legt fest, ob Checkboxen der Optionen bereits angewählt sind oder nicht
     * @param singleSelection
     *            legt fest, ob immer nur eine Checkbox oder mehrere gleichzeitig selektiert sein können
     * @param additionalSeparatedOption wenn nicht null oder leer, dann wird etwas separiert als letzte Option dieser String angezeigt. Das ist dafür
     *            gedacht, eine Frage wie "Diesen Dialog nicht mehr anzeigen" oder "Zeige diesen Dialog beim Start" oder ... zusätzlich zu den
     *            Optionen anzubieten
     * @param additionalSeparatedOptionSelected
     *            wenn <code>true</code>, dann wird die übergebene additionalSeparatedOption vorselektiert, sonst nicht
     * @return Array der übergebenen Options. War die Option ausgewählt, ist sie nicht <code>null</code>, sost ist sie <code>null</code>. Wurde ein
     *         gültiger String bei additionalSeparatedOption übergeben, dann ist die letzte Option in diesem Array der Wert dieser Checkbox bzw.
     *         <code>null</code> wenn diese Checkbox nicht selektiert war oder der übergebene String additionalSeparatedOption selbst, wenn die
     *         Box selektiert war
     */
    public static final Object[] showCheckBoxOptionDialog(final Component parentComponent, final String title, final String message, final Object[] options, final boolean[] selected, final boolean singleSelection, final String additionalSeparatedOption,
            final boolean additionalSeparatedOptionSelected) {
        JOptionPane optionPane = new MultipleOptionPane();
        int optionsCount = options == null ? 0 : options.length;
        AbstractButton[] boxes = new AbstractButton[optionsCount];
        ButtonGroup buttonGroup = singleSelection ? new ButtonGroup() : null;
        boolean showAdditionalOption = !Strings.isNullOrEmpty(additionalSeparatedOption);
        //Anzahl der Zeilen im Panel:
        // - wenn es mind. eine Option gibt und die "Diesen Dialog nicht mehr anzeigen"-Frage gezeigt werden soll, dann wird ein Separator zwischen den Optinen und der Frage eingebaut (optionsCount + 2)
        // - keine Option aber die "Diesen Dialog nicht mehr anzeigen"-Frage = nur die Frage wird angezeigt (= 1)
        // - nur die Optionen sollen angezeigt werden -> Panelzeilenanzahl  = Anzahl der Optionen (= optionsCount)
        // - weder Optionen noch die "Diesen Dialog nicht mehr anzeigen"-Frage -> 0 Zeilen in dem Panel
        int panelLength = showAdditionalOption ? optionsCount > 0 ? optionsCount + 2 : 1 : optionsCount > 0 ? optionsCount : 0;
        JPanel checkBoxPanel = panelLength > 0 ? new JPanel(new GridLayout(panelLength, 1)) : null;
        for (int i = 0; i < optionsCount; i++) {
            String name = options[i].toString();
            AbstractButton checkBox = singleSelection ? new JRadioButton(name) : new JCheckBox(name);
            boxes[i] = checkBox;
            if (buttonGroup != null) {
                buttonGroup.add(checkBox);
            }
            if (selected != null) {
                checkBox.setSelected(selected[i]);
            }
            checkBox.setActionCommand(new Integer(i).toString());
            checkBoxPanel.add(checkBox);
        }
        JCheckBox dontShowThisDialogAgainOption = null;
        if (showAdditionalOption) {
            if (optionsCount > 0) {
                checkBoxPanel.add(new JSeparator());
            }
            dontShowThisDialogAgainOption = new JCheckBox(additionalSeparatedOption);
            dontShowThisDialogAgainOption.setSelected(additionalSeparatedOptionSelected);
            checkBoxPanel.add(dontShowThisDialogAgainOption);
        }

        Object msg[] = {
                message, checkBoxPanel
        };
        optionPane.setMessage(msg);
        optionPane.setMessageType(JOptionPane.QUESTION_MESSAGE);
        optionPane.setOptionType(JOptionPane.OK_CANCEL_OPTION);
        JDialog dialog = optionPane.createDialog(parentComponent, title);
        dialog.setVisible(true);
        Object value = optionPane.getValue();
        // Schließen übers Kreuz oder irgendwas unvorhergesehenes
        if (value == null || !(value instanceof Integer)) {
            return null;
        }
        // Schließen über einen der Knöpfe
        // Knopf ermitteln
        int i = ((Integer) value).intValue();
        // Schließen oder OK
        if (i == JOptionPane.CLOSED_OPTION || i == JOptionPane.OK_OPTION) {
            Object[] returnValue = new Object[optionsCount + (showAdditionalOption ? 1 : 0)];
            for (int j = 0; j < optionsCount; j++) {
                returnValue[j] = boxes[j].isSelected() ? options[j] : null;
            }
            if (showAdditionalOption) {
                returnValue[optionsCount] = dontShowThisDialogAgainOption.isSelected() ? additionalSeparatedOption : null;
            }
            return returnValue;
            // Abbrechen gedrückt
        }
        return null;
    }

    /**
     * Zeigt einen Optionen-Dialog an, der für jede übergebene Option eine Checkbox darstellt.<br>
     * Alle Checkboxen sind nicht selektiert.
     *
     * @param parentComponent
     *            Besitzerkomponente des Dialoges
     * @param title
     *            Titel des Dialoges
     * @param message
     *            Nachricht des Dialoges
     * @param options
     *            Optionen, die über Checkboxen zur Auswahl gestellt werden sollen. Diese Optionen werden über
     *            ihre toString()-Methode im Dialog angezeigt.
     * @return Array der übergebenen Options. War die Option ausgewählt, ist sie nicht <code>null</code>, sost ist sie <code>null</code>
     */
    public static final Object[] showCheckBoxOptionDialog(final Component parentComponent, final String title, final String message, final Object[] options) {
        return showCheckBoxOptionDialog(parentComponent, title, message, options, null, false, null, false);
    }

    /**
     * Infodialog mit OK.
     *
     * @param parentComponent
     * @param title
     * @param message
     */
    public static final void showInformationMessageDialog(final Component parentComponent, final String title, final String message) {
        JOptionPane optionPane = new MultipleOptionPane();
        Object msg[] = {
                message
        };
        optionPane.setMessage(msg);
        optionPane.setMessageType(JOptionPane.WARNING_MESSAGE);
        JDialog dialog = optionPane.createDialog(parentComponent, title);
        dialog.setVisible(true);
    }

    /**
     * Confirm-Dialog
     *
     * @param parentComponent
     * @param title
     * @param message
     * @param options
     */
    public static final int showConfirmDialog(final Component parentComponent, final String title, final String message, final int options, final int messageType) {
        JOptionPane optionPane = new MultipleOptionPane();
        Object msg[] = {
                message
        };
        optionPane.setMessage(msg);
        optionPane.setMessageType(messageType);
        optionPane.setOptionType(options);
        JDialog dialog = optionPane.createDialog(parentComponent, title);
        dialog.setVisible(true);
        Object value = optionPane.getValue();
        //Schließen übers Kreuz oder irgendwas unvorhergesehenes
        if (value == null || !(value instanceof Integer)) {
            return CANCEL_OPTION;
        }
        //Schließen über einen der Knöpfe
        return ((Integer) value).intValue();
    }

    /*
     * (non-Javadoc)
     * @see javax.swing.JOptionPane#getMaxCharactersPerLineCount()
     */
    @Override
    public int getMaxCharactersPerLineCount() {
        return maxCharactersPerLineCount;
    }

    /**
     * @param maxCharactersPerLineCount The maxCharactersPerLineCount to set.
     */
    public void setMaxCharactersPerLineCount(final int maxCharactersPerLineCount) {
        this.maxCharactersPerLineCount = maxCharactersPerLineCount;
    }
}
