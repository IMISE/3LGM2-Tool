/*
 * Created on 22.04.2008
 */
package de.imise.tool3lgm.graphtools.dialog;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.rmi.registry.Registry;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.tool3lgm.userproperties.UserProperties.IntProperty;

/**
 * @author hboehme Eigenschaftenpanel für die RMI-Konfiguration. Das Panel beinhaltet zwei
 *         <code>JLabel</code>s, eine <code>JCheckBox</code> und ein <code>JTextField</code> Wenn
 *         die <code>JCheckBox</code> aktiviert wird, wird bem nächsten Systemstart die RMI-Funktion
 *         geladen, sonst nicht. In das <code>JTextField</code> wird der Port eingetragen, auf dem
 *         der RMI-Server lauschen soll.
 */
public class RMIPropertyPanel extends JPanel {

    /** hier wird eingetragen und dargestellt, auf welchem Pot der RMI-Server lauschen soll */
    private final JTextField rmiRegistryPortTextField = new JTextField();

    /**
     * Das Panel beinhaltet zwei <code>JLabel</code>s, eine <code>JCheckBox</code> und ein
     * <code>JTextField</code> Wenn die <code>JCheckBox</code> aktiviert wird, wird bem nächsten
     * Systemstart die RMI-Funktion geladen, sonst nicht. In das <code>JTextField</code> wird der
     * Port eingetragen, auf dem der RMI-Server lauschen soll.
     */
    public RMIPropertyPanel() {
        super();

        setLayout(new GridBagLayout());

        rmiRegistryPortTextField.setMinimumSize(new Dimension(100, 21));
        rmiRegistryPortTextField.setPreferredSize(new Dimension(100, 21));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 3);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridy = 0;

        gbc.gridx = 0;
        add(new JLabel(getResString("registryPort")), gbc);
        gbc.gridx++;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        add(rmiRegistryPortTextField, gbc);
        gbc.gridwidth = 0;
        gbc.gridx++;

        JButton stdRegistryButton = new JButton();
        add(stdRegistryButton, gbc);
        // Wenn der Button betätig wird, wird in das TextField der standardport eingetragen, der für
        // RMI vorgesehen ist. Derzeit 1099.
        stdRegistryButton.setAction(new AbstractAction(getResString("standardPort")) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                rmiRegistryPortTextField.setText("" + Registry.REGISTRY_PORT);
            }
        });

        gbc.gridx = 0;
        gbc.gridy++;

        int rmiPort = UserProperties.get(IntProperty.PROPERTY_INT_RMI_PORT);
        if (rmiPort < 0) {
            rmiRegistryPortTextField.setText("");
        } else {
            rmiRegistryPortTextField.setText(String.valueOf(rmiPort));
        }
    }

    /**
     * @return Returns the the value of rmiRegistryPortTextField.
     */
    public int getRmiRegistryPortTextFieldValue() {
        try {
            return Integer.valueOf(rmiRegistryPortTextField.getText());
        } catch (Exception e) {
            return -1;
        }
    }
}
