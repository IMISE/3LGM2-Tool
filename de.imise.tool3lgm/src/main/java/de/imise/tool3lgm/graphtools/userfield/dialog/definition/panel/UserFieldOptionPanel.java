/*
 * Created on 11.02.2008
 */
package de.imise.tool3lgm.graphtools.userfield.dialog.definition.panel;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_ENABLE_FORMULA_CALCULATION;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldTarget;
import de.imise.tool3lgm.graphtools.userfield.definition.type.UserField;
import de.imise.util.swing.component.StoreLastValueCheckbox;
import de.imise.util.swing.component.TriStateCheckBox;

/**
 * @author AXS
 */
public class UserFieldOptionPanel extends AbstractInputPanel {

    /**
     * These are the UserFields whose options are changed by this panel.
     */
    private List<UserField> userFields = new ArrayList<>();

    /**
     * CheckBox zum Einstellen, ob ein Benutzerfeld im Baum angezeigt werden
     * soll
     */
    private final TriStateCheckBox treeVisCheckBox;

    /**
     * CheckBox zum Einstellen, ob ein Benutzerfeld im Baum angezeigt werden
     * soll
     */
    private final TriStateCheckBox showDescriptionInDialog;

    /**
     * CheckBox zum Unstellen der Option, ob Kennzahlformeln tatsächlich
     * berechnet werden sollen
     */
    private StoreLastValueCheckbox enableFormulaCalculationCheckBox;

    /**
     * The panel for a real collection of UserFields
     */
    private UserFieldOptionPanel(final LayoutManager layout, final boolean addEnableCalculationOption) {
        super(layout == null ? new FlowLayout() : layout);
        setBorder(BorderFactory.createTitledBorder(getResString("optionenButtonText")));

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Object source = e.getSource();
                if (source == treeVisCheckBox) {
                    boolean isTreeVisibility = treeVisCheckBox.isSelected();
                    for (UserField userField : userFields) {
                        userField.setTreeVisibility(isTreeVisibility);
                    }
                } else if (source == showDescriptionInDialog) {
                    boolean isShowDescription = showDescriptionInDialog.isSelected();
                    for (UserField userField : userFields) {
                        userField.setShowDescriptionInDialog(isShowDescription);
                    }
                }
            }
        };

        treeVisCheckBox = new TriStateCheckBox(getResString("userFieldEditor_treevis"));
        treeVisCheckBox.addActionListener(actionListener);
        add(treeVisCheckBox);
        showDescriptionInDialog = new TriStateCheckBox(getResString("userFieldEditor_showDescriptionInDialog"));
        showDescriptionInDialog.addActionListener(actionListener);
        add(showDescriptionInDialog);

        //bei allen UserFields die mit Kennzahlen zu tun haben, die Option zum Einschalten der Berechnung anbieten
        if (addEnableCalculationOption) {
            enableFormulaCalculationCheckBox = new StoreLastValueCheckbox(OPTION_ENABLE_FORMULA_CALCULATION.createAction(), OPTION_ENABLE_FORMULA_CALCULATION.is());
            add(enableFormulaCalculationCheckBox);
        }
    }

    /**
     * The Panel for a songle UserField dialog
     *
     * @param userField
     */
    private UserFieldOptionPanel(final UserField userField) {
        this(null, userField.isNumberUserField());
        userFields.add(userField);
        setBorder(BorderFactory.createTitledBorder(getResString("optionenButtonText")));
        Class<? extends UserFieldTarget> targetClass = userField.getTargetClass();
        updateCheckBoxes(targetClass);
    }

    /**
     * @param userField
     * @return
     */
    public static UserFieldOptionPanel getOptionPanel(final UserField userField) {
        return userField.isGlobal() ? null : new UserFieldOptionPanel(userField);
    }

    /**
     * @param userField
     * @return
     */
    public static UserFieldOptionPanel getOptionPanel() {
        return new UserFieldOptionPanel(new GridLayout(3, 1), true);
    }

    @Override
    public void commit() {
        boolean isTreeVisibility = treeVisCheckBox.isSelected();
        boolean isShowDescription = showDescriptionInDialog.isSelected();
        for (UserField userField : userFields) {
            userField.setTreeVisibility(isTreeVisibility);
            userField.setShowDescriptionInDialog(isShowDescription);
        }
    }

    @Override
    public void cancel() {
        if (enableFormulaCalculationCheckBox != null) {
            OPTION_ENABLE_FORMULA_CALCULATION.set(enableFormulaCalculationCheckBox.isStoredState());
        }
    }

    /**
     * @param selectedUserFieldTargetClass
     * @param userFields
     */
    public void setUserFields(final Class<? extends UserFieldTarget> selectedUserFieldTargetClass, final List<UserField> userFields) {
        this.userFields = userFields;
        updateCheckBoxes(selectedUserFieldTargetClass);
    }

    /**
     * @param selectedUserFieldTargetClass
     */
    private void updateCheckBoxes(final Class<? extends UserFieldTarget> selectedUserFieldTargetClass) {
        boolean isOneDisabledTreeVisibility = false;
        boolean isOneEnabledTreeVisibility = false;
        boolean isOneDisabledShowDescription = false;
        boolean isOneEnabledShowDescription = false;
        for (UserField userField : userFields) {
            if (userField.isTreeVisibility()) {
                isOneEnabledTreeVisibility = true;
            } else {
                isOneDisabledTreeVisibility = true;
            }
            if (userField.isShowDescriptionInDialog()) {
                isOneEnabledShowDescription = true;
            } else {
                isOneDisabledShowDescription = true;
            }
        }
        boolean userFieldsSelected = !userFields.isEmpty();

        treeVisCheckBox.setEnabled(userFieldsSelected && Node.class.isAssignableFrom(selectedUserFieldTargetClass));
        showDescriptionInDialog.setEnabled(userFieldsSelected && ModelElement.class.isAssignableFrom(selectedUserFieldTargetClass));
        showDescriptionInDialog.setSelectionState(userFieldsSelected && isOneEnabledShowDescription, userFieldsSelected && isOneDisabledShowDescription);
        treeVisCheckBox.setSelectionState(userFieldsSelected && isOneEnabledTreeVisibility, userFieldsSelected && isOneDisabledTreeVisibility);
        if (enableFormulaCalculationCheckBox != null) {
            enableFormulaCalculationCheckBox.setSelected(OPTION_ENABLE_FORMULA_CALCULATION.is());
        }
    }

}
