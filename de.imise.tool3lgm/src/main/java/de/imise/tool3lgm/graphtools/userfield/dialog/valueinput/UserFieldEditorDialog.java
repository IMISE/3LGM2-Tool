/*
 * Created on 14.02.2008
 */
package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput;

import static de.imise.tool3lgm.graphtools.userfield.UserField.Style.CLASSIFICATION_NUMBER;
import static de.imise.tool3lgm.graphtools.userfield.UserField.Style.COMBO_BOX;
import static de.imise.tool3lgm.graphtools.userfield.UserField.Style.HYPERLINK;
import static de.imise.tool3lgm.graphtools.userfield.UserField.Style.ID;
import static de.imise.tool3lgm.graphtools.userfield.UserField.Style.SINGLE_LINE;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import com.google.common.collect.ImmutableSet;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.dialog.AbstractPropertyDialog;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel.AbstractUserFieldEditorPanel;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel.ClassificationNumberFormulaPanel;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel.DistributionWeightEditorPanel;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel.DistributionWeightReplacePanel;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel.GeneralUserFieldEditorPanel;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel.ModelVariableEditorPanel;
import de.imise.util.swing.component.TabbedPane;

/**
 * Dialog zur Massendateneingabe von Kennzahlen, Modelvariablen und Verteilungsgewichten
 * 
 * @author fstephan
 */
public class UserFieldEditorDialog extends AbstractPropertyDialog {

    /**
     * Die Standardgröße des Dialoges
     */
    private static final Dimension DEFAULT_SIZE = new Dimension(600, 500);

    /**
     * Property, für Änderungen der Selektierung in einem der Tables
     */
    public static final String PROPERTY_TABLE_SELECTION_CHANGED = "ptsc";

    /**
     * Hintergrundfarbe für Selektionen in ComboBoxes und Tables
     */
    public static final Color SELECTION_BACKROUND_COLOR = new Color(55, 55, 140, 100);

    /**
     * Einzige Instanz des Editors. Es ex. kein öffentlicher Konstruktor. Instanz kann über {@link #getDialog(Frame, GraphDocument)} erhalten werden.
     */
    private static UserFieldEditorDialog editor = null;

    /**
     * Das Panel für die Kennzahlen
     */
    private final GeneralUserFieldEditorPanel panelCN;

    /**
     * Das Panel für die Verteilungsgewichte
     */
    private final DistributionWeightEditorPanel panelDW;

    /**
     * Das Panel für die Ersetzung von Verteilungsgewichten
     */
    private final DistributionWeightReplacePanel panelDWReplace;

    /**
     * Das Panel für die ModelVariablen
     */
    private final ModelVariableEditorPanel panelMV;

    /**
     * Das Panel für die berechneten Kennzahlen
     */
    private final ClassificationNumberFormulaPanel panelCNF;

    /**
     * Das Panel für alle anderen benutzerdefinierten Eigenschaften
     */
    private final GeneralUserFieldEditorPanel panelAllOther;

    /**
     * Bei Abbruch, wird diese Aktion ausgelöst
     */
    private AbstractAction cancelAction;

    /**
     * Gibt wieder, ob das Drücken des {@link #okButton}s Grund für das Schließen des Dialogs ist.
     */
    private boolean okButtonPressed;

    /**
     * Gibt wieder, ob der User bei der Datenverlust-Warnmeldung "Ja" oder "Nein" gewählt hat. Wenn ja, schließt sich der Dialog und alle Änderungen
     * gehen verloren, wenn nein, bleibt der Dialog geöffnet.
     */
    private boolean shouldDispose;

    /* ********************* Beginn: Initialisierungsteil ****************************** */

    /**
     * Konstruktor Zugriff von außen nicht über diesen Konstruktor möglich. Verwende stattdessen {@link #getDialog(Frame, GraphDocument)}
     */
    protected UserFieldEditorDialog(final Frame owner, final GDCollection gdcoll) {
        super(owner, gdcoll);
        setTitle(Tool3lgmConstants.getResString("attribute_editor"));
        setModal(true);

        okButtonPressed = false;
        shouldDispose = true;

        // applyButton + okButton + cancelButton initialisieren
        applyButton = new JButton();
        okButton = new JButton();
        cancelButton = new JButton();

        // panelCN + panelDW + panelMV initialisieren
        panelCN = new GeneralUserFieldEditorPanel(this, CLASSIFICATION_NUMBER, Tool3lgmConstants.getResString("CLASSIFICATION_NUMBER"));
        panelDW = new DistributionWeightEditorPanel(this, Tool3lgmConstants.getResString("userFieldEditor_classification_weighting"));
        panelDWReplace = new DistributionWeightReplacePanel(this, Tool3lgmConstants.getResString("userFieldEditor_classification_weighting_replace"));
        panelMV = new ModelVariableEditorPanel(this, Tool3lgmConstants.getResString("userFieldEditor_classification_modelvariable"));
        panelCNF = new ClassificationNumberFormulaPanel(this, Tool3lgmConstants.getResString("CLASSIFICATION_NUMBER_FORMULA"));
        panelAllOther = new GeneralUserFieldEditorPanel(this, ImmutableSet.of(HYPERLINK, ID, SINGLE_LINE, COMBO_BOX), Tool3lgmConstants.getResString("userFieldDialog_other"));

        // TabPanel initialisieren
        tab = new TabbedPane();

        // Panel zu Darstellen von Übernehmen Button, OK Button, Cancel Button initialisieren
        controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // grafisches Initialisieren
        init();
    }

    /**
     * Methode erzeugt die grafische Darstellung des Dialogs
     */
    private void init() {

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        final UserFieldEditorDialog finalDialog = this;

        // Action, die beim Abbruch ausgeführt wird
        cancelAction = new AbstractAction(Tool3lgmConstants.getResString("cancel")) {
            @Override
            public void actionPerformed(final ActionEvent e) {

                boolean dataChanged = false;

                AbstractUserFieldEditorPanel[] panels = finalDialog.getEditablePanels();

                for (int i = 0; i < panels.length; i++) {
                    AbstractUserFieldEditorPanel panel = panels[i];

                    /*
                     * Beendet das Editieren der aktuelle ausgewählten Zelle im Table. Damit können Werte auch ohne Bestätigung mit "Enter" übernommen
                     * werden.
                     */
                    panel.stopEditing();

                    // Falls in einem Table Änderungen aufgetreten sind, wird das in dataChanged festgehalten
                    dataChanged = dataChanged || panel.dataChanged();
                }

                /*
                 * Falls in einem Table Änderungen aufgetreten sind, wird eine Datenverlust-Verwarnung angezeigt
                 */
                if (dataChanged == true) {
                    if (JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(finalDialog, Tool3lgmConstants.getResString("userFieldDialog_warning_message"), Tool3lgmConstants.getResString("userFieldDialog_warning"), JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE)) {
                        finalDialog.shouldDispose = false;
                        return;
                    }
                }

                // rückgängig machen der alten Transaktion
                doc.finish_transaction(getTransactionID());
                doc.undo(getTransactionID());

                //finalDialog.dispose();
            }
        };

        // Anfügen von panelCN + panelDW + panelMV an das HauptPanel
        initTab();

        // Constraints für das Anfügen von tab
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints = new GridBagConstraints();
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.BOTH;

        // HauptPanel an den Dialog anfügen
        add(tab, constraints);

        // Anfügen von Übernehmen-,OK- und CancelButton an das controlPanel
        initControlPanel();

        // Constraints für das Anfügen von controlPanel
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.gridy++;
        constraints.weighty = 0;

        // Anfügen des controlPanels an den Dialog 
        add(controlPanel, constraints);

        // Constraints für ElementsAtPointPanel
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridy++;
        constraints.weighty = 0;

        // Anfügen des ElementsAtPointPanel
        add(createElementsAtPointPane(), constraints);

        // Neue Transaktion starten
        doc.start_transaction(getTransactionID());
    }

    /**
     * Methode fügt panelCN, panelDW, panelMV und panelCNF an <code>tab</code> an
     */
    private void initTab() {
        tab.addTab(panelCN.getName(), panelCN);
        tab.addTab(panelDW.getName(), panelDW);
        tab.addTab(panelDWReplace.getName(), panelDWReplace);
        tab.addTab(panelMV.getName(), panelMV);
        tab.addTab(panelCNF.getName(), panelCNF);
        tab.addTab(panelAllOther.getName(), panelAllOther);
    }

    /**
     * Methode sorgt für das Setzen der Actions für <code>applyButton</code>, <code>okButton</code>und <code>cancelButton</code> und fügt diese
     * Buttons an das <code>controlPanel</code> an.
     */
    private void initControlPanel() {

        // Setzen der Actions für Übernehmen Button, OK Button, Cancel Button
        setDefaultActionsOfButtons();

        // Anfügen der 3 Buttons an das Panel
        controlPanel.add(okButton);
        controlPanel.add(applyButton);
        controlPanel.add(cancelButton);
    }

    /**
     * Setzen der Actions für <code>applyButton</code>,<code>okButton</code> und <code>cancelButton</code>.
     */
    private void setDefaultActionsOfButtons() {
        final UserFieldEditorDialog finalDialog = this;
        /*
         * Übernimmt die Werte aus den Tabellen der Panels ins Model, falls sich die Daten in einer der Tabellen geändert haben.
         */
        final AbstractAction applyAction = new AbstractAction(Tool3lgmConstants.getResString("apply")) {
            @Override
            public void actionPerformed(final ActionEvent e) {

                boolean dataChanged = false;

                AbstractUserFieldEditorPanel[] panels = finalDialog.getEditablePanels();

                for (int i = 0; i < panels.length; i++) {
                    AbstractUserFieldEditorPanel panel = panels[i];

                    /*
                     * Beendet das Editieren der aktuelle ausgewählten Zelle im Table. Damit können Werte auch ohne Bestätigung mit "Enter" übernommen
                     * werden.
                     */
                    panel.stopEditing();

                    // Falls in einem Table Änderungen aufgetreten sind, wird das in dataChanged festgehalten
                    dataChanged = dataChanged || panel.dataChanged();

                    panel.takeOver();

                    panel.dataChanged(false);

                }
                // Alte Transaktion beenden
                doc.finish_transaction(finalDialog.getTransactionID());

                // Falls in einem Table Änderungen aufgetreten sind, wird das dem GraphDocument mitgeteilt
                if (dataChanged == true) {
                    doc.distributeEvent(GraphDocument.DATA_CHANGED, getTransactionID());
                }

                // Meue Transaktion starten
                doc.start_transaction(finalDialog.createNewTransactionID());
            }
        };

        /*
         * Übernimmt die Werte in den Tabellen der Panels ins Model
         */
        applyButton.setAction(applyAction);

        /*
         * Übernimmt die Werte in den Tabellen der Panels ins Model und schließt diesen Dialog anschließend.
         */
        okButton.setAction(new AbstractAction(Tool3lgmConstants.getResString("ok")) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                finalDialog.okButtonPressed = true;
                applyAction.actionPerformed(e);
                // Alte Transaktion beenden
                finalDialog.getGraphDocument().finish_transaction(finalDialog.getTransactionID());
                finalDialog.dispose();
                finalDialog.okButtonPressed = false;
            }
        });

        /*
         * Macht Änderungen rückgängig und schließt diesen Dialog
         */
        cancelButton.setAction(new AbstractAction(Tool3lgmConstants.getResString("cancel")) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                finalDialog.dispose();
            }
        });
    }

    /**
     * Erstellt und initialisiert ein Panel, dass das Row- und ColumnElement im Table an der aktuellen Mausposition anzeigt und fügt dieses Panel dem
     * Dialog hinzu.
     */
    private JPanel createElementsAtPointPane() {

        final String rowTitel = Tool3lgmConstants.getResString("userFieldDialog_elementsAtPointPane_rowTitle") + " ";
        final String colTitel = Tool3lgmConstants.getResString("userFieldDialog_elementsAtPointPane_colTitle") + " ";

        final String rowTitel2 = Tool3lgmConstants.getResString("zeilensumme") + ": ";
        final String colTitel2 = Tool3lgmConstants.getResString("spaltensumme") + ": ";

        JPanel panel = new JPanel(new GridBagLayout());

        final JLabel rowLabel = new JLabel();
        final JLabel colLabel = new JLabel();

        final JLabel rowLabel2 = new JLabel();
        final JLabel colLabel2 = new JLabel();

        rowLabel.setText(rowTitel);
        colLabel.setText(colTitel);
        rowLabel2.setText(rowTitel2);
        colLabel2.setText(colTitel2);
        rowLabel2.setVisible(false);
        colLabel2.setVisible(false);

        // Bekommt Änderungen der Selektion von den Panels übergeben
        PropertyChangeListener l = new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(PROPERTY_TABLE_SELECTION_CHANGED)) {
                    String[] news = (String[]) evt.getNewValue();
                    String rowIdentifierName = news[0];
                    String columnIdentifierName = news[1];
                    String rowSum = news[2];
                    String columnSum = news[3];
                    String rowIndex = news[4];
                    String columnIndex = news[5];
                    String rowCount = news[6];
                    String columnCount = news[7];
                    rowLabel.setText(rowTitel.concat("\t").concat(rowIndex).concat("/").concat(columnCount).concat(": \t").concat(rowIdentifierName));
                    colLabel.setText(colTitel.concat("\t").concat(columnIndex).concat("/").concat(rowCount).concat(": \t").concat(columnIdentifierName));
                    rowLabel2.setText(rowTitel2.concat(rowSum));
                    colLabel2.setText(colTitel2.concat(columnSum));

                    rowLabel2.setVisible(news[2].length() > 0);
                    colLabel2.setVisible(news[3].length() > 0);

                    rowLabel.revalidate();
                    colLabel.revalidate();
                }
            }
        };

        AbstractUserFieldEditorPanel[] panels = getEditablePanels();
        for (int i = 0; i < panels.length; i++) {
            panels[i].addPropertyChangeListener(l);
        }

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.weightx = 1.0;

        constraints.gridy = 0;
        panel.add(rowLabel, constraints);
        constraints.gridy++;
        panel.add(colLabel, constraints);

        panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        constraints.anchor = GridBagConstraints.EAST;
        constraints.weightx = 0.0;
        constraints.gridy = 0;
        panel.add(rowLabel2, constraints);
        constraints.gridy++;
        panel.add(colLabel2, constraints);

        return panel;
    }

    /* ********************* Ende: Initialisierungsteil ****************************** */

    /* ********************* Beginn: statische Methoden **************************** */

    /**
     * Liefert einen neuen <code>UserFieldEditorDialog</code>.<br>
     * 
     * @param owner Frame, das diesen Dialog enthält
     * @param gdcoll Modell, das die Daten für die Kennzahlen und Verteilungsgewichte der ModelElemente enthält
     * @return JDialog
     */
    public static JDialog getDialog(final Frame owner, final GDCollection gdcoll) {
        editor = new UserFieldEditorDialog(owner, gdcoll);
        return editor;
    }

    /* ********************* Ende: statische Methoden ****************************** */

    /* ********************* Beginn: get/set-Methoden ******************************** */

    /**
     * @return <code>[panelCN,panelDW,panelMV,panelCNF]</code>
     */
    private AbstractUserFieldEditorPanel[] getEditablePanels() {
        return new AbstractUserFieldEditorPanel[] {
                panelCN, panelDW, panelDWReplace, panelMV, panelCNF, panelAllOther
        };
    }

    /* ********************* Ende: get/set-Methoden ******************************** */

    /* *********************** Start: funktionale Methoden ******************************* */

    /**
     * Wenn das Drücken des {@link #okButton}s Grund für das Schließen des Dialogs ist, wurden bereits alle Änderungen übernommen. <br>
     * Falls nicht, wird {@link #cancelAction} ausgeführt, das heißt, dass eine Datenverlust-Warnung angezeigt wird und nach Wunsch des Users alle
     * Änderungen rückgängig gemacht werden, oder das Schließen des Dialogs abgebrochen wird. Außerdem wird beim Schließen des Dialoges seine Größe
     * und Position gespeichert und beim erneuten Öffnen wieder auf diese Werte gesetzt. Die Speicherung dieser Werte erfolgt allerdings nur, wenn der
     * Dialog durch "Ok" beendet wird.
     * 
     * @see java.awt.Window#dispose()
     */
    @Override
    public void dispose() {
        // Änderungen übernommen --> Schließe einfach
        // Sonst zeige Datenverlust-Warnung und mache u.U. Änderungen rückgängig
        if (okButtonPressed != true) {
            cancelAction.actionPerformed(null);
            if (shouldDispose == false) {
                shouldDispose = true;
                return;
            }
        }
        super.dispose();
    }

    /* *********************** Ende: funktionale Methoden ******************************* */

    @Override
    public Dimension getDefaultSize() {
        return DEFAULT_SIZE;
    }

}
