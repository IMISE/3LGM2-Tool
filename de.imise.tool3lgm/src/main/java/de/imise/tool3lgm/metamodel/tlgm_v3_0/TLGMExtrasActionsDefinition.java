package de.imise.tool3lgm.metamodel.tlgm_v3_0;

import javax.swing.Action;

import de.imise.tool3lgm.graphtools.metamodel.ExtrasActionsDefinition;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.action.ExtrasActions;

public class TLGMExtrasActionsDefinition extends ExtrasActionsDefinition {

    @Override
    public Action[] getActions() {
        return new ExtrasActions().getActions();
    }

}
