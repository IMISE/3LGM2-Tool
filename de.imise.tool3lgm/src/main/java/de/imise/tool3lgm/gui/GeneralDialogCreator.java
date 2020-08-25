package de.imise.tool3lgm.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.util.swing.dialog.MultipleOptionPane;

/**
 * @author AXS (25.08.2020)
 */
public class GeneralDialogCreator {

    /**
     * @param message
     * @return
     */
    private static MultipleOptionPane createOptionPane(final Object... message) {
        MultipleOptionPane optionPane = new MultipleOptionPane();
        optionPane.setMessage(message);
        optionPane.setMessageType(JOptionPane.QUESTION_MESSAGE);
        optionPane.setOptionType(JOptionPane.OK_CANCEL_OPTION);
        return optionPane;
    }

    /**
     * @param titleOrResKey
     * @param message
     * @return
     */
    public static int showDialog(final String titleOrResKey, final Object... message) {
        return showDialog(Static.getMainFrame(), titleOrResKey, message);
    }

    /**
     * @param parent
     * @param titleOrResKey
     * @param message
     * @return
     */
    public static int showDialog(final Component parent, final String titleOrResKey, final Object... message) {
        MultipleOptionPane optionPane = createOptionPane(message);
        String title = Tool3lgmConstants.getResStringWithoutError(titleOrResKey);
        JDialog dialog = optionPane.createDialog(parent, title);
        dialog.setVisible(true);
        int answer = optionPane.getAnswer();
        return answer;
    }

    /**
     * @param resKeyOrText
     * @param fillContent
     * @return
     */
    public static JComponent getLabelPanel(final String resKeyOrText, final boolean fillContent) {
        return getLabelPanel(resKeyOrText, false, fillContent, -1, -1);
    }

    /**
     * @param resKeyOrText
     * @param scroll
     * @param fillContent
     * @return
     */
    public static JComponent getLabelPanel(final String resKeyOrText, final boolean scroll, final boolean fillContent) {
        return getLabelPanel(resKeyOrText, scroll, fillContent, -1, -1);
    }

    /**
     * @param resKeyOrText
     * @param scroll
     * @param fillContent
     * @param preferredWidth
     * @param preferredHeight
     * @return
     */
    public static JComponent getLabelPanel(final String resKeyOrText, final boolean scroll, final boolean fillContent, final int preferredWidth, final int preferredHeight) {
        String labelText = Tool3lgmConstants.getResStringWithoutError(resKeyOrText);
        JLabel content = new JLabel(labelText);
        return getTitledPanel(null, content, scroll, fillContent, preferredWidth, preferredHeight);
    }

    /**
     * @param content
     * @param scroll
     * @param fillContent
     * @param preferredWidth
     * @param preferredHeight
     * @return
     */
    public static JComponent getPanel(final JComponent content, final boolean scroll, final boolean fillContent, final int preferredWidth, final int preferredHeight) {
        return getTitledPanel(null, content, scroll, fillContent, preferredWidth, preferredHeight);
    }

    /**
     * @param resKeyOrTitle
     * @param content
     * @param scroll
     * @param fillContent
     * @param preferredWidth
     * @param preferredHeight
     * @return a panel with a border with the given title
     */
    public static JComponent getTitledPanel(final String resKeyOrTitle, final JComponent content, final boolean scroll, final boolean fillContent, final int preferredWidth, final int preferredHeight) {
        JComponent panel;
        if (scroll) {
            panel = new JScrollPane(content);
        } else {
            panel = new JPanel();
            if (fillContent) {
                panel.setLayout(new BorderLayout());
            }
            panel.add(content);
        }
        if (resKeyOrTitle != null) {
            String title = Tool3lgmConstants.getResStringWithoutError(resKeyOrTitle);
            TitledBorder titledBorder = BorderFactory.createTitledBorder(title);
            panel.setBorder(titledBorder);
        }

        Dimension preferredSize = panel.getPreferredSize();
        if (preferredWidth > 0) {
            preferredSize.width = preferredWidth;
        }
        if (preferredHeight > 0) {
            preferredSize.height = preferredHeight;
        }
        panel.setPreferredSize(preferredSize);
        return panel;
    }

}
