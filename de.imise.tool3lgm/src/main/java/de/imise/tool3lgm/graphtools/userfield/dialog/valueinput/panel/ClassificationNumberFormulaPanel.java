/*
 * Created on 01.04.2008
 */
package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel;

import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.UserFieldEditorDialog;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.UserFieldTableController;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.model.AbstractUserFieldTableModel;

/**
 * Panel zur Darstellung von berechneten Kennzahlen
 * 
 * @see de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel.GeneralUserFieldEditorPanel
 * @author fstephan
 */
public class ClassificationNumberFormulaPanel extends GeneralUserFieldEditorPanel {

    /**
     * @param dialog
     * @param name
     */
    public ClassificationNumberFormulaPanel(final UserFieldEditorDialog dialog, final String name) {
        super(dialog, UserField.Style.CLASSIFICATION_NUMBER_FORMULA, name);
    }

    @Override
    protected UserFieldTableController getTableController(final AbstractUserFieldTableModel uftm) {
        return UserFieldTableController.getNewClassificationNumberFormulaTableController(uftm);
    }

}
