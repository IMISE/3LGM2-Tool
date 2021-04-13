package de.imise.tool3lgm.graphtools.userfield.dialog.declaration;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType.DATA_CHANGED;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.CHECK_BOX;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.COMBO_BOX;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.FORMULA;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.HYPERLINK;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.ID;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.MULTI_LINE;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.NUMBER;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.RADIO_BUTTON;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.SINGLE_LINE;
import static de.imise.tool3lgm.graphtools.userfield.dialog.declaration.UserFieldDeclarationImportExportHandler.exportDefinitions;
import static de.imise.tool3lgm.graphtools.userfield.dialog.declaration.UserFieldDeclarationImportExportHandler.importDefinitions;
import static de.imise.tool3lgm.graphtools.userfield.dialog.definition.UserFieldDefinitionDialog.OK;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SHOW_USER_DEFINED_PROPERTIES_IN_MODEL_BROWSER;

import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.userfield.definition.UserField;
import de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldTarget;
import de.imise.tool3lgm.graphtools.userfield.dialog.definition.UserFieldDefinitionDialog;
import de.imise.util.event.DoubleClickListener;
import de.imise.util.swing.dialog.MultipleOptionPane;

/**
 * @author Thomas Rudert Dialog to create, edit, remove, import and export
 *         user-definied property-fields for model-elements
 */
public final class UserFieldDeclarationDialog extends AbstractUserFieldDeclarationDialog implements ActionListener, TreeSelectionListener {

    /**
     * Die <code>GDCollection</code> in dessen Kontext gerade gearbeitet wird.
     */
    private final GDCollection gdcoll;

    /** Speichert alle defnierten benutzerdefinierten Eigenschaftsfelder */
    private final UserFieldDefinitions definitions;

    /**
     * Clone der Definitionen vor allen Änderungen. Wird beim Abbrechen auf
     * diese Defnition zurück gesetzt.
     */
    private final UserFieldDefinitions oldUserFieldDefionitions;

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
     * Liste mit allen UserFields, die gelöscht wurden. Wird der Dialog mit OK
     * verlassen, werden bei allen <code>UserFieldTarget</code>s die Werte
     * dieser UserFields *unwiederbringlich* gelöscht.
     */
    private final List<UserField> removedUserFields = new ArrayList<>();

    /**
     * ist true, wenn eine Warnung angezeigt werden soll, dass die Werte einer
     * Kennzahl gelöscht werden - sonst false.
     */
    private static boolean showWarningForDeletingUserFields = true;

    /**
     * @param owner
     * @throws java.awt.HeadlessException
     */
    private UserFieldDeclarationDialog(final Frame owner, final GDCollection gdcoll) throws HeadlessException {
        super(owner, gdcoll.getUserFieldDefinitions());
        this.gdcoll = gdcoll;
        definitions = gdcoll.getUserFieldDefinitions();
        oldUserFieldDefionitions = definitions.clone();
        init();
        restoreSizeAndPosition();
    }

    /**
     * show the dialog
     *
     * @param owner Frame which owns the dialog
     * @param doc actual which give the context of <code>GDCollection</code> and
     *            <code>UserFieldDefinitions</code>
     * @return -1 if cancel was selected, 0 if ok was selected but k was changed
     *         by user, 1 if user made changes and pressed ok-button
     */
    public static int showDialog(final Frame owner, final GDCollection gdcoll) {
        UserFieldDeclarationDialog dialog = new UserFieldDeclarationDialog(owner, gdcoll);
        dialog.setVisible(true);
        return dialog.returnValue;
    }

    /**
     * Initialisiert den Dialog.
     */
    private void init() {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                //Schließen üer das Kreuz = Cancel
                cancelButton.doClick();
            }
        });
        //ersten Eintrag selektieren (= globale Variablen)
        declarationTree.addTreeSelectionListener(this);
        declarationTree.addMouseListener(new DoubleClickListener(editButton));
        classComboBox.addActionListener(this);
        classComboBox.restoreSelection();
    }

    /**
     * Setzt in Abhängigkeit von der ausgewählten Klasse der
     * <code>classComboBox</code> nur die Arten von neu anlgebaren
     * benutzerdefinierten Eigenschaftenfeldern, die man für die jeweilige
     * Klasse anlegen kann.<br>
     * Für Kantenklassen kann man als einziges Verteilungsgewichte anlegen. Für
     * Kanten sind aber keine Kennzahlen oder Kennzahlformeln vorgesehen. Im
     * Moment auch nicht für Assoziationsklassen (z. B.
     * <code>KommBeziehung</code>), da man das dür das Umsetzen von Ansgars
     * Kostenmodell nicht braucht.
     */
    private void updateUserFieldTypeComboBox() {
        userFieldTypeComboBox.removeAllItems();
        if (!classComboBox.isGlobalUserFieldClassSelected()) {

            addStyleCategory("STYLE_TYPE_TEXT");
            addStyle(SINGLE_LINE);
            addStyle(MULTI_LINE);

            addStyleCategory("STYLE_TYPE_LIST");
            addStyle(CHECK_BOX);
            addStyle(COMBO_BOX);
            addStyle(RADIO_BUTTON);
        }

        //Models have not an property dialog to present the properties
        addStyleCategory("STYLE_TYPE_ACCOUNTING");
        // Die Kennzahl kann immer zu Auswahl gestellt werden.
        //Nur wenn es sich um eine Modellvariable handelt, darf die Kennzahlformel nicht angeboten werden, sonst schon
        addStyle(NUMBER);
        if (!classComboBox.isGlobalUserFieldClassSelected()) {
            addStyle(FORMULA);

            addStyleCategory("STYLE_TYPE_SPECIAL");
            addStyle(HYPERLINK);
            addStyle(ID);
        }
    }

    /**
     * @param style
     */
    private void addStyle(final Style style) {
        userFieldTypeComboBox.addObject(style);
    }

    /**
     * @param resKey
     */
    private void addStyleCategory(final String resKey) {
        String separatorName = getResString(resKey);
        userFieldTypeComboBox.addSeparator(separatorName);
    }

    private ActionEvent lastActionEvent = null;

    private boolean is(final Object commandSource) {
        return lastActionEvent.getSource() == commandSource;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        lastActionEvent = e;
        if (is(okButton)) {
            dispose();
            gdcoll.removeUserFieldValues(removedUserFields);
            if (OPTION_SHOW_USER_DEFINED_PROPERTIES_IN_MODEL_BROWSER.isNot()) {
                return;
            }
            GDCollection gdcoll = definitions.getCollection();
            if (gdcoll != null) {
                gdcoll.getMainDoc().distributeEvent(DATA_CHANGED);
            }
        } else if (is(cancelButton)) {
            if (returnValue != 0) {
                if (JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(this, getResString("userFieldDialog_warning_message"), getResString("userFieldDialog_warning"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE)) {
                    return;
                }
            }
            definitions.getCollection().setUserFieldDefinitions(oldUserFieldDefionitions);
            returnValue = -1;
            dispose();
        } else if (is(importButton)) {
            if (importDefinitions(this, definitions)) {
                declarationTree.update(classComboBox.getSelectedClass());
                returnValue = 1;
            }
        } else if (is(exportButton)) {
            exportDefinitions(this, definitions);
        } else if (is(newButton)) {
            //Typ der seleketierten Klasse holen
            Class<? extends UserFieldTarget> selectedClass = classComboBox.getSelectedClass();
            //Definitionseditor für das neue userField anzeigen
            UserField.Style style = userFieldTypeComboBox.getSelectedObject();
            if (style == null) {
                JOptionPane.showMessageDialog(this, getResString("userFieldDeclarationDialog_chooseType"), getResString("fehler"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            //jetzt kann nur noch ein Node- oder Kantentyp selektiert sein
            //-> neues userField für die selektierte Klassenart anlegen
            UserField userField = new UserField(selectedClass, style);
            //das neu erzeugte UserField sofort zur ausgewählten Klasse hinzufügne
            definitions.addUserField(userField);
            //solange den Dialog zur Definition der Eigenschaften des neuen UserFields zeigen, bis nur konsitente Werte eingegeben wurden
            int userDefinitionDialogReturnValue;
            do {
                userDefinitionDialogReturnValue = UserFieldDefinitionDialog.showDialog(this, userField, gdcoll);
            } while (userDefinitionDialogReturnValue == OK && definitions.hasCrossReferences());

            //wenn der Dialog über OK verlassen wurde
            if (userDefinitionDialogReturnValue == OK) {
                //das neue UserField anzeigen
                declarationTree.addUserField(userField);
                returnValue = 1;
                //den Definitions sagen, dass sich was geändert hat
                definitions.getCollection().getUserFieldDefinitions().initReset();
                //wenn die Defnition der neuen Kennzahl oder Formel abgebrochen wurde
            } else {
                //wieder aus den Definitions entfernen
                definitions.remove(userField);
            }
        } else if (is(editButton)) {
            UserField userField = declarationTree.getSelectedUserField(); // null-Check kann man sich sparen, weil die Buttons deaktiviert sind, wenn nichts selektiert ist
            //die alte Formel des UserFields holen (die ist nur bei UserFields mit dem Formula-Style nicht null, aber das ist egal)
            String oldFormula = userField.getFormula();
            do {
                gdcoll.getUserFieldDefinitions().setConsistencyUnknown();
                //Definitionseditor für das zu bearbeitende userField anzeigen
                if (UserFieldDefinitionDialog.showDialog(this, userField, gdcoll) == OK) {
                    declarationTree.refresh();
                    returnValue = 1;
                } else {
                    // Wenn der Definitionsdialog für Kennzahlformeln abgebrochen wurde, wird die alte Formel zurückgesetzt.
                    userField.setFormula(oldFormula);
                    break;
                }
                //den Formel-Editor-Dialog kann man solange nicht verlassen, wie sich Formeln im Kreis referenzieren
            } while (definitions.hasCrossReferences());

        } else if (is(deleteButton)) {
            if (reallyDelete()) {
                declarationTree.deleteSelected();
                returnValue = -1;
            }
        } else if (is(upButton)) {
            declarationTree.moveUp();
            returnValue = 1;
        } else if (is(downButton)) {
            declarationTree.moveDown();
            returnValue = 1;
        } else if (is(classComboBox)) {
            declarationTree.update(classComboBox.getSelectedClass());
            updateUserFieldTypeComboBox();
        }
    }

    private boolean reallyDelete() {
        //Bevor ein userField gelöscht wird, wird nochmal eine Sicherheitsabfrage gestellt.
        //Wenn die Siocherheitsabfrage nicht bestätigt wird, wird cancel true. D.h. das Löschen wird abgebrochen.
        boolean delete = true;
        if (showWarningForDeletingUserFields) {
            Boolean answer = MultipleOptionPane.showSingleCheckboxDialog(this, getResString("warnung"), getResString("userFieldDeclarationDialog_allValuesWillBeDeleted"), getResString("dont_ask_again"), false);
            if (answer != null) { // OK wurde gedrückt
                showWarningForDeletingUserFields = answer;
            } else { //Abbrechen oder Schließen wurde gedrückt
                delete = false;
            }
        }
        return delete;
    }

    /**
     * @return
     */
    public UserFieldDefinitions getModifiedDefinitions() {
        return definitions;
    }

    @Override
    public void valueChanged(final TreeSelectionEvent e) {
        if (e.getSource() == declarationTree) {
            TreePath[] selectionPaths = declarationTree.getSelectionPaths();
            boolean isSelection = false;
            boolean isSingleUserFieldSelected = false;
            if (selectionPaths.length > 0) {
                isSelection = true;
                if (selectionPaths.length > 1) {

                }

            }
            setButtonsEnabled(isSelection, isSingleUserFieldSelected);
        }
    }

    private void setButtonsEnabled(final boolean isSelection, final boolean isSingleUserFieldSelected) {
        editButton.setEnabled(isSingleUserFieldSelected);
        deleteButton.setEnabled(isSelection);
        downButton.setEnabled(isSingleUserFieldSelected);
        upButton.setEnabled(isSingleUserFieldSelected);
    }

}
