package de.imise.tool3lgm.graphtools.userfield.dialog.declaration;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.GraphDocument.DATA_CHANGED;
import static de.imise.tool3lgm.graphtools.userfield.CostingUtil.getDisplayableStyleName;
import static de.imise.tool3lgm.graphtools.userfield.UserField.Style.CHECK_BOX;
import static de.imise.tool3lgm.graphtools.userfield.UserField.Style.CLASSIFICATION_NUMBER;
import static de.imise.tool3lgm.graphtools.userfield.UserField.Style.CLASSIFICATION_NUMBER_FORMULA;
import static de.imise.tool3lgm.graphtools.userfield.UserField.Style.COMBO_BOX;
import static de.imise.tool3lgm.graphtools.userfield.UserField.Style.HYPERLINK;
import static de.imise.tool3lgm.graphtools.userfield.UserField.Style.ID;
import static de.imise.tool3lgm.graphtools.userfield.UserField.Style.MULTI_LINE;
import static de.imise.tool3lgm.graphtools.userfield.UserField.Style.RADIO_BUTTON;
import static de.imise.tool3lgm.graphtools.userfield.UserField.Style.SEPARATOR;
import static de.imise.tool3lgm.graphtools.userfield.UserField.Style.SINGLE_LINE;
import static de.imise.tool3lgm.graphtools.userfield.dialog.declaration.UserFieldDeclarationImportExportHandler.exportDefinitions;
import static de.imise.tool3lgm.graphtools.userfield.dialog.declaration.UserFieldDeclarationImportExportHandler.importDefinitions;
import static de.imise.tool3lgm.graphtools.userfield.dialog.definition.UserFieldDefinitionDialog.OK;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.dialog.tools.EasyComponents;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.UserFieldTarget;
import de.imise.tool3lgm.graphtools.userfield.dialog.definition.UserFieldDefinitionDialog;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.util.event.DoubleClickListener;
import de.imise.util.swing.component.AlphabeticalComboBox;
import de.imise.util.swing.dialog.AbstractSizeAndPositionRestoringDialog;
import de.imise.util.swing.dialog.MultipleOptionPane;

/**
 * @author Thomas Rudert Dialog to create, edit, remove, import and export user-definied property-fields for model-elements
 */
public final class UserFieldDeclarationDialog extends AbstractSizeAndPositionRestoringDialog implements ActionListener, ListSelectionListener {

    /** combobox to select a model-class */
    private UserFieldDeclarationDialogClassComboBox classComboBox;

    /**
     * list with the defined userFields Es wird hier keine AplphabeticalJList genutzt, weil die Elemente in der Reinhenfolge angezeigt werden sollen,
     * die der User vorgibt.
     */
    private UserFieldDeclarationDialogFieldList fieldList;

    /** button to edit a userField */
    private JButton editButton;

    /** button to remove a userField */
    private JButton deleteButton;

    /** button to add new userField */
    private JButton newButton;

    /** Buttons zum Vertauschen der Reihenfolge von Attributen */
    private JButton upButton, downButton;

    /**
     * ComboBox mit der die Art des neuen benutzerdefinierten Eigenschaftsfeldes festgelegt wird
     */
    private AlphabeticalComboBox userFieldTypeComboBox;

    /** Die <code>GDCollection</code> in dessen Kontext gerade gearbeitet wird. */
    private final GDCollection gdcol;

    /** Speichert alle defnierten benutzerdefinierten Eigenschaftsfelder */
    private final UserFieldDefinitions definitions;

    /**
     * Clone der Definitionen vor allen Änderungen. Wird beim Abbrechen auf diese Defnition zurück gesetzt.
     */
    private final UserFieldDefinitions oldUserFieldDefionitions;

    private static Dimension defaultSize = null;

    /**
     * return-value of dialog <br>
     * <ul>
     * <li>-1 = cancel</li>
     * <li>0 = ok but no changes</li>
     * <li>1 = ok and changes</li>
     * </ul>
     */
    private int returnValue = 0;

    /**
     * Liste mit allen UserFields, die gelöscht wurden. Wird der Dialog mit OK verlassen, werden bei allen <code>UserFieldTarget</code>s die Werte
     * dieser UserFields *unwiederbringlich* gelöscht.
     */
    private final ArrayList<UserField> removedUserFields = new ArrayList<UserField>();

    /**
     * ist true, wenn eine Warnung angezeigt werden soll, dass die Werte einer Kennzahl gelöscht werden - sonst false.
     */
    private static boolean showWarningForDeletingUserFields = true;

    /**
     * @param owner
     * @throws java.awt.HeadlessException
     */
    private UserFieldDeclarationDialog(final Frame owner, final GDCollection gdcol) throws HeadlessException {
        super(owner, Tool3lgmConstants.getResString("userfields"), true);
        this.gdcol = gdcol;
        definitions = gdcol.getUserFieldDefinitions();
        oldUserFieldDefionitions = (UserFieldDefinitions) definitions.clone();
        init();
    }

    /**
     * show the dialog
     *
     * @param owner Frame which owns the dialog
     * @param doc actual which give the context of <code>GDCollection</code> and <code>UserFieldDefinitions</code>
     * @return -1 if cancel was selected, 0 if ok was selected but k was changed by user, 1 if user made changes and pressed ok-button
     */
    public static int showDialog(final Frame owner, final GDCollection gdcol) {
        UserFieldDeclarationDialog dialog = new UserFieldDeclarationDialog(owner, gdcol);
        dialog.setVisible(true);
        return dialog.returnValue;
    }

    /**
     * Initialiert die <code>ComboBox</code> mit den Klasseneinträgen, für die <code>UserField</code> s definiert werden können.
     */
    private void createClassComboBox() {
        classComboBox = new UserFieldDeclarationDialogClassComboBox(13);
        classComboBox.addActionListener(this);
    }

    /**
     * Initialisiert den Dialog.
     */
    private void init() {
        JButton button;
        JPanel panel1;
        JPanel panel2;
        JPanel panel3;
        JScrollPane scrollPane;
        LayoutManager layout;
        GridBagConstraints constraints;
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                //Schließen üer das Kreuz = Cancel
                UserFieldDeclarationDialog.this.actionPerformed(new ActionEvent(UserFieldDeclarationDialog.this, e.getID(), "cancel"));
            }
        });
        Container pane = getContentPane();
        pane.setLayout(new BorderLayout());
        createClassComboBox();
        fieldList = new UserFieldDeclarationDialogFieldList(definitions);

        panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel1.add(new JLabel(Tool3lgmConstants.getResString("userFieldDialog_class") + ": "));
        panel1.add(classComboBox);

        pane.add(panel1, BorderLayout.NORTH);

        panel1 = new JPanel(new BorderLayout());
        panel1.add(new JLabel(Tool3lgmConstants.getResString("userFieldDialog_fields") + ":"), BorderLayout.NORTH);
        scrollPane = new JScrollPane(fieldList);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panel1.add(scrollPane, BorderLayout.CENTER);

        layout = new GridBagLayout();
        panel2 = new JPanel(layout);
        constraints = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);

        newButton = createButton("new");
        constraints.gridwidth = 1;
        //Anstelle des Buttons die ComboBox einfügen

        userFieldTypeComboBox = new AlphabeticalComboBox();
        userFieldTypeComboBox.setEnabled(false);
        panel2.add(userFieldTypeComboBox, constraints);

        constraints.gridx++;
        constraints.gridwidth = 3;
        panel2.add(newButton, constraints);
        constraints.gridx--;
        editButton = createDisabledButton("editButtonText");
        constraints.gridy = 1;
        constraints.gridwidth = 4;
        panel2.add(editButton, constraints);

        deleteButton = createDisabledButton("delete");
        constraints.gridy = 2;
        constraints.gridwidth = 4;
        panel2.add(deleteButton, constraints);

        downButton = createDisabledButton("runter2.gif");
        constraints.gridwidth = 2;
        constraints.gridy = 3;
        panel2.add(downButton, constraints);

        upButton = createDisabledButton("hoch2.gif");
        constraints.gridwidth = 2;
        constraints.gridx = 2;
        constraints.gridy = 3;
        panel2.add(upButton, constraints);
        panel3 = new JPanel();
        panel3.setBorder(null);
        panel2.setBorder(null);
        panel3.add(panel2);
        panel1.add(panel3, BorderLayout.EAST);
        panel1.setBorder(new EmptyBorder(5, 5, 5, 5));
        pane.add(panel1, BorderLayout.CENTER);

        panel1 = new JPanel();
        layout = new BoxLayout(panel1, BoxLayout.X_AXIS);
        panel1.setLayout(layout);
        panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        button = createButton("importButtonText");
        panel2.add(button);

        button = createButton("exportButtonText");
        panel2.add(button);
        panel1.add(panel2);

        panel2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        button = createButton("ok");
        panel2.add(button);
        button = createButton("cancel");
        panel2.add(button);
        panel1.add(panel2);
        pane.add(panel1, BorderLayout.SOUTH);
        if (defaultSize == null) {
            pack();
            defaultSize = getSize();
        }
        //ersten Eintrag selektieren (= globale Variablen)
        fieldList.addListSelectionListener(this);
        fieldList.addMouseListener(new DoubleClickListener(editButton));
        classComboBox.restoreSelection();
    }

    /**
     * Setzt in Abhängigkeit von der ausgewählten Klasse der <code>classComboBox</code> nur die Arten von neu anlgebaren benutzerdefinierten
     * Eigenschaftenfeldern, die man für die jeweilige Klasse anlegen kann.<br>
     * Für Kantenklassen kann man als einziges Verteilungsgewichte anlegen. Für Kanten sind aber keine Kennzahlen oder Kennzahlformeln vorgesehen. Im
     * Moment auch nicht für Assoziationsklassen (z. B. <code>KommBeziehung</code>), da man das dür das Umsetzen von Ansgars Kostenmodell nicht
     * braucht.
     */
    private void updateUserFieldTypeComboBox() {

        userFieldTypeComboBox.setEnabled(true);
        userFieldTypeComboBox.removeAllItems();

        addType(CHECK_BOX);
        addType(COMBO_BOX);
        addType(HYPERLINK);
        addType(MULTI_LINE);
        addType(RADIO_BUTTON);
        addType(SEPARATOR);
        addType(SINGLE_LINE);
        addType(ID);

        // Die Kennzahl kann immer zu Auswahl gestellt werden.
        //Nur wenn es sich um eine Modellvariable handelt, darf die Kennzahlformel nicht angeboten werden, sonst schon
        addType(CLASSIFICATION_NUMBER);
        if (!classComboBox.isGlobalUserFieldClassSelected()) {
            addType(CLASSIFICATION_NUMBER_FORMULA);
        }
    }

    private void addType(final UserField.Style style) {
        userFieldTypeComboBox.addItem(style, getDisplayableStyleName(style));
    }

    private ActionEvent lastActionEvent = null;

    private boolean is(final String commandKey) {
        return lastActionEvent.getActionCommand().equals(commandKey);
    }

    private boolean is(final Object commandSource) {
        return lastActionEvent.getSource() == commandSource;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        lastActionEvent = e;
        if (is("ok")) {
            dispose();
            gdcol.removeUserFieldValues(removedUserFields);
            if (!UserProperties.isShowUserDefinedPropertiesInModelBrowser()) {
                return;
            }
            GDCollection gdcoll = definitions.getCollection();
            if (gdcoll != null) {
                gdcoll.getMainGraphDocument().distributeEvent(DATA_CHANGED);
            }
        } else if (is("cancel")) {
            if (returnValue != 0) {
                if (JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(this, Tool3lgmConstants.getResString("userFieldDialog_warning_message"), Tool3lgmConstants.getResString("userFieldDialog_warning"), JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE)) {
                    return;
                }
            }
            definitions.getCollection().setUserFieldDefinitions(oldUserFieldDefionitions);
            returnValue = -1;
            dispose();
        } else if (is("importButtonText")) {
            if (importDefinitions(this, definitions)) {
                fieldList.update(classComboBox.getSelectedClass());
                returnValue = 1;
            }
        } else if (is("exportButtonText")) {
            exportDefinitions(this, definitions);
        } else if (is(newButton)) {
            //Typ der seleketierten Klasse holen
            Class<? extends UserFieldTarget> selectedClass = classComboBox.getSelectedClass();
            //Definitionseditor für das neue userField anzeigen
            UserField.Style style = (UserField.Style) userFieldTypeComboBox.getSelectedObject();
            if (style == null) {
                JOptionPane.showMessageDialog(this, Tool3lgmConstants.getErrString("choose_type_first"), Tool3lgmConstants.getResString("fehler"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            //jetzt kann nur noch ein Knoten- oder Kantentyp selektiert sein
            //-> neues userField für die selektierte Klassenart anlegen
            UserField userField = new UserField(selectedClass, style, definitions);
            //das neu erzeugte UserField sofort zur ausgewählten Klasse hinzufügne
            definitions.add(userField);
            //solange den Dialog zur Definition der Eigenschaften des neuen UserFields zeigen, bis nur konsitente Werte eingegeben wurden
            int userDefinitionDialogReturnValue;
            do {
                userDefinitionDialogReturnValue = UserFieldDefinitionDialog.showDialog(this, userField, gdcol);
            } while (userDefinitionDialogReturnValue == OK && definitions.hasCrossReferences());

            //wenn der Dialog über OK verlassen wurde
            if (userDefinitionDialogReturnValue == OK) {
                //das neue UserField anzeigen
                fieldList.addEntry(userField);
                returnValue = 1;
                //den Definitions sagen, dass sich was geändert hat
                definitions.getCollection().getUserFieldDefinitions().initReset();
                //wenn die Defnition der neuen Kennzahl oder Formel abgebrochen wurde
            } else {
                //wieder aus den Definitions entfernen
                definitions.remove(userField);
            }
        } else if (is(editButton)) {
            UserField userField = fieldList.getSelected(); // null-Check kann man sich sparen, weil die Buttons deaktiviert sind, wenn nichts selektiert ist
            //die alte Formel des UserFields holen (die ist nur bei UserFields mit dem Formula-Style nicht null, aber das ist egal)
            String oldFormula = userField.getFormula();
            do {
                gdcol.getUserFieldDefinitions().setConsistencyUnknown();
                //Definitionseditor für das zu bearbeitende userField anzeigen
                if (UserFieldDefinitionDialog.showDialog(this, userField, gdcol) == OK) {
                    fieldList.refreshSelected();
                    returnValue = 1;
                } else {
                    // Wenn der Definitionsdialog für Kennzahlformeln abgebrochen wurde, wird die alte Formel zurückgesetzt.
                    userField.setFormula(oldFormula);
                    break;
                }
                //den Formel-Editor-Dialog kann man solange nicht verlassen, wie sich Formeln im Kreis referenzieren
            } while (definitions.hasCrossReferences());

        } else if (is(deleteButton)) {
            UserField userField = fieldList.getSelected(); // null-Check kann man sich sparen, weil die Buttons deaktiviert sind, wenn nichts selektiert ist
            if (reallyDelete()) {
                //Alle gelöschten UserFields merken
                removedUserFields.addAll(definitions.remove(userField));
                fieldList.update(classComboBox.getSelectedClass());
                returnValue = -1;
            }
        } else if (is(upButton)) {
            fieldList.moveUp();
            returnValue = 1;
        } else if (is(downButton)) {
            fieldList.moveDown();
            returnValue = 1;
        } else if (is(classComboBox)) {
            fieldList.update(classComboBox.getSelectedClass());
            updateUserFieldTypeComboBox();
        }
    }

    private boolean reallyDelete() {
        //Bevor ein userField gelöscht wird, wird nochmal eine Sicherheitsabfrage gestellt.
        //Wenn die Siocherheitsabfrage nicht bestätigt wird, wird cancel true. D.h. das Löschen wird abgebrochen.
        boolean delete = true;
        if (showWarningForDeletingUserFields) {
            String[] frage = {
                    getResString("dontShowAgain")
            };
            Object[] result = MultipleOptionPane.showCheckBoxOptionDialog(this, getResString("warnung"), getResString("allValuesWouldBeDeleted"), frage);
            if (result == null) { //Cancel gedrückt
                delete = false;
            } else if (result[0] != null) {
                showWarningForDeletingUserFields = false;
            }
        }
        return delete;
    }

    private JButton createButton(final String resKey) {
        return EasyComponents.createButton(this, resKey);
    }

    private JButton createDisabledButton(final String resKey) {
        JButton button = createButton(resKey);
        button.setEnabled(false);
        return button;
    }

    /**
     * @return
     */
    public UserFieldDefinitions getModifiedDefinitions() {
        return definitions;
    }

    @Override
    public void valueChanged(final ListSelectionEvent e) {
        if (e.getSource() == fieldList) {
            setButtonsEnabled(!fieldList.isSelectionEmpty());
        }
    }

    private void setButtonsEnabled(final boolean enabled) {
        editButton.setEnabled(enabled);
        deleteButton.setEnabled(enabled);
        downButton.setEnabled(enabled);
        upButton.setEnabled(enabled);
    }

    @Override
    public Dimension getDefaultSize() {
        return defaultSize;
    }

}
