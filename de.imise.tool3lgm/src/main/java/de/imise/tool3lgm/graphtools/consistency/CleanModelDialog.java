package de.imise.tool3lgm.graphtools.consistency;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.Tool3lgmConstants;

/**
 * Dieser Dialog stellt Optionen zur Auswahl, Modelle hinsichtlich auszuwählender Bereiche
 * bereinigen zu lassen.
 * 
 * @author AXS created on 03.07.2007
 */
public class CleanModelDialog extends JDialog implements WindowListener {

    /**
     * Checkbox für die Option, inkonsistente Anwendungsbaustein-Konfigurationen zu löschen
     */
    private static Checkbox inconsistentAWBConfigsBox = new Checkbox(getString("clean_option_inconsistent_awbconfig"));
    /**
     * Checkbox für die Option, inkonsistente Aufgabe-Orgnaisationseinheiten-Kombinationen zu
     * löschen
     */
    private static Checkbox inconsistentAufOrgBox = new Checkbox(getString("clean_option_inconsistent_auforg"));
    /**
     * Checkbox für die Option, inkonsistente Datenverarbeitungsbaustein-Konfigurationen zu löschen
     */
    private static Checkbox inconsistentPDVBConfigsBox = new Checkbox(getString("clean_option_inconsistent_pdvbconfig"));

    /**
     * Checkbox für die Option, AWB-Konfigurationen an die Blattaufgaben zu kopieren
     */
    private static Checkbox copyAWBConfigToLeafBox = new Checkbox(getString("clean_option_copy_awbconfig_to_leaf"));
    /**
     * Checkbox für die Option, einen Dialog mit allen Aufgaben zu öffnen, die AWB-Konfigurationen
     * mit mehreren Anwendungsbausteinen besitzen.
     */
    private static Checkbox multipleAWBConfigBox = new Checkbox(getString("clean_option_multiple_awbconfig"));
    /**
     * Checkbox für die Option, einen Dialog mit allen Aufgaben zu öffnen, die mehrer gleiche
     * AWB-Konfigurationen besitzen.
     */
    private static Checkbox identicalAWBConfigBox = new Checkbox(getString("clean_option_identical_awbconfig"));

    /**
     * Liste aller Chekcboxes, damit man den Wert von allen auf einmal setzen kann.
     */
    private final Checkbox[] boxes = {
            inconsistentAWBConfigsBox, inconsistentAufOrgBox, inconsistentPDVBConfigsBox, copyAWBConfigToLeafBox, multipleAWBConfigBox, identicalAWBConfigBox
    };

    /**
     * Speichert einen bereits geöffneten Dialog, damit immer nur ein solcher Dialog geöffnet ist.
     * Beim Schließen wird diese Variable wieder <code>null</code> gesetzt.
     */
    private static CleanModelDialog dialog = null;

    /**
     * @param owner
     */
    private CleanModelDialog(final Frame owner) {
        super(owner, getString("clean_model"), true);

        // //////////////////////////////////////////////////////
        // Panel für das Löschen von inkonsistenten Elementen //
        // //////////////////////////////////////////////////////
        JPanel inconsistentElementSectionBoxPanel = new JPanel(new GridLayout(3, 1));
        inconsistentElementSectionBoxPanel.add(inconsistentAWBConfigsBox);
        inconsistentElementSectionBoxPanel.add(inconsistentAufOrgBox);
        inconsistentElementSectionBoxPanel.add(inconsistentPDVBConfigsBox);

        JPanel inconsistentElementSectionHelpPanel = new JPanel(new GridLayout(3, 1));
        inconsistentElementSectionHelpPanel.add(new HelpButton("clean_option_inconsistent_awbconfig", "clean_help_inconsistent_awbconfig"));
        inconsistentElementSectionHelpPanel.add(new HelpButton("clean_option_inconsistent_auforg", "clean_help_inconsistent_auforg"));
        inconsistentElementSectionHelpPanel.add(new HelpButton("clean_option_inconsistent_pdvbconfig", "clean_help_inconsistent_pdvbconfig"));

        JPanel inconsistentElementSectionPanel = new JPanel(new BorderLayout());
        inconsistentElementSectionPanel.setBorder(BorderFactory.createTitledBorder(getString("clean_section_inconsistent_elem")));
        inconsistentElementSectionPanel.add(inconsistentElementSectionBoxPanel, BorderLayout.CENTER);
        inconsistentElementSectionPanel.add(inconsistentElementSectionHelpPanel, BorderLayout.EAST);

        // ///////////////////////////////////////////////
        // Panel für das Auffinden AWB-Konfigurationen //
        // ///////////////////////////////////////////////

        JPanel awbconfigSectionBoxPanel = new JPanel(new GridLayout(3, 1));
        awbconfigSectionBoxPanel.add(copyAWBConfigToLeafBox);
        awbconfigSectionBoxPanel.add(multipleAWBConfigBox);
        awbconfigSectionBoxPanel.add(identicalAWBConfigBox);

        JPanel awbconfigSectionHelpPanel = new JPanel(new GridLayout(3, 1));
        awbconfigSectionHelpPanel.add(new HelpButton("clean_option_copy_awbconfig_to_leaf", "clean_help_copy_awbconfig_to_leaf"));
        awbconfigSectionHelpPanel.add(new HelpButton("clean_option_multiple_awbconfig", "clean_help_multiple_awbconfig"));
        awbconfigSectionHelpPanel.add(new HelpButton("clean_option_identical_awbconfig", "clean_help_identical_awbconfig"));

        JPanel awbconfigSectionPanel = new JPanel(new BorderLayout());
        awbconfigSectionPanel.setBorder(BorderFactory.createTitledBorder(getString("clean_section_awbconfigs")));
        awbconfigSectionPanel.add(awbconfigSectionBoxPanel, BorderLayout.CENTER);
        awbconfigSectionPanel.add(awbconfigSectionHelpPanel, BorderLayout.EAST);

        // /////////////////////////////////////////////
        // Panel für den OK und Abbrechen Knopf usw. //
        // /////////////////////////////////////////////

        JPanel okQuitButPanel = new JPanel();
        okQuitButPanel.add(new JButton(new AbstractAction(getString("button_select_all")) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                for (int i = 0; i < boxes.length; i++) {
                    boxes[i].setState(true);
                }
            }
        }));
        okQuitButPanel.add(new JButton(new AbstractAction(getString("button_deselect_all")) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                for (int i = 0; i < boxes.length; i++) {
                    boxes[i].setState(false);
                }
            }
        }));
        okQuitButPanel.add(new JButton(new AbstractAction(getString("ok")) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                setVisible(false);
                ModelCleaner mc = new ModelCleaner(Tool3lgm.tool.getSelectedDoc().getCollection());
                if (inconsistentAWBConfigsBox.getState()) {
                    mc.removeInconsistentAWBConfigurationsWithoutAWB(true);
                }
                if (inconsistentAufOrgBox.getState()) {
                    mc.removeInconsistentAufOrgKombinations(true);
                }
                if (inconsistentPDVBConfigsBox.getState()) {
                    mc.removeInconsistentPDVBConfigurations(true);
                }
                if (copyAWBConfigToLeafBox.getState()) {
                    mc.copyAufOrgKombinationsToAufLeafs(true);
                }
                setVisible(true);
            }
        }));
        okQuitButPanel.add(new JButton(new AbstractAction(getString("exit")) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                dispose();
            }
        }));

        // //////////////
        // Hauptpanel //
        // //////////////

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        mainPanel.add(inconsistentElementSectionPanel, BorderLayout.NORTH);
        mainPanel.add(awbconfigSectionPanel, BorderLayout.CENTER);
        mainPanel.add(okQuitButPanel, BorderLayout.SOUTH);

        getContentPane().add(mainPanel);
        addWindowListener(this);
        setLocation(100, 100);
        setResizable(false);
        pack();
    }

    /**
     * Liefert den über den Schlüssel in den Resoucen auffindbaren String.
     * 
     * @param resourceKey
     * @return
     */
    private static final String getString(final String resourceKey) {
        return Tool3lgmConstants.getResString(resourceKey);
    }

    /**
     * Liefert einen <code>CleanModelDialog</code>
     * 
     * @return
     */
    public static final CleanModelDialog getDialog() {
        if (dialog == null) {
            dialog = new CleanModelDialog(Tool3lgm.tool);
        }
        return dialog;
    }

    // //////////////////
    // WindowListener //
    // //////////////////

    @Override
    public void windowOpened(final WindowEvent e) {
    }

    @Override
    public void windowClosing(final WindowEvent e) {
        dialog = null;
    }

    @Override
    public void windowClosed(final WindowEvent e) {
        dialog = null;
    }

    @Override
    public void windowIconified(final WindowEvent e) {
    }

    @Override
    public void windowDeiconified(final WindowEvent e) {
    }

    @Override
    public void windowActivated(final WindowEvent e) {
    }

    @Override
    public void windowDeactivated(final WindowEvent e) {
    }

    // ////////////////
    // Hilfsklassen //
    // ////////////////

    /**
     * Ein Hilfebutton mit einer bestimmten Darstellung, der bei Aktivierung einen Dialog mit dem
     * Hilfetext präsentiert.
     * 
     * @author AXS created on 03.07.2007
     */
    private class HelpButton extends JButton {
        /**
         * Resourcenschlüssel für den zu öffnenden Hilfedialog
         */
        private final String titleKey;
        /**
         * Resourcenschlüssel für den Hilfetext
         */
        private final String helpKey;

        /**
         * @param dialogTitleKey Resourcenschlüssel für den zu öffnenden Hilfedialog
         * @param helpKey Resourcenschlüssel für den Hilfetext
         */
        public HelpButton(final String dialogTitleKey, final String helpTextKey) {
            super();
            titleKey = dialogTitleKey;
            helpKey = helpTextKey;
            setAction(new AbstractAction(getString("help_icon")) {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    JOptionPane.showMessageDialog(Tool3lgm.tool, CleanModelDialog.getString(helpKey), CleanModelDialog.getString(titleKey), JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }
    }

}
