/*
 * Created on 01.04.2008
 */
package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel;

import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.Node;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserField.Style;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitionsAnalyzer;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.UserFieldEditorDialog;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.UserFieldTableController;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.model.AbstractUserFieldTableModel;

/**
 * Panel zur Darstellung von berechneten Kennzahlen
 * 
 * @see de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel.AbstractElementTypeUserFieldEditorPanel
 * @author fstephan
 */
public class ClassificationNumberFormulaPanel extends AbstractElementTypeUserFieldEditorPanel {

    /**
     * @param dialog
     * @param name
     */
    public ClassificationNumberFormulaPanel(final UserFieldEditorDialog dialog, final String name) {
        this(dialog, Node.class, name);
    }

    /**
     * @param dialog
     * @param selectableElementClass
     * @param name
     */
    public ClassificationNumberFormulaPanel(final UserFieldEditorDialog dialog, final Class<? extends ModelElement> selectableElementClass, final String name) {
        super(dialog, selectableElementClass, UserField.Style.CLASSIFICATION_NUMBER_FORMULA, name);
    }

    @Override
    protected UserFieldTableController getTableController(final AbstractUserFieldTableModel uftm) {
        return UserFieldTableController.getNewClassificationNumberFormulaTableController(uftm);
    }

    @Override
    public boolean hasValues() {
        UserFieldDefinitions definitions = dialog.getUserFieldDefinitions();
        UserFieldDefinitionsAnalyzer analyzer = definitions.getAnalyzer();
        boolean hasValues = analyzer.hasStyle(Style.CLASSIFICATION_NUMBER_FORMULA);
        return hasValues;
    }
}
