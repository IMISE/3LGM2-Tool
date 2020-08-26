package de.imise.tool3lgm.gui;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SHOW_TEMPLATE_BROWSER;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.TRANSIENT_OPTION_ASK_SHOW_TEMPLATE_BROWSER;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.dialog.tools.GeneralDialogCreator;
import de.imise.tool3lgm.graphtools.model.template.TemplateLibrariesManager;

/**
 * @author AXS (25.08.2020)
 */
public class SuggestShowTemplateBrowserHandler {

    /**
     *
     */
    public static final void suggestShowTemplateBrowser() {
        if (OPTION_SHOW_TEMPLATE_BROWSER.is() || !TRANSIENT_OPTION_ASK_SHOW_TEMPLATE_BROWSER.is()) {
            return;
        }
        TemplateLibrariesManager templateLibrariesManager = Static.getTemplateLibrariesManager();
        if (templateLibrariesManager.hasAvailableTemplatesForCurrentModelType()) {
            JComponent message = GeneralDialogCreator.getLabelPanel("SUGGEST_SHOW_TEMPLATE_BROWSER_DIALOG_MESSAGE", true);
            String title = getResString("message_do_not_ask_again");
            JCheckBox dontAskAgain = new JCheckBox(title, false);
            int answer = GeneralDialogCreator.showDialog("SUGGEST_SHOW_TEMPLATE_BROWSER_DIALOG_TITLE", message, dontAskAgain);
            TRANSIENT_OPTION_ASK_SHOW_TEMPLATE_BROWSER.set(!dontAskAgain.isSelected());
            if (answer == JOptionPane.OK_OPTION) {
                OPTION_SHOW_TEMPLATE_BROWSER.set(true);
            }
        }

    }

}
