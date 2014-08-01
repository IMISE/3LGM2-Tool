/*
 * Created on 08.12.2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package de.imise.tool3lgm.graphtools.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import de.imise.util.swing.component.text.ExtendedTextField;
import de.imise.util.swing.dialog.ExtendedFileChooser;

import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.Tool3lgmConstants.FileFilterType;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.Szenario;
import de.imise.tool3lgm.graphtools.dialog.tools.SzenarioTableModel;

/**
 * @author Thomas Rudert
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SzenarioDialog extends JDialog {

	private GDCollection collection;
	private ExtendedTextField destination;
	private JTable table;
	private JButton ok;
	
	private Szenario[] selectedSzenarios = new Szenario[0];

	/**
	 * @param owner, Frame, which is the owner of this dialog
	 * @param collection, GDCollection
	 * @throws java.awt.HeadlessException
	 */
	public SzenarioDialog(Frame owner, GDCollection collection, boolean forImport) throws HeadlessException {
		super(owner, forImport ? Tool3lgmConstants.getResString("importSzenario") : Tool3lgmConstants.getResString("exportSzenario"), true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.collection = collection;

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(new JLabel(Tool3lgmConstants.getResString("labelSource") + collection.getName()), BorderLayout.NORTH);
			
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		if (forImport)
			ok = new JButton(new AbstractAction(Tool3lgmConstants.getResString("importSzenario")) {
				@Override
				public void actionPerformed(final ActionEvent e) {
					selectedSzenarios = ((SzenarioTableModel)table.getModel()).getSelectedSzenarios();
					dispose();
				}
			});
				
		else {
			ok = new JButton(new AbstractAction(Tool3lgmConstants.getResString("exportSzenario")) {
				@Override
				public void actionPerformed(final ActionEvent e) {
					if (commit())
						dispose();
				}
			});
			ok.setEnabled(false);
		}
		panel.add(ok);
		
		if (!forImport) {
			panel.add(new JButton(new AbstractAction(Tool3lgmConstants.getResString("explore")) {
				@Override
				public void actionPerformed(final ActionEvent e) {
					changeFile();
				}
			}));	
		}
		
		panel.add(new JButton(new AbstractAction(Tool3lgmConstants.getResString("cancel")) {
			@Override
			public void actionPerformed(final ActionEvent e) {
				dispose();
			}
		}));
		getContentPane().add(panel, BorderLayout.SOUTH);
		
		panel = new JPanel(new BorderLayout());
		table = new JTable(new SzenarioTableModel(collection, (forImport ? Tool3lgmConstants.getResString("labelImport") : Tool3lgmConstants.getResString("labelExport"))));
		table.setSelectionBackground(table.getBackground());
		table.setSelectionForeground(table.getForeground());
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.getColumnModel().getColumn(0).setPreferredWidth(30);
		table.getColumnModel().getColumn(1).setPreferredWidth(120);
		table.getColumnModel().getColumn(2).setPreferredWidth(300);
		panel.add(new JScrollPane(table), BorderLayout.CENTER);
		getContentPane().add(panel, BorderLayout.CENTER);
		
		if (!forImport) {
			JPanel panel2 = new JPanel(new BorderLayout(5,20));
			destination = new ExtendedTextField(); 
			destination.addCaretListener(new CaretListener() {
				@Override
				public void caretUpdate(final CaretEvent e) {
					File f = new File(destination.getText());
					ok.setEnabled(f.isAbsolute() && !f.isDirectory());
				}

			});
			JLabel label = new JLabel(Tool3lgmConstants.getResString("labelDestination"));
			label.setLabelFor(destination);
			panel2.add(label, BorderLayout.WEST);
			panel2.add(destination, BorderLayout.CENTER);		
			panel.add(panel2, BorderLayout.SOUTH);
		}
		
		this.pack();
	}

	/**
	 * zeigt einen Dialog zum auswählen der Szenarios, die exportiert werden sollen und zur Auswahl der Zieldatei,
	 * und exportiert die Szenarios anschließend in die Zieldatei
	 * @param owner das übergeordnete JFrame 
	 * @param collection die GDCollection aus der die Szenarios exportiert werden sollen
	 */	
	public static void showExportDialog(JFrame owner, GDCollection collection) {
		SzenarioDialog dialog = new SzenarioDialog(owner, collection, false);
		dialog.setVisible(true);
	}
	
	/**
	 * zeigt einen Dialog zum auswählen der Szenarios, die importiert werden sollen
	 * @param owner das übergeordnete JFrame
	 * @param collection die GDCollection aus der die Szenarios importiert werden sollen
	 * @return Array mit den Szenarios die importiert werden sollen
	 */
	public static Szenario[] showImportDialog(JFrame owner, GDCollection collection) {
		SzenarioDialog dialog = new SzenarioDialog(owner, collection, true);
		dialog.setVisible(true);
		return dialog.getReturnValue();
	}
	
	public Szenario[] getReturnValue() {
		return selectedSzenarios;
	}

	private boolean commit() {
		if (destination.getText() != null) {
			File file = new File(destination.getText());
			if (!file.isAbsolute() || file.isDirectory()) return false;
			if (file.exists() && file.length() > 0 && JOptionPane.showConfirmDialog(this, Tool3lgmConstants.getResString("quest_overwrite") + "\n(" + file.toString() + ")", "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)!=JOptionPane.YES_OPTION) return false;

			Tool3lgm.tool.showProgressDialog();
			Tool3lgm.tool.setProgressDialogStatusLabel(Tool3lgmConstants.getResString("exportSzenario"));
			collection.exportSzenarios(((SzenarioTableModel)table.getModel()).getSelectedSzenarios(), file);
			Tool3lgm.tool.closeProgressDialog();	
		}
		
		return true;
	}
	
	private void changeFile() {
		ExtendedFileChooser saveDialog = new ExtendedFileChooser(null);
		saveDialog.setFileFilters(false, Tool3lgmConstants.getFileNameExtensionFilter(FileFilterType.LGM3_UNZIPPED));
		File file = new File(destination.getText());
		saveDialog.setCurrentDirectory(file);
		if (saveDialog.showSaveDialog(this) != ExtendedFileChooser.APPROVE_OPTION)
			return;
		file = saveDialog.getSelectedFile();
		destination.setText(file.toString());
	}	
}
