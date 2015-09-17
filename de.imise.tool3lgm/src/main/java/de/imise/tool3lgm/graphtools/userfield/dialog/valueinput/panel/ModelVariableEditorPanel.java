/*
 * Created on 14.02.2008
 */
package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel;

import java.awt.GridBagConstraints;
import java.util.Vector;

import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.UserFieldEditorDialog;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.UserFieldTable;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.UserFieldTableController;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.layout.UserFieldTableLayout;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.model.AbstractUserFieldTableModel;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.model.UserFieldGlobalNumberTableModel;
import de.imise.util.NamedObjectContainer;

/**
 * Panel zur Eingabe von Modelvariablen
 * 
 * @author fstephan
 */
public class ModelVariableEditorPanel extends AbstractUserFieldEditorPanel {

    /**
     * Kosntruktor
     * 
     * @param dialog
     * @param name
     */
    public ModelVariableEditorPanel(final UserFieldEditorDialog dialog, final String name) {
        super(dialog, name);
        drawTable();
    }

    @SuppressWarnings({
            "unchecked", "rawtypes"
    })
    @Override
    public void takeOver() {

        GraphDocument doc = getDialog().getGraphDocument();
        GDCollection gdcoll = doc.getCollection();
        UserFieldDefinitions definitions = gdcoll.getUserFieldDefinitions();

        if (!(table.getModel() instanceof AbstractUserFieldTableModel)) {
            return; // noch keine Werte vorhanden
        }
        AbstractUserFieldTableModel uftm = (AbstractUserFieldTableModel) table.getModel();

        Vector<NamedObjectContainer<UserField>> rowIdentifiers = (Vector) uftm.getRowIdentifiers();
        for (int i = 0; i < rowIdentifiers.size(); i++) {
            UserField field = rowIdentifiers.get(i).getObject();
            String value = uftm.getValueAt(i, 0).toString();
            gdcoll.setUserFieldInputValue(field, value);
        }
        definitions.initReset();

    }

    @Override
    protected UserFieldTable initTable() {

        UserFieldTableLayout uftl = new UserFieldTableLayout();
        UserFieldTable table = new UserFieldTable(uftl);

        return table;
    }

    @Override
    protected Object constraintsForTable() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.weighty = 1;
        return constraints;
    }

    @Override
    protected void drawTable() {
        UserFieldGlobalNumberTableModel uftm = new UserFieldGlobalNumberTableModel(getDialog().getGraphDocument());
        UserFieldTableController tec = UserFieldTableController.getNewModelVariableTableController(uftm);
        super.modifyTable(uftm, tec);
    }
}
