package de.imise.tool3lgm.gui;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_ENABLE_EXPERT_MODE;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SHOW_CHOOSE_METAMODEL_DIALOG;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.Tool3lgmMetaModelContext;
import de.imise.tool3lgm.Tool3lgmModelType;
import de.imise.tool3lgm.Tool3lgmModelType.ModelCategory;
import de.imise.tool3lgm.graphtools.dialog.tools.GeneralDialogCreator;
import de.imise.tool3lgm.userproperties.UserProperties.StringProperty;
import de.imise.util.swing.component.AlphabeticalComboBox;

/**
 * Privides a dialog to choose the standard MetaModelContext.
 *
 * @author AXS (08.11.2019)
 */
public class Tool3lgmMetaModelContextChooser {

    /** MetaModelChooser ComboBox */
    private final AlphabeticalComboBox chooseMetaModelComboBox = getChooseMetaModelComboBox();

    /** Description text field with titled border in a scroll pane */
    private final JTextComponent descriptionLabel = getDescriptionLabel();

    /** Checkbox to enabel creation of template model (expert mode only) */
    private final JCheckBox expertModeCreateAsTemplateCheckBox = getExpertModeCreateAsTemplateCheckBox();

    /** CheckBox show this dialog before creating a new model */
    private final JCheckBox showThisDialogAgainCheckBox = getShowThisDialogAgainCheckBox();

    /**
     * @return
     */
    public final Tool3lgmModelType chooseModelType() {
        //MetaModelChooser ComboBox
        JComponent chooseMetaModelComboBoxPanel = GeneralDialogCreator.getTitledPanel("MODEL_TYPE", chooseMetaModelComboBox, false, false, -1, -1);

        //Description text field with titled border in a scroll pane
        JComponent descriptionPanel = GeneralDialogCreator.getTitledPanel("description", descriptionLabel, true, false, 200, 150);
        //add listener to the combobox to update the metamodel description
        addDescriptionUpdateListener();

        //create the optionPane
        int answer = showDialog("choose_meta_model_dialog_title", chooseMetaModelComboBoxPanel, descriptionPanel, expertModeCreateAsTemplateCheckBox, showThisDialogAgainCheckBox);
        Tool3lgmModelType modelType = answer == JOptionPane.OK_OPTION ? getModelType() : null;
        return modelType;
    }

    /**
     * @return The selected model type. This type will be extracted from the
     *         selected MetaModel in the ComboBox.
     */
    private Tool3lgmModelType getModelType() {
        OPTION_SHOW_CHOOSE_METAMODEL_DIALOG.set(showThisDialogAgainCheckBox.isSelected());
        MetaModelContext choosedMetaModelContext = (MetaModelContext) chooseMetaModelComboBox.getSelectedObject();
        choosedMetaModelContext = (MetaModelContext) chooseMetaModelComboBox.getSelectedObject();
        StringProperty.META_MODEL.set(choosedMetaModelContext.getMetaModelID());
        ModelCategory modelCategory = expertModeCreateAsTemplateCheckBox != null && expertModeCreateAsTemplateCheckBox.isSelected() ? ModelCategory.TEMPLATE : ModelCategory.REGULAR;
        Tool3lgmModelType modelType = new Tool3lgmModelType(choosedMetaModelContext, modelCategory);
        return modelType;
    }

    /**
     * @param titleOrResKey
     * @param chooseMetaModelComboBoxPanel
     * @param descriptionPanel
     * @param expertModeCreateAsTemplateCheckBox
     * @param showThisDialogAgainCheckBox
     * @return
     */
    private static int showDialog(final String titleOrResKey, final JComponent chooseMetaModelComboBoxPanel, final JComponent descriptionPanel, final JCheckBox expertModeCreateAsTemplateCheckBox, final JCheckBox showThisDialogAgainCheckBox) {
        //null values are ignored in the msg object
        Object message[] = {
                chooseMetaModelComboBoxPanel, descriptionPanel, expertModeCreateAsTemplateCheckBox, expertModeCreateAsTemplateCheckBox == null ? null : new JSeparator(), showThisDialogAgainCheckBox
        };
        return GeneralDialogCreator.showDialog(titleOrResKey, message);
    }

    /**
     *
     */
    private void addDescriptionUpdateListener() {
        updateDescription(); // initial update!
        chooseMetaModelComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(final ItemEvent e) {
                updateDescription();
            }
        });
    }

    /**
     * @return
     */
    private void updateDescription() {
        MetaModelContext metaModelContext = (MetaModelContext) chooseMetaModelComboBox.getSelectedObject();
        String metaModelDescription = metaModelContext.getMetaModelDescription();
        metaModelDescription = metaModelDescription.replaceAll("<br>", "\r\n");
        descriptionLabel.setText(metaModelDescription);
    }

    /**
     * @return A ComboBox to choose a MetaModel.
     */
    private static AlphabeticalComboBox getChooseMetaModelComboBox() {
        AlphabeticalComboBox<MetaModelContext> comboBox = new AlphabeticalComboBox<>();
        MetaModelContext selectedOption = null;
        Tool3lgmModelType userpropertiesStoredModelType = Tool3lgmMetaModelContext.getUserpropertiesStoredModelType();
        if (userpropertiesStoredModelType == null) {
            userpropertiesStoredModelType = Tool3lgmMetaModelContext.getDefaultModelType();
        }
        MetaModelContext lastMetaModelContext = userpropertiesStoredModelType.getMetaModelContext();
        for (MetaModelContext metaModelContext : Tool3lgmMetaModelContext.getRegularMetaModelContexts()) {
            comboBox.addObject(metaModelContext, "   " + metaModelContext.getMetaModelDisplayName() + "   ");
            if (selectedOption == null || lastMetaModelContext == metaModelContext) {
                selectedOption = metaModelContext;
            }
        }
        comboBox.setSelectedObject(selectedOption);
        return comboBox;
    }

    /**
     * @return If expert mode is on it returns a CheckBox for enabling or
     *         disabling the creation of a template model. If expert mode is off
     *         it return <code>null</code>.
     */
    private static JCheckBox getExpertModeCreateAsTemplateCheckBox() {
        JCheckBox expertModeCreateAsTemplateCheckBox = null;
        if (OPTION_ENABLE_EXPERT_MODE.is()) {
            String checkBoxTitle = getResString("choose_meta_model_dialog_create_template_model");
            expertModeCreateAsTemplateCheckBox = new JCheckBox(checkBoxTitle, false);
        }
        return expertModeCreateAsTemplateCheckBox;
    }

    /**
     * @return
     */
    private static JCheckBox getShowThisDialogAgainCheckBox() {
        String title = getResString("show_this_dialog_when_creating_new_model");
        boolean enabled = OPTION_SHOW_CHOOSE_METAMODEL_DIALOG.is();
        JCheckBox showThisDialogAgainCheckBox = new JCheckBox(title, enabled);
        return showThisDialogAgainCheckBox;
    }

    /**
     * @return
     */
    public static JTextComponent getDescriptionLabel() {
        JTextArea descriptionLabel = new JTextArea();
        descriptionLabel.setLineWrap(true);
        descriptionLabel.setWrapStyleWord(true);
        descriptionLabel.setEditable(false);
        Color background = new JLabel().getBackground();
        descriptionLabel.setBackground(background);
        Border emptyBorder = BorderFactory.createEmptyBorder(5, 10, 5, 10);
        descriptionLabel.setBorder(emptyBorder);
        return descriptionLabel;
    }

}
