/*
 * Created on 22.04.2008
 */
package de.imise.tool3lgm.graphtools.dialog;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.userproperties.UserProperties.IntProperty.PROPERTY_INT_RMI_PORT;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.rmi.registry.Registry;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author hboehme RMI-fehlerPanel für die RMI-Konfiguration. Das Panel
 *         beinhaltet zwei <code>JLabel</code>s, eine <code>JCheckBox</code> und
 *         ein <code>JTextField</code> Wenn die <code>JCheckBox</code> aktiviert
 *         wird, wird versucht für den RMI-Service der nächste freie Port zu
 *         finden, sonst nicht. In das <code>JTextField</code> wird vom User der
 *         Port eingetragen, auf dem der RMI-Server lauschen soll. Sollte das
 *         auch wieder fhelschlagen, wird der Dialog erneut angeuzeigt.
 */
public class RMIErrorPanel extends JPanel implements ItemListener {

    /**
     * Hier wird festgelegt, ob automatisch ein neuer freie Port gesucht werden
     * soll
     */
    private final JCheckBox rmiAutoNextFreePortCheckBox = new JCheckBox();

    /**
     * hier wird vom User eingetragen und dargestellt, auf welchem Pot der
     * RMI-Server lauschen soll
     */
    private final JTextField rmiRegistryPortTextField = new JTextField();

    /** Der Button setzt den Standardport 1099 in das TextField. */
    private final JButton rmiStdRegistryButton = new JButton();

    /**
     * Das Panel beinhaltet zwei <code>JLabel</code>s, eine
     * <code>JCheckBox</code> und ein <code>JTextField</code> Wenn die
     * <code>JCheckBox</code> aktiviert wird, wird versucht ein neue freier Port
     * für den RMI-Service zu finden, sonst nicht. In das
     * <code>JTextField</code> wird vom User der Port eingetragen, auf dem der
     * RMI-Server lauschen soll.
     */
    public RMIErrorPanel() {
        super();

        setLayout(new GridBagLayout());

        rmiAutoNextFreePortCheckBox.addItemListener(this);
        rmiRegistryPortTextField.setMinimumSize(new Dimension(100, 21));
        rmiRegistryPortTextField.setPreferredSize(new Dimension(100, 21));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 3);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;

        // / CheckBox und Hinweistext

        gbc.gridwidth = 2;
        add(new JLabel(getResString("rmiErrorDescription")), gbc);
        gbc.gridy++;
        gbc.gridwidth = 1;

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(getResString("rmiSearchNextFreePort")), BorderLayout.WEST);
        panel.add(rmiAutoNextFreePortCheckBox, BorderLayout.EAST);
        add(panel, gbc);

        gbc.gridx++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        add(new JLabel(""), gbc);

        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        // / Bunutzereingabe für den Port

        JPanel panel2 = new JPanel(new BorderLayout(3, 3));

        gbc.gridx = 0;
        gbc.gridy++;
        panel2.add(new JLabel(getResString("registryPort")), BorderLayout.WEST);
        panel2.add(rmiRegistryPortTextField, BorderLayout.CENTER);

        gbc.gridy++;
        gbc.gridx = 0;

        panel2.add(rmiStdRegistryButton, BorderLayout.EAST);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        add(panel2, gbc);
        // Wenn der Button betätig wird, wird in das TextField der Standardport eingetragen, der für
        // RMI vorgesehen ist. Derzeit 1099.
        rmiStdRegistryButton.setAction(new AbstractAction(getResString("standardPort")) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                rmiRegistryPortTextField.setText("" + Registry.REGISTRY_PORT);
            }
        });

        gbc.gridx++;
        add(new JLabel(""), gbc);

        int rmiPort = PROPERTY_INT_RMI_PORT.get();
        if (rmiPort < 0) {
            rmiRegistryPortTextField.setText("");
        } else {
            rmiRegistryPortTextField.setText(String.valueOf(rmiPort));
        }

        rmiAutoNextFreePortCheckBox.setSelected(true);
    }

    /**
     * @return Returns the value of <code>rmiAutoNextFreePortCheckBox</code>.
     */
    public boolean isRmiAutoNextFreePortCheckBox() {
        return rmiAutoNextFreePortCheckBox.isSelected();
    }

    /**
     * @return Returns the the value of <code>rmiRegistryPortTextField</code>.
     */
    public String getRmiRegistryPortTextFieldValue() {
        return rmiRegistryPortTextField.getText();
    }

    @Override
    public void itemStateChanged(final ItemEvent e) {
        if (rmiAutoNextFreePortCheckBox.isSelected()) {
            rmiRegistryPortTextField.setEditable(false);
            rmiStdRegistryButton.setEnabled(false);
        } else {
            rmiRegistryPortTextField.setEditable(true);
            rmiStdRegistryButton.setEnabled(true);
        }
    }
}
