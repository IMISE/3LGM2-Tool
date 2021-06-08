package de.imise.tool3lgm.graphtools.userfield.dialog.declaration;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType.DATA_CHANGED;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.CHECK_BOX;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.COMBO_BOX;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.FORMULA;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.GROUP;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.HYPERLINK;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.ID;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.MULTI_LINE;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.NUMBER;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.RADIO_BUTTON;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.SEPARATOR;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.SINGLE_LINE;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.SUBTYPE;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.TAB;
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

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.imise.tool3lgm.graphtools.dialog.element.PreviewElementPropertyDialogCreator;
import de.imise.tool3lgm.graphtools.metamodel.CoreMetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
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
public final class UserFieldDeclarationDialog extends AbstractUserFieldDeclarationDialog implements ActionListener, ListSelectionListener {

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
        restoreSizeAndPosition(-1, 500);
        updateOptionsPanel();
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
        fieldList.addListSelectionListener(this);
        fieldList.addMouseListener(new DoubleClickListener(editButton));
        classComboBox.addActionListener(this);
        classComboBox.restoreSelection();

        //previewButton
        String previewButtonText = previewButton.getText(); //the text is set by the superclass
        AbstractAction previewButtonAction = new AbstractAction(previewButtonText) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                showPreview();
            }

            @Override
            public boolean isEnabled() {
                return isPreviewEnabeld();
            }
        };
        previewButton.setAction(previewButtonAction);
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
        boolean isShowGlobalUserFields = classComboBox.isGlobalUserFieldClassSelected();
        if (!isShowGlobalUserFields) {
            addStyleCategory("STYLE_TYPE_TEXT");
            addStyle(SINGLE_LINE);
            addStyle(MULTI_LINE);

            addStyleCategory("STYLE_TYPE_SELECT");
            addStyle(CHECK_BOX);
            addStyle(COMBO_BOX);
            addStyle(RADIO_BUTTON);
        }

        //Models have not an property dialog to present the properties
        addStyleCategory("STYLE_TYPE_ACCOUNTING");
        // Die Kennzahl kann immer zu Auswahl gestellt werden.
        //Nur wenn es sich um eine Modellvariable handelt, darf die Kennzahlformel nicht angeboten werden, sonst schon
        addStyle(NUMBER);
        if (!isShowGlobalUserFields) {
            addStyle(FORMULA);

            addStyleCategory("STYLE_TYPE_SPECIAL");
            addStyle(HYPERLINK);
            addStyle(ID);
            if (classComboBox.isNodeClassSelected()) {
                addStyle(SUBTYPE);
            }

            addStyleCategory("STYLE_TYPE_VIEW");
            addStyle(TAB);
            addStyle(GROUP);
            addStyle(SEPARATOR);
        }
        if (lastSelectedUserFieldStyle == null || isShowGlobalUserFields) {
            lastSelectedUserFieldStyle = NUMBER;
        }
        userFieldTypeComboBox.setSelectedObject(lastSelectedUserFieldStyle);
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

    /**
     *
     */
    private ActionEvent lastActionEvent = null;

    /**
     * @param commandSource
     * @return
     */
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
                LGMGraphDocument mainDoc = gdcoll.getMainDoc();
                mainDoc.distributeEvent(DATA_CHANGED);
            }
        } else if (is(cancelButton)) {
            if (returnValue != 0) {
                if (JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(this, getResString("userFieldDialog_warning_message"), getResString("userFieldDialog_warning"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE)) {
                    return;
                }
            }
            GDCollection gdcoll = definitions.getCollection();
            gdcoll.setUserFieldDefinitions(oldUserFieldDefionitions);
            returnValue = -1;
            dispose();
        } else if (is(importButton)) {
            if (importDefinitions(this, definitions)) {
                fieldList.update(classComboBox.getSelectedClass());
                returnValue = 1;
            }
        } else if (is(exportButton)) {
            exportDefinitions(this, definitions);
        } else {
            Class<? extends UserFieldTarget> selectedClass = classComboBox.getSelectedClass();
            if (is(newButton)) {
                //Definitionseditor für das neue userField anzeigen
                UserField.Style style = userFieldTypeComboBox.getSelectedObject();
                if (style == null) { //should never happen anymore, because we always set the first style in the list as selected
                    String message = getResString("userFieldDeclarationDialog_chooseType");
                    String title = getResString("fehler");
                    JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
                    return;
                }
                //jetzt kann nur noch ein Node- oder Kantentyp selektiert sein
                //-> neues userField für die selektierte Klassenart anlegen
                UserField userField = new UserField(selectedClass, style);
                //das neu erzeugte UserField sofort zur ausgewählten Klasse hinzufügen
                int nextInsertIndex = fieldList.getNextInsertIndex(style);
                definitions.insert(userField, nextInsertIndex);
                //solange den Dialog zur Definition der Eigenschaften des neuen UserFields zeigen, bis nur konsitente Werte eingegeben wurden
                int userDefinitionDialogReturnValue;
                do {
                    userDefinitionDialogReturnValue = UserFieldDefinitionDialog.showDialog(this, userField, gdcoll);
                } while (userDefinitionDialogReturnValue == OK && definitions.hasCrossReferences());

                //wenn der Dialog über OK verlassen wurde
                if (userDefinitionDialogReturnValue == OK) {
                    //das neue UserField anzeigen
                    int elementCountBeforeAdd = fieldList.getElementCount();
                    fieldList.update(selectedClass);
                    int elementCountAfterAdd = fieldList.getElementCount();
                    //if there was a default tab added there are 2 new elements -> to select the correct index we have to add 1 to the insert index
                    int newElementIndexDiff = elementCountAfterAdd - elementCountBeforeAdd - 1;
                    returnValue = 1;
                    //den Definitions sagen, dass sich was geändert hat
                    definitions.getCollection().getUserFieldDefinitions().initReset();
                    //select the new UserField (if the default tab was added for the very first element in the list-> go to index 1)
                    int newIndex = nextInsertIndex == 0 && !userField.hasStyle(TAB) ? 1 : nextInsertIndex + newElementIndexDiff;
                    fieldList.setSelectedIndex(newIndex);
                    fieldList.ensureIndexIsVisible(newIndex);
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
                        fieldList.update(selectedClass);
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
                    boolean defaultTabShouldBeDeleted = false;
                    for (int selectedIndex : selectedIndices) {
                        if (selectedIndex == 0) {
                            defaultTabShouldBeDeleted = true;
                        }
                        UserField userField = fieldList.get(selectedIndex);
                        //das aktuelle UserField kann schon gelöscht worden sein, durch das löschen eines vorhergehenden in der Schleife
                        if (!removedUserFields.contains(userField)) {
                            //Alle gelöschten UserFields merken
                            removedUserFields.addAll(definitions.remove(userField));
                        }
                    }
                    fieldList.update(selectedClass);
                    //The UserFieldList never deletes the last tab if there is at least one other element in the list
                    // -> remove the tab now if all the other elements are deleted
                    if (defaultTabShouldBeDeleted && fieldList.getElementCount() == 1) {
                        UserField defaultTab = fieldList.get(0);
                        definitions.remove(defaultTab);
                        fieldList.update(classComboBox.getSelectedClass());
                    }
                    returnValue = -1;
                }
            } else if (is(duplicateButton)) {
                int[] selectedIndices = fieldList.getSelectedIndices();
                int blockStart = -1;
                int blockEnd = -1;
                for (int i = selectedIndices.length - 1; i >= 0; i--) {
                    if (blockEnd < selectedIndices[i]) {
                        blockEnd = selectedIndices[i];
                        blockStart = blockEnd;
                        for (int j = i - 1; j >= 0; j--) {
                            if (selectedIndices[j] + 1 != selectedIndices[j + 1]) {
                                break;
                            }
                            blockStart--;
                        }
                    }
                    int insertOffset = 1;
                    for (int j = blockStart; j <= blockEnd; j++) {
                        UserField userField = fieldList.getUserField(j);
                        UserField duplicate = userField.clone(true);
                        definitions.insert(duplicate, blockEnd + insertOffset++);
                        i--;
                    }
                    blockEnd = -1;
                    i++;
                }
                fieldList.update(selectedClass);
            } else if (is(upButton)) {
                fieldList.moveUp();
                fieldList.update(selectedClass);
                returnValue = 1;
            } else if (is(downButton)) {
                fieldList.moveDown();
                fieldList.update(selectedClass);
                returnValue = 1;
            } else if (is(classComboBox)) {
                fieldList.update(selectedClass);
                updateUserFieldTypeComboBox();
                updateOptionsPanel();
            }
        }
        previewButton.setEnabled(isPreviewEnabeld());
    }

    /**
     * @return
     */
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

    /**
     * @return
     */
    protected boolean isPreviewEnabeld() {
        Class<? extends UserFieldTarget> selectedClass = classComboBox.getSelectedClass();
        if (selectedClass == null || !Node.class.isAssignableFrom(selectedClass)) {
            return false;
        }
        // at the moment no preview for abstract classes
        // but we don't have such classes in the class list
        if (CoreMetaModel.isAbstract(selectedClass)) {
            return false;
        }
        //if we have subtypes this condition must be refitted
        Iterable<UserField> userFields = definitions.getUserFields(selectedClass);
        return userFields.iterator().hasNext();
    }

    /**
     *
     */
    private void showPreview() {
        if (!isPreviewEnabeld()) {
            return;
        }
        Class<? extends ModelElement> selectedClass = classComboBox.getSelectedClass().asSubclass(ModelElement.class);
        PreviewElementPropertyDialogCreator.showPreview(gdcoll, selectedClass);
    }

    @Override
    public void valueChanged(final ListSelectionEvent e) {
        if (e.getSource() == fieldList) {
            int[] selectedIndices = fieldList.getSelectedIndices();
            int selectionCount = selectedIndices.length;
            editButton.setEnabled(selectionCount == 1);
            boolean isSelection = selectionCount > 0;
            deleteButton.setEnabled(isSelection);
            duplicateButton.setEnabled(isSelection);
            if (isSelection) {
                // update up and down buttons state
                boolean continiuosSelection = true; //up and down are enabled only if continiuosSelection
                for (int i = 0; i < selectionCount - 1; i++) { //check continious selection
                    if (selectedIndices[i] + 1 != selectedIndices[i + 1]) {
                        continiuosSelection = false;
                        break;
                    }
                }
                //discontinous selection -> disable up and down
                boolean upButtonEnabled = continiuosSelection;
                boolean downButtonEnabled = continiuosSelection;
                if (continiuosSelection) {
                    boolean isShowGlobalUserFields = classComboBox.isGlobalUserFieldClassSelected();
                    if (selectedIndices[0] == 0) { //first element selected -> no up
                        upButtonEnabled = false;
                    } else if (!isShowGlobalUserFields && selectedIndices[0] == 1) {
                        if (!fieldList.hasStyle(1, TAB, SUBTYPE)) { //second element selected but it is not a tab or subtype -> no up
                            upButtonEnabled = false;
                        }
                    }
                    int elementCount = fieldList.getElementCount();
                    if (selectedIndices[selectionCount - 1] == elementCount - 1) { //last list element selected -> no down
                        downButtonEnabled = false;
                    } else if (!isShowGlobalUserFields && selectedIndices[0] == 0) {
                        if (!fieldList.hasStyle(selectedIndices[selectionCount - 1] + 1, TAB, SUBTYPE)) { // only enable down if the very first tab is selected if the selected element after the last selected is a tab too
                            downButtonEnabled = false;
                        }
                    }
                }
                upButton.setEnabled(upButtonEnabled);
                downButton.setEnabled(downButtonEnabled);
            }
            updateOptionsPanel();
        }

    }

    /**
     * @return
     */
    private void updateOptionsPanel() {
        List<UserField> userFields = new ArrayList<>();
        for (int i : fieldList.getSelectedIndices()) {
            UserField userField = fieldList.getUserField(i);
            userFields.add(userField);
        }
        Class<? extends UserFieldTarget> selectedClass = classComboBox.getSelectedClass();
        optionPanel.setUserFields(selectedClass, userFields);
    }

}
