/*
 * Created on 10.02.2008
 */
package de.imise.tool3lgm.graphtools.userfield.dialog;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.util.swing.component.text.ExtendedTextField;

/**
 * @author AXS
 */
public class ListValuePanel extends AbstractInputPanel {

    /**
     * Das <code>UserField</code>, dessen Listeneinträge geändert werden sollen.
     */
    private final UserField userField;

    /**
     * Eingabefeld für die Listenwerte
     */
    private final ExtendedTextField valueListTextField = new ExtendedTextField();

    /**
     * @param userField
     */
    public ListValuePanel(final UserField userField) {
        super();
        this.userField = userField;

        setBorder(BorderFactory.createTitledBorder(Tool3lgmConstants.getResString("userFieldEditor_values")));
        setLayout(new BorderLayout());
        add(new JLabel(Tool3lgmConstants.getResString("userFieldEditor_values_hint")), BorderLayout.NORTH);

        add(valueListTextField, BorderLayout.CENTER);

        StringBuilder sb = new StringBuilder();
        if (userField.getListValuesCount() > 0) {
            sb.append(userField.getListValueAt(0));
        }
        for (int i = 1; i < userField.getListValuesCount(); i++) {
            sb.append("; ");
            sb.append(userField.getListValueAt(i));
        }
        valueListTextField.setText(sb.toString());
    }

    @Override
    public void cancel() {
    }

    @Override
    public void commit() {
        userField.removeAllStandardValues();
        String[] tokens = valueListTextField.getText().split(";");
        for (int i = 0; i < tokens.length; i++) {
            userField.addListValue(tokens[i].trim());
        }
    }

}