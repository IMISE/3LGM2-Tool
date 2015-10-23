package de.imise.tool3lgm.plugin.ilv;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.plugin.Plugin;

public class ILVServiceImportDialog extends JDialog implements Plugin {

    /**
     * Speichert die Größe des Dialogs nach dem Schließen
     */
    private static Dimension lastSize;

    /**
     * Speichert die letzte Postion des Dialogs nach dem Schließen
     */
    private static Point lastLocation;

    /**
     * Die Standardgröße des Dialoges
     */
    private static final Dimension DEFAULT_SIZE = new Dimension(600, 500);

    private JPanel mainPanel;

    private JPanel southButtonPanel;

    public ILVServiceImportDialog() {
        super(Tool3lgm.tool, "ILV Service Import");
        //        init();
    }

    public static Action getNewAction() {
        return new ILVServiceImportDialog().getAction();
    }

    private void init() {
        initPositionAndSize();
        initSouthPanel();
        initMainPanel();
        getContentPane().add(mainPanel);
    }

    private void initMainPanel() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(southButtonPanel, BorderLayout.SOUTH);
    }

    private void initPositionAndSize() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // Setzen von alter Größe und Position
        if (lastLocation != null && lastSize != null) {
            setSize(lastSize);
            setLocation(lastLocation);
        } else {
            setSize(DEFAULT_SIZE);
            setLocationByPlatform(true);
        }

    }

    private void initSouthPanel() {
        southButtonPanel = new JPanel(new FlowLayout());

        southButtonPanel.add(new JButton(new AbstractAction("Import") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                //TODO: Import
            }
        }));

        southButtonPanel.add(new JButton(new AbstractAction("Import CSV File") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                //TODO: Import CSV File
            }
        }));

        southButtonPanel.add(new JButton(new AbstractAction(Tool3lgmConstants.getResString("cancel")) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                dispose();
            }
        }));

    }

    @Override
    public Action getAction() {

        final ILVServiceImportDialog dialog = this;

        return new AbstractAction("ILV Service Import") {

            @Override
            public void actionPerformed(final ActionEvent e) {
                new ILVSimpleCSVFileImporter();
                //                dialog.setVisible(true);
            }

            @Override
            public boolean isEnabled() {
                return Tool3lgm.tool.getSelectedDoc() != null;
            }

        };
    }
}
