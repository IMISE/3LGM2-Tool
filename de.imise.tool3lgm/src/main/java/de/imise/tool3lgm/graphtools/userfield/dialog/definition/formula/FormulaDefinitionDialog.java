/*
 * Created on 13.09.2007
 */
package de.imise.tool3lgm.graphtools.userfield.dialog.definition.formula;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Stack;

import javax.swing.AbstractButton;
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
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.userfield.Calculator;
import de.imise.tool3lgm.graphtools.userfield.CostingUtil;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.UserFieldTarget;
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
    public static final String USERFIELD_IN_FORMULA_BRACKET_LEAVE = "%>%";

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
    private JButton okButton;

    /**
     * TextArea zum Anzeigen des FormelStrings in lesebarer Form.
     */
    private ExtendedTextArea formulaArea;

    /**
     * <code>ExtendedTextArea</code> zum Anzeigen des FormelStrings in HashCode-Komination. Diese Area ist nicht Teil des Formeleditor. Sie ist nur so
     * lang Bestandteil, wie die Arbeit am Editor dauert. Vor Auslieferung wird sie entfernt. Die Zugriffe darauf können gegen die Abfrage eines
     * Strings ausgetauscht werden.
     */
    private ExtendedTextArea hashArea;

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
    private Stack<String> term;

    /**
     * Die Buttons, für +,-,*,/,()
     */
    private JButton buttonplus, buttonminus, buttonmult, buttondiv, buttonklammerauf;

    /**
     * Die Buttons für die Verrechnungsfunktionen Summe, Teilwertsumme, Maximum, Minimum, Indikator und Reference
     */
    private JButton buttonsum, buttonMult, buttonteilwertsumme, buttonmax, buttonmin, buttonmittelwert, buttonindikator, buttonReference;

    /**
     * Die Buttons für die Formelsteuerung. Rückgänig machen, Klammer verlassen, Formel leeren.
     */
    private JButton undoButton, leaveBracketButton, clearFormulaButton;

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
     * Wenn die Elementklasse, für die die Kennzhalformel bearbeitet wird, eine Kante ist, ist <code>isOnlyEdge</code> true;
     */
    private boolean isOnlyEdge = false;

    /**
     * @param owner Dialogowner
     * @param def
     * @param field
     * @param classelement
     * @param oldFormulaString
     */
    private FormulaDefinitionDialog(final JDialog owner, final UserFieldDefinitions def, final UserField field, final String newUserFieldName) {
        super(owner, Tool3lgmConstants.getResString("formulaEditorDialog"), true);
        definitions = def;
        userField = field;
        oldFormulaString = userField.getFormula();
        setTitle(Tool3lgmConstants.getResString("formulaEditorDialog") + "  -  " + userField.getTargetClass().getSimpleName() + "  -  " + newUserFieldName);
        setLocationByPlatform(true);
        term = new Stack<String>();
        init();
        pack();
    }

    /**
     * Initialisiert den <code>FormulaEditorDialog</code>
     */
    private void init() {
        Container pane = getContentPane();
        JLabel label;
        JPanel panel, panel2;
        AbstractButton button;
        Border border1, border2;
        JScrollPane scrollPane;
        setLocationByPlatform(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        /*
         * schließen des Fensters abfangen (wenn nicht durch Abbrechen-Button veranlasst)
         */
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                FormulaDefinitionDialog.this.actionPerformed(new ActionEvent(FormulaDefinitionDialog.this, e.getID(), "cancel"));
            }
        });
        panel2 = new JPanel(new GridBagLayout());
        //GridBagConstraints constraints = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 0, 2, 0), 0, 0);

        isOnlyEdge = ModelConstants.isEdgeType(userField.getTargetClass());

        undoButton = new JButton(Tool3lgmConstants.getResString("undo"));
        leaveBracketButton = new JButton(Tool3lgmConstants.getResString("leaveBracketButton"));
        clearFormulaButton = new JButton(Tool3lgmConstants.getResString("clearFormula"));
        undoButton.setEnabled(false);
        leaveBracketButton.setEnabled(false);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.insets = new Insets(2, 0, 2, 0);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridheight = 1;
        constraints.gridwidth = 1;

        panel2.setBorder(new EmptyBorder(5, 5, 5, 5));
        label = new JLabel(Tool3lgmConstants.getResString("formula") + ": ");
        panel2.add(label, constraints);
        /* keine leere Eingabe zulassen */

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 1;
        constraints.weighty = 0.5;
        constraints.gridheight = 1;
        constraints.gridwidth = 4;
        formulaArea = new ExtendedTextArea(3, 4);
        formulaArea.setLineWrap(true);
        formulaArea.setWrapStyleWord(true);
        formulaArea.addCaretListener(new CaretListener() {
            //wenn keine Formel definiert wurde, soll der okButton nicht aktivert sein. 
            @Override
            public void caretUpdate(final CaretEvent e) {
                if (okButton != null) {
                    if (formulaArea.getText().trim().length() == 0) {
                        okButton.setEnabled(false);
                    } else {
                        okButton.setEnabled(true);
                    }
                }
            }

        });

        //formulaArea.setText(CostingUtil.getHumanReadableFormulaString(oldFormulaString, definitions));

        scrollPane = new JScrollPane(formulaArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        panel2.add(scrollPane, constraints);

        // wird gemacht, damit sämtliche Keyevents irrelevant werden. 
        formulaArea.addKeyListener(new KeyAdapter() {
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

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridheight = 1;
        constraints.weighty = 0.02;
        constraints.weightx = 1;
        constraints.gridwidth = 4;
        hashArea = new ExtendedTextArea(3, 4);
        hashArea.setEditable(false);
        //hashArea.setText(oldFormulaString);
        JScrollPane hashscrollPane = new JScrollPane(hashArea);

        //ElementDialogPanel.
        //Wenn die hashArea nicht sichtbar sein soll, darf ich nicht die ScrollPane eingefügt werden.
        if (hashAreaVisible) {
            panel2.add(hashscrollPane, constraints);
        }
        panel = new JPanel(new GridBagLayout());

        //bpc = ButtonPanelConstraints
        GridBagConstraints bpc = new GridBagConstraints();

        ///////////////UserfieldLabel + List Start
        constraints.gridwidth = 1;
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridheight = 1;
        constraints.weighty = 0;
        constraints.weightx = 0;
        panel2.add(new JLabel(Tool3lgmConstants.getResString("attributes")), constraints);

        userFieldList = new AlphabeticalJList();
        userFieldList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userFieldList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                if (userFieldList.isEnabled()) {
                    UserField tmpUserField = null;
                    if (e.getClickCount() == 1) {
                        tmpUserField = (UserField) userFieldList.getSelectedValue();
                        if (tmpUserField != null) {
                            //pushToStack(USERFIELD_IN_FORMULA_START + tmpUserField.getHashCode() + USERFIELD_IN_FORMULA_END);
                            pushToFormulaStack(tmpUserField.getHashCode());
                            //Wenn ein Attribut ausgewählt wurde, darf nicht sofort ein neues hinzugefügt werden.
                            orgButtons(CLASSIFICATION_NUMBER);
                            checkFormulaValidity();
                        }
                    }
                }
            }
        });

        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.weighty = 0.5;
        constraints.weightx = 0.5;
        constraints.gridwidth = 2;
        panel2.add(new JScrollPane(userFieldList), constraints);
        ///////////////////UserFieldLabel + List Ende

        /////////////////ModellVaraiblen Start

        constraints.gridwidth = 1;
        constraints.gridy = 3;
        constraints.gridx = 2;
        constraints.weighty = 0;
        constraints.weightx = 1;
        panel2.add(new JLabel(Tool3lgmConstants.getResString("model_variable")), constraints);

        modelAttributes = new AlphabeticalJList();
        for (UserField uf : definitions.getGlobalUserFields()) {
            if (!uf.hasStyle(UserField.Style.FORMAT)) {
                modelAttributes.addItem(uf);
            }
        }
        modelAttributes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                UserField tmpUserField = null;
                if (e.getClickCount() == 1) {
                    tmpUserField = (UserField) modelAttributes.getSelectedValue();
                    if (tmpUserField != null) {
                        pushToFormulaStack(tmpUserField.getHashCode());
                        orgButtons(CLASSIFICATION_NUMBER);
                        checkFormulaValidity();
                    }
                }
            }
        });
        constraints.gridx = 2;
        constraints.gridy = 4;
        constraints.weighty = 0.5;
        constraints.weightx = 0.5;
        constraints.gridwidth = 2;
        panel2.add(new JScrollPane(modelAttributes), constraints);

        //////////////////ModellVaraibleb End

        ////////////////// Formelsteuerung Start

        JPanel formulaController = new JPanel();

        Border border = BorderFactory.createTitledBorder(Tool3lgmConstants.getResString("formula_control"));

        formulaController.setBorder(border);
        formulaController.setLayout(new GridBagLayout());
        GridBagConstraints fcc = new GridBagConstraints();

        leaveBracketButton.addActionListener(this);
        leaveBracketButton.setActionCommand("leaveBracket");
        undoButton.addActionListener(this);
        undoButton.setActionCommand("undo");
        clearFormulaButton.addActionListener(this);
        clearFormulaButton.setActionCommand("clearFormula");

        fcc.insets = new Insets(3, 0, 3, 0);
        fcc.gridx = 1;
        fcc.fill = GridBagConstraints.HORIZONTAL;
        formulaController.add(undoButton, fcc);
        formulaController.add(leaveBracketButton, fcc);
        formulaController.add(clearFormulaButton, fcc);

        constraints.gridy = 5;
        constraints.gridx = 0;
        constraints.weighty = 0;
        constraints.weightx = 0;
        constraints.gridwidth = 1;
        //constraints.fill=GridBagConstraints.NONE;
        panel2.add(formulaController, constraints);

        ////////////////// Formelsteuerung End

        constraints.gridy = 5;
        constraints.gridx = 1;
        constraints.weighty = 0;
        constraints.weightx = 1;
        constraints.gridwidth = 3;
        border1 = BorderFactory.createEtchedBorder();
        border2 = BorderFactory.createTitledBorder(border1, " " + Tool3lgmConstants.getResString("operator") + " ");
        panel.setBorder(border2);
        panel2.add(panel, constraints);

        buttonplus = new JButton("+");
        buttonminus = new JButton("-");
        buttonmult = new JButton("*");
        buttondiv = new JButton("/");
        buttonklammerauf = new JButton("( )");
        buttonplus.addActionListener(this);
        buttonminus.addActionListener(this);
        buttonmult.addActionListener(this);
        buttondiv.addActionListener(this);
        buttonklammerauf.addActionListener(this);
        buttonplus.setActionCommand("+");
        buttonminus.setActionCommand("-");
        buttonmult.setActionCommand("*");
        buttondiv.setActionCommand("/");
        buttonklammerauf.setActionCommand("(");
        buttonsum = new JButton(Tool3lgmConstants.getResString("summe"));
        buttonsum.addActionListener(this);
        buttonsum.setActionCommand("sum");
        buttonMult = new JButton(Tool3lgmConstants.getResString("produkt"));
        buttonMult.addActionListener(this);
        buttonMult.setActionCommand("mult");

        buttonteilwertsumme = new JButton(Tool3lgmConstants.getResString("teilwertsumme"));
        buttonteilwertsumme.addActionListener(this);
        buttonteilwertsumme.setActionCommand("teilwertsumme");
        buttonmax = new JButton(Tool3lgmConstants.getResString("maximum"));
        buttonmax.addActionListener(this);
        buttonmax.setActionCommand("maximum");
        buttonmin = new JButton(Tool3lgmConstants.getResString("minimum"));
        buttonmin.addActionListener(this);
        buttonmin.setActionCommand("minimum");
        buttonmittelwert = new JButton(Tool3lgmConstants.getResString("mittelwert"));
        buttonmittelwert.addActionListener(this);
        buttonmittelwert.setActionCommand("mittelwert");
        buttonindikator = new JButton(Tool3lgmConstants.getResString("indicator"));
        buttonindikator.addActionListener(this);
        buttonindikator.setActionCommand("indikator");
        buttonReference = new JButton(Tool3lgmConstants.getResString("reference"));
        buttonReference.addActionListener(this);
        buttonReference.setActionCommand("reference");
        bpc.anchor = GridBagConstraints.NORTHWEST;
        bpc.gridx = 0;
        bpc.gridy = 0;
        bpc.weightx = 0;

        bpc.fill = GridBagConstraints.HORIZONTAL;
        bpc.insets = new Insets(3, 3, 3, 3);

        panel.add(buttonplus, bpc);
        bpc.gridx++;

        panel.add(buttonminus, bpc);
        bpc.gridx++;

        panel.add(buttonmult, bpc);
        bpc.gridx++;

        panel.add(buttondiv, bpc);
        bpc.gridx++;

        bpc.gridy++;
        bpc.gridx = 0;
        panel.add(buttonklammerauf, bpc);
        bpc.gridx++;

        bpc.gridy++;
        bpc.gridx = 0;
        bpc.gridwidth = 2;
        panel.add(new JLabel(Tool3lgmConstants.getResString("accounting") + ": "), bpc);
        bpc.gridwidth = 1;

        bpc.gridy++;
        if (!isOnlyEdge) {
            panel.add(buttonsum, bpc);
            bpc.gridx++;
            panel.add(buttonMult, bpc);
            bpc.gridx++;

            panel.add(buttonteilwertsumme, bpc);
            bpc.gridx++;
            panel.add(buttonmax, bpc);
            bpc.gridx++;
            panel.add(buttonmin, bpc);
            bpc.gridx++;
            panel.add(buttonmittelwert, bpc);
            bpc.gridx++;
            panel.add(buttonindikator, bpc);
            bpc.gridx++;
        } else {
            panel.add(buttonReference, bpc);
        }
        bpc.gridx++;

        // Ein Leer-JLabel mit weightx=1 eingefügt, damit die Buttons immer
        // schön an der linken
        // Seite "kleben" bleiben und nicht zentriert werden. Das Label nimmt
        // nach dem resizen
        // immer den ganzen neuen zusätzlichen Platz ein.
        bpc.gridy++;
        bpc.gridx = 0;
        bpc.weightx = 1;
        bpc.gridwidth = 10;
        panel.add(new JLabel(""), bpc);
        pane.setLayout(new BorderLayout());
        pane.add(panel2, BorderLayout.CENTER);
        //panel2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel2 = new JPanel(new GridBagLayout());
        statusLabel = new JLabel("");
        GridBagConstraints panel2C = new GridBagConstraints();
        panel2C.insets = new Insets(3, 3, 3, 3);
        panel2C.gridx = 1;
        panel2C.gridy = 1;
        panel2C.weightx = 1;
        panel2C.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(statusLabel, panel2C);
        panel2C.weightx = 0;
        panel2C.gridx++;

        okButton = new JButton(Tool3lgmConstants.getResString("ok"));
        okButton.setActionCommand("ok");
        if (formulaArea.getText().trim().length() == 0) {
            okButton.setEnabled(false);
        } else {
            okButton.setEnabled(true);
        }
        okButton.addActionListener(this);
        panel2.add(okButton, panel2C);
        panel2C.gridx++;
        button = new JButton(Tool3lgmConstants.getResString("cancel"));
        button.setActionCommand("cancel");
        button.addActionListener(this);
        panel2.add(button, panel2C);
        pane.add(panel2, BorderLayout.SOUTH);
        pack();
        updateFieldList(userField.getTargetClass());
        term = CostingUtil.getStackForInternalFormula(oldFormulaString);
        if (term != null) {
            formulaArea.setText(CostingUtil.getHumanReadableFormulaString(convertStackFormulaToOrdinaryFormula(), definitions));
            hashArea.setText(convertStackFormulaToOrdinaryFormula());
            orgButtons("withFormula");
        } else {
            orgButtons("initial");
        }
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

        if (e.getActionCommand().equals("ok")) {
            //retVal = hashArea.getText();
            String newHashFormula = convertStackFormulaToOrdinaryFormula();
            if (formulaArea.getText().equals(CostingUtil.getHumanReadableFormulaString(oldFormulaString, definitions))) {
                retVal = oldFormulaString;
            } else if (newHashFormula.length() > 0) {
                retVal = newHashFormula;
            }

            if (CostingUtil.isFormulaValid(retVal)) {
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, Tool3lgmConstants.getErrString("syntax_error_in_formula"));
            }
        } else if (e.getActionCommand().equals("cancel")) {
            retVal = oldFormulaString;
            dispose();
        } else if (e.getActionCommand().equals(Calculator.OPERATOR_PLUS)) {

            formulaArea.requestFocus();
            pushToFormulaStack(Calculator.OPERATOR_PLUS);
            orgButtons(Calculator.OPERATOR_PLUS);
        } else if (e.getActionCommand().equals(Calculator.OPERATOR_MINUS)) {

            pushToFormulaStack(Calculator.OPERATOR_MINUS);
            formulaArea.requestFocus();
            orgButtons(Calculator.OPERATOR_MINUS);
        } else if (e.getActionCommand().equals(Calculator.OPERATOR_MULT)) {

            pushToFormulaStack(Calculator.OPERATOR_MULT);
            formulaArea.requestFocus();
            orgButtons(Calculator.OPERATOR_MULT);
        } else if (e.getActionCommand().equals(Calculator.OPERATOR_DIV)) {

            pushToFormulaStack(Calculator.OPERATOR_DIV);
            formulaArea.requestFocus();
            orgButtons(Calculator.OPERATOR_DIV);
        } else if (e.getActionCommand().equals(Calculator.OPEN_BRACKET)) {
            formulaArea.requestFocus();
            leaveBracketButton.setEnabled(true);
            leaveableBracketCounter++;
            pushToFormulaStack(Calculator.OPEN_BRACKET + "  " + Calculator.CLOSE_BRACKET);
            orgButtons(Calculator.OPEN_BRACKET);

        } else if (e.getActionCommand().equals(Calculator.CLOSE_BRACKET)) {
            orgButtons(Calculator.CLOSE_BRACKET);
        } else if (e.getActionCommand().equals("sum")) {

            formulaArea.requestFocus();
            VfDialog vfd = new VfDialog(this, UserField.ACCOUNTING_FUNCTION_SUM, userField);

            // vfdResult = VerechnugsFunktionsDefinition ist der Term, 
            // der nach der angabe der VF zurück kommt.

            String vfdResult = vfd.showDialog();
            if (!vfdResult.equals("")) {
                pushToFormulaStack(vfdResult);
                orgButtons(UserField.ACCOUNTING_FUNCTION_SUM);
            }
        } else if (e.getActionCommand().equals("mult")) {

            formulaArea.requestFocus();
            VfDialog vfd = new VfDialog(this, UserField.ACCOUNTING_FUNCTION_MULT, userField);

            // vfdResult = VerechnugsFunktionsDefinition ist der Term, 
            // der nach der angabe der VF zurück kommt.

            String vfdResult = vfd.showDialog();
            if (!vfdResult.equals("")) {
                pushToFormulaStack(vfdResult);
                orgButtons(UserField.ACCOUNTING_FUNCTION_SUM);
            }
        } else if (e.getActionCommand().equals("teilwertsumme")) {

            VfDialog vfd = new VfDialog(this, UserField.ACCOUNTING_FUNCTION_TWSUM, userField);
            String vfdResult = vfd.showDialog();
            formulaArea.requestFocus();
            if (!vfdResult.equals("")) {
                pushToFormulaStack(vfdResult);
                orgButtons(UserField.ACCOUNTING_FUNCTION_TWSUM);
            }
        } else if (e.getActionCommand().equals("minimum")) {

            formulaArea.requestFocus();
            VfDialog vfd = new VfDialog(this, UserField.ACCOUNTING_FUNCTION_MIN, userField);

            // vfdResult = VerechnugsFunktionsDefinition ist der Term, 
            // der nach der angabe der VF zurück kommt.

            String vfdResult = vfd.showDialog();
            if (!vfdResult.equals("")) {
                pushToFormulaStack(vfdResult);
                orgButtons(UserField.ACCOUNTING_FUNCTION_SUM);
            }
        } else if (e.getActionCommand().equals("maximum")) {
            formulaArea.requestFocus();
            VfDialog vfd = new VfDialog(this, UserField.ACCOUNTING_FUNCTION_MAX, userField);

            // vfdResult = VerechnugsFunktionsDefinition ist der Term, 
            // der nach der angabe der VF zurück kommt.

            String vfdResult = vfd.showDialog();
            if (!vfdResult.equals("")) {
                pushToFormulaStack(vfdResult);
                orgButtons(UserField.ACCOUNTING_FUNCTION_SUM);
            }
        } else if (e.getActionCommand().equals("mittelwert")) {
            formulaArea.requestFocus();
            VfDialog vfd = new VfDialog(this, UserField.ACCOUNTING_FUNCTION_AVG, userField);

            // vfdResult = VerechnugsFunktionsDefinition ist der Term, 
            // der nach der angabe der VF zurück kommt.

            String vfdResult = vfd.showDialog();
            if (!vfdResult.equals("")) {
                pushToFormulaStack(vfdResult);
                orgButtons(UserField.ACCOUNTING_FUNCTION_SUM);
            }
            formulaArea.requestFocus();
        }

        else if (e.getActionCommand().equals("indikator")) {
            if (formulaArea.getText().equals("") || formulaArea.getText().trim().startsWith(UserField.ACCOUNTING_FUNCTION_INDI)) {
                IndicatorDialog indiDialog;
                indiDialog = new IndicatorDialog(this, userField);
                String newIndi = indiDialog.showDialog();
                if (!newIndi.equals("")) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(" ");
                    sb.append(UserField.ACCOUNTING_FUNCTION_INDI);
                    sb.append(" ( ");
                    sb.append(newIndi);
                    sb.append(" ) ");
                    clearStack();
                    pushToFormulaStack(sb.toString());
                    hashArea.setText(convertStackFormulaToOrdinaryFormula());
                    formulaArea.setText(CostingUtil.getHumanReadableFormulaString(convertStackFormulaToOrdinaryFormula(), definitions));
                    formulaArea.requestFocus();
                    orgButtons(UserField.ACCOUNTING_FUNCTION_INDI);
                }
            } else {
                JOptionPane.showMessageDialog(this, Tool3lgmConstants.getErrString("indicator_in_formula"), Tool3lgmConstants.getResString("fehler"), JOptionPane.ERROR_MESSAGE);
            }

        } else if (e.getActionCommand().equals("reference")) {
            VfDialog vfd = new VfDialog(this, UserField.ACCOUNTING_FUNCTION_REF, userField);
            String vfdResult = vfd.showDialog();
            formulaArea.requestFocus();
            if (!vfdResult.equals("")) {
                pushToFormulaStack(vfdResult);
                orgButtons(UserField.ACCOUNTING_FUNCTION_REF);
            }
        }

        else if (e.getActionCommand().equals("leaveBracket")) {
            formulaArea.requestFocus();
            pushToFormulaStack(USERFIELD_IN_FORMULA_BRACKET_LEAVE);

            leaveableBracketCounter--;
            if (leaveableBracketCounter == 0) {
                leaveBracketButton.setEnabled(false);
            }
            orgButtons(USERFIELD_IN_FORMULA_BRACKET_LEAVE);
        }

        else if (e.getActionCommand().equals("undo")) {

            String deletedElement = popFromStack();
            int termSize = term.size();

            if (deletedElement.equals(USERFIELD_IN_FORMULA_BRACKET_LEAVE)) {
                leaveableBracketCounter++;
            } else if (deletedElement.equals(Calculator.OPEN_BRACKET + "  " + Calculator.CLOSE_BRACKET)) {
                leaveableBracketCounter--;
                orgButtons("");
            } else if (deletedElement.startsWith(UserField.ACCOUNTING_FUNCTION_SUM) || deletedElement.startsWith(UserField.ACCOUNTING_FUNCTION_TWSUM)) {
                orgButtons(Calculator.OPERATOR_PLUS);
            } else

            if (Calculator.OPERATOR_SIGNS.contains(deletedElement)) {
                orgButtons(CLASSIFICATION_NUMBER);
                checkFormulaValidity();
                return;
            }
            if (termSize > 0) {
                String tmps = term.get(termSize - 1).toString();
                if (tmps.contains(" ")) {
                    tmps = tmps.substring(0, tmps.indexOf(" "));
                    orgButtons(tmps);
                } else if (tmps.contains(UserField.USERFIELD_HASH_STRING_PREFIX)) {
                    orgButtons(CLASSIFICATION_NUMBER);
                } else if (tmps.length() == 1) {
                    orgButtons(tmps);
                }
            } else {
                orgButtons(Calculator.OPERATOR_PLUS);
            }
        }

        else if (e.getActionCommand().equals("clearFormula")) {

            // Wenn der Löschenbutton betätigt wurde, werden alle Elemente des Term-stacks entfernt und die textAreas neu gefüllt (in diesem Falls mich nichts).  
            if (term != null) {
                term.clear();
            }

            String hashStringFormula = convertStackFormulaToOrdinaryFormula();
            hashArea.setText(hashStringFormula);
            formulaArea.setText(CostingUtil.getHumanReadableFormulaString(hashStringFormula, definitions));

            orgButtons(Calculator.OPERATOR_PLUS);
        }
        // Als letze Aktion nach jeder Aktion wird geprüft, ob die Formel korrekt ist.   
        checkFormulaValidity();

    }

    /**
     * Prüft die Formel auf syntaktische Korrektheit und setzt entsprechend das <code>statusLabel</code>. Über dieses <code>statusLabel</code> wird
     * angezeigt, ob die Formel korrekt ist oder nicht.
     */
    private void checkFormulaValidity() {
        if (CostingUtil.isFormulaValid(convertStackFormulaToOrdinaryFormula())) {
            statusLabel.setText(Tool3lgmConstants.getResString("formula_is_valid"));
        } else {
            statusLabel.setText(Tool3lgmConstants.getResString("formula_is_not_valid"));
        }
    }

    /**
     * Konvertiert den Stack mit seinen Elementen, die in Eingabereihenfolge vorliegen, in einen String der die Formel in der Form enthält, wie sie
     * mathematisch korrekt ist. Zusätzlich wird hier entschieden, an welcher Position der Cursor im Textfeld für die Formel angezeigt wird.
     * 
     * @return String Die Formel in menschenlesbarer Form aber in <code>UserField</code>-hashCode-schreibweise.
     */
    private String convertStackFormulaToOrdinaryFormula() {
        /*
         * Schreibt die Elemente aus dem Term-Stack in die <code>ArrayList</code> mit diesem Mechanismus kann evtl. auf den Stack ganz verzichtet
         * werden.
         */
        //die Liste wird verwendet, um die Formel in die korrekte Form zu ordnen 
        /**
         * Diese Liste beinhaltet die Formel in der korrekten Leseweise.
         */
        ArrayList<String> termList = new ArrayList<String>();
        caretPosInFormulaArea = 0;
        int insertIndex = 0;
        String partTerm = "";
        if (term != null) {
            for (int i = 0; i < term.size(); i++) {
                partTerm = term.get(i).toString();
                if (partTerm.equals("(  )")) {
                    termList.add(insertIndex, Calculator.OPEN_BRACKET);

                    //hier wird mit Absicht nicht insertIndex++ gemacht, 
                    //da somit nochmal an der selben Stelle etwas eingefügt werden kann
                    termList.add(insertIndex + 1, Calculator.CLOSE_BRACKET);

                    //Wenn eine Klammer eingefügt wird, 
                    //ist die soll der Cursor in der Mitte des Klammernpaares stehen.
                    caretPosInFormulaArea += 2;
                } else if (partTerm.equals(USERFIELD_IN_FORMULA_BRACKET_LEAVE)) {
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

    /**
     * Fügt dem <code>Stack</code> <code>term</code> ein Element an. Alle eingegebenen Elemente befinden sich in Eingabereihenfolge und nicht in
     * natürlicher Formelartiger Form auf diesem Stack.
     * 
     * @param element Der anzufügende String
     */
    private void pushToFormulaStack(final String element) {
        if (term == null) {
            term = new Stack<String>();
        }
        term.push(element);
        String hashStringFormula = convertStackFormulaToOrdinaryFormula();
        hashArea.setText(hashStringFormula);
        formelString = CostingUtil.getHumanReadableFormulaString(hashStringFormula, definitions);
        formulaArea.setText(formelString);
    }

    /**
     * Entfernt alle Elemente des <code>Stack</code>s
     */
    private void clearStack() {
        if (term != null) {
            term.clear();
        }
    }

    /**
     * Entfernt das letzte Element aus dem <code>term</code> <code>Stack</code> und gibt es zurück.
     * 
     * @return das gepoppte Element
     */
    private String popFromStack() {
        String deletetElement = "";
        if (term.size() != 0) {
            deletetElement = term.get(term.size() - 1).toString();
            term.pop();
        }
        String hashStringFormula = convertStackFormulaToOrdinaryFormula();
        hashArea.setText(hashStringFormula);
        formulaArea.setText(CostingUtil.getHumanReadableFormulaString(hashStringFormula, definitions));
        return deletetElement;
    }

    /**
     * Setzte den enabled-Status der Buttons auf true oder false. Somit soll Verhindert werden, dass mehrmal Operatoren hintereinanderer angegeben
     * werden können.
     * 
     * @param lastCommand das letze Komanndo, welches für den neuen Status der Button bestimmt. Bsp: Nach einem "+" darf nicht nochmal ein "+"
     *            betätigt werden. Übergabeparameter ist in diesem Fall das "+" als String.
     */
    private void orgButtons(final String lastCommand) {

        if (lastCommand.equals("initial")) {
            setEnableStatusOfOrdinaryButtons(false);
            buttonklammerauf.setEnabled(true);
            setEnableStatusOfVFButtons(true, false);
            buttonindikator.setEnabled(true);
            userFieldList.setEnabled(true);
            modelAttributes.setEnabled(true);
            undoButton.setEnabled(false);
            leaveBracketButton.setEnabled(false);
            clearFormulaButton.setEnabled(false);

        } else if (lastCommand.equals("withFormula")) {
            setEnableStatusOfOrdinaryButtons(true);
            buttonklammerauf.setEnabled(true);
            setEnableStatusOfVFButtons(false, true);
            buttonindikator.setEnabled(false);
            userFieldList.setEnabled(false);
            modelAttributes.setEnabled(false);

        } else if (Calculator.OPERATOR_SIGNS.contains(lastCommand) || lastCommand.equals(Calculator.OPEN_BRACKET)) {
            setEnableStatusOfOrdinaryButtons(false);
            buttonklammerauf.setEnabled(true);
            setEnableStatusOfVFButtons(true, false);
            buttonindikator.setEnabled(false);
            userFieldList.setEnabled(true);
            modelAttributes.setEnabled(true);
            //leaveBracketButton.setEnabled(true);

        } else if (lastCommand.equals(Calculator.OPEN_BRACKET) || UserField.ACCOUNTING_FUNCTIONS_SET.contains(lastCommand)) {
            setEnableStatusOfOrdinaryButtons(true);
            buttonklammerauf.setEnabled(false);
            setEnableStatusOfVFButtons(false, false);
            userFieldList.setEnabled(false);
            modelAttributes.setEnabled(false);

        } else if (lastCommand.equals(CLASSIFICATION_NUMBER)) {
            setEnableStatusOfOrdinaryButtons(true);
            buttonklammerauf.setEnabled(false);
            setEnableStatusOfVFButtons(false, false);
            userFieldList.setEnabled(false);
            modelAttributes.setEnabled(false);

        } else if (lastCommand.equals(UserField.ACCOUNTING_FUNCTION_INDI)) {
            setEnableStatusOfOrdinaryButtons(false);
            buttonklammerauf.setEnabled(false);
            setEnableStatusOfVFButtons(false, false);
        }

        // Wenn der term leer ist, kann nichts mehr rückgängig gemacht werden
        if (term != null && term.size() == 0 || term == null) {
            undoButton.setEnabled(false);
            setEnableStatusOfVFButtons(true, false);

        } else {
            //Wenn der Stack Elemente beinhaltet, kann der letzte Einfügeschritt widerrufen werden
            undoButton.setEnabled(true);
        }

        if (leaveableBracketCounter > 0) {
            leaveBracketButton.setEnabled(true);
        } else {
            leaveBracketButton.setEnabled(false);
        }

        if (isOnlyEdge) {
            setEnableStatusOfVFButtons(false, true);
        }

        //Setze den Cursor an die richtige Stelle. Speziell bei Klammerungen wichtig.
        setCaretInFormulaArea();
        formulaArea.requestFocus();
    }

    /**
     * Setzt den Editierbarkeitesstatus der Buttons für Verrechnungsfunktionen
     * 
     * @param state true, wenn die Buttons klickbar sein sollen false, wenn die Buttons nicht klickbar sein sollen
     * @param ignoreTypeElement true, wenn wirklich von allen Buttons die Enabled-einstellung geändert werden soll - unabhänig ob es ein Knoten oder
     *            eine Kante ist.
     */
    private void setEnableStatusOfVFButtons(final boolean state, final boolean ignoreTypeElement) {
        // Für den Fall, dass eine Formel für eine Elementklasse defijniert wird, sind diese Buttons anzuzeigen
        if (isOnlyEdge && ignoreTypeElement || !isOnlyEdge) {
            buttonmax.setEnabled(state);
            buttonmin.setEnabled(state);
            buttonmittelwert.setEnabled(state);
            buttonteilwertsumme.setEnabled(state);
            buttonsum.setEnabled(state);
            buttonMult.setEnabled(state);

            buttonindikator.setEnabled(state);
        }

        if (isOnlyEdge) {
            buttonReference.setEnabled(true);
        } else {
            buttonReference.setEnabled(false);
        }
    }

    /**
     * Setzt den Editierbarkeitesstatus der Buttons für die normale Operatoren ( +,-,*,/ )
     * 
     * @param state true, wenn die Buttons klickbar sein sollen false, wenn die Buttons nicht klickbar sein sollen
     * @param state
     */
    private void setEnableStatusOfOrdinaryButtons(final boolean state) {
        buttonplus.setEnabled(state);
        buttonminus.setEnabled(state);
        buttonmult.setEnabled(state);
        buttondiv.setEnabled(state);
    }

    /**
     * Setzt den Cursor in der TextArea, die die Formel darstellt. Der Cusor soll dem Benutzer anzeigen, an welcher Stelle er sich in der Formel
     * befindet und wo als nächstes ein Element eingefügt wird. Vorgehen:
     */
    private void setCaretInFormulaArea() {
        formulaArea.setCaretPosition(caretPosInFormulaArea);
    }
}