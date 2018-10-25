/*
 * Created on 13.09.2007
 */
package de.imise.tool3lgm.graphtools.userfield.dialog.definition.formula;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.userfield.UserField.ACCOUNTING_FUNCTION_AVG;
import static de.imise.tool3lgm.graphtools.userfield.UserField.ACCOUNTING_FUNCTION_INDI;
import static de.imise.tool3lgm.graphtools.userfield.UserField.ACCOUNTING_FUNCTION_MAX;
import static de.imise.tool3lgm.graphtools.userfield.UserField.ACCOUNTING_FUNCTION_MIN;
import static de.imise.tool3lgm.graphtools.userfield.UserField.ACCOUNTING_FUNCTION_MULT;
import static de.imise.tool3lgm.graphtools.userfield.UserField.ACCOUNTING_FUNCTION_REF;
import static de.imise.tool3lgm.graphtools.userfield.UserField.ACCOUNTING_FUNCTION_SUM;
import static de.imise.tool3lgm.graphtools.userfield.UserField.ACCOUNTING_FUNCTION_TWSUM;
import static de.imise.tool3lgm.graphtools.userfield.calculator.Calculator.CLOSE_BRACKET;
import static de.imise.tool3lgm.graphtools.userfield.calculator.Calculator.OPEN_BRACKET;
import static de.imise.tool3lgm.graphtools.userfield.calculator.Calculator.OPERATOR_DIV;
import static de.imise.tool3lgm.graphtools.userfield.calculator.Calculator.OPERATOR_MINUS;
import static de.imise.tool3lgm.graphtools.userfield.calculator.Calculator.OPERATOR_MULT;
import static de.imise.tool3lgm.graphtools.userfield.calculator.Calculator.OPERATOR_PLUS;
import static de.imise.tool3lgm.graphtools.userfield.calculator.Calculator.WHITESPACE;
import static de.imise.tool3lgm.graphtools.userfield.calculator.Calculator.isOperator;
import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.NORTHWEST;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import com.google.common.base.Strings;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.dialog.tools.EasyComponents;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.userfield.CostingUtil;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.UserFieldTarget;
import de.imise.tool3lgm.graphtools.userfield.calculator.Calculator;
import de.imise.util.swing.component.list.AlphabeticalJList;
import de.imise.util.swing.component.text.ExtendedTextArea;

/**
 * Der Formeleditor ist eine GUI-Komponente, die es ermöglich Formelnstrings aus verschiedenen Komponenten(Auswahl aus <code>JList</code>en und
 * <code>JButton</code> zu erstellen (zusammen zu klicken). <br>
 * Die Formel liegt danach in menschenlesbarer Form und als interne Repräsentation, jeweils als String, vor. Die menschenlesbare Form besteht aus den
 * Namen der benutzten <code>userField</code>s und den Operatoren. Die interne Repräsentation besteht aus den userField_Hash_codes und den Operatoren.
 * Zur Eingabe werden die OperatorButtons und OperandenListen jeweils aktiviert, wenn diese als nächstes in der FOrmel "erlaubt" sind. Alle eingaben
 * werden auf einem Stack gespeichert. D.h.: A * B + () C - D > / E Zur korrekten Auswerung in: A * + ( C - D ) / E Wird der Stack abgebaut und die
 * Elemente in die korrekte Position zueinander gebracht Das Klammersymbol liegt wie ein Operand auf dem Stack. Alle nachfolgenden Elemente gehören
 * damit in die Klammer. Zum Kenntlichmachen, das der nächste Operand/Operator nicht mehr in die Klammer gehört, wurde das Metazeichen " > "eingeührt.
 * In diesem Dialog kann die interne Repräsentation der Formal angezeigt werden. Dazu ist <code>hashAreaVisible</code> auf true zu setzen. Andernfalls
 * auf false.
 *
 * @author hboehme
 */
public class FormulaDefinitionDialog extends JDialog implements ActionListener {

    /**
     * COMMENTME
     */
    public static final String CLASSIFICATION_NUMBER = "CLASSIFICATION_NUMBER";

    /**
     * Kenzeichen dafür, dass eine Klammer in einer Formel nach rechts Verlassen wird.
     */
    public static final String LEAVE_BRACKET_ESCAPE_CHARS = "%>%";

    public static final String BRACKETS = OPEN_BRACKET + WHITESPACE + WHITESPACE + CLOSE_BRACKET;

    /**
     * String für Dialogrückgabewert
     */
    private String retVal = "";

    /**
     * Benutzerdefiniertes Eigenschaftsfeld, für das die Formel definiert wird.
     */
    private final UserField userField;

    /**
     * Button zum Bestätigen und schließen
     */
    private final JButton okButton, cancelButton;

    /**
     * TextArea zum Anzeigen des FormelStrings in lesebarer Form.
     */
    private final ExtendedTextArea formulaArea;

    /**
     * <code>ExtendedTextArea</code> zum Anzeigen des FormelStrings in HashCode-Komination. Diese Area ist nicht Teil des Formeleditor. Sie ist nur so
     * lang Bestandteil, wie die Arbeit am Editor dauert. Vor Auslieferung wird sie entfernt. Die Zugriffe darauf können gegen die Abfrage eines
     * Strings ausgetauscht werden.
     */
    private final ExtendedTextArea hashArea;

    private String formelString = "";

    /**
     * Gibt an, ob die <code>hashArea</code> im Formeleditor angezeigt werden soll oder nicht. Da die <code>hashArea</code> in einem
     * <code>JScrollPane</code> liegt, wird die <code>JScrollPane</code> angezeigt oder nicht
     */
    private final boolean hashAreaVisible = false;

    /**
     * Ist die Liste, die die <code> userField<code>s anzeigt.
     */
    private AlphabeticalJList userFieldList;

    /**
     * Die Liste, die die Modellattribute enthält.
     */
    private AlphabeticalJList modelAttributes;

    /**
     *
     */
    private final UserFieldDefinitions definitions;

    /**
     * Beim Bearbeiten einer Formel, der schon bestehende String in Hash-Ausdrücken.
     */
    private String oldFormulaString = "";

    /**
     * Dieser <code>Stack</code> beinhaltet die Formel in klickreihenfolge. Bsp: Klickreihenfolge: 3 * () 4 + 5 soll darstellen: 3 * ( 4 + 5 )
     */
    private FormulaDefinitionDialogStack termStack;

    private final AccountingFunctionsButtonPanel accountingFunctionsButtonPanel;

    private final CalculatorStyledButtonPanel operatorAndNumberInputPanel;

    private final FormulaControlButtonPanel formulaControlPanel;

    /**
     * Gibt an, wieviele Klammernpaare noch verlassen werden können.
     */
    private int leaveableBracketCounter = 0;

    /**
     * Dieses Label zeigt an, ob die Formel syntaktisch korrekt ist. dafür verbessern.
     */
    private JLabel statusLabel;

    /**
     * <code>caretPosInFormulaArea</code>, ist die Postition, an der der Cursor in der TextArea angezeigt werden soll, in der die Formel in
     * menschenlasbarer Form dargestellt wird. Der cursor zeigt die aktuelle Einfügemarke an. somit weiß man in geschachtelten klammer, wo als
     * nächstes ein Term eingefügt wird.
     */
    private int caretPosInFormulaArea;

    /**
     * @param owner Dialogowner
     * @param def
     * @param field
     * @param classelement
     * @param oldFormulaString
     */
    private FormulaDefinitionDialog(final JDialog owner, final UserFieldDefinitions def, final UserField field, final String newUserFieldName) {
        super(owner, true);
        definitions = def;
        userField = field;
        oldFormulaString = userField.getFormula();
        Class<? extends UserFieldTarget> targetClass = userField.getTargetClass();
        Class<? extends ModelElement> elementClass = ModelElement.class.isAssignableFrom(targetClass) ? targetClass.asSubclass(ModelElement.class) : null;
        ModelConstants.isNodeType(targetClass);
        String targetClassDisplayName = elementClass == null || ModelConstants.isEdgeType(targetClass) ? "" : "  -  " + ElementsNameBuilder.getDisplayableName(elementClass);
        setTitle(getResString("formulaEditorDialog") + targetClassDisplayName + "  -  " + newUserFieldName);
        setLocationByPlatform(true);

        okButton = createButton("ok");
        cancelButton = createButton("cancel");

        accountingFunctionsButtonPanel = new AccountingFunctionsButtonPanel();
        operatorAndNumberInputPanel = new CalculatorStyledButtonPanel();
        formulaControlPanel = new FormulaControlButtonPanel();
        formulaArea = createFormulaTextArea();
        hashArea = createFormulaTextArea();

        init();
    }

    /**
     * Initialisiert den <code>FormulaEditorDialog</code>
     */
    private void init() {
        Container pane = getContentPane();
        setLocationByPlatform(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        /*
         * schließen des Fensters abfangen (wenn nicht durch Abbrechen-Button veranlasst)
         */
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                cancelButton.doClick();
            }
        });

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = BOTH;
        gbc.anchor = NORTHWEST;
        gbc.insets = new Insets(2, 0, 2, 0);
        gbc.gridheight = 1;
        gbc.gridwidth = 3;
        panel.add(new JLabel(getResString("formula")), nextLine(gbc));

        gbc.weightx = 1;
        gbc.weighty = 0.5;
        gbc.gridheight = 1;
        JScrollPane formualAreaScrollPane = new JScrollPane(formulaArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panel.add(formualAreaScrollPane, nextLine(gbc));

        //Wenn die hashArea nicht sichtbar sein soll, darf ich nicht die ScrollPane eingefügt werden.
        if (hashAreaVisible) {
            gbc.gridheight = 1;
            gbc.weighty = 0.02;
            gbc.weightx = 1;
            panel.add(new JScrollPane(hashArea), nextLine(gbc));
        }

        gbc.weighty = 1;
        gbc.weightx = 1;
        panel.add(getListsPanel(), nextLine(gbc));

        gbc.gridwidth = 1;
        gbc.weighty = 0;
        gbc.weightx = 0;
        panel.add(formulaControlPanel, nextLine(gbc));
        gbc.weightx = 1;
        panel.add(accountingFunctionsButtonPanel, nextColumn(gbc));
        gbc.weightx = 0;
        panel.add(operatorAndNumberInputPanel, nextColumn(gbc));

        pane.setLayout(new BorderLayout());
        pane.add(panel, BorderLayout.CENTER);
        pane.add(getSouthButtonPanel(), BorderLayout.SOUTH);

        pack();

        termStack = new FormulaDefinitionDialogStack(this);
        termStack.fill(oldFormulaString);
    }

    private AlphabeticalJList initUserFieldList() {
        final AlphabeticalJList returnList = new AlphabeticalJList();
        returnList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        returnList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                if (returnList.isEnabled() && e.getClickCount() > 0) {
                    UserField tmpUserField = (UserField) returnList.getSelectedValue();
                    if (tmpUserField != null) {
                        termStack.push(tmpUserField.getHashCode());
                    }
                }
            }
        });
        return returnList;
    }

    private JPanel getListsPanel() {
        userFieldList = initUserFieldList();
        modelAttributes = initUserFieldList();
        updateFieldList(userField.getTargetClass());
        JPanel listsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weighty = 0;
        gbc.weightx = 0.5;
        listsPanel.add(new JLabel(getResString("attributes")), nextLine(gbc));
        listsPanel.add(new JLabel(getResString("model_variable")), nextColumn(gbc));
        gbc.weighty = 1;
        gbc.fill = BOTH;
        listsPanel.add(new JScrollPane(userFieldList), nextLine(gbc));
        listsPanel.add(new JScrollPane(modelAttributes), nextColumn(gbc));
        return listsPanel;
    }

    private JPanel getSouthButtonPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        statusLabel = new JLabel("");
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(statusLabel, nextLine(gbc));
        gbc.weightx = 0;
        panel.add(okButton, nextColumn(gbc));
        panel.add(cancelButton, nextColumn(gbc));
        return panel;
    }

    private JButton createButton(final String resKey) {
        return EasyComponents.createButton(this, resKey);
    }

    private static ExtendedTextArea createFormulaTextArea() {
        ExtendedTextArea formulaTextArea = new ExtendedTextArea(3, 4);
        formulaTextArea.setLineWrap(true);
        formulaTextArea.setWrapStyleWord(true);
        // wird gemacht, damit sämtliche Keyevents irrelevant werden.
        formulaTextArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(final KeyEvent e) {
                e.consume();
            }

            @Override
            public void keyPressed(final KeyEvent e) {
                e.consume();
            }

            @Override
            public void keyReleased(final KeyEvent e) {
                e.consume();
            }
        });
        return formulaTextArea;
    }

    /**
     * Lädt alle anderen <code>UserField</code>s (userFields, die vom STYLE CLASSIFICATION_NUMBER sind) in die JList.
     *
     * @param elementClass
     */
    private void updateFieldList(final Class<? extends UserFieldTarget> elementClass) {
        for (UserField uf : definitions.getUserFields(elementClass)) {
            if (uf != userField && uf.isClassificationUserField()) {
                userFieldList.addItem(uf);
            }
        }
        for (UserField uf : definitions.getGlobalUserFields()) {
            if (!uf.hasStyle(UserField.Style.FORMAT)) {
                modelAttributes.addItem(uf);
            }
        }
    }

    /**
     * Zeigt den FormelEditorDialog.
     *
     * @param owner
     * @param def
     * @param field
     * @param userFieldTargetClass
     * @param oldFormulaString
     * @return dialog.retval String: Formel in Hash-Ausdrucksform
     */
    public static String showDialog(final JDialog owner, final UserFieldDefinitions def, final UserField field, final String newUserFieldName) {
        if (owner == null || field == null) {
            return "-1";
        }
        FormulaDefinitionDialog dialog = new FormulaDefinitionDialog(owner, def, field, newUserFieldName);
        dialog.setVisible(true);
        return dialog.retVal;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        String cmd = e.getActionCommand();
        Object source = e.getSource();
        if (source == okButton) {
            retVal = convertStackFormulaToOrdinaryFormula();
            if (CostingUtil.isFormulaValid(retVal)) {
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, getResString("syntax_error_in_formula"));
            }
        } else if (source == cancelButton) {
            retVal = oldFormulaString;
            dispose();
        } else if (isOperator(cmd)) {
            termStack.push(cmd);
        } else if (cmd.equals(BRACKETS)) {
            leaveableBracketCounter++;
            termStack.push(cmd);
        } else if (cmd.equals(LEAVE_BRACKET_ESCAPE_CHARS)) {
            leaveableBracketCounter--;
            termStack.push(cmd);
        } else if (source == accountingFunctionsButtonPanel.buttonindikator) {
            if (formulaArea.getText().isEmpty() || formulaArea.getText().trim().startsWith(ACCOUNTING_FUNCTION_INDI)) {
                IndicatorDialog indiDialog;
                indiDialog = new IndicatorDialog(this, userField);
                String newIndi = indiDialog.showDialog();
                newIndi = getIndikatorStackString(newIndi);
                if (!newIndi.isEmpty()) {
                    termStack.push(newIndi);
                }
            } else {
                JOptionPane.showMessageDialog(this, getResString("indicator_in_formula"), getResString("fehler"), JOptionPane.ERROR_MESSAGE);
            }
        } else if (UserField.isAccountingFunction(cmd)) {
            VfDialog vfd = new VfDialog(this, cmd, userField);
            String vfdResult = vfd.showDialog();
            if (!vfdResult.isEmpty()) {
                termStack.push(vfdResult);
            }
        } else if (isNumber(cmd)) {
            String lastElement = termStack.getLastElement();
            if (isNumber(lastElement)) {
                termStack.append(cmd);
            } else if (lastElement.equals(OPERATOR_MINUS)) {
                String preLastElement = termStack.getPreLastElement();
                if (isOperator(preLastElement)) {
                    termStack.append(cmd);
                } else {
                    termStack.push(cmd);
                }
            } else {
                termStack.push(cmd);
            }
            //Komma kann nur aktiv gewesen sein, wenn das lastElement eine Zahl ist, die noch kein Komma enthält
        } else if (containsComma(cmd)) {
            termStack.append(cmd);
        } else if (source == formulaControlPanel.undoButton) {
            String lastStackElement = termStack.getLastElement();
            leaveableBracketCounter += lastStackElement.equals(LEAVE_BRACKET_ESCAPE_CHARS) ? 1 : lastStackElement.equals(BRACKETS) ? -1 : 0;
            termStack.pop();
        } else if (source == formulaControlPanel.clearFormulaButton) {
            // Wenn der Löschenbutton betätigt wurde, werden alle Elemente des Term-stacks entfernt und die textAreas neu gefüllt (in diesem Falls mich nichts).
            termStack.clear();
        }
    }

    private String getIndikatorStackString(final String orgIndicatorString) {
        StringBuilder sb = new StringBuilder();
        if (!Strings.isNullOrEmpty(orgIndicatorString)) {
            sb.append(" ");
            sb.append(UserField.ACCOUNTING_FUNCTION_INDI);
            sb.append(" ( ");
            sb.append(orgIndicatorString);
            sb.append(" ) ");
            termStack.clear();
            termStack.push(sb.toString());
        }
        return sb.toString();
    }

    /**
     * Konvertiert den Stack mit seinen Elementen, die in Eingabereihenfolge vorliegen, in einen String der die Formel in der Form enthält, wie sie
     * mathematisch korrekt ist. Zusätzlich wird hier entschieden, an welcher Position der Cursor im Textfeld für die Formel angezeigt wird.
     *
     * @return String Die Formel in menschenlesbarer Form aber in <code>UserField</code>-hashCode-schreibweise.
     */
    private String convertStackFormulaToOrdinaryFormula() {
        // Schreibt die Elemente aus dem Term-Stack in die <code>ArrayList</code> mit diesem Mechanismus kann evtl. auf den Stack ganz verzichtet
        // werden.
        // die Liste wird verwendet, um die Formel in die korrekte Form zu ordnen
        // Diese Liste beinhaltet die Formel in der korrekten Leseweise.
        ArrayList<String> termList = new ArrayList<>();
        caretPosInFormulaArea = 0;
        int insertIndex = 0;
        String partTerm = "";
        for (int i = 0; i < termStack.size(); i++) {
            partTerm = termStack.get(i);
            if (partTerm.equals(BRACKETS)) {
                termList.add(insertIndex, Calculator.OPEN_BRACKET);

                //hier wird mit Absicht nicht insertIndex++ gemacht,
                //da somit nochmal an der selben Stelle etwas eingefügt werden kann
                termList.add(insertIndex + 1, Calculator.CLOSE_BRACKET);

                //Wenn eine Klammer eingefügt wird,
                //ist die soll der Cursor in der Mitte des Klammernpaares stehen.
                caretPosInFormulaArea += 2;
            } else if (partTerm.equals(LEAVE_BRACKET_ESCAPE_CHARS)) {
                caretPosInFormulaArea += 2;
            } else {
                termList.add(insertIndex, partTerm);

                // Die Teilformel in einen menschenlesbaren String umwandeln,
                // damit seine Länge in die Berechung für die Cusorposition eingehen kann.
                String humanReadablePartTerm = CostingUtil.getHumanReadableFormulaString(partTerm, definitions);
                caretPosInFormulaArea += humanReadablePartTerm.length();
            }
            insertIndex++;
        }

        //hier wird die Formel,
        //die aus vielen Teilformeln bestehen kann zusammengesetzt
        StringBuilder formulaString = new StringBuilder();
        for (int i = 0; i < termList.size(); i++) {
            formulaString.append(termList.get(i).toString());
            formulaString.append(" ");
        }
        return formulaString.toString();
    }

    void update() {
        formulaArea.requestFocus();
        String hashStringFormula = convertStackFormulaToOrdinaryFormula();
        //        hashArea.setText(hashStringFormula);
        hashArea.setText(termStack.toString());
        formelString = CostingUtil.getHumanReadableFormulaString(hashStringFormula, definitions);
        formulaArea.setText(formelString);
        //Wenn ein Attribut ausgewählt wurde, darf nicht sofort ein neues hinzugefügt werden.
        updateButtonStates();
    }

    public static String extractFunctionName(final String s) {
        int firstWhitespace = s.indexOf(WHITESPACE);
        return firstWhitespace > 0 ? s.substring(0, firstWhitespace) : "";
    }

    /**
     * Setzte den enabled-Status der Buttons auf true oder false. Somit soll Verhindert werden, dass mehrmal Operatoren hintereinanderer angegeben
     * werden können.
     */
    private void updateButtonStates() {
        String lastTermElement = termStack.getLastElement();
        boolean emptyFormula = termStack.isEmpty();
        formulaControlPanel.undoButton.setEnabled(!emptyFormula);
        formulaControlPanel.clearFormulaButton.setEnabled(!emptyFormula);
        formulaControlPanel.leaveBracketButton.setEnabled(false);

        if (emptyFormula || lastTermElement.equals(BRACKETS)) {
            setOperatorAndNumberButtonStates(false, true, true, false, true);
            setFunctionButtonStates(true);
        } else if (lastTermElement.equals(LEAVE_BRACKET_ESCAPE_CHARS)) {
            setOperatorAndNumberButtonStates(true, true, false, false, false);
            setFunctionButtonStates(false);
        } else if (isOperator(lastTermElement)) {
            boolean minusBefore = lastTermElement.equals(Calculator.OPERATOR_MINUS);
            setOperatorAndNumberButtonStates(false, !minusBefore, true, false, true);
            String preLastElement = termStack.getPreLastElement();
            boolean negateMinusBefore = minusBefore && (preLastElement.isEmpty() || isOperator(preLastElement));
            setFunctionButtonStates(!negateMinusBefore);
        } else if (UserField.isAccountingFunction(extractFunctionName(lastTermElement)) || lastTermElement.startsWith(UserField.USERFIELD_HASH_STRING_PREFIX)) {
            setOperatorAndNumberButtonStates(true, true, false, false, false);
            setFunctionButtonStates(false);
            formulaControlPanel.leaveBracketButton.setEnabled(leaveableBracketCounter > 0);
        } else if (isNumber(lastTermElement)) {
            setOperatorAndNumberButtonStates(true, true, true, !containsComma(lastTermElement), false);
            setFunctionButtonStates(false);
            formulaControlPanel.leaveBracketButton.setEnabled(leaveableBracketCounter > 0);
        }

        String hashFormula = convertStackFormulaToOrdinaryFormula();
        boolean validFormula = !hashFormula.isEmpty() && CostingUtil.isFormulaValid(hashFormula);
        okButton.setEnabled(validFormula);

        //Setze den Cursor an die richtige Stelle. Speziell bei Klammerungen wichtig.
        setCaretInFormulaArea();
        statusLabel.setText(emptyFormula ? "" : getResString(validFormula ? "formula_is_valid" : "formula_is_not_valid"));
    }

    static boolean isNumber(final String s) {
        String n = s.replace(',', '.');
        if (n.length() > 1 && n.charAt(n.length() - 1) == '.') {
            n = n.substring(0, n.length() - 1);
        }
        try {
            Double.parseDouble(n);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private boolean containsComma(final String s) {
        String n = s.replace(',', '.');
        int indexOfComma = n.indexOf('.');
        return indexOfComma >= 0;
    }

    private void setFunctionButtonStates(final boolean enabled) {
        accountingFunctionsButtonPanel.setButtonStates(enabled);
    }

    private void setOperatorAndNumberButtonStates(final boolean operatorsEnabled, final boolean minusEnabled, final boolean numbersEnabled, final boolean commaEnabled, final boolean bracketsEnabled) {
        operatorAndNumberInputPanel.setButtonStates(operatorsEnabled, minusEnabled, numbersEnabled, commaEnabled, bracketsEnabled);
        setListStates(numbersEnabled);
    }

    private void setListStates(final boolean enabled) {
        userFieldList.setEnabled(enabled);
        modelAttributes.setEnabled(enabled);
    }

    /**
     * Setzt den Cursor in der TextArea, die die Formel darstellt. Der Cusor soll dem Benutzer anzeigen, an welcher Stelle er sich in der Formel
     * befindet und wo als nächstes ein Element eingefügt wird. Vorgehen:
     */
    private void setCaretInFormulaArea() {
        formulaArea.setCaretPosition(caretPosInFormulaArea);
    }

    private static final Border getPanelBorder(final String titleResKey) {
        Border border1 = BorderFactory.createEtchedBorder();
        Border border2 = BorderFactory.createTitledBorder(border1, " " + getResString(titleResKey) + " ");
        return border2;
    }

    private GridBagConstraints nextColumn(final GridBagConstraints gbc) {
        gbc.gridx++;
        return gbc;
    }

    private GridBagConstraints nextLine(final GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy++;
        return gbc;
    }

    private class FormulaControlButtonPanel extends JPanel {

        /**
         * Die Buttons für die Formelsteuerung. Rückgänig machen, Klammer verlassen, Formel leeren.
         */
        private final JButton undoButton, leaveBracketButton, clearFormulaButton;

        public FormulaControlButtonPanel() {
            super(new GridLayout(1, 3));
            setBorder(getPanelBorder("formula_control"));
            undoButton = createButton("undo");
            leaveBracketButton = createButton(LEAVE_BRACKET_ESCAPE_CHARS);
            clearFormulaButton = createButton("clearFormula");
            Border border = getPanelBorder("formula_control");
            setBorder(border);
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(3, 0, 3, 0);
            gbc.gridx = 1;
            gbc.fill = GridBagConstraints.BOTH;
            add(undoButton, gbc);
            add(leaveBracketButton, gbc);
            add(clearFormulaButton, gbc);
        }
    }

    private class AccountingFunctionsButtonPanel extends JPanel {

        /**
         * Die Buttons für die Verrechnungsfunktionen Summe, Teilwertsumme, Maximum, Minimum, Indikator und Reference
         */
        private final JButton buttonsum, buttonMult, buttonteilwertsumme, buttonmax, buttonmin, buttonmittelwert, buttonindikator, buttonReference;

        public AccountingFunctionsButtonPanel() {
            super(new GridLayout(3, 3));
            setBorder(getPanelBorder("accounting_functions"));
            buttonsum = createButton(ACCOUNTING_FUNCTION_SUM);
            buttonMult = createButton(ACCOUNTING_FUNCTION_MULT);
            buttonteilwertsumme = createButton(ACCOUNTING_FUNCTION_TWSUM);
            buttonmax = createButton(ACCOUNTING_FUNCTION_MAX);
            buttonmin = createButton(ACCOUNTING_FUNCTION_MIN);
            buttonmittelwert = createButton(ACCOUNTING_FUNCTION_AVG);
            buttonindikator = createButton(ACCOUNTING_FUNCTION_INDI);
            buttonReference = createButton(ACCOUNTING_FUNCTION_REF);
            addButtons();
        }

        private void addButtons() {
            Class<? extends UserFieldTarget> targetClass = userField.getTargetClass();
            boolean targetClassCanHaveEdges = ModelElement.class.isAssignableFrom(targetClass) && ModelConstants.getEdgeTypes(targetClass.asSubclass(ModelElement.class)).length > 0;
            if (targetClassCanHaveEdges) {
                add(buttonsum);
                add(buttonMult);
                add(buttonteilwertsumme);
                add(buttonmax);
                add(buttonmin);
                add(buttonmittelwert);
                add(buttonindikator);
            }
            if (ModelConstants.isEdgeType(userField.getTargetClass())) {
                add(buttonReference);
            }
        }

        private void setButtonStates(final boolean enabled) {
            buttonmax.setEnabled(enabled);
            buttonmin.setEnabled(enabled);
            buttonmittelwert.setEnabled(enabled);
            buttonteilwertsumme.setEnabled(enabled);
            buttonsum.setEnabled(enabled);
            buttonMult.setEnabled(enabled);
            buttonindikator.setEnabled(enabled);
            buttonReference.setEnabled(enabled);
        }
    }

    private class CalculatorStyledButtonPanel extends JPanel {

        private final JButton[] numberButtons = new JButton[10];

        /**
         * Die Buttons, für +,-,*,/,()
         */
        private final JButton buttonplus, buttonminus, buttonmult, buttondiv, buttonbrackets, buttoncomma;

        public CalculatorStyledButtonPanel() {
            super(new GridBagLayout());
            setBorder(getPanelBorder("nums_and_operators"));
            buttonplus = createButton(OPERATOR_PLUS);
            buttonminus = createButton(OPERATOR_MINUS);
            buttonmult = createButton(OPERATOR_MULT);
            buttondiv = createButton(OPERATOR_DIV);
            buttonbrackets = createButton(BRACKETS);
            char decimalSeparator = new DecimalFormat().getDecimalFormatSymbols().getDecimalSeparator();
            buttoncomma = createButton("" + decimalSeparator);
            for (int i = 0; i < numberButtons.length; i++) {
                numberButtons[i] = createButton(new Integer(i).toString());
            }
            addButtons();
        }

        private void addButtons() {
            GridBagConstraints gbc = new GridBagConstraints();
            add(numberButtons[7], nextLine(gbc));
            add(numberButtons[8], nextColumn(gbc));
            add(numberButtons[9], nextColumn(gbc));
            add(buttondiv, nextColumn(gbc));

            add(numberButtons[4], nextLine(gbc));
            add(numberButtons[5], nextColumn(gbc));
            add(numberButtons[6], nextColumn(gbc));
            add(buttonmult, nextColumn(gbc));

            add(numberButtons[1], nextLine(gbc));
            add(numberButtons[2], nextColumn(gbc));
            add(numberButtons[3], nextColumn(gbc));
            add(buttonminus, nextColumn(gbc));

            add(buttonbrackets, nextLine(gbc));
            add(numberButtons[0], nextColumn(gbc));
            add(buttoncomma, nextColumn(gbc));
            add(buttonplus, nextColumn(gbc));
        }

        /**
         * @param operatorsEnabled wenn <code>true</code> sind die Operator-Buttons ( +,*,/ ) enabled, sonst nicht
         * @param minusEnabled wenn <code>true</code> ist der Minus-Button ( - ) enabled, sonst nicht
         * @param numbersEnabled wenn <code>true</code> sind die Zahlen-Buttons ( 0 -9 ) enabled, sonst nicht
         * @param commaEnabled wenn <code>true</code> ist der Komma-Button enabled, sonst nicht
         * @param bracketsEnabled wenn <code>true</code> ist der Klammer-Button enabled, sonst nicht
         */
        public void setButtonStates(final boolean operatorsEnabled, final boolean minusEnabled, final boolean numbersEnabled, final boolean commaEnabled, final boolean bracketsEnabled) {
            buttonplus.setEnabled(operatorsEnabled);
            buttonminus.setEnabled(minusEnabled);
            buttonmult.setEnabled(operatorsEnabled);
            buttondiv.setEnabled(operatorsEnabled);
            buttoncomma.setEnabled(commaEnabled);
            for (int i = 0; i < numberButtons.length; i++) {
                numberButtons[i].setEnabled(numbersEnabled);
            }
            buttonbrackets.setEnabled(bracketsEnabled);
        }

    }

}