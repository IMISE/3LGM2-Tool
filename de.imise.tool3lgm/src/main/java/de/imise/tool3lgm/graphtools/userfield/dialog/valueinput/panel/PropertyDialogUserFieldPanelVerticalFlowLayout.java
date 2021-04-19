package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel;

import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.GROUP;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.SEPARATOR;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.TAB;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import de.imise.tool3lgm.graphtools.dialog.element.AbstractElementPropertyDialog;
import de.imise.tool3lgm.graphtools.userfield.definition.UserField;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldList;

/**
 * @author AXS (19.04.2021)
 */
public class PropertyDialogUserFieldPanelVerticalFlowLayout extends PropertyDialogUserFieldPanel {

    /**
     * @param propertyDialog
     * @param tabDefinition
     */
    public PropertyDialogUserFieldPanelVerticalFlowLayout(final AbstractElementPropertyDialog propertyDialog, final UserFieldList tabDefinition) {
        super(propertyDialog, tabDefinition);
    }

    /**
     * Visualisiert die UserField mit ihren entsprechenden Style-Vorgaben im
     * <code>JPanel</code>.
     */
    @Override
    protected void create(final UserFieldList tabDefinition) {
        JPanel mainPanel = createMainPanel();
        GridBagConstraints constraints = getDefaultConstraints();

        JPanel currentPanel = mainPanel;

        //Attributdefinitionen des GraphDocumentes holen
        for (UserField userField : tabDefinition) {
            if (userField.hasStyle(TAB)) {
                String tabName = userField.getName();
                setName(tabName);
                addDescriptionLabel(userField, mainPanel, constraints);
            } else if (userField.hasStyle(GROUP)) {
                currentPanel = new JPanel(new GridBagLayout());
                String name = userField.getName();
                TitledBorder titledBorder = BorderFactory.createTitledBorder(name);
                Font titleFont = titledBorder.getTitleFont();
                titleFont = deriveFont(titleFont, 1, true, true);
                titledBorder.setTitleFont(titleFont);
                currentPanel.setBorder(titledBorder);
                addDescriptionLabel(userField, currentPanel, constraints);
                constraints.insets.top = DEFAULT_INSETS;
                mainPanel.add(currentPanel, constraints);
                constraints.insets.top = 0;
            } else if (userField.hasStyle(SEPARATOR)) {
                addSeparator(userField, currentPanel, constraints);
            } else {
                JComponent label = getTitleLabel(userField);
                JComponent editor = getEditor(userField);
                constraints.insets.top = DEFAULT_INSETS;
                currentPanel.add(label, constraints);
                constraints.insets.top = 0;
                constraints.gridy++;
                addDescriptionLabel(userField, currentPanel, constraints);
                currentPanel.add(editor, constraints);
                constraints.gridy += constraints.gridheight;
            }
        }
        addFillSpacePanel(mainPanel, constraints);
    }

}
