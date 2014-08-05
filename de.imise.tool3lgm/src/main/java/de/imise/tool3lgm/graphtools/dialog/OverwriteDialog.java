package de.imise.tool3lgm.graphtools.dialog;

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

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.util.swing.component.text.ExtendedTextPane;

/**
 * @author Thomas Rudert
 */
public class OverwriteDialog extends JDialog implements ActionListener {

    public static final int OVERWRITE = 1 << 2;
    public static final int JOIN = 1 << 3;
    public static final int DONOTHING = 1 << 4;

    public static final int REMEMBER = 1;

    private final JCheckBox rememberCheckBox;
    private int exit_status = -1;

    private OverwriteDialog(final Frame owner, final ModelElement me1, final ModelElement me2) throws HeadlessException {
        super(owner);

        setModal(true);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        getContentPane().setLayout(new BorderLayout(5, 5));
        getContentPane().add(new JLabel(Tool3lgmConstants.getResString("overwriteDialog_text")), BorderLayout.NORTH);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton button;
        button = new JButton(Tool3lgmConstants.getResString("overwriteDialog_join"));
        button.addActionListener(this);
        button.setActionCommand("join");
        panel.add(button);
        button = new JButton(Tool3lgmConstants.getResString("overwriteDialog_ignore"));
        button.addActionListener(this);
        button.setActionCommand("donothing");
        panel.add(button);
        getContentPane().add(panel, BorderLayout.SOUTH);

        panel = new JPanel(new BorderLayout());
        rememberCheckBox = new JCheckBox(Tool3lgmConstants.getResString("overwriteDialog_remember"), false);
        panel.add(rememberCheckBox, BorderLayout.SOUTH);

        JPanel panel2 = new JPanel();
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));
        panel.add(panel2, BorderLayout.CENTER);
        getContentPane().add(panel, BorderLayout.CENTER);
        ExtendedTextPane textPane = new ExtendedTextPane();
        textPane.setEditable(false);
        String text = me1 instanceof Kante ? me1.getName() + "\n--------------------\n" + ((Kante) me1).getStart().getClearName() + "\n - \n" + ((Kante) me1).getEnd().getClearName() + "\n--------------------\n" + ":\n\n" + me1.getDescription() : me1
                .getName() + ":\n\n" + me1.getDescription();
        textPane.setText(text);
        panel2.add(new JScrollPane(textPane));
        textPane = new ExtendedTextPane();
        textPane.setEditable(false);
        text = me2 instanceof Kante ? me2.getName() + "\n--------------------\n" + ((Kante) me2).getStart().getClearName() + "\n - \n" + ((Kante) me2).getEnd().getClearName() + "\n--------------------\n" + ":\n\n" + me2.getDescription() : me2.getName()
                + ":\n\n" + me2.getDescription();
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
     * @param owner
     * @param old
     * @param insert
     * @return
     */
    public static int showDialog(final Frame owner, final ModelElement old, final ModelElement insert) {
        OverwriteDialog dialog = new OverwriteDialog(owner, old, insert);

        dialog.setVisible(true);
        dim = dialog.getPreferredSize();

        return dialog.exit_status | (dialog.rememberCheckBox.isSelected() ? 1 : 0);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        if (e.getActionCommand().equals("overwrite")) {
            exit_status = OVERWRITE;
        } else if (e.getActionCommand().equals("join")) {
            exit_status = JOIN;
        } else if (e.getActionCommand().equals("donothing")) {
            exit_status = DONOTHING;
        } else {
            return;
        }

        dispose();
    }
}
