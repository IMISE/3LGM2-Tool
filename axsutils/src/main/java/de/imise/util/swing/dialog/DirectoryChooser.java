/*
 * Created on 16.01.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package de.imise.util.swing.dialog;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;

import de.imise.util.swing.component.DirectoryTreeNode;
import de.imise.util.swing.component.DirectoryTreePane;
import de.imise.util.swing.component.text.ExtendedTextField;


/**
 * @author Thomas Rudert
 * 
 * Dialog zum auswählen eines Verzeichnisses.<br>
 * 
 * Die Datei besteht aus den mehreren Klassen, die alle für den  <code>DirectoryChooser</code>
 * gebraucht werden. 
 */
public class DirectoryChooser extends JDialog {

	/**
	 * choosed directory
	 */
	private File selectedDir;

	private boolean approveOption = false;

	private DirectoryTreePane pane;

	private static final DialogResourceHandler drh = new DialogResourceHandler(DirectoryChooser.class);

	
	/**
	 * @param owner
	 * @param selectDir
	 * @throws java.awt.HeadlessException
	 */
	private DirectoryChooser(Dialog owner) throws HeadlessException {
		super(owner, "labelChooseDir", true);
		init();
	}

	/**
	 * @param owner
	 * @param selectDir
	 * @throws java.awt.HeadlessException
	 */
	private DirectoryChooser(Frame owner) throws HeadlessException {
		super(owner, true);
		init();
	}

	/**
	 * show a Dailog to choose a Directory
	 * 
	 * @param owner
	 *            the owner of the Dialog
	 * @return File with directory, if a directory was selected and the Dialog
	 *         was closed with APPROVE_OPTION; otherwise null
	 */
	public static File showDialog(Dialog owner) {
		return showDialog(owner, "");
	}

	/**
	 * show a Dailog to choose a Directory
	 * 
	 * @param owner
	 *            the owner of the Dialog
	 * @param newDirName
	 *            defaultName for a created directory
	 * @return File with directory, if a directory was selected and the Dialog
	 *         was closed with APPROVE_OPTION; otherwise null
	 */
	public static File showDialog(Dialog owner, String newDirName) {
		DirectoryChooser dialog = new DirectoryChooser(owner);
		dialog.setVisible(true);
		return (dialog.approveOption ? dialog.selectedDir : null);
	}

	/**
	 * show a Dailog to choose a Directory
	 * 
	 * @param owner
	 *            the owner of the Dialog
	 * @return File with directory, if a directory was selected and the Dialog
	 *         was closed with APPROVE_OPTION; otherwise null
	 */
	public static File showDialog(Frame owner) {
		return showDialog(owner, "");
	}

	/**
	 * show a Dailog to choose a Directory
	 * 
	 * @param owner
	 *            the owner of the Dialog
	 * @param newDirName
	 *            defaultName for a created directory
	 * @return File with directory, if a directory was selected and the Dialog
	 *         was closed with APPROVE_OPTION; otherwise null
	 */
	public static File showDialog(Frame owner, String newDirName) {
		DirectoryChooser dialog = new DirectoryChooser(owner);
		dialog.setVisible(true);
		return (dialog.approveOption ? dialog.selectedDir : null);
	}

	/**
	 *  
	 */
	private void init() {
		getContentPane().setLayout(new BorderLayout());

		final ExtendedTextField location = new ExtendedTextField("");
		location.setEditable(false);
		getContentPane().add(location, BorderLayout.NORTH);

		final JButton button;

		setTitle(drh.getResString("labelChooseDir"));

		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		button = new JButton(new AbstractAction(drh.getResString("labelNewDir")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (selectedDir == null)
					return;
				String dirName = JOptionPane.showInputDialog(null, DirectoryChooser.drh.getResString("name"), DirectoryChooser.drh.getResString("labelNewDir"), JOptionPane.QUESTION_MESSAGE);
					
				if (dirName == null)
					return;
				pane.createDir(dirName);
			}
		});
		button.setEnabled(false);
		panel.add(button);

		panel.add(new JButton(new AbstractAction(drh.getResString("ok")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				approveOption = true;
				dispose();

			}
		}));
		panel.add(new JButton(new AbstractAction(drh.getResString("cancel")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		}));
		getContentPane().add(panel, BorderLayout.SOUTH);

		pane = new DirectoryTreePane();
		pane.setMultiSelection(false);
		pane.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				File selectedDirectory = ((DirectoryTreeNode) e.getPath().getLastPathComponent()).getDirectory();
				selectedDir = FileSystemView.getFileSystemView().isFileSystem(selectedDirectory) ? selectedDirectory : null;
				location.setText(selectedDirectory.toString());
				button.setEnabled(selectedDir != null);
			}
		});
		getContentPane().add(pane, BorderLayout.CENTER);

		pack();

		int x = getOwner().getX() + (getOwner().getWidth() - this.getWidth()) / 2;
		int y = getOwner().getY() + (getOwner().getHeight() - this.getHeight()) / 2;
		x = x > 0 ? x : 0;
		y = y > 0 ? y : 0;
		x = x + this.getHeight() < getGraphicsConfiguration().getDevice().getDisplayMode().getWidth() ? x : getGraphicsConfiguration().getDevice().getDisplayMode().getWidth() - this.getWidth();
		y = y + this.getHeight() < getGraphicsConfiguration().getDevice().getDisplayMode().getHeight() ? y : getGraphicsConfiguration().getDevice().getDisplayMode().getHeight() - this.getHeight();
		setLocation(x, y);
	}
}


