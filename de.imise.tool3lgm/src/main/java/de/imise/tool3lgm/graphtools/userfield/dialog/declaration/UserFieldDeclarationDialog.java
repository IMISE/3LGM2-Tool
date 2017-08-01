package de.imise.tool3lgm.graphtools.userfield.dialog.declaration;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.model.GraphDocument.DATA_CHANGED;
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

import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.UserFieldTarget;
import de.imise.tool3lgm.graphtools.userfield.dialog.definition.UserFieldDefinitionDialog;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.util.event.DoubleClickListener;
import de.imise.util.swing.dialog.MultipleOptionPane;

/**
 * @author Thomas Rudert Dialog to create, edit, remove, import and export user-definied property-fields for model-elements
 */
public final class UserFieldDeclarationDialog extends AbstractUserFieldDeclarationDialog implements ActionListener, ListSelectionListener {

    /** Die <code>GDCollection</code> in dessen Kontext gerade gearbeitet wird. */
    private final GDCollection gdcoll;

    /** Speichert alle defnierten benutzerdefinierten Eigenschaftsfelder */
    private final UserFieldDefinitions definitions;

    /**
     * Clone der Definitionen vor allen Änderungen. Wird beim Abbrechen auf diese Defnition zurück gesetzt.
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
    private UserFieldDeclarationDialog(final Frame owner, final GDCollection gdcoll) throws HeadlessException {
        super(owner, gdcoll.getUserFieldDefinitions());
        this.gdcoll = gdcoll;
        definitions = gdcoll.getUserFieldDefinitions();
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
        fieldList.addListSelectionListener(this);
        fieldList.addMouseListener(new DoubleClickListener(editButton));
        classComboBox.addActionListener(this);
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

    private boolean is(final Object commandSource) {
        return lastActionEvent.getSource() == commandSource;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        lastActionEvent = e;
        if (is(okButton)) {
            dispose();
            gdcoll.removeUserFieldValues(removedUserFields);
            if (!UserProperties.isShowUserDefinedPropertiesInModelBrowser()) {
                return;
            }
            GDCollection gdcoll = definitions.getCollection();
            if (gdcoll != null) {
                gdcoll.getMainGraphDocument().distributeEvent(DATA_CHANGED);
            }
        } else if (is(cancelButton)) {
            if (returnValue != 0) {
                if (JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(this, Tool3lgmConstants.getResString("userFieldDialog_warning_message"), Tool3lgmConstants.getResString("userFieldDialog_warning"), JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE)) {
                    return;
                }
            }
            definitions.getCollection().setUserFieldDefinitions(oldUserFieldDefionitions);
            returnValue = -1;
            dispose();
        } else if (is(importButton)) {
            if (importDefinitions(this, definitions)) {
                fieldList.update(classComboBox.getSelectedClass());
                returnValue = 1;
            }
        } else if (is(exportButton)) {
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
                userDefinitionDialogReturnValue = UserFieldDefinitionDialog.showDialog(this, userField, gdcoll);
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
                gdcoll.getUserFieldDefinitions().setConsistencyUnknown();
                //Definitionseditor für das zu bearbeitende userField anzeigen
                if (UserFieldDefinitionDialog.showDialog(this, userField, gdcoll) == OK) {
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
            if (reallyDelete()) {
                // null-Check kann man sich sparen, weil die Buttons deaktiviert sind, wenn nichts selektiert ist
                int[] selectedIndices = fieldList.getSelectedIndices();
                for (int selectedIndex : selectedIndices) {
                    UserField userField = fieldList.get(selectedIndex);
                    //das aktuelle UserField kann schon gelöscht worden sein, durch das löschen eines vorhergehenden in der Schleife
                    if (!removedUserFields.contains(userField)) {
                        //Alle gelöschten UserFields merken
                        removedUserFields.addAll(definitions.remove(userField));
                    }
                }
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

    /**
     * @return
     */
    public UserFieldDefinitions getModifiedDefinitions() {
        return definitions;
    }

    @Override
    public void valueChanged(final ListSelectionEvent e) {
        if (e.getSource() == fieldList) {
            int selectionCount = fieldList.getSelectedIndices().length;
            setButtonsEnabled(selectionCount == 1, selectionCount > 1);
        }
    }

    private void setButtonsEnabled(final boolean isSingleUserFieldSelected, final boolean isMultiUserFieldSelected) {
        editButton.setEnabled(isSingleUserFieldSelected);
        deleteButton.setEnabled(isSingleUserFieldSelected || isMultiUserFieldSelected);
        downButton.setEnabled(isSingleUserFieldSelected);
        upButton.setEnabled(isSingleUserFieldSelected);
    }

}
