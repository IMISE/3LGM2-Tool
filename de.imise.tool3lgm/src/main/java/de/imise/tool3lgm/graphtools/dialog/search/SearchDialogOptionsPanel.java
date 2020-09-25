package de.imise.tool3lgm.graphtools.dialog.search;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author AXS (23.09.2020)
 */
public class SearchDialogOptionsPanel extends BasicSearchOptionsPanel {

    /**
     *
     */
    public SearchDialogOptionsPanel() {
        super(new SearchDialogResultTablePanel(), new GridBagLayout());

        GridBagLayout gbl = new GridBagLayout();

        GridBagConstraints constraints = new GridBagConstraints();
        setLayout(gbl);
        setBorder(BorderFactory.createLineBorder(Color.black));

        constraints.insets = new Insets(3, 3, 3, 3);
        constraints.weighty = 0;
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        constraints.ipadx = 2;
        constraints.ipady = 2;
        constraints.anchor = GridBagConstraints.NORTHWEST;

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0;

        // erste Spalte
        add(labelModel, constraints);
        constraints.gridy++;
        add(labelSubmodel, constraints);
        constraints.gridy++;
        add(labelElementType, constraints);
        constraints.gridy++;
        add(labelName, constraints);
        constraints.gridy++;
        add(labelDescription, constraints);

        // nächste Spalte
        constraints.gridx++;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;

        add(modelBox, constraints);
        constraints.gridy++;
        add(subModelBox, constraints);
        constraints.gridy++;
        constraints.weightx = GridBagConstraints.REMAINDER;
        add(elementClassBox, constraints);
        constraints.gridy++;
        add(elementName, constraints);
        constraints.gridy++;
        add(elementDescription, constraints);

        // Checkbox Spalte
        constraints.gridx++;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0;

        constraints.gridy++;
        constraints.gridy++;
        constraints.gridy++;

        add(checkNameCaseSensitive, constraints);
        constraints.gridy++;
        add(checkDescriptionCaseSensitive, constraints);
        constraints.gridy++;

        // Subpanel für benutzerdefinierte Eigenschaften
        constraints.insets = new Insets(3, 3, 3, 3);
        constraints.weighty = 0;
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        constraints.ipadx = 2;
        constraints.ipady = 2;
        constraints.anchor = GridBagConstraints.NORTHWEST;

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0;

        // Zeile 1 Subpanel
        JPanel userFieldPanel = new JPanel();
        userFieldPanel.setLayout(new GridBagLayout());
        userFieldPanel.setBorder(BorderFactory.createTitledBorder(getResString("SEARCH_DIALOG_USERFIELD_benutzdef_eig")));

        JLabel userfieldproperty = new JLabel(getResString("SEARCH_DIALOG_USERFIELD_Attributtyp"));
        userfieldproperty.setToolTipText(getResString("SEARCH_DIALOG_USERFIELD_Attributtyp_TT"));
        userFieldPanel.add(userfieldproperty, constraints);

        constraints.gridx++;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        createUserFieldTypeComboBox();
        userFieldPanel.add(userFieldStyleComboBox, constraints);

        // Zeile 2 Subpanel
        constraints.gridx = 0;
        constraints.gridy++;
        constraints.fill = GridBagConstraints.NONE;
        JLabel containingText = new JLabel(getResString("SEARCH_DIALOG_USERFIELD_Text"));
        containingText.setToolTipText(getResString("SEARCH_DIALOG_USERFIELD_Text_TT"));
        userFieldPanel.add(containingText, constraints);

        constraints.gridx++;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        userFieldPanel.add(elementUserField, constraints);
        constraints.gridx++;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 1;
        userFieldPanel.add(checkUserFieldCaseSensitive, constraints);

        // Zeile 3 Checkboxsuche
        constraints.gridx = 0;
        constraints.gridy++;
        JLabel checkboxFind = new JLabel(getResString("SEARCH_DIALOG_USERFIELD_CB_filter"));
        userFieldPanel.add(checkboxFind, constraints);

        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        userFieldPanel.add(userFieldCheckBoxStateComboBox, constraints);

        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        add(userFieldPanel, constraints);

        userFieldStyleComboBox.setSelectedItem(getResString("SEARCH_DIALOG_USERFIELD_all"));

        // Searchbutton
        constraints.gridy++;
        constraints.gridx--;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.weightx = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        add(searchButton, constraints);

    }

}
