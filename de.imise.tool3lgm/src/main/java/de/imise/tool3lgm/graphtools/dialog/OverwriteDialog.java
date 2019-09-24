package de.imise.tool3lgm.graphtools.dialog;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.dialog.OverwriteDialog.OverwriteOption.IGNORE;
import static de.imise.tool3lgm.graphtools.dialog.OverwriteDialog.OverwriteOption.JOIN;
import static de.imise.tool3lgm.graphtools.dialog.OverwriteDialog.OverwriteOption.OVERWRITE;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.util.swing.component.text.ExtendedTextPane;

/**
 * @author Thomas Rudert
 */
public class OverwriteDialog extends JDialog implements ActionListener {

    public enum OverwriteOption {
        OVERWRITE, // das vorhandene Element wird ersetzt
        JOIN, // das vorhandene und das neue Element werden zusammengeführt
        IGNORE // das vorhandene Element bleibt unverändert
    }

    public static class OverwriteQuestionAnswer {
        public final OverwriteOption overwriteOption;
        public final boolean applyToAll;

        public OverwriteQuestionAnswer(final OverwriteOption overwriteOption, final boolean applyToAll) {
            this.overwriteOption = overwriteOption;
            this.applyToAll = applyToAll;
        }
    }

    private final JCheckBox rememberCheckBox;
    private OverwriteQuestionAnswer exit_status = null;

    private OverwriteDialog(final Frame owner, final ModelElement me1, final ModelElement me2) throws HeadlessException {
        super(owner);

        setModal(true);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        getContentPane().setLayout(new BorderLayout(5, 5));
        getContentPane().add(new JLabel(getResString("overwriteDialog_text")), BorderLayout.NORTH);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton button;
        button = new JButton(getResString("overwriteDialog_join"));
        button.addActionListener(this);
        button.setActionCommand(JOIN.name());
        panel.add(button);
        button = new JButton(getResString("overwriteDialog_ignore"));
        button.addActionListener(this);
        button.setActionCommand(IGNORE.name());
        panel.add(button);
        getContentPane().add(panel, BorderLayout.SOUTH);

        panel = new JPanel(new BorderLayout());
        rememberCheckBox = new JCheckBox(getResString("overwriteDialog_remember"), false);
        panel.add(rememberCheckBox, BorderLayout.SOUTH);

        JPanel panel2 = new JPanel();
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));
        panel.add(panel2, BorderLayout.CENTER);
        getContentPane().add(panel, BorderLayout.CENTER);
        ExtendedTextPane textPane = new ExtendedTextPane();
        textPane.setEditable(false);
        String text = me1 instanceof Edge ? me1.getName() + "\n--------------------\n" + ((Edge) me1).getStart().getClearName() + "\n - \n" + ((Edge) me1).getEnd().getClearName() + "\n--------------------\n" + ":\n\n" + me1.getDescription()
                : me1.getName() + ":\n\n" + me1.getDescription();
        textPane.setText(text);
        panel2.add(new JScrollPane(textPane));
        textPane = new ExtendedTextPane();
        textPane.setEditable(false);
        text = me2 instanceof Edge ? me2.getName() + "\n--------------------\n" + ((Edge) me2).getStart().getClearName() + "\n - \n" + ((Edge) me2).getEnd().getClearName() + "\n--------------------\n" + ":\n\n" + me2.getDescription()
                : me2.getName() + ":\n\n" + me2.getDescription();
        textPane.setText(text);
        panel2.add(new JScrollPane(textPane));

        setPreferredSize(dim);
        pack();
    }

    /**
     * COMMENTME
     */
    static Dimension dim = new Dimension(600, 300);

    /**
     * @param old
     * @param insert
     * @return
     */
    public static OverwriteQuestionAnswer showDialog(final ModelElement old, final ModelElement insert) {
        return showDialog(Static.getMainFrame(), old, insert);
    }

    /**
     * @param owner
     * @param old
     * @param insert
     * @return
     */
    public static OverwriteQuestionAnswer showDialog(final Frame owner, final ModelElement old, final ModelElement insert) {
        OverwriteDialog dialog = new OverwriteDialog(owner, old, insert);
        dialog.setVisible(true);
        dim = dialog.getPreferredSize();
        return dialog.exit_status;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        OverwriteOption option = null;
        if (e.getActionCommand().equals(OVERWRITE.name())) {
            option = OVERWRITE;
        } else if (e.getActionCommand().equals(JOIN.name())) {
            option = JOIN;
        } else if (e.getActionCommand().equals(IGNORE.name())) {
            option = IGNORE;
        }
        exit_status = new OverwriteQuestionAnswer(option, rememberCheckBox.isSelected());
        dispose();
    }
}
