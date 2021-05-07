/*
 * Created on 10.02.2008
 */
package de.imise.tool3lgm.graphtools.userfield.dialog.definition.panel;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.LIST_VALUE_SEPARATOR;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import de.imise.tool3lgm.graphtools.userfield.definition.UserField;
import de.imise.util.htmlxml.HTMLConverter;
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
        String borderTitle = createBorderTitle();
        setBorder(BorderFactory.createTitledBorder(borderTitle));
        JLabel hintLabel = getHintLabel();
        setLayout(new BorderLayout());
        add(hintLabel, BorderLayout.NORTH);
        JScrollPane valuesScrollPane = new JScrollPane(valueListTextField);
        add(valuesScrollPane, BorderLayout.CENTER);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < userField.getListValuesCount(); i++) {
            String value = userField.getListValueAt(i);
            sb.append(userField.getListValueAt(i));
            if (!value.endsWith(LIST_VALUE_SEPARATOR)) {
                sb.append("\n");
            }
        }
        valueListTextField.setText(sb.toString());
    }

    /**
     * @return
     */
    private String createBorderTitle() {
        String borderTitle = getResString("userFieldEditor_values");
        borderTitle = HTMLConverter.getTextAsHTMLLabelText(borderTitle);
        return borderTitle;
    }

    /**
     * @return
     */
    private JLabel getHintLabel() {
        String hintResKey = "userFieldEditor_values_hint_" + userField.getStyle().name();
        String hint = getResString(hintResKey);
        hint = HTMLConverter.getTextAsHTMLLabelTextItalic(hint);
        return new JLabel(hint);
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
            String lineToken = trimEnd(tokens[i]);
            String[] valuesPerLine = lineToken.split(LIST_VALUE_SEPARATOR);
            for (int j = 0; j < valuesPerLine.length - 1; j++) {
                if (!valuesPerLine[j].isBlank()) {
                    userField.addListValue(valuesPerLine[j] + LIST_VALUE_SEPARATOR);
                }
            }
            String lastValue = valuesPerLine[valuesPerLine.length - 1];
            if (!lastValue.isBlank()) {
                userField.addListValue(lastValue);
            }
        }
    }

    private String trimEnd(final String s) {
        int lastIndex = s.length() - 1;
        while (lastIndex >= 0) {
            char charAtLastIndex = s.charAt(lastIndex);
            if (Character.isWhitespace(charAtLastIndex) || charAtLastIndex == LIST_VALUE_SEPARATOR.charAt(0)) {
                lastIndex--;
            } else {
                break;
            }
        }
        return lastIndex == s.length() - 1 ? s : s.substring(0, lastIndex + 1);
    }

}