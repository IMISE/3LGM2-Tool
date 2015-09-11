package de.imise.tool3lgm.graphtools.userfield.dialog.definition;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.dialog.AbstractPropertyDialog;
import de.imise.tool3lgm.graphtools.elements.Doppelkante;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.userfield.CostingUtil;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.dialog.definition.panel.AbstractInputPanel;
import de.imise.tool3lgm.graphtools.userfield.dialog.definition.panel.FormatPanel;
import de.imise.tool3lgm.graphtools.userfield.dialog.definition.panel.FormulaPanel;
import de.imise.tool3lgm.graphtools.userfield.dialog.definition.panel.ListValuePanel;
import de.imise.tool3lgm.graphtools.userfield.dialog.definition.panel.NameDescripPanel;
import de.imise.tool3lgm.graphtools.userfield.dialog.definition.panel.OptionPanel;
import de.imise.util.swing.dialog.MultipleOptionPane;

/**
 * Definiert ein UserField. Ein UserField bekommt einen Namen, eine Beschreibung, eine Stylezuordnung (Separator, <code>JComboBox</code>,
 * <code>JCheckBox</code>,<code>JRadioButton</code>, <code>JTextField</code>,<code>JTextArea</code>, Kennzahl, KennzahlFormel, Verteilungsgewicht),
 * ein Zahlenformat und Einheit zugeordnet.
 * 
 * @author Thomas Rudert
 */
public final class UserFieldDefinitionDialog extends AbstractPropertyDialog implements ActionListener {

    /**
     * Liste, in die alle Panels, die der Dialog anzeigt
     */
    private final ArrayList<AbstractInputPanel> panelList = new ArrayList<AbstractInputPanel>();

    /**
     * Rückgabewert des Dialoges, wenn er über den Abbrechen-Knopf verlassen wurde
     */
    public static final int CANCEL = 0;

    /**
     * Rückgabewert des Dialoges, wenn er über den OK-Knopf verlassen wurde
     */
    public static final int OK = 1;

    /**
     * Rückgabewert des Dialoges. Er nimmt einen der Werte UserFieldDefinitionDialog.OK oder UserFieldDefinitionDialog.CANCEL an
     */
    private int retVal = -1;

    /**
     * Die Definitionen
     */
    private final UserFieldDefinitions definitions;

    /**
     * Das UserField, dessem Eigenschaften mit diesem Panel geändert werden sollen.
     */
    private final UserField userField;

    /**
     * Instanz des Dialogs zur Definition eines <code>UserField</code>
     * 
     * @param owner
     * @param userField
     * @param gdcol
     */
    private UserFieldDefinitionDialog(final JDialog owner, final UserField userField, final GDCollection gdcol) {
        super(owner, gdcol);
        this.userField = userField;
        definitions = gdcol.getUserFieldDefinitions();
        setModal(true);
        setLocationByPlatform(true);
        setTitle(Tool3lgmConstants.getResString("userFieldEditor_title"));
        setMinimumSize(new Dimension(500, 400));
        init();
    }

    private void init() {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.setLocation(getOwner().getLocation());

        /*
         * schließen des Fensters abfangen (wenn nicht durch Abbrechen-Button veranlasst)
         */
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                UserFieldDefinitionDialog.this.cancel();
            }
        });

        Container pane = getContentPane();
        GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
        pane.setLayout(new GridBagLayout());

        //Label mit dem Namen und der Art des zu bearbeitenden Feldes
        StringBuilder sb = new StringBuilder();
        sb.append(Tool3lgmConstants.getResString("attribute"));
        sb.append(":  ");
        if (ModelConstants.isNodeType(userField.getTargetClass())) {
            sb.append(Tool3lgmConstants.getResString(userField.getTargetClass().getSimpleName()));
        } else {
            if (ModelConstants.isEdgeType(userField.getTargetClass())) {
                sb.append(ModelConstants.getMetaAssociationName(userField.getTargetClass().asSubclass(Kante.class), false, Doppelkante.DOUBLE, true, true));
            } else if (userField.isGlobalOrFormat()) {
                sb.append(UserFieldDefinitions.getDisplayableGlobalFieldIdentifierName());
            }

        }
        sb.append("  ");
        sb.append(Tool3lgmConstants.getResString("attribute_typ"));
        sb.append(":  ");
        sb.append(CostingUtil.getDisplayableStyleName(userField.getStyle()));
        JLabel topLabel = new JLabel(sb.toString());
        topLabel.setFont(topLabel.getFont().deriveFont(14f).deriveFont(Font.BOLD));
        pane.add(topLabel, gbc);

        //Panel für das Ändern des Namens und der Beschreibung
        NameDescripPanel nameDescripPanel = new NameDescripPanel(userField);
        panelList.add(nameDescripPanel);
        gbc.gridy++;
        gbc.weighty = 1.0;
        pane.add(nameDescripPanel, gbc);
        gbc.weighty = 0.0;

        UserField.Style style = userField.getStyle();

        //Kennzahl
        if (style == UserField.Style.CLASSIFICATION_NUMBER) {
            //Format-Panel
            panelList.add(new FormatPanel(this, userField, definitions));
            //Kennzahlformel
        } else if (style == UserField.Style.CLASSIFICATION_NUMBER_FORMULA) {
            //Formel-Panel
            panelList.add(new FormulaPanel(this, userField, definitions, nameDescripPanel.getNameTextField()));
            //Format-Panel
            panelList.add(new FormatPanel(this, userField, definitions));
            //alle Styles, die irgendwelche Listenwerte haben
        } else if (UserField.isListValueStyle(style)) {
            //Listen-Werte-Panel
            panelList.add(new ListValuePanel(userField));
        }

        //Optionen-Panel
        panelList.add(new OptionPanel(userField));

        //Alle Panels hinzufügen
        for (int i = 0; i < panelList.size(); i++) {
            JPanel panel = panelList.get(i);
            if (!(panel instanceof NameDescripPanel)) {
                gbc.gridy++;
                pane.add(panel, gbc);
            }
        }
        //Ok und Abbrechen-Buttons unten hinzufügen
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
        JPanel buttonPane = new JPanel();
        buttonPane.add(okButton);
        buttonPane.add(cancelButton);
        gbc.gridy++;
        pane.add(buttonPane, gbc);
        pack();
    }

    /**
     * Zeigt den Dialog für die Definition eines <code>UserField</code>.
     * 
     * @param owner
     * @param userField
     * @param doc
     * @return UserFieldDefinitionDialog.CANCEL or UserFieldDefinitionDialog.OK
     */
    public static int showDialog(final JDialog owner, final UserField userField, final GDCollection gdcol) {
        UserFieldDefinitionDialog dialog = new UserFieldDefinitionDialog(owner, userField, gdcol);
        dialog.setVisible(true);
        return dialog.retVal;
    }

    /**
     * Bricht die aktuelle Transaktion ab und macht die Änderung Rückgänig.
     */
    public void cancel() {
        for (int i = 0; i < panelList.size(); i++) {
            panelList.get(i).cancel();
        }
        retVal = CANCEL;
        dispose();
    }

    /**
     * Die Transaktion wird beendet.
     */
    public void commit() {
        for (int i = 0; i < panelList.size(); i++) {
            panelList.get(i).commit();
        }
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        if (e.getSource() == okButton) {
            String errorString = null;
            for (int i = 0; i < panelList.size(); i++) {
                errorString = panelList.get(i).getError();
                if (errorString != null) {
                    break;
                }
            }
            if (errorString != null) {
                MultipleOptionPane.showInformationMessageDialog(this, Tool3lgmConstants.getResString("fehler"), errorString);
            } else {
                retVal = OK;
                commit();
                dispose();
            }

        } else if (e.getSource() == cancelButton) {
            //Achtung: Cancel wird auch aufgerufen, wenn auf das Kreuz des
            // Dialoges geklickt wurde,
            //also alles was rückgängig gemacht werden soll nicht hier hin
            // sondern in cancel() schreiben
            cancel();
        }

    }
}
