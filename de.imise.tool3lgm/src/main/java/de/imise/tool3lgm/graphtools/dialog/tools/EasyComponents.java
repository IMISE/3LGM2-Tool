package de.imise.tool3lgm.graphtools.dialog.tools;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JButton;

import de.imise.tool3lgm.Tool3lgmConstants;

public class EasyComponents {

    /**
     * Funktion, die einen Button anlegt, dessen ActionCommand der übergebene resKey ist und den übergebenen {@link ActionListener}
     * anhängt.
     *
     * @param actionListener
     * @param resKey
     * @return
     */
    public static JButton createButton(final ActionListener actionListener, final String resKey) {
        String buttonText = resKey;
        Icon icon = null;
        try {
            buttonText = getResString(resKey);
        } catch (Exception e) {
            icon = Tool3lgmConstants.getIcon(resKey);
        }
        JButton button = icon == null || icon.getIconWidth() < 0 ? new JButton(buttonText) : new JButton(icon);
        button.setActionCommand(resKey);
        if (actionListener != null) {
            button.addActionListener(actionListener);
        }
        return button;
    }

}
