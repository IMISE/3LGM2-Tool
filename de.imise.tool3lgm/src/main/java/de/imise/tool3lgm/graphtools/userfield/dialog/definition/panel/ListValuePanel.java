/*
 * Created on 10.02.2008
 */
package de.imise.tool3lgm.graphtools.userfield.dialog.definition.panel;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import de.imise.tool3lgm.graphtools.userfield.definition.UserField;
import de.imise.util.swing.component.text.ExtendedTextPane;

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
    private final ExtendedTextPane valueListTextField = new ExtendedTextPane();

    /**
     * @param userField
     */
    public ListValuePanel(final UserField userField) {
        this.userField = userField;
        setBorder(BorderFactory.createTitledBorder(getResString("userFieldEditor_values")));

        setLayout(new BorderLayout());
        add(new JLabel(getResString("userFieldEditor_values_hint")), BorderLayout.NORTH);

        JScrollPane valuesScrollPane = new JScrollPane(valueListTextField);
        add(valuesScrollPane, BorderLayout.CENTER);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < userField.getListValuesCount(); i++) {
            sb.append(userField.getListValueAt(i));
            sb.append("\n");
        }
        valueListTextField.setText(sb.toString());
    }

    @Override
    public void cancel() {
    }

    @Override
    public void commit() {
        userField.removeAllStandardValues();
        String text = valueListTextField.getText();
        text = text.replace("\r\n", "\n");
        String[] tokens = text.split("\n");
        for (int i = 0; i < tokens.length; i++) {
            userField.addListValue(tokens[i]);
        }
    }

}