package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel;

import java.util.Set;

import de.imise.tool3lgm.graphtools.elements.Node;
import de.imise.tool3lgm.graphtools.userfield.UserField.Style;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitionsAnalyzer;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.UserFieldEditorDialog;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.UserFieldTableController;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.model.AbstractUserFieldTableModel;

public class NodeTypeUserFieldEditorPanel extends AbstractElementTypeUserFieldEditorPanel {

    public NodeTypeUserFieldEditorPanel(final UserFieldEditorDialog dialog, final Style visibleUserField, final String name) {
        super(dialog, Node.class, visibleUserField, name);
    }

    public NodeTypeUserFieldEditorPanel(final UserFieldEditorDialog dialog, final Set<Style> visibleUserFields, final String name) {
        super(dialog, Node.class, visibleUserFields, name);
    }

    @Override
    protected UserFieldTableController getTableController(final AbstractUserFieldTableModel uftm) {
        return UserFieldTableController.getNewGeneralUserFieldTableController(uftm);
    }

    @Override
    public boolean hasValues() {
        UserFieldDefinitions definitions = dialog.getUserFieldDefinitions();
        UserFieldDefinitionsAnalyzer analyzer = definitions.getAnalyzer();
        for (Style style : visibleUserFields) {
            boolean hasValues = analyzer.hasStyle(style);
            if (hasValues) {
                return true;
            }
        }
        return false;
    }

}
