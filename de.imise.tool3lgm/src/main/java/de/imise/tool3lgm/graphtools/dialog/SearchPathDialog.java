/*
 * Created on 17.12.2003 To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package de.imise.tool3lgm.graphtools.dialog;

import java.awt.BorderLayout;
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
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.util.swing.component.DirectoryTreePane;

/**
 * @author Thomas Rudert TODO:AXS: Verallgemeinern und ab ins utils-package (also von allen
 *         tool3lgm-Zeug außerhalb des util-Packages befreien) To change the template for this
 *         generated type comment go to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and
 *         Comments
 */
public class SearchPathDialog extends JDialog implements ActionListener {
    private final JList searchList = new JList(new DefaultListModel() {
        @Override
        public void addElement(final Object obj) {
            if (obj instanceof File && !contains(obj)) {
                super.addElement(obj);
            }
        }
    });

    private final JCheckBox includeSubDir = new JCheckBox(Tool3lgmConstants.getResString("trans_subdir"));

    private DirectoryTreePane pathTree;

    public SearchPathDialog(final JDialog owner, final List<File> importPath) {
        super(owner, Tool3lgmConstants.getResString("trans_path"), true);
        init(importPath);
    }

    public SearchPathDialog(final JFrame owner, final List<File> importPath) {
        super(owner, Tool3lgmConstants.getResString("trans_path"), true);
        init(importPath);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        if (e.getActionCommand().equals("hinzufuegen")) {
            File[] selection = pathTree.getSelectedDirectories();

            for (int i = 0; i < selection.length; i++) {
                if (selection[i].isDirectory()) {
                    ((DefaultListModel) searchList.getModel()).addElement(selection[i]);
                }
            }
        }

        if (e.getActionCommand().equals("entfernen")) {
            int[] indices = searchList.getSelectedIndices();
            for (int i = indices.length - 1; i >= 0; i--) {
                ((DefaultListModel) searchList.getModel()).removeElementAt(indices[i]);
            }
        }

        if (e.getActionCommand().equals("close")) {
            commit();
        }

    }

    private void commit() {
        UserProperties.clearXslSearchDir();
        if (includeSubDir.isSelected()) {
            UserProperties.addAllXslSearchDir(rekAddSubDir(getSelectedPath().toArray()));
        } else {
            UserProperties.addAllXslSearchDir(getSelectedPath());
        }
        dispose();
    }

    private List<File> getSelectedPath() {
        List<File> fileArray = new ArrayList<File>(searchList.getModel().getSize());
        for (int i = 0; i < searchList.getModel().getSize(); i++) {
            fileArray.add((File) searchList.getModel().getElementAt(i));
        }
        return fileArray;
    }

    private void init(final List<File> importPath) {
        setSize(400, 400);
        getContentPane().setLayout(new BorderLayout());

        JPanel panel1 = new JPanel();
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

        JPanel panel = new JPanel(new BorderLayout(0, 3));
        panel.add(pathTree = new DirectoryTreePane(), BorderLayout.CENTER);
        panel.add(new JLabel(Tool3lgmConstants.getResString("trans_dir") + ":"), BorderLayout.NORTH);
        panel.setPreferredSize(getPreferredSize());
        panel1.add(panel);
        panel1.add(Box.createHorizontalStrut(5));

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JButton button = new JButton(Tool3lgmConstants.getIcon("arrow_right2.gif"));
        button.addActionListener(this);
        button.setActionCommand("hinzufuegen");
        panel.add(button);
        button = new JButton(Tool3lgmConstants.getIcon("arrow_left2.gif"));
        button.addActionListener(this);
        button.setActionCommand("entfernen");
        panel.add(button);
        panel1.add(panel);
        panel1.add(Box.createHorizontalStrut(5));

        panel = new JPanel(new BorderLayout(0, 3));
        panel.add(new JScrollPane(searchList), BorderLayout.CENTER);
        panel.add(new JLabel(Tool3lgmConstants.getResString("trans_path") + ":"), BorderLayout.NORTH);
        panel.setPreferredSize(getPreferredSize());
        panel1.add(panel);
        getContentPane().add(panel1, BorderLayout.CENTER);

        panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        panel.add(includeSubDir);
        button = new JButton(Tool3lgmConstants.getResString("trans_close"));
        button.addActionListener(this);
        button.setActionCommand("close");
        panel.add(button);
        getContentPane().add(panel, BorderLayout.SOUTH);

        for (int i = 0; i < importPath.size(); i++) {
            ((DefaultListModel) searchList.getModel()).addElement(importPath.get(i));
        }
    }

    private List<File> rekAddSubDir(final Object[] path) {
        if (path == null || path.length == 0) {
            return new ArrayList<File>(0);
        }

        List<File> temp = new ArrayList<File>();
        for (int i = 0; i < path.length; i++) {
            File f = (File) path[i];
            temp.add(f);
            temp.addAll(rekAddSubDir(f.listFiles(new FileFilter() {

                @Override
                public boolean accept(final File file) {
                    return file.isDirectory() && !file.toString().equals(".") && !file.toString().equals("..");
                }
            })));
        }
        return temp;
    }
}
