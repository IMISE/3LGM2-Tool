package de.imise.tool3lgm.graphtools.dialog.tools;

import static javax.swing.JOptionPane.CANCEL_OPTION;
import static javax.swing.JOptionPane.NO_OPTION;
import static javax.swing.JOptionPane.YES_OPTION;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import com.google.common.base.Strings;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.util.swing.dialog.MultipleOptionPane;

/**
 * Class to create YES-NO-Dialogs
 *
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
        optionPane.setOptionType(JOptionPane.YES_NO_OPTION);
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
        labelText = labelText.replaceAll("\n", "<br>");
        if (!labelText.toLowerCase().startsWith("<html>")) {
            labelText = "<html>" + labelText + "</html>";
        }
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

    /**
     * Struct to encapsulate the two answers from a Don't-ask-again-dialog.
     */
    public static class DontAskAgainDialogAnswer {
        /**
         * @param dialogAnswer The dialog answer according to a Yes-No-Cancel
         *            JOptionPane
         * @param dontAskAgain Boolean of the Don't-ask-again-Checkbox
         */
        public DontAskAgainDialogAnswer(int dialogAnswer, boolean dontAskAgain) {
            this.dialogAnswer = dialogAnswer;
            this.dontAskAgain = dontAskAgain;
        }
        /** The dialog answer according to a Yes-No-Cancel JOptionPane */
        private final int dialogAnswer;
        /** Boolean of the Don't-ask-again-Checkbox */
        private final boolean dontAskAgain;
        /**
         * @return <code>true</code> if the dialg answer is
         *         {@link JOptionPane#YES_OPTION}
         */
        public boolean isYes() {
            return dialogAnswer == YES_OPTION;
        }
        /**
         * @return <code>true</code> if the dialg answer is
         *         {@link JOptionPane#NO_OPTION}
         */
        public boolean isNo() {
            return dialogAnswer == NO_OPTION;
        }
        /**
         * @return <code>true</code> if the dialg answer is
         *         {@link JOptionPane#CANCEL_OPTION}
         */
        public boolean isCancel() {
            return dialogAnswer == CANCEL_OPTION;
        }
        /**
         * @return <code>true</code> if dontAskAgain is <code>true</code>
         */
        public boolean isDontAskAgain() {
            return dontAskAgain;
        }
    }

    /**
     * @param titleOrResKey
     * @param messageOrResKey
     * @return
     */
    public static DontAskAgainDialogAnswer showNeverAskAgainDialog(final String titleOrResKey, final String messageOrResKey) {
        String message = Strings.isNullOrEmpty(messageOrResKey) ? null : Tool3lgmConstants.getResStringWithoutError(messageOrResKey);
        JCheckBox showAgainCheckBox = getShowAgainCheckBox(false);
        Object fullMessage[] = {
                message == null ? null : getLabelPanel(message, true), showAgainCheckBox
        };
        int dialogAnswer = showDialog(titleOrResKey, fullMessage);
        boolean dontAskAgain = showAgainCheckBox.isSelected();
        return new DontAskAgainDialogAnswer(dialogAnswer, dontAskAgain);
    }

    /**
     * @param selected
     * @return
     */
    public static JCheckBox getShowAgainCheckBox(boolean selected) {
        return getShowAgainCheckBox("message_do_not_ask_again", selected);
    }

    /**
     * @param labelOrResKey
     * @param selected
     * @return
     */
    public static JCheckBox getShowAgainCheckBox(String labelOrResKey, boolean selected) {
        String title = Tool3lgmConstants.getResStringWithoutError(labelOrResKey);
        JCheckBox showThisDialogAgainCheckBox = new JCheckBox(title, selected);
        return showThisDialogAgainCheckBox;
    }

}
