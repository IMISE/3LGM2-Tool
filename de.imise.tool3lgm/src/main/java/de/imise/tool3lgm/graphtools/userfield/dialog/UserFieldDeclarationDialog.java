package de.imise.tool3lgm.graphtools.userfield.dialog;

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
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.RandomAccessFile;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.Tool3lgmConstants.FileFilterType;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.userfield.CostingUtil;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.UserFieldTarget;
import de.imise.tool3lgm.graphtools.userfield.UserFieldXMLParser;
import de.imise.tool3lgm.graphtools.userfield.dialog.definition.UserFieldDefinitionDialog;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.util.NamedObjectContainer;
import de.imise.util.swing.component.AlphabeticalComboBox;
import de.imise.util.swing.dialog.AbstractSizeAndPositionRestoringDialog;
import de.imise.util.swing.dialog.ExtendedFileChooser;
import de.imise.util.swing.dialog.MultipleOptionPane;

/**
 * @author Thomas Rudert Dialog to create, edit, remove, import and export user-definied property-fields for model-elements
 */
public final class UserFieldDeclarationDialog extends AbstractSizeAndPositionRestoringDialog implements ActionListener, ListSelectionListener, MouseListener {

    /** combobox to select a model-class */
    private AlphabeticalComboBox classComboBox;

    /**
     * list with the defined userFields Es wird hier keine AplphabeticalJList genutzt, weil die Elemente in der Reinhenfolge angezeigt werden sollen,
     * die der User vorgibt.
     */
    private JList fieldList;

    /** the model with the data for fieldList */
    private DefaultListModel fieldListModel;

    /** button to edit a userField */
    private JButton editButton;

    /** button to remove a userField */
    private JButton removeButton;

    /** button to add new userField */
    private JButton addButton;

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
        classComboBox = new AlphabeticalComboBox();
        classComboBox.setMaximumRowCount(13);
        //Modellklasse zur

        classComboBox.addItem(UserFieldDefinitions.GLOBAL_USERFIELD_IDENTIFIER_CLASS, UserFieldDefinitions.getDisplayableGlobalFieldIdentifierName());
        classComboBox.addSeparator(true);

        //alle nicht abstracten Knotenklassen hinzufügen
        for (int i = 0; i < ModelConstants.ALL_NODES.length; i++) {
            if (Modifier.isAbstract(ModelConstants.ALL_NODES[i].getModifiers())) {
                continue;
            }
            classComboBox.addItem(ModelConstants.ALL_NODES[i], ModelConstants.getDisplayableName(ModelConstants.ALL_NODES[i]));
        }
        classComboBox.addSeparator(true);

        //alle Kantenklassen jeweils mit hin und Rückrichtung
        for (int i = 0; i < ModelConstants.ALL_EDGES.length; i++) {
            classComboBox.addItem(ModelConstants.ALL_EDGES[i], ModelConstants.getFullForwardMetaAssociationName(ModelConstants.ALL_EDGES[i]));
            classComboBox.addItem(ModelConstants.ALL_EDGES[i], ModelConstants.getFullBackwardMetaAssociationName(ModelConstants.ALL_EDGES[i]));
        }
        //nichts selektieren
        classComboBox.setSelectedIndex(-1);
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
        fieldListModel = new DefaultListModel();
        fieldList = new JList(fieldListModel);
        fieldList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fieldList.addListSelectionListener(this);
        fieldList.addMouseListener(this);

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

        addButton = new JButton(Tool3lgmConstants.getResString("new"));
        addButton.setActionCommand("add");
        addButton.addActionListener(this);
        addButton.setEnabled(false);
        constraints.gridwidth = 1;
        //Anstelle des Buttons die ComboBox einfügen

        userFieldTypeComboBox = new AlphabeticalComboBox();
        userFieldTypeComboBox.setEnabled(false);
        panel2.add(userFieldTypeComboBox, constraints);

        constraints.gridx++;
        constraints.gridwidth = 3;
        panel2.add(addButton, constraints);
        constraints.gridx--;
        editButton = new JButton(Tool3lgmConstants.getResString("editButtonText"));
        editButton.setActionCommand("edit");
        editButton.addActionListener(this);
        editButton.setEnabled(false);
        constraints.gridy = 1;
        constraints.gridwidth = 4;
        panel2.add(editButton, constraints);

        removeButton = new JButton(Tool3lgmConstants.getResString("delete"));
        removeButton.setActionCommand("remove");
        removeButton.addActionListener(this);
        removeButton.setEnabled(false);
        constraints.gridy = 2;
        constraints.gridwidth = 4;
        panel2.add(removeButton, constraints);

        downButton = new JButton(Tool3lgmConstants.getIcon("runter2.gif"));
        downButton.setActionCommand("down");
        downButton.addActionListener(this);
        downButton.setEnabled(false);
        constraints.gridwidth = 2;
        constraints.gridy = 3;
        panel2.add(downButton, constraints);

        upButton = new JButton(Tool3lgmConstants.getIcon("hoch2.gif"));
        upButton.setActionCommand("up");
        upButton.addActionListener(this);
        upButton.setEnabled(false);
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
        button = new JButton(Tool3lgmConstants.getResString("importButtonText"));
        button.setActionCommand("import");
        button.addActionListener(this);
        panel2.add(button);

        button = new JButton(Tool3lgmConstants.getResString("exportButtonText"));
        button.setActionCommand("export");
        button.addActionListener(this);
        panel2.add(button);
        panel1.add(panel2);

        panel2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        button = new JButton(Tool3lgmConstants.getResString("ok"));
        button.setActionCommand("ok");
        button.addActionListener(this);
        panel2.add(button);
        button = new JButton(Tool3lgmConstants.getResString("cancel"));
        button.setActionCommand("cancel");
        button.addActionListener(this);
        panel2.add(button);
        panel1.add(panel2);
        pane.add(panel1, BorderLayout.SOUTH);
        if (defaultSize == null) {
            pack();
            defaultSize = getSize();
        }
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

        Class<?> selClass = (Class<?>) classComboBox.getSelectedObject();
        if (selClass == null) {
            return;
        }

        userFieldTypeComboBox.addItem(UserField.Style.CHECK_BOX, CostingUtil.getDisplayableStyleName(UserField.Style.CHECK_BOX));
        userFieldTypeComboBox.addItem(UserField.Style.COMBO_BOX, CostingUtil.getDisplayableStyleName(UserField.Style.COMBO_BOX));
        userFieldTypeComboBox.addItem(UserField.Style.HYPERLINK, CostingUtil.getDisplayableStyleName(UserField.Style.HYPERLINK));
        userFieldTypeComboBox.addItem(UserField.Style.MULTI_LINE, CostingUtil.getDisplayableStyleName(UserField.Style.MULTI_LINE));
        userFieldTypeComboBox.addItem(UserField.Style.RADIO_BUTTON, CostingUtil.getDisplayableStyleName(UserField.Style.RADIO_BUTTON));
        userFieldTypeComboBox.addItem(UserField.Style.SEPARATOR, CostingUtil.getDisplayableStyleName(UserField.Style.SEPARATOR));
        userFieldTypeComboBox.addItem(UserField.Style.SINGLE_LINE, CostingUtil.getDisplayableStyleName(UserField.Style.SINGLE_LINE));
        userFieldTypeComboBox.addItem(UserField.Style.ID, CostingUtil.getDisplayableStyleName(UserField.Style.ID));

        // Die Kennzahl kann immer zu Auswahl gestellt werden. 
        //Nur wenn es sich um eine Modellvariable handelt, darf die Kennzahlformel nicht angeboten werden, sonst schon
        userFieldTypeComboBox.addItem(UserField.Style.CLASSIFICATION_NUMBER, CostingUtil.getDisplayableStyleName(UserField.Style.CLASSIFICATION_NUMBER));
        if (selClass != UserFieldDefinitions.GLOBAL_USERFIELD_IDENTIFIER_CLASS) {
            userFieldTypeComboBox.addItem(UserField.Style.CLASSIFICATION_NUMBER_FORMULA, CostingUtil.getDisplayableStyleName(UserField.Style.CLASSIFICATION_NUMBER_FORMULA));
        }
    }

    /**
     * Gibt in Anghänigkeit des übergebenen userfields den style dessen als String zurück.
     * 
     * @param u
     * @return String, der den ausgeschirebenen Style enthält.
     */
    private static final String getUserFieldStyle(final UserField u) {
        String userFieldStyle = "";
        if (u != null) {
            userFieldStyle = CostingUtil.getDisplayableStyleName(u.getStyle());
        }
        return userFieldStyle;
    }

    /**
     * Aktualisiert die Liste der {@link UserField}s für die selektierte Klasse
     */
    private void updateFieldList() {
        //die Angabe für Start und Endklasse sind nur Testweise
        clearFieldList();
        Class<?> selClass = (Class<?>) classComboBox.getSelectedObject();
        if (selClass == null || !UserFieldTarget.class.isAssignableFrom(selClass)) {
            return;
        }
        Class<? extends UserFieldTarget> clazz = selClass.asSubclass(UserFieldTarget.class);
        for (UserField uf : definitions.getUserFields(clazz)) {
            addFieldListEntry(uf);
        }
        //		if (clazz != UserFieldDefinitions.GLOBAL_USERFIELD_IDENTIFIER_CLASS) {
        //			for (int i = 0; i < definitions.getUserFieldCount(clazz); i++) {
        //				UserField field = definitions.get(clazz, i);
        //				addFieldListEntry(field);
        //			}
        //		} else {
        //			int attributeCount = definitions.getGlobalUserFieldCount();
        //			for (int i = 0; i < attributeCount; i++) {
        //				UserField globalField = definitions.getGlobal(i);
        //				if (!globalField.hasStyle(UserField.Style.FORMAT)) 
        //					addFieldListEntry(globalField);
        //			}
        //		}

    }

    private void clearFieldList() {
        fieldListModel.removeAllElements();
    }

    /**
     * Fügt zur Liste der <code>UserField</code>s das übergebene <code>UserField</code> hinzu.
     * 
     * @param userField
     */
    private void addFieldListEntry(final UserField userField) {
        addFieldListEntry(userField, fieldListModel.size());
    }

    /**
     * Fügt zur Liste der <code>UserField</code>s das übergebene <code>UserField</code> hinzu.
     * 
     * @param userField
     * @param index
     */
    private void addFieldListEntry(final UserField userField, final int index) {
        NamedObjectContainer<UserField> noc = new NamedObjectContainer<UserField>(userField, userField.getName() + "  ( " + getUserFieldStyle(userField) + " )");
        fieldListModel.add(index, noc);
    }

    /**
     * Defnition importieren
     * 
     * @return
     */
    private boolean importDef() {
        ExtendedFileChooser dialog = new ExtendedFileChooser(UserFieldDeclarationDialog.class);
        dialog.setFileFilters(true, Tool3lgmConstants.getFileNameExtensionFilters(FileFilterType.USERFIELD));
        dialog.setMultiSelectionEnabled(false);
        if (dialog.showOpenDialog(this) != ExtendedFileChooser.APPROVE_OPTION) {
            return false;
        }
        try {
            UserFieldXMLParser parser = new UserFieldXMLParser(dialog.getSelectedFile(), definitions);
            parser.parseDocument();
        } catch (Exception exp) {
            Log.show(Log.ERROR, "IOException while parsing UserFieldFile", exp);
            return false;
        }
        updateFieldList();
        return true;
    }

    /**
     * Defnition exportieren
     * 
     * @return
     */
    private boolean exportDef() {
        ExtendedFileChooser dialog = new ExtendedFileChooser(UserFieldDeclarationDialog.class);
        dialog.setFileFilters(true, Tool3lgmConstants.getFileNameExtensionFilter(FileFilterType.USERFIELD));
        dialog.setMultiSelectionEnabled(false);
        if (dialog.showSaveDialog(this) != ExtendedFileChooser.APPROVE_OPTION) {
            return false;
        }

        try {
            RandomAccessFile raw = new RandomAccessFile(dialog.getSelectedFile(), "rw");
            raw.setLength(0);
            raw.writeBytes(definitions.toXMLString(false));
            raw.close();
        } catch (Exception exp) {
            Log.show(Log.ERROR, "IOException while parsing UserFieldFile", exp);
            return false;
        }
        return true;

    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        if (e.getActionCommand().equals("ok")) {
            dispose();

            gdcol.removeUserFieldValues(removedUserFields);

            if (!UserProperties.isShowUserDefinedPropertiesInModelBrowser()) {
                return;
            }

            GDCollection gdcoll = definitions.getCollection();
            if (gdcoll != null) {
                gdcoll.getMainGraphDocument().distributeEvent(GraphDocument.DATA_CHANGED);
            }
        } else if (e.getActionCommand().equals("cancel")) {

            if (returnValue != 0) {
                if (JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(this, Tool3lgmConstants.getResString("userFieldDialog_warning_message"), Tool3lgmConstants.getResString("userFieldDialog_warning"), JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE)) {
                    return;
                }
            }
            definitions.getCollection().setUserFieldDefinitions(oldUserFieldDefionitions);

            returnValue = -1;
            dispose();
        } else if (e.getActionCommand().equals("import")) {
            if (importDef()) {
                updateFieldList();
                returnValue = 1;
            }
        } else if (e.getActionCommand().equals("export")) {
            exportDef();

        } else if (e.getActionCommand().equals("add")) {
            //Typ der seleketierten Klasse holen
            Class<?> selectedClass = (Class<?>) classComboBox.getSelectedObject();

            //wenn keine Klasse, für die man Kennzahlen und Formeln definieren kann, selektiert ist
            if (!UserFieldTarget.class.isAssignableFrom(selectedClass)) {
                return;
            }
            Class<? extends UserFieldTarget> selClass = selectedClass.asSubclass(UserFieldTarget.class);

            //Definitionseditor für das neue userField anzeigen
            UserField.Style style = (UserField.Style) userFieldTypeComboBox.getSelectedObject();
            if (style == null) {
                JOptionPane.showMessageDialog(this, Tool3lgmConstants.getErrString("choose_type_first"), Tool3lgmConstants.getResString("fehler"), JOptionPane.ERROR_MESSAGE);
                return;
            }

            //jetzt kann nur noch ein Knoten- oder Kantentyp selektiert sein
            //-> neues userField für die selektierte Klassenart anlegen
            UserField userField = new UserField(selClass, style, definitions);

            //das neu erzeugte UserField sofort zur ausgewählten Klasse hinzufügne
            definitions.add(userField);

            //solange den Dialog zur Definition der Eigenschaften des neuen UserFields zeigen, bis nur konsitente Werte eingegeben wurden
            int userDefinitionDialogReturnValue;
            do {
                userDefinitionDialogReturnValue = UserFieldDefinitionDialog.showDialog(this, userField, gdcol);
            } while (userDefinitionDialogReturnValue == UserFieldDefinitionDialog.OK && definitions.hasCrossReferences());

            //wenn der Dialog über OK verlassen wurde
            if (userDefinitionDialogReturnValue == UserFieldDefinitionDialog.OK) {
                //das neue UserField anzeigen
                addFieldListEntry(userField);
                returnValue = 1;
                //den Definitions sagen, dass sich was geändert hat
                definitions.getCollection().getUserFieldDefinitions().initReset();
                //wenn die Defnition der neuen Kennzahl oder Formel abgebrochen wurde
            } else {
                //wieder aus den Definitions entfernen
                definitions.remove(userField);
            }
        } else if (e.getActionCommand().equals("edit")) {

            //aus der Liste der UserFields das selektierte holen
            Object selectedItem = fieldList.getSelectedValue();
            //keins gefunden -> raus
            if (selectedItem == null) {
                return;
            }
            @SuppressWarnings("unchecked")
            UserField userField = ((NamedObjectContainer<UserField>) selectedItem).getObject();

            //die alte Formel des UserFields holen (die ist nur bei UserFields mit dem Formula-Style nicht null, aber das ist egal)
            String oldFormula = userField.getFormula();

            do {
                gdcol.getUserFieldDefinitions().setConsistencyUnknown();
                //Definitionseditor für das zu bearbeitende userField anzeigen
                if (UserFieldDefinitionDialog.showDialog(this, userField, gdcol) == UserFieldDefinitionDialog.OK) {
                    //aus der Liste entfernen und wieder hinzufügen, damit der Anzeigename korrektr aktualisert wird
                    int selectedIndex = fieldList.getSelectedIndex();

                    //Das Element aus der Liste entfernen und an alter Stelle wieder neu hinzufügen, 
                    //damit der evtl. geänderte korrekt Name angezeigt wird
                    fieldListModel.remove(selectedIndex);
                    addFieldListEntry(userField, selectedIndex);
                    fieldList.setSelectedIndex(selectedIndex);

                    returnValue = 1;
                } else {
                    // Wenn der Definitionsdialog für Kennzahlformeln abgebrochen wurde, wird die alte Formel zurückgesetzt.
                    userField.setFormula(oldFormula);
                    break;
                }
                //den Formel-Editor-Dialog kann man solange nicht verlassen, wie sich Formeln im Kreis referenzieren
            } while (definitions.hasCrossReferences());

        } else if (e.getActionCommand().equals("remove")) {
            NamedObjectContainer<UserField> noc = (NamedObjectContainer<UserField>) fieldListModel.get(fieldList.getSelectedIndex());
            UserField userField = noc.getObject();

            //Bevor ein userField gelöscht wird, wird nochmal eine Sicherheitsabfrage gestellt.
            //Wenn die Siocherheitsabfrage nicht bestätigt wird, wird cancel true. D.h. das Löschen wird abgebrochen.
            boolean cancel = false;

            if (showWarningForDeletingUserFields) {
                String[] frage = {
                    Tool3lgmConstants.getResString("dontShowAgain")
                };
                Object[] result = MultipleOptionPane.showCheckBoxOptionDialog(this, Tool3lgmConstants.getResString("warnung"), Tool3lgmConstants.getResString("allValuesWouldBeDeleted"), frage);

                if (result == null) {
                    cancel = true;
                } else if (result[0] != null) {
                    showWarningForDeletingUserFields = false;
                }
            }

            //Alle gelöschten UserFields merken
            if (cancel == false) {
                removedUserFields.addAll(definitions.remove(userField));
                updateFieldList();
                returnValue = 1;
            }
            returnValue = -1;

        } else if (e.getActionCommand().equals("up")) {
            UserField userField;
            //if(fieldListModel.get(fieldList.getSelectedIndex()) instanceof
            // NamedObjectContainer)
            //	{
            NamedObjectContainer<UserField> noc = (NamedObjectContainer<UserField>) fieldListModel.get(fieldList.getSelectedIndex());
            userField = noc.getObject();

            //	}
            //	else
            //		userField = (UserField)
            // fieldListModel.get(fieldList.getSelectedIndex());

            //int index = fieldListModel.indexOf(userField);

            int index = fieldListModel.indexOf(noc);

            fieldListModel.remove(index);
            int newIndex = Math.max(0, index - 1);
            //fieldListModel.insertElementAt(userField, newIndex);
            fieldListModel.insertElementAt(noc, newIndex);
            definitions.insert(userField, newIndex);
            fieldList.setSelectedIndex(index > 0 ? --index : index);
            returnValue = 1;

        } else if (e.getActionCommand().equals("down")) {
            UserField userField;
            NamedObjectContainer<UserField> noc = (NamedObjectContainer<UserField>) fieldListModel.get(fieldList.getSelectedIndex());
            userField = noc.getObject();

            //int index = fieldListModel.indexOf(userField);
            int index = fieldListModel.indexOf(noc);
            fieldListModel.remove(index);
            int newIndex = Math.min(fieldListModel.size(), index + 1);
            //fieldListModel.insertElementAt(userField, newIndex);
            fieldListModel.insertElementAt(noc, newIndex);
            definitions.insert(userField, newIndex);
            fieldList.setSelectedIndex(index < fieldListModel.getSize() - 1 ? ++index : index);
            returnValue = 1;

        } else if (e.getSource().equals(classComboBox)) {
            updateFieldList();
            updateUserFieldTypeComboBox();
            addButton.setEnabled(true);
        }
    }

    /**
     * @return
     */
    public UserFieldDefinitions getModifiedDefinitions() {
        return definitions;
    }

    @Override
    public void valueChanged(final ListSelectionEvent e) {
        if (e.getSource().equals(fieldList)) {
            editButton.setEnabled(!fieldList.isSelectionEmpty());
            removeButton.setEnabled(!fieldList.isSelectionEmpty());
            downButton.setEnabled(!fieldList.isSelectionEmpty());
            upButton.setEnabled(!fieldList.isSelectionEmpty());

        }
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
        if (e.getSource() == fieldList && e.getClickCount() > 1 && fieldList.getSelectedIndex() > -1 && fieldList.getSelectedIndex() == fieldList.locationToIndex(new Point(e.getX(), e.getY()))) {
            editButton.doClick();
        }
    }

    @Override
    public void mouseEntered(final MouseEvent e) {
    }

    @Override
    public void mouseExited(final MouseEvent e) {
    }

    @Override
    public void mousePressed(final MouseEvent e) {
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
    }

    @Override
    public Dimension getDefaultSize() {
        return defaultSize;
    }

}
