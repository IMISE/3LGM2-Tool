package de.imise.util.swing.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.imise.util.resource.SimpleResourceIconSource;
import de.imise.util.swing.component.DirectoryTreePane;

/**
 * @author Thomas Rudert (17.12.2003), AXS
 */
public class SelectMultiPathsDialog extends AbstractSizeAndPositionRestoringDialog implements ActionListener {

    /** The resoruce bundle handler */
    private static final DialogResourceHandler drh = new DialogResourceHandler(SelectMultiPathsDialog.class);

    /** Checkbox to enable the option to include sub directories */
    private final JCheckBox includeSubDir = new JCheckBox(drh.getResString("trans_subdir"));

    /** Treepane to display the paths */
    private DirectoryTreePane pathTree;

    /**
     * Exit code of the dialog. Is only changed to 1 if the dialog is closed by
     * the Close button
     */
    private int exitCode = -1;

    /** The list that displays all selected paths */
    private final JList<File> searchList = new JList<>(new DefaultListModel<File>() {
        @Override
        public void addElement(final File obj) {
            if (!contains(obj)) {
                super.addElement(obj);
            }
        }
    });

    /**
     * @param owner
     * @param title
     * @param importPath
     */
    public SelectMultiPathsDialog(final JDialog owner, String title, final List<File> importPath) {
        super(owner, title, true);
        init(importPath);
    }

    /**
     * @param owner
     * @param title
     * @param importPath
     */
    public SelectMultiPathsDialog(final JFrame owner, String title, final List<File> importPath) {
        super(owner, title, true);
        init(importPath);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        if (e.getActionCommand().equals("hinzufuegen")) {
            File[] selection = pathTree.getSelectedDirectories();

            for (int i = 0; i < selection.length; i++) {
                if (selection[i].isDirectory()) {
                    ((DefaultListModel<File>) searchList.getModel()).addElement(selection[i]);
                }
            }
        }

        if (e.getActionCommand().equals("entfernen")) {
            int[] indices = searchList.getSelectedIndices();
            for (int i = indices.length - 1; i >= 0; i--) {
                ((DefaultListModel<File>) searchList.getModel()).removeElementAt(indices[i]);
            }
        }

        if (e.getActionCommand().equals("close")) {
            commit();
        }

    }

    @Override
    public Dimension getDefaultSize() {
        return new Dimension(600, 400);
    }

    /**
     *
     */
    private void commit() {
        exitCode = 1;
        dispose();
    }

    /**
     * @return
     */
    public List<File> getResultPaths() {
        List<File> selectedPaths = getSelectedPaths();
        if (includeSubDir.isSelected()) {
            return rekAddSubDir(selectedPaths.toArray());
        }
        return selectedPaths;
    }

    /**
     * @return <code>true</code> the dilaog was closed by the Close button. In
     *         all other cases it returns <code>false</code>.
     */
    public boolean isDisposedViaCloseButton() {
        return exitCode == 1;
    }

    /**
     * @return
     */
    private List<File> getSelectedPaths() {
        List<File> fileArray = new ArrayList<>(searchList.getModel().getSize());
        for (int i = 0; i < searchList.getModel().getSize(); i++) {
            fileArray.add(searchList.getModel().getElementAt(i));
        }
        return fileArray;
    }

    /**
     * @param importPath
     */
    private void init(final List<File> importPath) {
        getContentPane().setLayout(new BorderLayout());

        JPanel panel1 = new JPanel();
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

        JPanel panel = new JPanel(new BorderLayout(0, 3));
        panel.add(pathTree = new DirectoryTreePane(), BorderLayout.CENTER);
        panel.add(new JLabel(drh.getResString("trans_dir_struct") + ":"), BorderLayout.NORTH);
        panel.setPreferredSize(getPreferredSize());
        panel1.add(panel);
        panel1.add(Box.createHorizontalStrut(5));

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        ImageIcon icon = SimpleResourceIconSource.getIcon(getClass(), "ICON_LARGE_ARROW_RIGHT");
        JButton button = new JButton(icon);
        button.addActionListener(this);
        button.setActionCommand("hinzufuegen");
        panel.add(button);
        icon = SimpleResourceIconSource.getIcon(getClass(), "ICON_LARGE_ARROW_LEFT");
        button = new JButton(icon);
        button.addActionListener(this);
        button.setActionCommand("entfernen");
        panel.add(button);
        panel1.add(panel);
        panel1.add(Box.createHorizontalStrut(5));

        panel = new JPanel(new BorderLayout(0, 3));
        panel.add(new JScrollPane(searchList), BorderLayout.CENTER);
        panel.add(new JLabel(drh.getResString("trans_dirs") + ":"), BorderLayout.NORTH);
        panel.setPreferredSize(getPreferredSize());
        panel1.add(panel);
        getContentPane().add(panel1, BorderLayout.CENTER);

        panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        panel.add(includeSubDir);
        button = new JButton(drh.getResString("trans_close"));
        button.addActionListener(this);
        button.setActionCommand("close");
        panel.add(button);
        getContentPane().add(panel, BorderLayout.SOUTH);

        for (int i = 0; i < importPath.size(); i++) {
            ((DefaultListModel<File>) searchList.getModel()).addElement(importPath.get(i));
        }
    }

    /**
     * @param path
     * @return
     */
    private List<File> rekAddSubDir(final Object[] path) {
        if (path == null || path.length == 0) {
            return new ArrayList<>(0);
        }

        List<File> temp = new ArrayList<>();
        for (int i = 0; i < path.length; i++) {
            File f = (File) path[i];
            temp.add(f);
            temp.addAll(rekAddSubDir(f.listFiles((FileFilter) file -> file.isDirectory() && !file.toString().equals(".") && !file.toString().equals(".."))));
        }
        return temp;
    }
}
