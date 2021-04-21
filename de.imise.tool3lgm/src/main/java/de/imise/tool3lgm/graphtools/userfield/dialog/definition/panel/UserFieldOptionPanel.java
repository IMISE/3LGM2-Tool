/*
 * Created on 11.02.2008
 */
package de.imise.tool3lgm.graphtools.userfield.dialog.definition.panel;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_ENABLE_FORMULA_CALCULATION;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;

import de.imise.tool3lgm.graphtools.userfield.definition.UserField;
import de.imise.util.swing.component.StoreLastValueCheckbox;

/**
 * @author AXS
 */
public class UserFieldOptionPanel extends AbstractInputPanel {

    /**
     * Das UserField, dessem Eigenschaften mit diesem Panel geändert werden
     * sollen.
     */
    private final UserField userField;

    /**
     * CheckBox zum Einstellen, ob ein Benutzerfeld im Baum angezeigt werden
     * soll
     */
    private final JCheckBox treeVisCheckBox;

    /**
     * CheckBox zum Einstellen, ob ein Benutzerfeld im Baum angezeigt werden
     * soll
     */
    private final JCheckBox showDescriptionInDialog;

    /**
     * CheckBox zum Unstellen der Option, ob Kennzahlformeln tatsächlich
     * berechnet werden sollen
     */
    private StoreLastValueCheckbox enableFormulaCalculationCheckBox;

    /**
     * @param userField
     */
    private UserFieldOptionPanel(final UserField userField) {
        this.userField = userField;
        setBorder(BorderFactory.createTitledBorder(getResString("optionenButtonText")));

        boolean isTreeVisisbility = userField.isTreeVisibility();
        treeVisCheckBox = new JCheckBox(getResString("userFieldEditor_treevis"), isTreeVisisbility);
        add(treeVisCheckBox);

        boolean isShowDescriptionInDialog = userField.isShowDescriptionInDialog();
        showDescriptionInDialog = new JCheckBox(getResString("userFieldEditor_showDescriptionInDialog"), isShowDescriptionInDialog);
        add(showDescriptionInDialog);

        //bei allen UserFields die mit Kennzahlen zu tun haben, die Option zum Einschalten der Berechnung anbieten
        if (userField.isNumberUserField()) {
            enableFormulaCalculationCheckBox = new StoreLastValueCheckbox(OPTION_ENABLE_FORMULA_CALCULATION.createAction(), OPTION_ENABLE_FORMULA_CALCULATION.is());
            add(enableFormulaCalculationCheckBox);
        }
    }

    /**
     * @param userField
     * @return
     */
    public static UserFieldOptionPanel getOptionPanel(final UserField userField) {
        return userField.isGlobal() ? null : new UserFieldOptionPanel(userField);
    }

    @Override
    public void commit() {
        if (treeVisCheckBox != null) {
            boolean isTreeVissible = treeVisCheckBox.isSelected();
            userField.setTreeVisibility(isTreeVissible);
        }
        boolean showDescription = showDescriptionInDialog.isSelected();
        userField.setShowDescriptionInDialog(showDescription);
    }

    @Override
    public void cancel() {
        if (enableFormulaCalculationCheckBox != null) {
            OPTION_ENABLE_FORMULA_CALCULATION.set(enableFormulaCalculationCheckBox.isStoredState());
        }
    }

}
