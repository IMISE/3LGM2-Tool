package de.imise.tool3lgm.metamodel.original;

import javax.swing.Action;

import de.imise.tool3lgm.graphtools.metamodel.ExtrasActionsDefinition;
import de.imise.tool3lgm.metamodel.original.action.B1ExportPlugin;
import de.imise.tool3lgm.metamodel.original.action.ExportPdvb4AwbPlugin;
import de.imise.tool3lgm.metamodel.original.action.ExtrasActions;

public class TLGMOriginalExtrasActionsDefinition extends ExtrasActionsDefinition {

    @Override
    public Action[] getActions() {
        return new ExtrasActions().getActions();
    }

    @Override
    public Action[] getPluginActions() {
        return new Action[] {
                new B1ExportPlugin().getAction(), new ExportPdvb4AwbPlugin().getAction()
        };
    }

}
