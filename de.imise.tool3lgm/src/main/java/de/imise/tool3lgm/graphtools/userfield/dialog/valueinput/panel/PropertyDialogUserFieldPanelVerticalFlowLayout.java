package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel;

import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.GROUP;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.TAB;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import com.google.common.base.Strings;

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
        setLayout(new BorderLayout());
        Border mainPanelBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        setBorder(mainPanelBorder);
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanelBorder = BorderFactory.createEmptyBorder(0, 5, 5, 5);
        mainPanel.setBorder(mainPanelBorder);
        JScrollPane scrollPane = getScrollPane(mainPanel);
        add(scrollPane);

        GridBagConstraints constraints = getDefaultConstraints();
        constraints.insets.top = 5;

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
                mainPanel.add(currentPanel, constraints);
            } else {
                JComponent label = getTitleLabel(userField);
                JComponent editor = getEditor(userField);
                currentPanel.add(label, constraints);
                constraints.gridy++;
                constraints.insets.top = 0;
                addDescriptionLabel(userField, currentPanel, constraints);
                currentPanel.add(editor, constraints);
                constraints.gridy += constraints.gridheight;
                constraints.insets.top = 5;
            }
        }
        addFillSpacePanel(mainPanel, constraints);
    }

    /**
     * @param userField
     * @param panel
     * @param constraints
     */
    private boolean addDescriptionLabel(final UserField userField, final JPanel panel, final GridBagConstraints constraints) {
        String description = userField.getDescription();
        if (!Strings.isNullOrEmpty(description)) {
            if (userField.isShowDescriptionInDialog()) {
                panel.add(getDescriptionLabel(description), constraints);
                constraints.gridy++;
                if (userField.hasStyle(TAB) || userField.hasStyle(GROUP)) {
                    panel.add(new JSeparator(), constraints);
                    constraints.gridy++;
                }
                return true;
            }
        }
        return false;
    }

}
