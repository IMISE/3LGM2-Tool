package de.imise.tool3lgm.event.action;

import javax.swing.JCheckBoxMenuItem;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.model.GDCommands;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.util.swing.event.OptionAction;

/**
 * Eine Action für Optionen des GraphDocuments.
 *
 * @author AXS (25.03.2018)
 */
public class ModelOptionAction extends GraphDocumentAction implements OptionAction {

    /**  */
    private final GDCommands option;

    /**
     * @param identifier
     */
    public ModelOptionAction(final GDCommands identifier) {
        super(identifier);
        option = identifier;
    }

    @Override
    public JCheckBoxMenuItem createMenuItem() {
        return OptionAction.super.createMenuItem();
    }

    @Override
    public boolean isSelected() {
        GraphDocument selectedDoc = Static.getSelectedDoc();
        return selectedDoc != null && selectedDoc.optionsSupport.isOptionTrue(option);
    }

}
