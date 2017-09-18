/*
 * Created on 08.12.2003 To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package de.imise.tool3lgm.graphtools.dialog;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.Tool3lgmConstants.FileFilterType;
import de.imise.tool3lgm.graphtools.dialog.tools.SzenarioTableModel;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GDCollectionImExportHandler;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.util.swing.component.text.ExtendedTextField;
import de.imise.util.swing.dialog.ExtendedFileChooser;

/**
 * @author Thomas Rudert To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SzenarioDialog extends JDialog {

    private final GDCollection gdcoll;
    private ExtendedTextField destination;
    private final JTable table;
    private JButton ok;

    private List<Szenario> selectedSzenarios = ImmutableList.of();

    /**
     * @param owner, Frame, which is the owner of this dialog
     * @param collection, GDCollection
     * @throws java.awt.HeadlessException
     */
    public SzenarioDialog(final Frame owner, final GDCollection gdcoll, final boolean forImport) throws HeadlessException {
        super(owner, forImport ? getResString("importSzenario") : getResString("exportSzenario"), true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.gdcoll = gdcoll;

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new JLabel(getResString("labelSource") + gdcoll.getName()), BorderLayout.NORTH);
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        if (forImport) {
            ok = new JButton(new AbstractAction(getResString("importSzenario")) {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    selectedSzenarios = ((SzenarioTableModel) table.getModel()).getSelectedSzenarios();
                    dispose();
                }
            });
        } else {
            ok = new JButton(new AbstractAction(getResString("exportSzenario")) {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    if (commit()) {
                        dispose();
                    }
                }
            });
            ok.setEnabled(false);
        }
        panel.add(ok);

        if (!forImport) {
            panel.add(new JButton(new AbstractAction(getResString("explore")) {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    changeFile();
                }
            }));
        }

        panel.add(new JButton(new AbstractAction(getResString("cancel")) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                dispose();
            }
        }));
        getContentPane().add(panel, BorderLayout.SOUTH);

        panel = new JPanel(new BorderLayout());
        table = new JTable(new SzenarioTableModel(gdcoll, forImport ? getResString("labelImport") : getResString("labelExport")));
        table.setSelectionBackground(table.getBackground());
        table.setSelectionForeground(table.getForeground());
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(30);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(300);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        getContentPane().add(panel, BorderLayout.CENTER);

        if (!forImport) {
            JPanel panel2 = new JPanel(new BorderLayout(5, 20));
            destination = new ExtendedTextField();
            destination.addCaretListener(e -> {
                File f = new File(destination.getText());
                ok.setEnabled(f.isAbsolute() && !f.isDirectory());
            });
            JLabel label = new JLabel(getResString("labelDestination"));
            label.setLabelFor(destination);
            panel2.add(label, BorderLayout.WEST);
            panel2.add(destination, BorderLayout.CENTER);
            panel.add(panel2, BorderLayout.SOUTH);
        }

        pack();
    }

    /**
     * zeigt einen Dialog zum auswählen der Szenarios, die exportiert werden sollen und zur Auswahl
     * der Zieldatei, und exportiert die Szenarios anschließend in die Zieldatei
     *
     * @param owner das übergeordnete JFrame
     * @param collection die GDCollection aus der die Szenarios exportiert werden sollen
     */
    public static void showExportDialog(final JFrame owner, final GDCollection collection) {
        SzenarioDialog dialog = new SzenarioDialog(owner, collection, false);
        dialog.setVisible(true);
    }

    /**
     * zeigt einen Dialog zum auswählen der Szenarios, die importiert werden sollen
     *
     * @param owner das übergeordnete JFrame
     * @param collection die GDCollection aus der die Szenarios importiert werden sollen
     * @return Array mit den Szenarios die importiert werden sollen
     */
    public static List<Szenario> showImportDialog(final JFrame owner, final GDCollection collection) {
        SzenarioDialog dialog = new SzenarioDialog(owner, collection, true);
        dialog.setVisible(true);
        return dialog.getReturnValue();
    }

    public List<Szenario> getReturnValue() {
        return selectedSzenarios;
    }

    private boolean commit() {
        if (destination.getText() != null) {
            File file = new File(destination.getText());
            if (!file.isAbsolute() || file.isDirectory()) {
                return false;
            }
            if (file.exists() && file.length() > 0 && JOptionPane.showConfirmDialog(this, getResString("quest_overwrite") + "\n(" + file.toString() + ")", "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION) {
                return false;
            }

            Static.showProgressDialog();
            Static.setProgressDialogStatusLabel("exportSzenario");
            SzenarioTableModel tableModel = (SzenarioTableModel) table.getModel();
            List<Szenario> tmpSelectedSzenarios = tableModel.getSelectedSzenarios();
            GDCollectionImExportHandler imExportHandler = gdcoll.getImExportHandler();
            imExportHandler.exportSzenarios(tmpSelectedSzenarios, file);
            Static.closeProgressDialog();
        }

        return true;
    }

    private void changeFile() {
        ExtendedFileChooser saveDialog = new ExtendedFileChooser(null);
        saveDialog.setFileFilters(false, Tool3lgmConstants.getFileNameExtensionFilter(FileFilterType.LGM3_UNZIPPED));
        File file = new File(destination.getText());
        saveDialog.setCurrentDirectory(file);
        if (saveDialog.showSaveDialog(this) != ExtendedFileChooser.APPROVE_OPTION) {
            return;
        }
        file = saveDialog.getSelectedFile();
        destination.setText(file.toString());
    }
}
