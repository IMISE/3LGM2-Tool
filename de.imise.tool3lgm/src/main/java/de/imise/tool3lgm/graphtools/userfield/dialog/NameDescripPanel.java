/*
 * Created on 09.02.2008
 */
package de.imise.tool3lgm.graphtools.userfield.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.util.swing.component.text.ExtendedTextArea;
import de.imise.util.swing.component.text.ExtendedTextField;

/**
 * @author AXS
 */
public class NameDescripPanel extends AbstractInputPanel {

    /**
     * Das UserField, dessen Eigenschaften mit diesem Panel geändert werden sollen.
     */
    private final UserField userField;

    /**
     * Comment for <code>nameTextField</code>
     */
    private final ExtendedTextField nameTextField = new ExtendedTextField();

    /**
     * Comment for <code>decsripArea</code>
     */
    private final ExtendedTextArea descripArea = new ExtendedTextArea(8, 1);

    /**
     * @param userField
     */
    public NameDescripPanel(final UserField userField) {
        super(new GridBagLayout());
        this.userField = userField;

        nameTextField.setText(userField.getName());
        descripArea.setText(userField.getDescription());

        setBorder(BorderFactory.createTitledBorder(Tool3lgmConstants.getResString("general")));
        GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(3, 3, 3, 3), 0, 0);
        //die beiden Labels untereinander in der ersten Spalte einfügen
        add(new JLabel(Tool3lgmConstants.getResString("bez")), gbc);
        gbc.gridy = 1;
        add(new JLabel(Tool3lgmConstants.getResString("description")), gbc);

        //jetzt das Namentextfeld in der zweiten Spalte und ersten Zeile einfügen
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        add(nameTextField, gbc);

        //das Beschreibungsfeld unter das Namensfeld einfügen
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        add(new JScrollPane(descripArea), gbc);
        descripArea.setFont(nameTextField.getFont());
    }

    /**
     * @return Returns the nameTextField.
     */
    public ExtendedTextField getNameTextField() {
        return nameTextField;
    }

    @Override
    public void cancel() {
    }

    @Override
    public void commit() {
        userField.setName(nameTextField.getText());
        userField.setDescription(descripArea.getText());
    }

    @Override
    public String getError() {
        if (nameTextField.getText().trim().equals("")) {
            return Tool3lgmConstants.getErrString("empty_name");
        }
        return super.getError();
    }
}
