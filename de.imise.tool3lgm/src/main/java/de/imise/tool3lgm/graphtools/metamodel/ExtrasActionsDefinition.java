package de.imise.tool3lgm.graphtools.metamodel;

import javax.swing.Action;

public abstract class ExtrasActionsDefinition {

    /**
     * Liefert die Actions, die zum Extras-Menü hinzugefügt werden
     *
     * @return
     */
    public abstract Action[] getActions();

    /**
     * Liefert suer soezielle Actions, die man am besten im Extras-Menü im Untermenü Plugin versteckt
     * 
     * @return
     */
    public abstract Action[] getPluginActions();

}
