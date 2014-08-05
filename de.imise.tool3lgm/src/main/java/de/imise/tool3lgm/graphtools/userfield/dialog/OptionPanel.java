/*
 * Created on 11.02.2008
 */
package de.imise.tool3lgm.graphtools.userfield.dialog;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.userproperties.UserProperties;

/**
 * @author AXS
 */
public class OptionPanel extends AbstractInputPanel {

    /**
     * Das UserField, dessem Eigenschaften mit diesem Panel geändert werden sollen.
     */
    private final UserField userField;

    /**
     * CheckBox zum Einstellen, ob ein Benutzerfeld im Baum angezeigt werden soll
     */
    private final JCheckBox treeVisCheckBox = new JCheckBox(Tool3lgmConstants.getResString("userFieldEditor_treevis"));

    /**
     * CheckBox zum Unstellen der Option, ob Kennzahlformeln tatsächlich berechnet werden sollen
     */
    private JCheckBox enableClassificationNumberCalculationCheckBox;

    private JRadioButton kfBut;
    private JRadioButton kBut;

    /**
     * @param userField
     */
    public OptionPanel(final UserField userField) {
        super();
        this.userField = userField;
        setBorder(BorderFactory.createTitledBorder(Tool3lgmConstants.getResString("optionenButtonText")));
        treeVisCheckBox.setSelected(userField.isTreeVisibility());
        add(treeVisCheckBox);

        //bei allen UserFields die mit Kennzahlen zu tun haben, die Option zum Einschalten der Berechnung anbieten
        if (userField.isClassificationUserField()) {
            enableClassificationNumberCalculationCheckBox = new JCheckBox(Tool3lgmConstants.getResString("activate_calculation"));
            enableClassificationNumberCalculationCheckBox.setSelected(UserProperties.isEnableClassificationNumberCalculation());
            add(enableClassificationNumberCalculationCheckBox);
        }
    }

    @Override
    public void commit() {
        userField.setTreeVisibility(treeVisCheckBox.isSelected());

        if (enableClassificationNumberCalculationCheckBox != null) {
            UserProperties.setEnableClassificationNumberCalculation(enableClassificationNumberCalculationCheckBox.isSelected());
        }

        if (kBut != null && kBut.isSelected()) {
            userField.setStyle(UserField.Style.CLASSIFICATION_NUMBER);
        } else if (kfBut != null && kfBut.isSelected()) {
            userField.setStyle(UserField.Style.CLASSIFICATION_NUMBER_FORMULA);
        }
    }

    @Override
    public void cancel() {
    }

}
